package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import com.google.common.collect.ArrayListMultimap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import wanion.unidict.MetaItem;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.helper.LogHelper;

import java.util.*;

final class IEIntegration extends AbstractIntegrationThread
{
    IEIntegration()
    {
        super("Immersive Engineering");
    }

    @Override
    public String call()
    {
        try {
            fixArcFurnaceRecipes();
            fixBlastFurnaceRecipes();
            fixCrusherRecipes();
            fixMetalPressRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The world's engineer appears to be more immersive.";
    }

    private void fixArcFurnaceRecipes()
    {
        List<ArcFurnaceRecipe> arcFurnaceRecipes = ArcFurnaceRecipe.recipeList;
        List<ArcFurnaceRecipe> correctRecipes = new ArrayList<>(new Double(arcFurnaceRecipes.size() * 1.3).intValue());
        for (Iterator arcFurnaceRecipeIterator = arcFurnaceRecipes.iterator(); arcFurnaceRecipeIterator.hasNext(); ) {
            ArcFurnaceRecipe recipe = (ArcFurnaceRecipe) arcFurnaceRecipeIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(recipe.output);
            if (correctOutput == recipe.output)
                continue;
            correctRecipes.add(new ArcFurnaceRecipe(correctOutput, recipe.oreInputString, recipe.slag, recipe.time, recipe.energyPerTick, recipe.additives));
            arcFurnaceRecipeIterator.remove();
        }
        arcFurnaceRecipes.addAll(correctRecipes);
    }

    private void fixBlastFurnaceRecipes()
    {
        List<BlastFurnaceRecipe> correctRecipes = new ArrayList<>(new Double(BlastFurnaceRecipe.recipeList.size() * 1.3).intValue());
        for (BlastFurnaceRecipe blastFurnaceRecipe : BlastFurnaceRecipe.recipeList)
            correctRecipes.add(new BlastFurnaceRecipe(resourceHandler.getMainItemStack(blastFurnaceRecipe.output), blastFurnaceRecipe.input, blastFurnaceRecipe.time, blastFurnaceRecipe.slag));
        BlastFurnaceRecipe.recipeList.clear();
        BlastFurnaceRecipe.recipeList.addAll(correctRecipes);
    }

    private void fixCrusherRecipes()
    {
        List<CrusherRecipe> crusherRecipes = CrusherRecipe.recipeList;
        List<CrusherRecipe> correctRecipes = new FixedSizeList<>(crusherRecipes.size());
        Set<Integer> uniques = new HashSet<>(correctRecipes.size(), 1);
        for (Iterator<CrusherRecipe> crusherRecipesIterator = crusherRecipes.iterator(); crusherRecipesIterator.hasNext(); )
        {
            CrusherRecipe crusherRecipe = crusherRecipesIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(crusherRecipe.output);
            if (correctOutput == crusherRecipe.output)
                continue;
            ItemStack input = UniOreDictionary.getFirstEntry(crusherRecipe.oreInputString);
            Integer recipeId = MetaItem.getCumulativeKey(input, correctOutput);
            if (!uniques.contains(recipeId)) {
                if (crusherRecipe.secondaryOutput == null)
                    correctRecipes.add(new CrusherRecipe(correctOutput, crusherRecipe.input, crusherRecipe.energy));
                else
                    correctRecipes.add(new UniCrusherRecipe(correctOutput, crusherRecipe.input, crusherRecipe.energy, resourceHandler.getMainItemStacks(crusherRecipe.secondaryOutput), crusherRecipe.secondaryChance));
                uniques.add(recipeId);
            }
            crusherRecipesIterator.remove();
        }
        crusherRecipes.addAll(correctRecipes);
    }

    private void fixMetalPressRecipes()
    {
        ArrayListMultimap<ComparableItemStack, MetalPressRecipe> metalPressRecipes = MetalPressRecipe.recipeList;
        ArrayListMultimap<ComparableItemStack, MetalPressRecipe> correctRecipes = ArrayListMultimap.create();
        TIntSet uniques = new TIntHashSet(metalPressRecipes.size(), 1);
        for (Iterator<MetalPressRecipe> metalPressRecipesIterator = metalPressRecipes.values().iterator(); metalPressRecipesIterator.hasNext(); )
        {
            MetalPressRecipe metalPressRecipe = metalPressRecipesIterator.next();
            ItemStack output = resourceHandler.getMainItemStack(metalPressRecipe.output);
            if (output == metalPressRecipe.output)
                continue;
            int id = MetaItem.getCumulativeKey(output, metalPressRecipe.mold.stack);
            if (!uniques.contains(id)) {
                correctRecipes.put(metalPressRecipe.mold, new MetalPressRecipe(output, metalPressRecipe.input, metalPressRecipe.mold.stack, metalPressRecipe.energy));
                uniques.add(id);
            }
            metalPressRecipesIterator.remove();
        }
        metalPressRecipes.putAll(correctRecipes);
    }

    private static final class UniCrusherRecipe extends CrusherRecipe
    {
        private UniCrusherRecipe(ItemStack output, Object input, int energy, ItemStack[] secondaryOutputs, float[] secondaryChances)
        {
            super(output, input, energy);
            super.secondaryOutput = secondaryOutputs;
            super.secondaryChance = secondaryChances;
        }
    }
}