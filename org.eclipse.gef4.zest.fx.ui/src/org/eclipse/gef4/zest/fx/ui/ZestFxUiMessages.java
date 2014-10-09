/*******************************************************************************
 * Copyright (c) 2010, 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.zest.fx.ui;

import org.eclipse.osgi.util.NLS;

public class ZestFxUiMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.gef4.zest.fx.ui.messages"; //$NON-NLS-1$
	public static String DotGraphView_0;
	public static String DotGraphView_1;
	public static String DotGraphView_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ZestFxUiMessages.class);
	}

	private ZestFxUiMessages() {
	}
}
