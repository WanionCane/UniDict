package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.helper.GearHelper;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import java.util.List;
import java.util.stream.Collectors;

import static wanion.unidict.Config.*;

final class CraftingIntegration extends AbstractIntegrationThread
{
    private final List<IRecipe> recipes = RecipeHelper.recipes;
    private final FMLControlledNamespacedRegistry<Item> itemRegistry = MetaItem.itemRegistry;
    private final long dust = Resource.getKindOfName("dust");
    private final long nugget = Resource.getKindOfName("nugget");
    private final long ingot = Resource.getKindOfName("ingot");
    private final long block = Resource.getKindOfName("block");
    private final long gear = Resource.getKindOfName("gear");
    private final long plate = Resource.getKindOfName("plate");
    private final long rod = Resource.getKindOfName("rod");
    private final long dustTiny = Resource.getKindOfName("dustTiny");
    private final long gem = Resource.getKindOfName("gem");
    private final GearHelper gearHelper = (gear != 0) ? new GearHelper(gearRecipesRequiresSomeGear) : null;
    private final List<Resource> plates = (plate != 0 && ingot != 0) ? resourceHandler.getResources(plate, ingot) : null;
    private final List<Resource> rods = (rod != 0 && ingot != 0) ? resourceHandler.getResources(rod, ingot) : null;
    private final List<Resource> dustTinyAndDust = (dustTiny != 0 && dust != 0) ? resourceHandler.getResources(dustTiny, dust) : null;
    private final List<Resource> gems = (gem != 0) ? resourceHandler.getResources(gem, block) : null;
    private final UniResourceContainer dustBronze = resourceHandler.getContainer("dustBronze");
    private final UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
    private final UniResourceContainer dustBrass = resourceHandler.getContainer("dustBrass");
    private final UniResourceContainer dustInvar = resourceHandler.getContainer("dustInvar");
    private final UniResourceContainer oreIron = techReborn ? resourceHandler.getContainer("oreIron") : null;
    private final UniResourceContainer oreGold = techReborn ? resourceHandler.getContainer("oreGold") : null;
    private final UniResourceContainer oreEmerald = techReborn ? resourceHandler.getContainer("oreEmerald") : null;
    private final UniResourceContainer gemEmerald = techReborn ? resourceHandler.getContainer("gemEmerald") : null;
    private final UniResourceContainer gemDiamond = techReborn ? resourceHandler.getContainer("gemDiamond") : null;
    private final UniResourceContainer ingotIridium = techReborn ? resourceHandler.getContainer("ingotIridium") : null;

    CraftingIntegration()
    {
        super("Crafting");
        RecipeHelper.init();
    }

    @Override
    public String call()
    {
        final int initialSize = recipes.size();
        try {
            if (dustTinyAndDust != null)
                RecipeHelper.singleWayCompressionRecipe(dustTinyAndDust, dustTiny, dust);
            RecipeHelper.resourcesToCompressionRecipes(resourceHandler.resources, nugget, ingot, block);
            if (gems != null)
                RecipeHelper.resourcesToCompressionRecipes(gems, nugget, gem, block);
            if (gearHelper != null)
                recipes.addAll(gearHelper.recipes);
            if (rod != 0)
                createRodRecipes();
            if (techReborn)
                createUURecipes();
            if (dustBronze != null)
                recipes.add(new ShapelessOreRecipe(dustBronze.getMainEntry(4), "dustCopper", "dustCopper", "dustCopper", "dustTin"));
            if (ingotBronze != null)
                recipes.add(new ShapelessOreRecipe(ingotBronze.getMainEntry(4), "ingotCopper", "ingotCopper", "ingotCopper", "ingotTin"));
            if (dustBrass != null)
                recipes.add(new ShapelessOreRecipe(dustBrass.getMainEntry(4), "dustCopper", "dustCopper", "dustCopper", "dustZinc"));
            if (dustInvar != null)
                recipes.add(new ShapelessOreRecipe(dustInvar.getMainEntry(3), "dustIron", "dustIron", "dustNickel"));
            if (plates != null)
                createPlateRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "Why so many recipes? I had to deal with " + (recipes.size() - initialSize) + " recipes.";
    }

    private void createPlateRecipes()
    {
        final int platesResult = howManyPlatesWillBeCreatedPerRecipe;
        plates.forEach(r -> new ShapedOreRecipe(r.getChild(plate).getMainEntry(platesResult), "III", "   ", "   ", 'I', r.getChild(ingot).name));
        if (ic2)
            createForgeHammerRecipes();
    }

    private void createForgeHammerRecipes()
    {
        final Item forgeHammer = itemRegistry.getObject(new ResourceLocation("ic2:forge_hammer"));
        plates.forEach(r -> new ShapelessOreRecipe(r.getChild(plate).getMainEntry(1), new ItemStack(forgeHammer, 1, OreDictionary.WILDCARD_VALUE), r.getChild(ingot).name));
    }

    private void createRodRecipes()
    {
        final int ingotsRequired = howManyIngotsWillBeRequiredToCreateAnRod;
        final int rodsPerRecipe = howManyRodsWillBeCreatedPerRecipe;
        recipes.addAll(rods.stream().map(resource -> new ShapedOreRecipe(resource.getChild(rod).getMainEntry(rodsPerRecipe), (ingotsRequired == 3) ? new Object[]{"I  ", "I  ", "I  ", 'I', resource.getChild(ingot).name} : new Object[]{"I  ", "I  ", "   ", 'I', resource.getChild(ingot).name})).collect(Collectors.toList()));
    }

    private void createUURecipes()
    {
        final ItemStack UUMatter = new ItemStack(itemRegistry.getObject(new ResourceLocation("techreborn:uumatter")));
        if (oreIron != null)
            recipes.add(new ShapedOreRecipe(oreIron.getMainEntry(2), "U U", " U ", "U U", 'U', UUMatter));
        if (oreGold != null)
            recipes.add(new ShapedOreRecipe(oreGold.getMainEntry(2), " U ", "UUU", " U ", 'U', UUMatter));
        if (oreEmerald != null)
            recipes.add(new ShapedOreRecipe(oreEmerald.getMainEntry(1), "UU ", "U U", " UU", 'U', UUMatter));
        if (gemEmerald != null)
            recipes.add(new ShapedOreRecipe(gemEmerald.getMainEntry(2), "UUU", "UUU", " U ", 'U', UUMatter));
        if (gemDiamond != null)
            recipes.add(new ShapedOreRecipe(gemDiamond.getMainEntry(), "UUU", "UUU", "UUU", 'U', UUMatter));
        if (ingotIridium != null)
            recipes.add(new ShapedOreRecipe(ingotIridium.getMainEntry(), "UUU", " U ", "UUU", 'U', UUMatter));
    }
}