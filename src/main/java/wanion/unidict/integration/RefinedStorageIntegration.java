package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.*;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;

import java.lang.reflect.Field;

class RefinedStorageIntegration extends AbstractIntegrationThread
{
	private final Field soldererRecipeFluidStorageRows;
	private final Field soldererRecipePrintedProcessorRequeriment;
	private final Field soldererRecipeProcessorRows;
	private final Field soldererRecipeStorageRows;
	private final Field soldererRecipeUpgradeRows;
	RefinedStorageIntegration()
	{
		super("Refined Storage");
		try {
			(soldererRecipeFluidStorageRows = SoldererRecipeFluidStorage.class.getDeclaredField("rows")).setAccessible(true);
			(soldererRecipePrintedProcessorRequeriment = SoldererRecipePrintedProcessor.class.getDeclaredField("requirement")).setAccessible(true);
			(soldererRecipeProcessorRows = SoldererRecipeProcessor.class.getDeclaredField("rows")).setAccessible(true);
			(soldererRecipeStorageRows = SoldererRecipeStorage.class.getDeclaredField("rows")).setAccessible(true);
			(soldererRecipeUpgradeRows = SoldererRecipeUpgrade.class.getDeclaredField("rows")).setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Couldn't find the fields.");
		}
	}

	@Override
	public String call()
	{
		try {
			fixRecipes();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "Storing all the things.";
	}

	private void fixRecipes() throws IllegalAccessException
	{
		for (final ISoldererRecipe soldererRecipe : API.instance().getSoldererRegistry().getRecipes()) {
			if (soldererRecipe instanceof SoldererRecipeFluidStorage)
				resourceHandler.setMainItemStacks((Object[]) soldererRecipeFluidStorageRows.get(soldererRecipe));
			else if (soldererRecipe instanceof SoldererRecipePrintedProcessor)
				soldererRecipePrintedProcessorRequeriment.set(soldererRecipe, resourceHandler.getMainItemStack((ItemStack) soldererRecipePrintedProcessorRequeriment.get(soldererRecipe)));
			else if (soldererRecipe instanceof SoldererRecipeProcessor)
				resourceHandler.setMainItemStacks((Object[]) soldererRecipeProcessorRows.get(soldererRecipe));
			else if (soldererRecipe instanceof SoldererRecipeStorage)
				resourceHandler.setMainItemStacks((Object[]) soldererRecipeStorageRows.get(soldererRecipe));
			else if (soldererRecipe instanceof SoldererRecipeUpgrade)
				resourceHandler.setMainItemStacks((Object[]) soldererRecipeUpgradeRows.get(soldererRecipe));
		}
	}
}