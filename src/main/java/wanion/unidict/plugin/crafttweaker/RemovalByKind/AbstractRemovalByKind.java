package wanion.unidict.plugin.crafttweaker.RemovalByKind;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.IAction;
import gnu.trove.set.hash.TIntHashSet;
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

	protected final TIntHashSet getKindsForRemovalHashSet()
	{
		final TIntHashSet kindsForRemoval = new TIntHashSet();
		for (final RemovalByKind removalByKind : RECIPE_REMOVAL_BY_KIND_LIST) {
			final int kind = Resource.getKindFromName(removalByKind.kind);
			if (kind > 0)
				kindsForRemoval.add(kind);
		}
		return kindsForRemoval;
	}

	protected static class RemovalByKind implements IAction
	{
		private final AbstractRemovalByKind abstractRemovalByKind;
		protected final String kind;

		protected RemovalByKind(@Nonnull AbstractRemovalByKind abstractRemovalByKind, @Nonnull final String kind)
		{
			this.abstractRemovalByKind = abstractRemovalByKind;
			this.kind = kind;
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