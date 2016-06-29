package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;

import java.util.Iterator;
import java.util.List;

final class RailcraftIntegration extends AbstractIntegrationThread
{
    private List<BlastFurnaceCraftingManager.BlastFurnaceRecipe> blastFurnaceRecipes = Util.getField(BlastFurnaceCraftingManager.class, "recipes", BlastFurnaceCraftingManager.getInstance(), List.class);

    RailcraftIntegration()
    {
        super("Railcraft");
    }

    @Override
    public String call()
    {
        try {
            fixBlastFurnaceRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The things that are made in the explosion chamber had to change.";
    }

    private void fixBlastFurnaceRecipes()
    {
        if (blastFurnaceRecipes == null)
            return;
        List<BlastFurnaceCraftingManager.BlastFurnaceRecipe> newRecipes = new FixedSizeList<>(blastFurnaceRecipes.size());
        for (Iterator blastFurnaceRecipeIterator = blastFurnaceRecipes.iterator(); blastFurnaceRecipeIterator.hasNext(); )
        {
            BlastFurnaceCraftingManager.BlastFurnaceRecipe recipe = (BlastFurnaceCraftingManager.BlastFurnaceRecipe) blastFurnaceRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(recipe.getOutput());
            if (correctOutput == recipe.getOutput())
                continue;
            newRecipes.add(new BlastFurnaceCraftingManager.BlastFurnaceRecipe(recipe.getInput(), recipe.matchDamage(), recipe.matchNBT(), recipe.getCookTime(), correctOutput));
            blastFurnaceRecipeIterator.remove();
        }
        blastFurnaceRecipes.addAll(newRecipes);
    }
}