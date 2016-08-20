package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import nc.crafting.NCRecipeHelper;
import nc.crafting.machine.CrusherRecipes;
import nc.crafting.machine.CrusherRecipesOld;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;

import java.util.Map;

final class NuclearCraftIntegration extends AbstractIntegrationThread
{
	NuclearCraftIntegration()
	{
		super("NuclearCraft");
	}

	@Override
	public String call()
	{
		try {
			fixCrusherRecipes();
			fixElectricCrusherRecipes();
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
		return threadName + "Radioactive.";
	}

	private void fixCrusherRecipes()
	{
		final Map<Object[], Object[]> crusherRecipeMap = Util.getField(NCRecipeHelper.class, "recipeList", CrusherRecipes.instance(), Map.class);
		if (crusherRecipeMap == null)
			return;
		crusherRecipeMap.values().forEach(resourceHandler::setMainItemStacks);
	}

	private void fixElectricCrusherRecipes()
	{
		CrusherRecipesOld.smelting().getSmeltingList().entrySet().forEach(itemStackItemStackEntry -> itemStackItemStackEntry.setValue(resourceHandler.getMainItemStack(itemStackItemStackEntry.getValue())));
	}
}