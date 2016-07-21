package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.recipe.BasicManyToOneRecipe;
import crazypants.enderio.machine.recipe.IManyToOneRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeOutput;
import crazypants.enderio.material.OreDictionaryPreferences;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.common.Util;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "Some inanimate objects appear to have used ender pearls. They all disappeared, how this is possible?";
    }

    private void fixOreDictPreferences()
    {
        final Map preferences = Util.getField(OreDictionaryPreferences.class, "preferences", OreDictionaryPreferences.instance, Map.class);
        if (preferences != null)
            preferences.clear();
    }

    private void fixAlloySmelterRecipes()
    {
        final List<IManyToOneRecipe> alloySmelterRecipes = AlloyRecipeManager.getInstance().getRecipes();
        final List<IManyToOneRecipe> newAlloySmelterRecipes = new FixedSizeList<>(alloySmelterRecipes.size());
        for (final Iterator<IManyToOneRecipe> alloySmelterRecipesIterator = alloySmelterRecipes.iterator(); alloySmelterRecipesIterator.hasNext(); )
        {
            final IManyToOneRecipe alloySmelterRecipe = alloySmelterRecipesIterator.next();
            final ItemStack correctOutput = resourceHandler.getMainItemStack(alloySmelterRecipe.getOutput());
            if (correctOutput == alloySmelterRecipe.getOutput())
                continue;
            final RecipeOutput recipeOutput = alloySmelterRecipe.getOutputs()[0];
            newAlloySmelterRecipes.add(new BasicManyToOneRecipe(new Recipe(new RecipeOutput(correctOutput, recipeOutput.getChance(), recipeOutput.getExperiance()), alloySmelterRecipe.getEnergyRequired(), alloySmelterRecipe.getBonusType(), alloySmelterRecipe.getInputs())));
            alloySmelterRecipesIterator.remove();
        }
        alloySmelterRecipes.addAll(newAlloySmelterRecipes);
    }

    private void fixSagMillRecipes()
    {
        final List<Recipe> sagMillRecipes = CrusherRecipeManager.getInstance().getRecipes();
        final List<Recipe> newSagMillRecipes = new FixedSizeList<>(sagMillRecipes.size());
        newSagMillRecipes.addAll(sagMillRecipes.stream().map(this::sagMillRecipe).collect(Collectors.toList()));
        sagMillRecipes.clear();
        sagMillRecipes.addAll(newSagMillRecipes);
    }

    private Recipe sagMillRecipe(@Nonnull final Recipe sagMillRecipe)
    {
        final int outputSize = sagMillRecipe.getOutputs().length;
        final RecipeOutput[] output = new RecipeOutput[outputSize];
        for (int i = 0; i < outputSize; i++) {
            RecipeOutput oldOutput = sagMillRecipe.getOutputs()[i];
            output[i] = new RecipeOutput(resourceHandler.getMainItemStack(oldOutput.getOutput()), oldOutput.getChance(), oldOutput.getExperiance());
        }
        return new Recipe(sagMillRecipe.getInputs(), output, sagMillRecipe.getEnergyRequired(), sagMillRecipe.getBonusType());
    }
}