package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import exter.foundry.api.recipe.IAlloyFurnaceRecipe;
import exter.foundry.api.recipe.IAtomizerRecipe;
import exter.foundry.api.recipe.ICastingRecipe;
import exter.foundry.api.recipe.matcher.ItemStackMatcher;
import exter.foundry.recipes.AlloyFurnaceRecipe;
import exter.foundry.recipes.AtomizerRecipe;
import exter.foundry.recipes.CastingRecipe;
import exter.foundry.recipes.manager.AlloyFurnaceRecipeManager;
import exter.foundry.recipes.manager.AtomizerRecipeManager;
import exter.foundry.recipes.manager.CastingRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class FoundryIntegration extends AbstractIntegrationThread
{
    FoundryIntegration()
    {
        super("Foundry");
    }

    @Override
    public String call()
    {
        try {
            fixAlloyFurnaceRecipes();
            fixAtomizerRecipes();
            fixCastingRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "Somethings that are made in casts had to change.";
    }

    private void fixAlloyFurnaceRecipes()
    {
        final List<IAlloyFurnaceRecipe> alloyFurnaceRecipes = AlloyFurnaceRecipeManager.instance.recipes;
        final List<IAlloyFurnaceRecipe> newRecipes = new ArrayList<>();
        for (Iterator<IAlloyFurnaceRecipe> alloyFurnaceRecipeIterator = alloyFurnaceRecipes.iterator(); alloyFurnaceRecipeIterator.hasNext(); )
        {
            IAlloyFurnaceRecipe atomizerRecipe = alloyFurnaceRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(atomizerRecipe.getOutput());
            if (correctOutput == atomizerRecipe.getOutput())
                continue;
            newRecipes.add(new AlloyFurnaceRecipe(correctOutput, atomizerRecipe.getInputA(), atomizerRecipe.getInputB()));
            alloyFurnaceRecipeIterator.remove();
        }
        alloyFurnaceRecipes.addAll(newRecipes);
    }

    private void fixAtomizerRecipes()
    {
        final List<IAtomizerRecipe> atomizerRecipes = AtomizerRecipeManager.instance.recipes;
        final List<IAtomizerRecipe> newRecipes = new ArrayList<>();
        for (Iterator<IAtomizerRecipe> atomizerRecipeIterator = atomizerRecipes.iterator(); atomizerRecipeIterator.hasNext(); )
        {
            IAtomizerRecipe atomizerRecipe = atomizerRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(atomizerRecipe.getOutput());
            if (correctOutput == atomizerRecipe.getOutput())
                continue;
            newRecipes.add(new AtomizerRecipe(new ItemStackMatcher(correctOutput), atomizerRecipe.getInput()));
            atomizerRecipeIterator.remove();
        }
        atomizerRecipes.addAll(newRecipes);
    }

    private void fixCastingRecipes()
    {
        final List<ICastingRecipe> castingRecipes = CastingRecipeManager.instance.recipes;
        final List<CastingRecipe> newRecipes = new ArrayList<>();
        for (Iterator<ICastingRecipe> castingRecipeIterator = castingRecipes.iterator(); castingRecipeIterator.hasNext(); )
        {
            ICastingRecipe castingRecipe = castingRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(castingRecipe.getOutput());
            if (correctOutput == castingRecipe.getOutput())
                continue;
            newRecipes.add(new CastingRecipe(new ItemStackMatcher(correctOutput), castingRecipe.getInput(), castingRecipe.getMold(), castingRecipe.getInputExtra(), castingRecipe.getCastingSpeed()));
            castingRecipeIterator.remove();
        }
        castingRecipes.addAll(newRecipes);
    }
}