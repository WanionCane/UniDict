package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import hellfirepvp.astralsorcery.common.crafting.grindstone.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AstralSorceryIntegration extends AbstractIntegrationThread {
    private Field recipeChance;

    public AstralSorceryIntegration() {
        super("Astral Sorcery Integration");

        try {
            recipeChance = GrindstoneRecipe.class.getDeclaredField("chance");
            recipeChance.setAccessible(true);
        } catch (NoSuchFieldException e) {
            logger.error("Failed to find astral sorcery chance field!");
            e.printStackTrace();
        }
    }

    @Override
    public String call()  {
        try {
            if (recipeChance != null)
                fixGrindstoneRecipes();
        }
        catch (Exception e) {
            logger.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "Attuned the grindstone!";
    }

    private void fixGrindstoneRecipes() {
        final List<GrindstoneRecipe> recipes = GrindstoneRecipeRegistry.recipes;
        final List<GrindstoneRecipe> newRecipes = new ArrayList<>();
        for (final Iterator<GrindstoneRecipe> grindstoneRecipeIterator = recipes.iterator(); grindstoneRecipeIterator.hasNext(); ){
            GrindstoneRecipe recipe = grindstoneRecipeIterator.next();

            if (recipe instanceof CrystalSharpeningRecipe || recipe instanceof CrystalToolSharpeningRecipe || recipe instanceof SwordSharpeningRecipe)
                continue;

            try {
                newRecipes.add(new GrindstoneRecipe(recipe.getInputForRender(),
                        resourceHandler.getMainItemStack(recipe.getOutputForMatching()),
                        (int)recipeChance.get(recipe), recipe.getChanceToDoubleOutput()));
            } catch (IllegalAccessException e) {
                logger.warn(threadName + "Failed to recreate grindstone recipe!");
                e.printStackTrace();
            }

            grindstoneRecipeIterator.remove();
        }

        recipes.addAll(newRecipes);
    }
}
