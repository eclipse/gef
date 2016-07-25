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
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowtypeFactory;
import org.eclipse.gef.dot.internal.parser.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.DeprecatedShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.PrimitiveShape;
import org.eclipse.gef.dot.internal.parser.dir.DirType;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.point.Point;
import org.eclipse.gef.dot.internal.parser.point.PointFactory;
import org.eclipse.gef.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.parser.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.parser.shape.PolygonBasedShape;
import org.eclipse.gef.dot.internal.parser.shape.RecordBasedNodeShape;
import org.eclipse.gef.dot.internal.parser.shape.RecordBasedShape;
import org.eclipse.gef.dot.internal.parser.shape.Shape;
import org.eclipse.gef.dot.internal.parser.shape.ShapeFactory;
import org.eclipse.gef.dot.internal.parser.splines.Splines;
import org.eclipse.gef.dot.internal.parser.splinetype.Spline;
import org.eclipse.gef.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef.dot.internal.parser.splinetype.SplinetypeFactory;
import org.eclipse.gef.dot.internal.parser.style.Style;
import org.eclipse.gef.dot.internal.parser.style.StyleFactory;
import org.eclipse.gef.dot.internal.parser.style.StyleItem;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.junit.Test;

public class DotAttributesTests {

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
	public void edge_arrowsize() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeArrowSize = "0.5";
		DotAttributes.setArrowSize(edge, validEdgeArrowSize);
		assertEquals(validEdgeArrowSize, DotAttributes.getArrowSize(edge));

		// set valid parsed values
		Double validEdgeArrowSizeParsed = new Double(0.0);
		DotAttributes.setArrowSizeParsed(edge, validEdgeArrowSizeParsed);
		assertEquals(validEdgeArrowSizeParsed,
				DotAttributes.getArrowSizeParsed(edge));

