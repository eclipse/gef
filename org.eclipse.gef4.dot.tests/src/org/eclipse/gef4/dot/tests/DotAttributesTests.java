/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - implement additional test cases (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowtypeFactory;
import org.eclipse.gef4.dot.internal.parser.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.DeprecatedShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.PrimitiveShape;
import org.eclipse.gef4.dot.internal.parser.point.Point;
import org.eclipse.gef4.dot.internal.parser.point.PointFactory;
import org.eclipse.gef4.dot.internal.parser.splinetype.Spline;
import org.eclipse.gef4.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Node;
import org.junit.Test;

public class DotAttributesTests {

	@Test
	public void node_height() {
		Node n = new Node.Builder().buildNode();

		// set valid values
		DotAttributes.setHeight(n, "0.56");
		DotAttributes.setHeight(n, "76");

		// set invalid values
		try {
			DotAttributes.setHeight(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}
	}

	@Test
	public void node_pos() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		DotAttributes.setPos(n, "47, 11");
		DotAttributes.setPos(n, "34.5, 45.3!");

		// set valid parsed value
		Point pos = PointFactory.eINSTANCE.createPoint();
		pos.setX(33);
		pos.setY(54.6);
		pos.setInputOnly(true);
		DotAttributes.setPosParsed(n, pos);
		assertEquals("33.0, 54.6!", DotAttributes.getPos(n));
		assertTrue(EcoreUtil.equals(DotAttributes.getPosParsed(n), pos));

		// set invalid values
		try {
			DotAttributes.setPos(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'pos' to '47x, 11'. The value '47x, 11' is not a syntactically correct point: No viable alternative at character 'x'.",
					e.getMessage());
		}
	}

	@Test
	public void node_width() {
		Node n = new Node.Builder().buildNode();

		// set valid values
		DotAttributes.setWidth(n, "0.56");
		DotAttributes.setWidth(n, "76");

		// set invalid values
		try {
			DotAttributes.setWidth(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}
	}

	@Test
	public void edge_arrowhead() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		DotAttributes.setArrowHead(edge, "olbox");
		assertEquals("olbox", DotAttributes.getArrowHead(edge));

		ArrowType arrowHead = ArrowtypeFactory.eINSTANCE.createArrowType();
		ArrowShape olBox = ArrowtypeFactory.eINSTANCE.createArrowShape();
		olBox.setOpen(true);
		olBox.setSide("l");
		olBox.setShape(PrimitiveShape.BOX);
		arrowHead.getArrowShapes().add(olBox);
		assertTrue(EcoreUtil.equals(arrowHead,
				DotAttributes.getArrowHeadParsed(edge)));

		// set valid parsed values
		ArrowType arrowHeadParsed = ArrowtypeFactory.eINSTANCE
				.createArrowType();
		ArrowShape rdiamond = ArrowtypeFactory.eINSTANCE.createArrowShape();
		rdiamond.setOpen(false);
		rdiamond.setSide("r");
		rdiamond.setShape(PrimitiveShape.DIAMOND);
		arrowHeadParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowHeadParsed(edge, arrowHeadParsed);
		assertEquals("rdiamond", DotAttributes.getArrowHead(edge));

		// set valid values - multiple arrow shapes
		arrowHeadParsed = ArrowtypeFactory.eINSTANCE.createArrowType();
		arrowHeadParsed.getArrowShapes().add(olBox);
		arrowHeadParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowHeadParsed(edge, arrowHeadParsed);
		assertEquals("olboxrdiamond", DotAttributes.getArrowHead(edge));

		// set deprecated (but valid) values
		DotAttributes.setArrowHead(edge, "ediamond");
		assertEquals("ediamond", DotAttributes.getArrowHead(edge));

		arrowHead = ArrowtypeFactory.eINSTANCE.createArrowType();
		DeprecatedArrowShape deprecatedArrowShape = ArrowtypeFactory.eINSTANCE
				.createDeprecatedArrowShape();
		deprecatedArrowShape.setShape(DeprecatedShape.EDIAMOND);
		arrowHead.getArrowShapes().add(deprecatedArrowShape);
		assertTrue(EcoreUtil.equals(arrowHead,
				DotAttributes.getArrowHeadParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setArrowHead(edge, "olox");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowhead' to 'olox'. The value 'olox' is not a syntactically correct arrowType: No viable alternative at input 'o'. No viable alternative at character 'x'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_arrowtail() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		DotAttributes.setArrowTail(edge, "olbox");
		assertEquals("olbox", DotAttributes.getArrowTail(edge));

		ArrowType arrowTail = ArrowtypeFactory.eINSTANCE.createArrowType();
		ArrowShape olBox = ArrowtypeFactory.eINSTANCE.createArrowShape();
		olBox.setOpen(true);
		olBox.setSide("l");
		olBox.setShape(PrimitiveShape.BOX);
		arrowTail.getArrowShapes().add(olBox);
		assertTrue(EcoreUtil.equals(arrowTail,
				DotAttributes.getArrowTailParsed(edge)));

		// set valid parsed values
		ArrowType arrowTailParsed = ArrowtypeFactory.eINSTANCE
				.createArrowType();
		ArrowShape rdiamond = ArrowtypeFactory.eINSTANCE.createArrowShape();
		rdiamond.setOpen(false);
		rdiamond.setSide("r");
		rdiamond.setShape(PrimitiveShape.DIAMOND);
		arrowTailParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowTailParsed(edge, arrowTailParsed);
		assertEquals("rdiamond", DotAttributes.getArrowTail(edge));

		// set valid values - multiple arrow shapes
		arrowTailParsed = ArrowtypeFactory.eINSTANCE.createArrowType();
		arrowTailParsed.getArrowShapes().add(olBox);
		arrowTailParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowTailParsed(edge, arrowTailParsed);
		assertEquals("olboxrdiamond", DotAttributes.getArrowTail(edge));

		// set deprecated (but valid) values
		DotAttributes.setArrowTail(edge, "ediamond");
		assertEquals("ediamond", DotAttributes.getArrowTail(edge));

		arrowTail = ArrowtypeFactory.eINSTANCE.createArrowType();
		DeprecatedArrowShape deprecatedArrowShape = ArrowtypeFactory.eINSTANCE
				.createDeprecatedArrowShape();
		deprecatedArrowShape.setShape(DeprecatedShape.EDIAMOND);
		arrowTail.getArrowShapes().add(deprecatedArrowShape);
		assertTrue(EcoreUtil.equals(arrowTail,
				DotAttributes.getArrowTailParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setArrowTail(edge, "olox");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowtail' to 'olox'. The value 'olox' is not a syntactically correct arrowType: No viable alternative at input 'o'. No viable alternative at character 'x'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_pos() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge e = new Edge.Builder(n1, n2).buildEdge();

		// set valid values
		DotAttributes.setPos(e,
				"e,42.762,459.02 49.25,203.93 41.039,213.9 31.381,227.75 27,242 3.486,318.47 8.9148,344.07 27,422 29.222,431.57 33.428,441.41 37.82,449.98");
		assertEquals(
				"e,42.762,459.02 " + "49.25,203.93 " + "41.039,213.9 "
						+ "31.381,227.75 " + "27,242 " + "3.486,318.47 "
						+ "8.9148,344.07 " + "27,422 " + "29.222,431.57 "
						+ "33.428,441.41 " + "37.82,449.98",
				DotAttributes.getPos(e));

		SplineType posParsed = DotAttributes.getPosParsed(e);
		assertNotNull(posParsed);
		assertEquals(1, posParsed.getSplines().size());
		Spline spline = posParsed.getSplines().get(0);
		assertNotNull(spline.getEndp());
		assertEquals(spline.getEndp().getX(), 42.762, 0.0);
		assertEquals(spline.getEndp().getY(), 459.02, 0.0);
		assertEquals(10, spline.getControlPoints().size());
		assertNull(spline.getStartp());
	}

}
