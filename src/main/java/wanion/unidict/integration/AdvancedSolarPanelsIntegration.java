package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.chocohead.advsolar.IMolecularTransformerRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.recipe.BasicMachineRecipeManager;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;

import java.util.Map;

final class AdvancedSolarPanelsIntegration extends AbstractIntegrationThread
{
	AdvancedSolarPanelsIntegration()
	{
		super("Advanced Solar Panels");
	}

	@Override
	public String call()
	{
		try {
			fixMolecularTransformerRecipess();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "The Molecular Transformer is now outputting the right things.";
	}

	private void fixMolecularTransformerRecipess()
	{
		final Map<IRecipeInput, RecipeOutput> molecularTransformerRecipes = Util.getField(BasicMachineRecipeManager.class, "recipes", IMolecularTransformerRecipeManager.recipes, Map.class);
		if (molecularTransformerRecipes == null)
			return;
		for (final Map.Entry<IRecipeInput, RecipeOutput> recipe : molecularTransformerRecipes.entrySet())
			recipe.setValue(new RecipeOutput(recipe.getValue().metadata, resourceHandler.getMainItemStacks(recipe.getValue().items)));
	}
}