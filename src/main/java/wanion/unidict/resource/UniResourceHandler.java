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
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;
import wanion.lib.common.Dependencies;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.api.UniDictAPI;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static wanion.unidict.common.Reference.MOD_ID;
import static wanion.unidict.common.Reference.SLASH;

public final class UniResourceHandler
{
	private static final TIntSet kindJEIBlackSet = new TIntHashSet();
	private final Map<String, Resource> apiResourceMap = new THashMap<>();
	private final Map<String, Resource> resourceMap = new THashMap<>();
	private final Dependencies<UniDict.IDependency> dependencies = UniDict.getDependencies();
	private final Config config = UniDict.getConfig();

	public UniResourceHandler() {}

	public void preInit()
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

	static synchronized TIntSet getKindJEIBlackSet()
	{
		if (kindJEIBlackSet.isEmpty())
			UniDict.getConfig().hideInJEIKindBlackSet.forEach(blackKind -> kindJEIBlackSet.add(Resource.getKindFromName(blackKind)));
		return kindJEIBlackSet;
	}

	static Set<String> getEntryJEIBlackSet()
	{
		return UniDict.getConfig().hideInJEIEntryBlackSet;
	}

	public void init(final FMLInitializationEvent event)
	{
		customEntries();
		gatherResources();
		createAdditionalFiles();
		updateEntries(event);
		createDumps();
	}

