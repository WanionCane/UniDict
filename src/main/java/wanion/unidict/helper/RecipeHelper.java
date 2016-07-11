package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.sun.istack.internal.NotNull;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.hash.TObjectCharHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecipeHelper
{
    public static final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
    private static final char[] DEFAULT_RECIPE_CHARS = {' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[][] SHAPE = new char[][]{{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};

    private RecipeHelper() {}

    @NotNull
    public static Object[] rawShapeToShape(@Nonnull final Object[] objects)
    {
        int f = 1;
        final char[][] almostTheShape = Arrays.copyOf(SHAPE, 3);
        final TObjectCharMap<Object> thingToCharMap = new TObjectCharHashMap<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int value = x * 3 + y;
                if (objects[value] == null) {
                    almostTheShape[x][y] = DEFAULT_RECIPE_CHARS[0];
                    continue;
                }
                final Object key = objects[value] instanceof ItemStack ? MetaItem.get((ItemStack) objects[value]) : objects[value];
                if (thingToCharMap.containsKey(key))
                    almostTheShape[x][y] = thingToCharMap.get(key);
                else
                    thingToCharMap.put(key, almostTheShape[x][y] = DEFAULT_RECIPE_CHARS[f++]);
            }
        }
        final Object[] shape = Arrays.copyOf(new Object[]{new String(almostTheShape[0]), new String(almostTheShape[1]), new String(almostTheShape[2])}, 3 + thingToCharMap.size() * 2);
        int i = 0;
        for (final Object object : thingToCharMap.keySet()) {
            shape[3 + (2 * i)] = thingToCharMap.get(object);
            shape[4 + (2 * i++)] = (object instanceof Integer) ? MetaItem.toItemStack((Integer) object) : object;
        }
        return shape;
    }

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