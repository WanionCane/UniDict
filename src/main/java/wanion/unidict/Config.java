package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Sets;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import wanion.unidict.common.Reference;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class Config
{
	public static final Config INSTANCE = new Config();
	// general configs
	public final boolean libraryMode;
	public final boolean keepOneEntry;
	public final boolean registerNewCraftingIngredientsAsItemStacks;
	public final Set<String> keepOneEntryModBlackSet;
	public final Set<String> keepOneEntryKindBlackSet;
	public final Set<String> keepOneEntryEntryBlackSet;
	public final List<String> itemStacksNamesToIgnore;
	public final List<ItemStack> itemStacksToIgnore = new ArrayList<>();
	public final boolean keepOneEntryBlackListsAsWhiteLists;
	public final boolean autoHideInJEI;
	public final Set<String> hideInJEIKindBlackSet;
	public final Set<String> hideInJEIEntryBlackSet;
	// dumps
	public final boolean kindsDump;
	public final boolean entriesDump;
	public final boolean unifiedEntriesDump;
	// input replacement
	public final boolean inputReplacementFurnace;
	//public final boolean inputReplacementIC2;
	public final boolean inputReplacementMekanism;
	// resource related stuff
	public final boolean enableSpecificKindSort;
	public final boolean enableSpecificEntrySort;
	public final TObjectIntMap<String> ownerOfEveryThing;
	public final Set<String> metalsToUnify;
	public final Set<String> childrenOfMetals;
	public final List<String> resourceBlackList;
	public final Set<ResourceLocation> recipesToIgnore;
	public final List<String> furnaceInputsToIgnore;
	public final List<String> furnaceOutputsToIgnore;

	public final Boolean treatRecipesToRemoveAsRegex;
	public final List<String> recipesToRemove;
	public final Set<String> ignoreModIdRecipes;
	public final Map<String, Set<String>> customUnifiedResources;
	// integration specific configs:
	public final boolean ieIntegrationDuplicateRemoval;
	// userEntries
	public final List<String> userOreDictEntries;
	// modules
	public final boolean integrationModule;
	public final boolean modConfigModule;
	// config
	private final Configuration config;
	// resource related stuff
	private final String resources = "resources";

	private Config()
	{
		boolean deleted = false;
		config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_ID + SLASH + Reference.MOD_NAME + ".cfg"), Reference.MOD_VERSION);
		try {
			if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion()))
				deleted = config.getConfigFile().delete();

			// config
			// general configs
			final String general = Configuration.CATEGORY_GENERAL;
			libraryMode = config.getBoolean("libraryMode", general, false, "Enable this if you have mods that depend on UniDict but you don't like the unification.");
			keepOneEntry = config.getBoolean("keepOneEntry", general, false, "Keep only one entry per ore dictionary entry");
			keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", general, new String[]{}, "Mods listed here will be blacklisted in " + "keepOneEntry.\nMust be the exact modID."))));
			keepOneEntryKindBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryKindBlackList", general, new String[]{}, "Kinds listed here will be blacklisted in keepOneEntry.\nMust be the exact kind name."))));
			keepOneEntryEntryBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryEntryBlackList", general, new String[]{}, "Entries listed here will be blacklisted in keepOneEntry.\nMust be the exact entry name."))));
			keepOneEntryBlackListsAsWhiteLists = config.getBoolean("keepOneEntryBlackListsAsWhiteLists", general, false, "Enable this if you want the keepOneEntry blacklist to became a whitelist.\nNote: This doesn't applies for \"S:keepOneEntryModBlackList\"");
			itemStacksNamesToIgnore = Arrays.asList(config.getStringList("itemStacksNamesToIgnore", general, new String[]{}, "ItemStacks that you want to be ignored/not unified.\nExample Format: minecraft:iron_ingot#0"));
			registerNewCraftingIngredientsAsItemStacks = config.getBoolean("registerNewCraftingIngredientsAsItemStacks", general, false, "If Enabled, the ingredients of all the new recipes created by Crafting Integration will be registered as ItemStacks.\nEnable this if you don't like the cycling through possibilities in JEI.");
			autoHideInJEI = config.getBoolean("autoHideInJEI", general, true, "Automatically hide items in JEI") && isModLoaded("jei");
			hideInJEIKindBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("autoHideInJEIKindBlackList", general, new String[]{"ore"}, "Kinds listed here won't be hiden in JEI.\nOnly works if keepOneEntry is false."))));
			hideInJEIEntryBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("autoHideInJEIEntryBlackList", general, new String[]{}, "Entries listed here won't be hiden in JEI.\nOnly works if keepOneEntry is false."))));
			// dumps
			kindsDump = config.getBoolean("kindsDump", "dump", false, "Enable this to keep track of all the kinds.\nThe output file will be saved in \"config\\unidict\\dump\" folder.\nOnce the file is generated, you must delete it to re-generate.");
			entriesDump = config.getBoolean("entriesDump", "dump", false, "Enable this to keep track of all the entries.\nThe output file will be saved in \"config\\unidict\\dump\"  folder.\nOnce the file is generated, you must delete it to re-generate.");
			unifiedEntriesDump = config.getBoolean("unifiedEntriesDump", "dump", false, "Enable this to keep track of all the unificated entries.\nThe output file will be saved in \"config\\unidict\\dump\" folder.\nOnce the file is generated, you must delete it to re-generate.");
			// input replacement
			inputReplacementFurnace = config.getBoolean("furnace", "inputReplacement", false, "Enabling this will remove all non-standard items from inputs of the Furnace.");
			//inputReplacementIC2 = config.getBoolean("ic2", "inputReplacement", false, "Enabling this will remove all non-standard items as inputs of IC2 Machine Recipes.\nNote: This will only affect recipes that don't use OreDictionary.");
			inputReplacementMekanism = config.getBoolean("mekanism", "inputReplacement", false, "Enabling this will remove all non-standard items from inputs of Mekanism Machine Recipes.");
			// resource related stuff
			enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "Enabling this allows you to specify the \"owner\" of each kind.\nIt also will make \"S:ownerOfEveryThing\" be ignored for this kind.\nexample: \"ore\"\n");
			enableSpecificEntrySort = config.getBoolean("enableSpecificEntrySort", resources, false, "Enabling this allows you to specify the \"owner\" of each entry.\nIt also will make \"S:ownerOfEveryThing\" be ignored for this entry.\nexample: \"ingotIron\"\n");
			ownerOfEveryThing = new TObjectIntHashMap<>(getOwnerOfEveryThingMap());
			metalsToUnify = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Zinc", "Aluminium", "Aluminum", "Alumina", "Chromium", "Chrome", "Uranium", "Iridium", "Osmium", "Bronze", "Steel", "Brass", "Invar", "Electrum", "Cupronickel", "Constantan"}, "List of Metals to unify.\nNote 1: This will only work for \"metals\"\nNote 2: If your \"metal\" doesn't have an ingot form, check the \"S:customUnifiedResources\" option.\n"))));
			childrenOfMetals = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "dustSmall", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear", "rod"}, "What kind of children do you want to make standard\n"))));
			resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminium", "Alumina", "Chrome", "Redstone"}, "Resources to be black-listed.\nThis exists to avoid duplicates.\nThis affects the API."));

			recipesToIgnore = new HashSet<>();
			for (final String recipeToIgnore : config.getStringList("recipeToIgnoreList", resources, new String[]{"minecraft:iron_nugget", "minecraft:iron_block", "minecraft:iron_ingot_from_block", "minecraft:iron_ingot_from_nuggets", "minecraft:gold_nugget", "minecraft:gold_ingot_from_block", "minecraft:gold_ingot_from_nuggets", "minecraft:gold_block"}, "Recipe names that you don't want the Crafting Integration to mess with.")) {
				final int separator = recipeToIgnore.indexOf(':');
				if (separator > 0)
					recipesToIgnore.add(new ResourceLocation(recipeToIgnore.substring(0, separator), recipeToIgnore.substring(separator + 1)));
			}

			furnaceInputsToIgnore = Arrays.asList(config.getStringList("furnaceInputsToIgnore", resources, new String[]{""}, "Input ItemStack (item registry names) that you don't want the Furnace Integration to mess with.\nFormat:\nminecraft:iron_ingot#0"));
			furnaceOutputsToIgnore = Arrays.asList(config.getStringList("furnaceOutputsToIgnore", resources, new String[]{""}, "Ouput ItemStacks (item registry names) that you don't want the Furnace Integration to mess with.\nFormat:\nminecraft:iron_ingot#0"));

			// integration specific configs
			ieIntegrationDuplicateRemoval = config.getBoolean("ieIntegrationDuplicateRemoval", "integrations", true, "This controls if duplicates are removed in Immersive Engineering Integration.");

			treatRecipesToRemoveAsRegex = config.getBoolean("treatRecipeToRemoveAsRegex", resources, false, "This controls whether the recipes in recipeToRemoveList are treated as regular expressions (Regex).");
			recipesToRemove = Arrays.asList(config.getStringList("recipeToRemoveList", resources, new String[]{},
					"Recipe names that you want to be removed.\nNote: This will be executed after Crafting Integration.\nNote 2: If there is a space on the end of the recipe, then the recipe name must stay in \"recipename\", this is ONLY required when there is a space on the end \" \""));
			ignoreModIdRecipes = new LinkedHashSet<>(Arrays.asList(config.getStringList("ignoreModIdRecipes", resources, new String[]{"oreshrubs"}, "Crafting Integration will ignore recipes created by the ModIds listed below.\n")));
			customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());
			// userRegisteredOreDictEntries
			userOreDictEntries = Arrays.asList(config.getStringList("userOreDictEntries", general, new String[]{}, "This allows to the user to add/remove entries before the unification happen.\nThis is mainly useful to avoid trying to unify certain things.\n\nFormat to Add entries to the OreDictionary:\nweirdStone+minecraft:stone#1\nThe example above will register Granite as weirdStone.\n\nFormat to Remove entries from the OreDictionary:\nweirdStone-minecraft:stone#1\nThe example above will remove Granite from weirdStone."));
			// integration module
			integrationModule = config.getBoolean("integration", "modules", true, "Integration Module.\nIf false all of the Integrations will be disabled.\n");
			modConfigModule = config.getBoolean("modConfig", "modules", true, "Mod Config Module.\nIf false UniDict will not modifiy other mods' configs to achieve unification.\n");
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong on " + config.getConfigFile() + " loading. " + e);
		}
		if (config.hasChanged() || deleted)
			config.save();
	}

	public void saveIfHasChanged()
	{
		if (config.hasChanged())
			config.save();
	}

	private TObjectIntMap<String> getOwnerOfEveryThingMap()
	{
		final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"minecraft", "thermalfoundation", "substratum", "ic2", "mekanism", "immersiveengineering", "techreborn"}, "All the entries will be sorted according to the modID list below\nMust be the exact ModID.\n");
		final TObjectIntMap<String> ownerOfEveryThingMap = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
		for (int i = 0; i < ownerOfEveryThing.length; i++)
			ownerOfEveryThingMap.put(ownerOfEveryThing[i], i);
		return ownerOfEveryThingMap;
	}

	private Map<String, Set<String>> getCustomUnifiedResourcesMap()
	{
		final Map<String, Set<String>> customUnifiedResources = new THashMap<>();
		final Pattern splitPattern = Pattern.compile("\\|");
		for (String customUnifiedResource : config.getStringList("customUnifiedResources", resources, new String[]{"Obsidian:dustTiny|dust", "Stone:dust", "Obsidian:dust|dustSmall", "Coal:dust|dustSmall", "Sulfur:dust|dustSmall", "Salt:dust"}, "Here you can put a list to custom unifications.\nMust be in this format \"ResourceName:kind1|kind2|...\".")) {
			final int baseSeparatorIndex;
			final Set<String> kindSet;
			if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1))))).isEmpty())
				customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
		}
		return customUnifiedResources;
	}
}