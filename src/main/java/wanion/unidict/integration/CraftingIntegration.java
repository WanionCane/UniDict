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
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import wanion.lib.recipe.IRecipeResearcher;
import wanion.unidict.UniDict;
import wanion.unidict.recipe.ForgeRecipeResearcher;
import wanion.unidict.recipe.IC2RecipeResearcher;
import wanion.unidict.recipe.VanillaRecipeResearcher;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

final class CraftingIntegration extends AbstractIntegrationThread
{
	private final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
	private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapedResearcherMap = new IdentityHashMap<>();
	private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapelessResearcherMap = new IdentityHashMap<>();
	private final Map<UniResourceContainer, TIntObjectMap<List<IRecipe>>> smartRecipeMap = new IdentityHashMap<>();
	private final Method getShapedRecipeKeyMethod;
	private final Method getShapelessRecipeKeyMethod;
	private final Method getNewShapedRecipeMethod;
	private final Method getNewShapedFromShapelessRecipeMethod;
	private final Method getNewShapelessRecipeMethod;
	private final Method getNewShapelessFromShapedRecipeMethod;
	private int totalRecipesReCreated = 0;

	CraftingIntegration()
	{
		super("Crafting");
		final List<IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> researcherList = new ArrayList<>();
		researcherList.add(new VanillaRecipeResearcher());
		researcherList.add(new ForgeRecipeResearcher());
		if (Loader.isModLoaded("ic2"))
			researcherList.add(new IC2RecipeResearcher());
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
		} catch (NoSuchMethodException e){
			throw new RuntimeException("Couldn't find the Methods!");
		}
	}

	@Override
	public String call()
	{
		try {
			doTheResearch();
			reCreateTheRecipes();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "Why so many recipes? I had to deal with " + totalRecipesReCreated + " recipes.";
	}

	private void doTheResearch()
	{
		IRecipe bufferRecipe;
		UniResourceContainer bufferContainer;
		for (final Iterator<IRecipe> recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
			boolean isShapeless = false;
			if ((bufferRecipe = recipeIterator.next()) == null || (bufferContainer = resourceHandler.getContainer(bufferRecipe.getRecipeOutput())) == null || !(shapedResearcherMap.containsKey(bufferRecipe.getClass()) || (isShapeless = shapelessResearcherMap.containsKey(bufferRecipe.getClass()))))
				continue;
			try {
				final int recipeKey;
				recipeKey = !isShapeless ? (int) getShapedRecipeKeyMethod.invoke(shapedResearcherMap.get(bufferRecipe.getClass()), bufferRecipe) : (int) getShapelessRecipeKeyMethod.invoke(shapelessResearcherMap.get(bufferRecipe.getClass()), bufferRecipe);
				if (recipeKey == 0)
					continue;
				final TIntObjectMap<List<IRecipe>> evenSmarterRecipeMap;
				if (!smartRecipeMap.containsKey(bufferContainer))
					smartRecipeMap.put(bufferContainer, evenSmarterRecipeMap = new TIntObjectHashMap<>());
				else evenSmarterRecipeMap = smartRecipeMap.get(bufferContainer);
				if (!evenSmarterRecipeMap.containsKey(recipeKey))
					evenSmarterRecipeMap.put(recipeKey, Lists.newArrayList(bufferRecipe));
				else evenSmarterRecipeMap.get(recipeKey).add(bufferRecipe);
				recipeIterator.remove();
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void reCreateTheRecipes()
	{
		final Map<UniResourceContainer, Comparator<IRecipe>> comparatorCache = new HashMap<>();
		smartRecipeMap.forEach((container, evenSmartRecipeMap) -> evenSmartRecipeMap.forEachValue(recipeList -> {
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
					try {
						if (recipe.getRecipeSize() == 9)
							recipes.add(isShapeless ? (IRecipe) getNewShapedFromShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapedRecipeMethod.invoke(recipeResearcher, recipe));
						else if (recipe.getRecipeSize() == 1)
							recipes.add(isShapeless ? (IRecipe) getNewShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapelessFromShapedRecipeMethod.invoke(recipeResearcher, recipe));
						else
							recipes.add(isShapeless ? (IRecipe) getNewShapelessRecipeMethod.invoke(recipeResearcher, recipe) : (IRecipe) getNewShapedRecipeMethod.invoke(recipeResearcher, recipe));
						totalRecipesReCreated++;
					} catch (IllegalAccessException | InvocationTargetException e) {
						final ItemStack outputStack = recipe.getRecipeOutput();
						if (outputStack != null) {
							UniDict.getLogger().warn("Crafting Integration: Couldn't create the recipe for " + outputStack.getDisplayName() + ".\nre-adding the original recipe.");
							recipes.add(recipe);
						}
					}
					return true;
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