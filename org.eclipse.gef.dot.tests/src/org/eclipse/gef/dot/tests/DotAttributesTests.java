/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowShape;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypeFactory;
import org.eclipse.gef.dot.internal.language.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef.dot.internal.language.arrowtype.DeprecatedShape;
import org.eclipse.gef.dot.internal.language.arrowtype.PrimitiveShape;
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.ColorFactory;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.language.colorlist.ColorlistFactory;
import org.eclipse.gef.dot.internal.language.colorlist.WC;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.point.Point;
import org.eclipse.gef.dot.internal.language.point.PointFactory;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.ranktype.RankType;
import org.eclipse.gef.dot.internal.language.rect.Rect;
import org.eclipse.gef.dot.internal.language.rect.RectFactory;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedShape;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedShape;
import org.eclipse.gef.dot.internal.language.shape.Shape;
import org.eclipse.gef.dot.internal.language.shape.ShapeFactory;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.splinetype.Spline;
import org.eclipse.gef.dot.internal.language.splinetype.SplineType;
import org.eclipse.gef.dot.internal.language.splinetype.SplinetypeFactory;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.dot.internal.language.style.StyleFactory;
import org.eclipse.gef.dot.internal.language.style.StyleItem;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getArrowheadRaw(edge));
		assertNull(DotAttributes.getArrowhead(edge));
		assertNull(DotAttributes.getArrowheadParsed(edge));

		// set valid string values
		DotAttributes.setArrowhead(edge, "olbox");
		assertEquals("olbox", DotAttributes.getArrowhead(edge));

		ArrowType arrowHead = ArrowtypeFactory.eINSTANCE.createArrowType();
		ArrowShape olBox = ArrowtypeFactory.eINSTANCE.createArrowShape();
		olBox.setOpen(true);
		olBox.setSide("l");
		olBox.setShape(PrimitiveShape.BOX);
		arrowHead.getArrowShapes().add(olBox);
		assertTrue(EcoreUtil.equals(arrowHead,
				DotAttributes.getArrowheadParsed(edge)));

		// set valid parsed values
		ArrowType arrowHeadParsed = ArrowtypeFactory.eINSTANCE
				.createArrowType();
		ArrowShape rdiamond = ArrowtypeFactory.eINSTANCE.createArrowShape();
		rdiamond.setOpen(false);
		rdiamond.setSide("r");
		rdiamond.setShape(PrimitiveShape.DIAMOND);
		arrowHeadParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowheadParsed(edge, arrowHeadParsed);
		assertEquals("rdiamond", DotAttributes.getArrowhead(edge));

		// set valid values - multiple arrow shapes
		arrowHeadParsed = ArrowtypeFactory.eINSTANCE.createArrowType();
		arrowHeadParsed.getArrowShapes().add(olBox);
		arrowHeadParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowheadParsed(edge, arrowHeadParsed);
		assertEquals("olboxrdiamond", DotAttributes.getArrowhead(edge));

		// set deprecated (but valid) values
		DotAttributes.setArrowhead(edge, "ediamond");
		assertEquals("ediamond", DotAttributes.getArrowhead(edge));

		arrowHead = ArrowtypeFactory.eINSTANCE.createArrowType();
		DeprecatedArrowShape deprecatedArrowShape = ArrowtypeFactory.eINSTANCE
				.createDeprecatedArrowShape();
		deprecatedArrowShape.setShape(DeprecatedShape.EDIAMOND);
		arrowHead.getArrowShapes().add(deprecatedArrowShape);
		assertTrue(EcoreUtil.equals(arrowHead,
				DotAttributes.getArrowheadParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setArrowhead(edge, "olox");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getArrowsizeRaw(edge));
		assertNull(DotAttributes.getArrowsize(edge));
		assertNull(DotAttributes.getArrowsizeParsed(edge));

		// set valid string values
		String validEdgeArrowSize = "0.5";
		DotAttributes.setArrowsize(edge, validEdgeArrowSize);
		assertEquals(validEdgeArrowSize, DotAttributes.getArrowsize(edge));

		// set valid parsed values
		Double validEdgeArrowSizeParsed = new Double(0.0);
		DotAttributes.setArrowsizeParsed(edge, validEdgeArrowSizeParsed);
		assertEquals(validEdgeArrowSizeParsed,
				DotAttributes.getArrowsizeParsed(edge));

		// set syntactically invalid values
		try {
			DotAttributes.setArrowsize(edge, "0,5");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowsize' to '0,5'. The value '0,5' is not a syntactically correct double: For input string: \"0,5\".",
					e.getMessage());
		}

		try {
			DotAttributes.setArrowsize(edge, "foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'arrowsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setArrowsize(edge, "-0.5");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getArrowtailRaw(edge));
		assertNull(DotAttributes.getArrowtail(edge));
		assertNull(DotAttributes.getArrowtailParsed(edge));

		// set valid string values
		DotAttributes.setArrowtail(edge, "olbox");
		assertEquals("olbox", DotAttributes.getArrowtail(edge));

		ArrowType arrowTail = ArrowtypeFactory.eINSTANCE.createArrowType();
		ArrowShape olBox = ArrowtypeFactory.eINSTANCE.createArrowShape();
		olBox.setOpen(true);
		olBox.setSide("l");
		olBox.setShape(PrimitiveShape.BOX);
		arrowTail.getArrowShapes().add(olBox);
		assertTrue(EcoreUtil.equals(arrowTail,
				DotAttributes.getArrowtailParsed(edge)));

		// set valid parsed values
		ArrowType arrowTailParsed = ArrowtypeFactory.eINSTANCE
				.createArrowType();
		ArrowShape rdiamond = ArrowtypeFactory.eINSTANCE.createArrowShape();
		rdiamond.setOpen(false);
		rdiamond.setSide("r");
		rdiamond.setShape(PrimitiveShape.DIAMOND);
		arrowTailParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowtailParsed(edge, arrowTailParsed);
		assertEquals("rdiamond", DotAttributes.getArrowtail(edge));

		// set valid values - multiple arrow shapes
		arrowTailParsed = ArrowtypeFactory.eINSTANCE.createArrowType();
		arrowTailParsed.getArrowShapes().add(olBox);
		arrowTailParsed.getArrowShapes().add(rdiamond);
		DotAttributes.setArrowtailParsed(edge, arrowTailParsed);
		assertEquals("olboxrdiamond", DotAttributes.getArrowtail(edge));

		// set deprecated (but valid) values
		DotAttributes.setArrowtail(edge, "ediamond");
		assertEquals("ediamond", DotAttributes.getArrowtail(edge));

		arrowTail = ArrowtypeFactory.eINSTANCE.createArrowType();
		DeprecatedArrowShape deprecatedArrowShape = ArrowtypeFactory.eINSTANCE
				.createDeprecatedArrowShape();
		deprecatedArrowShape.setShape(DeprecatedShape.EDIAMOND);
		arrowTail.getArrowShapes().add(deprecatedArrowShape);
		assertTrue(EcoreUtil.equals(arrowTail,
				DotAttributes.getArrowtailParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setArrowtail(edge, "olox");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorRaw(edge));
		assertNull(DotAttributes.getColor(edge));
		assertNull(DotAttributes.getColorParsed(edge));

		// set valid string values - rgb format
		DotAttributes.setColor(edge, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getColor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getColorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setColor(edge, "#ffffff42");
		assertEquals("#ffffff42", DotAttributes.getColor(edge));
		rgbColor.setA("42");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getColorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setColor(edge, "0.000 0.000 1.000");
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getColorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setColor(edge, "white");
		assertEquals("white", DotAttributes.getColor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getColorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ab");
		rgbColor.setG("cd");
		rgbColor.setB("ef");
		DotAttributes.setColorParsed(edge, createColorList(rgbColor));
		assertEquals("#abcdef", DotAttributes.getColor(edge));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getColorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setColorParsed(edge, createColorList(rgbColor));
		assertEquals("#abcdef00", DotAttributes.getColor(edge));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getColorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setColorParsed(edge, createColorList(hsvColor));
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(edge));
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getColorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setColorParsed(edge, createColorList(stringColor));
		assertEquals("white", DotAttributes.getColor(edge));
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getColorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setColor(edge, "#foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'color' to '#foo'. The value '#foo' is not a syntactically correct colorList: No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void edge_colorscheme() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorschemeRaw(edge));
		assertNull(DotAttributes.getColorscheme(edge));

		// set valid string values
		final String validColorScheme = "x11";
		DotAttributes.setColorscheme(edge, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorscheme(edge));

		// set invalid string values
		try {
			DotAttributes.setColorscheme(edge, "#foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set edge attribute 'colorscheme' to '#foo'. The string value '#foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
					e.getMessage());
		}

	}

	@Test
	public void edge_dir() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getDirRaw(edge));
		assertNull(DotAttributes.getDir(edge));
		assertNull(DotAttributes.getDirParsed(edge));

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

		// test getter if no value explicit is set
		assertNull(DotAttributes.getFillcolorRaw(edge));
		assertNull(DotAttributes.getFillcolor(edge));
		assertNull(DotAttributes.getFillcolorParsed(edge));

		// set valid string values - rgb format
		DotAttributes.setFillcolor(edge, "#000000");
		assertEquals("#000000", DotAttributes.getFillcolor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("00");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setFillcolor(edge, "#0000002a");
		assertEquals("#0000002a", DotAttributes.getFillcolor(edge));
		rgbColor.setA("2a");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setFillcolor(edge, "0.000 0.000 0.000");
		assertEquals("0.000 0.000 0.000", DotAttributes.getFillcolor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("0.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setFillcolor(edge, "black");
		assertEquals("black", DotAttributes.getFillcolor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("black");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("12");
		rgbColor.setG("34");
		rgbColor.setB("56");
		DotAttributes.setFillcolorParsed(edge, rgbColor);
		assertEquals("#123456", DotAttributes.getFillcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("78");
		DotAttributes.setFillcolorParsed(edge, rgbColor);
		assertEquals("#12345678", DotAttributes.getFillcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setFillcolorParsed(edge, hsvColor);
		assertEquals("0.000 0.000 0.000", DotAttributes.getFillcolor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setFillcolorParsed(edge, stringColor);
		assertEquals("black", DotAttributes.getFillcolor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFillcolorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setFillcolor(edge, "#ff");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFontcolorRaw(edge));
		assertNull(DotAttributes.getFontcolor(edge));
		assertNull(DotAttributes.getFontcolorParsed(edge));

		// set valid string values - rgb format
		DotAttributes.setFontcolor(edge, "#ff0000");
		assertEquals("#ff0000", DotAttributes.getFontcolor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("00");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setFontcolor(edge, "#ff0000bb");
		assertEquals("#ff0000bb", DotAttributes.getFontcolor(edge));
		rgbColor.setA("bb");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setFontcolor(edge, "0.000 1.000 1.000");
		assertEquals("0.000 1.000 1.000", DotAttributes.getFontcolor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("1.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setFontcolor(edge, "red");
		assertEquals("red", DotAttributes.getFontcolor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("red");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("00");
		rgbColor.setB("00");
		DotAttributes.setFontcolorParsed(edge, rgbColor);
		assertEquals("#ff0000", DotAttributes.getFontcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("bb");
		DotAttributes.setFontcolorParsed(edge, rgbColor);
		assertEquals("#ff0000bb", DotAttributes.getFontcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setFontcolorParsed(edge, hsvColor);
		assertEquals("0.000 1.000 1.000", DotAttributes.getFontcolor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setFontcolorParsed(edge, stringColor);
		assertEquals("red", DotAttributes.getFontcolor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setFontcolor(edge, "#fffffffff");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getHeadlabelRaw(edge));
		assertNull(DotAttributes.getHeadlabel(edge));

		// set valid string values
		String validEdgeHeadLabel = "simpleEdgeLabel";
		DotAttributes.setHeadlabel(edge, validEdgeHeadLabel);
		assertEquals(validEdgeHeadLabel, DotAttributes.getHeadlabel(edge));
	}

	@Test
	public void edge_headlp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getHeadLpRaw(edge));
		assertNull(DotAttributes.getHeadLp(edge));
		assertNull(DotAttributes.getHeadLpParsed(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getIdRaw(edge));
		assertNull(DotAttributes.getId(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLabelRaw(edge));
		assertNull(DotAttributes.getLabel(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLabelfontcolorRaw(edge));
		assertNull(DotAttributes.getLabelfontcolor(edge));
		assertNull(DotAttributes.getLabelfontcolorParsed(edge));

		// set valid string values - rgb format
		DotAttributes.setLabelfontcolor(edge, "#40e0d0");
		assertEquals("#40e0d0", DotAttributes.getLabelfontcolor(edge));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("40");
		rgbColor.setG("e0");
		rgbColor.setB("d0");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid string values - rgba format
		DotAttributes.setLabelfontcolor(edge, "#40e0d0cc");
		assertEquals("#40e0d0cc", DotAttributes.getLabelfontcolor(edge));
		rgbColor.setA("cc");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid string values - hsv format
		DotAttributes.setLabelfontcolor(edge, "0.482 0.714 0.878");
		assertEquals("0.482 0.714 0.878",
				DotAttributes.getLabelfontcolor(edge));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.482");
		hsvColor.setS("0.714");
		hsvColor.setV("0.878");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid string values - string format
		DotAttributes.setLabelfontcolor(edge, "turquoise");
		assertEquals("turquoise", DotAttributes.getLabelfontcolor(edge));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("turquoise");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("40");
		rgbColor.setG("e0");
		rgbColor.setB("d0");
		DotAttributes.setLabelfontcolorParsed(edge, rgbColor);
		assertEquals("#40e0d0", DotAttributes.getLabelfontcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid parsed values - rgba format
		rgbColor.setA("cc");
		DotAttributes.setLabelfontcolorParsed(edge, rgbColor);
		assertEquals("#40e0d0cc", DotAttributes.getLabelfontcolor(edge));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid parsed values - hsv format
		DotAttributes.setLabelfontcolorParsed(edge, hsvColor);
		assertEquals("0.482 0.714 0.878",
				DotAttributes.getLabelfontcolor(edge));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set valid parsed values - string format
		DotAttributes.setLabelfontcolorParsed(edge, stringColor);
		assertEquals("turquoise", DotAttributes.getLabelfontcolor(edge));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getLabelfontcolorParsed(edge)));

		// set invalid string values
		try {
			DotAttributes.setLabelfontcolor(edge, "");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLpRaw(edge));
		assertNull(DotAttributes.getLp(edge));
		assertNull(DotAttributes.getLpParsed(edge));

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
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2")
				.buildNode();
		Edge e = new Edge.Builder(n1, n2).buildEdge();

		// test edge name calculation on a directed graph
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		graph.nodes(n1, n2).edges(e).build();
		assertEquals("1->2", DotAttributes._getName(e));

		// test edge name calculation on an undirected graph
		graph.attr(DotAttributes::_setType, GraphType.GRAPH);
		graph.nodes(n1, n2).edges(e).build();
		assertEquals("1--2", DotAttributes._getName(e));
	}

	@Test
	public void edge_pos() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getPosRaw(edge));
		assertNull(DotAttributes.getPos(edge));
		assertNull(DotAttributes.getPosParsed(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getStyleRaw(edge));
		assertNull(DotAttributes.getStyle(edge));
		assertNull(DotAttributes.getStyleParsed(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getTaillabelRaw(edge));
		assertNull(DotAttributes.getTaillabel(edge));

		// set valid string values
		String validEdgeTailLabel = "simpleEdgeLabel";
		DotAttributes.setTaillabel(edge, validEdgeTailLabel);
		assertEquals(validEdgeTailLabel, DotAttributes.getTaillabel(edge));
	}

	@Test
	public void edge_taillp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getTailLpRaw(edge));
		assertNull(DotAttributes.getTailLp(edge));
		assertNull(DotAttributes.getTailLpParsed(edge));

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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getXlabelRaw(edge));
		assertNull(DotAttributes.getXlabel(edge));

		// set valid string values
		final String validEdgeXLabel = "edgeXLabel";
		DotAttributes.setXlabel(edge, validEdgeXLabel);
		assertEquals(validEdgeXLabel, DotAttributes.getXlabel(edge));
	}

	@Test
	public void edge_xlp() {
		Node n1 = new Node.Builder().buildNode();
		Node n2 = new Node.Builder().buildNode();
		Edge edge = new Edge.Builder(n1, n2).buildEdge();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getXlpRaw(edge));
		assertNull(DotAttributes.getXlp(edge));
		assertNull(DotAttributes.getXlpParsed(edge));

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
	public void graph_bb() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getBbRaw(graph));
		assertNull(DotAttributes.getBb(graph));
		assertNull(DotAttributes.getBbParsed(graph));

		// set valid string values
		DotAttributes.setBb(graph, "39.631,558,111.63,398");
		assertEquals("39.631,558,111.63,398", DotAttributes.getBb(graph));

		Rect r = DotAttributes.getBbParsed(graph);
		assertNotNull(r);
		assertEquals(r.getLlx(), 39.631d, 0d);
		assertEquals(r.getLly(), 558d, 0d);
		assertEquals(r.getUrx(), 111.63d, 0d);
		assertEquals(r.getUry(), 398d, 0d);

		// set valid parsed values
		Rect bb = RectFactory.eINSTANCE.createRect();
		bb.setLlx(10.1);
		bb.setLly(20.2);
		bb.setUrx(30.3);
		bb.setUry(40.4);
		DotAttributes.setBbParsed(graph, bb);
		assertEquals("10.1 , 20.2 , 30.3 , 40.4", DotAttributes.getBb(graph));

		// set invalid string values
		try {
			DotAttributes.setBb(graph, "39.631,558,111.63");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'bb' to '39.631,558,111.63'. The value '39.631,558,111.63' is not a syntactically correct rect: Mismatched input '<EOF>' expecting ','.",
					e.getMessage());
		}
	}

	@Test
	public void graph_bgcolor() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getBgcolorRaw(graph));
		assertNull(DotAttributes.getBgcolor(graph));
		assertNull(DotAttributes.getBgcolorParsed(graph));

		// set valid string values - rgb format
		DotAttributes.setBgcolor(graph, "#a0522d");
		assertEquals("#a0522d", DotAttributes.getBgcolor(graph));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("a0");
		rgbColor.setG("52");
		rgbColor.setB("2d");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid string values - rgba format
		DotAttributes.setBgcolor(graph, "#a0522dcc");
		assertEquals("#a0522dcc", DotAttributes.getBgcolor(graph));
		rgbColor.setA("cc");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid string values - hsv format
		DotAttributes.setBgcolor(graph, ".051 .718 .627");
		assertEquals(".051 .718 .627", DotAttributes.getBgcolor(graph));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH(".051");
		hsvColor.setS(".718");
		hsvColor.setV(".627");
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid string values - string format
		DotAttributes.setBgcolor(graph, "sienna");
		assertEquals("sienna", DotAttributes.getBgcolor(graph));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("sienna");
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("a0");
		rgbColor.setG("52");
		rgbColor.setB("2d");
		DotAttributes.setBgcolorParsed(graph, createColorList(rgbColor));
		assertEquals("#a0522d", DotAttributes.getBgcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid parsed values - rgba format
		rgbColor.setA("cc");
		DotAttributes.setBgcolorParsed(graph, createColorList(rgbColor));
		assertEquals("#a0522dcc", DotAttributes.getBgcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid parsed values - hsv format
		DotAttributes.setBgcolorParsed(graph, createColorList(hsvColor));
		assertEquals(".051 .718 .627", DotAttributes.getBgcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set valid parsed values - string format
		DotAttributes.setBgcolorParsed(graph, createColorList(stringColor));
		assertEquals("sienna", DotAttributes.getBgcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getBgcolorParsed(graph)));

		// set invalid string values
		try {
			DotAttributes.setBgcolor(graph, "#gggggg");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'bgcolor' to '#gggggg'. The value '#gggggg' is not a syntactically correct colorList: No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_clusterrank() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getClusterrankRaw(graph));
		assertNull(DotAttributes.getClusterrank(graph));
		assertNull(DotAttributes.getClusterrankParsed(graph));

		// set valid string values
		String validGraphClusterMode = "local";
		DotAttributes.setClusterrank(graph, validGraphClusterMode);
		assertEquals(validGraphClusterMode,
				DotAttributes.getClusterrank(graph));
		assertEquals(ClusterMode.LOCAL,
				DotAttributes.getClusterrankParsed(graph));

		validGraphClusterMode = "global";
		DotAttributes.setClusterrank(graph, validGraphClusterMode);
		assertEquals(validGraphClusterMode,
				DotAttributes.getClusterrank(graph));
		assertEquals(ClusterMode.GLOBAL,
				DotAttributes.getClusterrankParsed(graph));

		validGraphClusterMode = "none";
		DotAttributes.setClusterrank(graph, validGraphClusterMode);
		assertEquals(validGraphClusterMode,
				DotAttributes.getClusterrank(graph));
		assertEquals(ClusterMode.NONE,
				DotAttributes.getClusterrankParsed(graph));

		// set valid parsed values
		ClusterMode validGraphClusterModeParsed = ClusterMode.LOCAL;
		DotAttributes.setClusterrankParsed(graph, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterrank(graph));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterrankParsed(graph));

		validGraphClusterModeParsed = ClusterMode.GLOBAL;
		DotAttributes.setClusterrankParsed(graph, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterrank(graph));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterrankParsed(graph));

		validGraphClusterModeParsed = ClusterMode.NONE;
		DotAttributes.setClusterrankParsed(graph, validGraphClusterModeParsed);
		assertEquals(validGraphClusterModeParsed.toString(),
				DotAttributes.getClusterrank(graph));
		assertEquals(validGraphClusterModeParsed,
				DotAttributes.getClusterrankParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setClusterrank(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'clusterrank' to 'foo'. The value 'foo' is not a syntactically correct clusterMode: Value has to be one of 'local', 'global', 'none'.",
					e.getMessage());
		}
	}

	@Test
	public void cluster_color() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorRaw(graph));
		assertNull(DotAttributes.getColor(graph));
		assertNull(DotAttributes.getColorParsed(graph));

		// set valid string values - rgb format
		DotAttributes.setColor(graph, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getColor(graph));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getColorParsed(graph)));

		// set valid string values - rgba format
		DotAttributes.setColor(graph, "#ffffff00");
		assertEquals("#ffffff00", DotAttributes.getColor(graph));
		rgbColor.setA("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getColorParsed(graph)));

		// set valid string values - hsv format
		DotAttributes.setColor(graph, "0.000,0.000,1.000");
		assertEquals("0.000,0.000,1.000", DotAttributes.getColor(graph));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getColorParsed(graph)));

		// set valid string values - string format
		DotAttributes.setColor(graph, "//white");
		assertEquals("//white", DotAttributes.getColor(graph));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(graph)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		DotAttributes.setColorParsed(graph, rgbColor);
		assertEquals("#ffffff", DotAttributes.getColor(graph));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getColorParsed(graph)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setColorParsed(graph, rgbColor);
		assertEquals("#ffffff00", DotAttributes.getColor(graph));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getColorParsed(graph)));

		// set valid parsed values - hsv format
		DotAttributes.setColorParsed(graph, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(graph));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getColorParsed(graph)));

		// set valid parsed values - string format
		DotAttributes.setColorParsed(graph, stringColor);
		assertEquals("white", DotAttributes.getColor(graph));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(graph)));

		// set invalid string values
		try {
			DotAttributes.setColor(graph, "/white");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'color' to '/white'. The value '/white' is not a syntactically correct color: Mismatched input '<EOF>' expecting '/'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_colorscheme() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorschemeRaw(graph));
		assertNull(DotAttributes.getColorscheme(graph));

		// set valid string values
		final String validColorScheme = "svg";
		DotAttributes.setColorscheme(graph, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorscheme(graph));

		try {
			DotAttributes.setColorscheme(graph, "foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'colorscheme' to 'foo'. The string value 'foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_fillcolor() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFillcolorRaw(graph));
		assertNull(DotAttributes.getFillcolor(graph));
		assertNull(DotAttributes.getFillcolorParsed(graph));

		// set valid string values - rgb format
		DotAttributes.setFillcolor(graph, "#00ff00");
		assertEquals("#00ff00", DotAttributes.getFillcolor(graph));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid string values - rgba format
		DotAttributes.setFillcolor(graph, "#00ff00ff");
		assertEquals("#00ff00ff", DotAttributes.getFillcolor(graph));
		rgbColor.setA("ff");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid string values - hsv format
		DotAttributes.setFillcolor(graph, "0.3 .8 .7");
		assertEquals("0.3 .8 .7", DotAttributes.getFillcolor(graph));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.3");
		hsvColor.setS(".8");
		hsvColor.setV(".7");
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid string values - string format
		DotAttributes.setFillcolor(graph, "/bugn9/7");
		assertEquals("/bugn9/7", DotAttributes.getFillcolor(graph));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("bugn9");
		stringColor.setName("7");
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		DotAttributes.setFillcolorParsed(graph, createColorList(rgbColor));
		assertEquals("#00ff00", DotAttributes.getFillcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid parsed values - rgba format
		rgbColor.setA("ff");
		DotAttributes.setFillcolorParsed(graph, createColorList(rgbColor));
		assertEquals("#00ff00ff", DotAttributes.getFillcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid parsed values - hsv format
		DotAttributes.setFillcolorParsed(graph, createColorList(hsvColor));
		assertEquals("0.3 .8 .7", DotAttributes.getFillcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set valid parsed values - string format
		DotAttributes.setFillcolorParsed(graph, createColorList(stringColor));
		assertEquals("/bugn9/7", DotAttributes.getFillcolor(graph));
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getFillcolorParsed(graph)));

		// set invalid string values
		try {
			DotAttributes.setFillcolor(graph, "//");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'fillcolor' to '//'. The value '//' is not a syntactically correct colorList: No viable alternative at input '<EOF>'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_fontcolor() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFontcolorRaw(graph));
		assertNull(DotAttributes.getFontcolor(graph));
		assertNull(DotAttributes.getFontcolorParsed(graph));

		// set valid string values - rgb format
		DotAttributes.setFontcolor(graph, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getFontcolor(graph));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid string values - rgba format
		DotAttributes.setFontcolor(graph, "#ffffff00");
		assertEquals("#ffffff00", DotAttributes.getFontcolor(graph));
		rgbColor.setA("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid string values - hsv format
		DotAttributes.setFontcolor(graph, "0.000,0.000,1.000");
		assertEquals("0.000,0.000,1.000", DotAttributes.getFontcolor(graph));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid string values - string format
		DotAttributes.setFontcolor(graph, "//white");
		assertEquals("//white", DotAttributes.getFontcolor(graph));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		DotAttributes.setFontcolorParsed(graph, rgbColor);
		assertEquals("#ffffff", DotAttributes.getFontcolor(graph));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setFontcolorParsed(graph, rgbColor);
		assertEquals("#ffffff00", DotAttributes.getFontcolor(graph));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid parsed values - hsv format
		DotAttributes.setFontcolorParsed(graph, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getFontcolor(graph));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set valid parsed values - string format
		DotAttributes.setFontcolorParsed(graph, stringColor);
		assertEquals("white", DotAttributes.getFontcolor(graph));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(graph)));

		// set invalid string values
		try {
			DotAttributes.setFontcolor(graph, "/white");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'fontcolor' to '/white'. The value '/white' is not a syntactically correct color: Mismatched input '<EOF>' expecting '/'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_forcelabels() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getForcelabelsRaw(graph));
		assertNull(DotAttributes.getForcelabels(graph));
		assertNull(DotAttributes.getForcelabelsParsed(graph));

		// set valid string values
		String validGraphForceLabels = "true";
		DotAttributes.setForcelabels(graph, validGraphForceLabels);
		assertEquals(validGraphForceLabels,
				DotAttributes.getForcelabels(graph));

		validGraphForceLabels = "false";
		DotAttributes.setForcelabels(graph, validGraphForceLabels);
		assertEquals(validGraphForceLabels,
				DotAttributes.getForcelabels(graph));

		// set valid parsed values
		boolean validGraphForceLabelsParsed = true;
		DotAttributes.setForcelabelsParsed(graph, validGraphForceLabelsParsed);
		assertEquals(validGraphForceLabelsParsed,
				DotAttributes.getForcelabelsParsed(graph));

		validGraphForceLabelsParsed = false;
		DotAttributes.setForcelabelsParsed(graph, validGraphForceLabelsParsed);
		assertEquals(validGraphForceLabelsParsed,
				DotAttributes.getForcelabelsParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setForcelabels(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'forcelabels' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value.",
					e.getMessage());
		}
	}

	@Test
	public void graph_id() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getIdRaw(graph));
		assertNull(DotAttributes.getId(graph));

		// set valid string values
		final String validGraphId = "graphId";
		DotAttributes.setId(graph, validGraphId);
		assertEquals(validGraphId, DotAttributes.getId(graph));

		// TODO: add test cases for setting invalid graph id (e.g. a not unique
		// id)
	}

	@Test
	public void graph_label() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLabelRaw(graph));
		assertNull(DotAttributes.getLabel(graph));

		// set valid string values
		final String validGraphLabel = "graphLabel";
		DotAttributes.setLabel(graph, validGraphLabel);
		assertEquals(validGraphLabel, DotAttributes.getLabel(graph));
	}

	@Test
	public void graph_layout() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLayoutRaw(graph));
		assertNull(DotAttributes.getLayout(graph));
		assertNull(DotAttributes.getLayoutParsed(graph));

		// set valid string values
		String validGraphLayout = "circo";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.CIRCO, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "dot";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.DOT, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "fdp";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.FDP, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "neato";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.NEATO, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "osage";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.OSAGE, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "sfdp";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.SFDP, DotAttributes.getLayoutParsed(graph));

		validGraphLayout = "twopi";
		DotAttributes.setLayout(graph, validGraphLayout);
		assertEquals(validGraphLayout, DotAttributes.getLayout(graph));
		assertEquals(Layout.TWOPI, DotAttributes.getLayoutParsed(graph));

		// set valid parsed values
		Layout validGraphLayoutParsed = Layout.CIRCO;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.DOT;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.CIRCO;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.FDP;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.NEATO;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.OSAGE;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.SFDP;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		validGraphLayoutParsed = Layout.TWOPI;
		DotAttributes.setLayoutParsed(graph, validGraphLayoutParsed);
		assertEquals(validGraphLayoutParsed.toString(),
				DotAttributes.getLayout(graph));
		assertEquals(validGraphLayoutParsed,
				DotAttributes.getLayoutParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setLayout(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'layout' to 'foo'. The value 'foo' is not a syntactically correct layout: Value has to be one of 'circo', 'dot', 'fdp', 'neato', 'osage', 'sfdp', 'twopi'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_lp() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLpRaw(graph));
		assertNull(DotAttributes.getLp(graph));
		assertNull(DotAttributes.getLpParsed(graph));

		// set valid string values
		String validGraphLp = "0.0,1.1";
		DotAttributes.setLp(graph, validGraphLp);
		assertEquals(validGraphLp, DotAttributes.getLp(graph));

		// set valid parsed values
		Point validGraphLpParsed = PointFactory.eINSTANCE.createPoint();
		validGraphLpParsed.setX(2.2);
		validGraphLpParsed.setY(3.3);
		DotAttributes.setLpParsed(graph, validGraphLpParsed);
		assertTrue(EcoreUtil.equals(validGraphLpParsed,
				DotAttributes.getLpParsed(graph)));
		assertEquals("2.2, 3.3", DotAttributes.getLp(graph));

		// set invalid string values
		try {
			DotAttributes.setLp(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_name() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes._getName(graph));
		assertNull(DotAttributes._getNameRaw(graph));

		DotAttributes._setName(graph, "TestGraph");
		assertEquals("TestGraph", DotAttributes._getName(graph));
		assertEquals(ID.fromString("TestGraph"),
				DotAttributes._getNameRaw(graph));

		ID graphName = ID.fromValue("Test Graph", Type.QUOTED_STRING);
		DotAttributes._setNameRaw(graph, graphName);
		assertEquals("Test Graph", DotAttributes._getName(graph));
		assertEquals(ID.fromValue("Test Graph", Type.QUOTED_STRING),
				DotAttributes._getNameRaw(graph));

		DotAttributes._setName(graph, "\"Test Graph\"");
		assertEquals("\"Test Graph\"", DotAttributes._getName(graph));
		assertEquals("\"\\\"Test Graph\\\"\"",
				DotAttributes._getNameRaw(graph).toString());
	}

	@Test
	public void graph_nodesep() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getNodesepRaw(graph));
		assertNull(DotAttributes.getNodesep(graph));
		assertNull(DotAttributes.getNodesepParsed(graph));

		// set valid string values
		String validNodesep = "0.5";
		DotAttributes.setNodesep(graph, validNodesep);
		assertEquals(validNodesep, DotAttributes.getNodesep(graph));

		// set valid parsed values
		Double validNodesepParsed = new Double(0.02);
		DotAttributes.setNodesepParsed(graph, validNodesepParsed);
		assertEquals(validNodesepParsed, DotAttributes.getNodesepParsed(graph));

		// set syntactically invalid values
		try {
			DotAttributes.setNodesep(graph, "0,5");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'nodesep' to '0,5'. The value '0,5' is not a syntactically correct double: For input string: \"0,5\".",
					e.getMessage());
		}

		try {
			DotAttributes.setNodesep(graph, "foo");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'nodesep' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setNodesep(graph, "0.0199");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'nodesep' to '0.0199'. The double value '0.0199' is not semantically correct: Value may not be smaller than 0.02.",
					e.getMessage());
		}
	}

	@Test
	public void graph_outputorder() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getOutputorderRaw(graph));
		assertNull(DotAttributes.getOutputorder(graph));
		assertNull(DotAttributes.getOutputorderParsed(graph));

		// set valid string values
		String validGraphOutputMode = "breadthfirst";
		DotAttributes.setOutputorder(graph, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputorder(graph));
		assertEquals(OutputMode.BREADTHFIRST,
				DotAttributes.getOutputorderParsed(graph));

		validGraphOutputMode = "nodesfirst";
		DotAttributes.setOutputorder(graph, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputorder(graph));
		assertEquals(OutputMode.NODESFIRST,
				DotAttributes.getOutputorderParsed(graph));

		validGraphOutputMode = "edgesfirst";
		DotAttributes.setOutputorder(graph, validGraphOutputMode);
		assertEquals(validGraphOutputMode, DotAttributes.getOutputorder(graph));
		assertEquals(OutputMode.EDGEFIRST,
				DotAttributes.getOutputorderParsed(graph));

		// set valid parsed values
		OutputMode validGraphOutputModeParsed = OutputMode.BREADTHFIRST;
		DotAttributes.setOutputorderParsed(graph, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputorder(graph));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputorderParsed(graph));

		validGraphOutputModeParsed = OutputMode.NODESFIRST;
		DotAttributes.setOutputorderParsed(graph, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputorder(graph));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputorderParsed(graph));

		validGraphOutputModeParsed = OutputMode.EDGEFIRST;
		DotAttributes.setOutputorderParsed(graph, validGraphOutputModeParsed);
		assertEquals(validGraphOutputModeParsed.toString(),
				DotAttributes.getOutputorder(graph));
		assertEquals(validGraphOutputModeParsed,
				DotAttributes.getOutputorderParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setOutputorder(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'outputorder' to 'foo'. The value 'foo' is not a syntactically correct outputMode: Value has to be one of 'breadthfirst', 'nodesfirst', 'edgesfirst'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_pagedir() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getPagedirRaw(graph));
		assertNull(DotAttributes.getPagedir(graph));
		assertNull(DotAttributes.getPagedirParsed(graph));

		// set valid string values
		String validGraphPagedir = "BL";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.BL, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "BR";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.BR, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "TL";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.TL, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "TR";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.TR, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "RB";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.RB, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "RT";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.RT, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "LB";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.LB, DotAttributes.getPagedirParsed(graph));

		validGraphPagedir = "LT";
		DotAttributes.setPagedir(graph, validGraphPagedir);
		assertEquals(validGraphPagedir, DotAttributes.getPagedir(graph));
		assertEquals(Pagedir.LT, DotAttributes.getPagedirParsed(graph));

		// set valid parsed values
		Pagedir validGraphPagedirParsed = Pagedir.BL;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.BR;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.TL;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.TR;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.RB;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.RT;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.LB;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		validGraphPagedirParsed = Pagedir.LT;
		DotAttributes.setPagedirParsed(graph, validGraphPagedirParsed);
		assertEquals(validGraphPagedirParsed.toString(),
				DotAttributes.getPagedir(graph));
		assertEquals(validGraphPagedirParsed,
				DotAttributes.getPagedirParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setPagedir(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'pagedir' to 'foo'. The value 'foo' is not a syntactically correct pagedir: Value has to be one of 'BL', 'BR', 'TL', 'TR', 'RB', 'RT', 'LB', 'LT'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_rankdir() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getRankdirRaw(graph));
		assertNull(DotAttributes.getRankdir(graph));
		assertNull(DotAttributes.getRankdirParsed(graph));

		// set valid string values
		String validGraphRankdir = "LR";
		DotAttributes.setRankdir(graph, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(graph));
		assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(graph));

		validGraphRankdir = "RL";
		DotAttributes.setRankdir(graph, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(graph));
		assertEquals(Rankdir.RL, DotAttributes.getRankdirParsed(graph));

		validGraphRankdir = "TB";
		DotAttributes.setRankdir(graph, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(graph));
		assertEquals(Rankdir.TB, DotAttributes.getRankdirParsed(graph));

		validGraphRankdir = "BT";
		DotAttributes.setRankdir(graph, validGraphRankdir);
		assertEquals(validGraphRankdir, DotAttributes.getRankdir(graph));
		assertEquals(Rankdir.BT, DotAttributes.getRankdirParsed(graph));

		// set valid parsed values
		Rankdir validGraphRankdirParsed = Rankdir.LR;
		DotAttributes.setRankdirParsed(graph, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(graph));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(graph));

		validGraphRankdirParsed = Rankdir.RL;
		DotAttributes.setRankdirParsed(graph, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(graph));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(graph));

		validGraphRankdirParsed = Rankdir.TB;
		DotAttributes.setRankdirParsed(graph, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(graph));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(graph));

		validGraphRankdirParsed = Rankdir.BT;
		DotAttributes.setRankdirParsed(graph, validGraphRankdirParsed);
		assertEquals(validGraphRankdirParsed.toString(),
				DotAttributes.getRankdir(graph));
		assertEquals(validGraphRankdirParsed,
				DotAttributes.getRankdirParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setRankdir(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'rankdir' to 'foo'. The value 'foo' is not a syntactically correct rankdir: Value has to be one of 'TB', 'LR', 'BT', 'RL'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_splines() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getSplinesRaw(graph));
		assertNull(DotAttributes.getSplines(graph));
		assertNull(DotAttributes.getSplinesParsed(graph));

		// set valid string values
		String validGraphSplines = "compound";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.COMPOUND, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "curved";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.CURVED, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "false";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.FALSE, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "line";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.LINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "none";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.NONE, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "spline";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.SPLINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "polyline";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.POLYLINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "ortho";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.ORTHO, DotAttributes.getSplinesParsed(graph));

		validGraphSplines = "true";
		DotAttributes.setSplines(graph, validGraphSplines);
		assertEquals(validGraphSplines, DotAttributes.getSplines(graph));
		assertEquals(Splines.TRUE, DotAttributes.getSplinesParsed(graph));

		// set valid parsed values
		Splines validGraphSplinesParsed = Splines.COMPOUND;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.COMPOUND, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.CURVED;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.CURVED, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.EMPTY;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.EMPTY, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.FALSE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.FALSE, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.LINE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.LINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.NONE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.NONE, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.ORTHO;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.ORTHO, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.POLYLINE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.POLYLINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.SPLINE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.SPLINE, DotAttributes.getSplinesParsed(graph));

		validGraphSplinesParsed = Splines.TRUE;
		DotAttributes.setSplinesParsed(graph, validGraphSplinesParsed);
		assertEquals(validGraphSplinesParsed.toString(),
				DotAttributes.getSplines(graph));
		assertEquals(Splines.TRUE, DotAttributes.getSplinesParsed(graph));

		// set invalid string values
		try {
			DotAttributes.setSplines(graph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'splines' to 'foo'. The value 'foo' is not a syntactically correct splines: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value. Value has to be one of 'compound', 'curved', '', 'false', 'line', 'none', 'ortho', 'polyline', 'spline', 'true'.",
					e.getMessage());
		}
	}

	@Test
	public void graph_style() {
		Graph graph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getStyleRaw(graph));
		assertNull(DotAttributes.getStyle(graph));
		assertNull(DotAttributes.getStyleParsed(graph));

		// set valid string values
		String[] validGraphStyleItems = { "filled", "radial" };

		for (String validGraphStyleItem : validGraphStyleItems) {
			DotAttributes.setStyle(graph, validGraphStyleItem);
			assertEquals(validGraphStyleItem, DotAttributes.getStyle(graph));

			Style styleParsed = StyleFactory.eINSTANCE.createStyle();
			StyleItem styleItem = StyleFactory.eINSTANCE.createStyleItem();
			styleItem.setName(validGraphStyleItem);
			styleParsed.getStyleItems().add(styleItem);
			assertTrue(EcoreUtil.equals(styleParsed,
					DotAttributes.getStyleParsed(graph)));
		}

		// set valid parsed values
		Style styleParsed = StyleFactory.eINSTANCE.createStyle();
		StyleItem styleItem1 = StyleFactory.eINSTANCE.createStyleItem();
		styleItem1.setName("filled");

		styleParsed.getStyleItems().add(styleItem1);
		DotAttributes.setStyleParsed(graph, styleParsed);
		assertEquals("filled", DotAttributes.getStyle(graph));

		// set syntactically invalid values
		try {
			DotAttributes.setStyle(graph, "filled, ");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'style' to 'filled, '. The value 'filled, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME.",
					e.getMessage());
		}

		// TODO: set syntactically correct, but semantically invalid values
	}

	@Test
	public void graph_type() {
		// test directed graph
		Graph graph = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.DIGRAPH).build();
		assertEquals(GraphType.DIGRAPH, DotAttributes._getType(graph));

		// test undirected graph
		graph = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.GRAPH).build();
		assertEquals(GraphType.GRAPH, DotAttributes._getType(graph));
	}

	@Test
	public void node_color() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorRaw(node));
		assertNull(DotAttributes.getColor(node));
		assertNull(DotAttributes.getColorParsed(node));

		// set valid string values - rgb format
		DotAttributes.setColor(node, "#ffffff");
		assertEquals("#ffffff", DotAttributes.getColor(node));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(node)));

		// set valid string values - rgba format
		DotAttributes.setColor(node, "#ffffff00");
		assertEquals("#ffffff00", DotAttributes.getColor(node));
		rgbColor.setA("00");
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(node)));

		// set valid string values - hsv format
		DotAttributes.setColor(node, "0.000, 0.000, 1.000");
		assertEquals("0.000, 0.000, 1.000", DotAttributes.getColor(node));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.000");
		hsvColor.setS("0.000");
		hsvColor.setV("1.000");
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(node)));

		// set valid string values - string format
		DotAttributes.setColor(node, "/svg/white");
		assertEquals("/svg/white", DotAttributes.getColor(node));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("svg");
		stringColor.setName("white");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(node)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("ff");
		rgbColor.setG("ff");
		rgbColor.setB("ff");
		DotAttributes.setColorParsed(node, rgbColor);
		assertEquals("#ffffff", DotAttributes.getColor(node));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(node)));

		// set valid parsed values - rgba format
		rgbColor.setA("00");
		DotAttributes.setColorParsed(node, rgbColor);
		assertEquals("#ffffff00", DotAttributes.getColor(node));
		assertTrue(
				EcoreUtil.equals(rgbColor, DotAttributes.getColorParsed(node)));

		// set valid parsed values - hsv format
		DotAttributes.setColorParsed(node, hsvColor);
		assertEquals("0.000 0.000 1.000", DotAttributes.getColor(node));
		assertTrue(
				EcoreUtil.equals(hsvColor, DotAttributes.getColorParsed(node)));

		// set valid parsed values - string format
		DotAttributes.setColorParsed(node, stringColor);
		assertEquals("/svg/white", DotAttributes.getColor(node));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getColorParsed(node)));

		// set invalid string values
		try {
			DotAttributes.setColor(node, "/foo/antiquewhite1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'color' to '/foo/antiquewhite1'. The color value '/foo/antiquewhite1' is not semantically correct: 'foo' is not a valid color scheme.",
					e.getMessage());
		}

		try {
			DotAttributes.setColor(node, "/svg/antiquewhite1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'color' to '/svg/antiquewhite1'. The color value '/svg/antiquewhite1' is not semantically correct: The 'antiquewhite1' color is not valid within the 'svg' color scheme.",
					e.getMessage());
		}
	}

	@Test
	public void node_colorscheme() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getColorschemeRaw(node));
		assertNull(DotAttributes.getColorscheme(node));

		// set valid string values
		final String validColorScheme = "accent3";
		DotAttributes.setColorscheme(node, validColorScheme);
		assertEquals(validColorScheme, DotAttributes.getColorscheme(node));

		try {
			DotAttributes.setColorscheme(node, "1");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'colorscheme' to '1'. The string value '1' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'.",
					e.getMessage());
		}
	}

	@Test
	public void node_distortion() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getDistortionRaw(node));
		assertNull(DotAttributes.getDistortion(node));
		assertNull(DotAttributes.getDistortionParsed(node));

		// set valid string values
		String validNodeDistortion = "5";
		DotAttributes.setDistortion(node, validNodeDistortion);
		assertEquals(validNodeDistortion, DotAttributes.getDistortion(node));
		assertEquals(5.0, DotAttributes.getDistortionParsed(node).doubleValue(),
				0.0);

		// set the minimum valid value
		validNodeDistortion = "-100.0";
		DotAttributes.setDistortion(node, validNodeDistortion);
		assertEquals(validNodeDistortion, DotAttributes.getDistortion(node));
		assertEquals(-100.0,
				DotAttributes.getDistortionParsed(node).doubleValue(), 0.0);

		// set valid parsed values
		Double validNodeDistortionParsed = 10.0;
		DotAttributes.setDistortionParsed(node, validNodeDistortionParsed);
		assertEquals("10.0", DotAttributes.getDistortion(node));
		assertEquals(validNodeDistortionParsed,
				DotAttributes.getDistortionParsed(node));

		validNodeDistortionParsed = 9.9;
		DotAttributes.setDistortionParsed(node, validNodeDistortionParsed);
		assertEquals("9.9", DotAttributes.getDistortion(node));
		assertEquals(validNodeDistortionParsed,
				DotAttributes.getDistortionParsed(node));

		// set syntactically invalid values
		try {
			DotAttributes.setDistortion(node, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'distortion' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setDistortion(node, "-100.01");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'distortion' to '-100.01'. The double value '-100.01' is not semantically correct: Value may not be smaller than -100.0.",
					e.getMessage());
		}
	}

	@Test
	public void node_fillcolor() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFillcolorRaw(node));
		assertNull(DotAttributes.getFillcolor(node));
		assertNull(DotAttributes.getFillcolorParsed(node));

		// set valid string values - rgb format
		DotAttributes.setFillcolor(node, "#00ff00");
		assertEquals("#00ff00", DotAttributes.getFillcolor(node));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid string values - rgba format
		DotAttributes.setFillcolor(node, "#00ff00ff");
		assertEquals("#00ff00ff", DotAttributes.getFillcolor(node));
		rgbColor.setA("ff");
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid string values - hsv format
		DotAttributes.setFillcolor(node, "0.3 .8 .7");
		assertEquals("0.3 .8 .7", DotAttributes.getFillcolor(node));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.3");
		hsvColor.setS(".8");
		hsvColor.setV(".7");
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid string values - string format
		DotAttributes.setFillcolor(node, "/bugn9/7");
		assertEquals("/bugn9/7", DotAttributes.getFillcolor(node));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("bugn9");
		stringColor.setName("7");
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		DotAttributes.setFillcolorParsed(node, createColorList(rgbColor));
		assertEquals("#00ff00", DotAttributes.getFillcolor(node));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid parsed values - rgba format
		rgbColor.setA("ff");
		DotAttributes.setFillcolorParsed(node, createColorList(rgbColor));
		assertEquals("#00ff00ff", DotAttributes.getFillcolor(node));
		assertTrue(EcoreUtil.equals(createColorList(rgbColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid parsed values - hsv format
		DotAttributes.setFillcolorParsed(node, createColorList(hsvColor));
		assertEquals("0.3 .8 .7", DotAttributes.getFillcolor(node));
		assertTrue(EcoreUtil.equals(createColorList(hsvColor),
				DotAttributes.getFillcolorParsed(node)));

		// set valid parsed values - string format
		DotAttributes.setFillcolorParsed(node, createColorList(stringColor));
		assertEquals("/bugn9/7", DotAttributes.getFillcolor(node));
		assertTrue(EcoreUtil.equals(createColorList(stringColor),
				DotAttributes.getFillcolorParsed(node)));

		// set invalid string values
		try {
			DotAttributes.setFillcolor(node, "//");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fillcolor' to '//'. The value '//' is not a syntactically correct colorList: No viable alternative at input '<EOF>'.",
					e.getMessage());
		}
	}

	@Test
	public void node_fixedsize() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFixedsizeRaw(node));
		assertNull(DotAttributes.getFixedsize(node));
		assertNull(DotAttributes.getFixedsizeParsed(node));

		// set valid string values
		String validNodeFixedSize = "true";
		DotAttributes.setFixedsize(node, validNodeFixedSize);
		assertEquals(validNodeFixedSize, DotAttributes.getFixedsize(node));

		validNodeFixedSize = "false";
		DotAttributes.setFixedsize(node, validNodeFixedSize);
		assertEquals(validNodeFixedSize, DotAttributes.getFixedsize(node));

		// set valid parsed values
		boolean validNodeFixedsizeParsed = true;
		DotAttributes.setFixedsizeParsed(node, validNodeFixedsizeParsed);
		assertEquals(validNodeFixedsizeParsed,
				DotAttributes.getFixedsizeParsed(node));

		validNodeFixedsizeParsed = false;
		DotAttributes.setFixedsizeParsed(node, validNodeFixedsizeParsed);
		assertEquals(validNodeFixedsizeParsed,
				DotAttributes.getFixedsizeParsed(node));

		// set invalid string values
		try {
			DotAttributes.setFixedsize(node, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fixedsize' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value.",
					e.getMessage());
		}
	}

	@Test
	public void node_fontcolor() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getFontcolorRaw(node));
		assertNull(DotAttributes.getFontcolor(node));
		assertNull(DotAttributes.getFontcolorParsed(node));

		// set valid string values - rgb format
		DotAttributes.setFontcolor(node, "#00ff00");
		assertEquals("#00ff00", DotAttributes.getFontcolor(node));
		RGBColor rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid string values - rgba format
		DotAttributes.setFontcolor(node, "#00ff00ff");
		assertEquals("#00ff00ff", DotAttributes.getFontcolor(node));
		rgbColor.setA("ff");
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid string values - hsv format
		DotAttributes.setFontcolor(node, "0.3, .8, .7");
		assertEquals("0.3, .8, .7", DotAttributes.getFontcolor(node));
		HSVColor hsvColor = ColorFactory.eINSTANCE.createHSVColor();
		hsvColor.setH("0.3");
		hsvColor.setS(".8");
		hsvColor.setV(".7");
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid string values - string format
		DotAttributes.setFontcolor(node, "/brbg11/10");
		assertEquals("/brbg11/10", DotAttributes.getFontcolor(node));
		StringColor stringColor = ColorFactory.eINSTANCE.createStringColor();
		stringColor.setScheme("brbg11");
		stringColor.setName("10");
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid parsed values - rgb format
		rgbColor = ColorFactory.eINSTANCE.createRGBColor();
		rgbColor.setR("00");
		rgbColor.setG("ff");
		rgbColor.setB("00");
		DotAttributes.setFontcolorParsed(node, rgbColor);
		assertEquals("#00ff00", DotAttributes.getFontcolor(node));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid parsed values - rgba format
		rgbColor.setA("ff");
		DotAttributes.setFontcolorParsed(node, rgbColor);
		assertEquals("#00ff00ff", DotAttributes.getFontcolor(node));
		assertTrue(EcoreUtil.equals(rgbColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid parsed values - hsv format
		DotAttributes.setFontcolorParsed(node, hsvColor);
		assertEquals("0.3 .8 .7", DotAttributes.getFontcolor(node));
		assertTrue(EcoreUtil.equals(hsvColor,
				DotAttributes.getFontcolorParsed(node)));

		// set valid parsed values - string format
		DotAttributes.setFontcolorParsed(node, stringColor);
		assertEquals("/brbg11/10", DotAttributes.getFontcolor(node));
		assertTrue(EcoreUtil.equals(stringColor,
				DotAttributes.getFontcolorParsed(node)));

		// set invalid string values
		try {
			DotAttributes.setFontcolor(node, "///");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'fontcolor' to '///'. The value '///' is not a syntactically correct color: No viable alternative at input '/'.",
					e.getMessage());
		}
	}

	@Test
	public void node_height() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getHeightRaw(node));
		assertNull(DotAttributes.getHeight(node));
		assertNull(DotAttributes.getHeightParsed(node));

		// set valid string values
		String validNodeHeight = "0.56";
		DotAttributes.setHeight(node, validNodeHeight);
		assertEquals(validNodeHeight, DotAttributes.getHeight(node));

		// set the minimum valid value
		validNodeHeight = "0.02";
		DotAttributes.setHeight(node, validNodeHeight);
		assertEquals(validNodeHeight, DotAttributes.getHeight(node));

		// set valid parsed values
		Double validNodeHeightParsed = 0.1;
		DotAttributes.setHeightParsed(node, validNodeHeightParsed);
		assertEquals(validNodeHeightParsed,
				DotAttributes.getHeightParsed(node));

		validNodeHeightParsed = 9.9;
		DotAttributes.setHeightParsed(node, validNodeHeightParsed);
		assertEquals(validNodeHeightParsed,
				DotAttributes.getHeightParsed(node));

		// set syntactically invalid values
		try {
			DotAttributes.setHeight(node, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setHeight(node, "0.01");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '0.01'. The double value '0.01' is not semantically correct: Value may not be smaller than 0.02.",
					e.getMessage());
		}
	}

	@Test
	public void node_id() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getIdRaw(node));
		assertNull(DotAttributes.getId(node));

		// set valid string values
		final String validNodeId = "nodeId";
		DotAttributes.setId(node, validNodeId);
		assertEquals(validNodeId, DotAttributes.getId(node));

		// TODO: add test cases for setting invalid node id (e.g. a not unique
		// id)
	}

	@Test
	public void node_label() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getLabelRaw(node));
		assertNull(DotAttributes.getLabel(node));

		// set valid string values
		final String validNodeLabel = "nodeLabel";
		DotAttributes.setLabel(node, validNodeLabel);
		assertEquals(validNodeLabel, DotAttributes.getLabel(node));
	}

	@Test
	public void node_name() {
		Node node = new Node.Builder().buildNode();
		assertNull(DotAttributes._getName(node));
		assertNull(DotAttributes._getNameRaw(node));

		DotAttributes._setName(node, "TestNode");
		assertEquals("TestNode", DotAttributes._getName(node));
		assertEquals(ID.fromString("TestNode"),
				DotAttributes._getNameRaw(node));
	}

	@Test
	public void node_pos() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getPosRaw(node));
		assertNull(DotAttributes.getPos(node));
		assertNull(DotAttributes.getPosParsed(node));

		// set valid string values
		DotAttributes.setPos(node, "47, 11");
		DotAttributes.setPos(node, "34.5, 45.3!");
		DotAttributes.setPos(node, "-221.31,936.82");

		// set valid parsed values
		Point pos = PointFactory.eINSTANCE.createPoint();
		pos.setX(33);
		pos.setY(54.6);
		pos.setInputOnly(true);
		DotAttributes.setPosParsed(node, pos);
		assertEquals("33.0, 54.6!", DotAttributes.getPos(node));
		assertTrue(EcoreUtil.equals(DotAttributes.getPosParsed(node), pos));

		// set invalid string values
		try {
			DotAttributes.setPos(node, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'pos' to '47x, 11'. The value '47x, 11' is not a syntactically correct point: No viable alternative at character 'x'.",
					e.getMessage());
		}
	}

	@Test
	public void node_shape() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getShapeRaw(node));
		assertNull(DotAttributes.getShape(node));
		assertNull(DotAttributes.getShapeParsed(node));

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
			DotAttributes.setShape(node, validPolygonBasedNodeShape);
			assertEquals(validPolygonBasedNodeShape,
					DotAttributes.getShape(node));

			Shape shapeParsed = ShapeFactory.eINSTANCE.createShape();
			PolygonBasedShape polygonBasedShape = ShapeFactory.eINSTANCE
					.createPolygonBasedShape();
			polygonBasedShape.setShape(
					PolygonBasedNodeShape.get(validPolygonBasedNodeShape));
			shapeParsed.setShape(polygonBasedShape);
			assertTrue(EcoreUtil.equals(shapeParsed,
					DotAttributes.getShapeParsed(node)));
		}

		// set valid (record based) string values
		String[] validRecordBasedNodeShapes = { "record", "Mrecord" };

		for (String validRecordBasedNodeShape : validRecordBasedNodeShapes) {
			DotAttributes.setShape(node, validRecordBasedNodeShape);
			assertEquals(validRecordBasedNodeShape,
					DotAttributes.getShape(node));

			Shape shapeParsed = ShapeFactory.eINSTANCE.createShape();
			RecordBasedShape recordBasedShape = ShapeFactory.eINSTANCE
					.createRecordBasedShape();
			recordBasedShape.setShape(
					RecordBasedNodeShape.get(validRecordBasedNodeShape));
			shapeParsed.setShape(recordBasedShape);
			assertTrue(EcoreUtil.equals(shapeParsed,
					DotAttributes.getShapeParsed(node)));
		}

		// set valid parsed values
		Shape validNodeShapeParsed = ShapeFactory.eINSTANCE.createShape();
		PolygonBasedShape polygonBasedShape = ShapeFactory.eINSTANCE
				.createPolygonBasedShape();
		polygonBasedShape.setShape(PolygonBasedNodeShape.BOX);
		validNodeShapeParsed.setShape(polygonBasedShape);
		DotAttributes.setShapeParsed(node, validNodeShapeParsed);
		assertEquals("box", DotAttributes.getShape(node));

		// set invalid string values
		try {
			DotAttributes.setShape(node, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'shape' to 'foo'. The value 'foo' is not a syntactically correct shape: No viable alternative at input 'foo'.",
					e.getMessage());
		}
	}

	@Test
	public void node_sides() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getSidesRaw(node));
		assertNull(DotAttributes.getSides(node));
		assertNull(DotAttributes.getSidesParsed(node));

		// set valid string values
		String validNodeSides = "5";
		DotAttributes.setSides(node, validNodeSides);
		assertEquals(validNodeSides, DotAttributes.getSides(node));
		assertEquals(5, DotAttributes.getSidesParsed(node).intValue());

		// set the minimum valid value
		validNodeSides = "0";
		DotAttributes.setSides(node, validNodeSides);
		assertEquals(validNodeSides, DotAttributes.getSides(node));
		assertEquals(0, DotAttributes.getSidesParsed(node).intValue());

		// set valid parsed values
		Integer validNodeSidesParsed = 3;
		DotAttributes.setSidesParsed(node, validNodeSidesParsed);
		assertEquals("3", DotAttributes.getSides(node));
		assertEquals(validNodeSidesParsed, DotAttributes.getSidesParsed(node));

		validNodeSidesParsed = 42;
		DotAttributes.setSidesParsed(node, validNodeSidesParsed);
		assertEquals("42", DotAttributes.getSides(node));
		assertEquals(validNodeSidesParsed, DotAttributes.getSidesParsed(node));

		// set syntactically invalid values
		try {
			DotAttributes.setSides(node, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'sides' to '42x'. The value '42x' is not a syntactically correct int: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setSides(node, "-1");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'sides' to '-1'. The int value '-1' is not semantically correct: Value may not be smaller than 0.",
					e.getMessage());
		}
	}

	@Test
	public void node_skew() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getSkewRaw(node));
		assertNull(DotAttributes.getSkew(node));
		assertNull(DotAttributes.getSkewParsed(node));

		// set valid string values
		String validNodeSkew = "5";
		DotAttributes.setSkew(node, validNodeSkew);
		assertEquals(validNodeSkew, DotAttributes.getSkew(node));
		assertEquals(5.0, DotAttributes.getSkewParsed(node).doubleValue(), 0.0);

		// set the minimum valid value
		validNodeSkew = "-100.0";
		DotAttributes.setSkew(node, validNodeSkew);
		assertEquals(validNodeSkew, DotAttributes.getSkew(node));
		assertEquals(-100.0, DotAttributes.getSkewParsed(node).doubleValue(),
				0.0);

		// set valid parsed values
		Double validNodeSkewParsed = 10.0;
		DotAttributes.setSkewParsed(node, validNodeSkewParsed);
		assertEquals("10.0", DotAttributes.getSkew(node));
		assertEquals(validNodeSkewParsed, DotAttributes.getSkewParsed(node));

		validNodeSkewParsed = 9.9;
		DotAttributes.setSkewParsed(node, validNodeSkewParsed);
		assertEquals("9.9", DotAttributes.getSkew(node));
		assertEquals(validNodeSkewParsed, DotAttributes.getSkewParsed(node));

		// set syntactically invalid values
		try {
			DotAttributes.setSkew(node, "42x");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'skew' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setSkew(node, "-100.01");
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

		// test getters if no explicit value is set
		assertNull(DotAttributes.getStyleRaw(node));
		assertNull(DotAttributes.getStyle(node));
		assertNull(DotAttributes.getStyleParsed(node));

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
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getWidthRaw(node));
		assertNull(DotAttributes.getWidth(node));
		assertNull(DotAttributes.getWidthParsed(node));

		// set valid string values
		String validNodeWidth = "0.56";
		DotAttributes.setWidth(node, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(node));

		validNodeWidth = "76";
		DotAttributes.setWidth(node, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(node));

		// set the minimum valid value
		validNodeWidth = "0.01";
		DotAttributes.setWidth(node, validNodeWidth);
		assertEquals(validNodeWidth, DotAttributes.getWidth(node));

		// set valid parsed values
		Double validNodeWidthParsed = 0.1;
		DotAttributes.setWidthParsed(node, validNodeWidthParsed);
		assertEquals(validNodeWidthParsed, DotAttributes.getWidthParsed(node));

		validNodeWidthParsed = 9.9;
		DotAttributes.setWidthParsed(node, validNodeWidthParsed);
		assertEquals(validNodeWidthParsed, DotAttributes.getWidthParsed(node));

		// set syntactically invalid values
		try {
			DotAttributes.setWidth(node, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\".",
					e.getMessage());
		}

		// set syntactically correct, but semantically invalid values
		try {
			DotAttributes.setWidth(node, "0.009");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '0.009'. The double value '0.009' is not semantically correct: Value may not be smaller than 0.01.",
					e.getMessage());
		}
	}

	@Test
	public void node_xlabel() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getXlabelRaw(node));
		assertNull(DotAttributes.getXlabel(node));

		// set valid string values
		final String validNodeXLabel = "nodeXLabel";
		DotAttributes.setXlabel(node, validNodeXLabel);
		assertEquals(validNodeXLabel, DotAttributes.getXlabel(node));
	}

	@Test
	public void node_xlp() {
		Node node = new Node.Builder().buildNode();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getXlpRaw(node));
		assertNull(DotAttributes.getXlp(node));
		assertNull(DotAttributes.getXlpParsed(node));

		// set valid string values
		DotAttributes.setXlp(node, "47, 11");
		DotAttributes.setXlp(node, "34.5, 45.3!");

		// set valid parsed values
		Point xlp = PointFactory.eINSTANCE.createPoint();
		xlp.setX(33);
		xlp.setY(54.6);
		xlp.setInputOnly(true);
		DotAttributes.setXlpParsed(node, xlp);
		assertEquals("33.0, 54.6!", DotAttributes.getXlp(node));
		assertTrue(EcoreUtil.equals(DotAttributes.getXlpParsed(node), xlp));

		// set invalid string values
		try {
			DotAttributes.setXlp(node, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'xlp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'.",
					e.getMessage());
		}
	}

	@Test
	public void subgraph_rank() {
		Graph subgraph = new Graph.Builder().build();

		// test getters if no explicit value is set
		assertNull(DotAttributes.getRank(subgraph));

		// set valid string values
		DotAttributes.setRank(subgraph, "same");
		assertEquals("same", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SAME, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRank(subgraph, "min");
		assertEquals("min", DotAttributes.getRank(subgraph));
		assertEquals(RankType.MIN, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRank(subgraph, "source");
		assertEquals("source", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SOURCE, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRank(subgraph, "max");
		assertEquals("max", DotAttributes.getRank(subgraph));
		assertEquals(RankType.MAX, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRank(subgraph, "sink");
		assertEquals("sink", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SINK, DotAttributes.getRankParsed(subgraph));

		// set valid parsed values
		DotAttributes.setRankParsed(subgraph, RankType.SAME);
		assertEquals("same", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SAME, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRankParsed(subgraph, RankType.MIN);
		assertEquals("min", DotAttributes.getRank(subgraph));
		assertEquals(RankType.MIN, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRankParsed(subgraph, RankType.SOURCE);
		assertEquals("source", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SOURCE, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRankParsed(subgraph, RankType.MAX);
		assertEquals("max", DotAttributes.getRank(subgraph));
		assertEquals(RankType.MAX, DotAttributes.getRankParsed(subgraph));

		DotAttributes.setRankParsed(subgraph, RankType.SINK);
		assertEquals("sink", DotAttributes.getRank(subgraph));
		assertEquals(RankType.SINK, DotAttributes.getRankParsed(subgraph));

		// set invalid string value
		try {
			DotAttributes.setRank(subgraph, "foo");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set graph attribute 'rank' to 'foo'. The value 'foo' is not a syntactically correct rankType: Value has to be one of 'same', 'min', 'source', 'max', 'sink'.",
					e.getMessage());
		}
	}

	private ColorList createColorList(Color color) {
		WC weightedColor = ColorlistFactory.eINSTANCE.createWC();
		weightedColor.setColor(color);

		ColorList colorList = ColorlistFactory.eINSTANCE.createColorList();
		colorList.getColorValues().add(weightedColor);

		return colorList;
	}
}
