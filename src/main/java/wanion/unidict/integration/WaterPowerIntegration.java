package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;
import waterpower.common.recipe.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class WaterPowerIntegration extends AbstractIntegrationThread
{
	private final Field hashMapRecipeField;
	private final Field multiRecipeField;
	private final Field myRecipeField;

	WaterPowerIntegration()
	{
		super("Water Power");
		try {
			(hashMapRecipeField = HashMapRecipeManager.class.getDeclaredField("recipes")).setAccessible(true);
			(multiRecipeField = MultiRecipeManager.class.getDeclaredField("container")).setAccessible(true);
			(myRecipeField = MyRecipeManager.class.getDeclaredField("recipes")).setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Couldn't find container field");
		}
	}

	@Override
	public String call()
	{
		try {
			fixRecipes(MyRecipes.centrifuge);
			fixRecipes(MyRecipes.compressor);
			fixRecipes(MyRecipes.cutter);
			fixRecipes(MyRecipes.implosion);
			fixRecipes(MyRecipes.lathe);
			fixRecipes(MyRecipes.macerator);
			fixRecipes(MyRecipes.sawmill);
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "Correct Output for machines =p";
	}

	@SuppressWarnings("unchecked")
	private void fixRecipes(@Nonnull final IRecipeManager iRecipeManager)
	{
		if (iRecipeManager instanceof HashMapRecipeManager) {
			try {
				final HashMap<ItemStack, ItemStack> recipes = (HashMap<ItemStack, ItemStack>) hashMapRecipeField.get(iRecipeManager);
				if (recipes != null)
					recipes.entrySet().forEach(entry -> entry.setValue(resourceHandler.getMainItemStack(entry.getValue())));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (iRecipeManager instanceof MultiRecipeManager) {
			try {
				final List<IRecipeManager> container = (List<IRecipeManager>) multiRecipeField.get(iRecipeManager);
				if (container != null)
					container.forEach(this::fixRecipes);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (iRecipeManager instanceof MyRecipeManager) {
			try {
				final Map<IMyRecipeInput, MyRecipeOutput> recipes = (Map<IMyRecipeInput, MyRecipeOutput>) myRecipeField.get(iRecipeManager);
				if (recipes != null)
					recipes.entrySet().forEach(entry -> entry.setValue(new MyRecipeOutput(entry.getValue().power, resourceHandler.getMainItemStacks(entry.getValue().items))));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}