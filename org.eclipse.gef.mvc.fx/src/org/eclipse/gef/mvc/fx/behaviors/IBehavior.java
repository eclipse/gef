/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link IBehavior} interface extends
 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound} and
 * {@link IActivatable}. Usually, implementations observe some model and perform
 * actions in reaction to model changes, e.g. displaying feedback and handles
 * when its {@link IAdaptable} is selected.
 *
 * @author anyssen
 *
 */
// TODO: change generic parameter to specify IVisualPart<VR> rather than VR
public interface IBehavior
		extends IActivatable, IAdaptable.Bound<IVisualPart<? extends Node>> {

	/**
	 * Returns the host {@link IVisualPart} of this {@link IBehavior}, i.e. the
	 * part where this behavior is registered as an adapter.
	 *
	 * @return The host {@link IVisualPart} of this {@link IBehavior}.
	 */
	public IVisualPart<? extends Node> getHost();

}
