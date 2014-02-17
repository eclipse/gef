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

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.domain.IDomainBound;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * An {@link ITool} handles a certain aspect of user interaction. It may react
 * to input mouse, keyboard, and gesture events, as well as to changes to the
 * {@link IVisualViewer}'s logical models like {@link ISelectionModel},
 * {@link IZoomModel}, or {@link IHoverModel}.
 * 
 * As an reaction to input events, an {@link ITool} may manipulate the
 * {@link IVisualViewer}'s logical models, or interact with the
 * {@link IVisualViewer}'s {@link IVisualPart} via a respective
 * {@link IPolicy}.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public interface ITool<V> extends IActivatable, IDomainBound<V> {

	// TODO: tools/handles should change the cursor
}
