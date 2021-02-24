package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.chocohead.advsolar.IMolecularTransformerRecipeManager;
import ic2.api.recipe.MachineRecipe;

import java.lang.reflect.Field;

final class AdvancedSolarPanelsIntegration extends AbstractIntegrationThread {
    private Field outputField;

    public AdvancedSolarPanelsIntegration() {
        super("Advanced Solar Panels");
        try {
            outputField = MachineRecipe.class.getDeclaredField("output");
            outputField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            logger.error("Could not find Advanced Solar Panels fields!");
            e.printStackTrace();
        }
    }

    @Override
    public String call() {
        try {
            if (outputField != null) {
                IMolecularTransformerRecipeManager.RECIPES.getRecipes().forEach(recipe -> {
                    try {
                        outputField.set(recipe, resourceHandler.getMainItemStack(recipe.getOutput()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            logger.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "All that free energy now has a use!";
    }
}
