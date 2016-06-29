package wanion.unidict.api;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.hash.THashMap;
import wanion.unidict.UniDict;
import wanion.unidict.resource.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class UniDictAPI implements UniDict.IDependence
{
    private final Map<String, Resource> resourceMap;
    public final Collection<Resource> resources;

    public UniDictAPI(Map<String, Resource> resourceMap)
    {
        resources = (this.resourceMap = resourceMap).values();
    }

    public Resource getResource(String resourceName)
    {
        return resourceMap.get(resourceName);
    }

    public List<Resource> getResources(String... kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    public List<Resource> getResources(int kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    public List<Resource> getResources(int... kinds)
    {
        return Resource.getResources(resources, kinds);
    }

    public static Map<String, Resource> toResourceMap(List<Resource> resources)
    {
        Map<String, Resource> resourceMap = new THashMap<>();
        for (Resource resource : resources)
            resourceMap.put(resource.name, resource);
        return resourceMap;
    }
}