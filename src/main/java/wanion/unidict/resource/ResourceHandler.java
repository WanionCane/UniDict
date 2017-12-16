package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.unidict.UniDict.IDependency;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ResourceHandler implements IDependency
{
	static final Set<ItemStack> keepOneEntryBlackSet = new HashSet<>();
	public final Collection<Resource> resources;
	private final TIntObjectMap<UniAttributes> individualStackAttributes = new TIntObjectHashMap<>();
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
		final int hash = MetaItem.get(thing);
		return individualStackAttributes.get(hash);
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

	public UniResourceContainer getContainer(@Nonnull final String resource, @Nonnull final String child)
	{
		return containerExists(resource, child) ? resourceMap.get(resource).getChild(Resource.getKindOfName(child)) : null;
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
		return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer.getMainEntry(thing) : thing;
	}

	public List<ItemStack> getMainItemStacks(@Nonnull final Collection<ItemStack> things)
	{
		return things.stream().map(this::getMainItemStack).collect(Collectors.toList());
	}

	public void setMainItemStacks(@Nonnull final List<ItemStack> thingList)
	{
		for (int i = 0; i < thingList.size(); i++)
			thingList.set(i, getMainItemStack(thingList.get(i)));
	}

	public void setMainObjects(@Nonnull final List<Object> thingList)
	{
		for (int i = 0; i < thingList.size(); i++)
			if (thingList.get(i) instanceof ItemStack)
			thingList.set(i, getMainItemStack((ItemStack) thingList.get(i)));
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

	public boolean containerExists(@Nonnull final String resource, @Nonnull final String child)
	{
		return resourceMap.containsKey(resource) && resourceMap.get(resource).childExists(Resource.getKindOfName(child));
	}

	public List<Resource> getResources(final int... kinds)
	{
		return Resource.getResources(resources, kinds);
	}

	public void populateIndividualStackAttributes()
	{
		individualStackAttributes.clear();
		resources.forEach(resource -> resource.getChildrenMap().forEachValue(container -> {
			final UniAttributes uniAttributes = new UniAttributes(resource, container);
			for (final int hash : container.getHashes())
				individualStackAttributes.put(hash, uniAttributes);
			return true;
		}));
	}
}