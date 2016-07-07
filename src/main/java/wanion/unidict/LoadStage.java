package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraftforge.fml.common.event.*;

import javax.annotation.Nonnull;

public enum LoadStage
{
    PRE_INIT(FMLPreInitializationEvent.class),
    INIT(FMLInitializationEvent.class),
    POST_INIT(FMLPostInitializationEvent.class),
    LOAD_COMPLETE(FMLLoadCompleteEvent.class);

    public final Class<? extends FMLStateEvent> stage;

    LoadStage(Class<? extends FMLStateEvent> stage)
    {
        this.stage = stage;
    }

    public static LoadStage getStage(@Nonnull final Class<? extends FMLStateEvent> stage)
    {
        for (final LoadStage loadStage : values())
            if (loadStage.stage == stage)
                return loadStage;
        return null;
    }
}