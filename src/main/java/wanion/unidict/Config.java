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
	public final List<ResourceLocation> recipesToRemove;
	public final Set<String> ignoreModIdRecipes;
	public final Map<String, Set<String>> customUnifiedResources;
	// integration specific configs:
	public final boolean ieIntegrationDuplicateRemoval;
	// userEntries
	public final List<String> userOreDictEntries;
	// modules
	public final boolean integrationModule;
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
			libraryMode = config.getBoolean("libraryMode", general, false, "Enable this if you have mods that depends on UniDict but you don't like the unification.");
			keepOneEntry = config.getBoolean("keepOneEntry", general, false, "keep only one entry per ore dict entry?");
			keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", general, new String[]{}, "mods listed here will be blacklisted in keepOneEntry.\nmust be the exact modID."))));
			keepOneEntryKindBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryKindBlackList", general, new String[]{}, "kinds listed here will be blacklisted in keepOneEntry.\nmust be the exact kind name."))));
			keepOneEntryEntryBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("keepOneEntryEntryBlackList", general, new String[]{}, "entries listed here will be blacklisted in keepOneEntry.\nmust be the exact entry name."))));
			keepOneEntryBlackListsAsWhiteLists = config.getBoolean("keepOneEntryBlackListsAsWhiteLists", general, false, "enable this if you want the keepOneEntry blacklists to became whitelists.\nNote: this doesn't applies for \"S:keepOneEntryModBlackSet\"");
			itemStacksNamesToIgnore = Arrays.asList(config.getStringList("itemStacksNamesToIgnore", general, new String[]{}, "Put here itemstacks that you want don't want to ignore/not unify.\nExample Format: minecraft:iron_ingot#0"));
			registerNewCraftingIngredientsAsItemStacks = config.getBoolean("registerNewCraftingIngredientsAsItemStacks", general, false, "If Enabled, the ingredients of all the new recipes created by Crafting Integration will be registered as ItemStacks.\nEnable this if you don't like the cycling through the possibilities of JEI.");
			autoHideInJEI = config.getBoolean("autoHideInJEI", general, true, "auto hide items in JEI?") && isModLoaded("jei");
			hideInJEIKindBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("autoHideInJEIKindBlackList", general, new String[]{"ore"}, "put here kinds that you don't want to hide in JEI.\nonly works if keepOneEntry is false."))));
			hideInJEIEntryBlackSet = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("autoHideInJEIEntryBlackList", general, new String[]{}, "put here entries that you don't want to hide in JEI.\nonly works if keepOneEntry is false."))));
			// dumps
			kindsDump = config.getBoolean("kindsDump", "dump", false, "Enable this to keep track of all the kinds.\nthe output file will be saved on \"config\\unidict\\dump\" folder.\nonce the file is generated, you must delete it to re-generate.");
			entriesDump = config.getBoolean("entriesDump", "dump", false, "Enable this to keep track of all the entries.\nthe output file will be saved on \"config\\unidict\\dump\"  folder.\nonce the file is generated, you must delete it to re-generate.");
			unifiedEntriesDump = config.getBoolean("unifiedEntriesDump", "dump", false, "Enable this to keep track of all the unificated entries.\nthe output file will be saved on \"config\\unidict\\dump\" folder.\nonce the file is generated, you must delete it to re-generate.");
			// input replacement
			inputReplacementFurnace = config.getBoolean("furnace", "inputReplacement", false, "Enabling this will remove all non-standard items as input of the Furnace.");
			//inputReplacementIC2 = config.getBoolean("ic2", "inputReplacement", false, "Enabling this will remove all non-standard items as input of IC2 Machine Recipes.\nNote: this will only affect recipes that doesn't uses OreDictionary.");
			inputReplacementMekanism = config.getBoolean("mekanism", "inputReplacement", false, "Enabling this will remove all non-standard items as input of Mekanism Machine Recipes.");
			// resource related stuff
			enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "enabling this allow you to specify the \"owner\" of each kind.\nit also will make \"S:ownerOfEveryThing\" be ignored for this kind.\nexample: \"ore\"\n");
			enableSpecificEntrySort = config.getBoolean("enableSpecificEntrySort", resources, false, "enabling this allow you to specify the \"owner\" of each entry.\nit also will make \"S:ownerOfEveryThing\" be ignored for this entry.\nexample: \"ingotIron\"\n");
			ownerOfEveryThing = new TObjectIntHashMap<>(getOwnerOfEveryThingMap());
			metalsToUnify = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Zinc", "Aluminium", "Aluminum", "Alumina", "Chromium", "Chrome", "Uranium", "Iridium", "Osmium", "Bronze", "Steel", "Brass", "Invar", "Electrum", "Cupronickel", "Constantan"}, "List of Metals to unify.\nNote 1: this will only work for \"metals\"\nNote 2: if your \"metal\" doesn't have an ingot form, check the \"S:customUnifiedResources\" config option.\n"))));
			childrenOfMetals = Collections.unmodifiableSet(Sets.newHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "dustSmall", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear", "rod"}, "what kind of child do you want to make a standard?\n"))));
			resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminium", "Alumina", "Chrome", "Redstone"}, "resources to be black-listed.\nthis exists to avoid duplicates.\nthis affect the API."));

			recipesToIgnore = new HashSet<>();
			for (final String recipeToIgnore : config.getStringList("recipeToIgnoreList", resources, new String[]{"minecraft:iron_nugget", "minecraft:iron_block", "minecraft:iron_ingot_from_block", "minecraft:iron_ingot_from_nuggets", "minecraft:gold_nugget", "minecraft:gold_ingot_from_block", "minecraft:gold_ingot_from_nuggets", "minecraft:gold_block"}, "add here recipes (names) that you don't want the Crafting Integration to mess with.")) {
				final int separator = recipeToIgnore.indexOf(':');
				if (separator > 0)
					recipesToIgnore.add(new ResourceLocation(recipeToIgnore.substring(0, separator), recipeToIgnore.substring(separator + 1, recipeToIgnore.length())));
			}

			furnaceInputsToIgnore = Arrays.asList(config.getStringList("furnaceInputsToIgnore", resources, new String[]{""}, "Add here input ItemStack's (item registry names) that you don't want the Furnace Integration to mess with.\nFormat:\nminecraft:iron_ingot#0"));
			furnaceOutputsToIgnore = Arrays.asList(config.getStringList("furnaceOutputsToIgnore", resources, new String[]{""}, "Add here output ItemStack's (item registry names) that you don't want the Furnace Integration to mess with.\nFormat:\nminecraft:iron_ingot#0"));

			// integration specific configs
			ieIntegrationDuplicateRemoval = config.getBoolean("ieIntegrationDuplicateRemoval", "integrations", true, "this controls if duplicate check & removal of duplicates on Immersive Engineering Integration.");

			recipesToRemove = new ArrayList<>();
			for (String recipeToRemove : config.getStringList("recipeToRemoveList", resources, new String[]{}, "add here recipes (names) that you want to be removed.\nnote: this will be executed after Crafting Integration.\nnote 2: if there is a space on the end of the recipe, then the recipe name must stay in \"recipename\", this is ONLY required when there is a space on the end \" \"")) {
				recipeToRemove = recipeToRemove.replace("\"", "");
				final int separator = recipeToRemove.indexOf(':');
				if (separator > 0)
					recipesToRemove.add(new ResourceLocation(recipeToRemove.substring(0, separator), recipeToRemove.substring(separator + 1, recipeToRemove.length())));
			}
			ignoreModIdRecipes = new LinkedHashSet<>(Arrays.asList(config.getStringList("ignoreModIdRecipes", resources, new String[]{"oreshrubs"}, "Crafting Integration will ignore recipes created by the ModId's listed below.\n")));
			customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());
			// userRegisteredOreDictEntries
			userOreDictEntries = Arrays.asList(config.getStringList("userOreDictEntries", general, new String[]{}, "This allows to the user add/remove entries before the Unification happen.\nthis is mainly useful to avoid trying to unify certain things.\n\nFormat to Add entries to the OreDictionary:\nweirdStone+minecraft:stone#1\nThe example above will register Granite as weirdStone.\n\nFormat to Remove entries from the OreDictionary:\nweirdStone-minecraft:stone#1\nThe example above will remove Granite from weirdStone."));
			// integration module
			integrationModule = config.getBoolean("integration", "modules", true, "Integration Module.\nif false all the Integrations will be disabled.\n");
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
		final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"minecraft", "thermalfoundation", "substratum", "ic2", "mekanism", "immersiveengineering", "techreborn"}, "all the entries will be sorted according to the modID list below\nmust be the exact modID.\n");
		final TObjectIntMap<String> ownerOfEveryThingMap = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
		for (int i = 0; i < ownerOfEveryThing.length; i++)
			ownerOfEveryThingMap.put(ownerOfEveryThing[i], i);
		return ownerOfEveryThingMap;
	}

	private Map<String, Set<String>> getCustomUnifiedResourcesMap()
	{
		final Map<String, Set<String>> customUnifiedResources = new THashMap<>();
		final Pattern splitPattern = Pattern.compile("\\|");
		for (String customUnifiedResource : config.getStringList("customUnifiedResources", resources, new String[]{"Obsidian:dustTiny|dust", "Stone:dust", "Obsidian:dust|dustSmall", "Coal:dust|dustSmall", "Sulfur:dust|dustSmall", "Salt:dust"}, "Here you can put a list to custom unify them.\nmust be in this format \"ResourceName:kind1|kind2|...\".")) {
			final int baseSeparatorIndex;
			final Set<String> kindSet;
			if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1, customUnifiedResource.length()))))).isEmpty())
				customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
		}
		return customUnifiedResources;
	}
}