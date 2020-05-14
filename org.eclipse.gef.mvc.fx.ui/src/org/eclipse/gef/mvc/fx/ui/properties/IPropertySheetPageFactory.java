/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.properties;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * A factory to create a new {@link IPropertySheetPage}.
 *
 * @author anyssen
 */
public interface IPropertySheetPageFactory {

	/**
	 * Creates a new {@link IPropertySheetPage} for the given
	 * {@link IWorkbenchPart}.
	 *
	 * @param workbenchPart
	 *            The {@link IWorkbenchPart} this {@link IPropertySheetPage} is
	 *            related to.
	 * @return A new {@link IPropertySheetPage} instance.
	 */
	public IPropertySheetPage create(IWorkbenchPart workbenchPart);
}
