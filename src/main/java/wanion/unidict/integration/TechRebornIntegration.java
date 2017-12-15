package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import reborncore.api.recipe.RecipeHandler;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.api.reactor.FusionReactorRecipeHelper;
import wanion.lib.common.Util;

final class TechRebornIntegration extends AbstractIntegrationThread
{
	TechRebornIntegration()
	{
		super("TechReborn");
	}

	@Override
	public String call()
	{
		try {
			fixFusionReactorRecipe();
			fixTechRebornRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "now Tech is truly Reborn.";
	}

	private void fixTechRebornRecipes()
	{
		RecipeHandler.recipeList.forEach(recipe -> {
			if (!recipe.useOreDic())
				resourceHandler.setMainObjects(recipe.getInputs());
			resourceHandler.setMainItemStacks(recipe.getOutputs());
		});
	}

	private void fixFusionReactorRecipe()
	{
		FusionReactorRecipeHelper.reactorRecipes.forEach(fusionReactorRecipe -> Util.setField(FusionReactorRecipe.class, "output", fusionReactorRecipe, resourceHandler.getMainItemStack(fusionReactorRecipe.getOutput())));
	}
}