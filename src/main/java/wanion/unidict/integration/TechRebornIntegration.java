package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.recipe.RecipeHandler;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.api.reactor.FusionReactorRecipeHelper;
import techreborn.api.recipe.Recipes;
import wanion.lib.common.Util;

import java.lang.reflect.Field;

final class TechRebornIntegration extends AbstractIntegrationThread
{
	private Field recipeOutputs;

	TechRebornIntegration()
	{
		super("TechReborn");
		try {
			(recipeOutputs = Recipe.class.getDeclaredField("itemOutputs")).setAccessible(true);
		} catch (NoSuchFieldException | NullPointerException e) {
			logger.error("Couldn't find TechReborn fields!");
			e.printStackTrace();
		}
	}

	@Override
	public String call()
	{
		try {
			fixFusionReactorRecipe();
			fixTechRebornRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
		return threadName + "now Tech is truly Reborn.";
	}

	private void fixTechRebornRecipes()
	{
		RecipeHandler.recipeList.forEach(recipe -> {
			if (!recipe.useOreDic())
				resourceHandler.setMainObjects(recipe.getInputs());
			resourceHandler.setMainItemStacks(recipe.getOutputs());
		});

		if (recipeOutputs == null)
			return;

		fixPraescriptumRecipes(Recipes.alloySmelter);
		fixPraescriptumRecipes(Recipes.assemblingMachine);
		fixPraescriptumRecipes(Recipes.centrifuge);
		fixPraescriptumRecipes(Recipes.chemicalReactor);
		fixPraescriptumRecipes(Recipes.compressor);
		fixPraescriptumRecipes(Recipes.extractor);
		fixPraescriptumRecipes(Recipes.grinder);
		fixPraescriptumRecipes(Recipes.plateBendingMachine);
		fixPraescriptumRecipes(Recipes.recycler);
		fixPraescriptumRecipes(Recipes.solidCanningMachine);
		fixPraescriptumRecipes(Recipes.wireMill);
	}

	private void fixPraescriptumRecipes(reborncore.api.praescriptum.recipes.RecipeHandler handler) {
		if (handler == null)
			return;
		handler.getRecipes().forEach(recipe -> {
			try {
				recipeOutputs.set(recipe, resourceHandler.getMainItemStacks(recipe.getItemOutputs()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	private void fixFusionReactorRecipe()
	{
		FusionReactorRecipeHelper.reactorRecipes.forEach(fusionReactorRecipe -> Util.setField(FusionReactorRecipe.class, "output", fusionReactorRecipe, resourceHandler.getMainItemStack(fusionReactorRecipe.getOutput())));
	}
}
