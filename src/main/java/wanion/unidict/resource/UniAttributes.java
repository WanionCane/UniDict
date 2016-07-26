package wanion.unidict.resource;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import javax.annotation.Nonnull;

class UniAttributes
{
    final Resource resource;
    final UniResourceContainer uniResourceContainer;

    UniAttributes(@Nonnull final Resource resource, @Nonnull final UniResourceContainer uniResourceContainer)
    {
        this.resource = resource;
        this.uniResourceContainer = uniResourceContainer;
    }
}