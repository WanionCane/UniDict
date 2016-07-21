package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import mods.eln.Eln;
import mods.eln.misc.Recipe;
import mods.eln.misc.RecipesList;
import wanion.unidict.UniDict;

import javax.annotation.Nonnull;

final class ElectricalAgeIntegration extends AbstractIntegrationThread
{
    ElectricalAgeIntegration()
    {
        super("Electrical Age");
    }

    @Override
    public String call()
    {
        final Eln elnInstance = Eln.instance;
        try {
            fixElectricalAgeRecipes(elnInstance.maceratorRecipes);
            fixElectricalAgeRecipes(elnInstance.plateMachineRecipes);
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "Where did all this electricity come from?";
    }

    private void fixElectricalAgeRecipes(@Nonnull final RecipesList recipes)
    {
        for (final Recipe recipe : recipes.getRecipes()) {
            recipe.input = resourceHandler.getMainItemStack(recipe.input);
            resourceHandler.setMainItemStacks(recipe.output);
        }
    }
}