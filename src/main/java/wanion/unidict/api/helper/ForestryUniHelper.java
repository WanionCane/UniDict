package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemCrated;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CarpenterRecipeManager;
import forestry.plugins.PluginStorage;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ForestryUniHelper
{
    private static final Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);

    private ForestryUniHelper() {}

    public static void createCrates(@Nonnull final String kindName)
    {
        final Map<UniResourceContainer, ItemCrated> crates = new LinkedHashMap<>();
        final long kind = Resource.getKindOfName(kindName);
        if (kind == 0)
            return;
        for (final Resource resource : UniDict.getResourceHandler().resources) {
            final UniResourceContainer container = resource.getChild(kind);
            if (container != null)
                crates.put(container, new ItemCrated(container.getMainEntry(), true));
        }
        registerCratesAndCreateRecipes(crates);
    }

    public static void registerCratesAndCreateRecipes(@Nonnull final Map<UniResourceContainer, ItemCrated> crates)
    {
        if (carpenterRecipes == null)
            return;
        for (final UniResourceContainer container : crates.keySet()) {
            final ItemCrated crate = crates.get(container);
            crate.setUnlocalizedName(WordUtils.capitalize(container.name));
            GameRegistry.registerItem(crate, crate.getUnlocalizedName().replaceFirst("^item\\.", "crated"));
            PluginStorage.registerCrate(crate);
            carpenterRecipes.add(new CarpenterRecipe(5, Fluids.WATER.getFluid(100), null, new ShapedRecipeCustom(new ItemStack(crate), "III", "III", "III", 'I', container.name)));
            carpenterRecipes.add(new CarpenterRecipe(5, null, null, new ShapedRecipeCustom(container.getMainEntry(9), "C  ", "   ", "   ", 'C', crate.getItemStack())));
        }
    }
}