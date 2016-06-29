package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import wanion.unidict.helper.LogHelper;

final class TechRebornIntegration extends AbstractIntegrationThread
{
    TechRebornIntegration()
    {
        super("TechReborn");
    }

    @Override
    public String call()
    {
        fixTechRebornRecipes();
        return threadName + "now Tech is truly Reborn.";
    }

    private void fixTechRebornRecipes()
    {
        for (IBaseRecipeType recipe : RecipeHandler.recipeList) {
            try {
                if (!recipe.useOreDic())
                    resourceHandler.setMainItemStacks(recipe.getInputs());
                resourceHandler.setMainItemStacks(recipe.getOutputs());
            } catch (Exception e) { LogHelper.error(e); }
        }
    }
}
