package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.*;
import mekanism.common.recipe.machines.CrystallizerRecipe;
import mekanism.common.recipe.machines.MachineRecipe;
import mekanism.common.recipe.machines.PressurizedRecipe;
import mekanism.common.recipe.machines.SawmillRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import mekanism.common.recipe.outputs.PressurizedOutput;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class MekanismIntegration extends AbstractIntegrationThread {
    MekanismIntegration()
    {
        super("Mekanism");
    }

    @Override
    public String call() {
        try {
            fixMachineRecipes(RecipeHandler.Recipe.ENERGIZED_SMELTER.get());
            fixMachineRecipes(RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get());
            fixMachineRecipes(RecipeHandler.Recipe.CRUSHER.get());
            fixMachineRecipes(RecipeHandler.Recipe.COMBINER.get());
            fixMachineRecipes(RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER.get());
            fixMachineRecipes(RecipeHandler.Recipe.METALLURGIC_INFUSER.get());
            fixSawmillRecipes(RecipeHandler.Recipe.PRECISION_SAWMILL.get());
            fixCrystallizerRecipes(RecipeHandler.Recipe.CHEMICAL_CRYSTALLIZER.get());
            fixPRCRecipes(RecipeHandler.Recipe.PRESSURIZED_REACTION_CHAMBER.get());
        } catch (Exception e) {
            logger.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "All the mekanisms were checked.";
    }

    private <I extends MachineInput<I>, R extends MachineRecipe<I, ? extends ItemStackOutput, R>> void fixMachineRecipes(@Nonnull final Map<I, R> recipes) {
        final Map<I, R> correctRecipes = new HashMap<>(recipes.size(), 1);
        final Map<UniResourceContainer, TIntSet> containerMap = new IdentityHashMap<>();

        final Iterator<R> mekanismRecipeIterator = recipes.values().iterator();
        while (mekanismRecipeIterator.hasNext()) {
            final R mekanismRecipe = mekanismRecipeIterator.next();
            final ItemStack inputItemStack = getInputStackFromInput(mekanismRecipe.recipeInput);
            if (inputItemStack == null) continue;
            final UniResourceContainer inputContainer = resourceHandler.getContainer(inputItemStack);
            final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
            if (outputContainer == null)
                continue;
            if (inputContainer == null) {
                mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                continue;
            }
            final R correctRecipe = mekanismRecipe.copy();

            final ItemStack inputStack = config.keepOneEntry ? inputContainer.getMainEntry(inputItemStack.getCount()) : inputItemStack.copy();
            if (!config.inputReplacementMekanism && config.keepOneEntry)
                setInputStack(correctRecipe.recipeInput, inputStack);

            final int inputId = config.inputReplacementMekanism ? inputContainer.kind : MetaItem.get(inputStack);

            containerMap.putIfAbsent(outputContainer, new TIntHashSet());
            final TIntSet inputKeySet = containerMap.get(outputContainer);
            if (!inputKeySet.contains(inputId)) {
                inputKeySet.add(inputId);
                if (config.inputReplacementMekanism) {
                    setInputStack(correctRecipe.recipeInput, inputContainer.getMainEntry(inputItemStack.getCount()));
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                }
                else
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.getCount());
                correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
            }

            mekanismRecipeIterator.remove();
        }

        recipes.putAll(correctRecipes);
    }

    private <I extends MachineInput<I>> ItemStack getInputStackFromInput(I input) {
        if (input instanceof ItemStackInput) {
            return ((ItemStackInput)input).ingredient;
        }
        else if (input instanceof DoubleMachineInput) {
            return ((DoubleMachineInput)input).itemStack;
        }
        else if (input instanceof AdvancedMachineInput) {
            return ((AdvancedMachineInput)input).itemStack;
        }
        else if (input instanceof InfusionInput) {
            return ((InfusionInput)input).inputStack;
        }
        return null;
    }

    private <I extends MachineInput<I>> void setInputStack(I input, ItemStack itemStack) {
        if (input instanceof ItemStackInput) {
            ((ItemStackInput)input).ingredient = itemStack;
        }
        else if (input instanceof DoubleMachineInput) {
            ((DoubleMachineInput)input).itemStack = itemStack;
        }
        else if (input instanceof AdvancedMachineInput) {
           ((AdvancedMachineInput)input).itemStack = itemStack;
        }
        else if (input instanceof InfusionInput) {
            ((InfusionInput)input).inputStack = itemStack;
        }
    }

    private void fixSawmillRecipes(@Nonnull final Map<ItemStackInput, SawmillRecipe> recipes) {
        final Map<ItemStackInput, SawmillRecipe> correctRecipes = new HashMap<>(recipes.size(), 1);
        final Map<SawMillRecipeKey, TIntSet> containerMap = new HashMap<>();

        final Iterator<SawmillRecipe> mekanismRecipeIterator = recipes.values().iterator();
        while (mekanismRecipeIterator.hasNext()) {
            final SawmillRecipe mekanismRecipe = mekanismRecipeIterator.next();
            final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
            final UniResourceContainer primaryOutputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.primaryOutput);
            final UniResourceContainer secondaryOutputContainer =
                    resourceHandler.getContainer(mekanismRecipe.recipeOutput.secondaryOutput);
            if (primaryOutputContainer == null) {
                if (secondaryOutputContainer != null)
                    mekanismRecipe.recipeOutput.secondaryOutput = secondaryOutputContainer.getMainEntry(mekanismRecipe.recipeOutput.secondaryOutput.getCount());
                continue;
            }
            if (inputContainer == null) {
                mekanismRecipe.recipeOutput.primaryOutput = primaryOutputContainer.getMainEntry(mekanismRecipe.recipeOutput.primaryOutput.getCount());
                if (secondaryOutputContainer != null)
                    mekanismRecipe.recipeOutput.secondaryOutput = secondaryOutputContainer.getMainEntry(mekanismRecipe.recipeOutput.secondaryOutput.getCount());
                continue;
            }
            final SawmillRecipe correctRecipe = mekanismRecipe.copy();

            final ItemStack inputStack = config.keepOneEntry ? inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount()) : correctRecipe.recipeInput.ingredient.copy();
            if (!config.inputReplacementMekanism && config.keepOneEntry)
                correctRecipe.recipeInput.ingredient = inputStack;

            final int inputId = config.inputReplacementMekanism ? inputContainer.kind : MetaItem.get(inputStack);
            SawMillRecipeKey key = new SawMillRecipeKey(primaryOutputContainer, secondaryOutputContainer);

            containerMap.putIfAbsent(key, new TIntHashSet());
            final TIntSet inputKeySet = containerMap.get(key);

            if (!inputKeySet.contains(inputId)) {
                inputKeySet.add(inputId);
                if (config.inputReplacementMekanism){
                    correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount());
                    correctRecipe.recipeOutput.primaryOutput = primaryOutputContainer.getMainEntry(mekanismRecipe.recipeOutput.primaryOutput.getCount());
                    if (secondaryOutputContainer != null)
                        correctRecipe.recipeOutput.secondaryOutput = secondaryOutputContainer.getMainEntry(mekanismRecipe.recipeOutput.secondaryOutput.getCount());
                }
                else {
                    correctRecipe.recipeOutput.primaryOutput = primaryOutputContainer.getMainEntry(correctRecipe.recipeOutput.primaryOutput.getCount());
                    if (secondaryOutputContainer != null)
                        correctRecipe.recipeOutput.secondaryOutput = secondaryOutputContainer.getMainEntry(correctRecipe.recipeOutput.secondaryOutput.getCount());
                }

                correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
            }

            mekanismRecipeIterator.remove();
        }

        recipes.putAll(correctRecipes);
    }

    private void fixCrystallizerRecipes(HashMap<GasInput, CrystallizerRecipe> recipes) {
        final Map<GasInput, CrystallizerRecipe> correctRecipes = new HashMap<>(recipes.size(), 1);

        final Iterator<CrystallizerRecipe> recipeIterator = recipes.values().iterator();
        while (recipeIterator.hasNext()) {
            final CrystallizerRecipe recipe = recipeIterator.next();
            final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.recipeOutput.output);
            if (outputContainer == null)
                continue;
            final CrystallizerRecipe correctRecipe = recipe.copy();
            correctRecipe.recipeOutput.output = outputContainer.getMainEntry(recipe.recipeOutput.output.getCount());
            correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
            recipeIterator.remove();
        }

        recipes.putAll(correctRecipes);
    }

    private void fixPRCRecipes(HashMap<PressurizedInput, PressurizedRecipe> recipes) {
        final Map<PressurizedInput, PressurizedRecipe> correctRecipes = new HashMap<>(recipes.size(), 1);

        final Iterator<PressurizedRecipe> recipeIterator = recipes.values().iterator();
        while (recipeIterator.hasNext()) {
            final PressurizedRecipe recipe = recipeIterator.next();
            final ItemStack inputStack = recipe.recipeOutput.getItemOutput();
            final UniResourceContainer outputContainer = resourceHandler.getContainer(inputStack);
            if (outputContainer == null)
                continue;
            final PressurizedRecipe correctRecipe = recipe.copy();
            correctRecipe.recipeOutput = new PressurizedOutput(outputContainer.getMainEntry(inputStack.getCount()), recipe.recipeOutput.getGasOutput());
            correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
            recipeIterator.remove();
        }

        recipes.putAll(correctRecipes);
    }

    private static class SawMillRecipeKey {
        private final UniResourceContainer primary;
        private final UniResourceContainer secondary;

        public SawMillRecipeKey(UniResourceContainer primary, UniResourceContainer secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SawMillRecipeKey) {
                return primary.equals(((SawMillRecipeKey)obj).primary) &&
                        secondary.equals(((SawMillRecipeKey)obj).secondary);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(primary) + 31 * System.identityHashCode(secondary);
        }

        @Override
        public String toString() {
            return "(" + primary + ";" + secondary + ")";
        }
    }
}
