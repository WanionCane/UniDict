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

    public List<ItemStack> getMainItemStackList(final Collection<ItemStack> things)
    {
        return things.stream().map(this::getMainItemStack).collect(Collectors.toList());
    }

    public void setMainItemStacks(final List<ItemStack> thingList)
    {
        final List<ItemStack> newThings = new ArrayList<>();
        for (Iterator<ItemStack> thingListIterator = thingList.iterator(); thingListIterator.hasNext(); thingListIterator.remove())
            newThings.add(getMainItemStack(thingListIterator.next()));
        thingList.addAll(newThings);
    }

    public ItemStack[] getMainItemStacks(final ItemStack[] things)
    {
        for (int i = 0; i < things.length; i++)
            things[i] = getMainItemStack(things[i]);
        return things;
    }

    public boolean containerExists(final String name)
    {
        return containerMap.containsKey(name);
    }

    public List<Resource> getResources(final long kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    public List<Resource> getResources(final long... kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    void populateIndividualStackAttributes()
    {
        resources.forEach(resource -> resource.getChildrenMap().forEachValue(container -> {
            containerMap.put(container.name, container);
            UniAttributes uniAttributes = new UniAttributes(resource, container);
            MetaItem.populateMap(container.getEntries(), individualStackAttributes, uniAttributes);
            return true;
        }));
    }
}