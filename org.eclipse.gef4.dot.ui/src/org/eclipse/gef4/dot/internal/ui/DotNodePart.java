/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.scene.Node;
import javafx.scene.shape.StrokeType;

/**
 * A specialization of {@link NodePart} that supports different shapes.
 * 
 * @author anyssen
 */
// TODO: we can remove this specialization, if zest offers respective properties
// to control the shape and padding
public class DotNodePart extends NodePart {

	@Override
	protected Node createShape() {
		// TODO: support different dot provided shapes
		// ellipse is the default shape used by DOT
		GeometryNode<? extends IGeometry> shape = new GeometryNode<>(
				new Ellipse(new Rectangle(0, 0, 0, 0)));
		shape.setStrokeType(StrokeType.INSIDE);
		return shape;
	}

	// TODO: geometry of shape has to be refreshed from within refreshVisuals,
	// as it might change, we need to introduce a Zest property which allows us
	// to control the shape (or its geometry).

	@Override
	protected double getPadding() {
		return 0.0;
	}
}
