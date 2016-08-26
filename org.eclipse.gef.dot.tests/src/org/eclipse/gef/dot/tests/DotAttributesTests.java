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
import org.eclipse.gef.dot.internal.parser.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowtypeFactory;
import org.eclipse.gef.dot.internal.parser.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.DeprecatedShape;
import org.eclipse.gef.dot.internal.parser.arrowtype.PrimitiveShape;
import org.eclipse.gef.dot.internal.parser.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.parser.color.ColorFactory;
import org.eclipse.gef.dot.internal.parser.color.HSVColor;
import org.eclipse.gef.dot.internal.parser.color.RGBColor;
import org.eclipse.gef.dot.internal.parser.color.StringColor;
import org.eclipse.gef.dot.internal.parser.dir.DirType;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.parser.pagedir.Pagedir;
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
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
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
	public void edge_color() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values - rgb format
		DotAttributes.setColor(edge, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getColor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setColor(edge, "#ffffff42");
		assertEquals("#ffffff42", DotAttributes.getColor(edge));
		rgbColor.setA("42");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setColor(edge, "0.000 0.000 1.000");
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setColor(edge, "white");
		assertEquals("white", DotAttributes.getColor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ab");
		rgbColor.setG("cd");
		rgbColor.setB("ef");
		DotAttributes.setColorParsed(edge, rgbColor);
		assertEquals("#abcdef", DotAttributes.getColor(edge));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setColorParsed(edge, rgbColor);
		assertEquals("#abcdef00", DotAttributes.getColor(edge));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setColorParsed(edge, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(edge));
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setColorParsed(edge, stringColor);
		assertEquals("white", DotAttributes.getColor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setColor(edge, "#foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'color' to '#foo'. The value '#foo' is not a syntactically correct color: No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_colorscheme() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values
		final String validColorScheme = "x11";
		DotAttributes.setColorScheme(edge, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorScheme(edge));

		// set invalid string values
		try {
			DotAttributes.setColorScheme(edge, "#foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'colorscheme' to '#foo'. The colorscheme value '#foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
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
	public void edge_fillcolor() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values - rgb format
		DotAttributes.setFillColor(edge, "#000000");
		assertEquals("#000000", DotAttributes.getFillColor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("00");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setFillColor(edge, "#0000002a");
		assertEquals("#0000002a", DotAttributes.getFillColor(edge));
		rgbColor.setA("2a");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setFillColor(edge, "0.000 0.000 0.000");
		assertEquals("0.000 0.000 0.000", DotAttributes.getFillColor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("0.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setFillColor(edge, "black");
		assertEquals("black", DotAttributes.getFillColor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("black");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("12");
		rgbColor.setG("34");
		rgbColor.setB("56");
		DotAttributes.setFillColorParsed(edge, rgbColor);
		assertEquals("#123456", DotAttributes.getFillColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("78");
		DotAttributes.setFillColorParsed(edge, rgbColor);
		assertEquals("#12345678", DotAttributes.getFillColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setFillColorParsed(edge, hsvColor);
		assertEquals("0.000 0.000 0.000", DotAttributes.getFillColor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillColorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setFillColorParsed(edge, stringColor);
		assertEquals("black", DotAttributes.getFillColor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillColorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setFillColor(edge, "#ff");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'fillcolor' to '#ff'. The value '#ff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.",
					e.getMessage());
		}
	}

	@Test
	public void edge_fontcolor() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values - rgb format
		DotAttributes.setFontColor(edge, "#ff0000");
		assertEquals("#ff0000", DotAttributes.getFontColor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("00");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setFontColor(edge, "#ff0000bb");
		assertEquals("#ff0000bb", DotAttributes.getFontColor(edge));
		rgbColor.setA("bb");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setFontColor(edge, "0.000 1.000 1.000");
		assertEquals("0.000 1.000 1.000", DotAttributes.getFontColor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("1.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setFontColor(edge, "red");
		assertEquals("red", DotAttributes.getFontColor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("red");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("00");
		rgbColor.setB("00");
		DotAttributes.setFontColorParsed(edge, rgbColor);
		assertEquals("#ff0000", DotAttributes.getFontColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("bb");
		DotAttributes.setFontColorParsed(edge, rgbColor);
		assertEquals("#ff0000bb", DotAttributes.getFontColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setFontColorParsed(edge, hsvColor);
		assertEquals("0.000 1.000 1.000", DotAttributes.getFontColor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setFontColorParsed(edge, stringColor);
		assertEquals("red", DotAttributes.getFontColor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setFontColor(edge, "#fffffffff");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'fontcolor' to '#fffffffff'. The value '#fffffffff' is not a syntactically correct color: Extraneous input 'f' expecting EOF.",
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
	public void edge_labelfontcolor() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// set valid string values - rgb format
		DotAttributes.setLabelFontColor(edge, "#40e0d0");
		assertEquals("#40e0d0", DotAttributes.getLabelFontColor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("40");
		rgbColor.setG("e0");
		rgbColor.setB("d0");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setLabelFontColor(edge, "#40e0d0cc");
		assertEquals("#40e0d0cc", DotAttributes.getLabelFontColor(edge));
		rgbColor.setA("cc");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setLabelFontColor(edge, "0.482 0.714 0.878");
		assertEquals("0.482 0.714 0.878",
				DotAttributes.getLabelFontColor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.482");
		hsvColor.setS("0.714");
		hsvColor.setV("0.878");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setLabelFontColor(edge, "turquoise");
		assertEquals("turquoise", DotAttributes.getLabelFontColor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("turquoise");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("40");
		rgbColor.setG("e0");
		rgbColor.setB("d0");
		DotAttributes.setLabelFontColorParsed(edge, rgbColor);
		assertEquals("#40e0d0", DotAttributes.getLabelFontColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("cc");
		DotAttributes.setLabelFontColorParsed(edge, rgbColor);
		assertEquals("#40e0d0cc", DotAttributes.getLabelFontColor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setLabelFontColorParsed(edge, hsvColor);
		assertEquals("0.482 0.714 0.878",
				DotAttributes.getLabelFontColor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setLabelFontColorParsed(edge, stringColor);
		assertEquals("turquoise", DotAttributes.getLabelFontColor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getLabelFontColorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setLabelFontColor(edge, "");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'labelfontcolor' to ''. The value '' is not a syntactically correct color: No viable alternative at input '<EOF>'.",
					e.getMessage());
		}
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
	public void graph_bgcolor() {
		Graph g = new Graph.Builder().build();

		// set valid string values - rgb format
		DotAttributes.setBgColor(g, "#a0522d");
		assertEquals("#a0522d", DotAttributes.getBgColor(g));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("a0");
		rgbColor.setG("52");
		rgbColor.setB("2d");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getBgColorParsed(g)));

		// set valid string values - rgba format
		DotAttributes.setBgColor(g, "#a0522dcc");
		assertEquals("#a0522dcc", DotAttributes.getBgColor(g));
		rgbColor.setA("cc");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getBgColorParsed(g)));

		// set valid string values - hsv format
		DotAttributes.setBgColor(g, ".051 .718 .627");
		assertEquals(".051 .718 .627", DotAttributes.getBgColor(g));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH(".051");
		hsvColor.setS(".718");
		hsvColor.setV(".627");
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getBgColorParsed(g)));

		// set valid string values - string format
		DotAttributes.setBgColor(g, "sienna");
		assertEquals("sienna", DotAttributes.getBgColor(g));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("sienna");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getBgColorParsed(g)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("a0");
		rgbColor.setG("52");
		rgbColor.setB("2d");
		DotAttributes.setBgColorParsed(g, rgbColor);
		assertEquals("#a0522d", DotAttributes.getBgColor(g));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getBgColorParsed(g)));

		// set valid parsed values - rgba format
		rgbColor.setA("cc");
		DotAttributes.setBgColorParsed(g, rgbColor);
		assertEquals("#a0522dcc", DotAttributes.getBgColor(g));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getBgColorParsed(g)));

		// set valid parsed values - hsv format
		DotAttributes.setBgColorParsed(g, hsvColor);
		assertEquals(".051 .718 .627", DotAttributes.getBgColor(g));
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getBgColorParsed(g)));

		// set valid parsed values - string format
		DotAttributes.setBgColorParsed(g, stringColor);
		assertEquals("sienna", DotAttributes.getBgColor(g));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getBgColorParsed(g)));

		// set invalid string values
		try {
			DotAttributes.setBgColor(g, "#gggggg");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'bgcolor' to '#gggggg'. The value '#gggggg' is not a syntactically correct color: No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_clusterrank() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphClusterMode = "local";
		DotAttributes.setClusterRank(g, validGraphClusterMode);
		assertEquals(validGraphClusterMode, DotAttributes.getClusterRank(g));
		assertEquals(ClusterMode.LOCAL, DotAttributes.getClusterRankParsed(g));

		validGraphClusterMode = "global";
		DotAttributes.setClusterRank(g, validGraphClusterMode);
		assertEquals(validGraphClusterMode, DotAttributes.getClusterRank(g));
		assertEquals(ClusterMode.GLOBAL, DotAttributes.getClusterRankParsed(g));

		validGraphClusterMode = "none";
		DotAttributes.setClusterRank(g, validGraphClusterMode);
		assertEquals(validGraphClusterMode, DotAttributes.getClusterRank(g));
		assertEquals(ClusterMode.NONE, DotAttributes.getClusterRankParsed(g));

		// set valid parsed values
		ClusterMode validGraphClusterModeParsed = ClusterMode.LOCAL;
		DotAttributes.setClusterRankParsed(g, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterRank(g));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterRankParsed(g));

		validGraphClusterModeParsed = ClusterMode.GLOBAL;
		DotAttributes.setClusterRankParsed(g, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterRank(g));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterRankParsed(g));

		validGraphClusterModeParsed = ClusterMode.NONE;
		DotAttributes.setClusterRankParsed(g, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterRank(g));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterRankParsed(g));

		// set invalid string values
		try {
			DotAttributes.setClusterRank(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'clusterrank' to 'foo'. The value 'foo' is not a syntactically correct clusterMode: Value has to be one of 'local', 'global', 'none'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_colorscheme() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		final String validColorScheme = "svg";
		DotAttributes.setColorScheme(g, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorScheme(g));

		try {
			DotAttributes.setColorScheme(g, "foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'colorscheme' to 'foo'. The colorscheme value 'foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_fontcolor() {
		Graph g = new Graph.Builder().build();

		// set valid string values - rgb format
		DotAttributes.setFontColor(g, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getFontColor(g));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid string values - rgba format
		DotAttributes.setFontColor(g, "#ffffff00");
		assertEquals("#ffffff00", DotAttributes.getFontColor(g));
		rgbColor.setA("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid string values - hsv format
		DotAttributes.setFontColor(g, "0.000,0.000,1.000");
		assertEquals("0.000,0.000,1.000", DotAttributes.getFontColor(g));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid string values - string format
		DotAttributes.setFontColor(g, "//white");
		assertEquals("//white", DotAttributes.getFontColor(g));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		DotAttributes.setFontColorParsed(g, rgbColor);
		assertEquals("#ffffff", DotAttributes.getFontColor(g));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setFontColorParsed(g, rgbColor);
		assertEquals("#ffffff00", DotAttributes.getFontColor(g));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid parsed values - hsv format
		DotAttributes.setFontColorParsed(g, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getFontColor(g));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(g)));

		// set valid parsed values - string format
		DotAttributes.setFontColorParsed(g, stringColor);
		assertEquals("white", DotAttributes.getFontColor(g));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(g)));

		// set invalid string values
		try {
			DotAttributes.setFontColor(g, "/white");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'fontcolor' to '/white'. The value '/white' is not a syntactically correct color: Mismatched input '<EOF>' expecting '/'.",
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
					"Cannot set graph attribute 'layout' to 'foo'. The layout value 'foo' is not semantically correct: Value should be one of 'circo', 'dot', 'fdp', 'neato', 'osage', 'sfdp', 'twopi'.",
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
	public void graph_outputorder() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphOutputMode = "breadthfirst";
		DotAttributes.setOutputOrder(g, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputOrder(g));
		assertEquals(OutputMode.BREADTHFIRST,
				DotAttributes.getOutputOrderParsed(g));

		validGraphOutputMode = "nodesfirst";
		DotAttributes.setOutputOrder(g, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputOrder(g));
		assertEquals(OutputMode.NODESFIRST,
				DotAttributes.getOutputOrderParsed(g));

		validGraphOutputMode = "edgesfirst";
		DotAttributes.setOutputOrder(g, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputOrder(g));
		assertEquals(OutputMode.EDGEFIRST,
				DotAttributes.getOutputOrderParsed(g));

		// set valid parsed values
		OutputMode validGraphOutputModeParsed = OutputMode.BREADTHFIRST;
		DotAttributes.setOutputOrderParsed(g, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputOrder(g));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputOrderParsed(g));

		validGraphOutputModeParsed = OutputMode.NODESFIRST;
		DotAttributes.setOutputOrderParsed(g, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputOrder(g));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputOrderParsed(g));

		validGraphOutputModeParsed = OutputMode.EDGEFIRST;
		DotAttributes.setOutputOrderParsed(g, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputOrder(g));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputOrderParsed(g));

		// set invalid string values
		try {
			DotAttributes.setOutputOrder(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'outputorder' to 'foo'. The value 'foo' is not a syntactically correct outputMode: Value has to be one of 'breadthfirst', 'nodesfirst', 'edgesfirst'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_pagedir() {
		Graph g = new Graph.Builder().build();

		// set valid string values
		String validGraphPagedir = "BL";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.BL, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "BR";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.BR, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "TL";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.TL, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "TR";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.TR, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "RB";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.RB, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "RT";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.RT, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "LB";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.LB, DotAttributes.getPagedirParsed(g));

		validGraphPagedir = "LT";
		DotAttributes.setPagedir(g, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(g));
		assertEquals(Pagedir.LT, DotAttributes.getPagedirParsed(g));

		// set valid parsed values
		Pagedir validGraphPagedirParsed = Pagedir.BL;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.BR;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.TL;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.TR;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.RB;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.RT;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.LB;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		validGraphPagedirParsed = Pagedir.LT;
		DotAttributes.setPagedirParsed(g, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(g));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(g));

		// set invalid string values
		try {
			DotAttributes.setPagedir(g, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'pagedir' to 'foo'. The value 'foo' is not a syntactically correct pagedir: Value has to be one of 'BL', 'BR', 'TL', 'TR', 'RB', 'RT', 'LB', 'LT'.",
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
	public void node_color() {
		Node n = new Node.Builder().buildNode();

		// set valid string values - rgb format
		DotAttributes.setColor(n, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getColor(n));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(n)));

		// set valid string values - rgba format
		DotAttributes.setColor(n, "#ffffff00");
		assertEquals("#ffffff00", DotAttributes.getColor(n));
		rgbColor.setA("00");
		assertTrue(EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(n)));

		// set valid string values - hsv format
		DotAttributes.setColor(n, "0.000, 0.000, 1.000");
		assertEquals("0.000, 0.000, 1.000", DotAttributes.getColor(n));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(n)));

		// set valid string values - string format
		DotAttributes.setColor(n, "/svg/white");
		assertEquals("/svg/white", DotAttributes.getColor(n));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("svg");
		stringColor.setName("white");
		assertTrue(
				EcoreUtil.equals(stringColor, DotAttributes.getColorParsed(n)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		DotAttributes.setColorParsed(n, rgbColor);
		assertEquals("#ffffff", DotAttributes.getColor(n));
		assertTrue(EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(n)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setColorParsed(n, rgbColor);
		assertEquals("#ffffff00", DotAttributes.getColor(n));
		assertTrue(EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(n)));

		// set valid parsed values - hsv format
		DotAttributes.setColorParsed(n, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(n));
		assertTrue(EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(n)));

		// set valid parsed values - string format
		DotAttributes.setColorParsed(n, stringColor);
		assertEquals("/svg/white", DotAttributes.getColor(n));
		assertTrue(
				EcoreUtil.equals(stringColor, DotAttributes.getColorParsed(n)));

		// set invalid string values
		try {
			DotAttributes.setColor(n, "/foo/antiquewhite1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'color' to '/foo/antiquewhite1'. The color value '/foo/antiquewhite1' is not semantically correct: 'foo' is not a valid color scheme.",
					e.getMessage());
		}

		try {
			DotAttributes.setColor(n, "/svg/antiquewhite1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'color' to '/svg/antiquewhite1'. The color value '/svg/antiquewhite1' is not semantically correct: The 'antiquewhite1' color is not valid within the 'svg' color scheme.",
					e.getMessage());
		}
	}

	@Test
	public void node_colorscheme() {
		Node n = new Node.Builder().buildNode();

		// set valid string values
		final String validColorScheme = "accent3";
		DotAttributes.setColorScheme(n, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorScheme(n));

		try {
			DotAttributes.setColorScheme(n, "1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'colorscheme' to '1'. The colorscheme value '1' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
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
	public void node_fillcolor() {
		Node n = new Node.Builder().buildNode();

		// set valid string values - rgb format
		DotAttributes.setFillColor(n, "#00ff00");
		assertEquals("#00ff00", DotAttributes.getFillColor(n));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid string values - rgba format
		DotAttributes.setFillColor(n, "#00ff00ff");
		assertEquals("#00ff00ff", DotAttributes.getFillColor(n));
		rgbColor.setA("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid string values - hsv format
		DotAttributes.setFillColor(n, "0.3 .8 .7");
		assertEquals("0.3 .8 .7", DotAttributes.getFillColor(n));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.3");
		hsvColor.setS(".8");
		hsvColor.setV(".7");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid string values - string format
		DotAttributes.setFillColor(n, "/bugn9/7");
		assertEquals("/bugn9/7", DotAttributes.getFillColor(n));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("bugn9");
		stringColor.setName("7");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		DotAttributes.setFillColorParsed(n, rgbColor);
		assertEquals("#00ff00", DotAttributes.getFillColor(n));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid parsed values - rgba format
		rgbColor.setA("ff");
		DotAttributes.setFillColorParsed(n, rgbColor);
		assertEquals("#00ff00ff", DotAttributes.getFillColor(n));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid parsed values - hsv format
		DotAttributes.setFillColorParsed(n, hsvColor);
		assertEquals("0.3 .8 .7", DotAttributes.getFillColor(n));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillColorParsed(n)));

		// set valid parsed values - string format
		DotAttributes.setFillColorParsed(n, stringColor);
		assertEquals("/bugn9/7", DotAttributes.getFillColor(n));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillColorParsed(n)));

		// set invalid string values
		try {
			DotAttributes.setFillColor(n, "//");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fillcolor' to '//'. The value '//' is not a syntactically correct color: No viable alternative at input '<EOF>'.",
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
	public void node_fontcolor() {
		Node n = new Node.Builder().buildNode();

		// set valid string values - rgb format
		DotAttributes.setFontColor(n, "#00ff00");
		assertEquals("#00ff00", DotAttributes.getFontColor(n));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid string values - rgba format
		DotAttributes.setFontColor(n, "#00ff00ff");
		assertEquals("#00ff00ff", DotAttributes.getFontColor(n));
		rgbColor.setA("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid string values - hsv format
		DotAttributes.setFontColor(n, "0.3, .8, .7");
		assertEquals("0.3, .8, .7", DotAttributes.getFontColor(n));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.3");
		hsvColor.setS(".8");
		hsvColor.setV(".7");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid string values - string format
		DotAttributes.setFontColor(n, "/brbg11/10");
		assertEquals("/brbg11/10", DotAttributes.getFontColor(n));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("brbg11");
		stringColor.setName("10");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		DotAttributes.setFontColorParsed(n, rgbColor);
		assertEquals("#00ff00", DotAttributes.getFontColor(n));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid parsed values - rgba format
		rgbColor.setA("ff");
		DotAttributes.setFontColorParsed(n, rgbColor);
		assertEquals("#00ff00ff", DotAttributes.getFontColor(n));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid parsed values - hsv format
		DotAttributes.setFontColorParsed(n, hsvColor);
		assertEquals("0.3 .8 .7", DotAttributes.getFontColor(n));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontColorParsed(n)));

		// set valid parsed values - string format
		DotAttributes.setFontColorParsed(n, stringColor);
		assertEquals("/brbg11/10", DotAttributes.getFontColor(n));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontColorParsed(n)));

		// set invalid string values
		try {
			DotAttributes.setFontColor(n, "///");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fontcolor' to '///'. The value '///' is not a syntactically correct color: No viable alternative at input '/'.",
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
					"Cannot set node attribute 'shape' to 'foo'. The value 'foo' is not a syntactically correct shape: No viable alternative at input 'foo'.",
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
