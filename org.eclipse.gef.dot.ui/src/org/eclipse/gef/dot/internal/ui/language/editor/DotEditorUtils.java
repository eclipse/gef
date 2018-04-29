/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class DotEditorUtils {

	/**
	 * @param object
	 * 
	 * @return true if the object is the DOT Editor, false otherwise
	 */
	public static boolean isDotEditor(Object object) {
		if (object instanceof XtextEditor) {
			XtextEditor editor = (XtextEditor) object;
			return DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
					.equals(editor.getLanguageName());
		}

		return false;
	}

}
