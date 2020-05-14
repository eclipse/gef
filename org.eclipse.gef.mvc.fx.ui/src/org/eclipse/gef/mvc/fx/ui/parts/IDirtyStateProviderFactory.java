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
package org.eclipse.gef.mvc.fx.ui.parts;

import org.eclipse.ui.IWorkbenchPart;

/**
 * A factory for the creation of an {@link IDirtyStateProvider}.
 *
 * @author anyssen
 *
 */
public interface IDirtyStateProviderFactory {

	/**
	 * Creates a new {@link IDirtyStateProvider} for the given
	 * {@link IWorkbenchPart}.
	 *
	 * @param workbenchPart
	 *            The {@link IWorkbenchPart} the {@link IDirtyStateProvider} is
	 *            related to.
	 * @return A new {@link IDirtyStateProvider} instance.
	 */
	public IDirtyStateProvider create(IWorkbenchPart workbenchPart);
}
