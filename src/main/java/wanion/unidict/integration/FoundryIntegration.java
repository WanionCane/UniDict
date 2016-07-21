package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import exter.foundry.api.recipe.IAlloyFurnaceRecipe;
import exter.foundry.api.recipe.IAtomizerRecipe;
import exter.foundry.api.recipe.ICastingRecipe;
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
        for (final Iterator<IAlloyFurnaceRecipe> alloyFurnaceRecipeIterator = alloyFurnaceRecipes.iterator(); alloyFurnaceRecipeIterator.hasNext(); )
        {
            final IAlloyFurnaceRecipe atomizerRecipe = alloyFurnaceRecipeIterator.next();
            final ItemStack correctOutput = resourceHandler.getMainItemStack(atomizerRecipe.GetOutput());
            if (correctOutput == atomizerRecipe.GetOutput())
                continue;
            newRecipes.add(new AlloyFurnaceRecipe(correctOutput, atomizerRecipe.GetInputA(), atomizerRecipe.GetInputB()));
            alloyFurnaceRecipeIterator.remove();
        }
        alloyFurnaceRecipes.addAll(newRecipes);
    }

    private void fixAtomizerRecipes()
    {
        final List<IAtomizerRecipe> atomizerRecipes = AtomizerRecipeManager.instance.recipes;
        final List<IAtomizerRecipe> newRecipes = new ArrayList<>();
        for (final Iterator<IAtomizerRecipe> atomizerRecipeIterator = atomizerRecipes.iterator(); atomizerRecipeIterator.hasNext(); )
        {
            final IAtomizerRecipe atomizerRecipe = atomizerRecipeIterator.next();
            final Object output = atomizerRecipe.GetOutput();
            if (output instanceof ItemStack) {
                ItemStack correctOutput = resourceHandler.getMainItemStack((ItemStack) output);
                if (correctOutput == output)
                    continue;
                newRecipes.add(new AtomizerRecipe(correctOutput, atomizerRecipe.GetInputFluid()));
                atomizerRecipeIterator.remove();
            }
        }
        atomizerRecipes.addAll(newRecipes);
    }

    private void fixCastingRecipes()
    {
        final List<ICastingRecipe> castingRecipes = CastingRecipeManager.instance.recipes;
        final List<CastingRecipe> newRecipes = new ArrayList<>();
        for (final Iterator<ICastingRecipe> castingRecipeIterator = castingRecipes.iterator(); castingRecipeIterator.hasNext(); )
        {
            final ICastingRecipe castingRecipe = castingRecipeIterator.next();
            final Object output = castingRecipe.GetOutput();
            if (output instanceof ItemStack) {
                ItemStack correctOutput = resourceHandler.getMainItemStack((ItemStack) output);
                if (correctOutput == output)
                    continue;
                newRecipes.add(new CastingRecipe(correctOutput, castingRecipe.GetInputFluid(), castingRecipe.GetInputMold(), null, castingRecipe.GetCastingSpeed()));
                castingRecipeIterator.remove();
            }
        }
        castingRecipes.addAll(newRecipes);
    }
}