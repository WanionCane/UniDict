package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import WayofTime.bloodmagic.api.recipe.AlchemyTableCustomRecipe;
import WayofTime.bloodmagic.api.recipe.AlchemyTableRecipe;
import WayofTime.bloodmagic.api.registry.AlchemyTableRecipeRegistry;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

final class BloodMagicIntegration extends AbstractIntegrationThread
{
	private final UniOreDictionary uniOreDictionary = UniDict.getDependencies().get(UniOreDictionary.class);

	BloodMagicIntegration()
	{
		super("Blood Magic");
	}

	@Override
	public String call()
	{
		try {
			fixAlchemyTableRecipes();
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
		return threadName + "It's bleeding...";
	}

	private void fixAlchemyTableRecipes()
	{
		final List<AlchemyTableRecipe> alchemyTableRecipes = Util.getField(AlchemyTableRecipeRegistry.class, "recipeList", null, List.class);
		if (alchemyTableRecipes == null)
			return;
		final List<AlchemyTableRecipe> newRecipes = new ArrayList<>();
		for (final Iterator<AlchemyTableRecipe> alchemyTableRecipeIterator = alchemyTableRecipes.iterator(); alchemyTableRecipeIterator.hasNext(); )
		{
			final AlchemyTableRecipe alchemyTableRecipe = alchemyTableRecipeIterator.next();
			final Class<? extends AlchemyTableRecipe> recipeClass = alchemyTableRecipe.getClass();
			if (!(recipeClass == AlchemyTableRecipe.class || alchemyTableRecipe.getClass() == AlchemyTableCustomRecipe.class))
				continue;
			Object[] inputs = alchemyTableRecipe.getInput().stream().map(input -> input instanceof List ? uniOreDictionary.getName(input) : input).collect(Collectors.toCollection(ArrayList::new)).toArray();
			newRecipes.add(recipeClass == AlchemyTableCustomRecipe.class
					? new AlchemyTableCustomRecipe(resourceHandler.getMainItemStack(alchemyTableRecipe.getRecipeOutput(null)), alchemyTableRecipe.getLpDrained(), alchemyTableRecipe.getTicksRequired(), alchemyTableRecipe.getTierRequired(), inputs)
					: new AlchemyTableRecipe(resourceHandler.getMainItemStack(alchemyTableRecipe.getRecipeOutput(null)), alchemyTableRecipe.getLpDrained(), alchemyTableRecipe.getTicksRequired(), alchemyTableRecipe.getTierRequired(), inputs));
			alchemyTableRecipeIterator.remove();
		}
		alchemyTableRecipes.addAll(newRecipes);
	}
}