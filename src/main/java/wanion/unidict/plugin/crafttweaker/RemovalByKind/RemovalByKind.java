package wanion.unidict.plugin.crafttweaker.RemovalByKind;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.plugin.crafttweaker.UniDictCraftTweakerPlugin;
import wanion.unidict.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.unidict.removalByKind")
public abstract class RemovalByKind
{
	private static final Map<Class<? extends RemovalByKind>, List<RemovalByKindAction>> RECIPE_REMOVAL_BY_KIND_MAP = new IdentityHashMap<>();

	@ZenMethod
	public static RemovalByKind get(String name)
	{
		name = name != null ? name.toLowerCase() : null;
		if (!UniDictCraftTweakerPlugin.NAME_REMOVAL_BY_KIND_MAP.containsKey(name)) {
			CraftTweakerAPI.logError("mods.unidict.removalByKind.get(" + name + ") doesn't exists!");
			return null;
		}
		return UniDictCraftTweakerPlugin.NAME_REMOVAL_BY_KIND_MAP.get(name);
	}

	@ZenMethod
	public abstract void remove(@Nonnull final String kind, @Optional final String[] resourceKindWhiteList);

	public abstract void apply(@Nonnull final UniDictAPI uniDictAPI);

	@Nonnull
	protected abstract String getName();

	@Nonnull
	protected abstract String getDescription();

	public final wanion.unidict.plugin.crafttweaker.RemovalByKind.RemovalByKind getInstance()
	{
		return UniDictCraftTweakerPlugin.getRemovalByKind(getClass());
	}

	@Override
	public String toString()
	{
		return getName().toLowerCase();
	}

	protected final TIntObjectMap<TIntList> getKindsForRemovalHashMap()
	{
		final TIntObjectMap<TIntList> kindsForRemoval = new TIntObjectHashMap<>();
		for (final RemovalByKindAction removalByKindAction : getRemovalByKindList()) {
			final int kind = Resource.getKindFromName(removalByKindAction.kind);
			if (kind > 0)
				kindsForRemoval.put(kind, Resource.kindNamesToKindList(removalByKindAction.resourceKindWhiteList));
		}
		return kindsForRemoval;
	}

	private List<RemovalByKindAction> getRemovalByKindList()
	{
		final Class<? extends RemovalByKind> removalByKindClass = getClass();
		if (!RECIPE_REMOVAL_BY_KIND_MAP.containsKey(removalByKindClass))
			RECIPE_REMOVAL_BY_KIND_MAP.put(removalByKindClass, new ArrayList<>());
		return RECIPE_REMOVAL_BY_KIND_MAP.get(removalByKindClass);
	}

	protected class RemovalByKindAction implements IAction
	{
		protected final String kind;
		protected final String[] resourceKindWhiteList;

		protected RemovalByKindAction(@Nonnull final String kind, final String[] resourceKindWhiteList)
		{
			this.kind = kind;
			this.resourceKindWhiteList = resourceKindWhiteList;
		}

		@Override
		public void apply()
		{
			getRemovalByKindList().add(this);
		}

		@Override
		public String describe()
		{
			return getDescription() + kind;
		}
	}
}