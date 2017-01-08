package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;
import wanion.unidict.resource.UniResourceContainer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class FurnaceIntegration extends AbstractIntegrationThread
{
	FurnaceIntegration()
	{
		super("Furnace");
	}

	private static final Field experienceListField;

	static {
		Field obfuscatedField = null;
		try {
			obfuscatedField = FurnaceRecipes.class.getDeclaredField("field_77605_c");
			obfuscatedField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			UniDict.getLogger().debug("Dev-Environment, right?");
		}
		Field deobfuscatedField = null;
		if (obfuscatedField == null) {
			try {
				deobfuscatedField = FurnaceRecipes.class.getDeclaredField("experienceList");
				deobfuscatedField.setAccessible(true);
			} catch (NoSuchFieldException e) {
				UniDict.getLogger().error("UniDict couldn't find the experienceList Field, even you being in an Dev-Environment, please report this.");
			}
		}
		experienceListField = obfuscatedField != null ? obfuscatedField : deobfuscatedField;
	}

	@Override
	public String call()
	{
		try {
			optimizeFurnaceRecipes();
		} catch (Exception e) {
			UniDict.getLogger().error(threadName + e);
		}
		return threadName + "Some things that you smelted appear to be different now.";
	}

	@SuppressWarnings("unchecked")
	private void optimizeFurnaceRecipes()
	{
		final Map<ItemStack, Float> experienceMap;
		try {
			experienceMap = (Map<ItemStack, Float>) experienceListField.get(FurnaceRecipes.instance());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Furnace Integration Couldn't find the required field.");
		}
		if (!config.inputReplacementFurnace)
			for (final Map.Entry<ItemStack, ItemStack> furnaceRecipe : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
				final ItemStack oldEntry = furnaceRecipe.getValue();
				final ItemStack newEntry = resourceHandler.getMainItemStack(oldEntry);
				furnaceRecipe.setValue(newEntry);
				if (experienceMap.containsKey(oldEntry))
					experienceMap.put(newEntry, experienceMap.remove(oldEntry));
			}
		else {
			final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
			final Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.instance().getSmeltingList();
			final Map<ItemStack, ItemStack> newRecipes = new HashMap<>();
			for (final Iterator<Map.Entry<ItemStack, ItemStack>> furnaceRecipeIterator = furnaceRecipes.entrySet().iterator(); furnaceRecipeIterator.hasNext(); ) {
				final Map.Entry<ItemStack, ItemStack> furnaceRecipe = furnaceRecipeIterator.next();
				final UniResourceContainer inputContainer = resourceHandler.getContainer(furnaceRecipe.getKey());
				final UniResourceContainer outputContainer = resourceHandler.getContainer(furnaceRecipe.getValue());
				if (outputContainer == null)
					continue;
				else if (inputContainer == null) {
					furnaceRecipe.setValue(outputContainer.getMainEntry(furnaceRecipe.getValue().stackSize));
					continue;
				}
				final int kind = inputContainer.kind;
				final ItemStack oldEntry = furnaceRecipe.getValue();
				if (!containerKindMap.containsKey(outputContainer))
					containerKindMap.put(outputContainer, new TIntHashSet());
				final TIntSet kindSet = containerKindMap.get(outputContainer);
				if (!kindSet.contains(kind)) {
					kindSet.add(kind);
					final ItemStack newEntry = outputContainer.getMainEntry(oldEntry.stackSize);
					newRecipes.put(inputContainer.getMainEntry(furnaceRecipe.getKey().stackSize), newEntry);
					if (experienceMap.containsKey(oldEntry))
						experienceMap.put(newEntry, experienceMap.remove(oldEntry));
				}
				furnaceRecipeIterator.remove();
				experienceMap.remove(oldEntry);
			}
			furnaceRecipes.putAll(newRecipes);
		}
	}
}