		// set syntactically invalid values
		try {
			DotAttributes.setArrowSize(edge, "0,5");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowsize' to '0,5'. The value '0,5' is not a syntactically correct double: For input string: \"0,5\".",
					e.getMessage());
		}

		try {
			DotAttributes.setArrowSize(edge, "foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setArrowSize(edge, "-0.5");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowsize' to '-0.5'. The double value '-0.5' is not semantically correct: Value may not be smaller than 0.0.",
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
	public void edge_dir() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeDir = "forward";
		DotAttributes.setDir(edge, validEdgeDir);
		assertEquals(validEdgeDir, DotAttributes.getDir(edge));
		assertEquals(DirType.FORWARD, DotAttributes.getDirParsed(edge));

		validEdgeDir = "back";
		DotAttributes.setDir(edge, validEdgeDir);
		assertEquals(validEdgeDir, DotAttributes.getDir(edge));
		assertEquals(DirType.BACK, DotAttributes.getDirParsed(edge));

		validEdgeDir = "both";
		DotAttributes.setDir(edge, validEdgeDir);
		assertEquals(validEdgeDir, DotAttributes.getDir(edge));
		assertEquals(DirType.BOTH, DotAttributes.getDirParsed(edge));

		validEdgeDir = "none";
		DotAttributes.setDir(edge, validEdgeDir);
		assertEquals(validEdgeDir, DotAttributes.getDir(edge));
		assertEquals(DirType.NONE, DotAttributes.getDirParsed(edge));

		// set valid parsed values
		DirType validEdgeDirParsed = DirType.FORWARD;
		DotAttributes.setDirParsed(edge, validEdgeDirParsed);
		assertEquals(validEdgeDirParsed.toString(), DotAttributes.getDir(edge));
		assertEquals(validEdgeDirParsed, DotAttributes.getDirParsed(edge));

		validEdgeDirParsed = DirType.BACK;
		DotAttributes.setDirParsed(edge, validEdgeDirParsed);
		assertEquals(validEdgeDirParsed.toString(), DotAttributes.getDir(edge));
		assertEquals(validEdgeDirParsed, DotAttributes.getDirParsed(edge));

		validEdgeDirParsed = DirType.BOTH;
		DotAttributes.setDirParsed(edge, validEdgeDirParsed);
		assertEquals(validEdgeDirParsed.toString(), DotAttributes.getDir(edge));
		assertEquals(validEdgeDirParsed, DotAttributes.getDirParsed(edge));

		validEdgeDirParsed = DirType.NONE;
		DotAttributes.setDirParsed(edge, validEdgeDirParsed);
		assertEquals(validEdgeDirParsed.toString(), DotAttributes.getDir(edge));
		assertEquals(validEdgeDirParsed, DotAttributes.getDirParsed(edge));

		// set invalid string values
		try {
			DotAttributes.setDir(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'dir' to 'foo'. The value 'foo' is not a syntactically correct dirType: Value has to be one of 'forward', 'back', 'both', 'none'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_headlabel() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeHeadLabel = "simpleEdgeLabel";
		DotAttributes.setHeadLabel(edge, validEdgeHeadLabel);
		assertEquals(validEdgeHeadLabel, DotAttributes.getHeadLabel(edge));
	}

	@Test
	public void edge_headlp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeHeadLp = "42,0.0";
		DotAttributes.setHeadLp(edge, validEdgeHeadLp);
		assertEquals(validEdgeHeadLp, DotAttributes.getHeadLp(edge));

		validEdgeHeadLp = "0.0,0.0";
		DotAttributes.setHeadLp(edge, validEdgeHeadLp);
		assertEquals(validEdgeHeadLp, DotAttributes.getHeadLp(edge));

		// set valid parsed values
		Point validEdgeHeadLpParsed = PointFactory.eINSTANCE.createPoint();
		validEdgeHeadLpParsed.setX(42);
		validEdgeHeadLpParsed.setY(0.0);
		DotAttributes.setHeadLpParsed(edge, validEdgeHeadLpParsed);
		assertTrue(EcoreUtil.equals(validEdgeHeadLpParsed,
				DotAttributes.getHeadLpParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setHeadLp(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'head_lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_id() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validEdgeId = "edgeId";
		DotAttributes.setId(edge, validEdgeId);
		assertEquals(validEdgeId, DotAttributes.getId(edge));

		// TODO: add test cases for setting invalid edge id (e.g. a not unique
		// id)
	}

	@Test
	public void edge_label() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validEdgeLabel = "edgeLabel";
		DotAttributes.setLabel(edge, validEdgeLabel);
		assertEquals(validEdgeLabel, DotAttributes.getLabel(edge));
	}

	@Test
	public void edge_lp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeLp = "0.0,1.1";
		DotAttributes.setLp(edge, validEdgeLp);
		assertEquals(validEdgeLp, DotAttributes.getLp(edge));

		// set valid parsed values
		Point validEdgeLpParsed = PointFactory.eINSTANCE.createPoint();
		validEdgeLpParsed.setX(2.2);
		validEdgeLpParsed.setY(3.3);
		DotAttributes.setLpParsed(edge, validEdgeLpParsed);
		assertTrue(EcoreUtil.equals(validEdgeLpParsed,
				DotAttributes.getLpParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setLp(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_name() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validEdgeName = "simpleEdge";
		DotAttributes._setName(edge, validEdgeName);
		assertEquals(validEdgeName, DotAttributes._getName(edge));

		// TODO: add test case for setting invalid edge name (e.g. a keyword)
	}

	@Test
	public void edge_pos() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid values
		DotAttributes.setPos(edge,
				"e,42.762,459.02 49.25,203.93 41.039,213.9 31.381,227.75 27,242 3.486,318.47 8.9148,344.07 27,422 29.222,431.57 33.428,441.41 37.82,449.98");
		assertEquals(
				"e,42.762,459.02 " + "49.25,203.93 " + "41.039,213.9 "
						+ "31.381,227.75 " + "27,242 " + "3.486,318.47 "
						+ "8.9148,344.07 " + "27,422 " + "29.222,431.57 "
						+ "33.428,441.41 " + "37.82,449.98",
				DotAttributes.getPos(edge));

		SplineType posParsed = DotAttributes.getPosParsed(edge);
		assertNotNull(posParsed);
		assertEquals(1, posParsed.getSplines().size());
		Spline spline = posParsed.getSplines().get(0);
		assertNotNull(spline.getEndp());
		assertEquals(spline.getEndp().getX(), 42.762, 0.0);
		assertEquals(spline.getEndp().getY(), 459.02, 0.0);
		assertEquals(10, spline.getControlPoints().size());
		assertNull(spline.getStartp());

		// set valid parsed values: spline with 4 control points
		Point controlPoint0 = PointFactory.eINSTANCE.createPoint();
		controlPoint0.setX(0);
		controlPoint0.setY(0);
		Point controlPoint1 = PointFactory.eINSTANCE.createPoint();
		controlPoint1.setX(1);
		controlPoint1.setY(1);
		Point controlPoint2 = PointFactory.eINSTANCE.createPoint();
		controlPoint2.setX(2);
		controlPoint2.setY(2);
		Point controlPoint3 = PointFactory.eINSTANCE.createPoint();
		controlPoint3.setX(3);
		controlPoint3.setY(3);
		spline = SplinetypeFactory.eINSTANCE.createSpline();
		spline.getControlPoints().add(controlPoint0);
		spline.getControlPoints().add(controlPoint1);
		spline.getControlPoints().add(controlPoint2);
		spline.getControlPoints().add(controlPoint3);
		posParsed = SplinetypeFactory.eINSTANCE.createSplineType();
		posParsed.getSplines().add(spline);

		DotAttributes.setPosParsed(edge, posParsed);

		assertEquals("0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0",
				DotAttributes.getPos(edge));
		assertTrue(
				EcoreUtil.equals(posParsed, DotAttributes.getPosParsed(edge)));

		// set valid parsed values: spline with 4 control points and a start
		// point
		Point startPoint = PointFactory.eINSTANCE.createPoint();
		startPoint.setX(10);
		startPoint.setY(11);
		spline = SplinetypeFactory.eINSTANCE.createSpline();
		spline.setStartp(startPoint);
		spline.getControlPoints().add(controlPoint0);
		spline.getControlPoints().add(controlPoint1);
		spline.getControlPoints().add(controlPoint2);
		spline.getControlPoints().add(controlPoint3);
		posParsed = SplinetypeFactory.eINSTANCE.createSplineType();
		posParsed.getSplines().add(spline);

		DotAttributes.setPosParsed(edge, posParsed);

		assertEquals("s,10.0,11.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0",
				DotAttributes.getPos(edge));
		assertTrue(
				EcoreUtil.equals(posParsed, DotAttributes.getPosParsed(edge)));

		// set valid parsed values: spline with 4 control points and an end
		// point
		Point endPoint = PointFactory.eINSTANCE.createPoint();
		endPoint.setX(20);
		endPoint.setY(21);
		spline = SplinetypeFactory.eINSTANCE.createSpline();
		spline.getControlPoints().add(controlPoint0);
		spline.getControlPoints().add(controlPoint1);
		spline.getControlPoints().add(controlPoint2);
		spline.getControlPoints().add(controlPoint3);
		spline.setEndp(endPoint);
		posParsed = SplinetypeFactory.eINSTANCE.createSplineType();
		posParsed.getSplines().add(spline);

		DotAttributes.setPosParsed(edge, posParsed);

		assertEquals("e,20.0,21.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0",
				DotAttributes.getPos(edge));
		assertTrue(
				EcoreUtil.equals(posParsed, DotAttributes.getPosParsed(edge)));

		// set valid parsed values: spline with 4 control points, start and end
		// point
		spline = SplinetypeFactory.eINSTANCE.createSpline();
		spline.setStartp(startPoint);
		spline.getControlPoints().add(controlPoint0);
		spline.getControlPoints().add(controlPoint1);
		spline.getControlPoints().add(controlPoint2);
		spline.getControlPoints().add(controlPoint3);
		spline.setEndp(endPoint);
		posParsed = SplinetypeFactory.eINSTANCE.createSplineType();
		posParsed.getSplines().add(spline);

		DotAttributes.setPosParsed(edge, posParsed);

		assertEquals("s,10.0,11.0 e,20.0,21.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0",
				DotAttributes.getPos(edge));
		assertTrue(
				EcoreUtil.equals(posParsed, DotAttributes.getPosParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setPos(edge, "s,10.0,11.0 e,20.0,21.0");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'pos' to 's,10.0,11.0 e,20.0,21.0'. The value 's,10.0,11.0 e,20.0,21.0' is not a syntactically correct splineType: Mismatched input '<EOF>' expecting RULE_DOUBLE.",
					e.getMessage());
		}

		// TODO: add test case for setting invalid parsed values
	}

	@Test
	public void edge_style() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String[] validEdgeStyleItems = { "bold", "dashed", "dotted", "invis",
				"solid", "tapered" };

		for (String validEdgeStyleItem : validEdgeStyleItems) {
			DotAttributes.setStyle(edge, validEdgeStyleItem);
			assertEquals(validEdgeStyleItem, DotAttributes.getStyle(edge));

			Style styleParsed = StyleFactory.eINSTANCE.createStyle();
			StyleItem styleItem = StyleFactory.eINSTANCE.createStyleItem();
			styleItem.setName(validEdgeStyleItem);
			styleParsed.getStyleItems().add(styleItem);
			assertTrue(EcoreUtil.equals(styleParsed,
					DotAttributes.getStyleParsed(edge)));
		}

		String validEdgeStyle = "";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		// set valid parsed values
		Style styleParsed = StyleFactory.eINSTANCE.createStyle();
		StyleItem styleItem1 = StyleFactory.eINSTANCE.createStyleItem();
		styleItem1.setName("bold");
		StyleItem styleItem2 = StyleFactory.eINSTANCE.createStyleItem();
		styleItem2.setName("dashed");

		styleParsed.getStyleItems().add(styleItem1);
		styleParsed.getStyleItems().add(styleItem2);
		DotAttributes.setStyleParsed(edge, styleParsed);
		assertEquals("bold , dashed", DotAttributes.getStyle(edge));

		// set syntactically invalid values
		try {
			DotAttributes.setStyle(edge, "bold, ");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'style' to 'bold, '. The value 'bold, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME.",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setStyle(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'.",
					e.getMessage());
		}

		try {
			DotAttributes.setStyle(edge, "diagonals");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'style' to 'diagonals'. The style value 'diagonals' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_taillabel() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeTailLabel = "simpleEdgeLabel";
		DotAttributes.setTailLabel(edge, validEdgeTailLabel);
		assertEquals(validEdgeTailLabel, DotAttributes.getTailLabel(edge));
	}

	@Test
	public void edge_taillp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeTailLp = "42,0.0";
		DotAttributes.setTailLp(edge, validEdgeTailLp);
		assertEquals(validEdgeTailLp, DotAttributes.getTailLp(edge));

		validEdgeTailLp = "0.0,0.0";
		DotAttributes.setTailLp(edge, validEdgeTailLp);
		assertEquals(validEdgeTailLp, DotAttributes.getTailLp(edge));

		// set valid parsed values
		Point validEdgeTailLpParsed = PointFactory.eINSTANCE.createPoint();
		validEdgeTailLpParsed.setX(42);
		validEdgeTailLpParsed.setY(0.0);
		DotAttributes.setTailLpParsed(edge, validEdgeTailLpParsed);
		assertTrue(EcoreUtil.equals(validEdgeTailLpParsed,
				DotAttributes.getTailLpParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setTailLp(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'tail_lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_xlabel() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validEdgeXLabel = "edgeXLabel";
		DotAttributes.setXLabel(edge, validEdgeXLabel);
		assertEquals(validEdgeXLabel, DotAttributes.getXLabel(edge));
	}

	@Test
	public void edge_xlp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		DotAttributes.setXlp(edge, "47, 11");
		DotAttributes.setXlp(edge, "34.5, 45.3!");

		// set valid parsed values
		Point xlp = PointFactory.eINSTANCE.createPoint();
		xlp.setX(33);
		xlp.setY(54.6);
		DotAttributes.setXlpParsed(edge, xlp);
		assertEquals("33.0, 54.6", DotAttributes.getXlp(edge));
		assertTrue(EcoreUtil.equals(DotAttributes.getXlpParsed(edge), xlp));

		// set invalid string values
		try {
			DotAttributes.setXlp(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'xlp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_forcelabels() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphForceLabels = "true";
		DotAttributes.setForceLabels(g, validGraphForceLabels);
		assertEquals(validGraphForceLabels, DotAttributes.getForceLabels(g));

		validGraphForceLabels = "false";
		DotAttributes.setForceLabels(g, validGraphForceLabels);
		assertEquals(validGraphForceLabels, DotAttributes.getForceLabels(g));

		// set valid parsed values
		boolean validGraphForceLabelsParsed = true;
		DotAttributes.setForceLabelsParsed(g, validGraphForceLabelsParsed);
		assertEquals(validGraphForceLabelsParsed,
				DotAttributes.getForceLabelsParsed(g));

		validGraphForceLabelsParsed = false;
		DotAttributes.setForceLabelsParsed(g, validGraphForceLabelsParsed);
		assertEquals(validGraphForceLabelsParsed,
				DotAttributes.getForceLabelsParsed(g));

		// set invalid string values
		try {
			DotAttributes.setForceLabels(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'forcelabels' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value.",
					e.getMessage());
		}
	}

	@Test
	public void graph_id() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		final String validGraphId = "graphId";
		DotAttributes.setId(g, validGraphId);
		assertEquals(validGraphId, DotAttributes.getId(g));

		// TODO: add test cases for setting invalid graph id (e.g. a not unique
		// id)
	}

	@Test
	public void graph_label() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		final String validGraphLabel = "graphLabel";
		DotAttributes.setLabel(g, validGraphLabel);
		assertEquals(validGraphLabel, DotAttributes.getLabel(g));
	}

	@Test
	public void graph_layout() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphLayout = "circo";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.CIRCO, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "dot";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.DOT, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "fdp";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.FDP, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "grid";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.GRID, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "neato";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.NEATO, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "osage";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.OSAGE, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "sfdp";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.SFDP, DotAttributes.getLayoutParsed(g));

		validGraphLayout = "twopi";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));
		assertEquals(Layout.TWOPI, DotAttributes.getLayoutParsed(g));

		// set valid parsed values
		Layout validGraphLayoutParsed = Layout.CIRCO;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.DOT;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.CIRCO;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.FDP;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.GRID;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.NEATO;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.OSAGE;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.SFDP;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		validGraphLayoutParsed = Layout.TWOPI;
		DotAttributes.setLayoutParsed(g, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(g));
		assertEquals(validGraphLayoutParsed, DotAttributes.getLayoutParsed(g));

		// set invalid string values
		try {
			DotAttributes.setLayout(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'layout' to 'foo'. The layout value 'foo' is not semantically correct: Value should be one of 'circo', 'dot', 'fdp', 'grid', 'neato', 'osage', 'sfdp', 'twopi'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_lp() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphLp = "0.0,1.1";
		DotAttributes.setLp(g, validGraphLp);
		assertEquals(validGraphLp, DotAttributes.getLp(g));

		// set valid parsed values
		Point validGraphLpParsed = PointFactory.eINSTANCE.createPoint();
		validGraphLpParsed.setX(2.2);
		validGraphLpParsed.setY(3.3);
		DotAttributes.setLpParsed(g, validGraphLpParsed);
		assertTrue(EcoreUtil.equals(validGraphLpParsed,
				DotAttributes.getLpParsed(g)));
		assertEquals("2.2, 3.3", DotAttributes.getLp(g));

		// set invalid string values
		try {
			DotAttributes.setLp(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_name() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		final String validGraphName = "simpleGraph";
		DotAttributes._setName(g, validGraphName);
		assertEquals(validGraphName, DotAttributes._getName(g));

		// TODO: add test cases for setting invalid graph name (e.g. a keyword)
	}

	@Test
	public void graph_rankdir() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphRankdir = "LR";
		DotAttributes.setRankdir(g, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(g));
		assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(g));

		validGraphRankdir = "RL";
		DotAttributes.setRankdir(g, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(g));
		assertEquals(Rankdir.RL, DotAttributes.getRankdirParsed(g));

		validGraphRankdir = "TB";
		DotAttributes.setRankdir(g, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(g));
		assertEquals(Rankdir.TB, DotAttributes.getRankdirParsed(g));

		validGraphRankdir = "BT";
		DotAttributes.setRankdir(g, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(g));
		assertEquals(Rankdir.BT, DotAttributes.getRankdirParsed(g));

		// set valid parsed values
		Rankdir validGraphRankdirParsed = Rankdir.LR;
		DotAttributes.setRankdirParsed(g, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(g));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(g));

		validGraphRankdirParsed = Rankdir.RL;
		DotAttributes.setRankdirParsed(g, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(g));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(g));

		validGraphRankdirParsed = Rankdir.TB;
		DotAttributes.setRankdirParsed(g, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(g));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(g));

		validGraphRankdirParsed = Rankdir.BT;
		DotAttributes.setRankdirParsed(g, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(g));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(g));

		// set invalid string values
		try {
			DotAttributes.setRankdir(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'rankdir' to 'foo'. The value 'foo' is not a syntactically correct rankdir: The given value 'foo' has to be one of 'TB', 'LR', 'BT', 'RL'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_splines() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphSplines = "compound";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.COMPOUND, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "curved";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.CURVED, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "false";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.FALSE, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "line";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.LINE, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "none";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.NONE, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "spline";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.SPLINE, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "polyline";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.POLYLINE, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "ortho";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.ORTHO, DotAttributes.getSplinesParsed(g));

		validGraphSplines = "true";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));
		assertEquals(Splines.TRUE, DotAttributes.getSplinesParsed(g));

		// set valid parsed values
		Splines validGraphSplinesParsed = Splines.COMPOUND;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.COMPOUND, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.CURVED;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.CURVED, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.EMPTY;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.EMPTY, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.FALSE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.FALSE, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.LINE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.LINE, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.NONE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.NONE, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.ORTHO;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.ORTHO, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.POLYLINE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.POLYLINE, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.SPLINE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.SPLINE, DotAttributes.getSplinesParsed(g));

		validGraphSplinesParsed = Splines.TRUE;
		DotAttributes.setSplinesParsed(g, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(g));
		assertEquals(Splines.TRUE, DotAttributes.getSplinesParsed(g));

		// set invalid string values
		try {
			DotAttributes.setSplines(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'splines' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value. The splines string value 'foo' is not semantically correct: Value should be one of 'compound', 'curved', '', 'false', 'line', 'none', 'ortho', 'polyline', 'spline', 'true'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_type() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphType = "graph";
		DotAttributes._setType(g, validGraphType);
		assertEquals(validGraphType, DotAttributes._getType(g));

		validGraphType = "digraph";
		DotAttributes._setType(g, validGraphType);
		assertEquals(validGraphType, DotAttributes._getType(g));

		// set invalid string values
		try {
			DotAttributes._setType(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute \"type\" to \"foo\"; supported values: graph, digraph",
					e.getMessage());
		}
	}

	@Test
	public void node_distortion() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeDistortion = "5";
		DotAttributes.setDistortion(n, validNodeDistortion);
		assertEquals(validNodeDistortion, DotAttributes.getDistortion(n));
		assertEquals(5.0, DotAttributes.getDistortionParsed(n).doubleValue(),
				0.0);

		// set the minimum valid value
		validNodeDistortion = "-100.0";
		DotAttributes.setDistortion(n, validNodeDistortion);
		assertEquals(validNodeDistortion, DotAttributes.getDistortion(n));
		assertEquals(-100.0, DotAttributes.getDistortionParsed(n).doubleValue(),
				0.0);

		// set valid parsed values
		Double validNodeDistortionParsed = 10.0;
		DotAttributes.setDistortionParsed(n, validNodeDistortionParsed);
		assertEquals("10.0", DotAttributes.getDistortion(n));
		assertEquals(validNodeDistortionParsed,
				DotAttributes.getDistortionParsed(n));

		validNodeDistortionParsed = 9.9;
		DotAttributes.setDistortionParsed(n, validNodeDistortionParsed);
		assertEquals("9.9", DotAttributes.getDistortion(n));
		assertEquals(validNodeDistortionParsed,
				DotAttributes.getDistortionParsed(n));

		// set syntactically invalid values
		try {
			DotAttributes.setDistortion(n, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'distortion' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setDistortion(n, "-100.01");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'distortion' to '-100.01'. The double value '-100.01' is not semantically correct: Value may not be smaller than -100.0.",
					e.getMessage());
		}
	}

	@Test
	public void node_fixedsize() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeFixedSize = "true";
		DotAttributes.setFixedSize(n, validNodeFixedSize);
		assertEquals(validNodeFixedSize, DotAttributes.getFixedSize(n));

		validNodeFixedSize = "false";
		DotAttributes.setFixedSize(n, validNodeFixedSize);
		assertEquals(validNodeFixedSize, DotAttributes.getFixedSize(n));

		// set valid parsed values
		boolean validNodeFixedSizeParsed = true;
		DotAttributes.setFixedSizeParsed(n, validNodeFixedSizeParsed);
		assertEquals(validNodeFixedSizeParsed,
				DotAttributes.getFixedSizeParsed(n));

		validNodeFixedSizeParsed = false;
		DotAttributes.setFixedSizeParsed(n, validNodeFixedSizeParsed);
		assertEquals(validNodeFixedSizeParsed,
				DotAttributes.getFixedSizeParsed(n));

		// set invalid string values
		try {
			DotAttributes.setFixedSize(n, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fixedsize' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value.",
					e.getMessage());
		}
	}

	@Test
	public void node_height() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeHeight = "0.56";
		DotAttributes.setHeight(n, validNodeHeight);
		assertEquals(validNodeHeight, DotAttributes.getHeight(n));

		// set the minimum valid value
		validNodeHeight = "0.02";
		DotAttributes.setHeight(n, validNodeHeight);
		assertEquals(validNodeHeight, DotAttributes.getHeight(n));

		// set valid parsed values
		Double validNodeHeightParsed = 0.1;
		DotAttributes.setHeightParsed(n, validNodeHeightParsed);
		assertEquals(validNodeHeightParsed, DotAttributes.getHeightParsed(n));

		validNodeHeightParsed = 9.9;
		DotAttributes.setHeightParsed(n, validNodeHeightParsed);
		assertEquals(validNodeHeightParsed, DotAttributes.getHeightParsed(n));

		// set syntactically invalid values
		try {
			DotAttributes.setHeight(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setHeight(n, "0.01");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '0.01'. The double value '0.01' is not semantically correct: Value may not be smaller than 0.02.",
					e.getMessage());
		}
	}

	@Test
	public void node_id() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validNodeId = "nodeId";
		DotAttributes.setId(n, validNodeId);
		assertEquals(validNodeId, DotAttributes.getId(n));

		// TODO: add test cases for setting invalid node id (e.g. a not unique
		// id)
	}

	@Test
	public void node_label() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validNodeLabel = "nodeLabel";
		DotAttributes.setLabel(n, validNodeLabel);
		assertEquals(validNodeLabel, DotAttributes.getLabel(n));
	}

	@Test
	public void node_name() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validNodeName = "simpleNode";
		DotAttributes._setName(n, validNodeName);
		assertEquals(validNodeName, DotAttributes._getName(n));

		// TODO: add test case for setting invalid node name (e.g. a keyword)
	}

	@Test
	public void node_pos() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		DotAttributes.setPos(n, "47, 11");
		DotAttributes.setPos(n, "34.5, 45.3!");
		DotAttributes.setPos(n, "-221.31,936.82");

		// set valid parsed values
		Point pos = PointFactory.eINSTANCE.createPoint();
		pos.setX(33);
		pos.setY(54.6);
		pos.setInputOnly(true);
		DotAttributes.setPosParsed(n, pos);
		assertEquals("33.0, 54.6!", DotAttributes.getPos(n));
		assertTrue(EcoreUtil.equals(DotAttributes.getPosParsed(n), pos));

		// set invalid string values
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
	public void node_shape() {
		Node n = new Node.Builder().buildNode();

		// set valid (polygon based) string values
		String[] validPolygonBasedNodeShapes = { "assembly", "box", "box3d",
				"cds", "circle", "component", "cylinder", "diamond",
				"doublecircle", "doubleoctagon", "egg", "ellipse",
				"fivepoverhang", "folder", "hexagon", "house", "insulator",
				"invhouse", "invtrapezium", "invtriangle", "larrow",
				"lpromoter", "Mcircle", "Mdiamond", "Msquare", "none", "note",
				"noverhang", "octagon", "oval", "parallelogram", "pentagon",
				"plain", "plaintext", "point", "polygon", "primersite",
				"promoter", "proteasesite", "proteinstab", "rarrow", "rect",
				"rectangle", "restrictionsite", "ribosite", "rnastab",
				"rpromoter", "septagon", "signature", "square", "star", "tab",
				"terminator", "threepoverhang", "trapezium", "triangle",
				"tripleoctagon", "underline", "utr" };

		for (String validPolygonBasedNodeShape : validPolygonBasedNodeShapes) {
			DotAttributes.setShape(n, validPolygonBasedNodeShape);
			assertEquals(validPolygonBasedNodeShape, DotAttributes.getShape(n));

			Shape shapeParsed = ShapeFactory.eINSTANCE.createShape();
			PolygonBasedShape polygonBasedShape = ShapeFactory.eINSTANCE
					.createPolygonBasedShape();
			polygonBasedShape.setShape(
					PolygonBasedNodeShape.get(validPolygonBasedNodeShape));
			shapeParsed.setShape(polygonBasedShape);
			assertTrue(EcoreUtil.equals(shapeParsed,
					DotAttributes.getShapeParsed(n)));
		}

		// set valid (record based) string values
		String[] validRecordBasedNodeShapes = { "record", "Mrecord" };

		for (String validRecordBasedNodeShape : validRecordBasedNodeShapes) {
			DotAttributes.setShape(n, validRecordBasedNodeShape);
			assertEquals(validRecordBasedNodeShape, DotAttributes.getShape(n));

			Shape shapeParsed = ShapeFactory.eINSTANCE.createShape();
			RecordBasedShape recordBasedShape = ShapeFactory.eINSTANCE
					.createRecordBasedShape();
			recordBasedShape.setShape(
					RecordBasedNodeShape.get(validRecordBasedNodeShape));
			shapeParsed.setShape(recordBasedShape);
			assertTrue(EcoreUtil.equals(shapeParsed,
					DotAttributes.getShapeParsed(n)));
		}

		// set valid parsed values
		Shape validNodeShapeParsed = ShapeFactory.eINSTANCE.createShape();
		PolygonBasedShape polygonBasedShape = ShapeFactory.eINSTANCE
				.createPolygonBasedShape();
		polygonBasedShape.setShape(PolygonBasedNodeShape.BOX);
		validNodeShapeParsed.setShape(polygonBasedShape);
		DotAttributes.setShapeParsed(n, validNodeShapeParsed);
		assertEquals("box", DotAttributes.getShape(n));

		// set invalid string values
		try {
			DotAttributes.setShape(n, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'shape' to 'foo'. The value 'foo' is not a syntactically correct shape: Mismatched character 'o' expecting 'l'.",
					e.getMessage());
		}
	}

	@Test
	public void node_sides() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeSides = "5";
		DotAttributes.setSides(n, validNodeSides);
		assertEquals(validNodeSides, DotAttributes.getSides(n));
		assertEquals(5, DotAttributes.getSidesParsed(n).intValue());

		// set the minimum valid value
		validNodeSides = "0";
		DotAttributes.setSides(n, validNodeSides);
		assertEquals(validNodeSides, DotAttributes.getSides(n));
		assertEquals(0, DotAttributes.getSidesParsed(n).intValue());

		// set valid parsed values
		Integer validNodeSidesParsed = 3;
		DotAttributes.setSidesParsed(n, validNodeSidesParsed);
		assertEquals("3", DotAttributes.getSides(n));
		assertEquals(validNodeSidesParsed, DotAttributes.getSidesParsed(n));

		validNodeSidesParsed = 42;
		DotAttributes.setSidesParsed(n, validNodeSidesParsed);
		assertEquals("42", DotAttributes.getSides(n));
		assertEquals(validNodeSidesParsed, DotAttributes.getSidesParsed(n));

		// set syntactically invalid values
		try {
			DotAttributes.setSides(n, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'sides' to '42x'. The value '42x' is not a syntactically correct int: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setSides(n, "-1");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'sides' to '-1'. The int value '-1' is not semantically correct: Value may not be smaller than 0.",
					e.getMessage());
		}
	}

	@Test
	public void node_skew() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeSkew = "5";
		DotAttributes.setSkew(n, validNodeSkew);
		assertEquals(validNodeSkew, DotAttributes.getSkew(n));
		assertEquals(5.0, DotAttributes.getSkewParsed(n).doubleValue(), 0.0);

		// set the minimum valid value
		validNodeSkew = "-100.0";
		DotAttributes.setSkew(n, validNodeSkew);
		assertEquals(validNodeSkew, DotAttributes.getSkew(n));
		assertEquals(-100.0, DotAttributes.getSkewParsed(n).doubleValue(), 0.0);

		// set valid parsed values
		Double validNodeSkewParsed = 10.0;
		DotAttributes.setSkewParsed(n, validNodeSkewParsed);
		assertEquals("10.0", DotAttributes.getSkew(n));
		assertEquals(validNodeSkewParsed, DotAttributes.getSkewParsed(n));

		validNodeSkewParsed = 9.9;
		DotAttributes.setSkewParsed(n, validNodeSkewParsed);
		assertEquals("9.9", DotAttributes.getSkew(n));
		assertEquals(validNodeSkewParsed, DotAttributes.getSkewParsed(n));

		// set syntactically invalid values
		try {
			DotAttributes.setSkew(n, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'skew' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setSkew(n, "-100.01");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'skew' to '-100.01'. The double value '-100.01' is not semantically correct: Value may not be smaller than -100.0.",
					e.getMessage());
		}
	}

	@Test
	public void node_style() {
		Node node = new Node.Builder().buildNode();

		// set valid string values
		String[] validNodeStyleItems = { "bold", "dashed", "diagonals",
				"dotted", "filled", "invis", "radial", "rounded", "solid",
				"striped", "wedged" };

		for (String validNodeStyleItem : validNodeStyleItems) {
			DotAttributes.setStyle(node, validNodeStyleItem);
			assertEquals(validNodeStyleItem, DotAttributes.getStyle(node));

			Style styleParsed = StyleFactory.eINSTANCE.createStyle();
			StyleItem styleItem = StyleFactory.eINSTANCE.createStyleItem();
			styleItem.setName(validNodeStyleItem);
			styleParsed.getStyleItems().add(styleItem);
			assertTrue(EcoreUtil.equals(styleParsed,
					DotAttributes.getStyleParsed(node)));
		}

		String validNodeStyle = "";
		DotAttributes.setStyle(node, validNodeStyle);
		assertEquals(validNodeStyle, DotAttributes.getStyle(node));

		// set valid parsed values
		Style styleParsed = StyleFactory.eINSTANCE.createStyle();
		StyleItem styleItem1 = StyleFactory.eINSTANCE.createStyleItem();
		styleItem1.setName("bold");
		StyleItem styleItem2 = StyleFactory.eINSTANCE.createStyleItem();
		styleItem2.setName("dashed");

		styleParsed.getStyleItems().add(styleItem1);
		styleParsed.getStyleItems().add(styleItem2);
		DotAttributes.setStyleParsed(node, styleParsed);
		assertEquals("bold , dashed", DotAttributes.getStyle(node));

		// set syntactically invalid values
		try {
			DotAttributes.setStyle(node, "bold, ");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'style' to 'bold, '. The value 'bold, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME.",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setStyle(node, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'.",
					e.getMessage());
		}

		try {
			DotAttributes.setStyle(node, "tapered");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'style' to 'tapered'. The style value 'tapered' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'.",
					e.getMessage());
		}
	}

	@Test
	public void node_width() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		String validNodeWidth = "0.56";
		DotAttributes.setWidth(n, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(n));

		validNodeWidth = "76";
		DotAttributes.setWidth(n, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(n));

		// set the minimum valid value
		validNodeWidth = "0.01";
		DotAttributes.setWidth(n, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(n));

		// set valid parsed values
		Double validNodeWidthParsed = 0.1;
		DotAttributes.setWidthParsed(n, validNodeWidthParsed);
		assertEquals(validNodeWidthParsed, DotAttributes.getWidthParsed(n));

		validNodeWidthParsed = 9.9;
		DotAttributes.setWidthParsed(n, validNodeWidthParsed);
		assertEquals(validNodeWidthParsed, DotAttributes.getWidthParsed(n));

		// set syntactically invalid values
		try {
			DotAttributes.setWidth(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setWidth(n, "0.009");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '0.009'. The double value '0.009' is not semantically correct: Value may not be smaller than 0.01.",
					e.getMessage());
		}
	}

	@Test
	public void node_xlabel() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validNodeXLabel = "nodeXLabel";
		DotAttributes.setXLabel(n, validNodeXLabel);
		assertEquals(validNodeXLabel, DotAttributes.getXLabel(n));
	}

	@Test
	public void node_xlp() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		DotAttributes.setXlp(n, "47, 11");
		DotAttributes.setXlp(n, "34.5, 45.3!");

		// set valid parsed values
		Point xlp = PointFactory.eINSTANCE.createPoint();
		xlp.setX(33);
		xlp.setY(54.6);
		xlp.setInputOnly(true);
		DotAttributes.setXlpParsed(n, xlp);
		assertEquals("33.0, 54.6!", DotAttributes.getXlp(n));
		assertTrue(EcoreUtil.equals(DotAttributes.getXlpParsed(n), xlp));

		// set invalid string values
		try {
			DotAttributes.setXlp(n, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'xlp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}
}
