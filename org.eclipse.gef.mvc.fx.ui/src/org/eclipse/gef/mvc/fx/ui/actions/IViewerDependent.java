/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.ui.services.IDisposable;

/**
 * The {@link IViewerDependent} interface specifies an {@link #init(IViewer)}
 * method that can be used to supply an {@link IViewer} to this
 * {@link IViewerDependent} or remove a previously supplied {@link IViewer} from
 * an {@link IViewerDependent} by passing in <code>null</code>. Moreover, the
 * {@link IDisposable} interface is supported and implemented by calling
 * {@link #init(IViewer)} with <code>null</code>.
 *
 * @author mwienand
 *
 */
public interface IViewerDependent extends IDisposable {

	@Override
	default void dispose() {
		init(null);
	}

	/**
	 * Binds this {@link IViewerDependent} to the given {@link IViewer}, or
	 * unbinds this {@link IViewerDependent} if <code>null</code> is given.
	 *
	 * @param viewer
	 *            The {@link IViewer} to bind this {@link IViewerDependent} to.
	 *            May be <code>null</code> to unbind.
	 */
	public void init(IViewer viewer);
}
