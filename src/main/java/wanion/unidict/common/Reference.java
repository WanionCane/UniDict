package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import static java.io.File.separatorChar;

public final class Reference
{
	public static final String MOD_ID = "unidict";
	public static final String MOD_NAME = "UniDict";
	public static final String MOD_VERSION = "1.12.2-2.5e";
	public static final String DEPENDENCIES = "required-after:wanionlib@[1.12.2-1.5,);after:*";
	public static final char SLASH = separatorChar;
	public static final String MC_VERSION = "[1.12,]";
	public static final String CLIENT_PROXY = "wanion.unidict.proxy.ClientProxy";
	public static final String SERVER_PROXY = "wanion.unidict.proxy.CommonProxy";

	private Reference() {}
}