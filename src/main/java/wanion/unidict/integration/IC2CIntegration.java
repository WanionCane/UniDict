package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.custom.IClassicScrapBoxManager;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.classic.recipe.machine.MachineOutput;
import ic2.core.item.recipe.ScrapBoxManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import trinsdar.ic2c_extras.recipes.Ic2cExtrasRecipes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class IC2CIntegration extends AbstractIntegrationThread
{
	IC2CIntegration()
	{
		super("Industrial Craft 2 Classic");
	}

	@Override
	public String call()
	{
		try {
			fixMachineOutput(ClassicRecipes.macerator);
			fixMachineOutput(ClassicRecipes.compressor);
			fixMachineOutput(ClassicRecipes.extractor);
			fixMachineOutput(ClassicRecipes.sawMill);
			fixMachineOutput(ClassicRecipes.recycler);
			fixScrapBoxDrops();
			if (Loader.isModLoaded("ic2c_extras"))
				fixIc2CExtrasRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
		return threadName + "Expect the world to be nuked by the Industrial Revolution.";
	}

	private void fixMachineOutput(@Nonnull final IMachineRecipeList iMachineRecipeList)
	{
		final List<IMachineRecipeList.RecipeEntry> recipeEntryList = iMachineRecipeList.getRecipeMap();
		if (recipeEntryList == null || recipeEntryList.isEmpty())
			return;
		final Map<IMachineRecipeList.RecipeEntry, List<ItemStack>> recipeEntryListMap = new HashMap<>();
		for (final IMachineRecipeList.RecipeEntry recipeEntry : recipeEntryList) {
			final MachineOutput machineOutput = recipeEntry.getOutput();
			if (!recipeEntry.getOutput().canOverride())
				continue;
			final List<ItemStack> outputList = machineOutput.getAllOutputs();
			final List<ItemStack> newOutputList = resourceHandler.getMainItemStacksChecked(outputList);
			if (outputList == newOutputList)
				continue;
			recipeEntryListMap.put(recipeEntry, newOutputList);
		}
		recipeEntryListMap.forEach((recipeEntry, newOutputsList) -> {
			iMachineRecipeList.removeRecipe(recipeEntry);
			iMachineRecipeList.addRecipe(recipeEntry.getInput(), recipeEntry.getOutput().overrideOutput(newOutputsList), recipeEntry.getRecipeID());
		});
	}

	private void fixScrapBoxDrops()
	{
		final IClassicScrapBoxManager classicScrapBoxManager = ClassicRecipes.scrapboxDrops;
		final List<ScrapBoxManager.IDrop> dropList = classicScrapBoxManager.getEntries();
		final Map<ScrapBoxManager.IDrop, ItemStack> newDrops = new HashMap<>();
		for (IClassicScrapBoxManager.IDrop drop : dropList) {
			final ItemStack newItemStackDrop = resourceHandler.getMainItemStack(drop.getDrop());
			if (newItemStackDrop != drop.getDrop())
				newDrops.put(drop, newItemStackDrop);
		}
		newDrops.forEach((drop, newItemStackDrop) -> {
			classicScrapBoxManager.removeDrop(drop);
			classicScrapBoxManager.addDrop(newItemStackDrop, drop.getRawChance());
		});
	}

	private void fixIc2CExtrasRecipes() {
		fixMachineOutput(Ic2cExtrasRecipes.rolling);
		fixMachineOutput(Ic2cExtrasRecipes.extruding);
		fixMachineOutput(Ic2cExtrasRecipes.cutting);
		fixMachineOutput(Ic2cExtrasRecipes.oreWashingPlant);
		fixMachineOutput(Ic2cExtrasRecipes.thermalCentrifuge);
	}
}
