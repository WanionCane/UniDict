package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Resource
{
	private static final TObjectIntMap<String> nameToKind = new TObjectIntHashMap<>();
	private static final TIntObjectMap<String> kindToName = new TIntObjectHashMap<>();
	private static int totalKindsRegistered = 0;
	public final String name;
	private final TIntObjectMap<UniResourceContainer> childrenMap = new TIntObjectHashMap<>();
	private final List<Resource> copies = new ArrayList<>();
	private TIntSet children = new TIntHashSet();
	private boolean updated;

	public Resource(@Nonnull final String name)
	{
		this.name = name;
	}

	public Resource(@Nonnull final String name, @Nonnull final TIntObjectMap<UniResourceContainer> containerMap)
	{
		this.name = name;
		containerMap.forEachValue(container -> childrenMap.put(container.kind, container) == null);
	}

	public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final String... kinds)
	{
		final TIntList kindList = new TIntArrayList();
		for (final String kindName : kinds) {
			final int kind;
			if ((kind = Resource.getKindOfName(kindName)) == 0)
				return Collections.emptyList();
			kindList.add(kind);
		}
		return getResources(resources, kindList);
	}

	public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final TIntList kinds)
	{
		return (kinds.size() > 0) ? resources.stream().filter(resource -> (resource.childrenExists(kinds))).collect(Collectors.toList()) : Collections.emptyList();
	}

	public static List<Resource> getResources(@Nonnull final Collection<Resource> resources, final int... kinds)
	{
		final TIntList kindList = new TIntArrayList();
		for (final int kind : kinds)
			if (kind != 0)
				kindList.add(kind);
			else
				return Collections.emptyList();
		return getResources(resources, kindList);
	}

	public static List<String> getKinds()
	{
		return Collections.unmodifiableList(new ArrayList<>(nameToKind.keySet()));
	}

	public static int getKindOfName(@Nonnull final String name)
	{
		return nameToKind.get(name);
	}

	public static String getNameOfKind(final int kind)
	{
		return kindToName.get(kind);
	}

	public static TIntList kindNamesToKindList(@Nonnull final String[] kindNames)
	{
		int bufferKind;
		final TIntList kindList = new TIntArrayList();
		for (final String kindName : kindNames)
			if ((bufferKind = getKindOfName(kindName)) != 0)
				kindList.add(bufferKind);
		return kindList;
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

	public static int registerAndGet(@Nonnull final String kindName)
	{
		if (nameToKind.containsKey(kindName))
			return nameToKind.get(kindName);
		final int kind = ++totalKindsRegistered;
		nameToKind.put(kindName, kind);
		kindToName.put(kind, kindName);
		return kind;
	}

	public static void register(@Nonnull final String kindName)
	{
		if (nameToKind.containsKey(kindName))
			return;
		final int kind = ++totalKindsRegistered;
		nameToKind.put(kindName, kind);
		kindToName.put(kind, kindName);
	}

	public boolean childExists(final int kind)
	{
		return childrenMap.containsKey(kind);
	}

	public boolean childrenExists(final TIntList kindList)
	{
		return childrenMap.keySet().containsAll(kindList);
	}

	public UniResourceContainer getChild(@Nonnull final String childName)
	{
		return childrenMap.get(nameToKind.get(childName));
	}

	public UniResourceContainer getChild(final int kind)
	{
		return childrenMap.get(kind);
	}

	public int getChildrenCount()
	{
		return childrenMap.size();
	}

	public Resource filteredClone(final TIntList kindList)
	{
		final TIntObjectMap<UniResourceContainer> newChildrenMap = new TIntObjectHashMap<>();
		final TIntSet kindSet = new TIntHashSet(kindList);
		childrenMap.forEachEntry((child, container) -> {
			if (kindSet.contains(child))
				newChildrenMap.put(child, container);
			return true;
		});
		final Resource copiedResource = new Resource(name, newChildrenMap);
		copies.add(copiedResource);
		return copiedResource;
	}

	public boolean addChild(@Nonnull final UniResourceContainer child)
	{
		final long kind = child.kind;
		if (childrenMap.containsKey(child.kind) || !child.name.endsWith(name))
			return false;
		childrenMap.put(child.kind, child);
		return true;
	}

	public void updateEntries()
	{
		if (updated)
			return;
		else
			updated = true;
		for (final TIntIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext(); ) {
			final int kindId = childrenIterator.next();
			if (childrenMap.get(kindId).updateEntries())
				continue;
			childrenIterator.remove();
		}
		copies.forEach(Resource::updateEntries);
	}

	public Collection<UniResourceContainer> getChildrenCollection()
	{
		return childrenMap.valueCollection();
	}

	@Override
	public String toString()
	{
		if (childrenMap.isEmpty())
			return name + " = {}";
		final StringBuilder output = new StringBuilder(name + " = {");
		for (final TIntIterator childrenIterator = childrenMap.keySet().iterator(); childrenIterator.hasNext(); )
			output.append(kindToName.get(childrenIterator.next())).append((childrenIterator.hasNext()) ? ", " : "}");
		return output.toString();
	}

	TIntObjectMap<UniResourceContainer> getChildrenMap()
	{
		return new TUnmodifiableIntObjectMap<>(childrenMap);
	}

	Resource setSortOfChildren(final boolean sort)
	{
		childrenMap.forEachValue(child -> {
			child.setSort(sort);
			return true;
		});
		return this;
	}
}