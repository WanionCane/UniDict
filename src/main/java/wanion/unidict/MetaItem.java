package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import java.util.Collection;

public final class MetaItem
{
    public static final FMLControlledNamespacedRegistry<Item> itemRegistry = (FMLControlledNamespacedRegistry<Item>) GameRegistry.findRegistry(Item.class);

    private MetaItem()
    {
    }

    public static int get(ItemStack itemStack)
    {
        if (itemStack == null)
            return 0;
        final Item item = itemStack.getItem();
        final int id = itemRegistry.getId(item);
        return (id > 0) ? id | item.getDamage(itemStack) + 1 << 16 : 0;
    }

    public static int get(final Item item)
    {
        if (item == null)
            return 0;
        final int id = itemRegistry.getIDForObject(item);
        return (id > 0) ? id | 65536 : 0;
    }

    public static int[] getArray(final Collection<ItemStack> itemStackCollection)
    {
        return getList(itemStackCollection).toArray();
    }

    public static TIntList getList(final Collection<ItemStack> itemStackCollection)
    {
        final TIntList keys = new TIntArrayList();
        int hash;
        for (final ItemStack itemStack : itemStackCollection)
            if ((hash = get(itemStack)) != 0)
                keys.add(hash);
        return keys;
    }

    public static TIntSet getSet(final Collection<Resource> resourceCollection, final long kind)
    {
        final TIntSet keys = new TIntHashSet();
        UniResourceContainer container;
        for (final Resource resource : resourceCollection)
            if ((container = resource.getChild(kind)) != null)
                keys.addAll(getList(container.getEntries()));
        return keys;
    }

    public static TIntSet getSet(final Collection<ItemStack> itemStackCollection)
    {
        final TIntSet keys = new TIntHashSet();
        int hash;
        for (final ItemStack itemStack : itemStackCollection)
            if ((hash = get(itemStack)) != 0)
                keys.add(hash);
        return keys;
    }

    public static <E> void populateMap(final Collection<ItemStack> itemStackCollection, final TIntObjectMap<E> map, final E defaultValue)
    {
        for (final int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }

    public static void populateMap(final Collection<ItemStack> itemStackCollection, final TIntLongMap map, long defaultValue)
    {
        for (final int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }
}