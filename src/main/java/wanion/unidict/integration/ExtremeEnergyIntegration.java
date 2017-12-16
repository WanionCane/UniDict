package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ru.minebot.extreme_energy.recipes.managers.AssemblerRecipes;
import ru.minebot.extreme_energy.recipes.managers.CrusherRecipes;

import java.util.ArrayList;
import java.util.List;

class ExtremeEnergyIntegration extends AbstractIntegrationThread
{
	ExtremeEnergyIntegration()
	{
		super("Extreme Energy");
	}

	@Override
	public String call()
	{
		try {
			fixAssemblerRecipes();
			fixCrusherRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "be careful with this much energy.";
	}

	private void fixAssemblerRecipes()
	{
		final List<AssemblerRecipes.FullRecipeAssembler> recipes = AssemblerRecipes.recipesList;
		final List<AssemblerRecipes.FullRecipeAssembler> newRecipes = new ArrayList<>();
		recipes.forEach(recipe -> newRecipes.add(new AssemblerRecipes.FullRecipeAssembler(recipe.getInput(), resourceHandler.getMainItemStack(recipe.getOutput()), recipe.getEnergy())));
		recipes.clear();
		recipes.addAll(newRecipes);
	}

	private void fixCrusherRecipes()
	{
		final List<CrusherRecipes.FullRecipeCrusher> recipes = CrusherRecipes.recipesList;
		final List<CrusherRecipes.FullRecipeCrusher> newRecipes = new ArrayList<>();
		recipes.forEach(recipe -> newRecipes.add(new CrusherRecipes.FullRecipeCrusher(recipe.getInput(), resourceHandler.getMainItemStack(recipe.getOutput()), recipe.getEnergy())));
		recipes.clear();
		recipes.addAll(newRecipes);
	}
}