package wanion.unidict.plugin.crafttweaker.removalByKind;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.CraftTweakerAPI;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import net.minecraft.item.crafting.FurnaceRecipes;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniAttributes;

import javax.annotation.Nonnull;

@ZenExpansion("mods.unidict.removalByKind")
public class Furnace extends RemovalByKind
{
	@Override
	public void remove(@Nonnull String kind, @Optional String[] resourceKindWhiteList)
	{
		CraftTweakerAPI.apply(this.new RemovalByKindAction(kind, resourceKindWhiteList));
	}

	@Override
	public void apply(@Nonnull final UniDictAPI uniDictAPI)
	{
		final TIntObjectMap<TIntList> kindsForRemoval = getKindsForRemovalHashMap();
		final ResourceHandler uniDictApiResourceHandler = uniDictAPI.getResourceHandler();
		FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(furnaceRecipe -> {
			final UniAttributes uniAttributes = uniDictApiResourceHandler.get(furnaceRecipe.getValue());
			final TIntList removalWhiteList = uniAttributes != null ? kindsForRemoval.get(uniAttributes.kind) : null;
			return removalWhiteList != null && (removalWhiteList.isEmpty() || (!removalWhiteList.isEmpty() && uniAttributes.resource.childrenExists(removalWhiteList)));
		});
	}

	@Nonnull
	@Override
	protected String getName()
	{
		return "Furnace";
	}

	@Nonnull
	@Override
	protected String getDescription()
	{
		return "Trying to remove Furnace recipes for kind: ";
	}
}