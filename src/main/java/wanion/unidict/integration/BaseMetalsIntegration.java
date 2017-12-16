package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.mcmoddev.lib.registry.CrusherRecipeRegistry;
import com.mcmoddev.lib.registry.recipe.ArbitraryCrusherRecipe;
import com.mcmoddev.lib.registry.recipe.ICrusherRecipe;
import com.mcmoddev.lib.registry.recipe.OreDictionaryCrusherRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import wanion.lib.common.Util;

import java.util.ArrayList;
import java.util.List;

final class BaseMetalsIntegration extends AbstractIntegrationThread
{
	BaseMetalsIntegration()
	{
		super("Base Metals");
	}

	@Override
	public String call()
	{
		try {
			fixCrushingRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "Fixing Everything!";
	}

	private void fixCrushingRecipes()
	{
		final List<ResourceLocation> recipesToRemove = new ArrayList<>();
		final List<ICrusherRecipe> newRecipes = new ArrayList<>();
		CrusherRecipeRegistry.getAll().forEach(recipe -> {
			final ItemStack output = recipe.getOutput();
			final ItemStack newOutput = resourceHandler.getMainItemStack(recipe.getOutput());
			if (output != newOutput) {
				recipesToRemove.add(recipe.getRegistryName());
				if (recipe instanceof ArbitraryCrusherRecipe)
					newRecipes.add(new ArbitraryCrusherRecipe(recipe.getInputs().get(0), newOutput));
				else if (recipe instanceof OreDictionaryCrusherRecipe)
					newRecipes.add(new OreDictionaryCrusherRecipe(Util.getField(OreDictionaryCrusherRecipe.class, "oreDictSource", recipe, String .class), newOutput));
			}
		});
		final CrusherRecipeRegistry crusherRecipeRegistry = CrusherRecipeRegistry.getInstance();
		recipesToRemove.forEach(crusherRecipeRegistry::remove);
		newRecipes.forEach(crusherRecipeRegistry::register);
	}
}