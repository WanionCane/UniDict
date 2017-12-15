package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import teamroots.embers.recipe.RecipeRegistry;

final class EmbersIntegration extends AbstractIntegrationThread
{
	EmbersIntegration()
	{
		super("Embers");
	}

	@Override
	public String call()
	{
		try {
			RecipeRegistry.stampingRecipes.forEach(itemStampingRecipe -> itemStampingRecipe.result = resourceHandler.getMainItemStack(itemStampingRecipe.result));
			RecipeRegistry.stampingOreRecipes.forEach(itemStampingOreRecipe -> itemStampingOreRecipe.result = resourceHandler.getMainItemStack(itemStampingOreRecipe.result));
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "Stamper is outputting the right things now.";
	}
}