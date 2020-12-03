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
import java.util.Collection;

final class TechRebornIntegration extends AbstractIntegrationThread
{
	private Field recipeOutputs;

	TechRebornIntegration()
	{
		super("TechReborn");
		try {
			(recipeOutputs = Recipe.class.getDeclaredField("itemOutputs")).setAccessible(true);
		} catch (NoSuchFieldException | NullPointerException e) {
			e.printStackTrace();
		}
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

		if (recipeOutputs == null)
			return;

		fixPraescriptumRecipes(Recipes.alloySmelter.getRecipes());
		fixPraescriptumRecipes(Recipes.assemblingMachine.getRecipes());
		fixPraescriptumRecipes(Recipes.centrifuge.getRecipes());
		fixPraescriptumRecipes(Recipes.chemicalReactor.getRecipes());
		fixPraescriptumRecipes(Recipes.compressor.getRecipes());
		fixPraescriptumRecipes(Recipes.extractor.getRecipes());
		fixPraescriptumRecipes(Recipes.grinder.getRecipes());
		fixPraescriptumRecipes(Recipes.plateBendingMachine.getRecipes());
		fixPraescriptumRecipes(Recipes.recycler.getRecipes());
		fixPraescriptumRecipes(Recipes.solidCanningMachine.getRecipes());
		fixPraescriptumRecipes(Recipes.wireMill.getRecipes());
	}

	private void fixPraescriptumRecipes(Collection<Recipe> recipes) {
		recipes.forEach(recipe -> {
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