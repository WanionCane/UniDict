package wanion.unidict.integration;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
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
    @SuppressWarnings("unchecked")
    public String call()
    {
        try {
            FixCrusherRecipes(RecipeHandler.Recipe.CRUSHER.get());
            FixEnrichmentRecipes(RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get());
            FixChemicalInjectionRecipes(RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER.get());
            FixInfusionRecipes(RecipeHandler.Recipe.METALLURGIC_INFUSER.get());
        } catch (Exception e) { logger.error(threadName + e); }
        return threadName + "All the mekanisms were checked.";
    }

    private void FixCrusherRecipes(@Nonnull final Map<ItemStackInput, CrusherRecipe> recipes)
    {
        final int initialSize = recipes.size();
        final Map<ItemStackInput, CrusherRecipe> correctRecipes = new HashMap<>(initialSize, 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<CrusherRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
                final CrusherRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final CrusherRecipe correctRecipe = mekanismRecipe.copy();
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
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<CrusherRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
                final CrusherRecipe mekanismRecipe = mekanismRecipeIterator.next();
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
                    final CrusherRecipe correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }

    private void FixEnrichmentRecipes(@Nonnull final HashMap<ItemStackInput, EnrichmentRecipe> recipes)
    {
        final int initialSize = recipes.size();
        final Map<ItemStackInput, EnrichmentRecipe> correctRecipes = new HashMap<>(initialSize, 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<EnrichmentRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
                final EnrichmentRecipe mekanismRecipe = mekanismRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
                final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    continue;
                }
                final EnrichmentRecipe correctRecipe = mekanismRecipe.copy();
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
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<EnrichmentRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
                final EnrichmentRecipe mekanismRecipe = mekanismRecipeIterator.next();
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
                    final EnrichmentRecipe correctRecipe = mekanismRecipe.copy();
                    correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.getCount());
                    correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.getCount());
                    correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
                }
                mekanismRecipeIterator.remove();
            }
        }
        recipes.putAll(correctRecipes);
    }

    private void FixChemicalInjectionRecipes(@Nonnull final Map<AdvancedMachineInput, InjectionRecipe> recipes)
    {
        final int initialSize = recipes.size();
        final Map<AdvancedMachineInput, InjectionRecipe>  correctRecipes = new HashMap<>(initialSize, 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<InjectionRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
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
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<InjectionRecipe> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
            {
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

    private void FixInfusionRecipes(@Nonnull final Map<InfusionInput, MetallurgicInfuserRecipe> recipes)
    {
        final int initialSize = recipes.size();
        final Map<InfusionInput, MetallurgicInfuserRecipe> correctRecipes = new HashMap<>(initialSize, 1);
        if (!config.inputReplacementMekanism) {
            final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
            for (final Iterator<MetallurgicInfuserRecipe> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
            {
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
        } else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            for (final Iterator<MetallurgicInfuserRecipe> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
            {
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