package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.cout970.magneticraft.api.access.MgRecipeRegister;
import com.cout970.magneticraft.api.access.RecipeCrusher;
import com.cout970.magneticraft.api.access.RecipeGrinder;
import com.cout970.magneticraft.api.access.RecipeSifter;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.helper.LogHelper;

import java.util.List;

@SuppressWarnings("unchecked")
final class MagneticraftIntegration extends AbstractIntegrationThread
{
    MagneticraftIntegration()
    {
        super("Magneticraft");
    }

    @Override
    public String call()
    {
        try {
            fixCrusherRecipes();
            fixGrinderRecipes();
            fixSifterRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The world's magnetic poles appear to be inverting.";
    }

    private void fixCrusherRecipes()
    {
        List<RecipeCrusher> crusherRecipeList = MgRecipeRegister.crusher;
        List<RecipeCrusher> newRecipes = new FixedSizeList<>(crusherRecipeList.size());
        for (RecipeCrusher crusherRecipe : crusherRecipeList)
            newRecipes.add(new RecipeCrusher(resourceHandler.getMainItemStack(crusherRecipe.getInput()), resourceHandler.getMainItemStack(crusherRecipe.getOutput()), resourceHandler.getMainItemStack(crusherRecipe.getOutput2()), crusherRecipe.getProb2(), resourceHandler.getMainItemStack(crusherRecipe.getOutput3()), crusherRecipe.getProb3()));
        crusherRecipeList.clear();
        crusherRecipeList.addAll(newRecipes);
    }

    private void fixGrinderRecipes()
    {
        List<RecipeGrinder> grinderRecipeList = MgRecipeRegister.grinder;
        List<RecipeGrinder> newRecipes = new FixedSizeList<>(grinderRecipeList.size());
        for (RecipeGrinder grinderRecipe : grinderRecipeList)
            newRecipes.add(new RecipeGrinder(resourceHandler.getMainItemStack(grinderRecipe.getInput()), resourceHandler.getMainItemStack(grinderRecipe.getOutput()), resourceHandler.getMainItemStack(grinderRecipe.getOutput2()), grinderRecipe.getProb2(), resourceHandler.getMainItemStack(grinderRecipe.getOutput3()), grinderRecipe.getProb3()));
        grinderRecipeList.clear();
        grinderRecipeList.addAll(newRecipes);
    }

    private void fixSifterRecipes()
    {
        List<RecipeSifter> sifterRecipeList = MgRecipeRegister.sifter;
        List<RecipeSifter> newRecipes = new FixedSizeList<>(sifterRecipeList.size());
        for (RecipeSifter sifterRecipe : sifterRecipeList)
            newRecipes.add(new RecipeSifter(resourceHandler.getMainItemStack(sifterRecipe.getInput()), resourceHandler.getMainItemStack(sifterRecipe.getOutput()), resourceHandler.getMainItemStack(sifterRecipe.getExtra()), sifterRecipe.getProb()));
        sifterRecipeList.clear();
        sifterRecipeList.addAll(newRecipes);
    }
}