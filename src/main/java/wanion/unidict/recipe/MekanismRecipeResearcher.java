package wanion.unidict.recipe;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.recipe.ShapelessMekanismRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Reference;
import wanion.unidict.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Heavily based from Forge Researcher
public class MekanismRecipeResearcher extends AbstractRecipeResearcher<ShapedMekanismRecipe, ShapelessMekanismRecipe> {
    private final Field oresField;

    public MekanismRecipeResearcher()
    {
        Field dummyOresField = null;
        try {
            dummyOresField = OreIngredient.class.getDeclaredField("ores");
            dummyOresField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        oresField = dummyOresField;
    }

    @Override
    public int getShapedRecipeKey(@Nonnull final ShapedMekanismRecipe recipe)
    {
        final TIntList recipeKeys = Util.getList(recipe.getIngredients().toArray(), resourceHandler);
        int recipeKey = 0;
        recipeKeys.sort();
        for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
            recipeKey += 31 * recipeKeysIterator.next();
        return recipeKey;
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull final ShapelessMekanismRecipe recipe)
    {
        final TIntList recipeKeys = Util.getList(recipe.getIngredients().toArray(), resourceHandler);
        int recipeKey = 0;
        recipeKeys.sort();
        for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
            recipeKey += 31 * recipeKeysIterator.next();
        return recipeKey;
    }

    @Override
    @Nonnull
    public List<Class<? extends ShapedMekanismRecipe>> getShapedRecipeClasses()
    {
        return Collections.singletonList(ShapedMekanismRecipe.class);
    }

    @Override
    @Nonnull
    public List<Class<? extends ShapelessMekanismRecipe>> getShapelessRecipeClasses()
    {
        return Collections.singletonList(ShapelessMekanismRecipe.class);
    }

    @Override
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull final ShapedMekanismRecipe recipe)
    {
        final List<Ingredient> recipeInputs = recipe.getIngredients();
        final int width = recipe.getRecipeWidth(), height = recipe.getRecipeHeight(), root = Math.max(height, width);
        final Object[] newRecipeInputs = new Object[root * root];
        for (int y = 0, i = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {
                final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
                if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
                    final ItemStack itemStack = ingredient.getMatchingStacks()[0];
                    final UniResourceContainer container = resourceHandler.getContainer(itemStack);
                    newRecipeInputs[y * root + x] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
                } else if (ingredient instanceof OreIngredient) {
                    try {
                        newRecipeInputs[y * root + x] = uniOreDictionary.getName(oresField.get(ingredient));
                    } catch (IllegalAccessException e) { e.printStackTrace(); }
                }
            }
        }
        final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
        if (outputContainer == null)
            return null;
        final int outputSize;
        final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
        final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
        return new ShapedMekanismRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
    }

    @Override
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final ShapelessMekanismRecipe recipe)
    {
        final List<Ingredient> recipeInputs = recipe.getIngredients();
        final Object[] newRecipeInputs = new Object[9];
        for (int i = 0; i < 9; i++) {
            final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
            if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
                final ItemStack itemStack = ingredient.getMatchingStacks()[0];
                final UniResourceContainer container = resourceHandler.getContainer(itemStack);
                newRecipeInputs[i] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
            }
        }
        final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
        if (outputContainer == null)
            return null;
        final int outputSize;
        final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
        final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
        return new ShapedMekanismRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
    }

    @Override
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final ShapelessMekanismRecipe recipe)
    {
        final List<Object> inputs = new ArrayList<>();
        if (itemStacksOnly) {
            recipe.getIngredients().forEach(ingredient -> {
                if (ingredient != null && ingredient.getMatchingStacks().length > 0)
                    inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
            });
        } else {
            recipe.getIngredients().forEach(ingredient -> {
                if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
                    final ItemStack input = ingredient.getMatchingStacks()[0];
                    final UniResourceContainer container = resourceHandler.getContainer(input);
                    inputs.add(container != null ? container.name : input);
                }
            });
        }
        final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
        if (outputContainer == null)
            return null;
        final int outputSize;
        final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
        return new ShapelessMekanismRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + inputs.size()), outputStack, inputs.toArray());
    }

    @Override
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final ShapedMekanismRecipe recipe)
    {
        final List<Object> inputs = new ArrayList<>();
        if (itemStacksOnly) {
            for (final Ingredient ingredient : recipe.getIngredients())
                if (ingredient != null && ingredient.getMatchingStacks().length > 0)
                    inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
        } else {
            for (final Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
                    final ItemStack input = ingredient.getMatchingStacks()[0];
                    final UniResourceContainer container = resourceHandler.getContainer(input);
                    inputs.add(container != null ? container.name : input);
                }
            }
        }
        final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
        if (outputContainer == null)
            return null;
        final int outputSize;
        final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
        return new ShapelessMekanismRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + inputs.size()), outputStack, inputs.toArray());
    }
}
