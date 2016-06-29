package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import appeng.api.AEApi;
import appeng.api.features.IGrinderEntry;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.helper.LogHelper;

import java.util.Iterator;
import java.util.List;

final class AE2Integration extends AbstractIntegrationThread
{
    AE2Integration()
    {
        super("Applied Energistics 2");
    }

    @Override
    public String call()
    {
        try {
            fixGrindStoneRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The world of energistics has never been so powerful.";
    }

    private void fixGrindStoneRecipes()
    {
        List<IGrinderEntry> grindStoneRecipeList = AEApi.instance().registries().grinder().getRecipes();
        TIntSet uniques = new TIntHashSet(grindStoneRecipeList.size(), 1);
        for (Iterator<IGrinderEntry> grindStoneRecipeIterator = grindStoneRecipeList.iterator(); grindStoneRecipeIterator.hasNext(); )
        {
            IGrinderEntry grindStoneRecipe = grindStoneRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(grindStoneRecipe.getOutput());
            if (correctOutput == grindStoneRecipe.getOutput())
                continue;
            if (Config.keepOneEntry)
                grindStoneRecipe.setInput(resourceHandler.getMainItemStack(grindStoneRecipe.getInput()));
            int recipeId = MetaItem.getCumulativeKey(grindStoneRecipe.getInput(), correctOutput);
            if (!uniques.contains(recipeId)) {
                uniques.add(recipeId);
                grindStoneRecipe.setOutput(correctOutput);
                if (grindStoneRecipe.getOptionalOutput() != null)
                    grindStoneRecipe.setOptionalOutput(resourceHandler.getMainItemStack(grindStoneRecipe.getOptionalOutput()), grindStoneRecipe.getOptionalChance());
                if (grindStoneRecipe.getSecondOptionalOutput() != null)
                    grindStoneRecipe.setSecondOptionalOutput(resourceHandler.getMainItemStack(grindStoneRecipe.getSecondOptionalOutput()), grindStoneRecipe.getSecondOptionalChance());
            } else grindStoneRecipeIterator.remove();
        }
    }
}