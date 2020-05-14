/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import org.eclipse.gef.dot.internal.ui.DotGraphView;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.xtext.ui.editor.XtextEditor;

/**
 * The DotEditor class implements the IShowInTargetList interface to provide the
 * list of views that should occur in the 'Show In' context menu of the DOT
 * Editor.
 */
public class DotEditor extends XtextEditor implements IShowInTargetList {

	@Override
	public String[] getShowInTargetIds() {
		return new String[] { DotGraphView.class.getName() };
	}

}
