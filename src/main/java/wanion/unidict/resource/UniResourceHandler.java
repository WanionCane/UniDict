package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.google.common.collect.Sets;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Dependencies;
import wanion.unidict.common.SpecificKindItemStackComparator;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;

public final class UniResourceHandler
{
    private static boolean hasInit;
    private final Map<String, Resource> apiResourceMap = new THashMap<>();
    private final Map<String, Resource> resourceMap = new THashMap<>();
    private final Dependencies<UniDict.IDependence> dependencies = UniDict.getDependencies();
    private final long childrenOfMetals;

    private UniResourceHandler()
    {
        dependencies.subscribe(dependencies.new DependenceWatcher<ResourceHandler>()
        {
            @Override
            @Nonnull
            public ResourceHandler instantiate()
            {
                return new ResourceHandler(Collections.unmodifiableMap(resourceMap));
            }
        });
        dependencies.subscribe(dependencies.new DependenceWatcher<UniDictAPI>()
        {
            @Override
            @Nonnull
            public UniDictAPI instantiate()
            {
                return new UniDictAPI(Collections.unmodifiableMap(apiResourceMap));
            }
        });
        long childrenOfMetals = 0;
        for (final String child : Config.childrenOfMetals)
            childrenOfMetals += Resource.registerAndGet(child);
        this.childrenOfMetals = childrenOfMetals;
    }

    public static UniResourceHandler create()
    {
        if (hasInit)
            return null;
        else
            hasInit = true;
        return new UniResourceHandler();
    }

    public void init()
    {
        createResources();
    }

    private void createResources()
    {
        final List<String> allTheResourceNames = new ArrayList<>();
        final Pattern resourceBlackTagsPattern = Pattern.compile(".*(?i)(Dense|Nether|Dye|Glass|Tiny|Small).*");
        UniOreDictionary.getThoseThatMatches("^ingot").stream().filter(matcher -> !resourceBlackTagsPattern.matcher(matcher.replaceFirst("")).find()).forEach(matcher -> allTheResourceNames.add(WordUtils.capitalize(matcher.replaceFirst(""))));
        final StringBuilder patternBuilder = new StringBuilder("(");
        for (final Iterator<String> allTheResourceNamesIterator = allTheResourceNames.iterator(); allTheResourceNamesIterator.hasNext(); )
            patternBuilder.append(allTheResourceNamesIterator.next()).append(allTheResourceNamesIterator.hasNext() ? "|" : ")$");
        final Map<String, Set<String>> basicResourceMap = new HashMap<>();
        final Set<String> allTheKinds = new LinkedHashSet<>();
        final Set<String> allTheKindsBlackSet = Sets.newHashSet("stair", "bars", "fence", "trapdoor", "stairs", "bucketLiquid", "slab", "crystal", "stick", "orePoor", "oreChargedCertus", "slabNether", "bucketDust", "oreCoralium", "gem", "sapling", "pulp", "item", "stone", "wood", "crop", "bottleLiquid", "quartz", "log", "mana", "chest", "crafter", "material", "leaves", "oreCertus", "crystalSHard", "eternalLife", "blockPrismarine");
        UniOreDictionary.getThoseThatMatches(Pattern.compile(patternBuilder.toString())).forEach(matcher -> {
            final String kindName = matcher.replaceFirst("");
            if (!allTheKindsBlackSet.contains(kindName)) {
                final String resourceName = matcher.group();
                if (!basicResourceMap.containsKey(resourceName))
                    basicResourceMap.put(resourceName, new LinkedHashSet<>());
                basicResourceMap.get(resourceName).add(kindName);
                allTheKinds.add(kindName);
            }
        });
        if (!Config.enableSpecificKindSort)
            allTheKinds.forEach(Resource::register);
        else
            allTheKinds.forEach(kind -> new SpecificKindItemStackComparator(Resource.registerAndGet(kind)));
        basicResourceMap.forEach((resourceName, kinds) -> {
            final TLongObjectMap<UniResourceContainer> kindMap = new TLongObjectHashMap<>();
            kinds.forEach(kindName -> {
                final long kind = Resource.getKindOfName(kindName);
                kindMap.put(kind, new UniResourceContainer(kindName + resourceName, kind));
            });
            apiResourceMap.put(resourceName, new Resource(resourceName, kindMap));
        });
        Config.metalsToUnify.stream().filter(apiResourceMap::containsKey).forEach(resourceName -> resourceMap.put(resourceName, apiResourceMap.get(resourceName).filteredClone(childrenOfMetals).setSortOfChildren(true)));
        if (!Config.customUnifiedResources.isEmpty()) {
            Config.customUnifiedResources.forEach((resourceName, kinds) -> {
                if (resourceMap.containsKey(resourceName)) {
                    kinds.forEach(kindName -> {
                        final String oreDictName = kindName + resourceName;
                        final long kind = Resource.registerAndGet(kindName);
                        if (OreDictionary.doesOreNameExist(oreDictName))
                            resourceMap.get(resourceName).addChild(new UniResourceContainer(oreDictName, kind).setSortAndGet(true));
                    });
                } else {
                    final TLongObjectMap<UniResourceContainer> childrenOfCustomResource = new TLongObjectHashMap<>();
                    kinds.forEach(kindName -> {
                        final String oreDictName = kindName + resourceName;
                        if (OreDictionary.doesOreNameExist(oreDictName)) {
                            final long kind;
                            if (!Resource.kindExists(kindName))
                                if (Config.enableSpecificKindSort)
                                    new SpecificKindItemStackComparator(kind = Resource.registerAndGet(kindName));
                                else kind = Resource.registerAndGet(kindName);
                            else
                                kind = Resource.getKindOfName(kindName);
                            if (resourceMap.containsKey(resourceName))
                                childrenOfCustomResource.put(kind, new UniResourceContainer(oreDictName, kind).setSortAndGet(true));
                            if (!childrenOfCustomResource.isEmpty())
                                resourceMap.put(resourceName, new Resource(resourceName, childrenOfCustomResource));
                        }
                    });
                }
            });
        }
        Config.saveIfHasChanged();
    }

    public void postInit()
    {
        updateEverything();
        ResourceHandler resourceHandler = dependencies.get(ResourceHandler.class);
        Resource customResource;
        for (String customEntry : Config.customUnifiedResources.keySet())
            if ((customResource = resourceMap.get(customEntry)) != null)
                customResource.updateEntries();
        if (Config.keepOneEntry)
            OreDictionary.rebakeMap();
        resourceHandler.populateIndividualStackAttributes();
        for (String blackListedResource : Config.resourceBlackList) {
            resourceMap.remove(blackListedResource);
            apiResourceMap.remove(blackListedResource);
        }
    }

    private void updateEverything()
    {
        apiResourceMap.values().forEach(Resource::updateEntries);
    }
}