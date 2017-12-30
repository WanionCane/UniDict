package wanion.unidict.plugin.crafttweaker.RemovalByKind;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.IAction;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.plugin.crafttweaker.UniDictCraftTweakerPlugin;
import wanion.unidict.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRemovalByKind
{
	protected final List<RemovalByKind> RECIPE_REMOVAL_BY_KIND_LIST = new ArrayList<>();

	public abstract void apply(@Nonnull final UniDictAPI uniDictAPI);

	@Nonnull
	protected abstract String getDescription();

	public final boolean empty()
	{
		return RECIPE_REMOVAL_BY_KIND_LIST.isEmpty();
	}

	public final AbstractRemovalByKind getInstance()
	{
		return UniDictCraftTweakerPlugin.getRemovalByKind(getClass());
	}

	protected final TIntObjectMap<TIntList> getKindsForRemovalHashMap()
	{
		final TIntObjectMap<TIntList> kindsForRemoval = new TIntObjectHashMap<>();
		for (final RemovalByKind removalByKind : RECIPE_REMOVAL_BY_KIND_LIST) {
			final int kind = Resource.getKindFromName(removalByKind.kind);
			if (kind > 0)
				kindsForRemoval.put(kind, Resource.kindNamesToKindList(removalByKind.resourceKindWhiteList));
		}
		return kindsForRemoval;
	}

	protected static class RemovalByKind implements IAction
	{
		private final AbstractRemovalByKind abstractRemovalByKind;
		protected final String kind;
		protected final String[] resourceKindWhiteList;

		protected RemovalByKind(@Nonnull AbstractRemovalByKind abstractRemovalByKind, @Nonnull final String kind, final String[] resourceKindWhiteList)
		{
			this.abstractRemovalByKind = abstractRemovalByKind;
			this.kind = kind;
			this.resourceKindWhiteList = resourceKindWhiteList;
		}

		@Override
		public void apply()
		{
			abstractRemovalByKind.RECIPE_REMOVAL_BY_KIND_LIST.add(this);
		}

		@Override
		public String describe()
		{
			return abstractRemovalByKind.getDescription() + kind;
		}
	}
}