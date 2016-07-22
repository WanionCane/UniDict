package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Sets;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.common.Reference;
import wanion.unidict.resource.Resource;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class Config
{
    private Config() {}

    // config
    private static final Configuration config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_NAME + ".cfg"), Reference.MOD_VERSION);
    // general configs
    private static final String general = Configuration.CATEGORY_GENERAL;
    public static final boolean keepOneEntry = config.getBoolean("keepOneEntry", general, false, "keep only one entry per ore dict entry?");
    public static final Set<String> keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", general, new String[]{}, "mods listed here will be blacklisted in keepOneEntry.\nmust be the exact modID."))));
    public static boolean autoHideInJEI;
    public static final Set<String> hideInJEIBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("autoHideInJEIBlackList", general, new String[]{"ore"}, "put here things that you don't want to hide in JEI.\nonly works if keepOneEntry is false."))));
    public static final boolean kindDebugMode = config.getBoolean("kindDebugMode", general, false, "Enable this to keep track of all the kinds.\nthe output will be in logs folder.");
    // resource related stuff
    private static final String resources = "resources";
    public static final boolean enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "enabling this allow you to specify the \"owner\" of each kind.\nalso will make \"S:ownerOfEveryThing\" be ignored.");
    public static final TObjectLongMap<String> ownerOfEveryThing = new TUnmodifiableObjectLongMap<>((!enableSpecificKindSort) ? getOwnerOfEveryThingMap() : new TObjectLongHashMap<>());
    public static final Set<String> metalsToUnify = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Zinc", "Aluminium", "Aluminum", "Alumina", "Chrome", "Chromium", "Iridium", "Osmium", "Titanium", "Tungsten", "Bronze", "Steel", "Brass", "Invar", "Electrum", "Signalum", "Cupronickel"}, "list of things to do unifying things.\n"))));
    public static final Set<String> childrenOfMetals = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear", "rod"}, "what kind of child do you want to make a standard?\n"))));
    public static final List<String> resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminium"}, "resources to be black-listed.\nthis exists to avoid duplicates.\nthis affect the API."));
    public static final Map<String, Set<String>> customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());
    // modules
    static final boolean integrationModule = config.getBoolean("integration", "modules", true, "Integration Module enabled?\nif false all the Integrations will be disabled.\n");
    // vanilla integrations
    public static final boolean craftingIntegration = config.getBoolean("craftingIntegration", "vanillaIntegrations", true, "Crafting Integration");
    public static final boolean furnaceIntegration = config.getBoolean("furnaceIntegration", "vanillaIntegrations", true, "Furnace Integration");
    // ore gen integration
    //public static final boolean oreGenIntegration = config.getBoolean("enabled", "oreGenIntegration", false, "Ore Gen Integration (alpha)\nbe sure to keep enabled all the ore generation in the configuration of other mods, because this will compensate the missing ores.");
    //public static final int oreGenIntegrationRadius = config.getInt("radius", "oreGenIntegration", 3, 1, 32, "this is the radius (square root) of chuncks that the profiling of Ore Gen integration will use;\nhigher values means more accuracy, but in exchange of a really long loading time;\nthe profiling is done only once.");
    // ensure mod loaded
    public static boolean ic2;
    // integration
    public static boolean abyssalCraft;
    public static boolean baseMetalsIntegration;
    public static boolean calculatorIntegration;
    //public static boolean botaniaIntegration;
    public static boolean enderIOIntegration;
    public static boolean foundryIntegration;
    public static boolean ic2Integration;
    public static boolean mekanismIntegration;
    public static boolean techRebornIntegration;
    public static boolean techyIntegration;

    static void init()
    {
        boolean deleted = false;
        try {
            if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion()))
                deleted = config.getConfigFile().delete();

            //forestry = isModLoaded("forestry");
            ic2 = isModLoaded("IC2");

            // general configs
            autoHideInJEI = config.getBoolean("autoHideInJEI", general, true, "auto hide items in JEI?") && isModLoaded("JEI");

            // integration
            final String integrations = "integrations";
            abyssalCraft = config.getBoolean("abyssalCraft", integrations, true, "AbyssalCraft Integration.") && isModLoaded("abyssalcraft");
            baseMetalsIntegration = config.getBoolean("baseMetals", integrations, true, "Base Metals Integration.") && isModLoaded("basemetals");
            //botaniaIntegration = config.getBoolean("botania", integrations, true, "Botania Integration.") && isModLoaded("Botania");
            calculatorIntegration = config.getBoolean("calculator", integrations, false, "Calculator Integration.") && isModLoaded("Calculator");
            enderIOIntegration = config.getBoolean("enderIO", integrations, true, "Ender IO Integration.") && isModLoaded("EnderIO");
            //forestryIntegration = config.getBoolean("forestry", integrations, true, "Forestry Integration.") && forestry;
            foundryIntegration = config.getBoolean("foundry", integrations, true, "Foundry Integration.") && isModLoaded("foundry");
            ic2Integration = config.getBoolean("industrialCraft2", integrations, true, "Industrial Craft 2 Integration.") && ic2;
            mekanismIntegration = config.getBoolean("mekanism", integrations, true, "Mekanism Integration.") && isModLoaded("Mekanism");
            techRebornIntegration = config.getBoolean("techReborn", integrations, true, "TechReborn Integration.") && isModLoaded("techreborn");
            techyIntegration = config.getBoolean("techy", integrations, true, "Techy Integration.") && isModLoaded("Techy");
        } catch (Exception e) {
            UniDict.getLogger().info("Something went wrong on " + config.getConfigFile() + "loading. " + e);
        }
        if (config.hasChanged() || deleted)
            config.save();
    }

    public static void saveIfHasChanged()
    {
        if (config.hasChanged())
            config.save();
    }

    public static TObjectLongMap<String> getOwnerOfEveryKindMap(final long kind)
    {
        final String kindName = WordUtils.capitalize(Resource.getNameOfKind(kind));
        final String[] ownerOfEveryKind = config.getStringList("ownerOfEvery" + kindName, resources, new String[]{"substratum", "minecraft", "ic2", "techreborn"}, "entries of kind \"" + kindName + "\" will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectLongMap<String> ownerOfEveryThingMap = new TObjectLongHashMap<>(10, 1, Long.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryKind.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryKind[i], i);
        return ownerOfEveryThingMap;
    }

    private static TObjectLongMap<String> getOwnerOfEveryThingMap()
    {
        final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"substratum", "minecraft", "ic2", "techreborn"}, "all the entries will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectLongMap<String> ownerOfEveryThingMap = new TObjectLongHashMap<>(10, 1, Long.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryThing.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryThing[i], i);
        return ownerOfEveryThingMap;
    }

    private static Map<String, Set<String>> getCustomUnifiedResourcesMap()
    {
        final Map<String, Set<String>> customUnifiedResources = new THashMap<>();
        final Pattern splitPattern = Pattern.compile("\\|");
        for (String customUnifiedResource : config.getStringList("customUnifiedResources", resources, new String[]{"Obsidian:dustTiny|dust", "Stone:dust"}, "Here you can put a list to custom unify them.\nmust be in this format \"ResourceName:kind1|kind2|...\".")) {
            final int baseSeparatorIndex;
            final Set<String> kindSet;
            if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newLinkedHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1, customUnifiedResource.length()))))).isEmpty())
                customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
        }
        return customUnifiedResources;
    }
}