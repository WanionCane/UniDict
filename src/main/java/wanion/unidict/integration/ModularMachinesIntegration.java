package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import modularmachines.api.recipes.Recipe;
import modularmachines.api.recipes.RecipeItem;
import modularmachines.api.recipes.RecipeRegistry;
import wanion.unidict.UniDict;

final class ModularMachinesIntegration extends AbstractIntegrationThread
{
	ModularMachinesIntegration()
	{
		super("Modular-Machines");
	}

	@Override
	public String call()
	{
		try {
			fixAllTheRecipes();
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
		return threadName + "Done";
	}

	private void fixAllTheRecipes()
	{
		RecipeRegistry.getHandlers().values().forEach(iRecipeHandler ->
				iRecipeHandler.getRecipes().forEach(iRecipe -> {
					if (iRecipe instanceof Recipe) {
						final RecipeItem[] recipeOutputs = iRecipe.getOutputs();
						if (recipeOutputs != null)
							for (int i = 0; i < recipeOutputs.length; i++) {
								final RecipeItem recipeOutput = recipeOutputs[i];
								recipeOutputs[i] = new RecipeItem(recipeOutput.index, resourceHandler.getMainItemStack(recipeOutput.item), recipeOutput.fluid, recipeOutput.ore, recipeOutput.chance);
							}
					}
				})
		);
	}
}
