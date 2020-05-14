/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Certain parts of this interface have been transferred from org.eclipse.gef.Tool.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.List;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

/**
 * An {@link IGesture} handles a certain aspect of user interaction. It may
 * react to input mouse, keyboard, and gesture events, as well as to changes to
 * logical models (adapted to the {@link IViewer}), which keep track of
 * selection, hover, etc.
 *
 * As an reaction to input events, an {@link IGesture} may manipulate the
 * {@link IViewer}'s logical models, or interact with the {@link IViewer}'s
 * {@link IVisualPart}s via their respective {@link IPolicy}s.
 *
 * @author anyssen
 *
 */
public interface IGesture extends IActivatable, IAdaptable.Bound<IDomain> {

	/**
	 * Returns an (unmodifiable) list containing the {@link IHandler interaction
	 * handlers} that are currently active within this tool for the given
	 * {@link IViewer}, i.e. the target policies of this tool that get notified
	 * about events within the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to return the active policies.
	 * @return An (unmodifiable) list containing the {@link IHandler interaction
	 *         handlers} that are currently active within this gesture.
	 */
	public List<? extends IHandler> getActiveHandlers(IViewer viewer);

	/**
	 * The {@link IDomain}, this {@link IGesture} is adapted to.
	 *
	 * @return The {@link IDomain}, this {@link IGesture} is adapted to, or
	 *         <code>null</code> if this {@link IGesture} is not adapted to any
	 *         {@link IDomain}.
	 */
	public IDomain getDomain();
}
