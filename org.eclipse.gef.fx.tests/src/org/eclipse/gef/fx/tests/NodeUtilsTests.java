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
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

// TODO: Turn into a parameterized test that test all kind of shapes
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
		assertEquals(new Rectangle(33, 43, 30, 40),
				NodeUtils.localToParent(geometryNode, geometricOutline).getBounds());
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

	/**
	 * Ensure NodeUtils transform operations preserve shape geometries as far as
	 * possible. This is useful to benefit from optimizations that are applied in
	 * the respective shape geometries and will generally speed up succeeding
	 * calculations with the transformed geometry, cf.
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=566564
	 */
	@Test
	public void preverseGeometryUponSimpleTransformation() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
		Group shapeGroup = new Group();
		Scene scene = new Scene(shapeGroup, 500, 400);
		canvas.setScene(scene);

		Shape[] shapes = new Shape[] { new javafx.scene.shape.Rectangle(10, 10, 70, 40),
				new Arc(50, 50, 20, 20, 15, 135), new Circle(50, 50, 20), new Ellipse(50, 50, 30, 15),
				new Polygon(10, 10, 80, 50, 10, 80), new Line(10, 10, 80, 80), new Polyline(10, 10, 80, 50, 10, 80),
				new QuadCurve(10, 80, 40, 40, 80, 80), new CubicCurve(10, 80, 10, 10, 80, 10, 80, 80) };

		Transform[] transforms = new Transform[] { new Translate(100, 200), new Scale(4, 2),
				new Affine(4, 0, 100, 0, 2, 200) };

		for (int i = 0; i < shapes.length; i++) {
			for (int j = 0; j < transforms.length; j++) {
				shapeGroup.getChildren().setAll(shapes[i]);
				shapes[i].getTransforms().setAll(transforms[j]);
				IGeometry shapeGeometry = NodeUtils.getShapeOutline(shapes[i]);

				// apply all transform operations
				IGeometry shapeGeometryFromLocalToParent = NodeUtils.localToParent(shapes[i], shapeGeometry);
				IGeometry shapeGeometryFromParentToLocal = NodeUtils.parentToLocal(shapes[i],
						shapeGeometryFromLocalToParent);
				IGeometry shapeGeometryFromLocalToScene = NodeUtils.localToScene(shapes[i], shapeGeometry);
				IGeometry shapeGeometryFromSceneToLocal = NodeUtils.sceneToLocal(shapes[i],
						shapeGeometryFromLocalToScene);

				// ensure geometries are preserved and correct
				assertEquals(shapeGeometry.getClass(), shapeGeometryFromParentToLocal.getClass());
				assertEquals(shapeGeometry.getClass(), shapeGeometryFromLocalToScene.getClass());
				assertEquals(shapeGeometry.getClass(), shapeGeometryFromSceneToLocal.getClass());

				// ensure applying getTransformed() returns the same result (compared to
				// applying the identity transform on the preserved geometry)
				assertEquals(shapeGeometry, shapeGeometryFromParentToLocal);
				assertEquals(shapeGeometry, shapeGeometryFromSceneToLocal);
				assertEquals(shapeGeometry.getTransformed(FX2Geometry.toAffineTransform(transforms[j])),
						shapeGeometryFromLocalToParent.getTransformed(new AffineTransform()));
				assertEquals(shapeGeometry.getTransformed(FX2Geometry.toAffineTransform(transforms[j])),
						shapeGeometryFromLocalToScene.getTransformed(new AffineTransform()));
			}
		}
	}
}
