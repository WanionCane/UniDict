package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.MachineRecipe;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.helper.LogHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
final class MekanismIntegration extends AbstractIntegrationThread
{
    MekanismIntegration()
    {
        super("Mekanism");
    }

    @Override
    public String call()
    {
        try {
            fixMekanismRecipes(RecipeHandler.Recipe.CRUSHER.get());
            fixMekanismRecipes(RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get());
            fixInfusionMekanismRecipes(RecipeHandler.Recipe.METALLURGIC_INFUSER.get());
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "All the mekanisms were checked.";
    }

    private void fixMekanismRecipes(Map<ItemStackInput, MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> recipes)
    {
        int initialSize = recipes.size();
        Map<ItemStackInput, MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> correctRecipes = new HashMap<>(initialSize, 1);
        TIntSet uniques = new TIntHashSet(initialSize, 1);
        for (Iterator<MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
        {
            MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> mekanismRecipe = mekanismRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(mekanismRecipe.recipeOutput.output);
            if (correctOutput == mekanismRecipe.recipeOutput.output)
                continue;
            MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> newRecipe = mekanismRecipe.copy();
            newRecipe.recipeOutput.output = correctOutput;
            if (Config.keepOneEntry)
                newRecipe.recipeInput.ingredient = resourceHandler.getMainItemStack(newRecipe.recipeInput.ingredient);
            int recipeID = MetaItem.getCumulativeKey(newRecipe.recipeOutput.output, newRecipe.recipeInput.ingredient);
            if (!uniques.contains(recipeID)) {
                correctRecipes.put(newRecipe.recipeInput, newRecipe);
                uniques.add(recipeID);
            }
            mekanismRecipeIterator.remove();
        }
        recipes.putAll(correctRecipes);
    }

    private void fixInfusionMekanismRecipes(Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> recipes)
    {
        int initialSize = recipes.size();
        Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> correctRecipes = new HashMap<>(initialSize, 1);
        TIntSet uniques = new TIntHashSet(initialSize, 1);
        for (Iterator<MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
        {
            MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> infusionRecipe = infusionRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(infusionRecipe.recipeOutput.output);
            if (correctOutput == infusionRecipe.recipeOutput.output)
                continue;
            MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> newRecipe = infusionRecipe.copy();
            newRecipe.recipeOutput.output = correctOutput;
            if (Config.keepOneEntry)
                newRecipe.recipeInput.inputStack = resourceHandler.getMainItemStack(newRecipe.recipeInput.inputStack);
            Integer recipeID = MetaItem.getCumulativeKey(newRecipe.recipeOutput.output, newRecipe.recipeInput.inputStack);
            if (!uniques.contains(recipeID)) {
                correctRecipes.put(newRecipe.recipeInput, newRecipe);
                uniques.add(recipeID);
            }
            infusionRecipeIterator.remove();
        }
        recipes.putAll(correctRecipes);
    }
}