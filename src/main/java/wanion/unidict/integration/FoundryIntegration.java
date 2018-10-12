package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import exter.foundry.api.recipe.IAlloyFurnaceRecipe;
import exter.foundry.api.recipe.IAtomizerRecipe;
import exter.foundry.api.recipe.ICastingRecipe;
import exter.foundry.api.recipe.ICastingTableRecipe;
import exter.foundry.api.recipe.matcher.ItemStackMatcher;
import exter.foundry.recipes.AlloyFurnaceRecipe;
import exter.foundry.recipes.AtomizerRecipe;
import exter.foundry.recipes.CastingRecipe;
import exter.foundry.recipes.CastingTableRecipe;
import exter.foundry.recipes.manager.AlloyFurnaceRecipeManager;
import exter.foundry.recipes.manager.AtomizerRecipeManager;
import exter.foundry.recipes.manager.CastingRecipeManager;
import exter.foundry.recipes.manager.CastingTableRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class FoundryIntegration extends AbstractIntegrationThread
{
	FoundryIntegration()
	{
		super("Foundry");
	}

	@Override
	public String call()
	{
		try {
			fixAlloyFurnaceRecipes();
			fixAtomizerRecipes();
			fixCastingRecipes();
			fixCastingTableRecipes();
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
		return threadName + "Somethings that were made in casts had to change.";
	}

	private void fixAlloyFurnaceRecipes()
	{
		final AlloyFurnaceRecipeManager alloyFurnaceRecipeManager = AlloyFurnaceRecipeManager.INSTANCE;

		final List<IAlloyFurnaceRecipe> removalList = new ArrayList<>();
		final List<IAlloyFurnaceRecipe> newRecipes = new ArrayList<>();
		for (final IAlloyFurnaceRecipe atomizerRecipe : alloyFurnaceRecipeManager.getRecipes()) {
			final ItemStack output = atomizerRecipe.getOutput();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(output);
			if (output == correctOutput)
				continue;
			newRecipes.add(new AlloyFurnaceRecipe(correctOutput, atomizerRecipe.getInputA(), atomizerRecipe.getInputB()));
			removalList.add(atomizerRecipe);
		}
		removalList.forEach(alloyFurnaceRecipeManager::removeRecipe);
		newRecipes.forEach(alloyFurnaceRecipeManager::addRecipe);
	}

	private void fixAtomizerRecipes()
	{
		final AtomizerRecipeManager atomizerRecipeManager = AtomizerRecipeManager.INSTANCE;
		final List<IAtomizerRecipe> removalList = new ArrayList<>();
		final List<IAtomizerRecipe> newRecipes = new ArrayList<>();
		for (final IAtomizerRecipe atomizerRecipe : atomizerRecipeManager.getRecipes()) {
			final ItemStack output = atomizerRecipe.getOutput();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(output);
			if (output == correctOutput)
				continue;
			newRecipes.add(new AtomizerRecipe(new ItemStackMatcher(correctOutput), atomizerRecipe.getInput()));
			removalList.add(atomizerRecipe);
		}
		removalList.forEach(atomizerRecipeManager::removeRecipe);
		newRecipes.forEach(atomizerRecipeManager::addRecipe);
	}

	private void fixCastingRecipes()
	{
		final CastingRecipeManager castingRecipeManager = CastingRecipeManager.INSTANCE;
		final List<ICastingRecipe> removalList = new ArrayList<>();
		final List<ICastingRecipe> newRecipes = new ArrayList<>();
		for (final ICastingRecipe castingRecipe : castingRecipeManager.getRecipes()) {
			final ItemStack output = castingRecipe.getOutput();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(output);
			if (output == correctOutput)
				continue;
			newRecipes.add(new CastingRecipe(new ItemStackMatcher(correctOutput), castingRecipe.getInput(), castingRecipe.getMold(), castingRecipe.getInputExtra(), castingRecipe.getCastingSpeed()));
			removalList.add(castingRecipe);
		}
		removalList.forEach(castingRecipeManager::removeRecipe);
		newRecipes.forEach(castingRecipeManager::addRecipe);
	}

	private void fixCastingTableRecipes()
	{
		final Map<ICastingTableRecipe.TableType, Map<String, ICastingTableRecipe>> castingTableRecipeMap = CastingTableRecipeManager.INSTANCE.getRecipesMap();
		castingTableRecipeMap.forEach((tableType, stringICastingTableRecipeMap) -> stringICastingTableRecipeMap.entrySet().forEach(entry -> {
			final ICastingTableRecipe castingTableRecipe = entry.getValue();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(castingTableRecipe.getOutput());
			if (castingTableRecipe.getOutput() != correctOutput) {
				entry.setValue(new CastingTableRecipe(new ItemStackMatcher(correctOutput), castingTableRecipe.getInput(), castingTableRecipe.getTableType()));
			}
		}));
	}
}