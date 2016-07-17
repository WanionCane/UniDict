package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import wanion.unidict.UniDict;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class MekanismIntegration extends AbstractIntegrationThread
{
    MekanismIntegration()
    {
        super("Mekanism");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String call()
    {
        try {
            fixMekanismRecipes(RecipeHandler.Recipe.CRUSHER.get());
            fixMekanismRecipes(RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get());
            fixInfusionMekanismRecipes(RecipeHandler.Recipe.METALLURGIC_INFUSER.get());
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "All the mekanisms were checked.";
    }

    private void fixMekanismRecipes(@Nonnull final Map<ItemStackInput, MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> recipes)
    {
        final int initialSize = recipes.size();
        final Map<ItemStackInput, MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> correctRecipes = new HashMap<>(initialSize, 1);
        final TIntSet uniques = new TIntHashSet(initialSize, 1);
        for (Iterator<MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
        {
            final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> mekanismRecipe = mekanismRecipeIterator.next();
            final ItemStack correctOutput = resourceHandler.getMainItemStack(mekanismRecipe.recipeOutput.output);
            if (correctOutput == mekanismRecipe.recipeOutput.output)
                continue;
            final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> newRecipe = mekanismRecipe.copy();
            newRecipe.recipeOutput.output = correctOutput;
            if (Config.keepOneEntry)
                newRecipe.recipeInput.ingredient = resourceHandler.getMainItemStack(newRecipe.recipeInput.ingredient);
            final int recipeID = MetaItem.getCumulative(newRecipe.recipeOutput.output, newRecipe.recipeInput.ingredient);
            if (!uniques.contains(recipeID)) {
                correctRecipes.put(newRecipe.recipeInput, newRecipe);
                uniques.add(recipeID);
            }
            mekanismRecipeIterator.remove();
        }
        recipes.putAll(correctRecipes);
    }

    private void fixInfusionMekanismRecipes(@Nonnull final Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> recipes)
    {
        final int initialSize = recipes.size();
        final Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> correctRecipes = new HashMap<>(initialSize, 1);
        final TIntSet uniques = new TIntHashSet(initialSize, 1);
        for (Iterator<MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
        {
            final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> infusionRecipe = infusionRecipeIterator.next();
            final ItemStack correctOutput = resourceHandler.getMainItemStack(infusionRecipe.recipeOutput.output);
            if (correctOutput == infusionRecipe.recipeOutput.output)
                continue;
            final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> newRecipe = infusionRecipe.copy();
            newRecipe.recipeOutput.output = correctOutput;
            if (Config.keepOneEntry)
                newRecipe.recipeInput.inputStack = resourceHandler.getMainItemStack(newRecipe.recipeInput.inputStack);
            final int recipeID = MetaItem.getCumulative(newRecipe.recipeOutput.output, newRecipe.recipeInput.inputStack);
            if (!uniques.contains(recipeID)) {
                correctRecipes.put(newRecipe.recipeInput, newRecipe);
                uniques.add(recipeID);
            }
            infusionRecipeIterator.remove();
        }
        recipes.putAll(correctRecipes);
    }
}