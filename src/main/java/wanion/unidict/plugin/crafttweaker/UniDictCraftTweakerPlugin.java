package wanion.unidict.plugin.crafttweaker;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.oredict.MCOreDictEntry;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Reference;
import wanion.unidict.plugin.crafttweaker.RemovalByKind.Crafting;
import wanion.unidict.plugin.crafttweaker.RemovalByKind.Furnace;
import wanion.unidict.plugin.crafttweaker.RemovalByKind.RemovalByKind;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.unidict.api")
public final class UniDictCraftTweakerPlugin
{
	private static final List<ShapedRecipeTemplate> NEW_SHAPED_RECIPE_TEMPLATE_LIST = new ArrayList<>();
	private static final List<ShapelessRecipeTemplate> NEW_SHAPELESS_RECIPE_TEMPLATE_LIST = new ArrayList<>();
	private static final Map<Class<? extends RemovalByKind>, RemovalByKind> REMOVAL_BY_KIND_MAP = new HashMap<>();
	public static final Map<String, RemovalByKind> NAME_REMOVAL_BY_KIND_MAP = new HashMap<>();

	private UniDictCraftTweakerPlugin() {}

	@ZenMethod
	public static void newShapedRecipeTemplate(@Nonnull final String outputKind, final int outputSize, @Nonnull final Object[][] inputs)
	{
		CraftTweakerAPI.apply(new ShapedRecipeTemplate(outputKind, outputSize, inputs));
	}

	@ZenMethod
	public static void newShapelessRecipeTemplate(@Nonnull final String outputKind, final int outputSize, @Nonnull final Object[] inputs)
	{
		CraftTweakerAPI.apply(new ShapelessRecipeTemplate(outputKind, outputSize, inputs));
	}

	public static void preInit()
	{
		registerAbstractRemovalByKind(new Crafting());
		registerAbstractRemovalByKind(new Furnace());
	}

	private static void registerAbstractRemovalByKind(@Nonnull final RemovalByKind removalByKind)
	{
		REMOVAL_BY_KIND_MAP.put(removalByKind.getClass(), removalByKind);
		NAME_REMOVAL_BY_KIND_MAP.put(removalByKind.toString(), removalByKind);
	}

	public static <R extends RemovalByKind> R getRemovalByKind(@Nonnull final Class<R> abstractRemovalByKindClass)
	{
		return abstractRemovalByKindClass.cast(REMOVAL_BY_KIND_MAP.get(abstractRemovalByKindClass));
	}

	public static void init()
	{
		final UniDictAPI uniDictAPI = REMOVAL_BY_KIND_MAP.size() > 0 || NEW_SHAPED_RECIPE_TEMPLATE_LIST.size() > 0 || NEW_SHAPELESS_RECIPE_TEMPLATE_LIST.size() > 0 ? UniDict.getAPI() : null;
		if (uniDictAPI == null)
			return;
		REMOVAL_BY_KIND_MAP.values().forEach(removalByKind -> removalByKind.apply(uniDictAPI));
		final List<IRecipe> recipeList = new ArrayList<>();
		fetchShapedRecipeTemplates(uniDictAPI, recipeList);
		fetchShapelessRecipeTemplates(uniDictAPI, recipeList);
		final IForgeRegistry<IRecipe> recipeRegistry = ForgeRegistries.RECIPES;
		recipeList.forEach(recipe -> recipeRegistry.register(recipe.setRegistryName(new ResourceLocation(recipe.getGroup()))));
	}

