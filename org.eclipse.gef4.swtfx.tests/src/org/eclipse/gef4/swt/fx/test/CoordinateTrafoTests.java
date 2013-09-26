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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

public class CoordinateTrafoTests {

	@Test
	public void test_no_trafo() {
		Pane pane = new Pane();

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		// create node to test its behavior
		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		pane.addChildNodes(box);

		// set Pane width x height so that it does not reject our test calls
		pane.setWidth(200);
		pane.setHeight(100);

		// test local box coordinate system
		assertTrue(box.contains(0, 0));
		assertTrue(box.contains(100, 50));
		assertTrue(box.contains(0, 50));
		assertTrue(box.contains(100, 0));
		assertTrue(box.contains(50, 25));
		assertFalse(box.contains(0, 51));

		// test pane coordinate system
		assertSame(box, pane.getNodeAt(new Point(0, 0)));
		assertSame(box, pane.getNodeAt(new Point(100, 50)));
		assertSame(box, pane.getNodeAt(new Point(0, 50)));
		assertSame(box, pane.getNodeAt(new Point(100, 0)));
		assertSame(box, pane.getNodeAt(new Point(50, 25)));
		assertNotSame(box, pane.getNodeAt(new Point(0, 51)));
		assertNotSame(box, pane.getNodeAt(new Point(101, 0)));
	}

	@Test
	public void test_picking_not_contained() {
		AffineTransform tx = new AffineTransform(0.351887384264445,
				-0.502547266434965, 303.1330229625572, 0.502547266434965,
				0.351887384264445, -62.77682910935087);
		Rectangle rect = new Rectangle(0.0, 0.0, 200.0, 100.0);
		Point point = new Point(196.93226843743457, 70.50282858290068);

		Pane pane = new Pane();
		pane.setWidth(500);
		pane.setHeight(500);

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		ShapeFigure box = new ShapeFigure(rect);
		pane.getTransforms().add(tx);
		INode nodeAt = pane.getNodeAt(point);
		assertNotSame(box, nodeAt);
		assertNull(nodeAt);
	}

	@Test
	public void test_pivot_scale() {
		Pane root = new Pane();
		ShapeFigure s = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		new Scene(new Shell(), root);
		root.addChildNodes(s);

		s.setPivot(new Point(50, 50));
		s.setScaleX(2);
		s.setScaleY(2);
		// bounds contains stroke (line width = 1 => 0.5 stroke on all sides)
		assertEquals(new Rectangle(-51, -51, 202, 202), s.getBoundsInParent());
	}

	@Test
	public void test_rotate45_only() {
		Pane pane = new Pane();

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		// create Node for which we test the behavior
		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		box.setPivot(new Point(0, 50));
		box.setRotationAngle(Angle.fromDeg(45));
		// the box is not at: (0, 50), (36, 15), (105, 85), (71, 120)
		// (the values are not exact, but inner integer approximations)
		pane.addChildNodes(box);

		// set Pane width x height so that it does not reject our test calls
		pane.setWidth(200);
		pane.setHeight(200);

		// test local box coordinate system
		assertTrue(box.contains(0, 0));
		assertTrue(box.contains(100, 50));
		assertTrue(box.contains(0, 50));
		assertTrue(box.contains(100, 0));
		assertTrue(box.contains(50, 25));

		assertFalse(box.contains(0, 51));
		assertFalse(box.contains(101, 25));

		// test pane coordinate system
		assertSame(box, pane.getNodeAt(new Point(0, 50)));
		assertSame(box, pane.getNodeAt(new Point(36, 16)));
		assertSame(box, pane.getNodeAt(new Point(105, 85)));
		assertSame(box, pane.getNodeAt(new Point(71, 120)));
		assertSame(box, pane.getNodeAt(new Point(50, 50)));

		assertNotSame(box, pane.getNodeAt(new Point(10, 10)));
		assertNotSame(box, pane.getNodeAt(new Point(100, 50)));
		assertNotSame(box, pane.getNodeAt(new Point(25, 100)));
	}

