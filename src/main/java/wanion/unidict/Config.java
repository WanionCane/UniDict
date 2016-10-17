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
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.common.Reference;
import wanion.unidict.resource.Resource;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class Config implements UniDict.IDependence
{
	// general configs
	public final boolean keepOneEntry;
	public final boolean inputReplacementFurnace;
	public final boolean inputReplacementIC2;
	public final boolean inputReplacementMekanism;
	public final Set<String> keepOneEntryModBlackSet;
	public final boolean autoHideInJEI;
	public final Set<String> hideInJEIBlackSet;
	public final boolean kindDebugMode;
	public final boolean enableSpecificKindSort;
	public final TObjectIntMap<String> ownerOfEveryThing;
	public final Set<String> metalsToUnify;
	public final Set<String> childrenOfMetals;
	public final List<String> resourceBlackList;
	public final Map<String, Set<String>> customUnifiedResources;
	// userRegisteredOreDictEntries
	public final List<String> userRegisteredOreDictEntries;
	// modules
	public final boolean integrationModule;
	// vanilla integrations
	public final boolean craftingIntegration;
	public final boolean furnaceIntegration;
	// integration
	public final boolean abyssalCraft;
	public final boolean ae2Integration;
	public final boolean baseMetalsIntegration;
	public final boolean calculatorIntegration;
	public final boolean bloodMagicIntegration;
	public final boolean enderIOIntegration;
	public final boolean embersIntegration;
	public final boolean forestryIntegration;
	public final boolean foundryIntegration;
	public final boolean ic2Integration;
	public final boolean ieIntegration;
	public final boolean mekanismIntegration;
	public final boolean modularMachinesIntegration;
	public final boolean railCraftIntegration;
	public final boolean techRebornIntegration;
	// config
	private final Configuration config;
	// resource related stuff
	private final String resources = "resources";
	// ensure mod loaded
	public final boolean forestry;
	public final boolean ic2;

	private Config()
	{
		boolean deleted = false;
		config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_NAME + ".cfg"), Reference.MOD_VERSION);
		try {
			if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion()))
				deleted = config.getConfigFile().delete();

			// config
			// general configs
			final String general = Configuration.CATEGORY_GENERAL;
			keepOneEntry = config.getBoolean("keepOneEntry", general, false, "keep only one entry per ore dict entry?");
			keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", general, new String[]{}, "mods listed here will be blacklisted in keepOneEntry.\nmust be the exact modID."))));
			autoHideInJEI = config.getBoolean("autoHideInJEI", general, true, "auto hide items in JEI?") && isModLoaded("JEI");
			hideInJEIBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("autoHideInJEIBlackList", general, new String[]{"ore"}, "put here things that you don't want to hide in JEI.\nonly works if keepOneEntry is false."))));
			kindDebugMode = config.getBoolean("kindDebugMode", general, false, "Enable this to keep track of all the kinds.\nthe output will be in logs folder.");
			// input replacement
			inputReplacementFurnace = config.getBoolean("furnace", "inputReplacement", false, "Enabling this will remove all non-standard items as input of the Furnace.");
			inputReplacementIC2 = config.getBoolean("ic2", "inputReplacement", false, "Enabling this will remove all non-standard items as input of IC2 Machine Recipes.\nNote: this will only affect recipes that doesn't uses OreDictionary.");
			inputReplacementMekanism = config.getBoolean("mekanism", "inputReplacement", false, "Enabling this will remove all non-standard items as input of Mekanism Machine Recipes.");
			// resource related stuff
			enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "enabling this allow you to specify the \"owner\" of each kind.\nalso will make \"S:ownerOfEveryThing\" be ignored.");
			ownerOfEveryThing = new TObjectIntHashMap<>((!enableSpecificKindSort) ? getOwnerOfEveryThingMap() : new TObjectIntHashMap<>());
			metalsToUnify = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Zinc", "Aluminium", "Aluminum", "Alumina", "Chromium", "Chrome", "Uranium", "Iridium", "Osmium", "Bronze", "Steel", "Brass", "Invar", "Electrum", "Cupronickel", "Constantan"}, "list of things to do unifying things.\n"))));
			childrenOfMetals = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "dustSmall", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear", "rod"}, "what kind of child do you want to make a standard?\n"))));
			resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminum", "Alumina", "Chrome", "Constantan"}, "resources to be black-listed.\nthis exists to avoid duplicates.\nthis affect the API."));
			customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());
			// userRegisteredOreDictEntries
			userRegisteredOreDictEntries = Arrays.asList(config.getStringList("userRegisteredOreDictEntries", general, new String[]{}, "This allows to the user register their own ore entries before the Unification happen.\nthis is mainly useful when the user is trying to unify things that aren't registered previously in the Ore Dictionary.\n\nFormat:\nweirdStone+minecraft:stone#1\nThe example above will register Granite as weirdStone."));
			// modules
			integrationModule = config.getBoolean("integration", "modules", true, "Integration Module enabled?\nif false all the Integrations will be disabled.\n");
			// vanilla integrations
			craftingIntegration = config.getBoolean("craftingIntegration", "vanillaIntegrations", true, "Crafting Integration");
			furnaceIntegration = config.getBoolean("furnaceIntegration", "vanillaIntegrations", true, "Furnace Integration");

			forestry = isModLoaded("forestry");
			ic2 = isModLoaded("IC2");

			// integration
			final String integrations = "integrations";
			abyssalCraft = config.getBoolean("abyssalCraft", integrations, true, "AbyssalCraft Integration.") && isModLoaded("abyssalcraft");
			ae2Integration = config.getBoolean("appliedEnergistics2", integrations, true, "Applied Energistics 2 Integration.") && isModLoaded("appliedenergistics2");
			baseMetalsIntegration = config.getBoolean("baseMetals", integrations, true, "Base Metals Integration.") && isModLoaded("basemetals");
			bloodMagicIntegration = config.getBoolean("bloodMagic", integrations, true, "Blood Magic Integration.") && isModLoaded("BloodMagic");
			calculatorIntegration = config.getBoolean("calculator", integrations, false, "Calculator Integration.") && isModLoaded("Calculator");
			embersIntegration = config.getBoolean("embers", integrations, true, "Embers Integration") && isModLoaded("embers");
			enderIOIntegration = config.getBoolean("enderIO", integrations, true, "Ender IO Integration.") && isModLoaded("EnderIO");
			forestryIntegration = config.getBoolean("forestry", integrations, true, "Forestry Integration.") && forestry;
			foundryIntegration = config.getBoolean("foundry", integrations, true, "Foundry Integration.") && isModLoaded("foundry");
			ic2Integration = config.getBoolean("industrialCraft2", integrations, true, "Industrial Craft 2 Integration.") && ic2;
			ieIntegration = config.getBoolean("immersiveEngineering", integrations, true, "Immersive Engineering Integration.") && isModLoaded("immersiveengineering");
			mekanismIntegration = config.getBoolean("mekanism", integrations, true, "Mekanism Integration.") && isModLoaded("Mekanism");
			modularMachinesIntegration = config.getBoolean("modularMachines", integrations, true, "Modular-Machines Integration.") && isModLoaded("modularmachines");
			railCraftIntegration = config.getBoolean("railcraft", integrations, true, "Railcraft Integration.") && isModLoaded("Railcraft");
			techRebornIntegration = config.getBoolean("techReborn", integrations, true, "TechReborn Integration.") && isModLoaded("techreborn");
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

	public TObjectIntMap<String> getOwnerOfEveryKindMap(final int kind)
	{
		final String kindName = WordUtils.capitalize(Resource.getNameOfKind(kind));
		final String[] ownerOfEveryKind = config.getStringList("ownerOfEvery" + kindName, resources, new String[]{"minecraft", "substratum", "ic2", "mekanism", "immersiveengineering", "techreborn"}, "entries of kind \"" + kindName + "\" will be sorted according to the modID list below\nmust be the exact modID.\n");
		final TObjectIntMap<String> ownerOfEveryThingMap = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
		for (int i = 0; i < ownerOfEveryKind.length; i++)
			ownerOfEveryThingMap.put(ownerOfEveryKind[i], i);
		return ownerOfEveryThingMap;
	}

	private TObjectIntMap<String> getOwnerOfEveryThingMap()
	{
		final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"minecraft", "substratum", "ic2", "mekanism", "immersiveengineering", "techreborn"}, "all the entries will be sorted according to the modID list below\nmust be the exact modID.\n");
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
			if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newLinkedHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1, customUnifiedResource.length()))))).isEmpty())
				customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
		}
		return customUnifiedResources;
	}
}