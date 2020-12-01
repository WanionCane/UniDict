package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Lists;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import wanion.lib.recipe.IRecipeResearcher;
import wanion.unidict.recipe.*;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class CraftingIntegration extends AbstractIntegrationThread
{
	private final Set<Map.Entry<ResourceLocation, IRecipe>> recipes = RegistryManager.ACTIVE.<IRecipe>getRegistry(GameData.RECIPES).getEntries();
	private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapedResearcherMap = new IdentityHashMap<>();
	private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapelessResearcherMap = new IdentityHashMap<>();
	private final Map<UniResourceContainer, Map<Integer, List<IRecipe>>> smartRecipeMap = new IdentityHashMap<>();
	private final Method getShapedRecipeKeyMethod;
	private final Method getShapelessRecipeKeyMethod;
	private final Method getNewShapedRecipeMethod;
	private final Method getNewShapedFromShapelessRecipeMethod;
	private final Method getNewShapelessRecipeMethod;
	private final Method getNewShapelessFromShapedRecipeMethod;
	private int totalRecipesReCreated = 0;

	public CraftingIntegration()
	{
		super("Crafting");
		final List<IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> researcherList = new ArrayList<>();
		researcherList.add(new VanillaRecipeResearcher());
		researcherList.add(new ForgeRecipeResearcher());
		if (Loader.isModLoaded("ic2") && !Loader.isModLoaded("ic2-classic-spmod"))
			researcherList.add(new IC2RecipeResearcher());
		if (Loader.isModLoaded("ic2-classic-spmod"))
			researcherList.add(new IC2CRecipeResearcher());
		if (Loader.isModLoaded("mekanism"))
			researcherList.add(new MekanismRecipeResearcher());
		researcherList.forEach(researcher -> {
			researcher.getShapedRecipeClasses().forEach(shapedRecipeClass -> shapedResearcherMap.put(shapedRecipeClass, researcher));
			researcher.getShapelessRecipeClasses().forEach(shapelessRecipeClass -> shapelessResearcherMap.put(shapelessRecipeClass, researcher));
		});
		try {
			getShapedRecipeKeyMethod = IRecipeResearcher.class.getMethod("getShapedRecipeKey", IRecipe.class);
			getShapelessRecipeKeyMethod = IRecipeResearcher.class.getMethod("getShapelessRecipeKey", IRecipe.class);
			getNewShapedRecipeMethod = IRecipeResearcher.class.getMethod("getNewShapedRecipe", IRecipe.class);
			getNewShapedFromShapelessRecipeMethod = IRecipeResearcher.class.getMethod("getNewShapedFromShapelessRecipe", IRecipe.class);
			getNewShapelessRecipeMethod = IRecipeResearcher.class.getMethod("getNewShapelessRecipe", IRecipe.class);
			getNewShapelessFromShapedRecipeMethod = IRecipeResearcher.class.getMethod("getNewShapelessFromShapedRecipe", IRecipe.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Couldn't find the Methods!");
		}
	}

	@Override
	public String call()
	{
		try {
			doTheResearch();
			reCreateTheRecipes();
			if (Loader.isModLoaded("ic2-classic-spmod"))
				IC2CIntegration.fixAdvancedRecipes(resourceHandler);
		} catch (Exception e) {	e.printStackTrace(); logger.error(threadName + e); }
		return threadName + "Why so many recipes? I had to deal with at least " + totalRecipesReCreated + " recipes.";
	}

	private void doTheResearch()
	{
		UniResourceContainer bufferContainer;
		final List<ResourceLocation> recipesToRemove = new ArrayList<>();
		for (final Map.Entry<ResourceLocation, IRecipe> entry : recipes) {
			final IRecipe recipe = entry.getValue();
			boolean isShapeless = false;
			if (config.recipesToIgnore.contains(entry.getKey()) || config.ignoreModIdRecipes.contains(entry.getKey().getResourceDomain()) ||  (recipe == null || recipe.getRecipeOutput() == ItemStack.EMPTY || (bufferContainer = resourceHandler.getContainer(entry.getValue().getRecipeOutput())) == null || !(shapedResearcherMap.containsKey(recipe.getClass()) || (isShapeless = shapelessResearcherMap.containsKey(recipe.getClass())))))
				continue;
			try {
				final int recipeKey;
				recipeKey = !isShapeless ? (int) getShapedRecipeKeyMethod.invoke(shapedResearcherMap.get(recipe.getClass()), recipe) : (int) getShapelessRecipeKeyMethod.invoke(shapelessResearcherMap.get(recipe.getClass()), recipe);
				if (recipeKey == 0)
					continue;
				final Map<Integer, List<IRecipe>> evenSmarterRecipeMap;
				if (!smartRecipeMap.containsKey(bufferContainer))
					smartRecipeMap.put(bufferContainer, evenSmarterRecipeMap = new HashMap<>());
				else evenSmarterRecipeMap = smartRecipeMap.get(bufferContainer);
				if (!evenSmarterRecipeMap.containsKey(recipeKey))
					evenSmarterRecipeMap.put(recipeKey, Lists.newArrayList(recipe));
				else evenSmarterRecipeMap.get(recipeKey).add(recipe);
				recipesToRemove.add(recipe.getRegistryName());
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		final ForgeRegistry<IRecipe> recipeRegistry = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
		recipesToRemove.forEach(recipeRegistry::remove);
	}

	private void reCreateTheRecipes()
	{
		final Map<UniResourceContainer, Comparator<IRecipe>> comparatorCache = new HashMap<>();
		smartRecipeMap.forEach((container, evenSmartRecipeMap) -> evenSmartRecipeMap.forEach((key, recipeList) -> {
					if (recipeList.size() > 1) {
						final boolean hasComparator = comparatorCache.containsKey(container);
						final Comparator<IRecipe> recipeComparator = hasComparator ? comparatorCache.get(container) : new RecipeComparator(container.getComparator());
						if (!hasComparator)
							comparatorCache.put(container, recipeComparator);
						recipeList.sort(recipeComparator);
					}
					final IRecipe recipe = recipeList.get(0);
					final boolean isShapeless = shapelessResearcherMap.containsKey(recipe.getClass());
					final IRecipeResearcher<? extends IRecipe, ? extends IRecipe> recipeResearcher = !isShapeless ? shapedResearcherMap.get(recipe.getClass()) : shapelessResearcherMap.get(recipe.getClass());
					IRecipe iRecipe;
					try {
						if (recipe.getIngredients().size() == 9)
							iRecipe = isShapeless ? (IRecipe) getNewShapedFromShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapedRecipeMethod.invoke(recipeResearcher, recipe);
						else if (recipe.getIngredients().size() == 1)
							iRecipe = isShapeless ? (IRecipe) getNewShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapelessFromShapedRecipeMethod.invoke(recipeResearcher, recipe);
						else
							iRecipe = isShapeless ? (IRecipe) getNewShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapedRecipeMethod.invoke(recipeResearcher, recipe);
						if (iRecipe != null) {
							ForgeRegistries.RECIPES.register(iRecipe.setRegistryName(new ResourceLocation(iRecipe.getGroup())));
							totalRecipesReCreated++;
						}
					} catch (IllegalAccessException | InvocationTargetException e) {
						logger.error("Crafting Integration: Couldn't create the recipe for " + recipe.getRecipeOutput().getDisplayName() + ".\nfor now, isn't possible to restore the original recipe.");
						e.printStackTrace();
					}
				})
		);
	}

	private static class RecipeComparator implements Comparator<IRecipe>
	{
		private final Comparator<ItemStack> itemStackComparator;

		private RecipeComparator(@Nonnull final Comparator<ItemStack> itemStackComparator)
		{
			this.itemStackComparator = itemStackComparator;
		}

		@Override
		public int compare(IRecipe o1, IRecipe o2)
		{
			return itemStackComparator.compare(o1.getRecipeOutput(), o2.getRecipeOutput());
		}
	}
}