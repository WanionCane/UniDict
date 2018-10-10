package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Lists;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.crafting.IAdvRecipe;
import ic2.api.classic.recipe.crafting.ICraftingRecipeList;
import ic2.api.classic.recipe.custom.IClassicScrapBoxManager;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.classic.recipe.machine.MachineOutput;
import ic2.core.item.recipe.AdvRecipeBase;
import ic2.core.item.recipe.ScrapBoxManager;
import net.minecraft.item.ItemStack;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.recipe.IC2CRecipeResearcher;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.*;

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
		} catch (Exception e) { e.printStackTrace(); }
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

	static void fixAdvancedRecipes(final ResourceHandler resourceHandler)
	{
		final ICraftingRecipeList advCrafting = ClassicRecipes.advCrafting;
		final List<IAdvRecipe> advRecipeList = advCrafting.getRecipes();
		final Map<AdvRecipeBase, ItemStack> advRecipeItemStackMap = new HashMap<>();
		for (final IAdvRecipe advRecipe : advRecipeList) {
			if (advRecipe instanceof AdvRecipeBase) {
				if (!advRecipe.isInvisible())
					continue;
				final ItemStack output = ((AdvRecipeBase) advRecipe).getRecipeOutput();
				final ItemStack newOutput = resourceHandler.getMainItemStack(output);
				if (output != newOutput)
					advRecipeItemStackMap.put((AdvRecipeBase) advRecipe, newOutput);
			}
		}
		advRecipeItemStackMap.forEach((advRecipe, newOutput) -> {

			switch (advRecipe.getRecipeType()) {
				case Shaped:
					final List<Object> newShapedInputs = Lists.newArrayList(RecipeHelper.rawShapeToShape(IC2CRecipeResearcher.getNewShapedRecipeInputs(advRecipe, resourceHandler)).actualShape);
					newShapedInputs.add(true);
					advCrafting.overrideRecipe(advRecipe.getRecipeID(), newOutput, newShapedInputs.toArray());
					break;
				case Shapeless:
					final List<Object> newShapelessInputs = IC2CRecipeResearcher.getNewShapelessRecipeInputs(advRecipe, resourceHandler);
					newShapelessInputs.add(true);
					advCrafting.overrideShapelessRecipe(advRecipe.getRecipeID(), newOutput, newShapelessInputs.toArray());
			}
		});
	}
}