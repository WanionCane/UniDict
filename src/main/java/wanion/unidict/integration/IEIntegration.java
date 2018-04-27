package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.*;
import com.google.common.collect.ArrayListMultimap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import wanion.lib.common.FixedSizeList;
import wanion.lib.common.MetaItem;
import wanion.unidict.UniOreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

final class IEIntegration extends AbstractIntegrationThread
{
	IEIntegration()
	{
		super("Immersive Engineering");
	}

	@Override
	public String call()
	{
		try {
			fixAlloyKilnRecipes();
			fixArcFurnaceRecipes();
			fixBlastFurnaceRecipes();
			fixCrusherRecipes();
			fixMetalPressRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
		}
		return threadName + "The world's engineer appears to be more immersive.";
	}

	private void fixAlloyKilnRecipes()
	{
		final List<AlloyRecipe> alloyRecipeList = AlloyRecipe.recipeList;
		final List<AlloyRecipe> newRecipes = new ArrayList<>();
		for (final Iterator<AlloyRecipe> alloyRecipeIterator = alloyRecipeList.iterator(); alloyRecipeIterator.hasNext(); ) {
			final AlloyRecipe alloyRecipe = alloyRecipeIterator.next();
			newRecipes.add(new AlloyRecipe(resourceHandler.getMainItemStack(alloyRecipe.output), alloyRecipe.input0, alloyRecipe.input1, alloyRecipe.time));
			alloyRecipeIterator.remove();
		}
		alloyRecipeList.clear();
		alloyRecipeList.addAll(newRecipes);
	}

	private void fixArcFurnaceRecipes()
	{
		final List<ArcFurnaceRecipe> arcFurnaceRecipes = ArcFurnaceRecipe.recipeList;
		final List<ArcFurnaceRecipe> correctRecipes = new ArrayList<>(new Double(arcFurnaceRecipes.size() * 1.3).intValue());
		for (final Iterator<ArcFurnaceRecipe> arcFurnaceRecipeIterator = arcFurnaceRecipes.iterator(); arcFurnaceRecipeIterator.hasNext(); ) {
			final ArcFurnaceRecipe recipe = arcFurnaceRecipeIterator.next();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(recipe.output);
			if (correctOutput == recipe.output)
				continue;
			final int time = (int) Math.floor((double) ((float) recipe.getTotalProcessTime() / ArcFurnaceRecipe.timeModifier));
			final int energy = (int) Math.floor((double) ((float) ((recipe.getTotalProcessEnergy() / ArcFurnaceRecipe.energyModifier) / recipe.getTotalProcessTime())));
			correctRecipes.add(new ArcFurnaceRecipe(correctOutput, recipe.input, recipe.slag, time, energy, (Object[]) recipe.additives));
			arcFurnaceRecipeIterator.remove();
		}
		arcFurnaceRecipes.addAll(correctRecipes);
	}

	private void fixBlastFurnaceRecipes()
	{
		final List<BlastFurnaceRecipe> correctRecipes = new ArrayList<>(new Double(BlastFurnaceRecipe.recipeList.size() * 1.3).intValue());
		correctRecipes.addAll(BlastFurnaceRecipe.recipeList.stream().map(blastFurnaceRecipe -> new BlastFurnaceRecipe(resourceHandler.getMainItemStack(blastFurnaceRecipe.output), blastFurnaceRecipe.input, blastFurnaceRecipe.time, blastFurnaceRecipe.slag)).collect(Collectors.toList()));
		BlastFurnaceRecipe.recipeList.clear();
		BlastFurnaceRecipe.recipeList.addAll(correctRecipes);
	}

	private void fixCrusherRecipes()
	{
		final List<CrusherRecipe> crusherRecipes = CrusherRecipe.recipeList;
		final List<CrusherRecipe> correctRecipes = new FixedSizeList<>(crusherRecipes.size());
		final TIntSet uniques = new TIntHashSet(crusherRecipes.size(), 1);
		for (final Iterator<CrusherRecipe> crusherRecipesIterator = crusherRecipes.iterator(); crusherRecipesIterator.hasNext(); ) {
			final CrusherRecipe crusherRecipe = crusherRecipesIterator.next();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(crusherRecipe.output);
			if (correctOutput == crusherRecipe.output)
				continue;
			final ItemStack input = UniOreDictionary.getFirstEntry(crusherRecipe.oreInputString);
			final int recipeId = MetaItem.getCumulative(input, correctOutput);
			if (!uniques.contains(recipeId)) {
				final CrusherRecipe newRecipe = new CrusherRecipe(correctOutput, crusherRecipe.input, (int) Math.floor((double) ((float) crusherRecipe.getTotalProcessEnergy() / CrusherRecipe.energyModifier)));
				if (crusherRecipe.secondaryOutput != null)
					setSecondaryOutputAndChance(newRecipe,resourceHandler.getMainItemStacks(crusherRecipe.secondaryOutput), crusherRecipe.secondaryChance);
				correctRecipes.add(newRecipe);
				uniques.add(recipeId);
			}
			crusherRecipesIterator.remove();
		}
		crusherRecipes.addAll(correctRecipes);
	}

	private void fixMetalPressRecipes()
	{
		final ArrayListMultimap<ComparableItemStack, MetalPressRecipe> metalPressRecipes = MetalPressRecipe.recipeList;
		final ArrayListMultimap<ComparableItemStack, MetalPressRecipe> correctRecipes = ArrayListMultimap.create();
		final TIntSet uniques = new TIntHashSet(metalPressRecipes.size(), 1);
		for (final Iterator<MetalPressRecipe> metalPressRecipesIterator = metalPressRecipes.values().iterator(); metalPressRecipesIterator.hasNext(); ) {
			final MetalPressRecipe metalPressRecipe = metalPressRecipesIterator.next();
			final ItemStack output = resourceHandler.getMainItemStack(metalPressRecipe.output);
			if (output == metalPressRecipe.output)
				continue;
			final int id = MetaItem.getCumulative(output, metalPressRecipe.mold.stack);
			if (!uniques.contains(id)) {
				correctRecipes.put(metalPressRecipe.mold, new MetalPressRecipe(output, metalPressRecipe.input, metalPressRecipe.mold, (int) Math.floor((double) ((float) metalPressRecipe.getTotalProcessEnergy() / MetalPressRecipe.energyModifier))).setInputSize(metalPressRecipe.input.inputSize));
				uniques.add(id);
			}
			metalPressRecipesIterator.remove();
		}
		metalPressRecipes.putAll(correctRecipes);
	}

	private static void setSecondaryOutputAndChance(@Nonnull final CrusherRecipe crusherRecipe, final ItemStack[] itemStacks, final float[] chance)
	{
		if (crusherRecipe.secondaryOutput != null && crusherRecipe.secondaryChance != null && itemStacks.length == chance.length) {
			final List<Object> secondaryAndChanceList = new ArrayList<>();
			for (int i = 0; i < itemStacks.length; i++) {
				secondaryAndChanceList.add(itemStacks[i]);
				secondaryAndChanceList.add(chance[i]);
			}
			crusherRecipe.addToSecondaryOutput(secondaryAndChanceList.toArray());
		}
	}
}
