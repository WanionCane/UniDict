package wanion.unidict.api;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.unidict.UniDict;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// be sure to call this after init.
@SuppressWarnings("unused")
public class UniDictAPI implements UniDict.IDependency
{
	public final Collection<Resource> resources;
	private final Map<String, Resource> resourceMap;
	private ResourceHandler resourceHandler = null;

	public UniDictAPI(@Nonnull final Map<String, Resource> resourceMap)
	{
		resources = (this.resourceMap = resourceMap).values();
	}

	@Nonnull
	public static Map<String, Resource> toResourceMap(@Nonnull final Collection<Resource> resources)
	{
		return resources.stream().collect(Collectors.toMap(Resource::getName, Function.identity()));
	}

	// I (WanionCane) wouldn't recommend using this unless it is really required!
	@Nonnull
	public ResourceHandler getResourceHandler()
	{
		if (resourceHandler == null) {
			resourceHandler = new ResourceHandler(resourceMap);
			resourceHandler.populateIndividualStackAttributes();
		}
		return resourceHandler;
	}

	public Resource getResource(@Nonnull final String resourceName)
	{
		return resourceMap.get(resourceName);
	}

	public List<Resource> getResources(@Nonnull final String... kinds)
	{
		return Resource.getResources(resources, kinds);
	}

	public List<Resource> getResources(@Nonnull final int... kinds)
	{
		return Resource.getResources(resources, kinds);
	}
}