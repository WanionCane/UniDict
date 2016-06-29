package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cpw.mods.fml.common.Loader;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GearHelper
{
    public final List<IRecipe> recipes = new ArrayList<>();
    private final Map<String, String> someMadMap = new THashMap<>();
    private final int gear = Resource.getKindOfName("gear");
    private final int ingot = Resource.getKindOfName("ingot");
    private final String iron = "Iron";
    private final UniDictAPI uniDictAPI = UniDict.getAPI();

    public GearHelper()
    {
        createAllTheRecipes();
    }

    private void createAllTheRecipes()
    {
        Object[] shape = (Loader.isModLoaded("BuildCraft|Core")) ? new Object[]{" I ", "ILI", " I ", 'I', "ingotIron", 'L', "gearStone"} : new Object[]{" I ", "III", " I ", 'I', "ingotIron"};
        Resource iron = uniDictAPI.getResource(this.iron);
        recipes.add(new ShapedOreRecipe(iron.getChild(gear).getMainEntry(), shape));
        someMadMap.put("Invar", "Tin");
        someMadMap.put("Electrum", "Tin");
        someMadMap.put("Signalum", "Electrum");
        someMadMap.put("Enderium", "Signalum");
        List<Resource> resourceList = uniDictAPI.getResources(gear, ingot);
        resourceList.remove(iron);
        for (Resource resource : resourceList)
            createGearRecipe(resource);
    }

    private void createGearRecipe(Resource resource)
    {
        final String name = resource.name;
        Resource targetResource = uniDictAPI.getResource(someMadMap.containsKey(name) ? someMadMap.get(name) : iron);
        recipes.add(new ShapedOreRecipe(resource.getChild(gear).getMainEntry(), " I ", "ILI", " I ", 'I', resource.getChild(ingot).name, 'L', targetResource.getChild(gear).name));
    }
}