package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import nmd.primal.forgecraft.crafting.AnvilCrafting;
import nmd.primal.forgecraft.crafting.BloomeryCrafting;
import nmd.primal.forgecraft.crafting.CastingformCrafting;
import nmd.primal.forgecraft.crafting.ForgeCrafting;
import wanion.lib.common.Util;

import java.util.List;

class ForgeCraftIntegration extends AbstractIntegrationThread
{
	ForgeCraftIntegration()
	{
		super("Kitsu's ForgeCraft");
	}

	@Override
	public String call()
	{
		try {
			fixAnvilCrafting();
			fixBloomeryCrafting();
			fixCastingformCrafting();
			fixForgeCrafting();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "good work Kitsu =)";
	}

	private void fixAnvilCrafting()
	{
		final List<AnvilCrafting> recipes = Util.getField(AnvilCrafting.class, "anvilRecipes", null, List.class);
		if (recipes != null)
			recipes.forEach(recipe -> Util.setField(AnvilCrafting.class, "output", recipe, resourceHandler.getMainItemStack(recipe.getOutput())));
	}

	private void fixBloomeryCrafting()
	{
		final List<BloomeryCrafting> recipes = Util.getField(BloomeryCrafting.class, "bloomeryRecipes", null, List.class);
		if (recipes != null)
			recipes.forEach(recipe -> Util.setField(BloomeryCrafting.class, "output", recipe, resourceHandler.getMainItemStack(recipe.getOutput())));
	}

	private void fixCastingformCrafting()
	{
		final List<CastingformCrafting> recipes = Util.getField(CastingformCrafting.class, "castingRecipes", null, List.class);
		if (recipes != null)
			recipes.forEach(recipe -> Util.setField(CastingformCrafting.class, "output", recipe, resourceHandler.getMainItemStack(recipe.getOutput())));
	}

	private void fixForgeCrafting()
	{
		final List<ForgeCrafting> recipes = Util.getField(ForgeCrafting.class, "forgeRecipes", null, List.class);
		if (recipes != null)
			recipes.forEach(recipe -> Util.setField(ForgeCrafting.class, "output", recipe, resourceHandler.getMainItemStack(recipe.getOutput())));
	}
}