package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Resource
{
    private static final TObjectIntMap<String> nameToKind = new TObjectIntHashMap<>();
    private static final TIntObjectMap<String> kindToName = new TIntObjectHashMap<>();
    private static int totalKindsRegistered;
    private static boolean populated;
    public final String name;
    private final TIntObjectMap<UniResourceContainer> childrenMap = new TIntObjectHashMap<>();
    private int children = 0;
    private boolean updated;

    public Resource(String name, TIntObjectMap<UniResourceContainer> containerMap)
    {
        this.name = name;
        childrenMap.putAll(containerMap);
        populateChildren();
    }

    private void populateChildren()
    {
        for (int kind : childrenMap.keys())
            children |= kind;
    }

    public int getChildren()
    {
        return children;
    }

    public UniResourceContainer getChild(String childName)
    {
        return childrenMap.get(nameToKind.get(childName));
    }

    public UniResourceContainer getChild(int kind)
    {
        return childrenMap.get(kind);
    }

    public TIntObjectMap<UniResourceContainer> getChildrenMap()
    {
        return new TUnmodifiableIntObjectMap<>(childrenMap);
    }

    Collection<UniResourceContainer> getChildrenCollection()
    {
        return childrenMap.valueCollection();
    }

    public Resource filteredClone(int kinds)
    {
        TIntObjectMap<UniResourceContainer> newChildrenMap = new TIntObjectHashMap<>();
        for (int kind : childrenMap.keys())
            if ((kinds & kind) > 0)
                newChildrenMap.put(kind, childrenMap.get(kind));
        return new Resource(name, newChildrenMap);
    }

    public void updateEntries()
    {
        if (updated)
            return;
        else
            updated = true;
        for (TIntIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext(); ) {
            int kindId = childrenIterator.next();
            if (childrenMap.get(kindId).updateEntries())
                continue;
            children &= ~kindId;
            childrenIterator.remove();
        }
    }

    public static void populateKindMap(String[] kinds)
    {
        if (populated)
            return;
        else
            populated = true;
        for (String kindName : kinds) {
            if (nameToKind.containsKey(kindName))
                continue;
            int value = 1 << totalKindsRegistered++;
            nameToKind.put(kindName, value);
            kindToName.put(value, kindName);
        }
    }

    public static int registerKind(String kindName)
    {
        if (nameToKind.containsKey(kindName))
            return nameToKind.get(kindName);
        int kind = 1 << totalKindsRegistered++;
        nameToKind.put(kindName, kind);
        kindToName.put(kind, kindName);
        return kind;
    }

    public static List<Resource> getResources(Collection<Resource> resources, String... kinds)
    {
        int kindsId = 0;
        for (String kind : kinds) {
            int kindId;
            if ((kindId = Resource.getKindOfName(kind)) == 0)
                return Collections.emptyList();
            kindsId |= kindId;
        }
        return getResources(resources, kindsId);
    }

    public static List<Resource> getResources(Collection<Resource> resources, int kinds)
    {
        if (kinds == 0)
            return Collections.emptyList();
        List<Resource> sortedResources = new ArrayList<>();
        for (Resource resource : resources)
            if ((kinds & resource.getChildren()) == kinds)
                sortedResources.add(resource);
        return sortedResources;
    }

    public static List<Resource> getResources(Collection<Resource> resources, int... kinds)
    {
        int trueKinds = 0;
        for (int kind : kinds)
            if (kind != 0)
                trueKinds |= kind;
            else
                return Collections.emptyList();
        return getResources(resources, trueKinds);
    }

    public static List<String> getKinds()
    {
        return Collections.unmodifiableList(new ArrayList<>(nameToKind.keySet()));
    }

    public static int getKindOfName(String name)
    {
        return nameToKind.get(name);
    }

    public static String getNameOfKind(int kind)
    {
        return kindToName.get(kind);
    }

    public static boolean kindExists(String name)
    {
        return nameToKind.containsKey(name);
    }

    public static boolean kindExists(String... names)
    {
        for (String name : names)
            if (!nameToKind.containsKey(name))
                return false;
        return true;
    }

    @Override
    public String toString()
    {
        if (childrenMap.isEmpty())
            return name + " = {}";
        final StringBuilder output = new StringBuilder(name + " = {");
        for (TIntIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext();)
            output.append(kindToName.get(childrenIterator.next())).append((childrenIterator.hasNext()) ? ", " : "}");
        return output.toString();
    }
}