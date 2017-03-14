/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.gestures.IGesture;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.IPolicy;

import javafx.scene.Node;

/**
 * An {@link IHandler} handles (part) of a user interaction that manifests
 * itself in potentially overlapping gestures. It is bound to an
 * {@link IVisualPart} and triggered by one or more {@link IGesture gestures}.
 * An {@link IHandler} can use {@link IPolicy policies} to perform changes.
 *
 * @author anyssen
 *
 */
public interface IHandler
		extends IAdaptable.Bound<IVisualPart<? extends Node>> {

	/**
	 * Returns the host of this {@link IHandler}, i.e. the {@link IVisualPart}
	 * this {@link IHandler} is attached to.
	 *
	 * @return The host of this {@link IHandler}.
	 */
	public default IVisualPart<? extends Node> getHost() {
		return getAdaptable();
	}
}
