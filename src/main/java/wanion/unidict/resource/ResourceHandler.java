package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict.IDependence;

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

    ResourceHandler(Map<String, Resource> resourceMap)
    {
        resources = (this.resourceMap = resourceMap).values();
    }

    public static void addToKeepOneEntryModBlackSet(ItemStack itemStack)
    {
        keepOneEntryBlackSet.add(itemStack);
    }

    public boolean exists(int thingId)
    {
        return individualStackAttributes.containsKey(thingId);
    }

    public boolean exists(ItemStack thing)
    {
        return individualStackAttributes.containsKey(MetaItem.get(thing));
    }

    public boolean resourceExists(String name)
    {
        return resourceMap.containsKey(name);
    }

    private UniAttributes get(ItemStack thing)
    {
        return individualStackAttributes.get(MetaItem.get(thing));
    }

    public Resource getResource(String resourceName)
    {
        return resourceMap.get(resourceName);
    }

    public Resource getResource(ItemStack thing)
    {
        UniAttributes attributesOfThing = get(thing);
        return (attributesOfThing != null) ? attributesOfThing.resource : null;
    }

    public UniResourceContainer getContainer(String name)
    {
        return containerMap.get(name);
    }

    public UniResourceContainer getContainer(ItemStack thing)
    {
        UniAttributes attributesOfThing = get(thing);
        return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer : null;
    }

    public String getContainerName(ItemStack thing)
    {
        UniAttributes attributesOfThing = get(thing);
        return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer.name : null;
    }

    public ItemStack getMainItemStack(ItemStack thing)
    {
        UniAttributes attributesOfThing = get(thing);
        return (attributesOfThing != null) ? attributesOfThing.uniResourceContainer.getMainEntry(thing.stackSize) : thing;
    }

    public List<ItemStack> getMainItemStackList(Collection<ItemStack> things)
    {
        return things.stream().map(this::getMainItemStack).collect(Collectors.toList());
    }

    public void setMainItemStacks(List<ItemStack> thingList)
    {
        List<ItemStack> newThings = new ArrayList<>();
        for (Iterator<ItemStack> thingListIterator = thingList.iterator(); thingListIterator.hasNext(); thingListIterator.remove())
            newThings.add(getMainItemStack(thingListIterator.next()));
        thingList.addAll(newThings);
    }

    public ItemStack[] getMainItemStacks(ItemStack[] things)
    {
        for (int i = 0; i < things.length; i++)
            things[i] = getMainItemStack(things[i]);
        return things;
    }

    public boolean containerExists(String name)
    {
        return containerMap.containsKey(name);
    }

    public List<Resource> getResources(long kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    public List<Resource> getResources(long... kinds)
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