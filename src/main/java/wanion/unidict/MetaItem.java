package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import java.util.Collection;

public final class MetaItem
{
    private static final FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();

    private MetaItem() {}

    public static int get(ItemStack itemStack)
    {
        if (itemStack == null)
            return 0;
        Item item = itemStack.getItem();
        int id = itemRegistry.getId(item);
        return (id > 0) ? id | item.getDamage(itemStack) + 1 << 16 : 0;
    }

    public static int get(Item item)
    {
        if (item == null)
            return 0;
        int id = itemRegistry.getIDForObject(item);
        return (id > 0) ? id | 65536 : 0;
    }

    public static int[] getArray(Collection<ItemStack> itemStackCollection)
    {
        return getList(itemStackCollection).toArray();
    }

    public static TIntList getList(Collection<ItemStack> itemStackCollection)
    {
        TIntList keys = new TIntArrayList();
        int hash;
        for (ItemStack itemStack : itemStackCollection)
            if ((hash = get(itemStack)) != 0)
                keys.add(hash);
        return keys;
    }

    public static TIntSet getSet(Collection<Resource> resourceCollection, int kind)
    {
        TIntSet keys = new TIntHashSet();
        UniResourceContainer container;
        for (Resource resource : resourceCollection)
            if ((container = resource.getChild(kind)) != null)
                keys.addAll(getList(container.getEntries()));
        return keys;
    }

    public static <E> void populateMap(Collection<ItemStack> itemStackCollection, TIntObjectMap<E> map, E defaultValue)
    {
        for (int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }

    public static void populateMap(Collection<ItemStack> itemStackCollection, TIntIntMap map, int defaultValue)
    {
        for (int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }

    public static int getCumulativeKey(ItemStack... itemStacks)
    {
        int cumulativeKey = 0;
        for (ItemStack itemStack : itemStacks)
            cumulativeKey += get(itemStack);
        return cumulativeKey;
    }
}