/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class NodeUtilsTests {

	@Test
	public void getGeometricOutline() {
		// Shape
		javafx.scene.shape.Rectangle shape = new javafx.scene.shape.Rectangle(5, 10, 30, 40);
		shape.setStroke(Color.RED);
		shape.setStrokeWidth(3);
		shape.setStrokeType(StrokeType.OUTSIDE);
		shape.relocate(30, 40);

		IGeometry geometricOutline = NodeUtils.getGeometricOutline(shape);
		assertTrue(geometricOutline instanceof Rectangle);
		// the geometry is returned in the local coordinates of the Shape (the
		// stroke is outside), thus the X and Y values are preserved
		assertEquals(new Rectangle(5, 10, 30, 40), geometricOutline);
		// translating it into parent coordinates returns the relocate values
		// including the stroke offset
		assertEquals(new Rectangle(33, 43, 30, 40), NodeUtils.localToParent(shape, geometricOutline).getBounds());

		// GeometryNode
		GeometryNode<Rectangle> geometryNode = new GeometryNode<>();
		geometryNode.setStroke(Color.RED);
		geometryNode.setStrokeWidth(3);
		geometryNode.setStrokeType(StrokeType.OUTSIDE);
		geometryNode.setGeometry(new Rectangle(0, 0, 30, 40));
		geometryNode.relocate(30, 40);
		geometricOutline = NodeUtils.getGeometricOutline(geometryNode);
		assertTrue(geometricOutline instanceof Rectangle);
		// the geometric is returned in the local coordinates of the
		// GeometryNode (as the stroke is outside the geometry but inside the
		// GeometryNode the geometry is translated by the stroke offset)
		assertEquals(new Rectangle(3, 3, 30, 40), geometricOutline);
		// translating it into parent coordinates should provide the same values
		// as in the Shape case, i.e. the relocate values including the stroke
		// offset
		assertEquals(new Rectangle(33, 43, 30, 40), NodeUtils.localToParent(geometryNode, geometricOutline).getBounds());
	}

	@Test
	public void getShapeOutline() {
		// GeometryNode
		GeometryNode<Rectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new Rectangle(30, 40, 30, 40));
		n.relocate(30, 40);

		IGeometry geometricOutline = NodeUtils.getShapeOutline(n);
		assertTrue(geometricOutline instanceof Rectangle);
		assertEquals(new Rectangle(0, 0, 40, 50), geometricOutline);
	}
}
