package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import wanion.lib.common.MetaItem;
import wanion.lib.common.Util;
import wanion.unidict.UniDict.IDependency;

import javax.annotation.Nonnull;
import javax.annotation.RegEx;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class UniOreDictionary implements IDependency
{
	private static final Map<String, Integer> nameToId = Util.getField(OreDictionary.class, "nameToId", null, Map.class);
	private static final List<String> idToName = Util.getField(OreDictionary.class, "idToName", null, List.class);
	private static final List<List<ItemStack>> idToStack = Util.getField(OreDictionary.class, "idToStack", null, List.class);
	private static final List<List<ItemStack>> idToStackUn = Util.getField(OreDictionary.class, "idToStackUn", null, List.class);
	private static final Map<Integer, List<Integer>> stackToId = Util.getField(OreDictionary.class, "stackToId", null, Map.class);
	private final Map<List<ItemStack>, String> entryToName = new IdentityHashMap<>();
	private final TIntObjectMap<String> stackToName = new TIntObjectHashMap<>();

	private UniOreDictionary()
	{
		nameToId.keySet().forEach(name -> {
			final Integer oreDictId = nameToId.get(name);
			final List<ItemStack> entries = getUn(oreDictId);
			if (entries != null) {
				for (final int hash : MetaItem.getArray(entries))
					if (!stackToName.containsKey(hash))
						stackToName.put(hash, name);
				entryToName.put(entries, name);
			}
		});
	}

	public static List<ItemStack> get(final String oreDictName)
	{
		return get(nameToId.get(oreDictName));
	}

	public static List<ItemStack> get(final Integer oreDictId)
	{
		return checkId(oreDictId, idToStack) ? idToStack.get(oreDictId) : null;
	}

	public static List<ItemStack> getUn(final Integer oreDictId)
	{
		return checkId(oreDictId, idToStackUn) ? idToStackUn.get(oreDictId) : null;
	}

	public static List<ItemStack> getUn(@Nonnull final String oreDictName)
	{
		return getUn(nameToId.get(oreDictName));
	}

	public static ItemStack getFirstEntry(final String oreDictName)
	{
		final List<ItemStack> oreList = get(oreDictName);
		return (oreList != null && !oreList.isEmpty()) ? oreList.get(0).copy() : null;
	}

	public static ItemStack getLastEntry(final String oreDictName)
	{
		final List<ItemStack> oreList = get(oreDictName);
		return (oreList != null && !oreList.isEmpty()) ? oreList.get(oreList.size() - 1).copy() : null;
	}

	public static List<ItemStack> get(final ItemStack thing)
	{
		final int thingId = MetaItem.get(thing);
		if (stackToId.containsKey(thingId))
			return idToStack.get(stackToId.get(thingId).get(0));
		return null;
	}

	public static List<Matcher> getThoseThatMatches(@RegEx final String regex)
	{
		return getThoseThatMatches(Pattern.compile(regex));
	}

	public static List<Matcher> getThoseThatMatches(final Pattern pattern)
	{
		final List<Matcher> matcherList = new ArrayList<>();
		nameToId.keySet().forEach(name -> {
			Matcher matcher = pattern.matcher(name);
			if (matcher.find())
				matcherList.add(matcher);
		});
		return matcherList;
	}

	public static void removeFromElsewhere(final String oreDictName)
	{
		final ItemStack mainEntry = getFirstEntry(oreDictName);
		if (mainEntry == null)
			return;
		final int[] ids = OreDictionary.getOreIDs(mainEntry);
		if (ids.length == 0)
			return;
		final int mainEntryHash = MetaItem.get(mainEntry);
		final int oreDictId = getId(oreDictName);
		for (final int id : ids) {
			if (oreDictId == id)
				continue;
			final List<ItemStack> oreDictEntries = get(id);
			if (oreDictEntries == null)
				continue;
			oreDictEntries.removeIf(itemStack -> mainEntryHash == MetaItem.get(itemStack));
		}
	}

	public static Integer getId(final String oreDictName)
	{
		return nameToId.get(oreDictName);
	}

	private static String getName(final Integer oreDictId)
	{
		return idToName.get(oreDictId);
	}

	private static boolean checkId(final Integer oreDictId, List<List<ItemStack>> list)
	{
		return oreDictId != null && oreDictId < list.size();
	}

	public String getName(final Object thing)
	{
		if (thing instanceof ItemStack)
			return stackToName.get(MetaItem.get((ItemStack) thing));
		else if (thing instanceof List)
			return entryToName.get(thing);
		else
			return null;
	}
}
