package wanion.unidict.plugin.crafttweaker.RemovalByKind;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.plugin.crafttweaker.UniDictCraftTweakerPlugin;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ZenRegister
@ZenClass("mods.unidict.removalByKind")
public final class Crafting extends AbstractRemovalByKind
{
	@ZenMethod
	public static void crafting(@Nonnull final String kind)
	{
		CraftTweakerAPI.apply(new RemovalByKind(UniDictCraftTweakerPlugin.getRemovalByKind(Crafting.class), kind));
	}

	@Override
	public void apply(@Nonnull final UniDictAPI uniDictAPI)
	{
		final TIntHashSet kindsForRemoval = getKindsForRemovalHashSet();
		final ResourceHandler uniDictApiResourceHandler = uniDictAPI.getResourceHandler();
		final Set<Map.Entry<ResourceLocation, IRecipe>> recipes = RegistryManager.ACTIVE.<IRecipe>getRegistry(GameData.RECIPES).getEntries();
		final List<ResourceLocation> recipesToRemove = new ArrayList<>();
		for (final Map.Entry<ResourceLocation, IRecipe> recipeEntry : recipes) {
			final IRecipe recipe = recipeEntry.getValue();
			if (kindsForRemoval.contains(uniDictApiResourceHandler.getKind(recipe.getRecipeOutput())))
				recipesToRemove.add(recipe.getRegistryName());
		}
		final ForgeRegistry<IRecipe> recipeRegistry = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
		recipesToRemove.forEach(recipeRegistry::remove);
	}

	@Nonnull
	@Override
	protected String getDescription()
	{
		return "Trying to remove Crafting Table recipes for kind: ";
	}
}