package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.helper.GearHelper;
import wanion.unidict.helper.LogHelper;
import wanion.unidict.helper.MagicalCropsHelper;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static wanion.unidict.Config.*;

final class CraftingIntegration extends AbstractIntegrationThread
{
    private final List<IRecipe> recipes = RecipeHelper.recipes;
    private final MagicalCropsHelper magicalCropsHelper = (magicalCrops) ? UniDict.getDependencies().get(MagicalCropsHelper.class) : null;
    private final FMLControlledNamespacedRegistry<Item> gameRegistry = GameData.getItemRegistry();
    private final int ore = Resource.getKindOfName("ore");
    private final int dust = Resource.getKindOfName("dust");
    private final int nugget = Resource.getKindOfName("nugget");
    private final int ingot = Resource.getKindOfName("ingot");
    private final int block = Resource.getKindOfName("block");
    private final int gear = Resource.getKindOfName("gear");
    private final int plate = Resource.getKindOfName("plate");
    private final int dustTiny = Resource.getKindOfName("dustTiny");
    private final int gem = Resource.getKindOfName("gem");
    private final GearHelper gearHelper = (Config.gearRecipesRequiresSomeGear && gear != 0) ? new GearHelper() : null;
    private final List<Resource> gears = (gear != 0 && gearHelper == null) ? resourceHandler.getResources(gear, ingot) : null;
    private final List<Resource> plates = (plate != 0 && ingot != 0 && (ic2Integration || ieIntegration)) ? resourceHandler.getResources(plate, ingot) : null;
    private final List<Resource> dustsAndOres = (ore != 0 && dust != 0 && ieIntegration) ? resourceHandler.getResources(ore, dust) : null;
    private final List<Resource> dustTinyAndDust = (dustTiny != 0 && dust != 0) ? resourceHandler.getResources(dustTiny, dust) : null;
    private final List<Resource> gems = (gem != 0) ? resourceHandler.getResources(gem, block) : null;
    private final UniResourceContainer dustBronze = resourceHandler.getContainer("dustBronze");
    private final UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
    private final UniResourceContainer dustBrass = resourceHandler.getContainer("dustBrass");
    private final UniResourceContainer dustInvar = resourceHandler.getContainer("dustInvar");

    CraftingIntegration()
    {
        super("Crafting");
        RecipeHelper.init();
    }

    @Override
    public String call()
    {
        int initialSize = recipes.size();
        try {
            RecipeHelper.singleWayCompressionRecipe(dustTinyAndDust, dustTiny, dust);
            RecipeHelper.resourcesToCompressionRecipes(resourceHandler.resources, nugget, ingot, block);
            if (gems != null)
                RecipeHelper.resourcesToCompressionRecipes(gems, nugget, gem, block);
            if (gear != 0) {
                if (gearHelper == null)
                    createGearRecipes();
                else if (gears == null)
                    recipes.addAll(gearHelper.recipes);
            }
            if (plates != null && ic2Integration)
                createForgeHammerRecipes();
            if (ieIntegration)
                createEngineeringHammerRecipes();
            if (dustBronze != null)
                recipes.add(new ShapelessOreRecipe(dustBronze.getMainEntry(4), "dustCopper", "dustCopper", "dustCopper", "dustTin"));
            if (ingotBronze != null && forestry)
                recipes.add(new ShapelessOreRecipe(ingotBronze.getMainEntry(4), "ingotCopper", "ingotCopper", "ingotCopper", "ingotTin"));
            if (dustBrass != null)
                recipes.add(new ShapelessOreRecipe(dustBrass.getMainEntry(4), "dustCopper", "dustCopper", "dustCopper", "dustZinc"));
            if (dustInvar != null)
                recipes.add(new ShapelessOreRecipe(dustInvar.getMainEntry(3), "dustIron", "dustIron", "dustNickel"));
            if (magicalCropsHelper != null && ore != 0 && ingot != 0)
                createMagicalCropsRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "Why so many recipes? I had to deal with " + (recipes.size() - initialSize) + " recipes.";
    }

    private void createGearRecipes()
    {
        for (Resource resource : gears)
            recipes.add(new ShapedOreRecipe(resource.getChild(gear).getMainEntry(1), " I ", "IFI", " I ", 'I', "ingot" + resource.name, 'F', "ingotIron"));
    }

    private void createForgeHammerRecipes()
    {
        for (Resource resource : plates)
            recipes.add(new ShapelessOreRecipe(resource.getChild(plate).getMainEntry(1), new ItemStack(gameRegistry.getRaw("IC2:itemToolForgeHammer"), 1, OreDictionary.WILDCARD_VALUE), resource.getChild(ingot).name));
    }

    private void createEngineeringHammerRecipes()
    {
        Item engineerHammer = gameRegistry.getRaw("ImmersiveEngineering:tool");
        if (plates != null)
            for (Resource resource : plates)
                recipes.add(new ShapelessOreRecipe(resource.getChild(plate).getMainEntry(1), new ItemStack(engineerHammer, 1, 0), resource.getChild(ingot).name));
        if (dustsAndOres != null) {
            int stackSize = engineerHammerDust;
            for (Resource resource : dustsAndOres)
                recipes.add(new ShapelessOreRecipe(resource.getChild(dust).getMainEntry(stackSize), new ItemStack(engineerHammer, 1, 0), resource.getChild(ore).name));
        }
    }

    private void createMagicalCropsRecipes()
    {
        Map<String, Item> magicalThings = new LinkedHashMap<>(27, 1);
        for (String resourceName : resourceHandler.resourceMap.keySet()) {
            Item magicalItem = gameRegistry.getRaw("magicalcrops:magicalcrops_" + resourceName + "Essence");
            if (magicalItem != null)
                magicalThings.put(resourceName, magicalItem);
        }
        Item aluminumEssence = (resourceHandler.resourceExists("Aluminum")) ? gameRegistry.getRaw("magicalcrops:magicalcrops_AluminiumEssence") : null;
        if (aluminumEssence != null)
            magicalThings.put("Aluminum", aluminumEssence);
        for (String resourceName : magicalThings.keySet()) {
            UniResourceContainer container;
            if ((container = resourceHandler.getContainer("ore" + resourceName)) == null)
                if ((container = resourceHandler.getContainer("ingot" + resourceName)) == null)
                    if ((container = resourceHandler.getContainer("gem" + resourceName)) == null)
                        continue;
            ItemStack input = new ItemStack(magicalThings.get(resourceName));
            ItemStack[] inputs = new ItemStack[9];
            int i = 0;
            while (i < inputs.length)
                inputs[i++] = input.copy();
            recipes.add(new ShapedRecipes(3, 3, inputs, container.getMainEntry(magicalCropsHelper.magicalEssence.get(resourceName))));
        }
    }
}