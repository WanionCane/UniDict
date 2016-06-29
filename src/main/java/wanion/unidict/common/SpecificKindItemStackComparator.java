package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceHandler;

import java.util.Comparator;

import static wanion.unidict.common.Util.getModName;

public final class SpecificKindItemStackComparator implements Comparator<ItemStack>
{
    private static TIntObjectMap<SpecificKindItemStackComparator> kindSpecificComparators = new TIntObjectHashMap<>();
    private final TObjectIntMap<String> ownerOfKind;

    public SpecificKindItemStackComparator(int kind)
    {
        if ((ownerOfKind = Config.getOwnerOfEveryKindMap(kind)) == null)
            throw new RuntimeException("this exception should be called: ThisShouldNeverHappenException.");
        kindSpecificComparators.put(kind, this);
    }

    @Override
    public int compare(ItemStack itemStack1, ItemStack itemStack2)
    {
        String stack1ModName = getModName(itemStack1);
        if (Config.keepOneEntry && Config.keepOneEntryModBlackSet.contains(stack1ModName))
            ResourceHandler.addToKeepOneEntryModBlackSet(itemStack1);
        return getIndex(stack1ModName) < getIndex(itemStack2) ? -1 : 0;
    }

    private int getIndex(ItemStack itemStack)
    {
        return ownerOfKind.get(getModName(itemStack));
    }

    private int getIndex(String modName)
    {
        return ownerOfKind.get(modName);
    }

    public static SpecificKindItemStackComparator getComparatorFor(int kind)
    {
        return kindSpecificComparators.get(kind);
    }

    public static void nullify(UniResourceHandler uniResourceHandler)
    {
        if (uniResourceHandler != null)
            kindSpecificComparators = null;
    }
}