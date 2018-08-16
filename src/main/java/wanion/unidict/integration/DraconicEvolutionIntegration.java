package wanion.unidict.integration;

import com.brandon3055.draconicevolution.lib.OreDoublingRegistry;
import gnu.trove.set.TIntSet;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.unidict.common.Util;

import java.util.Map;

public class DraconicEvolutionIntegration extends AbstractIntegrationThread
{
    private final TIntSet outputsToIgnore;

    DraconicEvolutionIntegration()
    {
        super("Draconic Evolution");
        outputsToIgnore = MetaItem.getSet(Util.stringListToItemStackList(config.furnaceOutputsToIgnore));
    }

    @Override
    public String call() throws Exception
    {
        try
        {
            fixOreDoublingRecipes();
        }
        catch (Exception e)
        {
            logger.error(threadName + e);
        }
        return threadName + "Draconic Chest has doubled it's power.";
    }

    private void fixOreDoublingRecipes()
    {
        final Map<String, ItemStack> oreRecipes = OreDoublingRegistry.oreRecipes;
        for (final Map.Entry<String, ItemStack> oreRecipe : oreRecipes.entrySet())
        {
            if (outputsToIgnore.contains(MetaItem.get(oreRecipe.getValue())))
                continue;
            final ItemStack oldEntry = oreRecipe.getValue();
            final ItemStack newEntry = resourceHandler.getMainItemStack(oldEntry);
            oreRecipe.setValue(newEntry);
        }
    }
}
