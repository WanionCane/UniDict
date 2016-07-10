package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.Recipes;
import wanion.unidict.UniDict;
import wanion.unidict.common.FixedSizeList;

import java.util.List;

final class IC2Integration extends AbstractIntegrationThread
{
    private final List<Iterable<IMachineRecipeManager.RecipeIoContainer>> ic2MachinesRecipeList = new FixedSizeList<>(5);

    @SuppressWarnings("unchecked")
    IC2Integration()
    {
        super("Industrial Craft 2");
        try {
            ic2MachinesRecipeList.add(Recipes.centrifuge.getRecipes());
            ic2MachinesRecipeList.add(Recipes.compressor.getRecipes());
            ic2MachinesRecipeList.add(Recipes.blastfurnace.getRecipes());
            ic2MachinesRecipeList.add(Recipes.macerator.getRecipes());
            ic2MachinesRecipeList.add(Recipes.metalformerRolling.getRecipes());
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
    }

    @Override
    public String call()
    {
        ic2MachinesRecipeList.forEach(recipeIterable -> {
            try {
                recipeIterable.forEach(recipe -> resourceHandler.getMainItemStackList(recipe.output.items));
            } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        });
        return threadName + "The world appears to be entirely industrialized.";
    }
}