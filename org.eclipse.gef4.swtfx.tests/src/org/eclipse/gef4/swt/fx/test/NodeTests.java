/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.fx.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.StrokeType;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.junit.Test;

/*
 * TODO: Write tests for the scene graph abstractions: INode, IParent, and IFigure.
 * 
 * A) Scene graph child-parent relationships
 * 
 * 1. Simple
 *  - Create a simple scene graph consisting of one IParent and one IFigure.
 *  - Manually set their child-parent relationship (if necessary).
 *  - Call methods that inform about the child-parent relationship (such as getParentNode()).
 *  - Test if the return values of those methods match the expectation.
 * 
 * 2. "Complex"
 *  - Create a complex scene graph:
 *           Pane
 *   Pane     IF     Pane
 *  IF  IF        Pane IF IF
 *                 IF
 *  - Check all relations in there.
 * 
 * 
 * B) Scene graph transformations
 * 
 * 1. Simple
 *  - Create one IFigure.
 *  - Only change layout-attributes, test local-to-parent-tx
 *  - Only change translation-attributes, test local-to-parent-tx
 *  - Only change scale-attributes, test local-to-parent-tx
 *  - Only change rotation-attributes, test local-to-parent-tx
 *  - Add one other trafo to the figure's trafo list and check again.
 *  - Add another trafo to the figure's trafo list and check again.
 * 
 * 2. "Complex"
 *  - Create a complex scene graph:
 *           Pane
 *   Pane     IF     Pane
 *  IF  IF        Pane IF IF
 *                 IF
 *  - Twist some transformation attributes and check if the several transformations are okay.
 */
public class NodeTests {

	@Test
	public void test_relationship() {
		IParent parent = new Pane();
		IFigure child = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		parent.addChildren(child);

		assertEquals(parent, child.getParentNode());
		assertEquals(child, parent.getChildrenUnmodifiable().get(0));
	}

	@Test
	public void test_relationship_complex() {
		Rectangle SHAPE = new Rectangle(0, 0, 100, 100);

		IParent root = new Pane();
		IParent left = new Pane();
		IParent right = new Pane();
		IFigure mid = new ShapeFigure(SHAPE);
		root.addChildren(left, mid, right);

		assertEquals(root, left.getParentNode());
		assertEquals(root, mid.getParentNode());
		assertEquals(root, right.getParentNode());

		assertEquals(left, root.getChildrenUnmodifiable().get(0));
		assertEquals(mid, root.getChildrenUnmodifiable().get(1));
		assertEquals(right, root.getChildrenUnmodifiable().get(2));

		IFigure leftLeft = new ShapeFigure(SHAPE);
		IFigure leftMid = new ShapeFigure(SHAPE);
		IFigure leftRight = new ShapeFigure(SHAPE);
		left.addChildren(leftLeft, leftMid, leftRight);

		assertEquals(left, leftLeft.getParentNode());
		assertEquals(left, leftMid.getParentNode());
		assertEquals(left, leftRight.getParentNode());

		assertEquals(leftLeft, left.getChildrenUnmodifiable().get(0));
		assertEquals(leftMid, left.getChildrenUnmodifiable().get(1));
		assertEquals(leftRight, left.getChildrenUnmodifiable().get(2));

		IFigure rightLeft = new ShapeFigure(SHAPE);
		IFigure rightRight = new ShapeFigure(SHAPE);
		right.addChildren(rightLeft, rightRight);

		assertEquals(right, rightLeft.getParentNode());
		assertEquals(right, rightRight.getParentNode());

		assertEquals(rightLeft, right.getChildrenUnmodifiable().get(0));
		assertEquals(rightRight, right.getChildrenUnmodifiable().get(1));
	}

	@Test
	public void test_transforms() {
		AffineTransform IDENTITY = new AffineTransform();
		Rectangle SHAPE = new Rectangle(0, 0, 100, 100);

		// add parent to another parent, so that we do not need a Scene for the
		// transformation calculations
		IParent root = new Pane();
		IParent parent = new Pane();
		root.addChildren(parent);

		ShapeFigure child = new ShapeFigure(SHAPE);
		parent.addChildren(child);

		// would die if parent did not have a parent
		assertEquals(IDENTITY, parent.getLocalToParentTransform());
		assertEquals(IDENTITY, child.getLocalToParentTransform());
		assertEquals(child.getBoundsInLocal(), child.getBoundsInParent());

		// assure that the stroke does not expand layout-bounds
		child.setStrokeType(StrokeType.INSIDE);
		assertEquals(SHAPE, child.getLayoutBounds());
		assertEquals(SHAPE, child.getBoundsInLocal());

		/*
		 * Check that setting transformation attributes on a node yields the
		 * correct local-to-parent transform.
		 * 
		 * Note that the order of applying the transformations to the identity
		 * matrix is important, because a node is specified to apply them in
		 * this order: translate, rotate, scale
		 * 
		 * Note that when using a pivot point, the transformations are enclosed
		 * by a translation to that point and the negated translation, i.e.
		 * translate(pivot.x, pivot.y) -> translate(...) -> rotate(...) ->
		 * scale(...) -> translate(-pivot.x, -pivot.y)
		 */
		AffineTransform at = IDENTITY.getCopy();
		child.setTranslateX(10);
		child.setTranslateY(5);
		at.translate(10, 5);
		assertEquals(at, child.getLocalToParentTransform());

		child.setRotationAngle(Angle.fromDeg(90));
		at.rotate(Angle.fromDeg(90).rad());
		assertEquals(at, child.getLocalToParentTransform());

		child.setScaleX(2.5);
		child.setScaleY(0.5);
		at.scale(2.5, 0.5);
		assertEquals(at, child.getLocalToParentTransform());

		child.getTransforms().add(new AffineTransform().scale(0.3, 1.4));
		at.scale(0.3, 1.4);
		assertEquals(at, child.getLocalToParentTransform());

		// shear transformations should not be used, but we test it nonetheless
		child.getTransforms().add(new AffineTransform().shear(2, 2));
		at.shear(2, 2);
		assertEquals(at, child.getLocalToParentTransform());

		// check bounds-in-parent
		assertEquals(SHAPE.getTransformed(at).getBounds(),
				child.getBoundsInParent());

		// check pivot behavior; reset transformation attributes first
		child.setTranslateX(0);
		child.setTranslateY(0);
		child.setScaleX(1);
		child.setScaleY(1);
		child.setRotationAngle(Angle.fromRad(0));
		child.getTransforms().clear();

		child.setPivot(new Point(10, 10));
		child.setRotationAngle(Angle.fromDeg(45));
		at.setToIdentity().translate(10, 10).rotate(Angle.fromDeg(45).rad())
				.translate(-10, -10);
		assertEquals(at, child.getLocalToParentTransform());
	}
}
