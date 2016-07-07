package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.map.hash.THashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static wanion.unidict.Config.gearRecipesUsesIngotsInsteadOfPlates;
import static wanion.unidict.Config.useBaseMetalsShapeForGears;

public final class GearHelper
{
    private final List<IRecipe> realRecipes = new ArrayList<>();
    public final List<IRecipe> recipes = Collections.unmodifiableList(realRecipes);
    private final Map<String, String> someMadMap;
    private final long gear = Resource.getKindOfName("gear");
    private final long ingot = Resource.getKindOfName("ingot");
    private final long rod = Resource.getKindOfName("rod");
    private final long plate = Resource.getKindOfName("plate");
    private final UniDictAPI uniDictAPI = UniDict.getAPI();
    private final Resource iron = uniDictAPI.getResource("Iron");

    public GearHelper(final boolean gearVersion)
    {
        someMadMap = (gearVersion) ? new THashMap<>() : null;
        createAllTheRecipes();
    }

    private void createAllTheRecipes()
    {
        final String ironKind = (gearRecipesUsesIngotsInsteadOfPlates) ? "ingotIron" : "plateIron";
        final Object[] shape = (OreDictionary.doesOreNameExist("gearStone")) ? new Object[]{" I ", "ILI", " I ", 'I', ironKind, 'L', "gearStone"} : new Object[]{" I ", "III", " I ", 'I', ironKind};
        realRecipes.add(new ShapedOreRecipe(iron.getChild(gear).getMainEntry(), shape));
        if (someMadMap != null) {
            someMadMap.put("Invar", "Tin");
            someMadMap.put("Electrum", "Tin");
            someMadMap.put("Signalum", "Electrum");
            someMadMap.put("Enderium", "Signalum");
        }
        final List<Resource> resourceList = (useBaseMetalsShapeForGears)
                ? (gearRecipesUsesIngotsInsteadOfPlates) ? uniDictAPI.getResources(gear, ingot, rod) : uniDictAPI.getResources(gear, plate, rod)
                : (gearRecipesUsesIngotsInsteadOfPlates) ? uniDictAPI.getResources(gear, ingot) : uniDictAPI.getResources(gear, ingot, plate);
        resourceList.remove(iron);
        final long borderKind = (gearRecipesUsesIngotsInsteadOfPlates) ? ingot : plate;
        if (someMadMap != null) {
            for (Resource resource : resourceList)
                createGearWithGearRecipe(resource, borderKind);
            return;
        }
        final long middleKind = (useBaseMetalsShapeForGears) ? rod : ingot;
        resourceList.forEach(resource -> createGearRecipe(resource, borderKind, middleKind));
    }

    private void createGearWithGearRecipe(final Resource resource, final long borderKind)
    {
        final String name = resource.name;
        final Resource targetResource = someMadMap.containsKey(name) ? uniDictAPI.getResource(name) : iron;
        realRecipes.add(new ShapedOreRecipe(resource.getChild(gear).getMainEntry(), " I ", "ILI", " I ", 'I', resource.getChild(borderKind).name, 'L', targetResource.getChild(gear).name));
    }

    private void createGearRecipe(final Resource resource, final long borderKind, final long middleKind)
    {
        realRecipes.add(new ShapedOreRecipe(resource.getChild(gear).getMainEntry(), " I ", "ILI", " I ", 'I', resource.getChild(borderKind).name, 'L', resource.getChild(middleKind).name));
    }
}