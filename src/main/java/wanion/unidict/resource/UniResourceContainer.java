package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniJEIPlugin;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.common.Util;

import javax.annotation.Nonnull;
import java.util.*;

public final class UniResourceContainer
{
	public final String name;
	public final int kind;
	private final int id;
	private final List<ItemStack> entries;
	private final int initialSize;
	private boolean sort = false;
	private boolean updated = false;
	private Item mainEntryItem;
	private int mainEntryMeta;
	private int[] hashes;

	public UniResourceContainer(@Nonnull final String name, final int kind)
	{
		if ((entries = UniOreDictionary.get(this.id = UniOreDictionary.getId(this.name = name))) == null)
			throw new RuntimeException("Something may have broken the Ore Dictionary!");
		this.kind = kind;
		initialSize = entries.size();
	}

	public UniResourceContainer(@Nonnull final String name, final int kind, final boolean sort)
	{
		this(name, kind);
		setSort(sort);
	}

	public ItemStack getMainEntry()
	{
		return new ItemStack(mainEntryItem, 1, mainEntryMeta);
	}

	public ItemStack getMainEntry(final int size)
	{
		return new ItemStack(mainEntryItem, size, mainEntryMeta);
	}

	public List<ItemStack> getEntries()
	{
		return UniOreDictionary.getUn(id);
	}

	boolean updateEntries()
	{
		if (entries.isEmpty())
			return false;
		if (updated)
			return true;
		if (sort && initialSize != entries.size())
			sort();
		final ItemStack mainEntry = entries.get(0);
		mainEntryMeta = (mainEntryItem = mainEntry.getItem()).getDamage(mainEntry);
		if (sort) {
			hashes = MetaItem.getArray(entries);
			if (UniDict.getConfig().autoHideInJEI)
				removeBadEntriesFromJEI();
			if (UniDict.getConfig().keepOneEntry)
				keepOneEntry();
		}
		return updated = true;
	}

	int[] getHashes()
	{
		return hashes;
	}

	private void keepOneEntry()
	{
		if (entries.size() == 1)
			return;
		final Set<ItemStack> keepOneEntryBlackSet = ResourceHandler.keepOneEntryBlackSet;
		if (!keepOneEntryBlackSet.isEmpty()) {
			for (Iterator<ItemStack> keepOneEntryIterator = entries.subList(1, entries.size()).iterator(); keepOneEntryIterator.hasNext(); )
				if (!keepOneEntryBlackSet.contains(keepOneEntryIterator.next()))
					keepOneEntryIterator.remove();
		} else entries.subList(1, entries.size()).clear();
	}

	private void removeBadEntriesFromJEI()
	{
		if (entries.size() > 1)
			if (UniDict.getConfig().keepOneEntry)
				entries.subList(1, entries.size()).forEach(UniJEIPlugin::hide);
			else if (!UniResourceHandler.getKindBlackSet().contains(kind))
				entries.subList(1, entries.size()).forEach(UniJEIPlugin::hide);
	}

	public Comparator<ItemStack> getComparator()
	{
		return UniDict.getConfig().enableSpecificKindSort ? SpecificKindItemStackComparator.getComparatorFor(kind) : Util.itemStackComparatorByModName;
	}

	void setSort(final boolean sort)
	{
		if (this.sort = sort)
			sort();
	}

	public void sort()
	{
		final Comparator<ItemStack> itemStackComparator = getComparator();
		if (itemStackComparator != null)
			Collections.sort(entries, itemStackComparator);
	}

	@Override
	public String toString()
	{
		return name;
	}
}