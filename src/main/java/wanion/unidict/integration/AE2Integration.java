package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IGrinderRegistry;
import appeng.core.features.registries.grinder.AppEngGrinderRecipe;
import appeng.core.features.registries.grinder.GrinderRecipeManager;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.lib.common.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class AE2Integration extends AbstractIntegrationThread
{
	AE2Integration()
	{
		super("Applied Energistics 2");
	}

	@Override
	public String call()
	{
		try {
			fixGrindStoneRecipes();
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
		return threadName + "The world of energistics has never been so powerful.";
	}

	private void fixGrindStoneRecipes() throws NoSuchMethodException
	{
		final Map<Object, IGrinderRecipe> grindStoneRecipes = Util.getField(GrinderRecipeManager.class, "recipes", AEApi.instance().registries().grinder(), Map.class);
		final List<GrinderRecipeWrapper> newRecipes = new ArrayList<>();
		final TIntSet uniques = new TIntHashSet(grindStoneRecipes.size(), 1);
		for (final Iterator<Map.Entry<Object, IGrinderRecipe>> grindStoneRecipeIterator = grindStoneRecipes.entrySet().iterator(); grindStoneRecipeIterator.hasNext(); ) {
			final Map.Entry<Object, IGrinderRecipe> grindStoneEntry = grindStoneRecipeIterator.next();
			final IGrinderRecipe grindStoneRecipe = grindStoneEntry.getValue();
			final ItemStack correctOutput = resourceHandler.getMainItemStack(grindStoneRecipe.getOutput());
			if (correctOutput == grindStoneRecipe.getOutput())
				continue;
			final ItemStack inputStack = config.keepOneEntry ? resourceHandler.getMainItemStack(grindStoneRecipe.getInput()) : grindStoneRecipe.getInput();
			final int recipeKey = MetaItem.getCumulative(inputStack, correctOutput);
			if (!uniques.contains(recipeKey)) {
				uniques.add(recipeKey);
				newRecipes.add(new GrinderRecipeWrapper(inputStack, resourceHandler.getMainItemStack(grindStoneRecipe.getOutput()), grindStoneRecipe.getOptionalOutput().isPresent() ? resourceHandler.getMainItemStack(grindStoneRecipe.getOptionalOutput().get()) : null, grindStoneRecipe.getOptionalChance(), grindStoneRecipe.getSecondOptionalOutput().isPresent() ? resourceHandler.getMainItemStack(grindStoneRecipe.getSecondOptionalOutput().get()) : null, grindStoneRecipe.getSecondOptionalChance(), grindStoneRecipe.getRequiredTurns()));
			}
			grindStoneRecipeIterator.remove();
		}
		final IGrinderRegistry grinderRecipeManager = AEApi.instance().registries().grinder();
		final Class<AppEngGrinderRecipe> appEngGrinderRecipeClass = AppEngGrinderRecipe.class;
		final Constructor<AppEngGrinderRecipe> appEngGrinderNormalRecipeConstructor = appEngGrinderRecipeClass.getDeclaredConstructor(ItemStack.class, ItemStack.class, int.class);
		final Constructor<AppEngGrinderRecipe> appEngGrinderOptionalRecipeConstructor = appEngGrinderRecipeClass.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, float.class, int.class);
		final Constructor<AppEngGrinderRecipe> appEngGrinderTwoOptionalRecipeConstructor = appEngGrinderRecipeClass.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, ItemStack.class, float.class, float.class, int.class);
		appEngGrinderNormalRecipeConstructor.setAccessible(true);
		appEngGrinderOptionalRecipeConstructor.setAccessible(true);
		appEngGrinderTwoOptionalRecipeConstructor.setAccessible(true);
		newRecipes.forEach(grinderRecipeWrapper -> {
			try {
				if (grinderRecipeWrapper.optionalOutput == null && grinderRecipeWrapper.optionalOutput2 == null)
					grinderRecipeManager.addRecipe(appEngGrinderNormalRecipeConstructor.newInstance(grinderRecipeWrapper.input, grinderRecipeWrapper.output, grinderRecipeWrapper.cost));
				else if (grinderRecipeWrapper.optionalOutput != null && grinderRecipeWrapper.optionalOutput2 == null)
					grinderRecipeManager.addRecipe(appEngGrinderOptionalRecipeConstructor.newInstance(grinderRecipeWrapper.input, grinderRecipeWrapper.output, grinderRecipeWrapper.optionalOutput, grinderRecipeWrapper.optionalChance, grinderRecipeWrapper.cost));
				else if (grinderRecipeWrapper.optionalOutput != null)
					grinderRecipeManager.addRecipe(appEngGrinderTwoOptionalRecipeConstructor.newInstance(grinderRecipeWrapper.input, grinderRecipeWrapper.output, grinderRecipeWrapper.optionalOutput, grinderRecipeWrapper.optionalOutput2, grinderRecipeWrapper.optionalChance, grinderRecipeWrapper.optionalChance2, grinderRecipeWrapper.cost));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	private static class GrinderRecipeWrapper
	{
		private final ItemStack input;
		private final ItemStack output;
		private final ItemStack optionalOutput;
		private final ItemStack optionalOutput2;
		private final float optionalChance;
		private final float optionalChance2;
		private final int cost;

		private GrinderRecipeWrapper(final ItemStack input, final ItemStack output, final ItemStack optionalOutput, final float optionalChance, final ItemStack optionalOutput2, final float optionalChance2, final int cost)
		{
			this.input = input;
			this.output = output;
			this.optionalOutput = optionalOutput;
			this.optionalOutput2 = optionalOutput2;
			this.optionalChance = optionalChance;
			this.optionalChance2 = optionalChance2;
			this.cost = cost;
		}
	}
}
