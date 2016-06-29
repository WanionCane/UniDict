package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.google.common.collect.Sets;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.common.Reference;
import wanion.unidict.helper.LogHelper;
import wanion.unidict.resource.Resource;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static cpw.mods.fml.common.Loader.isModLoaded;

public final class Config
{
    // config
    private static final Configuration config = new Configuration(new File(Reference.MOD_FOLDER + Reference.MOD_NAME + ".cfg"), Reference.MOD_VERSION);

    // ensure mod loaded
    public static boolean exNihilo;
    public static boolean forestry;
    public static boolean foundry;
    public static boolean magicalCrops;
    public static boolean tinkersConstruct;

    // general configs
    private static final String general = Configuration.CATEGORY_GENERAL;
    public static final boolean keepOneEntry = config.getBoolean("keepOneEntry", general, false, "keep only one entry per ore dict entry?");
    public static final Set<String> keepOneEntryModBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("keepOneEntryModBlackList", Configuration.CATEGORY_GENERAL, new String[]{}, "mods listed here will be blacklisted in keepOneEntry.\nmust be the exact modID."))));
    public static boolean autoHideInNEI;

    public static final Set<String> hideInNEIBlackSet = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("autoHideInNEIBlackList", general, new String[]{"ore"}, "put here things that you don't want to hide in NEI.\nonly works if keepOneEntry is false."))));

    // resource related stuff
    private static final String resources = "resources";
    public static final boolean enableSpecificKindSort = config.getBoolean("enableSpecificKindSort", resources, false, "enabling this allow you to specify the \"owner\" of each kind.\nalso will make \"S:ownerOfEveryThing\" be ignored.");
    public static final TObjectIntMap<String> ownerOfEveryThing = new TUnmodifiableObjectIntMap<>((!enableSpecificKindSort) ? getOwnerOfEveryThingMap() : new TObjectIntHashMap<String>());
    public static final Set<String> metalsToUnify = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("metalsToUnify", resources, new String[]{"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Aluminum", "Aluminium", "Ardite", "Cobalt", "Osmium", "Mithril", "Zinc", "Invar", "Steel", "Bronze", "Electrum", "Brass"}, "list of things to do unifying things.\n"))));
    public static final Set<String> childrenOfMetals = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList(config.getStringList("childrenOfMetals", resources, new String[]{"ore", "dustTiny", "chunk", "dust", "nugget", "ingot", "block", "plate", "gear"}, "what kind of child do you want to make a standard?\n"))));
    public static final List<String> resourceBlackList = Arrays.asList(config.getStringList("resourceBlackList", resources, new String[]{"Aluminium"}, "resources to be black-listed.\nthis exists to avoid duplicates.\nthis affect the API."));
    public static final Map<String, Set<String>> customUnifiedResources = Collections.unmodifiableMap(getCustomUnifiedResourcesMap());

    // modules
    private static final String modules = "modules";
    private static final String externalModules = "externalModules";
    static final boolean integrationModule = config.getBoolean("integration", modules, true, "Integration Module enabled?\nif false all the Integrations will be disabled.\nthis will affect non-standalone tweak.\n");
    static final boolean tweakModule = config.getBoolean("tweak", modules, true, "Tweak Module enabled?\nif false all standalone Tweaks will be disabled.\n");
    static final boolean loadExternalModules = config.getBoolean("loadExternalModules", externalModules, true, "External Modules enabled?\nif false UniDict won't will load any external module.\n");

    // recipe tweaks
    private static final String recipeTweaks = "recipeTweaks";
    public static final boolean gearRecipesRequiresSomeGear = config.getBoolean("gearRecipesRequiresSomeGear", recipeTweaks, false, "change the gear recipes to use some gear as requirement?\nalso will remove the alternative gear recipes.");
    public static final int engineerHammerDust = config.getInt("engineerHammerDust", recipeTweaks, 1, 1, 64, "how many dusts will be created?");

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
    private  static final String vanillaIntegrations = "vanillaIntegrations";
    public static final boolean chestIntegration = config.getBoolean("chestIntegration", vanillaIntegrations, true, "Chest Integration? (\"dungeon chest, nether fortress chests, etc...\").");
    public static final boolean craftingIntegration = config.getBoolean("craftingIntegration", vanillaIntegrations, true, "Crafting Integration");
    public static final boolean furnaceIntegration = config.getBoolean("furnaceIntegration", vanillaIntegrations, true, "Furnace Integration");

    // tweak
    public static boolean forestryTweak;
    public static boolean exNihiloTweak;

    static void init()
    {
        boolean deleted = false;
        try {
            if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion()))
                deleted = config.getConfigFile().delete();

            // ensure mod loaded
            exNihilo = isModLoaded("exnihilo");
            forestry = isModLoaded("Forestry");
            foundry = isModLoaded("foundry");
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
            ic2Integration = config.getBoolean("industrialCraft2", integrations, true, "Industrial Craft 2 Integration.") && isModLoaded("IC2");
            ieIntegration = config.getBoolean("immersiveEngineering", integrations, true, "Immersive Engineering Integration.") && isModLoaded("ImmersiveEngineering");
            magnetiCraftIntegration = config.getBoolean("magnetiCraft", integrations, true, "Magneticraft Integration.") && isModLoaded("Magneticraft");
            mekanismIntegration = config.getBoolean("mekanism", integrations, true, "Mekanism Integration.") && isModLoaded("Mekanism");
            railCraftIntegration = config.getBoolean("railcraft", integrations, true, "Railcraft Integration.") && isModLoaded("Railcraft");
            teIntegration = config.getBoolean("thermalExpansion", integrations, true, "Thermal Expansion Integration.") && isModLoaded("ThermalExpansion");

            magicalCrops = craftingIntegration && isModLoaded("magicalcrops");

            // recipe tweaks
            config.setCategoryComment(recipeTweaks, "everything in this category requires \"Crafting Integration\" to work.");
            // tweak
            final String tweaks = "tweak";
            forestryTweak = config.getBoolean("forestry", tweaks, false, "UniDict best ingots to crates.");
            exNihiloTweak = config.getBoolean("exNihilo", tweaks, false, "re-allow broken/crushed/powdered;\nthings to be molten in smeltery.\nlike was in 1.6.4\nstandalone") && exNihilo && tinkersConstruct;
        } catch (Exception e) {
            LogHelper.info("Something went wrong on " + config.getConfigFile() + "loading. " + e);
        }
        if (config.hasChanged() || deleted)
            config.save();
    }

    public static void saveIfHasChanged()
    {
        if (config.hasChanged())
            config.save();
    }

    public static TObjectIntMap<String> getOwnerOfEveryKindMap(int kind)
    {
        final String kindName = WordUtils.capitalize(Resource.getNameOfKind(kind));
        final String[] ownerOfEveryKind = config.getStringList("ownerOfEvery" + kindName, resources, new String[]{"ThermalFoundation", "minecraft", "IC2", "TConstruct", "Mekanism", "Magneticraft"}, "entries of kind \"" + kindName + "\" will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectIntMap<String> ownerOfEveryThingMap = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryKind.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryKind[i], i);
        return ownerOfEveryThingMap;
    }

    private static TObjectIntMap<String> getOwnerOfEveryThingMap()
    {
        final String[] ownerOfEveryThing = config.getStringList("ownerOfEveryThing", resources, new String[]{"ThermalFoundation", "minecraft", "IC2", "TConstruct", "Mekanism", "Magneticraft"}, "all the entries will be sorted according to the modID list below\nmust be the exact modID.\n");
        final TObjectIntMap<String> ownerOfEveryThingMap = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
        for (int i = 0; i < ownerOfEveryThing.length; i++)
            ownerOfEveryThingMap.put(ownerOfEveryThing[i], i);
        return ownerOfEveryThingMap;
    }

    private static Map<String, Set<String>> getCustomUnifiedResourcesMap()
    {
        Map<String, Set<String>> customUnifiedResources = new THashMap<>();
        Pattern splitPattern = Pattern.compile("\\|");
        for (String customUnifiedResource : config.getStringList("customUnifiedResources", resources, new String[]{"Obsidian:dustTiny|dust"}, "Here you can put a list to custom unify them.\nmay break some recipes.\nmust be in this format \"ResourceName:kind1|kind2|...\".\nif you put gems here, be aware that it will include the \"block\" of that gem too.")) {
            int baseSeparatorIndex;
            Set<String> kindSet;
            if ((baseSeparatorIndex = customUnifiedResource.indexOf(':')) != -1 && !(kindSet = Sets.newLinkedHashSet(Arrays.asList(splitPattern.split(customUnifiedResource.substring(baseSeparatorIndex + 1, customUnifiedResource.length()))))).isEmpty())
                customUnifiedResources.put(customUnifiedResource.substring(0, baseSeparatorIndex), kindSet);
        }
        return customUnifiedResources;
    }

    static boolean specificModuleEnabled(String name)
    {
        try {
            return config.get(externalModules, name, true).getBoolean();
        } finally {
            if (config.hasChanged())
                config.save();
        }
    }
}