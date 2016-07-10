package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class RecipeHelper
{
    public static final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

    private RecipeHelper() {}

    public static void singleWayCompressionRecipe(@Nonnull final List<Resource> smallerAndBiggerResources, final long smaller, final long bigger)
    {
        smallerAndBiggerResources.forEach(r -> recipes.add(new ShapedOreRecipe(r.getChild(bigger).getMainEntry(), "SSS", "SSS", "SSS", 'S', r.getChild(smaller).name)));
    }

    public static void resourcesToCompressionRecipes(@Nonnull final Collection<Resource> resources, final long... smallerToBigger)
    {
        UniResourceContainer smaller, bigger;
        for (Resource resource : resources)
            for (int i = 0; i < smallerToBigger.length - 1; i++)
                if ((smaller = resource.getChild(smallerToBigger[i])) != null && (bigger = resource.getChild(smallerToBigger[i + 1])) != null)
                    createCompressionRecipe(smaller, bigger);
    }

    private static void createCompressionRecipe(@Nonnull final UniResourceContainer smaller, final UniResourceContainer bigger)
    {
        recipes.add(new ShapedOreRecipe(bigger.getMainEntry(), "SSS", "SSS", "SSS", 'S', smaller.name));
        recipes.add(new ShapelessOreRecipe(smaller.getMainEntry(9), bigger.name));
    }
}