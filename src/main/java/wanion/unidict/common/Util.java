package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import wanion.lib.common.MetaItem;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static wanion.lib.common.Util.getModName;

public final class Util
{
	public static final Comparator<ItemStack> itemStackComparatorByModName = new Comparator<ItemStack>()
	{
		@Override
		public int compare(final ItemStack itemStack1, final ItemStack itemStack2)
		{
			final String stack1ModName = getModName(itemStack1);
			final Config config = UniDict.getConfig();
			if (config.keepOneEntry && config.keepOneEntryModBlackSet.contains(stack1ModName))
				ResourceHandler.addToKeepOneEntryModBlackSet(itemStack1);
			return getIndex(stack1ModName) < getIndex(itemStack2) ? -1 : 0;
		}

		private long getIndex(final ItemStack itemStack)
		{
			return UniDict.getConfig().ownerOfEveryThing.get(getModName(itemStack));
		}

		private long getIndex(final String modName)
		{
			return UniDict.getConfig().ownerOfEveryThing.get(modName);
		}
	};

	private Util() {}

	public static int getCumulative(@Nonnull final Object[] objects, @Nonnull final ResourceHandler resourceHandler)
	{
		int cumulativeKey = 0;
		for (final Object object : objects)
			if (object instanceof ItemStack)
				cumulativeKey += MetaItem.get(resourceHandler.getMainItemStack((ItemStack) object));
			else if (object instanceof List && !((List) object).isEmpty())
				cumulativeKey += MetaItem.get((ItemStack) ((List) object).get(0));
		return cumulativeKey;
	}

	public static TIntList getList(@Nonnull final Object[] objects, @Nonnull final ResourceHandler resourceHandler)
	{
		final TIntList keys = new TIntArrayList();
		int bufKey;
		for (final Object object : objects)
			if (object instanceof ItemStack) {
				if ((bufKey = MetaItem.get(resourceHandler.getMainItemStack((ItemStack) object))) > 0)
					keys.add(bufKey);
			} else if (object instanceof Ingredient && ((Ingredient) object).getMatchingStacks().length > 0) {
				if ((bufKey = MetaItem.get(resourceHandler.getMainItemStack(((Ingredient) object).getMatchingStacks()[0]))) > 0)
					keys.add(bufKey);
			} else if (object instanceof List && !((List) object).isEmpty())
				if ((bufKey = MetaItem.get(((ItemStack) ((List) object).get(0)))) > 0)
					keys.add(bufKey);
		return keys;
	}

	public static TIntList getList(@Nonnull final List<?> objects, @Nonnull final ResourceHandler resourceHandler)
	{
		return getList(objects.toArray(), resourceHandler);
	}

	public static TIntSet getSet(@Nonnull final Collection<Resource> resourceCollection, final int kind)
	{
		final TIntSet keys = new TIntHashSet();
		resourceCollection.stream().filter(resource -> resource.childExists(kind)).forEach(resource -> keys.addAll(MetaItem.getList(resource.getChild(kind).getEntries())));
		return keys;
	}
}