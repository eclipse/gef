/*******************************************************************************
 * Copyright (c) 2010, 2016 Fabian Steeg, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API & implementation (bug #277380)
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *     							  - Refactoring of DOT Graph view live update/live export (bug #337644)
 *
 *******************************************************************************/

package org.eclipse.gef.dot.internal.ui;

import org.eclipse.osgi.util.NLS;

public class DotUiMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.gef.dot.internal.ui.messages"; //$NON-NLS-1$
	public static String GraphvizPreference_0;
	public static String GraphvizPreference_1;
	public static String GraphvizPreference_2;
	public static String GraphvizPreference_3;
	public static String GraphvizPreference_4;
	public static String GraphvizPreference_5;
	public static String GraphvizPreference_6;
	public static String DotGraphView_0;
	public static String DotGraphView_1;
	public static String DotGraphView_2;
	public static String DotGraphView_3;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, DotUiMessages.class);
	}

	private DotUiMessages() {
	}
}
