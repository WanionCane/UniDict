package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict.IDependence;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ResourceHandler implements IDependence
{
	static final Set<ItemStack> keepOneEntryBlackSet = new HashSet<>();
	public final Collection<Resource> resources;
	private final TIntObjectMap<UniAttributes> individualStackAttributes = new TIntObjectHashMap<>();
	private final Map<String, UniResourceContainer> containerMap = new THashMap<>();
	private final Map<String, Resource> resourceMap;

	ResourceHandler(@Nonnull final Map<String, Resource> resourceMap)
	{
		resources = (this.resourceMap = resourceMap).values();
	}

	public static void addToKeepOneEntryModBlackSet(@Nonnull final ItemStack itemStack)
	{
		keepOneEntryBlackSet.add(itemStack);
	}

	public boolean exists(final int thingId)
	{
		return individualStackAttributes.containsKey(thingId);
	}

	public boolean exists(final ItemStack thing)
	{
		return individualStackAttributes.containsKey(MetaItem.get(thing));
	}

	public boolean resourceExists(@Nonnull final String name)
	{
		return resourceMap.containsKey(name);
	}

	private UniAttributes get(final ItemStack thing)
	{
		return individualStackAttributes.get(MetaItem.get(thing));
	}

	public Resource getResource(final String resourceName)
	{
		return resourceMap.get(resourceName);
	}

	public Resource getResource(final ItemStack thing)
	{
		final UniAttributes attributesOfThing = get(thing);
		return (attributesOfThing != null) ? attributesOfThing.resource : null;
	}

	public UniResourceContainer getContainer(final String name)
	{
		return containerMap.get(name);
	}

	public UniResourceContainer getContainer(final ItemStack thing)
	{
		final UniAttributes attributesOfThing = get(thing);
		return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer : null;
	}

	public String getContainerName(final ItemStack thing)
	{
		final UniAttributes attributesOfThing = get(thing);
		return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer.name : null;
	}

	public ItemStack getMainItemStack(final ItemStack thing)
	{
		final UniAttributes attributesOfThing = get(thing);
		return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer.getMainEntry(thing.stackSize) : thing;
	}

	public List<ItemStack> getMainItemStackList(@Nonnull final Collection<ItemStack> things)
	{
		return things.stream().map(this::getMainItemStack).collect(Collectors.toList());
	}

	public void setMainItemStacks(@Nonnull final List<ItemStack> thingList)
	{
		for (int i = 0; i < thingList.size(); i++)
			thingList.set(i, getMainItemStack(thingList.get(i)));
	}

	public ItemStack[] getMainItemStacks(@Nonnull final ItemStack[] things)
	{
		for (int i = 0; i < things.length; i++)
			things[i] = getMainItemStack(things[i]);
		return things;
	}

	public void setMainItemStacks(@Nonnull final Object[] things)
	{
		for (int i = 0; i < things.length; i++)
			if (things[i] instanceof ItemStack)
				things[i] = getMainItemStack((ItemStack) things[i]);
	}

	public boolean containerExists(@Nonnull final String name)
	{
		return containerMap.containsKey(name);
	}

	public List<Resource> getResources(final int... kinds)
	{
		return Resource.getResources(resources, kinds);
	}

	void populateIndividualStackAttributes()
	{
		resources.forEach(resource -> resource.getChildrenMap().forEachValue(container -> {
			containerMap.put(container.name, container);
			final UniAttributes uniAttributes = new UniAttributes(resource, container);
			for (final int hash : container.getHashes())
				individualStackAttributes.put(hash, uniAttributes);
			return true;
		}));
	}
}