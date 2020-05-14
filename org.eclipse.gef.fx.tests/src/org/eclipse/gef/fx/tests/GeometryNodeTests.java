/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - add boundsForLineGeometry()
 *
 *******************************************************************************/
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.junit.Test;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

public class GeometryNodeTests {

	@Test
	public void boundsForLineGeometry() {
		// test line bounds with default styles
		Line gefLine = new Line(0, 0, 100, 0);
		GeometryNode<Line> geomNode = new GeometryNode<Line>(gefLine);
		assertEquals(gefLine.getBounds(), geomNode.getGeometry().getBounds());
		assertEquals(gefLine.getBounds(), convertLayoutBoundsToGeometryBounds(geomNode, geomNode.getLayoutBounds()));
		assertEquals(gefLine.getBounds(), convertBoundsInLocalToGeometryBounds(geomNode, geomNode.getBoundsInLocal()));
		assertEquals(gefLine.getBounds(),
				convertBoundsInParentToGeometryBounds(geomNode, geomNode.getBoundsInParent()));

		// test bounds w.r.t. stroke width
		geomNode.setStrokeWidth(10);
		assertEquals(gefLine.getBounds(), geomNode.getGeometry().getBounds());
		assertEquals(gefLine.getBounds(), convertLayoutBoundsToGeometryBounds(geomNode, geomNode.getLayoutBounds()));
		assertEquals(gefLine.getBounds(), convertBoundsInLocalToGeometryBounds(geomNode, geomNode.getBoundsInLocal()));
		assertEquals(gefLine.getBounds(),
				convertBoundsInParentToGeometryBounds(geomNode, geomNode.getBoundsInParent()));

		// test that bounds-in-local and bounds-in-parent are too large w.r.t. butt line
		// caps
		geomNode.setStrokeLineCap(StrokeLineCap.BUTT);
		assertEquals(gefLine.getBounds(), geomNode.getGeometry().getBounds());
		assertEquals(gefLine.getBounds(), convertLayoutBoundsToGeometryBounds(geomNode, geomNode.getLayoutBounds()));
		// XXX: GeometryNode expects a uniform stroke being applied to all outlines of
		// the geometry, i.e. a circle with diameter matching stroke-width is expected
		// to be drawn at each point of a curve geometry, or at each point on the
		// outline ‚of a shape geometry, respectively.
		assertFalse(gefLine.getBounds()
				.equals(convertBoundsInLocalToGeometryBounds(geomNode, geomNode.getBoundsInLocal())));
		assertFalse(gefLine.getBounds()
				.equals(convertBoundsInParentToGeometryBounds(geomNode, geomNode.getBoundsInParent())));
	}

	/**
	 * Ensures setting/resizing the geometry will resize the visuals
	 */
	@Test
	public void resizeOnGeometryChange() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));

		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);

		n.resizeGeometry(50, 60);
		assertEquals(n.getGeometry().getBounds().getWidth(), 50, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 60, 0);
		assertEquals(60.0, n.getWidth(), 0);
		assertEquals(70.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}

	/**
	 * Ensures setting/resizing the geometry will resize the visuals
	 */
	@Test
	public void resizeGeometryOnResize() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));

		n.resize(30, 40);
		assertEquals(n.getGeometry().getBounds().getWidth(), 20, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 30, 0);
		assertEquals(30.0, n.getWidth(), 0);
		assertEquals(40.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}

	@Test
	public void resizePolylineNode() {
		GeometryNode<Polyline> n = new GeometryNode<>();
		// Polyline: (19.02538299560547, 30.438175201416016) -> (19.02538299560547,
		// 108.58389282226562)
		n.setGeometry(new Polyline(19.02538299560547, 30.438175201416016, 19.02538299560547, 108.58389282226562));
		n.setStrokeWidth(3.5);
		n.setStrokeType(StrokeType.CENTERED);
	}

	@Test
	public void resizeRelocateOnStrokeWidthAndTypeChange() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));

		assertEquals(29.5, n.getLayoutX(), 0);
		assertEquals(39.5, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);

		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);

		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}

	@Test
	public void relocateOnGeometryChange() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));

		assertEquals(n.getGeometry().getBounds().getX(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 40, 0);
		assertEquals(25.0, n.getLayoutX(), 0);
		assertEquals(35.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);

		n.relocateGeometry(50, 60);
		assertEquals(n.getGeometry().getBounds().getX(), 50, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 60, 0);
		assertEquals(45.0, n.getLayoutX(), 0);
		assertEquals(55.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
	}

	@Test
	public void relocateGeometryOnRelocate() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		n.relocate(30, 40);

		assertEquals(n.getGeometry().getBounds().getX(), 35, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 45, 0);
		assertEquals(30.0, n.getLayoutX(), 0);
		assertEquals(40.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
	}

	private Rectangle convertBoundsInLocalToGeometryBounds(GeometryNode<? extends IGeometry> geom,
			Bounds boundsInLocal) {
		Rectangle boundsLocal = FX2Geometry.toRectangle(boundsInLocal);
		Rectangle shrinkedByMinPenSize = boundsLocal.getShrinked(0.5, 0.5);
		return convertLayoutBoundsToGeometryBounds(geom, Geometry2FX.toFXBounds(shrinkedByMinPenSize));
	}

	private Rectangle convertBoundsInParentToGeometryBounds(GeometryNode<? extends IGeometry> geom,
			Bounds boundsInParent) {
		return convertBoundsInLocalToGeometryBounds(geom,
				Geometry2FX.toFXBounds(FX2Geometry.toRectangle(boundsInParent)
						.getTransformed(FX2Geometry.toAffineTransform(geom.getLocalToParentTransform()).getInverse())
						.getBounds()));
	}

	private Rectangle convertLayoutBoundsToGeometryBounds(GeometryNode<? extends IGeometry> geom, Bounds layoutBounds) {
		Rectangle translatedByLocation = FX2Geometry.toRectangle(layoutBounds).translate(geom.getLayoutX(),
				geom.getLayoutY());
		Rectangle shrinkedByStrokeWidth = translatedByLocation.shrink(geom.getStrokeWidth() / 2,
				geom.getStrokeWidth() / 2);
		return shrinkedByStrokeWidth;
	}
}
