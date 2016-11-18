package wanion.unidict.processing;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import wanion.lib.module.AbstractModule;
import wanion.unidict.common.Reference;

import javax.annotation.Nonnull;
import java.io.File;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class ProcessingModule extends AbstractModule
{
	public ProcessingModule()
	{
		super("Processing", Class::newInstance);
	}

	@Override
	protected void init()
	{
		final Configuration config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_ID + SLASH + "ProcessingModule.cfg"));
		for (final Processing processing : Processing.values())
			if (config.get("Processings", WordUtils.capitalizeFully(processing.name().replace("_", " ")).replace(" ", ""), processing.enabledByDefault).getBoolean() && isModLoaded(processing.modId))
				manager.add(processing.processingClass);
		if (config.hasChanged())
			config.save();
	}

	private enum Processing
	{
		APPLIED_ENERGISTICS_2("appliedenergistics2", AE2Processing.class),
		MEKANISM("mekanism", null);

		private final String modId;
		private final Class<? extends AbstractProcessingThread> processingClass;
		private final boolean enabledByDefault;

		Processing(@Nonnull final String modId, @Nonnull final Class<? extends AbstractProcessingThread> processingClass)
		{
			this.modId = modId;
			this.processingClass = processingClass;
			this.enabledByDefault = true;
		}

		Processing(@Nonnull final String modId, @Nonnull final Class<? extends AbstractProcessingThread> processingClass, final boolean enabledByDefault)
		{
			this.modId = modId;
			this.processingClass = processingClass;
			this.enabledByDefault = enabledByDefault;
		}
	}
}