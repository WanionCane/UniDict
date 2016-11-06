package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.unidict.UniDict;
import zmaster587.libVulpes.recipe.RecipesMachine;

final class AdvancedRocketryIntegration extends AbstractIntegrationThread
{
	AdvancedRocketryIntegration()
	{
		super("Advanced Rocketry");
	}

	@Override
	public String call()
	{
		try {
			fixRecipes();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "10. 9...3 2 1... BOOOOM!!";
	}

	private void fixRecipes()
	{
		RecipesMachine.getInstance().recipeList.values().forEach(iRecipes -> iRecipes.forEach(iRecipe -> {
			if (iRecipe instanceof RecipesMachine.Recipe)
				resourceHandler.setMainItemStacks(iRecipe.getOutput());
		}));
	}
}