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
import org.eclipse.gef4.dot.internal.parser.dir.DirType;
import org.eclipse.gef4.dot.internal.parser.point.Point;
import org.eclipse.gef4.dot.internal.parser.point.PointFactory;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef4.dot.internal.parser.splinetype.Spline;
import org.eclipse.gef4.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef4.dot.internal.parser.splinetype.SplinetypeFactory;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.junit.Test;

public class DotAttributesTests {

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

		// TODO: add test cases for setting invalid graph forcelabels
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
	public void graph_layout() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphLayout = "circo";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "dot";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "fdp";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "grid";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "neato";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "osage";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "sfdp";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

		validGraphLayout = "twopi";
		DotAttributes.setLayout(g, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(g));

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
	public void graph_name() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		final String validGraphName = "simpleGraph";
		DotAttributes._setName(g, validGraphName);
		assertEquals(validGraphName, DotAttributes._getName(g));

		// TODO: add test cases for setting invalid graph name (e.g. a keyword)
	}

	@Test
	public void graph_splines() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphSplines = "compound";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		// TODO: add test cases for setting the graph spline attribute to
		// "curved"

		validGraphSplines = "false";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "line";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "none";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "spline";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "polyline";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "ortho";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		validGraphSplines = "true";
		DotAttributes.setSplines(g, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(g));

		// set invalid string values
		try {
			DotAttributes.setSplines(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'splines' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value. The splines string value 'foo' is not semantically correct: Value should be one of '', 'compound', 'false', 'line', 'none', 'ortho', 'polyline', 'spline', 'true'.",
					e.getMessage());
		}
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

		// TODO: add test cases for setting invalid node label
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

		// TODO: add test cases for setting invalid node exterior label
		// positions
	}

	@Test
	public void node_xlabel() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validNodeXLabel = "nodeXLabel";
		DotAttributes.setXLabel(n, validNodeXLabel);
		assertEquals(validNodeXLabel, DotAttributes.getXLabel(n));

		// TODO: add test cases for setting invalid node xlabel
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
					// TODO: remove unnecessary period at the end of the error
					// message
					"Cannot set edge attribute 'dir' to 'foo'. The value 'foo' is not a syntactically correct dirType: Value has to be one of 'forward', 'back', 'both', 'none'..",
					e.getMessage());
		}
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

		// TODO: add test cases for setting invalid edge head label positions
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

		// TODO: add test cases for setting invalid edge label positions
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

		// TODO: add test cases for setting invalid edge label
	}

	@Test
	public void edge_id() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validEdgeId = "nodeId";
		DotAttributes.setId(edge, validEdgeId);
		assertEquals(validEdgeId, DotAttributes.getId(edge));

		// TODO: add test cases for setting invalid edge id (e.g. a not unique
		// id)
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

		// set valid string values
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

		// set valid parsed values
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

		// TODO: check if this string representation is correct
		assertEquals("0.0 , 0.0 1.0 , 1.0 2.0 , 2.0 3.0 , 3.0",
				DotAttributes.getPos(edge));
		assertTrue(
				EcoreUtil.equals(posParsed, DotAttributes.getPosParsed(edge)));

		// TODO: add test case for setting invalid edge position
	}

	@Test
	public void edge_style() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		String validEdgeStyle = "bold";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "dashed";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "dotted";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "invis";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "solid";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "tapered";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		validEdgeStyle = "";
		DotAttributes.setStyle(edge, validEdgeStyle);
		assertEquals(validEdgeStyle, DotAttributes.getStyle(edge));

		// set invalid string values
		try {
			DotAttributes.setStyle(edge, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of '', 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'.",
					e.getMessage());
		}
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

		// TODO: add test cases for setting invalid edge tail label positions
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

		// TODO: add test cases for setting invalid edge exterior label
		// positions
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

		// TODO: add test cases for setting invalid edge xlabel
	}
}
