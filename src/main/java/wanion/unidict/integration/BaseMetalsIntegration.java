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
import net.minecraftforge.registries.IForgeRegistryModifiable;
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
		fixCrushingRecipes();
		return threadName + "Fixing Everything!";
	}

	private void fixCrushingRecipes()
	{
		final CrusherRecipeRegistry crusherRecipeRegistry = Util.getField(CrusherRecipeRegistry.class, "instance", null, CrusherRecipeRegistry.class);
		final IForgeRegistryModifiable<ICrusherRecipe> registry = Util.getField(CrusherRecipeRegistry.class, "registry", crusherRecipeRegistry, IForgeRegistryModifiable.class);
		final List<ResourceLocation> recipesToRemove = new ArrayList<>();
		final List<ICrusherRecipe> newRecipes = new ArrayList<>();
		registry.getEntries().forEach(recipe -> {
			final ICrusherRecipe crusherRecipe = recipe.getValue();
			final ItemStack output = crusherRecipe.getOutput();
			final ItemStack newOutput = resourceHandler.getMainItemStack(crusherRecipe.getOutput());
			if (output != newOutput) {
				recipesToRemove.add(recipe.getKey());
				if (crusherRecipe instanceof ArbitraryCrusherRecipe)
					newRecipes.add(new ArbitraryCrusherRecipe(crusherRecipe.getInputs().get(0), newOutput));
				else if (crusherRecipe instanceof OreDictionaryCrusherRecipe)
					newRecipes.add(new OreDictionaryCrusherRecipe(Util.getField(OreDictionaryCrusherRecipe.class, "oreDictSource", crusherRecipe, String .class), newOutput));
			}
		});
		recipesToRemove.forEach(registry::remove);
		newRecipes.forEach(registry::register);
	}
}