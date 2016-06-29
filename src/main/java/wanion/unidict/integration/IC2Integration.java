package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import wanion.unidict.helper.LogHelper;

import java.util.Map;

final class IC2Integration extends AbstractIntegrationThread
{
    IC2Integration()
    {
        super("Industrial Craft 2");
    }

    @Override
    public String call()
    {
        try {
            fixMachinesOutputs(Recipes.centrifuge.getRecipes());
            fixMachinesOutputs(Recipes.metalformerRolling.getRecipes());
            fixMachinesOutputs(Recipes.blastfurance.getRecipes());
            fixMachinesOutputs(Recipes.compressor.getRecipes());
            fixMachinesOutputs(Recipes.macerator.getRecipes());
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The world appears to be entirely industrialized.";
    }

    private void fixMachinesOutputs(Map<IRecipeInput, RecipeOutput> recipes)
    {
        for (Map.Entry<IRecipeInput, RecipeOutput> recipe : recipes.entrySet())
            recipe.setValue(new RecipeOutput(recipe.getValue().metadata, resourceHandler.getMainItemStackList(recipe.getValue().items)));
    }
}