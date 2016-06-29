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
import ic2.core.recipe.BasicMachineRecipeManager;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;

import java.util.List;
import java.util.Map;

final class IC2Integration extends AbstractIntegrationThread
{
    private final List<Map<IRecipeInput, RecipeOutput>> ic2MachinesRecipeList = new FixedSizeList<>(5);

    @SuppressWarnings("unchecked")
    IC2Integration()
    {
        super("Industrial Craft 2");
        try {
            ic2MachinesRecipeList.add(Util.getField(BasicMachineRecipeManager.class, "recipes", Recipes.centrifuge, Map.class));
            ic2MachinesRecipeList.add(Util.getField(BasicMachineRecipeManager.class, "recipes", Recipes.compressor, Map.class));
            ic2MachinesRecipeList.add(Util.getField(BasicMachineRecipeManager.class, "recipes", Recipes.blastfurnace, Map.class));
            ic2MachinesRecipeList.add(Util.getField(BasicMachineRecipeManager.class, "recipes", Recipes.macerator, Map.class));
            ic2MachinesRecipeList.add(Util.getField(BasicMachineRecipeManager.class, "recipes", Recipes.metalformerRolling, Map.class));
        } catch (Exception e) { LogHelper.error(e); }
    }

    @Override
    public String call()
    {
        try {
            for (Map<IRecipeInput, RecipeOutput> recipes : ic2MachinesRecipeList)
                fixMachinesOutputs(recipes);
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