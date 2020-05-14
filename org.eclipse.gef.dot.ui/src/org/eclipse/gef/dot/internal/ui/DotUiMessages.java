/*******************************************************************************
 * Copyright (c) 2010, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API & implementation (bug #277380)
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *                                - Refactoring of DOT Graph view live update/live export (bug #337644)
 *                                - Add 'Find References' support (bug #531049)
 *                                - Add 'Open the exported file automatically' option (bug #521329)
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
	public static String GraphvizPreference_7;
	public static String DotGraphView_0;
	public static String DotGraphView_1;
	public static String DotGraphView_2;
	public static String DotGraphView_3;
	public static String DotGraphView_4;
	public static String DotReferenceFinder;
	public static String DotErrorPrefix;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, DotUiMessages.class);
	}

	private DotUiMessages() {
	}
}
