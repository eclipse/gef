/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Certain parts of this interface have been transferred from org.eclipse.gef.Tool.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tools;

import java.util.List;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.IPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

/**
 * An {@link ITool} handles a certain aspect of user interaction. It may react
 * to input mouse, keyboard, and gesture events, as well as to changes to
 * logical models (adapted to the {@link IViewer}), which keep track of
 * selection, hover, etc.
 *
 * As an reaction to input events, an {@link ITool} may manipulate the
 * {@link IViewer}'s logical models, or interact with the {@link IViewer}'s
 * {@link IVisualPart}s via their respective {@link IPolicy}s.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public interface ITool<VR> extends IActivatable, IAdaptable.Bound<IDomain<VR>> {

	/**
	 * Returns an (unmodifiable) list containing the {@link IPolicy interaction
	 * policies} that are currently active within this tool for the given
	 * {@link IViewer}, i.e. the target policies of this tool that get notified
	 * about events within the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to return the active policies.
	 * @return An (unmodifiable) list containing the {@link IPolicy interaction
	 *         policies} that are currently active within this tool.
	 */
	public List<? extends IPolicy<VR>> getActivePolicies(IViewer<VR> viewer);

	/**
	 * The {@link IDomain}, this {@link ITool} is adapted to.
	 *
	 * @return The {@link IDomain}, this {@link ITool} is adapted to, or
	 *         <code>null</code> if this {@link ITool} is not adapted to any
	 *         {@link IDomain}.
	 */
	public IDomain<VR> getDomain();

}
