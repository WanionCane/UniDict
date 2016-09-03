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
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
        if (!(Config.keepOneEntry || Config.inputReplacement)) {
            final TIntSet uniques = new TIntHashSet(initialSize, 1);
            for (final Iterator<MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
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
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
                final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer != null && inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
                    continue;
                } else if (outputContainer == null)
                    continue;
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> newRecipe = mekanismRecipe.copy();
                    newRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
                    if (Config.keepOneEntry || Config.inputReplacement)
                        newRecipe.recipeInput.ingredient = inputContainer.getMainEntry(newRecipe.recipeInput.ingredient.stackSize);
                    correctRecipes.put(newRecipe.recipeInput, newRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }

    private void fixInfusionMekanismRecipes(@Nonnull final Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> recipes)
    {
        final int initialSize = recipes.size();
        final Map<InfusionInput, MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> correctRecipes = new HashMap<>(initialSize, 1);
        if (!Config.inputReplacement) {
            final TIntSet uniques = new TIntHashSet(initialSize, 1);

            for (final Iterator<MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
            {
                final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> infusionRecipe = infusionRecipeIterator.next();
                final ItemStack correctOutput = resourceHandler.getMainItemStack(infusionRecipe.recipeOutput.output);
                if (correctOutput == infusionRecipe.recipeOutput.output)
                    continue;
                final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> newRecipe = infusionRecipe.copy();
                if (Config.keepOneEntry)
                    newRecipe.recipeInput.inputStack = resourceHandler.getMainItemStack(newRecipe.recipeInput.inputStack);
                newRecipe.recipeOutput.output = correctOutput;
                final int recipeID = MetaItem.getCumulative(newRecipe.recipeOutput.output, newRecipe.recipeInput.inputStack);
                if (!uniques.contains(recipeID)) {
                    correctRecipes.put(newRecipe.recipeInput, newRecipe);
                    uniques.add(recipeID);
                }
                infusionRecipeIterator.remove();
            }
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
            {
                final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> mekanismRecipe = infusionRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.inputStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer != null && inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
                    continue;
                } else if (outputContainer == null)
                    continue;
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> newRecipe = mekanismRecipe.copy();
                    newRecipe.recipeInput.inputStack = inputContainer.getMainEntry(newRecipe.recipeInput.inputStack.stackSize);
                    newRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
                    correctRecipes.put(newRecipe.recipeInput, newRecipe);
                }
                infusionRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }
}