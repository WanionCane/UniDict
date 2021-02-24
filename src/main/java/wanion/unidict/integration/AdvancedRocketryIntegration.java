package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import zmaster587.libVulpes.recipe.RecipesMachine;

import java.lang.reflect.Field;
import java.util.List;

final class AdvancedRocketryIntegration extends AbstractIntegrationThread
{
	private Field outputField;

	AdvancedRocketryIntegration()
	{
		super("Advanced Rocketry");
		try {
			(outputField = RecipesMachine.Recipe.class.getDeclaredField("output")).setAccessible(true);
		} catch (NoSuchFieldException e) {
			logger.error("Couldn't find Advanced Rocketry fields!");
			e.printStackTrace();
		}
	}

	@Override
	public String call()
	{
		try {
			if (outputField != null)
				fixRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "10. 9...3 2 1... BOOOOM!!";
	}

	@SuppressWarnings("unchecked")
	private void fixRecipes()
	{
		RecipesMachine.getInstance().recipeList.values().forEach(iRecipes -> iRecipes.forEach(iRecipe -> {
			if (iRecipe instanceof RecipesMachine.Recipe)
				try {
					((List<RecipesMachine.ChanceItemStack> ) outputField.get(iRecipe)).forEach(stack -> stack.stack = resourceHandler.getMainItemStack(stack.stack));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
		}));
	}
}
