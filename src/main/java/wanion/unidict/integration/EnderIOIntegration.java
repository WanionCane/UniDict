package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.recipe.BasicManyToOneRecipe;
import crazypants.enderio.machine.recipe.IManyToOneRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeOutput;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.material.OreDictionaryPreferences;
import net.minecraft.item.ItemStack;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class EnderIOIntegration extends AbstractIntegrationThread
{
    EnderIOIntegration()
    {
        super("Ender IO");
    }

    @Override
    public String call()
    {
        try {
            fixOreDictPreferences();
            fixAlloySmelterRecipes();
            fixSagMillRecipes();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "Some inanimate objects appear to have used ender pearls. They all disappeared, how this is possible?";
    }

    private void fixOreDictPreferences()
    {
        Map preferences = Util.getField(OreDictionaryPreferences.class, "preferences", OreDictionaryPreferences.instance, Map.class);
        Map stackCache = Util.getField(OreDictionaryPreferences.class, "stackCache", OreDictionaryPreferences.instance, Map.class);
        if (preferences != null)
            preferences.clear();
        if (stackCache != null)
            stackCache.clear();
    }

    private void fixAlloySmelterRecipes()
    {
        List<IManyToOneRecipe> alloySmelterRecipes = AlloyRecipeManager.getInstance().getRecipes();
        List<IManyToOneRecipe> newAlloySmelterRecipes = new FixedSizeList<>(alloySmelterRecipes.size());
        for (Iterator<IManyToOneRecipe> alloySmelterRecipesIterator = alloySmelterRecipes.iterator(); alloySmelterRecipesIterator.hasNext(); )
        {
            IManyToOneRecipe alloySmelterRecipe = alloySmelterRecipesIterator.next();
            ItemStack correctOutput = resourceHandler.getMainItemStack(alloySmelterRecipe.getOutput());
            if (correctOutput == alloySmelterRecipe.getOutput())
                continue;
            RecipeOutput recipeOutput = alloySmelterRecipe.getOutputs()[0];
            newAlloySmelterRecipes.add(new BasicManyToOneRecipe(new Recipe(new RecipeOutput(correctOutput, recipeOutput.getChance(), recipeOutput.getExperiance()), alloySmelterRecipe.getEnergyRequired(), alloySmelterRecipe.getBonusType(), alloySmelterRecipe.getInputs())));
            alloySmelterRecipesIterator.remove();
        }
        alloySmelterRecipes.addAll(newAlloySmelterRecipes);
    }

    private void fixSagMillRecipes()
    {
        List<Recipe> sagMillRecipes = SagMillRecipeManager.getInstance().getRecipes();
        List<Recipe> newSagMillRecipes = new FixedSizeList<>(sagMillRecipes.size());
        for (Recipe sagMillRecipe : sagMillRecipes)
            newSagMillRecipes.add(sagMillRecipe(sagMillRecipe));
        sagMillRecipes.clear();
        sagMillRecipes.addAll(newSagMillRecipes);
    }

    private Recipe sagMillRecipe(Recipe sagMillRecipe)
    {
        int outputSize = sagMillRecipe.getOutputs().length;
        RecipeOutput[] output = new RecipeOutput[outputSize];
        for (int i = 0; i < outputSize; i++) {
            RecipeOutput oldOutput = sagMillRecipe.getOutputs()[i];
            output[i] = new RecipeOutput(resourceHandler.getMainItemStack(oldOutput.getOutput()), oldOutput.getChance(), oldOutput.getExperiance());
        }
        return new Recipe(sagMillRecipe.getInputs(), output, sagMillRecipe.getEnergyRequired(), sagMillRecipe.getBonusType());
    }
}