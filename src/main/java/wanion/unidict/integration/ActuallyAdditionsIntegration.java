package wanion.unidict.integration;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;

import java.lang.reflect.Field;

final class ActuallyAdditionsIntegration extends AbstractIntegrationThread {
    private Field crusherOutputOne;
    private Field crusherOutputTwo;
    private Field empowererOutput;
    private Field reconstructorOutput;

    public ActuallyAdditionsIntegration() {
        super("Actually Additions");
        try {
            (crusherOutputOne = CrusherRecipe.class.getDeclaredField("outputOne")).setAccessible(true);
            (crusherOutputTwo = CrusherRecipe.class.getDeclaredField("outputTwo")).setAccessible(true);
            (empowererOutput = EmpowererRecipe.class.getDeclaredField("output")).setAccessible(true);
            (reconstructorOutput = LensConversionRecipe.class.getDeclaredField("output")).setAccessible(true);
        } catch (NoSuchFieldException | NullPointerException e) {
            logger.error("Could not find actually additions fields!");
            e.printStackTrace();
        }
    }

    @Override
    public String call() {
        try {
            fixCrusherRecipes();
            fixEmpowererRecipes();
            fixAtomicReconstructorRecipes();
        } catch (Exception e) { logger.error(threadName + e); }
        return threadName + "Actually unified the Additions!";
    }

    private void fixCrusherRecipes() {
        ActuallyAdditionsAPI.CRUSHER_RECIPES.forEach(recipe -> {
            if (crusherOutputOne != null && recipe.getOutputOne() != null) {
                try {
                    crusherOutputOne.set(recipe, resourceHandler.getMainItemStack(recipe.getOutputOne()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (crusherOutputTwo != null && recipe.getOutputTwo() != null){
                try {
                    crusherOutputTwo.set(recipe, resourceHandler.getMainItemStack(recipe.getOutputTwo()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fixEmpowererRecipes() {
        if (empowererOutput == null)
            return;
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.forEach(recipe -> {
            if (recipe.getOutput() != null){
                try {
                    empowererOutput.set(recipe, resourceHandler.getMainItemStack(recipe.getOutput()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fixAtomicReconstructorRecipes() {
        if (reconstructorOutput == null)
            return;
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.forEach(recipe -> {
            if (recipe.getOutput() != null){
                try {
                    reconstructorOutput.set(recipe, resourceHandler.getMainItemStack(recipe.getOutput()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
