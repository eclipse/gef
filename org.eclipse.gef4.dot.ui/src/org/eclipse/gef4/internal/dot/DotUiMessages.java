/*******************************************************************************
 * Copyright (c) 2010, 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.internal.dot;

import org.eclipse.osgi.util.NLS;

public class DotUiMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.gef4.internal.dot.messages"; //$NON-NLS-1$
	public static String DotDirStore_0;
	public static String DotDirStore_1;
	public static String DotDirStore_2;
	public static String DotDirStore_3;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, DotUiMessages.class);
	}

	private DotUiMessages() {
	}
}
