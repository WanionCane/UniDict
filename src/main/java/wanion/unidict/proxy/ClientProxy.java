package wanion.unidict.proxy;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import wanion.lib.common.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientProxy extends CommonProxy
{
	private final Map<CreativeTabs, List<RecipeList>> RECIPES_BY_TAB = Util.getField(RecipeBookClient.class, "RECIPES_BY_TAB", "field_194086_e", null, Map.class);
	private final List<RecipeList> ALL_RECIPES = Util.getField(RecipeBookClient.class, "ALL_RECIPES", "field_194087_f", null, List.class);

	@Override
	public void postInit(final FMLPostInitializationEvent event)
	{
		super.postInit(event);

		RECIPES_BY_TAB.clear();
		ALL_RECIPES.clear();

		final Table<CreativeTabs, String, RecipeList> table = HashBasedTable.create();

		for (IRecipe irecipe : CraftingManager.REGISTRY) {
			if (!irecipe.isDynamic()) {
				CreativeTabs creativetabs = getItemStackTab(irecipe.getRecipeOutput());
				String s = irecipe.getGroup();
				RecipeList recipelist1;

				if (s.isEmpty()) {
					recipelist1 = newRecipeList(creativetabs);
				} else {
					recipelist1 = table.get(creativetabs, s);

					if (recipelist1 == null) {
						recipelist1 = newRecipeList(creativetabs);
						table.put(creativetabs, s, recipelist1);
					}
				}

				recipelist1.add(irecipe);
			}
		}
	}

	private RecipeList newRecipeList(CreativeTabs creativeTabs)
	{
		final RecipeList recipelist = new RecipeList();
		ALL_RECIPES.add(recipelist);
		(RECIPES_BY_TAB.computeIfAbsent(creativeTabs, (p_194085_0_) -> new ArrayList<>())).add(recipelist);
		(RECIPES_BY_TAB.computeIfAbsent(CreativeTabs.SEARCH, (p_194083_0_) -> new ArrayList<>())).add(recipelist);
		return recipelist;
	}

	private CreativeTabs getItemStackTab(ItemStack itemStack)
	{
		CreativeTabs creativetabs = itemStack.getItem().getCreativeTab();
		if (creativetabs != CreativeTabs.BUILDING_BLOCKS && creativetabs != CreativeTabs.TOOLS && creativetabs != CreativeTabs.REDSTONE) {
			return creativetabs == CreativeTabs.COMBAT ? CreativeTabs.TOOLS : CreativeTabs.MISC;
		} else {
			return creativetabs;
		}
	}
}