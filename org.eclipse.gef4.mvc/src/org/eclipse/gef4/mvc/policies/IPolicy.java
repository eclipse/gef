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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPolicy.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.bindings.IAdaptable;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
//TODO: change generic parameter to specify IVisualPart<VR> rather than VR
public interface IPolicy<VR> extends IAdaptable.Bound<IVisualPart<VR>> {

	public IVisualPart<VR> getHost();
}
