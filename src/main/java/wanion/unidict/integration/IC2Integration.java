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
import ic2.core.recipe.MachineRecipeHelper;
import ic2.core.recipe.ScrapboxRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
		} catch (Exception e) { logger.error(threadName + e); }
		try {
			fixScrapBoxDrops();
		} catch (Exception e) { logger.error(threadName + e); }
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
}