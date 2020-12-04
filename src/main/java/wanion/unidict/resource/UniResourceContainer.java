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
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.common.SpecificEntryItemStackComparator;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.common.Util;
import wanion.unidict.plugin.UniDictJEIPlugin;

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
	private List<ItemStack> originalEntries;

	public UniResourceContainer(@Nonnull final String name, final int kind)
	{
		if ((entries = UniOreDictionary.get(this.id = UniOreDictionary.getId(this.name = name))) == null)
			throw new RuntimeException("Something may have broke the Ore Dictionary!");
		originalEntries = new ArrayList<>(entries);
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

	public ItemStack getMainEntry(final ItemStack itemStack)
	{
		return new ItemStack(mainEntryItem, itemStack.getCount(), mainEntryMeta);
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
		final ItemStack mainEntry = entries.get(0);
		mainEntryMeta = (mainEntryItem = mainEntry.getItem()).getDamage(mainEntry);
		if (originalEntries.size() != entries.size())
			originalEntries = new ArrayList<>(entries);
		if (sort) {
			if (initialSize != entries.size())
				sort();
			final Config config = UniDict.getConfig();
			if (config.autoHideInJEI)
				removeBadEntriesFromJEI();
			if (config.keepOneEntry) {
				if ((!config.keepOneEntryBlackListsAsWhiteLists && (!config.keepOneEntryEntryBlackSet.contains(name) || !config.keepOneEntryKindBlackSet.contains(Resource.getNameFromKind(kind)))) || (config.keepOneEntryBlackListsAsWhiteLists && (config.keepOneEntryEntryBlackSet.contains(name) || config.keepOneEntryKindBlackSet.contains(Resource.getNameFromKind(kind)))))
					keepOneEntry();
			}
		}
		return updated = true;
	}

	@Nonnull
	int[] getHashes()
	{
		return MetaItem.getArray(originalEntries);
	}

	private void removeBadEntriesFromJEI()
	{
		if (entries.size() > 1 && !UniResourceHandler.getEntryJEIBlackSet().contains(name) && !UniResourceHandler.getKindJEIBlackSet().contains(kind))
			entries.subList(1, entries.size()).forEach(UniDictJEIPlugin::hide);
	}

	private void keepOneEntry()
	{
		if (entries.size() == 1)
			return;
		final Set<ItemStack> keepOneEntryBlackSet = ResourceHandler.keepOneEntryBlackSet;
		if (!keepOneEntryBlackSet.isEmpty()) {
			entries.subList(1, entries.size()).removeIf(itemStack -> !keepOneEntryBlackSet.contains(itemStack));
		} else entries.subList(1, entries.size()).clear();
	}

	public Comparator<ItemStack> getComparator()
	{
		final Config config = UniDict.getConfig();
		if (config.enableSpecificEntrySort && SpecificEntryItemStackComparator.hasComparatorForEntry(name))
			return SpecificEntryItemStackComparator.getComparatorFor(name);
		else if (config.enableSpecificKindSort && SpecificKindItemStackComparator.hasComparatorForKind(kind))
			return SpecificKindItemStackComparator.getComparatorFor(kind);
		return Util.itemStackComparatorByModName;
	}

	public boolean isSorted()
	{
		return sort;
	}

	void setSort(final boolean sort)
	{
		if (this.sort = sort)
			sort();
	}

	public void sort()
	{
		final Comparator<ItemStack> itemStackComparator = getComparator();
		if (itemStackComparator == null)
			return;
		entries.sort(itemStackComparator);
	}

	@Override
	public String toString()
	{
		return name;
	}
}