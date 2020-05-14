/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link IAnchorProvider} can provide an {@link IAnchor} for an anchorage
 * part depending on a given anchored {@link IVisualPart} and a corresponding
 * role (see {@link #get(IVisualPart, String)} for details).
 */
public interface IAnchorProvider {

	/**
	 * Returns an {@link IAnchor} that should be used to provide a position for
	 * the given anchored {@link IVisualPart} and the given role.
	 *
	 * @param anchoredPart
	 *            The anchored {@link IVisualPart} which the returned
	 *            {@link IAnchor} should provide a position for.
	 * @param role
	 *            The role which the returned {@link IAnchor} should provide a
	 *            position for.
	 * @return An {@link IAnchor} that should be used to provide a position for
	 *         the given anchored {@link IVisualPart} and the given role.
	 */
	public IAnchor get(IVisualPart<? extends Node> anchoredPart, String role);

}
