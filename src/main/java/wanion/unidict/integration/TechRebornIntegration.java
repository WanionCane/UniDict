package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import reborncore.api.recipe.RecipeHandler;
import wanion.unidict.UniDict;

final class TechRebornIntegration extends AbstractIntegrationThread
{
    TechRebornIntegration()
    {
        super("TechReborn");
    }

    @Override
    public String call()
    {
        try {
            fixTechRebornRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "now Tech is truly Reborn.";
    }

    private void fixTechRebornRecipes()
    {
        RecipeHandler.recipeList.forEach(recipe -> {
                if (!recipe.useOreDic())
                    resourceHandler.setMainItemStacks(recipe.getInputs());
                resourceHandler.setMainItemStacks(recipe.getOutputs());
        });
    }
}