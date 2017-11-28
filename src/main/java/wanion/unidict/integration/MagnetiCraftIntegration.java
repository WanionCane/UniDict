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
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipe;
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipeManager;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe;
import kotlin.Pair;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;

import java.util.ArrayList;
import java.util.List;

final class MagnetiCraftIntegration extends AbstractIntegrationThread
{
	MagnetiCraftIntegration()
	{
		super("Magneticraft");
	}

	@Override
	public String call()
	{
		fixCrushingTableRecipes();
		fixSluiceBoxRecipes();
		return threadName + "Everything get Magnetized.";
	}

	private void fixCrushingTableRecipes()
	{
		final List<ICrushingTableRecipe> newRecipes = new ArrayList<>();
		final List<ICrushingTableRecipe> recipes = Util.getField(CrushingTableRecipeManager.class, "recipes", CrushingTableRecipeManager.INSTANCE, List.class);
		recipes.forEach(iCrushingTableRecipe -> newRecipes.add(new CrushingTableRecipe(iCrushingTableRecipe.getInput(), resourceHandler.getMainItemStack(iCrushingTableRecipe.getOutput()), iCrushingTableRecipe.useOreDictionaryEquivalencies())));
		recipes.clear();
		recipes.addAll(newRecipes);
	}

	private void fixSluiceBoxRecipes()
	{
		final List<ISluiceBoxRecipe> newRecipes = new ArrayList<>();
		final List<ISluiceBoxRecipe> recipes = Util.getField(SluiceBoxRecipeManager.class, "recipes", SluiceBoxRecipeManager.INSTANCE, List.class);
		recipes.forEach(iSluiceBoxRecipe -> {
			final List<Pair<ItemStack, Float>> newSecondaryOutputs = new ArrayList<>();
			iSluiceBoxRecipe.getSecondaryOutput().forEach(itemStackFloatPair -> newSecondaryOutputs.add(new Pair<>(resourceHandler.getMainItemStack(itemStackFloatPair.getFirst()), itemStackFloatPair.getSecond())));
			newRecipes.add(new SluiceBoxRecipe(iSluiceBoxRecipe.getInput(), resourceHandler.getMainItemStack(iSluiceBoxRecipe.getPrimaryOutput()), newSecondaryOutputs, iSluiceBoxRecipe.useOreDictionaryEquivalencies()));
		});
		recipes.clear();
		recipes.addAll(newRecipes);
	}
}