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

import static cpw.mods.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class Config
{
    // config
    private static final Configuration config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_NAME + ".cfg"), Reference.MOD_VERSION);

    // ensure mod loaded
    public static boolean forestry;
    public static boolean foundry;
    public static boolean ic2;
    //public static boolean ic2Classic;
    public static boolean tinkersConstruct;

    // general configs
    private static final String general = Configuration.CATEGORY_GENERAL;
    public static final boolean keepOneEntry = config.getBoolean("keepOneEntry", general, false, "keep only one entry per ore dict entry?");
    public static final Set<String> keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", Configuration.CATEGORY_GENERAL, new String[]{}, "mods listed here will be blacklisted in keepOneEntry.\nmust be the exact modID."))));
    public static boolean autoHideInNEI;
    public static final Set<String> hideInNEIBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("autoHideInNEIBlackList", general, new String[]{"ore"}, "put here things that you don't want to hide in NEI.\nonly works if keepOneEntry is false."))));
    public static final boolean kindDebugMode = config.getBoolean("kindDebugMode", general, false, "Enable this to keep track of all the kinds.\nthe output will be in logs folder.");
    // resource related stuff
    private static final String resources = "resources";
    public static final boolean enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "enabling this allow you to specify the \"owner\" of each kind.\nalso will make \"S:ownerOfEveryThing\" be ignored.");
    public static final TObjectLongMap<String> ownerOfEveryThing = new TUnmodifiableObjectLongMap<>((!enableSpecificKindSort) ? getOwnerOfEveryThingMap() : new TObjectLongHashMap<>());
    public static final Set<String> metalsToUnify = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Aluminum", "Aluminium", "Ardite", "Cobalt", "Osmium", "Mithril", "Zinc", "Invar", "Steel", "Bronze", "Electrum", "Brass"}, "list of things to do unifying things.\n"))));
    public static final Set<String> childrenOfMetals = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear"}, "what kind of child do you want to make a standard?\n"))));
    public static final List<String> resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminium"}, "resources to be black-listed.\nthis exists to avoid duplicates.\nthis affect the API."));
    public static final Map<String, Set<String>> customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());

    // modules
    private static final String modules = "modules";
    static final boolean integrationModule = config.getBoolean("integration", modules, true, "Integration Module enabled?\nif false all the Integrations will be disabled.\nthis will affect non-standalone tweak.\n");

    // integration
    public static boolean abyssalCraft;
    public static boolean ae2Integration;
    public static boolean electricalAgeIntegration;
    public static boolean enderIOIntegration;
    public static boolean forestryIntegration;
    public static boolean foundryIntegration;
    public static boolean fspIntegration;
    public static boolean hydrauliCraftIntegration;
    public static boolean ic2Integration;
    public static boolean ieIntegration;
    public static boolean magnetiCraftIntegration;
    public static boolean mekanismIntegration;
    public static boolean railCraftIntegration;
    public static boolean teIntegration;

    // vanilla integrations
    private static final String vanillaIntegrations = "vanillaIntegrations";
    public static final boolean chestIntegration = config.getBoolean("chestIntegration", vanillaIntegrations, true, "Chest Integration? (\"dungeon chest, nether fortress chests, etc...\").");
    public static final boolean craftingIntegration = config.getBoolean("craftingIntegration", vanillaIntegrations, true, "Crafting Integration");
    public static final boolean furnaceIntegration = config.getBoolean("furnaceIntegration", vanillaIntegrations, true, "Furnace Integration");

    static void init()
    {
        boolean deleted = false;
        try {
            if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion()))
                deleted = config.getConfigFile().delete();

            // ensure mod loaded
            forestry = isModLoaded("Forestry");
            foundry = isModLoaded("foundry");
            ic2 = isModLoaded("IC2");
            tinkersConstruct = isModLoaded("TConstruct");

            // general
            autoHideInNEI = config.getBoolean("autoHideInNEI", general, true, "auto hide items in NEI?") && isModLoaded("NotEnoughItems");

            // integrations
            final String integrations = "integrations";
            abyssalCraft = config.getBoolean("abyssalCraft", integrations, true, "AbyssalCraft Integration.") && isModLoaded("abyssalcraft");
            ae2Integration = config.getBoolean("appliedEnergistics2", integrations, true, "Applied Energistics 2 Integration.") && isModLoaded("appliedenergistics2");
            electricalAgeIntegration = config.getBoolean("electricalAge", integrations, true, "Electrical Age Integration.") && isModLoaded("Eln");
            enderIOIntegration = config.getBoolean("enderIO", integrations, true, "Ender IO Integration.") && isModLoaded("EnderIO");
            forestryIntegration = config.getBoolean("forestry", integrations, true, "Forestry Integration.") && forestry;
            foundryIntegration = config.getBoolean("foundry", integrations, true, "Foundry Integration.") && isModLoaded("foundry");
            fspIntegration = config.getBoolean("flaxbeardsSteamPower", integrations, true, "Flaxbeard's Steam Power Integration.") && isModLoaded("Steamcraft");
            hydrauliCraftIntegration = config.getBoolean("hydrauliCraft", integrations, true, "Hydraulicraft Integration.") && isModLoaded("HydCraft");
            ic2Integration = config.getBoolean("industrialCraft2", integrations, true, "Industrial Craft 2 Integration.") && ic2;
            ieIntegration = config.getBoolean("immersiveEngineering", integrations, true, "Immersive Engineering Integration.") && isModLoaded("ImmersiveEngineering");
            magnetiCraftIntegration = config.getBoolean("magnetiCraft", integrations, true, "Magneticraft Integration.") && isModLoaded("Magneticraft");
            mekanismIntegration = config.getBoolean("mekanism", integrations, true, "Mekanism Integration.") && isModLoaded("Mekanism");
            railCraftIntegration = config.getBoolean("railcraft", integrations, true, "Railcraft Integration.") && isModLoaded("Railcraft");
            teIntegration = config.getBoolean("thermalExpansion", integrations, true, "Thermal Expansion Integration.") && isModLoaded("ThermalExpansion");
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

    public static TObjectLongMap<String> getOwnerOfEveryKindMap(long kind)
    {
        final String kindName = WordUtils.capitalize(Resource.getNameOfKind(kind));
        final String[] ownerOfEveryKind = config.getStringList("ownerOfEvery" + kindName, resources, new String[]{"ThermalFoundation", "minecraft", "IC2", "TConstruct", "Mekanism", "Magneticraft"}, "entries of kind \"" + kindName + "\" will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectLongMap<String> ownerOfEveryThingMap = new TObjectLongHashMap<>(10, 1, Long.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryKind.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryKind[i], i);
        return ownerOfEveryThingMap;
    }

    private static TObjectLongMap<String> getOwnerOfEveryThingMap()
    {
        final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"ThermalFoundation", "minecraft", "IC2", "TConstruct", "Mekanism", "Magneticraft"}, "all the entries will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectLongMap<String> ownerOfEveryThingMap = new TObjectLongHashMap<>(10, 1, Long.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryThing.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryThing[i], i);
        return ownerOfEveryThingMap;
    }

    private static Map<String, Set<String>> getCustomUnifiedResourcesMap()
    {
        Map<String, Set<String>> customUnifiedResources = new THashMap<>();
        Pattern splitPattern = Pattern.compile("\\|");
        for (String customUnifiedResource : config.getStringList("customUnifiedResources", resources, new String[]{"Obsidian:dustTiny|dust", "Stone:dust"}, "Here you can put a list to custom unify them.\nmay break some recipes.\nmust be in this format \"ResourceName:kind1|kind2|...\".\nif you put gems here, be aware that it will include the \"block\" of that gem too.")) {
            int baseSeparatorIndex;
            Set<String> kindSet;
            if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newLinkedHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1, customUnifiedResource.length()))))).isEmpty())
                customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
        }
        return customUnifiedResources;
    }
}