	private void customEntries()
	{
		config.userOreDictEntries.forEach(customEntries -> {
			final int plusSeparator = customEntries.indexOf('+');
			final int minusSeparator = customEntries.indexOf('-');
			if (plusSeparator > 0) {
				final String itemName = customEntries.substring(plusSeparator + 1);
				final int separatorChar = itemName.indexOf('#');
				final Item item = Item.REGISTRY.getObject(new ResourceLocation(separatorChar == -1 ? itemName : itemName.substring(0, separatorChar)));
				if (item != null) {
					try {
						final int metadata = separatorChar == -1 ? 0 : Integer.parseInt(itemName.substring(separatorChar + 1, itemName.length()));
						OreDictionary.registerOre(customEntries.substring(0, plusSeparator), new ItemStack(item, 1, metadata));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			} else if (minusSeparator > 0) {
				final String oreName = customEntries.substring(0, minusSeparator);
				final List<ItemStack> oreList = UniOreDictionary.get(oreName);
				if (oreList != null) {
					final String itemName = customEntries.substring(minusSeparator + 1);
					final int separatorChar = itemName.indexOf('#');
					final Item item = Item.REGISTRY.getObject(new ResourceLocation(separatorChar == -1 ? itemName : itemName.substring(0, separatorChar)));
					if (item != null) {
						final int metaData = separatorChar == -1 ? 0 : Integer.parseInt(itemName.substring(separatorChar + 1, itemName.length()));
						final ItemStack itemStack = new ItemStack(item, 1, metaData);
						boolean found = false;
						for (final Iterator<ItemStack> itemStackIterator = oreList.iterator(); !found && itemStackIterator.hasNext(); )
							if (found = itemStack.isItemEqual(itemStackIterator.next()))
								itemStackIterator.remove();
					}
				}
			}
		});
	}

	private void gatherResources()
	{
		final List<String> allTheResourceNames = Collections.synchronizedList(new ArrayList<>());
		final Pattern resourceBlackTagsPattern = Pattern.compile(".*(?i)(Brick|Dense|Nether|Dye|Glass|Tiny|Small|Slime|Coralium|Fuel|Certus|ChargedCertus|ore|Redstone).*");
		UniOreDictionary.getThoseThatMatches("^ingot").parallelStream().filter(matcher -> !resourceBlackTagsPattern.matcher(matcher.replaceFirst("")).find()).sequential().forEach(matcher -> allTheResourceNames.add(WordUtils.capitalize(matcher.replaceFirst(""))));
		final StringBuilder patternBuilder = new StringBuilder("(");
		for (final Iterator<String> allTheResourceNamesIterator = allTheResourceNames.iterator(); allTheResourceNamesIterator.hasNext(); )
			patternBuilder.append(allTheResourceNamesIterator.next()).append(allTheResourceNamesIterator.hasNext() ? "|" : ")$");
		final Map<String, Set<String>> basicResourceMap = new HashMap<>();
		final Set<String> allTheKinds = new LinkedHashSet<>();
		final Set<String> allTheKindsBlackSet = Sets.newHashSet("blockFuel", "blockPrudentium", "trapdoor", "oreChargedCertus", "slabNether", "bucketDust", "oreCoralium", "sapling", "pulp", "item", "stone", "wood", "bottleLiquid", "quartz", "mana", "crafter", "material", "leaves", "oreCertus", "crystalSHard", "eternalLife", "blockPrismarine", "bells", "enlightenedFused", "darkFused", "crystalShard", "food", "hardened", "blockPsi", "blockStainedHardened", "rubber", "scaffoldingTreated", "fenceGate", "oc:stone", "calculatorReinforced");
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
			final TIntObjectHashMap<UniResourceContainer> kindMap = new TIntObjectHashMap<>();
			kinds.forEach(kindName -> {
				final int kind = Resource.getKindFromName(kindName);
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

	private void createAdditionalFiles()
	{
		if (!config.enableSpecificEntrySort && !config.enableSpecificKindSort)
			return;
		final File jsonFormatGuideFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "jsonFormatGuide.txt");
		if (jsonFormatGuideFile.exists())
			return;
		try {
			if (!jsonFormatGuideFile.createNewFile()) {
				UniDict.getLogger().error("UniDict couldn't create the jsonFormatGuide.txt file.");
				return;
			}
			try (final BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFormatGuideFile))) {
				bw.write("In order to use specificEntrySorting.json you must use the following format:");
				bw.newLine();
				bw.newLine();
				bw.write("[");
				bw.newLine();
				bw.write("\t{");
				bw.newLine();
				bw.write("\t\t\"entryName\" : \"oreCopper\",");
				bw.newLine();
				bw.write("\t\t\"modIdPriorityList\" :");
				bw.newLine();
				bw.write("\t\t[");
				bw.newLine();
				bw.write("\t\t\t\"minecraft\",");
				bw.newLine();
				bw.write("\t\t\t\"thermalfoundation\",");
				bw.newLine();
				bw.write("\t\t\t\"substratum\",");
				bw.newLine();
				bw.write("\t\t\t\"ic2\",");
				bw.newLine();
				bw.write("\t\t\t\"mekanism\",");
				bw.newLine();
				bw.write("\t\t\t\"immersiveengineering\",");
				bw.newLine();
				bw.write("\t\t\t\"techreborn\"");
				bw.newLine();
				bw.write("\t\t]");
				bw.newLine();
				bw.write("\t},");
				bw.newLine();
				bw.write("\t{");
				bw.newLine();
				bw.write("\t\t\"entryName\" : \"oreIron\",");
				bw.newLine();
				bw.write("\t\t\"modIdPriorityList\" :");
				bw.newLine();
				bw.write("\t\t[");
				bw.newLine();
				bw.write("\t\t\t\"minecraft\",");
				bw.newLine();
				bw.write("\t\t\t\"thermalfoundation\",");
				bw.newLine();
				bw.write("\t\t\t\"substratum\",");
				bw.newLine();
				bw.write("\t\t\t\"ic2\",");
				bw.newLine();
				bw.write("\t\t\t\"mekanism\",");
				bw.newLine();
				bw.write("\t\t\t\"immersiveengineering\",");
				bw.newLine();
				bw.write("\t\t\t\"techreborn\"");
				bw.newLine();
				bw.write("\t\t]");
				bw.newLine();
				bw.write("\t}");
				bw.newLine();
				bw.write("]");
				bw.newLine();
				bw.newLine();
				bw.write("In order to use specificKindSorting.json you must use the following format:");
				bw.newLine();
				bw.newLine();
				bw.write("[");
				bw.newLine();
				bw.write("\t{");
				bw.newLine();
				bw.write("\t\t\"kindName\" : \"ore\",");
				bw.newLine();
				bw.write("\t\t\"modIdPriorityList\" :");
				bw.newLine();
				bw.write("\t\t[");
				bw.newLine();
				bw.write("\t\t\t\"minecraft\",");
				bw.newLine();
				bw.write("\t\t\t\"thermalfoundation\",");
				bw.newLine();
				bw.write("\t\t\t\"substratum\",");
				bw.newLine();
				bw.write("\t\t\t\"ic2\",");
				bw.newLine();
				bw.write("\t\t\t\"mekanism\",");
				bw.newLine();
				bw.write("\t\t\t\"immersiveengineering\",");
				bw.newLine();
				bw.write("\t\t\t\"techreborn\"");
				bw.newLine();
				bw.write("\t\t]");
				bw.newLine();
				bw.write("\t},");
				bw.newLine();
				bw.write("\t{");
				bw.newLine();
				bw.write("\t\t\"kindName\" : \"ingot\",");
				bw.newLine();
				bw.write("\t\t\"modIdPriorityList\" :");
				bw.newLine();
				bw.write("\t\t[");
				bw.newLine();
				bw.write("\t\t\t\"minecraft\",");
				bw.newLine();
				bw.write("\t\t\t\"thermalfoundation\",");
				bw.newLine();
				bw.write("\t\t\t\"substratum\",");
				bw.newLine();
				bw.write("\t\t\t\"ic2\",");
				bw.newLine();
				bw.write("\t\t\t\"mekanism\",");
				bw.newLine();
				bw.write("\t\t\t\"immersiveengineering\",");
				bw.newLine();
				bw.write("\t\t\t\"techreborn\"");
				bw.newLine();
				bw.write("\t\t]");
				bw.newLine();
				bw.write("\t}");
				bw.newLine();
				bw.write("]");
				bw.newLine();
				bw.newLine();
				bw.write("NOTE: specific Entry Sorting always will have a higher priority over specific Kind Sorting.");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// we may need this on the future, who knows?
	public void postInit(final FMLPostInitializationEvent event) {}

	private void updateEntries(final FMLStateEvent event)
	{
		apiResourceMap.values().forEach(Resource::updateEntries);
		if (!config.libraryMode) {
			Resource customResource;
			for (String customEntry : config.customUnifiedResources.keySet())
				if ((customResource = resourceMap.get(customEntry)) != null)
					customResource.updateEntries();
			if (config.keepOneEntry)
				OreDictionary.rebakeMap();
			dependencies.get(ResourceHandler.class).populateIndividualStackAttributes(event);
		}
		for (final String blackListedResource : config.resourceBlackList) {
			resourceMap.remove(blackListedResource);
			apiResourceMap.remove(blackListedResource);
		}
	}

	private void createDumps()
	{
		if (config.kindsDump || config.entriesDump || config.unifiedEntriesDump) {
			final File dumpFolder = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "dump");
			if (!dumpFolder.exists())
				if (dumpFolder.mkdirs())
					UniDict.getLogger().error("UniDict wasn't able to create the dump folder.");
			if (dumpFolder.exists()) {
				if (config.kindsDump) {
					final File kindDumpFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "dump" + SLASH + "kindsDump.txt");
					try {
						if (kindDumpFile.createNewFile()) {
							try (final BufferedWriter bw = new BufferedWriter(new FileWriter(kindDumpFile))) {
								Resource.getKinds().forEach(kind -> {
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (config.entriesDump) {
					final File entriesDumpFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "dump" + SLASH + "entriesDump.txt");
					try {
						if (entriesDumpFile.createNewFile()) {
							try (final BufferedWriter bw = new BufferedWriter(new FileWriter(entriesDumpFile))) {
								apiResourceMap.values().forEach(resource -> {
									try {
										bw.write(resource.name);
										bw.newLine();
										resource.getChildrenCollection().forEach(children -> {
											try {
												bw.write("\t" + children.name);
												bw.newLine();
											} catch (IOException e) {
												e.printStackTrace();
											}
										});
										bw.newLine();
									} catch (IOException e) {
										e.printStackTrace();
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (config.unifiedEntriesDump) {
					final File unifiedEntriesDumpFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "dump" + SLASH + "unifiedEntriesDump.txt");
					try {
						if (unifiedEntriesDumpFile.createNewFile()) {
							try (final BufferedWriter bw = new BufferedWriter(new FileWriter(unifiedEntriesDumpFile))) {
								resourceMap.values().forEach(resource -> {
									try {
										bw.write(resource.name);
										bw.newLine();
										resource.getChildrenCollection().forEach(children -> {
											if (children.isSorted()) {
												try {
													bw.write("\t" + children.name);
													bw.newLine();
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										});
										bw.newLine();
									} catch (IOException e) {
										e.printStackTrace();
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}