	@Test
	public void test_rotate90_only() {
		Pane pane = new Pane();

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		// create Node for which we test the behavior
		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		box.setPivot(new Point(50, 50));
		box.setRotationAngle(Angle.fromDeg(90));
		// the box is now at (50,0) sized 50x100
		pane.addChildNodes(box);

		// set Pane width x height so that it does not reject our test calls
		pane.setWidth(200);
		pane.setHeight(200);

		// test local box coordinate system
		assertTrue(box.contains(0, 0));
		assertTrue(box.contains(100, 50));
		assertTrue(box.contains(0, 50));
		assertTrue(box.contains(100, 0));
		assertTrue(box.contains(50, 25));

		assertFalse(box.contains(0, 51));
		assertFalse(box.contains(101, 25));

		// test pane coordinate system
		assertSame(box, pane.getNodeAt(new Point(50, 0)));
		assertSame(box, pane.getNodeAt(new Point(50, 100)));
		assertSame(box, pane.getNodeAt(new Point(100, 100)));
		assertSame(box, pane.getNodeAt(new Point(100, 0)));
		assertSame(box, pane.getNodeAt(new Point(75, 50)));

		assertNotSame(box, pane.getNodeAt(new Point(49, 0)));
		assertNotSame(box, pane.getNodeAt(new Point(101, 0)));
		assertNotSame(box, pane.getNodeAt(new Point(75, 101)));
	}

	@Test
	public void test_scale_only() {
		Pane pane = new Pane();

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		// create Node for which we test the behavior
		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		box.setScaleX(2);
		box.setScaleY(0.5);
		pane.addChildNodes(box);

		// set Pane width x height so that it does not reject our test calls
		pane.setWidth(300);
		pane.setHeight(100);

		// test local box coordinate system
		assertTrue(box.contains(0, 0));
		assertTrue(box.contains(100, 50));
		assertTrue(box.contains(0, 50));
		assertTrue(box.contains(100, 0));
		assertTrue(box.contains(50, 25));
		assertFalse(box.contains(0, 51));

		// test pane coordinate system
		assertSame(box, pane.getNodeAt(new Point(0, 0)));
		assertSame(box, pane.getNodeAt(new Point(200, 0)));
		assertSame(box, pane.getNodeAt(new Point(200, 25)));
		assertSame(box, pane.getNodeAt(new Point(0, 25)));
		assertSame(box, pane.getNodeAt(new Point(100, 15)));

		assertNotSame(box, pane.getNodeAt(new Point(201, 15)));
		assertNotSame(box, pane.getNodeAt(new Point(100, 26)));
	}

	@Test
	public void test_translate_only() {
		Pane pane = new Pane();

		// construct a scene, so that we can use Pane#getNodeAt()
		new Scene(new Shell(), pane);

		// create Node for which we test the behavior
		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		box.setTranslateX(10);
		box.setTranslateY(20);
		pane.addChildNodes(box);

		// set Pane width x height so that it does not reject our test calls
		pane.setWidth(200);
		pane.setHeight(100);

		// test local box coordinate system
		assertTrue(box.contains(0, 0));
		assertTrue(box.contains(100, 50));
		assertTrue(box.contains(0, 50));
		assertTrue(box.contains(100, 0));
		assertTrue(box.contains(50, 25));
		assertFalse(box.contains(0, 51));

		// test pane coordinate system
		assertNotSame(box, pane.getNodeAt(new Point(0, 0)));
		assertSame(box, pane.getNodeAt(new Point(100, 50)));
		assertNotSame(box, pane.getNodeAt(new Point(0, 50)));
		assertNotSame(box, pane.getNodeAt(new Point(100, 0)));
		assertSame(box, pane.getNodeAt(new Point(50, 25)));
		assertNotSame(box, pane.getNodeAt(new Point(0, 51)));
		assertNotSame(box, pane.getNodeAt(new Point(101, 0)));

		assertSame(box, pane.getNodeAt(new Point(10, 20)));
		assertSame(box, pane.getNodeAt(new Point(110, 70)));
		assertSame(box, pane.getNodeAt(new Point(10, 70)));
		assertSame(box, pane.getNodeAt(new Point(110, 20)));
		assertSame(box, pane.getNodeAt(new Point(60, 45)));

		assertNotSame(box, pane.getNodeAt(new Point(111, 40)));
		assertNotSame(box, pane.getNodeAt(new Point(60, 71)));
	}

}
