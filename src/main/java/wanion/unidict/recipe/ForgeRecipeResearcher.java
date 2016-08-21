package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ForgeRecipeResearcher implements IRecipeResearcher<ShapedOreRecipe, ShapelessOreRecipe>
{
    @Override
    public int getShapedRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapedOreRecipe) recipe).getInput(), resourceHandler) + MetaItem.get(resourceHandler.getMainItemStack(recipe.getRecipeOutput()));
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapelessOreRecipe) recipe).getInput().toArray(), resourceHandler) + MetaItem.get(resourceHandler.getMainItemStack(recipe.getRecipeOutput()));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public List<Class<? extends ShapedOreRecipe>> getShapedRecipeClasses()
    {
        Class<? extends ShapedOreRecipe> shapedCustomRecipe = null;
        try {
            if (Loader.isModLoaded("Forestry"))
                shapedCustomRecipe = (Class<? extends ShapedOreRecipe>) Class.forName("forestry.core.recipes.ShapedRecipeCustom");
        } catch (ClassNotFoundException e) { UniDict.getLogger().error(e); }
        return shapedCustomRecipe == null ? Collections.singletonList(ShapedOreRecipe.class) : Arrays.asList(ShapedOreRecipe.class, shapedCustomRecipe);
    }

    @Override
    @Nonnull
    public List<Class<? extends ShapelessOreRecipe>> getShapelessRecipeClasses()
    {
        return Collections.singletonList(ShapelessOreRecipe.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] recipeInputs = ((ShapedOreRecipe) recipe).getInput();
        final Object[] newRecipeInputs = new Object[9];
        for (int i = 0; i < 9; i++) {
            final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
            final String bufferOreName = input != null ? input instanceof List ? uniOreDictionary.getName(input) : input instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) input) : null : null;
            newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : input : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] recipeInputs = ((ShapelessOreRecipe) recipe).getInput().toArray();
        final Object[] newRecipeInputs = new Object[recipeInputs.length];
        for (int i = 0; i < recipeInputs.length; i++) {
            final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
            final String bufferOreName = input != null ? input instanceof List ? uniOreDictionary.getName(input) : input instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) input) : null : null;
            newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : input : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        ((ShapelessOreRecipe) recipe).getInput().forEach(object -> {
            if (object != null) {
                final String bufferOreName = object instanceof List ? uniOreDictionary.getName(object) : object instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) object) : null;
                if (bufferOreName != null)
                    inputs.add(bufferOreName);
                else if (object instanceof ItemStack)
                    inputs.add(object);
            }
        });
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (Object object : ((ShapedOreRecipe) recipe).getInput()) {
            if (object != null) {
                final String bufferOreName = object instanceof List ? uniOreDictionary.getName(object) : object instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) object) : null;
                if (bufferOreName != null)
                    inputs.add(bufferOreName);
                else if (object instanceof ItemStack)
                    inputs.add(object);
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }
}