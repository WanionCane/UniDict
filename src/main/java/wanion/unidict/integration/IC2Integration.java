package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import ic2.core.recipe.MachineRecipeHelper;
import ic2.core.recipe.ScrapboxRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class IC2Integration extends AbstractIntegrationThread
{
	private final List<Map<IRecipeInput, MachineRecipe<IRecipeInput, Collection<ItemStack>>>> ic2MachinesRecipeList = new ArrayList<>(7);

	IC2Integration()
	{
		super("Industrial Craft 2");
		try {
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.centrifuge, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.compressor, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.blastfurnace, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.macerator, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.metalformerCutting, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.metalformerExtruding, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.metalformerRolling, Map.class));
			ic2MachinesRecipeList.add(Util.getField(MachineRecipeHelper.class, "recipes", Recipes.blockcutter, Map.class));
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
	}

	@Override
	public String call()
	{
		ic2MachinesRecipeList.forEach(map -> {
			try {
				fixMachinesOutputs(map);
				fixScrapBoxDrops();
			} catch (Exception e) { logger.error(threadName + e); }
		});
		return threadName + "The world appears to be entirely industrialized.";
	}

	private void fixMachinesOutputs(final Map<IRecipeInput, MachineRecipe<IRecipeInput, Collection<ItemStack>>> recipes)
	{
		for (final Map.Entry<IRecipeInput, MachineRecipe<IRecipeInput, Collection<ItemStack>>> recipe : recipes.entrySet())
			recipe.setValue(new MachineRecipe<>(recipe.getValue().getInput(), resourceHandler.getMainItemStacks(recipe.getValue().getOutput())));
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