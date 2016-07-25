/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.nodes;

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.IComputationStrategy.Parameter;
import org.eclipse.gef.geometry.planar.Point;

/**
 * Abstract base class for {@link IConnectionRouter}s.
 *
 * @author anyssen
 * @author mwienand
 */
public abstract class AbstractRouter implements IConnectionRouter {

	/**
	 * Computes the reference point for the dynamic anchor at the given index.
	 *
	 * @param connection
	 *            The {@link Connection} that is currently routed.
	 * @param index
	 *            The index specifying the dynamic anchor for which to provide a
	 *            reference point.
	 * @return The reference point for the anchor at the given index.
	 */
	protected abstract Point getAnchoredReferencePoint(Connection connection,
			int index);

	/**
	 * Returns the specified parameter for the given index.
	 *
	 * @param <T>
	 *            The value type.
	 * @param connection
	 *            The Connection.
	 * @param index
	 *            The index.
	 * @param parameterType
	 *            The type of the parameter.
	 * @return The parameter.
	 */
	protected <T extends Parameter<?>> T getComputationParameter(
			Connection connection, int index, Class<T> parameterType) {
		return ((DynamicAnchor) connection.getAnchor(index))
				.getComputationParameter(connection.getAnchorKey(index),
						parameterType);
	}

	/**
	 * Update's the reference point of the anchor with the given index.
	 *
	 * @param connection
	 *            The connection whose anchor to update.
	 * @param index
	 *            The index of the connection anchor, whose reference point is
	 *            to be updated.
	 */
	protected void updateComputationParameters(Connection connection,
			int index) {
		// only update if necessary (when it changes)
		AnchoredReferencePoint referencePointParameter = getComputationParameter(
				connection, index, AnchoredReferencePoint.class);
		Point oldRef = referencePointParameter.get();

		// if we have a position hint for the anchor, we need to use this as the
		// reference point
		Point newRef = getAnchoredReferencePoint(connection, index);
		if (oldRef == null || !newRef.equals(oldRef)) {
			referencePointParameter.set(newRef);
		}
	}

}
