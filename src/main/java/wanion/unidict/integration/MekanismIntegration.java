package wanion.unidict.integration;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.inputs.DoubleMachineInput;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.*;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
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
    public String call() {
        try {
            FixBasicRecipes(RecipeHandler.Recipe.CRUSHER.get());
            FixBasicRecipes(RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get());
            FixBasicRecipes(RecipeHandler.Recipe.ENERGIZED_SMELTER.get());
            FixCombinerRecipes(RecipeHandler.Recipe.COMBINER.get());
            FixChemicalInjectionRecipes(RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER.get());
            FixInfusionRecipes(RecipeHandler.Recipe.METALLURGIC_INFUSER.get());
        } catch (Exception e) { logger.error(threadName + e); }
        return threadName + "All the mekanisms were checked.";
    }

    private <T extends BasicMachineRecipe<T>> void FixBasicRecipes(@Nonnull final Map<ItemStackInput, T> recipes) {
        final Map<ItemStackInput, T> correctRecipes = new HashMap<>(recipes.size(), 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<T> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ) {
                final T mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final T correctRecipe = mekanismRecipe.copy();
                final ItemStack inputStack;
                if (config.keepOneEntry)
                    inputStack = correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount());
                else
                    inputStack = correctRecipe.recipeInput.ingredient = correctRecipe.recipeInput.ingredient.copy();
                final int inputId = MetaItem.get(inputStack);
                if (!containerInputKeyMap.containsKey(outputContainer))
                    containerInputKeyMap.put(outputContainer, new TIntHashSet());
                final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
                if (!inputKeySet.contains(inputId)) {
                    inputKeySet.add(inputId);
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<T> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ) {
                final T mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final T correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }

    private void FixCombinerRecipes(@Nonnull final Map<DoubleMachineInput, CombinerRecipe> recipes) {
        final Map<DoubleMachineInput, CombinerRecipe> correctRecipes = new HashMap<>(recipes.size(), 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<CombinerRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ){
                final CombinerRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.itemStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final CombinerRecipe correctRecipe = mekanismRecipe.copy();
                final ItemStack inputStack;
                if (config.keepOneEntry)
                    inputStack = correctRecipe.recipeInput.itemStack = inputContainer.getMainEntry(correctRecipe.recipeInput.itemStack.getCount());
                else
                    inputStack = correctRecipe.recipeInput.itemStack = correctRecipe.recipeInput.itemStack.copy();
                final int inputId = MetaItem.get(inputStack);
                if (!containerInputKeyMap.containsKey(outputContainer))
                    containerInputKeyMap.put(outputContainer, new TIntHashSet());
                final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
                if (!inputKeySet.contains(inputId)) {
                    inputKeySet.add(inputId);
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<CombinerRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ) {
                final CombinerRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.itemStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final CombinerRecipe correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.itemStack = inputContainer.getMainEntry(correctRecipe.recipeInput.itemStack.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }

        recipes.putAll(correctRecipes);
    }

    private void FixChemicalInjectionRecipes(@Nonnull final Map<AdvancedMachineInput, InjectionRecipe> recipes) {
        final Map<AdvancedMachineInput, InjectionRecipe>  correctRecipes = new HashMap<>(recipes.size(), 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<InjectionRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ) {
                final InjectionRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.itemStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final InjectionRecipe correctRecipe = mekanismRecipe.copy();
                final ItemStack inputStack;
                if (config.keepOneEntry)
                    inputStack = correctRecipe.recipeInput.itemStack = inputContainer.getMainEntry(correctRecipe.recipeInput.itemStack.getCount());
                else
                    inputStack = correctRecipe.recipeInput.itemStack = correctRecipe.recipeInput.itemStack.copy();
                final int inputId = MetaItem.get(inputStack);
                if (!containerInputKeyMap.containsKey(outputContainer))
                    containerInputKeyMap.put(outputContainer, new TIntHashSet());
                final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
                if (!inputKeySet.contains(inputId)) {
                    inputKeySet.add(inputId);
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<InjectionRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); ) {
                final InjectionRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.itemStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final InjectionRecipe correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.itemStack = inputContainer.getMainEntry(correctRecipe.recipeInput.itemStack.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }

    private void FixInfusionRecipes(@Nonnull final Map<InfusionInput, MetallurgicInfuserRecipe> recipes) {
        final Map<InfusionInput, MetallurgicInfuserRecipe> correctRecipes = new HashMap<>(recipes.size(), 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<MetallurgicInfuserRecipe> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); ) {
                final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> infusionRecipe = infusionRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(infusionRecipe.recipeInput.inputStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(infusionRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    infusionRecipe.recipeOutput.output = outputContainer.getMainEntry(infusionRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final MetallurgicInfuserRecipe correctRecipe = infusionRecipe.copy();
                final ItemStack inputStack;
                if (config.keepOneEntry)
                    inputStack = correctRecipe.recipeInput.inputStack = inputContainer.getMainEntry(correctRecipe.recipeInput.inputStack.getCount());
                else
                    inputStack = correctRecipe.recipeInput.inputStack = correctRecipe.recipeInput.inputStack.copy();
                final int inputId = MetaItem.get(inputStack);
                if (!containerInputKeyMap.containsKey(outputContainer))
                    containerInputKeyMap.put(outputContainer, new TIntHashSet());
                final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
                if (!inputKeySet.contains(inputId)) {
                    inputKeySet.add(inputId);
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                infusionRecipeIterator.remove();
            }
        }
        else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<MetallurgicInfuserRecipe> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); ) {
                final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> mekanismRecipe = infusionRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.inputStack);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    final MetallurgicInfuserRecipe correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.inputStack = inputContainer.getMainEntry(correctRecipe.recipeInput.inputStack.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                infusionRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }
}