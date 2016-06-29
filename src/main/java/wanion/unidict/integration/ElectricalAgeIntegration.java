package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import mods.eln.Eln;
import mods.eln.misc.Recipe;
import mods.eln.misc.RecipesList;
import wanion.unidict.helper.LogHelper;

final class ElectricalAgeIntegration extends AbstractIntegrationThread
{
    ElectricalAgeIntegration()
    {
        super("Electrical Age");
    }

    @Override
    public String call()
    {
        Eln elnInstance = Eln.instance;
        try {
            fixElectricalAgeRecipes(elnInstance.maceratorRecipes);
            fixElectricalAgeRecipes(elnInstance.plateMachineRecipes);
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "Where did all this electricity come from?";
    }

    private void fixElectricalAgeRecipes(RecipesList recipes)
    {
        for (Recipe recipe : recipes.getRecipes()) {
            recipe.input = resourceHandler.getMainItemStack(recipe.input);
            resourceHandler.setMainItemStacks(recipe.output);
        }
    }
}