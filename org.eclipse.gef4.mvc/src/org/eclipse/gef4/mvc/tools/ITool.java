/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * An {@link ITool} handles a certain aspect of user interaction. It may react
 * to input mouse, keyboard, and gesture events, as well as to changes to the
 * {@link IViewer}'s logical models like {@link ISelectionModel},
 * {@link IZoomModel}, or {@link IHoverModel}.
 * 
 * As an reaction to input events, an {@link ITool} may manipulate the
 * {@link IViewer}'s logical models, or interact with the {@link IViewer}'s
 * {@link IVisualPart}s via their respective {@link IPolicy}s.
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public interface ITool<VR> extends IActivatable, IAdaptable.Bound<IDomain<VR>> {

	/**
	 * The {@link IDomain}, this {@link ITool} is adapted to.
	 * 
	 * @return The {@link IDomain}, this {@link ITool} is adapted to, or
	 *         <code>null</code> if this {@link ITool} is not adapted to any
	 *         {@link IDomain}.
	 */
	public IDomain<VR> getDomain();
}