	private static void fetchShapedRecipeTemplates(@Nonnull final UniDictAPI uniDictAPI, @Nonnull final List<IRecipe> recipeList)
	{
		NEW_SHAPED_RECIPE_TEMPLATE_LIST.forEach(shapedRecipeTemplate -> {
			boolean badEntry = false;
			if (Resource.getKindFromName(shapedRecipeTemplate.outputKind) == 0)
				badEntry = true;
			for (final Object[] subInputs : shapedRecipeTemplate.inputs)
				for (final Object input : subInputs)
					if (input != null && (input instanceof String && !((String) input).isEmpty() && (shapedRecipeTemplate.outputKind.equals(input) || Resource.getKindFromName((String) input) == 0)))
						badEntry = true;
			if (!badEntry) {
				final TObjectIntMap<String> nameKindMap = new TObjectIntHashMap<>();
				for (final Object input : shapedRecipeTemplate.inputs)
					if (input instanceof String && !nameKindMap.containsKey(input))
						nameKindMap.put((String) input, Resource.getKindFromName((String) input));
				nameKindMap.put(shapedRecipeTemplate.outputKind, Resource.getKindFromName(shapedRecipeTemplate.outputKind));
				final Object[] trueInputs = new Object[9];
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						final Object input = shapedRecipeTemplate.inputs[x][y];
						if (input instanceof String && !input.equals(""))
							trueInputs[y * 3 + x] = input;
						else if (input instanceof MCItemStack && ((MCItemStack) input).getInternal() instanceof ItemStack)
							trueInputs[y * 3 + x] = ((MCItemStack) input).getInternal();
						else if (input instanceof MCOreDictEntry)
							trueInputs[y * 3 + x] = ((MCOreDictEntry) input).getName();
					}
				}
				final RecipeAttributes recipeAttributes = RecipeHelper.rawShapeToShape(trueInputs);
				final int outputKind = Resource.getKindFromName(shapedRecipeTemplate.outputKind);
				final List<Resource> resourceList = uniDictAPI.getResources(nameKindMap.values());
				resourceList.forEach(resource -> {
					final UniResourceContainer uniResourceContainer = resource.getChild(outputKind);
					final ItemStack itemStack = uniResourceContainer.getMainEntry();
					final int stackSize = MathHelper.clamp(shapedRecipeTemplate.outputSize, 1, itemStack.getMaxStackSize());
					itemStack.setCount(stackSize);
					recipeList.add(new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, uniResourceContainer.name + ".x" + stackSize + "_shape." + recipeAttributes.shape + ".template"), itemStack, kindShapeToActualShape(recipeAttributes.actualShape, resource)));
				});
			}
		});
	}

	private static void fetchShapelessRecipeTemplates(@Nonnull final UniDictAPI uniDictAPI, @Nonnull final List<IRecipe> recipeList)
	{
		NEW_SHAPELESS_RECIPE_TEMPLATE_LIST.forEach(shapelessRecipeTemplate -> {
			boolean badEntry = false;
			if (Resource.getKindFromName(shapelessRecipeTemplate.outputKind) == 0)
				badEntry = true;
			for (final Object input : shapelessRecipeTemplate.inputs)
				if (input instanceof String && !((String) input).isEmpty() && (shapelessRecipeTemplate.outputKind.equals(input) || Resource.getKindFromName((String) input) == 0))
					badEntry = true;
			if (!badEntry) {
				final TObjectIntMap<String> nameKindMap = new TObjectIntHashMap<>();
				nameKindMap.put(shapelessRecipeTemplate.outputKind, Resource.getKindFromName(shapelessRecipeTemplate.outputKind));
				for (final Object input : shapelessRecipeTemplate.inputs)
					if (input instanceof String && !nameKindMap.containsKey(input))
						nameKindMap.put((String) input, Resource.getKindFromName((String) input));
				final int outputKind = Resource.getKindFromName(shapelessRecipeTemplate.outputKind);
				final List<Resource> resourceList = uniDictAPI.getResources(nameKindMap.values());
				resourceList.forEach(resource -> {
					final UniResourceContainer uniResourceContainer = resource.getChild(outputKind);
					final ItemStack itemStack = uniResourceContainer.getMainEntry();
					final int stackSize = MathHelper.clamp(shapelessRecipeTemplate.outputSize, 1, itemStack.getMaxStackSize());
					itemStack.setCount(stackSize);
					recipeList.add(new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, uniResourceContainer.name + ".x" + shapelessRecipeTemplate.outputSize + "_size." + shapelessRecipeTemplate.inputs.length + ".template"), itemStack, kindShapeToActualShape(shapelessRecipeTemplate.inputs, resource)));
				});
			}
		});
	}

	private static Object[] kindShapeToActualShape(@Nonnull final Object[] inputs, @Nonnull final Resource resource)
	{
		final Object[] newInputKinds = new Object[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			final Object input = inputs[i];
			final int kind = input instanceof String ? Resource.getKindFromName((String) input) : 0;
			if (kind != 0)
				newInputKinds[i] = resource.getChild(kind).name;
			else if (input instanceof MCItemStack && ((MCItemStack) input).getInternal() instanceof ItemStack)
				newInputKinds[i] = ((MCItemStack) input).getInternal();
			else if (input instanceof MCOreDictEntry)
				newInputKinds[i] = ((MCOreDictEntry) input).getName();
			else newInputKinds[i] = input;
		}
		return newInputKinds;
	}

	private static class ShapedRecipeTemplate implements IAction
	{
		private final String outputKind;
		private final int outputSize;
		private final Object[][] inputs;

		private ShapedRecipeTemplate(@Nonnull final String outputKind, final int outputSize, @Nonnull final Object[][] inputs)
		{
			this.outputKind = outputKind;
			this.outputSize = outputSize;
			this.inputs = inputs;
		}

		@Override
		public void apply()
		{
			NEW_SHAPED_RECIPE_TEMPLATE_LIST.add(this);
		}

		@Override
		public String describe()
		{
			return "Trying to create a Shaped Recipe Template for kind: " + outputKind;
		}
	}

	private static class ShapelessRecipeTemplate implements IAction
	{
		private final String outputKind;
		private final int outputSize;
		private final Object[] inputs;

		private ShapelessRecipeTemplate(@Nonnull final String output, final int outputSize, @Nonnull final Object[] inputs)
		{
			this.outputKind = output;
			this.outputSize = outputSize;
			this.inputs = inputs;
		}

		@Override
		public void apply()
		{
			NEW_SHAPELESS_RECIPE_TEMPLATE_LIST.add(this);
		}

		@Override
		public String describe()
		{
			return "Trying to create a Shapeless Recipe Template for kind: " + outputKind;
		}
	}
}