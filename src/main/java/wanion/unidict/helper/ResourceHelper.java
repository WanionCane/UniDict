package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniDict.IDependence;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;

import java.util.List;

public final class ResourceHelper implements IDependence
{
    private final TObjectIntMap<List> oreKindMap = new TObjectIntHashMap<>();
    private final TIntIntMap stackKindMap = new TIntIntHashMap();
    private final UniDictAPI uniDictAPI = UniDict.getAPI();

    public int get(Object thing)
    {
        return (thing instanceof ItemStack) ? stackKindMap.get(MetaItem.get((ItemStack) thing)) : (thing instanceof List) ? oreKindMap.get(thing) : 0;
    }

    public void prepare(int kind)
    {
        if (kind == 0)
            return;
        for (Resource resource : uniDictAPI.getResources(kind)) {
            List<ItemStack> entries = resource.getChild(kind).getEntries();
            oreKindMap.put(entries, kind);
            MetaItem.populateMap(entries, stackKindMap, kind);
        }
    }
}