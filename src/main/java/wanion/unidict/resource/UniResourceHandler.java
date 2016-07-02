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
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Dependencies;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;

import java.util.*;
import java.util.regex.Matcher;

public final class UniResourceHandler
{
    private static boolean hasInit;
    private final Map<String, Resource> apiResourceMap = new THashMap<>();
    private final Map<String, Resource> resourceMap = new THashMap<>();
    private final Dependencies<UniDict.IDependence> dependencies = UniDict.getDependencies();

    private UniResourceHandler()
    {
        dependencies.subscribe(dependencies.new DependenceWatcher<ResourceHandler>()
        {
            @Override
            public ResourceHandler instantiate()
            {
                return new ResourceHandler(Collections.unmodifiableMap(resourceMap));
            }
        });
        dependencies.subscribe(dependencies.new DependenceWatcher<UniDictAPI>()
        {
            @Override
            public UniDictAPI instantiate()
            {
                return new UniDictAPI(Collections.unmodifiableMap(apiResourceMap));
            }
        });
    }

    public static UniResourceHandler create()
    {
        if (hasInit)
            return null;
        else
            hasInit = true;
        return new UniResourceHandler();
    }

    public void preInit()
    {
        if (Config.tinkersConstruct) {
            for (String metal : new String[]{"Iron", "Gold", "Copper", "Tin", "Aluminum"})
                UniOreDictionary.removeFromElsewhere("oreberry" + metal);
            try {
                Util.setField(Class.forName("tconstruct.util.config.PHConstruct"), "tconComesFirst", null, false);
            } catch (ClassNotFoundException e) { LogHelper.info(e); }
        }
    }

    public void init()
    {
        createResources();
    }

    private void createResources()
    {
        Set<String> kinds = Config.childrenOfMetals;
        if (kinds.isEmpty())
            throw new RuntimeException();
        if (Config.enableSpecificKindSort) {
            for (String kind : kinds)
                new SpecificKindItemStackComparator(Resource.registerKind(kind));
            Config.saveIfHasChanged();
        } else
            for (String kind : kinds)
                Resource.registerKind(kind);
        Map<String, Set<String>> basicResourceMap = new THashMap<>();
        StringBuilder patternBaseString = new StringBuilder("^(");
        for (Iterator<String> kindIterator = kinds.iterator(); kindIterator.hasNext(); )
            patternBaseString.append(kindIterator.next()).append((kindIterator.hasNext()) ? "|" : ")");
        for (Matcher matcher : UniOreDictionary.getThoseThatMatches(patternBaseString.toString())) {
            String resourceName = WordUtils.capitalize(matcher.replaceFirst(""));
            if (!basicResourceMap.containsKey(resourceName))
                basicResourceMap.put(resourceName, new LinkedHashSet<String>());
            basicResourceMap.get(resourceName).add(matcher.group());
        }
        List<String> blackList = Arrays.asList("ingot", "dust");
        List<String> secondaryBlackList = Arrays.asList("dustTiny", "dust");
        for (String resourceName : basicResourceMap.keySet()) {
            Set<String> kindSet = basicResourceMap.get(resourceName);
            if (!(kindSet.containsAll(blackList) || kindSet.containsAll(secondaryBlackList)))
                continue;
            boolean sort = Config.metalsToUnify.contains(resourceName);
            TIntObjectMap<UniResourceContainer> childrenOfThisResource = new TIntObjectHashMap<>();
            int kind;
            for (String kindName : kindSet)
                childrenOfThisResource.put(kind = Resource.getKindOfName(kindName), new UniResourceContainer(kindName + resourceName, kind, sort));
            Resource resource = new Resource(resourceName, childrenOfThisResource);
            apiResourceMap.put(resourceName, resource);
            if (sort)
                resourceMap.put(resourceName, resource);
        }
        if (Config.customUnifiedResources.isEmpty())
            return;
        final List<String> gemRequires = Arrays.asList("nugget", "block");
        for (Map.Entry<String, Set<String>> customEntries : Config.customUnifiedResources.entrySet()) {
            final String resourceName = customEntries.getKey();
            final TIntObjectMap<UniResourceContainer> childrenOfCustomResource = new TIntObjectHashMap<>();
            final Set<String> customEntriesKindSet = customEntries.getValue();
            if (customEntriesKindSet.contains("gem"))
                customEntriesKindSet.addAll(gemRequires);
            int kind;
            for (String kindName : customEntriesKindSet) {
                final String oreDictName = kindName + resourceName;
                if (!OreDictionary.doesOreNameExist(oreDictName))
                    continue;
                if ((kind = Resource.getKindOfName(kindName)) == 0)
                    kind = Resource.registerKind(kindName);
                childrenOfCustomResource.put(kind, new UniResourceContainer(oreDictName, kind, true));
            }
            if (!childrenOfCustomResource.isEmpty())
                resourceMap.put(resourceName, new Resource(resourceName, childrenOfCustomResource));
        }
    }

    public void postInit()
    {
        updateEverything();
        ResourceHandler resourceHandler = dependencies.get(ResourceHandler.class);
        Resource customResource;
        for (String customEntry : Config.customUnifiedResources.keySet())
            if ((customResource = resourceMap.get(customEntry)) != null)
                customResource.updateEntries();
        resourceHandler.populateIndividualStackAttributes();
        for (String blackListedResource : Config.resourceBlackList)
        {
            resourceMap.remove(blackListedResource);
            apiResourceMap.remove(blackListedResource);
        }
        cleanEverything();
        if (Config.enableSpecificKindSort)
            SpecificKindItemStackComparator.nullify(this);
    }

    private void updateEverything()
    {
        for (Resource resource : apiResourceMap.values())
            resource.updateEntries();
    }

    private void cleanEverything()
    {
        hideInNEI();
        keepOneEntry();
    }

    private void hideInNEI()
    {
        if (!Config.autoHideInNEI)
            return;
        if (!Config.keepOneEntry) {
            TIntSet blackSet = new TIntHashSet();
            for (String blackThing : Config.hideInNEIBlackSet)
                blackSet.add(Resource.getKindOfName(blackThing));
            for (Resource resource : resourceMap.values()) {
                TIntObjectMap<UniResourceContainer> childrenMap = resource.getChildrenMap();
                for (int kind : childrenMap.keys())
                    if (!blackSet.contains(kind))
                        childrenMap.get(kind).removeBadEntriesFromNEI();
            }
        } else
            for (Resource resource : resourceMap.values())
                for (UniResourceContainer container : resource.getChildrenCollection())
                    container.removeBadEntriesFromNEI();
    }

    private void keepOneEntry()
    {
        if (!Config.keepOneEntry)
            return;
        for (Resource resource : resourceMap.values())
            for (UniResourceContainer container : resource.getChildrenCollection())
                container.keepOneEntry();
        OreDictionary.rebakeMap();
    }
}