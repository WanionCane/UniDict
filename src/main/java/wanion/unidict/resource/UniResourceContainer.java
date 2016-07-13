package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniJEIPlugin;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.common.Util;

import java.util.*;

import static wanion.unidict.Config.enableSpecificKindSort;

public final class UniResourceContainer
{
    public final String name;
    public final long kind;
    private final int id;
    private final List<ItemStack> entries;
    private final int initialSize;
    private boolean sort = false;
    private Item mainEntryItem;
    private int mainEntryMeta;

    public UniResourceContainer(String name, long kind) {
        if ((entries = UniOreDictionary.get(this.id = UniOreDictionary.getId(this.name = name))) == null)
            throw new RuntimeException("Something may have broken the Ore Dictionary!");
        this.kind = kind;
        initialSize = entries.size();
    }

    public ItemStack getMainEntry() {
        return new ItemStack(mainEntryItem, 1, mainEntryMeta);
    }

    public ItemStack getMainEntry(int size) {
        return new ItemStack(mainEntryItem, size, mainEntryMeta);
    }

    public List<ItemStack> getEntries() {
        return UniOreDictionary.getUn(id);
    }

    void keepOneEntry() {
        if (!sort || entries.size() == 1)
            return;
        Set<ItemStack> keepOneEntryBlackSet = ResourceHandler.keepOneEntryBlackSet;
        if (!keepOneEntryBlackSet.isEmpty()) {
            for (Iterator<ItemStack> keepOneEntryIterator = entries.subList(1, entries.size()).iterator(); keepOneEntryIterator.hasNext(); )
                if (!keepOneEntryBlackSet.contains(keepOneEntryIterator.next()))
                    keepOneEntryIterator.remove();
        } else entries.subList(1, entries.size()).clear();
    }

    void removeBadEntriesFromNEI()
    {
        if (sort)
            if (entries.size() > 1)
                entries.subList(1, entries.size()).forEach(UniJEIPlugin::hide);
    }

    boolean updateEntries() {
        if (entries.isEmpty())
            return false;
        if (sort && initialSize != entries.size())
            sort();
        ItemStack mainEntry = entries.get(0);
        mainEntryMeta = (mainEntryItem = mainEntry.getItem()).getDamage(mainEntry);
        return true;
    }

    public Comparator<ItemStack> getComparator() {
        return (enableSpecificKindSort) ? SpecificKindItemStackComparator.getComparatorFor(kind) : Util.itemStackComparatorByModName;
    }

    public UniResourceContainer setSortAndGet(final boolean sort)
    {
        if (this.sort = sort)
            sort();
        return this;
    }

    void setSort(final boolean sort)
    {
        if (this.sort = sort)
            sort();
    }

    public void sort() {
        final Comparator<ItemStack> itemStackComparator = getComparator();
        if (itemStackComparator != null)
            Collections.sort(entries, itemStackComparator);
    }

    @Override
    public String toString() {
        return name;
    }
}