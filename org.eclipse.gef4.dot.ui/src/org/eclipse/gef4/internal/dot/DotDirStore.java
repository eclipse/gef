/*******************************************************************************
 * Copyright (c) 2014 Fabian Steeg, hbz.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg, hbz - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.internal.dot;

import java.io.File;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Store and access the path to the 'dot' executable in the preference store.
 * The path can be set by the user, using a directory selection dialog. The
 * selected location is stored in the bundle's preferences and available from
 * there after the initial setting.
 */
public class DotDirStore {

	private static final String DOT_SELECT_SHORT = "Where is 'dot'?";
	private static final String DOT_SELECT_LONG = "Please specify the folder that contains 'dot' (http://www.graphviz.org/)";
	private static final String NOT_FOUND_LONG = "The application 'dot' was not found in the specified directory";
	private static final String NOT_FOUND_SHORT = "Not found";

	private static boolean containsDot(final File folder) {
		String[] files = folder.list();
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals("dot") || files[i].equals("dot.exe")) {
				return true;
			}
		}
		return false;
	}

	private static String dotPathFromPreferences() {
		return dotUiPrefs().get("dotpath", "");
	}

	/** @return The path to the folder containing the local 'dot' executable */
	public static String getDotDirPath() {
		if (dotPathFromPreferences().length() == 0) {
			setDotDirPath(); // set the preferences
		}
		return dotPathFromPreferences();
	}

	private static void processUserInput(final IWorkbenchWindow parent,
			final DirectoryDialog dialog) {
		String selectedPath = dialog.open();
		if (selectedPath != null) {
			if (!containsDot(new File(selectedPath))) {
				MessageDialog.openError(parent.getShell(), NOT_FOUND_SHORT,
						NOT_FOUND_LONG);
			} else {
				Preferences preferences = dotUiPrefs();
				preferences.put("dotpath", selectedPath + File.separator);
				try {
					preferences.flush();
				} catch (BackingStoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** Sets the path to the local 'dot' executable based on user selection. */
	public static void setDotDirPath() {
		IWorkbenchWindow parent = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
		dialog.setMessage(DOT_SELECT_LONG);
		dialog.setText(DOT_SELECT_SHORT);
		processUserInput(parent, dialog);
	}

	private static Preferences dotUiPrefs() {
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode("corg.eclipse.gef4");
		Preferences node = preferences.node("dot.ui");
		return node;
	}

	private DotDirStore() {/* Enforce non-instantiability */
	}

}