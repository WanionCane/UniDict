package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Resource
{
    private static final TObjectLongMap<String> nameToKind = new TObjectLongHashMap<>();
    private static final TLongObjectMap<String> kindToName = new TLongObjectHashMap<>();
    private static int totalKindsRegistered = 0;
    private static boolean populated;
    public final String name;
    private final TLongObjectMap<UniResourceContainer> childrenMap = new TLongObjectHashMap<>();
    private long children = 0;
    private boolean updated;

    public Resource(@Nonnull final String name)
    {
        this.name = name;
    }

    public Resource(@Nonnull final String name, @Nonnull final TLongObjectMap<UniResourceContainer> containerMap)
    {
        this.name = name;
        containerMap.forEachValue(container -> {
            children |= container.kind;
            return childrenMap.put(container.kind, container) == null;
        });
    }

    public long getChildren()
    {
        return children;
    }

    public UniResourceContainer getChild(@Nonnull final String childName)
    {
        return childrenMap.get(nameToKind.get(childName));
    }

    public UniResourceContainer getChild(final long kind)
    {
        return childrenMap.get(kind);
    }

    public TLongObjectMap<UniResourceContainer> getChildrenMap()
    {
        return new TUnmodifiableLongObjectMap<>(childrenMap);
    }

    Collection<UniResourceContainer> getChildrenCollection()
    {
        return childrenMap.valueCollection();
    }

    public Resource filteredClone(final long kinds)
    {
        final TLongObjectMap<UniResourceContainer> newChildrenMap = new TLongObjectHashMap<>();
        childrenMap.forEachEntry((child, container) -> {
            if ((child & kinds) > 0)
                newChildrenMap.put(child, container);
            return true;
        });
        return new Resource(name, newChildrenMap);
    }

    public boolean addChild(@Nonnull final UniResourceContainer child)
    {
        final long kind = child.kind;
        if ((children & kind) > 0 || !child.name.endsWith(name))
            return false;
        children |= kind;
        childrenMap.put(kind, child);
        return true;
    }

    public Resource setSortOfChildren(final boolean sort)
    {
        childrenMap.forEachValue(child -> {
            child.setSort(sort);
            return true;
        });
        return this;
    }

    public void updateEntries()
    {
        if (updated)
            return;
        else
            updated = true;
        for (final TLongIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext(); ) {
            long kindId = childrenIterator.next();
            if (childrenMap.get(kindId).updateEntries())
                continue;
            children &= ~kindId;
            childrenIterator.remove();
        }
    }

    @Override
    public String toString()
    {
        if (childrenMap.isEmpty())
            return name + " = {}";
        final StringBuilder output = new StringBuilder(name + " = {");
        for (TLongIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext(); )
            output.append(kindToName.get(childrenIterator.next())).append((childrenIterator.hasNext()) ? ", " : "}");
        return output.toString();
    }

    public static void register(@Nonnull final String kindName)
    {
        if (nameToKind.containsKey(kindName))
            return;
        final int kind = 1 << totalKindsRegistered++;
        nameToKind.put(kindName, kind);
        kindToName.put(kind, kindName);
    }

    public static long registerAndGet(@Nonnull final String kindName)
    {
        if (nameToKind.containsKey(kindName))
            return nameToKind.get(kindName);
        final int kind = 1 << totalKindsRegistered++;
        nameToKind.put(kindName, kind);
        kindToName.put(kind, kindName);
        return kind;
    }

    public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final String... kinds)
    {
        long kindsId = 0;
        for (final String kind : kinds) {
            long kindId;
            if ((kindId = Resource.getKindOfName(kind)) == 0)
                return Collections.emptyList();
            kindsId |= kindId;
        }
        return getResources(resources, kindsId);
    }

    public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final long kinds)
    {
        return (kinds != 0) ? resources.stream().filter(resource -> (kinds & resource.getChildren()) == kinds).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final long... kinds)
    {
        long trueKinds = 0;
        for (final long kind : kinds)
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

    public static long getKindOfName(@Nonnull final String name)
    {
        return nameToKind.get(name);
    }

    public static String getNameOfKind(final long kind)
    {
        return kindToName.get(kind);
    }

    public static boolean kindExists(@Nonnull final String name)
    {
        return nameToKind.containsKey(name);
    }

    public static boolean kindExists(@Nonnull final String... names)
    {
        for (final String name : names)
            if (!nameToKind.containsKey(name))
                return false;
        return true;
    }
}