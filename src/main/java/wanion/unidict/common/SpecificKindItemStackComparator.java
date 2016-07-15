package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.Comparator;

import static wanion.unidict.common.Util.getModName;

public final class SpecificKindItemStackComparator implements Comparator<ItemStack>
{
    private static TLongObjectMap<SpecificKindItemStackComparator> kindSpecificComparators = new TLongObjectHashMap<>();
    private final TObjectLongMap<String> ownerOfKind;

    private SpecificKindItemStackComparator(final long kind)
    {
        if ((ownerOfKind = Config.getOwnerOfEveryKindMap(kind)) == null)
            throw new RuntimeException("this exception should be called: ThisShouldNeverHappenException.");
    }

    public static synchronized SpecificKindItemStackComparator getComparatorFor(final long kind)
    {
        if (!kindSpecificComparators.containsKey(kind))
            kindSpecificComparators.put(kind, new SpecificKindItemStackComparator(kind));
        return kindSpecificComparators.get(kind);
    }

    public static void nullify()
    {
        kindSpecificComparators = null;
    }

    @Override
    public int compare(@Nonnull final ItemStack itemStack1, @Nonnull final ItemStack itemStack2)
    {
        final String stack1ModName = getModName(itemStack1);
        if (Config.keepOneEntry && Config.keepOneEntryModBlackSet.contains(stack1ModName))
            ResourceHandler.addToKeepOneEntryModBlackSet(itemStack1);
        return getIndex(stack1ModName) < getIndex(itemStack2) ? -1 : 0;
    }

    private long getIndex(ItemStack itemStack)
    {
        return ownerOfKind.get(getModName(itemStack));
    }

    private long getIndex(String modName)
    {
        return ownerOfKind.get(modName);
    }
}