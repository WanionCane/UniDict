package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cofh.thermalexpansion.util.managers.machine.*;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class TEIntegration extends AbstractIntegrationThread
{
	TEIntegration()
	{
		super("Thermal Expansion");
	}

	@Override
	public String call()
	{
		try {
			fixCompactorRecipes();
			fixRefineryRecipes();
			fixInductionSmelterRecipes();
			fixRedstoneFurnaceRecipes();
			fixPulverizerRecipes();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "The world seems to be more thermally involved.";
	}

	private void fixCompactorRecipes()
	{
		final Map<CompactorManager.ComparableItemStackCompactor, CompactorManager.CompactorRecipe> recipeMapPress = Util.getField(CompactorManager.class, "recipeMapPress", null, Map.class);
		final Map<CompactorManager.ComparableItemStackCompactor, CompactorManager.CompactorRecipe> recipeMapStorage = Util.getField(CompactorManager.class, "recipeMapStorage", null, Map.class);
		if (recipeMapPress == null || recipeMapStorage == null)
			return;
		Constructor<CompactorManager.CompactorRecipe> recipeCompactorConstructor = null;
		try {
			recipeCompactorConstructor = CompactorManager.CompactorRecipe.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, int.class);
			recipeCompactorConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (recipeCompactorConstructor == null)
			return;
		final List<Map<CompactorManager.ComparableItemStackCompactor, CompactorManager.CompactorRecipe>> recipeMaps = new ArrayList<>();
		recipeMaps.add(recipeMapPress);
		recipeMaps.add(recipeMapStorage);
		for (final Map<CompactorManager.ComparableItemStackCompactor, CompactorManager.CompactorRecipe> recipeMap : recipeMaps)
			for (final CompactorManager.ComparableItemStackCompactor recipeMapKey : recipeMap.keySet()) {
				final CompactorManager.CompactorRecipe recipeCompactor = recipeMap.get(recipeMapKey);
				final ItemStack correctOutput = resourceHandler.getMainItemStack(recipeCompactor.getOutput());
				if (correctOutput == recipeCompactor.getOutput())
					continue;
				try {
					recipeMap.put(recipeMapKey, recipeCompactorConstructor.newInstance(recipeCompactor.getInput(), correctOutput, recipeCompactor.getEnergy()));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
	}

	private void fixInductionSmelterRecipes()
	{
		final Map<List<SmelterManager.ComparableItemStackSmelter>, SmelterManager.SmelterRecipe> recipeMap = Util.getField(SmelterManager.class, "recipeMap", null, Map.class);
		if (recipeMap == null)
			return;
		Constructor<SmelterManager.SmelterRecipe> smelterRecipeConstructor = null;
		try {
			smelterRecipeConstructor = SmelterManager.SmelterRecipe.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class);
			smelterRecipeConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (smelterRecipeConstructor == null)
			return;
		for (final List<SmelterManager.ComparableItemStackSmelter> recipeMapKey : recipeMap.keySet()) {
			final SmelterManager.SmelterRecipe smelterRecipe = recipeMap.get(recipeMapKey);
			final ItemStack correctOutput = resourceHandler.getMainItemStack(smelterRecipe.getPrimaryOutput());
			final ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(smelterRecipe.getSecondaryOutput());
			if (correctOutput == smelterRecipe.getPrimaryOutput() && correctSecondaryOutput == smelterRecipe.getSecondaryOutput())
				continue;
			try {
				recipeMap.put(recipeMapKey, smelterRecipeConstructor.newInstance(smelterRecipe.getPrimaryInput(), smelterRecipe.getSecondaryInput(), correctOutput, correctSecondaryOutput, smelterRecipe.getSecondaryOutputChance(), smelterRecipe.getEnergy()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void fixRefineryRecipes()
	{
		final TIntObjectHashMap<RefineryManager.RefineryRecipe> recipeMap = Util.getField(RefineryManager.class, "recipeMap", null, TIntObjectMap.class);
		if (recipeMap == null)
			return;
		Constructor<RefineryManager.RefineryRecipe> refineryRecipeConstructor = null;
		try {
			refineryRecipeConstructor = RefineryManager.RefineryRecipe.class.getDeclaredConstructor(FluidStack.class, FluidStack.class, ItemStack.class, int.class, int.class);
			refineryRecipeConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (refineryRecipeConstructor == null)
			return;
		for (final int recipeMapKey : recipeMap.keys()) {
			final RefineryManager.RefineryRecipe refineryRecipe = recipeMap.get(recipeMapKey);
			final ItemStack correctOutput = resourceHandler.getMainItemStack(refineryRecipe.getOutputItem());
			if (correctOutput == refineryRecipe.getOutputItem())
				continue;
			try {
				recipeMap.put(recipeMapKey, refineryRecipeConstructor.newInstance(refineryRecipe.getInput(), refineryRecipe.getOutputFluid(), correctOutput, refineryRecipe.getEnergy(), refineryRecipe.getChance()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void fixRedstoneFurnaceRecipes()
	{
		final Map<FurnaceManager.ComparableItemStackFurnace, FurnaceManager.FurnaceRecipe> recipeMap = Util.getField(FurnaceManager.class, "recipeMap", null, Map.class);
		if (recipeMap == null)
			return;
		Constructor<FurnaceManager.FurnaceRecipe> redstoneFurnaceRecipeConstructor = null;
		try {
			redstoneFurnaceRecipeConstructor = FurnaceManager.FurnaceRecipe.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, int.class);
			redstoneFurnaceRecipeConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (redstoneFurnaceRecipeConstructor == null)
			return;
		for (final FurnaceManager.ComparableItemStackFurnace recipeMapKey : recipeMap.keySet()) {
			final FurnaceManager.FurnaceRecipe redstoneFurnaceRecipe = recipeMap.get(recipeMapKey);
			final ItemStack correctOutput = resourceHandler.getMainItemStack(redstoneFurnaceRecipe.getOutput());
			if (correctOutput == redstoneFurnaceRecipe.getOutput())
				continue;
			try {
				recipeMap.put(recipeMapKey, redstoneFurnaceRecipeConstructor.newInstance(redstoneFurnaceRecipe.getInput(), correctOutput, redstoneFurnaceRecipe.getEnergy()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void fixPulverizerRecipes()
	{
		final Map<PulverizerManager.ComparableItemStackPulverizer, PulverizerManager.PulverizerRecipe> recipeMap = Util.getField(PulverizerManager.class, "recipeMap", null, Map.class);
		if (recipeMap == null)
			return;
		Constructor<PulverizerManager.PulverizerRecipe> pulverizerRecipeConstructor = null;
		try {
			pulverizerRecipeConstructor = PulverizerManager.PulverizerRecipe.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class);
			pulverizerRecipeConstructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (pulverizerRecipeConstructor == null)
			return;
		for (final PulverizerManager.ComparableItemStackPulverizer recipeMapKey : recipeMap.keySet()) {
			final PulverizerManager.PulverizerRecipe pulverizerRecipe = recipeMap.get(recipeMapKey);
			final ItemStack correctOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getPrimaryOutput());
			final ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getSecondaryOutput());
			if (correctOutput == pulverizerRecipe.getPrimaryOutput() && correctSecondaryOutput == pulverizerRecipe.getSecondaryOutput())
				continue;
			try {
				recipeMap.put(recipeMapKey, pulverizerRecipeConstructor.newInstance(pulverizerRecipe.getInput(), correctOutput, correctSecondaryOutput, pulverizerRecipe.getSecondaryOutputChance(), pulverizerRecipe.getEnergy()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}