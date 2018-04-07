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
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.part.IShowInTargetList;

/**
 * Usually, the editor class can directly implement the IShowInTargetList
 * interface to provide the list of views that should occur in the 'Show In'
 * context menu of the editor. Since the DOT Editor class is accessed using the
 * ExecutableExtensionFactory, the desired interface is implemented using an
 * adapterfactory.
 * 
 * The implementation of this class is mainly taken from the
 * org.eclipse.xtend.ide.editor.XtendEditorAdapterFactory java class.
 */
public class DotEditorAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (DotEditorUtils.isDotEditor(adaptableObject)
				&& IShowInTargetList.class.equals(adapterType)) {
			return new IShowInTargetList() {
				@Override
				public String[] getShowInTargetIds() {
					return new String[] { DotGraphView.class.getName() };
				}
			};
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IShowInTargetList.class };
	}

}
