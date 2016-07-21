package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.cout970.magneticraft.api.access.MgRecipeRegister;
import com.cout970.magneticraft.api.access.RecipeCrusher;
import com.cout970.magneticraft.api.access.RecipeGrinder;
import com.cout970.magneticraft.api.access.RecipeSifter;
import wanion.unidict.UniDict;
import wanion.unidict.common.FixedSizeList;

import java.util.List;
import java.util.stream.Collectors;

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
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "The world's magnetic poles appear to be inverting.";
    }

    private void fixCrusherRecipes()
    {
        final List<RecipeCrusher> crusherRecipeList = MgRecipeRegister.crusher;
        final List<RecipeCrusher> newRecipes = new FixedSizeList<>(crusherRecipeList.size());
        newRecipes.addAll(crusherRecipeList.stream().map(crusherRecipe -> new RecipeCrusher(resourceHandler.getMainItemStack(crusherRecipe.getInput()), resourceHandler.getMainItemStack(crusherRecipe.getOutput()), resourceHandler.getMainItemStack(crusherRecipe.getOutput2()), crusherRecipe.getProb2(), resourceHandler.getMainItemStack(crusherRecipe.getOutput3()), crusherRecipe.getProb3())).collect(Collectors.toList()));
        crusherRecipeList.clear();
        crusherRecipeList.addAll(newRecipes);
    }

    private void fixGrinderRecipes()
    {
        final List<RecipeGrinder> grinderRecipeList = MgRecipeRegister.grinder;
        final List<RecipeGrinder> newRecipes = new FixedSizeList<>(grinderRecipeList.size());
        newRecipes.addAll(grinderRecipeList.stream().map(grinderRecipe -> new RecipeGrinder(resourceHandler.getMainItemStack(grinderRecipe.getInput()), resourceHandler.getMainItemStack(grinderRecipe.getOutput()), resourceHandler.getMainItemStack(grinderRecipe.getOutput2()), grinderRecipe.getProb2(), resourceHandler.getMainItemStack(grinderRecipe.getOutput3()), grinderRecipe.getProb3())).collect(Collectors.toList()));
        grinderRecipeList.clear();
        grinderRecipeList.addAll(newRecipes);
    }

    private void fixSifterRecipes()
    {
        final List<RecipeSifter> sifterRecipeList = MgRecipeRegister.sifter;
        final List<RecipeSifter> newRecipes = new FixedSizeList<>(sifterRecipeList.size());
        newRecipes.addAll(sifterRecipeList.stream().map(sifterRecipe -> new RecipeSifter(resourceHandler.getMainItemStack(sifterRecipe.getInput()), resourceHandler.getMainItemStack(sifterRecipe.getOutput()), resourceHandler.getMainItemStack(sifterRecipe.getExtra()), sifterRecipe.getProb())).collect(Collectors.toList()));
        sifterRecipeList.clear();
        sifterRecipeList.addAll(newRecipes);
    }
}