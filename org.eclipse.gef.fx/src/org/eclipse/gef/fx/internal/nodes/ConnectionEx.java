/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.internal.nodes;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.ICurve;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;

/**
 * This is a temporary internal-API extension of Connection, implementing the
 * IBinaryConnection interface. It enables us to evaluate the new provisional
 * API without having to break the existing API.
 *
 * @author nyssen
 *
 */
// TODO: we should restrict decorations to Shape and remove the type parameter
// from IBinaryConnection.
public class ConnectionEx extends Connection
		implements IBendableCurve<GeometryNode<? extends ICurve>, Node> {

	@Override
	public DoubleProperty clickableAreaWidthProperty() {
		return getCurve().clickableAreaWidthProperty();
	}

	@Override
	public double getClickableAreaWidth() {
		return getCurve().getClickableAreaWidth();
	}

	@SuppressWarnings("unchecked")
	@Override
	public GeometryNode<? extends ICurve> getCurve() {
		return (GeometryNode<? extends ICurve>) super.getCurve();
	}

	@Override
	public void setClickableAreaWidth(double clickableAreaWidth) {
		getCurve().setClickableAreaWidth(clickableAreaWidth);
	}

}
