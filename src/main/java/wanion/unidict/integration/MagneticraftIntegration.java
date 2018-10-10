package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipe;
import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipe;
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipe;
import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipe;
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipeManager;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe;
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe;
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe;
import com.google.common.collect.Lists;
import kotlin.Pair;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;

import java.util.List;

final class MagneticraftIntegration extends AbstractIntegrationThread
{
	MagneticraftIntegration()
	{
		super("Magneticraft");
	}

	@Override
	public String call()
	{
		try {
			fixCrushingTableRecipes();
			fixSluiceBoxRecipes();
			fixGrinderRecipes();
				fixSieveRecipe();
			} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "Everything got Magnetized.";
	}

	private void fixCrushingTableRecipes()
	{
		final List<ICrushingTableRecipe> recipes = Util.getField(CrushingTableRecipeManager.class, "recipes", CrushingTableRecipeManager.INSTANCE, List.class);
		if (recipes != null)
			recipes.replaceAll(iCrushingTableRecipe -> new CrushingTableRecipe(iCrushingTableRecipe.getInput(), resourceHandler.getMainItemStack(iCrushingTableRecipe.getOutput()), iCrushingTableRecipe.useOreDictionaryEquivalencies()));
	}

	private void fixSluiceBoxRecipes()
	{
		final List<ISluiceBoxRecipe> recipes = Util.getField(SluiceBoxRecipeManager.class, "recipes", SluiceBoxRecipeManager.INSTANCE, List.class);
		if (recipes == null)
			return;
		recipes.replaceAll(iSluiceBoxRecipe -> {
			final List<Pair<ItemStack, Float>> newOutputs = Lists.newArrayList(iSluiceBoxRecipe.getOutputs());
			newOutputs.replaceAll((itemStackFloatPair -> new Pair<>(resourceHandler.getMainItemStack(itemStackFloatPair.getFirst()), itemStackFloatPair.getSecond())));
			return new SluiceBoxRecipe(iSluiceBoxRecipe.getInput(), newOutputs, iSluiceBoxRecipe.useOreDictionaryEquivalencies());
		});
	}

	private void fixGrinderRecipes()
	{
		final List<IGrinderRecipe> recipes = Util.getField(GrinderRecipeManager.class, "recipes", GrinderRecipeManager.INSTANCE, List.class);
		if (recipes != null)
			recipes.replaceAll(iGrinderRecipe -> new GrinderRecipe(iGrinderRecipe.getInput(), resourceHandler.getMainItemStack(iGrinderRecipe.getPrimaryOutput()), resourceHandler.getMainItemStack(iGrinderRecipe.getSecondaryOutput()), iGrinderRecipe.getProbability(), iGrinderRecipe.getDuration(), iGrinderRecipe.useOreDictionaryEquivalencies()));
	}

	private void fixSieveRecipe()
	{
		final List<ISieveRecipe> recipes = Util.getField(SieveRecipeManager.class, "recipes", SieveRecipeManager.INSTANCE, List.class);
		if (recipes != null)
			recipes.replaceAll(iSieveRecipe -> new SieveRecipe(iSieveRecipe.getInput(), resourceHandler.getMainItemStack(iSieveRecipe.getPrimary()), iSieveRecipe.getPrimaryChance(), resourceHandler.getMainItemStack(iSieveRecipe.getSecondary()), iSieveRecipe.getSecondaryChance(), resourceHandler.getMainItemStack(iSieveRecipe.getTertiary()), iSieveRecipe.getTertiaryChance(), iSieveRecipe.getDuration(), iSieveRecipe.useOreDictionaryEquivalencies()));
	}
}