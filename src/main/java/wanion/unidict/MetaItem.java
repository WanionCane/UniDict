package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public final class MetaItem
{
    public static final FMLControlledNamespacedRegistry<Item> itemRegistry = (FMLControlledNamespacedRegistry<Item>) GameRegistry.findRegistry(Item.class);

    private MetaItem() {}

    public static int get(final ItemStack itemStack)
    {
        Item item;
        if (itemStack == null || (item = itemStack.getItem()) == null)
            return 0;
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

    public static ItemStack toItemStack(final int metaItemKey)
    {
        return metaItemKey > 0 ?  new ItemStack(itemRegistry.getRaw(metaItemKey ^ (metaItemKey & 65536)), 0, metaItemKey >> 16) : null;
    }

    public static int getCumulative(@Nonnull final Object[] objects, @Nonnull final ResourceHandler resourceHandler)
    {
        int cumulativeKey = 0;
        for (final Object object : objects)
            if (object instanceof ItemStack)
                cumulativeKey += get(resourceHandler.getMainItemStack((ItemStack) object));
            else if (object instanceof List && !((List) object).isEmpty())
                cumulativeKey += get((ItemStack) ((List) object).get(0));
        return cumulativeKey;
    }

    public static int getCumulative(@Nonnull final ItemStack... itemStacks)
    {
        int cumulativeKey = 0;
        for (final ItemStack itemStack : itemStacks)
            cumulativeKey += get(itemStack);
        return cumulativeKey;
    }

    public static int[] getArray(@Nonnull final Collection<ItemStack> itemStackCollection)
    {
        return getList(itemStackCollection).toArray();
    }

    public static TIntList getList(@Nonnull final Collection<ItemStack> itemStackCollection)
    {
        final TIntList keys = new TIntArrayList();
        int hash;
        for (final ItemStack itemStack : itemStackCollection)
            if ((hash = get(itemStack)) != 0)
                keys.add(hash);
        return keys;
    }

    public static TIntSet getSet(@Nonnull final Collection<Resource> resourceCollection, final long kind)
    {
        final TIntSet keys = new TIntHashSet();
        resourceCollection.stream().filter(resource -> (resource.getChildren() & kind) > 0).forEach(resource -> keys.addAll(getList(resource.getChild(kind).getEntries())));
        return keys;
    }

    public static TIntSet getSet(@Nonnull final Collection<ItemStack> itemStackCollection)
    {
        return new TIntHashSet(getList(itemStackCollection));
    }

    public static <E> void populateMap(@Nonnull final Collection<ItemStack> itemStackCollection, @Nonnull final TIntObjectMap<E> map, final E defaultValue)
    {
        for (final int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }

    public static void populateMap(@Nonnull final Collection<ItemStack> itemStackCollection, @Nonnull final TIntLongMap map, final long defaultValue)
    {
        for (final int id : getArray(itemStackCollection))
            map.put(id, defaultValue);
    }
}