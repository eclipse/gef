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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link IPolicy} interface extends
 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound}, i.e. it is bound to an
 * {@link IAdaptable}, its so called {@link #getHost()}.
 *
 * @author anyssen
 *
 */
// TODO: change generic parameter to specify IVisualPart<VR> rather than VR
public interface IPolicy extends IAdaptable.Bound<IVisualPart<? extends Node>> {

	/**
	 * Returns the host of this {@link IPolicy}, i.e. the {@link IVisualPart}
	 * this {@link IPolicy} is attached to.
	 *
	 * @return The host of this {@link IPolicy}.
	 */
	public IVisualPart<? extends Node> getHost();

}
