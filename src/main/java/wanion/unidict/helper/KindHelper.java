package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniDict.IDependence;

import java.util.List;

public class KindHelper implements IDependence
{
    private final TObjectLongMap<List<ItemStack>> oreKindMap = new TObjectLongHashMap<>();
    private final TIntLongMap stackKindMap = new TIntLongHashMap();

    public KindHelper()
    {
        UniDict.getAPI().resources.parallelStream().forEach(resource ->
                resource.getChildrenCollection().forEach(child -> {
                    final List<ItemStack> entries = child.getEntries();
                    if (entries != null) {
                        oreKindMap.put(entries, child.kind);
                        MetaItem.populateMap(entries, stackKindMap, child.kind);
                    }
                })
        );
    }

    public long get(final Object thing)
    {
        return (thing instanceof ItemStack) ? stackKindMap.get(MetaItem.get((ItemStack) thing)) : (thing instanceof List) ? oreKindMap.get(thing) : 0;
    }
}