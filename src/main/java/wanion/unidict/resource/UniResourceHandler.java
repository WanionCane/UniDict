package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Sets;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Dependencies;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;

public final class UniResourceHandler
{
    private static final TLongSet kindBlackSet = new TLongHashSet();
    private static boolean hasInit;
    private final Map<String, Resource> apiResourceMap = new THashMap<>();
    private final Map<String, Resource> resourceMap = new THashMap<>();
    private final Dependencies<UniDict.IDependence> dependencies = UniDict.getDependencies();
    private final long childrenOfMetals;

    private UniResourceHandler()
    {
        dependencies.subscribe(dependencies.new DependenceWatcher<UniDictAPI>()
        {
            @Override
            @Nonnull
            public UniDictAPI instantiate()
            {
                return new UniDictAPI(Collections.unmodifiableMap(apiResourceMap));
            }
        });
        dependencies.subscribe(dependencies.new DependenceWatcher<ResourceHandler>()
        {
            @Override
            @Nonnull
            public ResourceHandler instantiate()
            {
                return new ResourceHandler(Collections.unmodifiableMap(resourceMap));
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
        final List<String> allTheResourceNames = Collections.synchronizedList(new ArrayList<>());
        final Pattern resourceBlackTagsPattern = Pattern.compile(".*(?i)(Dense|Nether|Dye|Glass|Tiny|Small).*");
        UniOreDictionary.getThoseThatMatches("^ingot").parallelStream().filter(matcher -> !resourceBlackTagsPattern.matcher(matcher.replaceFirst("")).find()).parallel().forEach(matcher -> allTheResourceNames.add(WordUtils.capitalize(matcher.replaceFirst(""))));
        final StringBuilder patternBuilder = new StringBuilder("(");
        for (final Iterator<String> allTheResourceNamesIterator = allTheResourceNames.iterator(); allTheResourceNamesIterator.hasNext(); )
            patternBuilder.append(allTheResourceNamesIterator.next()).append(allTheResourceNamesIterator.hasNext() ? "|" : ")$");
        final Map<String, Set<String>> basicResourceMap = new HashMap<>();
        final Set<String> allTheKinds = new LinkedHashSet<>();
        final Set<String> allTheKindsBlackSet = Sets.newHashSet("stair", "bars", "fence", "trapdoor", "stairs", "bucketLiquid", "slab", "crystal", "stick", "orePoor", "oreChargedCertus", "slabNether", "bucketDust", "oreCoralium", "gem", "sapling", "pulp", "item", "stone", "wood", "crop", "bottleLiquid", "quartz", "log", "mana", "chest", "crafter", "material", "leaves", "oreCertus", "crystalSHard", "eternalLife", "blockPrismarine", "Door", "Bells", "Arrow");
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
        allTheKinds.forEach(Resource::register);
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
                final Resource customResource = resourceMap.containsKey(resourceName) ? resourceMap.get(resourceName) : new Resource(resourceName);
                kinds.forEach(kindName -> {
                    final String oreDictName = kindName + resourceName;
                    if (OreDictionary.doesOreNameExist(oreDictName))
                        customResource.addChild(new UniResourceContainer(oreDictName, Resource.registerAndGet(kindName)).setSortAndGet(true));
                });
                if (!resourceMap.containsKey(resourceName) && customResource.getChildren() != 0)
                    resourceMap.put(resourceName, customResource);
            });
        }
        Config.saveIfHasChanged();
    }

    public void postInit()
    {
        apiResourceMap.values().parallelStream().forEach(Resource::updateEntries);
        Resource customResource;
        for (String customEntry : Config.customUnifiedResources.keySet())
            if ((customResource = resourceMap.get(customEntry)) != null)
                customResource.updateEntries();
        if (Config.keepOneEntry)
            OreDictionary.rebakeMap();
        final ResourceHandler resourceHandler = dependencies.get(ResourceHandler.class);
        resourceHandler.populateIndividualStackAttributes();
        for (final String blackListedResource : Config.resourceBlackList) {
            resourceMap.remove(blackListedResource);
            apiResourceMap.remove(blackListedResource);
        }
    }

    static TLongSet getKindBlackSet()
    {
        if (kindBlackSet.isEmpty())
            Config.hideInJEIBlackSet.forEach(blackKind -> kindBlackSet.add(Resource.getKindOfName(blackKind)));
        return kindBlackSet;
    }
}