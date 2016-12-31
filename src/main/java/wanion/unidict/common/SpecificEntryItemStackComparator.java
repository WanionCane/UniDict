package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.gson.stream.JsonReader;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;

import static wanion.lib.common.Util.getModName;
import static wanion.unidict.common.Reference.MOD_ID;
import static wanion.unidict.common.Reference.SLASH;

public final class SpecificEntryItemStackComparator implements Comparator<ItemStack>
{
	public static THashMap<String, SpecificEntryItemStackComparator> entrySpecificComparators = new THashMap<>();
	private static boolean initialized = false;
	private final TObjectIntMap<String> ownerOfEntry;

	private SpecificEntryItemStackComparator(@Nonnull final TObjectIntMap<String> ownerOfEntry)
	{
		this.ownerOfEntry = ownerOfEntry;
	}

	public static synchronized SpecificEntryItemStackComparator getComparatorFor(@Nonnull final String entryName)
	{
		if (!initialized)
			init();
		return entrySpecificComparators.get(entryName);
	}

	public static synchronized boolean hasComparatorForEntry(@Nonnull final String entryName)
	{
		if (!initialized)
			init();
		return entrySpecificComparators.containsKey(entryName);
	}

	private static void init()
	{
		final File jsonFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "specificEntrySorting.json");
		try {
			if (!jsonFile.exists() && !jsonFile.createNewFile()) {
				UniDict.getLogger().error("UniDict couldn't create the specificEntrySorting.json file.");
				return;
			}
			try (final JsonReader jsonReader = new JsonReader(new FileReader(jsonFile))) {
				jsonReader.beginArray();
				while (jsonReader.hasNext()) {
					jsonReader.beginObject();
					if (!jsonReader.nextName().equals("entryName")) {
						jsonReader.skipValue();
						jsonReader.endObject();
						jsonReader.endArray();
						continue;
					}
					final String entryName = jsonReader.nextString();
					if (!jsonReader.nextName().equals("modIdPriorityList")) {
						jsonReader.skipValue();
						jsonReader.endObject();
						jsonReader.endArray();
						continue;
					}
					jsonReader.beginArray();
					final TObjectIntMap<String> ownerOfEntry = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
					int i = 0;
					while (jsonReader.hasNext())
						ownerOfEntry.put(jsonReader.nextString(), i++);
					entrySpecificComparators.put(entryName, new SpecificEntryItemStackComparator(ownerOfEntry));
					jsonReader.endArray();
					jsonReader.endObject();
				}
			}
		} catch (Throwable e) {
			UniDict.getLogger().error("UniDict couldn't read the specificEntrySorting.json file.");
			e.printStackTrace();
		}
		initialized = true;
	}

	@Override
	public int compare(@Nonnull final ItemStack itemStack1, @Nonnull final ItemStack itemStack2)
	{
		final String stack1ModName = getModName(itemStack1);
		final Config config = UniDict.getConfig();
		if (config.keepOneEntry && config.keepOneEntryModBlackSet.contains(stack1ModName))
			ResourceHandler.addToKeepOneEntryModBlackSet(itemStack1);
		return getIndex(stack1ModName) < getIndex(itemStack2) ? -1 : 0;
	}

	private long getIndex(final ItemStack itemStack)
	{
		return ownerOfEntry.get(getModName(itemStack));
	}

	private long getIndex(final String modName)
	{
		return ownerOfEntry.get(modName);
	}
}