package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.BloodMagicRecipeRegistrar;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyTable;
import wanion.lib.common.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class BloodMagicIntegration extends AbstractIntegrationThread
{
	BloodMagicIntegration()
	{
		super("Blood Magic");
	}

	@Override
	public String call()
	{
		try {
			fixAlchemyTableRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
		return threadName + "It's bleeding...";
	}

	private void fixAlchemyTableRecipes()
	{
		final Set<RecipeAlchemyTable> alchemyTableRecipes = Util.getField(BloodMagicRecipeRegistrar.class, "alchemyRecipes", BloodMagicAPI.INSTANCE.getRecipeRegistrar(), Set.class);
		if (alchemyTableRecipes == null)
			return;
		final List<RecipeAlchemyTable> newRecipes = new ArrayList<>();
		for (final Iterator<RecipeAlchemyTable> alchemyTableRecipeIterator = alchemyTableRecipes.iterator(); alchemyTableRecipeIterator.hasNext(); ) {
			final RecipeAlchemyTable alchemyTableRecipe = alchemyTableRecipeIterator.next();
			newRecipes.add(new RecipeAlchemyTable(alchemyTableRecipe.getInput(), resourceHandler.getMainItemStack(alchemyTableRecipe.getOutput()), alchemyTableRecipe.getSyphon(), alchemyTableRecipe.getTicks(), alchemyTableRecipe.getMinimumTier()));
			alchemyTableRecipeIterator.remove();
		}
		alchemyTableRecipes.addAll(newRecipes);
	}
}
