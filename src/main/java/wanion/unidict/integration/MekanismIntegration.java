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
		if (!config.inputReplacementMekanism) {
			final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
			for (final Iterator<MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe>> mekanismRecipeIterator = recipes.values().iterator(); mekanismRecipeIterator.hasNext(); )
			{
				final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> mekanismRecipe = mekanismRecipeIterator.next();
				final UniResourceContainer inputContainer = resourceHandler.getContainer(mekanismRecipe.recipeInput.ingredient);
				final UniResourceContainer outputContainer = resourceHandler.getContainer(mekanismRecipe.recipeOutput.output);
				if (outputContainer == null)
					continue;
				else if (inputContainer == null) {
					mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
					continue;
				}
				final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> correctRecipe = mekanismRecipe.copy();
				final ItemStack inputStack;
				if (config.keepOneEntry)
					inputStack = correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.stackSize);
				else
					inputStack = correctRecipe.recipeInput.ingredient = correctRecipe.recipeInput.ingredient.copy();
				final int inputId = MetaItem.get(inputStack);
				if (!containerInputKeyMap.containsKey(outputContainer))
					containerInputKeyMap.put(outputContainer, new TIntHashSet());
				final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
				if (!inputKeySet.contains(inputId)) {
					inputKeySet.add(inputId);
					correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.stackSize);
					correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
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
				if (outputContainer == null)
					continue;
				if (inputContainer == null) {
					mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
					continue;
				}
				final int kind = inputContainer.kind;
				if (!containerKindMap.containsKey(outputContainer))
					containerKindMap.put(outputContainer, new TIntHashSet());
				final TIntSet kindSet = containerKindMap.get(outputContainer);
				if (!kindSet.contains(kind)) {
					kindSet.add(kind);
					final MachineRecipe<ItemStackInput, ItemStackOutput, ? extends MachineRecipe> correctRecipe = mekanismRecipe.copy();
					correctRecipe.recipeInput.ingredient = inputContainer.getMainEntry(correctRecipe.recipeInput.ingredient.stackSize);
					correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
					correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
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
		if (!config.inputReplacementMekanism) {
			final Map<UniResourceContainer, TIntSet> containerInputKeyMap = new IdentityHashMap<>();
			for (final Iterator<MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe>> infusionRecipeIterator = recipes.values().iterator(); infusionRecipeIterator.hasNext(); )
			{
				final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> infusionRecipe = infusionRecipeIterator.next();
				final UniResourceContainer inputContainer = resourceHandler.getContainer(infusionRecipe.recipeInput.inputStack);
				final UniResourceContainer outputContainer = resourceHandler.getContainer(infusionRecipe.recipeOutput.output);
				if (outputContainer == null)
					continue;
				else if (inputContainer == null) {
					infusionRecipe.recipeOutput.output = outputContainer.getMainEntry(infusionRecipe.recipeOutput.output.stackSize);
					continue;
				}
				final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> correctRecipe = infusionRecipe.copy();
				final ItemStack inputStack;
				if (config.keepOneEntry)
					inputStack = correctRecipe.recipeInput.inputStack = inputContainer.getMainEntry(correctRecipe.recipeInput.inputStack.stackSize);
				else
					inputStack = correctRecipe.recipeInput.inputStack = correctRecipe.recipeInput.inputStack.copy();
				final int inputId = MetaItem.get(inputStack);
				if (!containerInputKeyMap.containsKey(outputContainer))
					containerInputKeyMap.put(outputContainer, new TIntHashSet());
				final TIntSet inputKeySet = containerInputKeyMap.get(outputContainer);
				if (!inputKeySet.contains(inputId)) {
					inputKeySet.add(inputId);
					correctRecipe.recipeOutput.output = outputContainer.getMainEntry(correctRecipe.recipeOutput.output.stackSize);
					correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
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
				if (outputContainer == null)
					continue;
				if (inputContainer == null) {
					mekanismRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
					continue;
				}
				final int kind = inputContainer.kind;
				if (!containerKindMap.containsKey(outputContainer))
					containerKindMap.put(outputContainer, new TIntHashSet());
				final TIntSet kindSet = containerKindMap.get(outputContainer);
				if (!kindSet.contains(kind)) {
					kindSet.add(kind);
					final MachineRecipe<InfusionInput, ItemStackOutput, MetallurgicInfuserRecipe> correctRecipe = mekanismRecipe.copy();
					correctRecipe.recipeInput.inputStack = inputContainer.getMainEntry(correctRecipe.recipeInput.inputStack.stackSize);
					correctRecipe.recipeOutput.output = outputContainer.getMainEntry(mekanismRecipe.recipeOutput.output.stackSize);
					correctRecipes.put(correctRecipe.recipeInput, correctRecipe);
				}
				infusionRecipeIterator.remove();
			}
		}
		recipes.putAll(correctRecipes);
	}
}