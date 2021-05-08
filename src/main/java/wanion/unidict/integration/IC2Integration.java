package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import ic2.core.block.steam.TileEntityCokeKiln;
import ic2.core.recipe.MachineRecipeHelper;
import ic2.core.recipe.ScrapboxRecipeManager;
import ic2.core.recipe.dynamic.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

final class IC2Integration extends AbstractIntegrationThread
{
	IC2Integration()
	{
		super("Industrial Craft 2");
	}

	@Override
	public String call()
	{
		try {
			fixMachineOutput(Recipes.centrifuge);
			fixMachineOutput(Recipes.compressor);
			fixMachineOutput(Recipes.blastfurnace);
			fixMachineOutput(Recipes.macerator);
			fixMachineOutput(Recipes.metalformerCutting);
			fixMachineOutput(Recipes.metalformerExtruding);
			fixMachineOutput(Recipes.metalformerRolling);
			fixMachineOutput(Recipes.blockcutter);
			fixMachineOutput(Recipes.extractor);
			fixMachineOutput(Recipes.oreWashing);
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}

		try {
			fixScrapBoxDrops();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}

		try {
			fixCokeKilnRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}


		return threadName + "The world appears to be entirely industrialized.";
	}

	private void fixMachineOutput(@Nonnull final IBasicMachineRecipeManager iBasicMachineRecipeManager)
	{
		final Map<IRecipeInput, MachineRecipe<IRecipeInput, Collection<ItemStack>>> recipes = Util.getField(MachineRecipeHelper.class, "recipes", iBasicMachineRecipeManager, Map.class);
		final Map<Item, List<MachineRecipe<IRecipeInput, Collection<ItemStack>>>> recipeCache = Util.getField(MachineRecipeHelper.class, "recipeCache", iBasicMachineRecipeManager, Map.class);
		final List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> uncacheableRecipes = Util.getField(MachineRecipeHelper.class, "uncacheableRecipes", iBasicMachineRecipeManager, List.class);
		if (recipes != null)
			for (final Map.Entry<IRecipeInput, MachineRecipe<IRecipeInput, Collection<ItemStack>>> recipe : recipes.entrySet())
				recipe.setValue(fixMachineRecipe(recipe.getValue()));
		if (recipeCache != null)
			recipeCache.forEach((item, recipeList) -> recipeList.replaceAll(this::fixMachineRecipe));
		if (uncacheableRecipes != null)
			uncacheableRecipes.replaceAll(this::fixMachineRecipe);
	}

	private MachineRecipe<IRecipeInput, Collection<ItemStack>> fixMachineRecipe(@Nonnull final MachineRecipe<IRecipeInput, Collection<ItemStack>> machineRecipe)
	{
		return new MachineRecipe<>(machineRecipe.getInput(), resourceHandler.getMainItemStacks(machineRecipe.getOutput()), machineRecipe.getMetaData());
	}

	private void fixScrapBoxDrops()
	{
		final Class<?>[] classes = ScrapboxRecipeManager.class.getDeclaredClasses();
		Class<?> actualClass = null;
		for (Class<?> clas : classes) {
			if (clas.getName().equals("ic2.core.recipe.ScrapboxRecipeManager$Drop")) {
				actualClass = clas;
				break;
			}
		}
		if (actualClass == null)
			return;
		final Class<?> dropClass = actualClass;
		final Constructor<?> dropConstructor;
		try {
			dropConstructor = dropClass.getDeclaredConstructor(ItemStack.class, float.class);
			dropConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			return;
		}
		final List<Object> dropList = Util.getField(ScrapboxRecipeManager.class, "drops", Recipes.scrapboxDrops, List.class);
		final List<Object> newDrops = new ArrayList<>();
		dropList.forEach(drop -> {
			try {
				newDrops.add(dropConstructor.newInstance(resourceHandler.getMainItemStack(Util.getField(dropClass, "item", drop, ItemStack.class)), Util.getField(dropClass, "originalChance", drop, Float.class)));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
		dropList.clear();
		dropList.addAll(newDrops);
	}

	private void fixCokeKilnRecipes() {
		try {
			Class.forName("ic2.core.block.steam.TileEntityCokeKiln");

			final DynamicRecipeManager recipeManager = TileEntityCokeKiln.recipeManager;

			final Map<Collection<RecipeInputIngredient<?>>, DynamicRecipe> recipes =
					Util.getField(DynamicRecipeManager.class, "recipes", recipeManager, Map.class);
			final Map<Item, List<DynamicRecipe>> recipeCacheItem = Util.getField(DynamicRecipeManager.class,
					"recipeCacheItem", recipeManager, Map.class);
			final Map<String, List<DynamicRecipe>> recipeCacheFluid = Util.getField(DynamicRecipeManager.class,
					"recipeCacheFluid", recipeManager, Map.class);
			final List<DynamicRecipe> uncacheableRecipes = Util.getField(DynamicRecipeManager.class,
					"uncacheableRecipes", recipeManager, List.class);

			if (recipes == null || recipeCacheItem == null || recipeCacheFluid == null || uncacheableRecipes == null)
				return;

			recipeCacheItem.clear();
			recipeCacheFluid.clear();
			uncacheableRecipes.clear();

			final List<DynamicRecipe> toRegister = new ArrayList<>();
			final Iterator<DynamicRecipe> recipeIterator = recipes.values().iterator();
			while (recipeIterator.hasNext()) {
				final DynamicRecipe recipe = recipeIterator.next();

				final List<RecipeOutputIngredient> newOutputs = new ArrayList<>();

				boolean skip = false;
				for (RecipeOutputIngredient<?> outputIngredient : recipe.getOutputIngredients()) {
					if (outputIngredient instanceof RecipeOutputFluidStack){
						newOutputs.add(outputIngredient);
					}
					else if (outputIngredient instanceof RecipeOutputItemStack) {
						RecipeOutputItemStack itemStack = (RecipeOutputItemStack)outputIngredient;
						newOutputs.add(RecipeOutputItemStack.of(resourceHandler.getMainItemStack(itemStack.ingredient)));
					}
					else {
						skip = true;
					}
				}
				if (skip)
					continue;

				toRegister.add(new DynamicRecipe(recipeManager)
						.withInput(recipe.getInputIngredients())
						.withOutput(newOutputs)
						.withMetadata(recipe.getMetadata())
						.withOperationDurationTicks(recipe.getOperationDuration())
						.withOperationEnergyCost(recipe.getOperationEnergyCost()));

				recipeIterator.remove();
			}

			toRegister.forEach(DynamicRecipe::register);
		} catch (ClassNotFoundException e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
	}
}
