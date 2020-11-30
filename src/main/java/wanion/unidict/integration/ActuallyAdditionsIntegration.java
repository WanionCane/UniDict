package wanion.unidict.integration;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;

import java.lang.reflect.Field;

public class ActuallyAdditionsIntegration extends AbstractIntegrationThread {
    private Field crusherOutputOne;
    private Field crusherOutputTwo;
    private Field empowererOutput;

    public ActuallyAdditionsIntegration() {
        super("Actually Additions");
        try {
            crusherOutputOne = CrusherRecipe.class.getDeclaredField("outputOne");
            crusherOutputOne.setAccessible(true);
            crusherOutputTwo = CrusherRecipe.class.getDeclaredField("outputTwo");
            crusherOutputTwo.setAccessible(true);
            empowererOutput = EmpowererRecipe.class.getDeclaredField("output");
            empowererOutput.setAccessible(true);
        } catch (NoSuchFieldException e) {
            logger.error("Could not find actually additions fields!");
            e.printStackTrace();
        }
    }

    @Override
    public String call() {
        try {
            fixCrusherRecipes();
            fixEmpowererRecipes();
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

    private void fixEmpowererRecipes(){
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.forEach(recipe -> {
            if (empowererOutput != null && recipe.getOutput() != null){
                try {
                    empowererOutput.set(recipe, resourceHandler.getMainItemStack(recipe.getOutput()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
