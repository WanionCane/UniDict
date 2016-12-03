package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Sets;
import gnu.trove.list.TIntList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;
import wanion.lib.common.Dependencies;
import wanion.lib.common.MetaItem;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Reference;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public final class UniResourceHandler
{
	private static final TLongSet kindBlackSet = new TLongHashSet();
	private static boolean hasInit;
	private final Map<String, Resource> apiResourceMap = new THashMap<>();
	private final Map<String, Resource> resourceMap = new THashMap<>();
	private final Dependencies<UniDict.IDependency> dependencies = UniDict.getDependencies();
	private final Config config = UniDict.getConfig();

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
	}

	public static UniResourceHandler create()
	{
		if (hasInit)
			return null;
		else
			hasInit = true;
		return new UniResourceHandler();
	}

	static TLongSet getKindBlackSet()
	{
		if (kindBlackSet.isEmpty())
			UniDict.getConfig().hideInJEIBlackSet.forEach(blackKind -> kindBlackSet.add(Resource.getKindOfName(blackKind)));
		return kindBlackSet;
	}

	public void init()
	{
		registerCustomEntries();
		createResources();
	}

	private void registerCustomEntries()
	{
		config.userRegisteredOreDictEntries.forEach(customEntries -> {
			final int plusSeparator = customEntries.indexOf('+');
			if (plusSeparator != -1 && plusSeparator > 0) {
				final String itemName = customEntries.substring(plusSeparator + 1, customEntries.length());
				final int separatorChar = itemName.indexOf('#');
				final Item item = MetaItem.itemRegistry.getObject(new ResourceLocation(separatorChar == -1 ? itemName : itemName.substring(0, separatorChar)));
				if (item != null) {
					try {
						final int metadata = separatorChar == -1 ? 0 : Integer.parseInt(itemName.substring(separatorChar + 1, itemName.length()));
						OreDictionary.registerOre(customEntries.substring(0, plusSeparator), new ItemStack(item, 1, metadata));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void createResources()
	{
		final List<String> allTheResourceNames = Collections.synchronizedList(new ArrayList<>());
		final Pattern resourceBlackTagsPattern = Pattern.compile(".*(?i)(Dense|Nether|Dye|Glass|Tiny|Small|ore).*");
		UniOreDictionary.getThoseThatMatches("^ingot").parallelStream().filter(matcher -> !resourceBlackTagsPattern.matcher(matcher.replaceFirst("")).find()).parallel().forEach(matcher -> allTheResourceNames.add(WordUtils.capitalize(matcher.replaceFirst(""))));
		final StringBuilder patternBuilder = new StringBuilder("(");
		for (final Iterator<String> allTheResourceNamesIterator = allTheResourceNames.iterator(); allTheResourceNamesIterator.hasNext(); )
			patternBuilder.append(allTheResourceNamesIterator.next()).append(allTheResourceNamesIterator.hasNext() ? "|" : ")$");
		final Map<String, Set<String>> basicResourceMap = new HashMap<>();
		final Set<String> allTheKinds = new LinkedHashSet<>();
		final Set<String> allTheKindsBlackSet = Sets.newHashSet("stair", "bars", "fence", "trapdoor", "stairs", "bucketLiquid", "slab", "crystal", "stick", "orePoor", "oreChargedCertus", "slabNether", "bucketDust", "oreCoralium", "gem", "sapling", "pulp", "item", "stone", "wood", "crop", "bottleLiquid", "quartz", "log", "mana", "chest", "crafter", "material", "leaves", "oreCertus", "crystalSHard", "eternalLife", "blockPrismarine", "door", "bells", "arrow", "itemCompressed", "enlightenedFused", "darkFused", "crystalShard", "food", "hardened");
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
		final File kindDebugFile = config.kindDebugMode ? new File("." + Reference.SLASH + "logs" + Reference.SLASH + "kindDebugLog.txt") : null;
		if (kindDebugFile != null && !kindDebugFile.exists()) {
			try (final BufferedWriter bw = new BufferedWriter(new FileWriter(kindDebugFile))) {
				allTheKinds.forEach(kind -> {
					try {
						bw.write(kind);
						bw.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		basicResourceMap.forEach((resourceName, kinds) -> {
			final TIntObjectHashMap<UniResourceContainer> kindMap = new TIntObjectHashMap<>();
			kinds.forEach(kindName -> {
				final int kind = Resource.getKindOfName(kindName);
				kindMap.put(kind, new UniResourceContainer(kindName + resourceName, kind));
			});
			apiResourceMap.put(resourceName, new Resource(resourceName, kindMap));
		});
		if (!config.libraryMode) {
			final TIntList kindList = Resource.kindNamesToKindList(config.childrenOfMetals.toArray(new String[config.childrenOfMetals.size()]));
			config.metalsToUnify.stream().filter(apiResourceMap::containsKey).forEach(resourceName -> resourceMap.put(resourceName, apiResourceMap.get(resourceName).filteredClone(kindList).setSortOfChildren(true)));
			if (!config.customUnifiedResources.isEmpty()) {
				config.customUnifiedResources.forEach((resourceName, kinds) -> {
					final Resource customResource = resourceMap.containsKey(resourceName) ? resourceMap.get(resourceName) : new Resource(resourceName);
					kinds.forEach(kindName -> {
						final String oreDictName = kindName + resourceName;
						if (OreDictionary.doesOreNameExist(oreDictName))
							customResource.addChild(new UniResourceContainer(oreDictName, Resource.registerAndGet(kindName), true));
					});
					if (!resourceMap.containsKey(resourceName) && customResource.getChildrenCount() != 0)
						resourceMap.put(resourceName, customResource);
				});
			}
		}
		config.saveIfHasChanged();
	}

	public void postInit()
	{
		apiResourceMap.values().parallelStream().forEach(Resource::updateEntries);
		if (!config.libraryMode) {
			Resource customResource;
			for (String customEntry : config.customUnifiedResources.keySet())
				if ((customResource = resourceMap.get(customEntry)) != null)
					customResource.updateEntries();
			if (config.keepOneEntry)
				OreDictionary.rebakeMap();
			final ResourceHandler resourceHandler = dependencies.get(ResourceHandler.class);
			resourceHandler.populateIndividualStackAttributes();
		}
		for (final String blackListedResource : config.resourceBlackList) {
			resourceMap.remove(blackListedResource);
			apiResourceMap.remove(blackListedResource);
		}
	}
}