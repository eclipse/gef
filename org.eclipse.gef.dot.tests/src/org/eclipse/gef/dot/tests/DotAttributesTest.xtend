/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)    - initial API and implementation
 *     Tamas Miklossy  (itemis AG)    - implement additional test cases (bug #461506)
 *     Zoey Gerrit Prigge (itemis AG) - implement additional test cases (bugs #461506, #547809, #559031)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.util.function.Consumer
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypeFactory
import org.eclipse.gef.dot.internal.language.arrowtype.DeprecatedShape
import org.eclipse.gef.dot.internal.language.arrowtype.PrimitiveShape
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode
import org.eclipse.gef.dot.internal.language.color.Color
import org.eclipse.gef.dot.internal.language.color.ColorFactory
import org.eclipse.gef.dot.internal.language.colorlist.ColorlistFactory
import org.eclipse.gef.dot.internal.language.dir.DirType
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.escstring.EscstringFactory
import org.eclipse.gef.dot.internal.language.escstring.Justification
import org.eclipse.gef.dot.internal.language.fontname.FontnameFactory
import org.eclipse.gef.dot.internal.language.fontname.PostScriptFontAlias
import org.eclipse.gef.dot.internal.language.layout.Layout
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir
import org.eclipse.gef.dot.internal.language.point.PointFactory
import org.eclipse.gef.dot.internal.language.portpos.PortposFactory
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir
import org.eclipse.gef.dot.internal.language.ranktype.RankType
import org.eclipse.gef.dot.internal.language.rect.RectFactory
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape
import org.eclipse.gef.dot.internal.language.shape.RecordBasedNodeShape
import org.eclipse.gef.dot.internal.language.shape.ShapeFactory
import org.eclipse.gef.dot.internal.language.splines.Splines
import org.eclipse.gef.dot.internal.language.splinetype.SplinetypeFactory
import org.eclipse.gef.dot.internal.language.style.StyleFactory
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.language.terminals.ID.Type
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.DotAttributes.*
import static extension org.junit.Assert.assertEquals
import static extension org.junit.Assert.assertFalse
import static extension org.junit.Assert.assertNotNull
import static extension org.junit.Assert.assertNull
import static extension org.junit.Assert.assertTrue
import static extension org.junit.Assert.fail

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotAttributesTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	val extension ArrowtypeFactory = ArrowtypeFactory.eINSTANCE
	val extension ColorFactory = ColorFactory.eINSTANCE
	val extension ColorlistFactory = ColorlistFactory.eINSTANCE
	val extension EscstringFactory = EscstringFactory.eINSTANCE
	val extension FontnameFactory = FontnameFactory.eINSTANCE
	val extension PointFactory = PointFactory.eINSTANCE
	val extension PortposFactory = PortposFactory.eINSTANCE
	val extension RectFactory = RectFactory.eINSTANCE
	val extension ShapeFactory = ShapeFactory.eINSTANCE
	val extension SplinetypeFactory = SplinetypeFactory.eINSTANCE
	val extension StyleFactory = StyleFactory.eINSTANCE

	@Test def edge_arrowhead() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		arrowheadRaw.assertNull
		arrowhead.assertNull
		arrowheadParsed.assertNull
		
		// set valid string values
		arrowhead = "olbox"
		"olbox".assertEquals(arrowhead)
		var arrowHead = createArrowType
		val olBox = createArrowShape => [open=true side="l" shape=PrimitiveShape.BOX]
		arrowHead.arrowShapes += olBox
		
		EcoreUtil.equals(arrowHead, arrowheadParsed).assertTrue
		
		// set empty string value (this is also valid, however, currently not documented)
		arrowhead = ""
		"".assertEquals(arrowhead)
		EcoreUtil.equals(createArrowType, arrowheadParsed).assertTrue
		
		// set valid parsed values
		var arrowHeadParsed = createArrowType
		arrowHeadParsed.setResource
		val rdiamond = createArrowShape => [open=false side="r" shape=PrimitiveShape.DIAMOND]
		arrowHeadParsed.arrowShapes += rdiamond
		arrowheadParsed = arrowHeadParsed
		"rdiamond".assertEquals(arrowhead)
		
		// set valid values - multiple arrow shapes
		arrowHeadParsed = createArrowType => [arrowShapes += #[olBox, rdiamond]]
		arrowHeadParsed.setResource
		arrowheadParsed = arrowHeadParsed
		"olboxrdiamond".assertEquals(arrowhead)
		
		// set deprecated (but valid) values
		arrowhead = "ediamond"
		"ediamond".assertEquals(arrowhead)
		arrowHead = createArrowType => [
			arrowShapes += createDeprecatedArrowShape => [shape = DeprecatedShape.EDIAMOND]
		]		
		EcoreUtil.equals(arrowHead, arrowheadParsed).assertTrue
		
		// set invalid string values
		invalidValue([arrowhead = "olox"],
			"Cannot set edge attribute 'arrowhead' to 'olox'. The value 'olox' is not a syntactically correct arrowType: No viable alternative at input 'o'. No viable alternative at character 'x'."
		)
	}

	@Test def edge_arrowsize() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		arrowsizeRaw.assertNull
		arrowsize.assertNull
		arrowsizeParsed.assertNull
		
		// set valid string values
		val validEdgeArrowSize = "0.5"
		arrowsize = validEdgeArrowSize
		validEdgeArrowSize.assertEquals(arrowsize)
		
		// set valid parsed values
		val validEdgeArrowSizeParsed = new Double(0.0)
		arrowsizeParsed = validEdgeArrowSizeParsed
		validEdgeArrowSizeParsed.assertEquals(arrowsizeParsed)
		
		// set syntactically invalid values
		invalidValue([arrowsize = "0,5"],
			"Cannot set edge attribute 'arrowsize' to '0,5'. The value '0,5' is not a syntactically correct double: For input string: \"0,5\"."
		)
		invalidValue([arrowsize = "foo"],
			"Cannot set edge attribute 'arrowsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([arrowsize = "-0.5"],
			"Cannot set edge attribute 'arrowsize' to '-0.5'. The double value '-0.5' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def edge_arrowtail() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		arrowtailRaw.assertNull
		arrowtail.assertNull
		arrowtailParsed.assertNull
		
		// set valid string values
		arrowtail = "olbox"
		"olbox".assertEquals(arrowtail)
		var arrowTail = createArrowType
		val olBox = createArrowShape => [open=true side = "l" shape = PrimitiveShape.BOX]
		arrowTail.arrowShapes += olBox
		EcoreUtil.equals(arrowTail, arrowtailParsed).assertTrue
		
		// set empty string value (this is also valid, however, currently not documented)
		arrowtail = ""
		"".assertEquals(arrowtail)
		EcoreUtil.equals(createArrowType, arrowtailParsed).assertTrue
		
		// set valid parsed values
		val rdiamond = createArrowShape => [open = false side = "r" shape = PrimitiveShape.DIAMOND]
		arrowTail = createArrowType => [
			arrowShapes += rdiamond
		]
		arrowTail.setResource
		arrowtailParsed = arrowTail
		"rdiamond".assertEquals(arrowtail)
		
		// set valid values - multiple arrow shapes
		arrowTail = createArrowType => [arrowShapes += #[olBox, rdiamond]]
		arrowTail.setResource
		arrowtailParsed = arrowTail
		"olboxrdiamond".assertEquals(arrowtail)
		
		// set deprecated (but valid) values
		arrowtail = "ediamond"
		"ediamond".assertEquals(arrowtail)
		val expected = createArrowType => [
			arrowShapes += createDeprecatedArrowShape => [
				shape = DeprecatedShape.EDIAMOND
			]
		]
		EcoreUtil.equals(expected, arrowtailParsed).assertTrue
		
		// set invalid string values
		invalidValue([arrowtail = "olox"],
			"Cannot set edge attribute 'arrowtail' to 'olox'. The value 'olox' is not a syntactically correct arrowType: No viable alternative at input 'o'. No viable alternative at character 'x'."
		)
	}

	@Test def edge_color() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		colorRaw.assertNull
		color.assertNull
		colorParsed.assertNull
		
		// set valid string values - rgb format
		color = "#ffffff"
		"#ffffff".assertEquals(color)
		
		var rgbColor = createRGBColor => [r="ff" g="ff" b="ff"]
		EcoreUtil.equals(rgbColor.createColorList, colorParsed).assertTrue
		
		// set valid string values - rgba format
		color = "#ffffff42"
		"#ffffff42".assertEquals(color)
		rgbColor.a = "42"
		EcoreUtil.equals(rgbColor.createColorList, colorParsed).assertTrue
		
		// set valid string values - hsv format
		color = "0.000 0.000 1.000"
		"0.000 0.000 1.000".assertEquals(color)
		val hsvColor = createHSVColor => [h="0.000" s="0.000" v="1.000"]
		EcoreUtil.equals(hsvColor.createColorList, colorParsed).assertTrue
		
		// set valid string values - string format
		color = "white"
		"white".assertEquals(color)
		val stringColor = createStringColor => [name="white"]
		EcoreUtil.equals(stringColor.createColorList, colorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [r="ab" g="cd" b="ef"]
		colorParsed = rgbColor.createColorList
		"#abcdef".assertEquals(color)
		EcoreUtil.equals(rgbColor.createColorList, colorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "00"
		colorParsed = rgbColor.createColorList
		"#abcdef00".assertEquals(color)
		EcoreUtil.equals(rgbColor.createColorList, colorParsed).assertTrue
		
		// set valid parsed values - hsv format
		colorParsed = hsvColor.createColorList
		"0.000 0.000 1.000".assertEquals(color)
		EcoreUtil.equals(hsvColor.createColorList, colorParsed).assertTrue
		
		// set valid parsed values - string format
		colorParsed = stringColor.createColorList
		"white".assertEquals(color)
		EcoreUtil.equals(stringColor.createColorList, colorParsed).assertTrue
		
		// set invalid string values
		invalidValue([color = "#foo"],
			"Cannot set edge attribute 'color' to '#foo'. The value '#foo' is not a syntactically correct colorList: No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def edge_colorscheme() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		colorschemeRaw.assertNull
		colorscheme.assertNull
		
		// set valid string values
		val validColorScheme = "x11"
		colorscheme = validColorScheme
		validColorScheme.assertEquals(colorscheme)
		
		// set invalid string values
		invalidValue([colorscheme = "#foo"],
			"Cannot set edge attribute 'colorscheme' to '#foo'. The string value '#foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'."
		)
	}

	@Test def edge_dir() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		dirRaw.assertNull
		dir.assertNull
		dirParsed.assertNull
		
		// set valid string values
		var validEdgeDir = "forward"
		dir = validEdgeDir
		validEdgeDir.assertEquals(dir)
		DirType.FORWARD.assertEquals(dirParsed)
		
		validEdgeDir = "back"
		dir = validEdgeDir
		validEdgeDir.assertEquals(dir)
		DirType.BACK.assertEquals(dirParsed)
		
		validEdgeDir = "both"
		dir = validEdgeDir
		validEdgeDir.assertEquals(dir)
		DirType.BOTH.assertEquals(dirParsed)
		
		validEdgeDir = "none"
		dir = validEdgeDir
		validEdgeDir.assertEquals(dir)
		DirType.NONE.assertEquals(dirParsed)
		
		validEdgeDir = ""
		dir = validEdgeDir
		validEdgeDir.assertEquals(dir)
		
		// set valid parsed values
		var validEdgeDirParsed = DirType.FORWARD
		dirParsed = validEdgeDirParsed
		validEdgeDirParsed.toString.assertEquals(dir)
		validEdgeDirParsed.assertEquals(dirParsed)
		
		validEdgeDirParsed = DirType.BACK
		dirParsed = validEdgeDirParsed
		validEdgeDirParsed.toString.assertEquals(dir)
		validEdgeDirParsed.assertEquals(dirParsed)
		
		validEdgeDirParsed = DirType.BOTH
		dirParsed = validEdgeDirParsed
		validEdgeDirParsed.toString.assertEquals(dir)
		validEdgeDirParsed.assertEquals(dirParsed)
		
		validEdgeDirParsed = DirType.NONE
		dirParsed = validEdgeDirParsed
		validEdgeDirParsed.toString.assertEquals(dir)
		validEdgeDirParsed.assertEquals(dirParsed)
		
		// set invalid string values
		invalidValue([dir = "foo"],
			"Cannot set edge attribute 'dir' to 'foo'. The value 'foo' is not a syntactically correct dirType: Value has to be one of 'forward', 'back', 'both', 'none'."
		)
	}

	@Test def edge_edgetooltip() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		edgetooltipRaw.assertNull
		edgetooltip.assertNull
		edgetooltipParsed.assertNull
		
		// set valid string values
		edgetooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(edgetooltip)
		val expected = createEscString => [
			lines += createJustifiedText => [text="line1"]
			lines += createJustifiedText => [text="line2"]
		]
		EcoreUtil.equals(expected, edgetooltipParsed).assertTrue
		
		// set valid parsed values
		val edgeTooltip = createEscString => [
			lines += createJustifiedText => [ text="a" justification=Justification.LEFT	]
			lines += createJustifiedText => [ text="b" justification=Justification.RIGHT ]
		]
		edgeTooltip.setResource
		edgetooltipParsed = edgeTooltip
		"a\\lb\\r".assertEquals(edgetooltip)
	}

	@Test def edge_fillcolor() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getter if no value explicit is set
		fillcolorRaw.assertNull
		fillcolor.assertNull
		fillcolorParsed.assertNull
		
		// set valid string values - rgb format
		fillcolor = "#000000"
		"#000000".assertEquals(fillcolor)
		var rgbColor = createRGBColor => [ r="00" g="00" b="00"]
		EcoreUtil.equals(rgbColor, fillcolorParsed).assertTrue
		
		// set valid string values - rgba format
		fillcolor = "#0000002a"
		"#0000002a".assertEquals(fillcolor)
		rgbColor.a = "2a"
		EcoreUtil.equals(rgbColor, fillcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fillcolor = "0.000 0.000 0.000"
		"0.000 0.000 0.000".assertEquals(fillcolor)
		val hsvColor = createHSVColor => [ h="0.000" s="0.000" v="0.000" ]
		EcoreUtil.equals(hsvColor, fillcolorParsed).assertTrue
		
		// set valid string values - string format
		fillcolor = "black"
		"black".assertEquals(fillcolor)
		val stringColor = createStringColor => [ name="black" ]
		EcoreUtil.equals(stringColor, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [ r="12" g="34" b="56"]
		rgbColor.setResource
		fillcolorParsed = rgbColor
		"#123456".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "78"
		fillcolorParsed = rgbColor
		"#12345678".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor, fillcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		fillcolorParsed = hsvColor
		"0.000 0.000 0.000".assertEquals(fillcolor)
		EcoreUtil.equals(hsvColor, fillcolorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		fillcolorParsed = stringColor
		"black".assertEquals(fillcolor)
		EcoreUtil.equals(stringColor, fillcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fillcolor = "#ff"],
			"Cannot set edge attribute 'fillcolor' to '#ff'. The value '#ff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT."
		)
	}

	@Test def edge_fontcolor() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		fontcolorRaw.assertNull
		fontcolor.assertNull
		fontcolorParsed.assertNull
		
		// set valid string values - rgb format
		fontcolor = "#ff0000"
		"#ff0000".assertEquals(fontcolor)
		var rgbColor = createRGBColor => [ r="ff" g="00" b="00" ]
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid string values - rgba format
		fontcolor = "#ff0000bb"
		"#ff0000bb".assertEquals(getFontcolor)
		rgbColor.a = "bb"
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fontcolor = "0.000 1.000 1.000"
		"0.000 1.000 1.000".assertEquals(fontcolor)
		val hsvColor = createHSVColor => [ h="0.000" s="1.000" v="1.000"]
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue
		
		// set valid string values - string format
		fontcolor = "red"
		"red".assertEquals(fontcolor)
		val stringColor = createStringColor => [ name="red"	]
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [ r="ff" g="00" b="00"	]
		rgbColor.setResource
		fontcolorParsed = rgbColor
		"#ff0000".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		// set valid parsed values - rgba format
		rgbColor.a = "bb"
		fontcolorParsed = rgbColor
		"#ff0000bb".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue

		// set valid parsed values - hsv format
		hsvColor.setResource
		fontcolorParsed = hsvColor
		"0.000 1.000 1.000".assertEquals(fontcolor)
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue

		// set valid parsed values - string format
		stringColor.setResource
		fontcolorParsed = stringColor
		"red".assertEquals(fontcolor)
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fontcolor = "#fffffffff"],
			"Cannot set edge attribute 'fontcolor' to '#fffffffff'. The value '#fffffffff' is not a syntactically correct color: Extraneous input 'f' expecting EOF."
		)
	}

	@Test def edge_fontname() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		fontnameRaw.assertNull
		fontname.assertNull
		fontnameParsed.assertNull
		
		val validPostScriptParsed = createPostScriptFontName => [ alias = PostScriptFontAlias.TIMES_ROMAN]
		val validPostScriptString = "Times-Roman"
		
		//String setter
		fontname = validPostScriptString
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPostScriptParsed.setResource
		fontnameParsed = validPostScriptParsed
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		
		val validPangoParsed = createPangoFontName => [ families += "Times"]
		val validPangoString = "Times,"
		
		//String setter
		fontname = validPangoString
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPangoParsed.setResource
		fontnameParsed = validPangoParsed
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
	}

	@Test def edge_fontsize() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		fontsizeRaw.assertNull
		fontsize.assertNull
		fontsizeParsed.assertNull
		
		// set valid string values
		val validFontsize = "22.5"
		fontsize = validFontsize
		validFontsize.assertEquals(fontsize)
		
		// set valid parsed values
		var validFontsizeParsed = new Double(5)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set valid parsed values
		validFontsizeParsed = new Double(1.0)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set syntactically invalid values
		invalidValue([fontsize = "2,5"],
			"Cannot set edge attribute 'fontsize' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([fontsize = "foo"],
			"Cannot set edge attribute 'fontsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([fontsize = "0.5"],
			"Cannot set edge attribute 'fontsize' to '0.5'. The double value '0.5' is not semantically correct: Value may not be smaller than 1.0."
		)
	}

	@Test def edge_headlabel() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		headlabelRaw.assertNull
		headlabel.assertNull
		
		// set valid string values
		val validEdgeHeadLabel = "simpleEdgeLabel"
		headlabel = validEdgeHeadLabel
		validEdgeHeadLabel.assertEquals(headlabel)
	}

	@Test def edge_headlp() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		headLpRaw.assertNull
		headLp.assertNull
		headLpParsed.assertNull
		
		// set valid string values
		var validEdgeHeadLp = "42,0.0"
		headLp = validEdgeHeadLp
		validEdgeHeadLp.assertEquals(headLp)
		
		validEdgeHeadLp = "0.0,0.0"
		headLp = validEdgeHeadLp
		validEdgeHeadLp.assertEquals(headLp)
		
		// set valid parsed values
		val validEdgeHeadLpParsed = createPoint => [ x=42 y=0.0 ]
		validEdgeHeadLpParsed.setResource
		headLpParsed = validEdgeHeadLpParsed
		EcoreUtil.equals(validEdgeHeadLpParsed, headLpParsed).assertTrue
		
		// set invalid string values
		invalidValue([headLp = "foo"],
			"Cannot set edge attribute 'head_lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def edge_headport() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		headportRaw.assertNull
		headport.assertNull
		headportParsed.assertNull
		
		// set valid string values
		headport ="w"
		"w".assertEquals(headport)
		val expected = createPortPos => [ port="w" ]
		EcoreUtil.equals(expected, headportParsed).assertTrue

		// set valid parsed values
		val headPort = createPortPos => [
			port = "nameOfThePort"
			compassPoint = "_"
		]
		headPort.setResource
		headportParsed = headPort
		"nameOfThePort:_".assertEquals(headport)
		
		// set invalid string values
		invalidValue([headport = "a:foo"],
			"Cannot set edge attribute 'headport' to 'a:foo'. The value 'a:foo' is not a syntactically correct portPos: Mismatched input 'foo' expecting RULE_COMPASS_POINT_POS."
		)
	}

	@Test def edge_headtooltip() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		headtooltipRaw.assertNull
		headtooltip.assertNull
		headtooltipParsed.assertNull
		
		// set valid string values
		headtooltip ="line1\\nline2"
		"line1\\nline2".assertEquals(headtooltip)
		val expected = createEscString => [
			lines += createJustifiedText => [ text="line1" ]
			lines += createJustifiedText => [ text="line2" ]
		]
		EcoreUtil.equals(expected, headtooltipParsed).assertTrue

		// set valid parsed values
		val headTooltip = createEscString => [
			lines += createJustifiedText => [ text="a" justification=Justification.LEFT ]
			lines += createJustifiedText => [ text="b" justification=Justification.RIGHT ]
		]
		headTooltip.setResource
		headtooltipParsed = headTooltip
		"a\\lb\\r".assertEquals(headtooltip)
	}

	@Test def edge_id() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		idRaw.assertNull
		id.assertNull
		
		// set valid string values
		val validEdgeId = "edgeId"
		id = validEdgeId
		validEdgeId.assertEquals(id)
		
		// TODO: add test cases for setting invalid edge id (e.g. a not unique id)
	}

	@Test def edge_label() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		labelRaw.assertNull
		label.assertNull
		
		// set valid string values
		val validEdgeLabel = "edgeLabel"
		label = validEdgeLabel
		validEdgeLabel.assertEquals(label)
	}

	@Test def edge_labelfontcolor() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		labelfontcolorRaw.assertNull
		labelfontcolor.assertNull
		labelfontcolorParsed.assertNull
		
		// set valid string values - rgb format
		labelfontcolor = "#40e0d0"
		"#40e0d0".assertEquals(labelfontcolor)
		var rgbColor = createRGBColor => [ r="40" g="e0" b="d0" ]
		EcoreUtil.equals(rgbColor, labelfontcolorParsed).assertTrue
		
		// set valid string values - rgba format
		labelfontcolor = "#40e0d0cc"
		"#40e0d0cc".assertEquals(labelfontcolor)
		rgbColor.a = "cc"
		EcoreUtil.equals(rgbColor, labelfontcolorParsed).assertTrue
		
		// set valid string values - hsv format
		labelfontcolor = "0.482 0.714 0.878"
		"0.482 0.714 0.878".assertEquals(labelfontcolor)
		val hsvColor = createHSVColor => [h ="0.482" s="0.714" v="0.878"]
		EcoreUtil.equals(hsvColor, labelfontcolorParsed).assertTrue
		
		// set valid string values - string format
		labelfontcolor = "turquoise"
		"turquoise".assertEquals(labelfontcolor)
		val stringColor = createStringColor => [ name="turquoise"]
		EcoreUtil.equals(stringColor, labelfontcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [ r="40" g="e0" b="d0" ]
		rgbColor.setResource
		labelfontcolorParsed = rgbColor
		"#40e0d0".assertEquals(labelfontcolor)
		EcoreUtil.equals(rgbColor, labelfontcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "cc"
		labelfontcolorParsed = rgbColor
		"#40e0d0cc".assertEquals(labelfontcolor)
		EcoreUtil.equals(rgbColor, labelfontcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		labelfontcolorParsed = hsvColor
		"0.482 0.714 0.878".assertEquals(labelfontcolor)
		EcoreUtil.equals(hsvColor, labelfontcolorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		labelfontcolorParsed = stringColor
		"turquoise".assertEquals(labelfontcolor)
		EcoreUtil.equals(stringColor, labelfontcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([labelfontcolor = "_"],
			"Cannot set edge attribute 'labelfontcolor' to '_'. The value '_' is not a syntactically correct color: No viable alternative at character '_'."
		)
	}

	@Test def edge_labelfontname() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		labelfontnameRaw.assertNull
		labelfontname.assertNull
		labelfontnameParsed.assertNull
		
		val validPostScriptParsed = createPostScriptFontName => [ alias = PostScriptFontAlias.TIMES_ROMAN]
		val validPostScriptString = "Times-Roman"
		
		//String setter
		labelfontname = validPostScriptString
		validPostScriptString.assertEquals(labelfontname)
		EcoreUtil.equals(validPostScriptParsed, labelfontnameParsed).assertTrue
		//Parsed setter
		validPostScriptParsed.setResource
		labelfontnameParsed = validPostScriptParsed
		validPostScriptString.assertEquals(labelfontname)
		EcoreUtil.equals(validPostScriptParsed, labelfontnameParsed).assertTrue
		
		val validPangoParsed = createPangoFontName => [ families += "Times"]
		val validPangoString = "Times,"
		
		//String setter
		labelfontname = validPangoString
		validPangoString.assertEquals(labelfontname)
		EcoreUtil.equals(validPangoParsed, labelfontnameParsed).assertTrue
		//Parsed setter
		validPangoParsed.setResource
		labelfontnameParsed = validPangoParsed
		validPangoString.assertEquals(labelfontname)
		EcoreUtil.equals(validPangoParsed, labelfontnameParsed).assertTrue
	}

	@Test def edge_labelfontsize() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		labelfontsizeRaw.assertNull
		labelfontsize.assertNull
		labelfontsizeParsed.assertNull
		
		// set valid string values
		var validLabelfontsize = "22.5"
		labelfontsize = validLabelfontsize
		validLabelfontsize.assertEquals(labelfontsize)
		
		// set valid parsed values
		var validLabelfontsizeParsed = new Double(5)
		labelfontsizeParsed = validLabelfontsizeParsed
		validLabelfontsizeParsed.assertEquals(labelfontsizeParsed)
		
		// set valid parsed values
		validLabelfontsizeParsed = new Double(1.0)
		labelfontsizeParsed = validLabelfontsizeParsed
		validLabelfontsizeParsed.assertEquals(labelfontsizeParsed)
		
		// set syntactically invalid values
		invalidValue([labelfontsize = "2,5"],
			"Cannot set edge attribute 'labelfontsize' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([labelfontsize = "foo"],
			"Cannot set edge attribute 'labelfontsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([labelfontsize = "0.5"],
			"Cannot set edge attribute 'labelfontsize' to '0.5'. The double value '0.5' is not semantically correct: Value may not be smaller than 1.0."
		)
	}

	@Test def edge_labeltooltip() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		labeltooltipRaw.assertNull
		labeltooltip.assertNull
		labeltooltipParsed.assertNull
		
		// set valid string values
		labeltooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(labeltooltip)
		var expected = createEscString => [
			lines += createJustifiedText => [ text="line1" ]
			lines += createJustifiedText => [ text="line2" ]
		]
		EcoreUtil.equals(expected, labeltooltipParsed).assertTrue
		
		// set valid parsed values
		 val labelTooltip = createEscString => [
			lines += createJustifiedText => [ text="a" justification=Justification.LEFT ]
			lines += createJustifiedText => [ text="b" justification=Justification.RIGHT ]
		]
		labelTooltip.setResource
		labeltooltipParsed = labelTooltip
		"a\\lb\\r".assertEquals(labeltooltip)
	}

	@Test def edge_lp() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		lpRaw.assertNull
		lp.assertNull
		lpParsed.assertNull
		
		// set valid string values
		val validEdgeLp = "0.0,1.1"
		lp = validEdgeLp
		validEdgeLp.assertEquals(lp)
		
		// set valid parsed values
		val validEdgeLpParsed = createPoint => [ x=2.2 y=3.3 ]
		validEdgeLpParsed.setResource
		lpParsed = validEdgeLpParsed
		EcoreUtil.equals(validEdgeLpParsed, lpParsed).assertTrue
		
		// set invalid string values
		invalidValue([lp = "foo"],
			"Cannot set edge attribute 'lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def edge_name() {
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode
		val e = new Edge.Builder(n1, n2).buildEdge
		
		// test edge name calculation on a directed graph
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		graph.nodes(n1, n2).edges(e).build
		"1->2".assertEquals(e._getName)
		
		// test edge name calculation on an undirected graph
		graph.attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		graph.nodes(n1, n2).edges(e).build
		"1--2".assertEquals(e._getName)
	}

	@Test def edge_penwidth() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		penwidthRaw.assertNull
		penwidth.assertNull
		penwidthParsed.assertNull
		
		// set valid string values
		var validPenwidth = "22.5"
		penwidth = validPenwidth
		validPenwidth.assertEquals(penwidth)

		validPenwidth = ""
		penwidth = validPenwidth
		validPenwidth.assertEquals(penwidth)

		// set valid parsed values
		var validPenwidthParsed = new Double(5)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(penwidthParsed)
		
		// set minimum parsed values
		validPenwidthParsed = new Double(0.0)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(penwidthParsed)
		
		// set syntactically invalid values
		invalidValue([penwidth = "2,5"],
			"Cannot set edge attribute 'penwidth' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([penwidth = "foo"],
			"Cannot set edge attribute 'penwidth' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([penwidth = "-0.5"],
			"Cannot set edge attribute 'penwidth' to '-0.5'. The double value '-0.5' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def edge_pos() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		posRaw.assertNull
		pos.assertNull
		posParsed.assertNull
		
		// set valid values
		pos = "e,42.762,459.02 49.25,203.93 41.039,213.9 31.381,227.75 27,242 3.486,318.47 8.9148,344.07 27,422 29.222,431.57 33.428,441.41 37.82,449.98"
		"e,42.762,459.02 49.25,203.93 41.039,213.9 31.381,227.75 27,242 3.486,318.47 8.9148,344.07 27,422 29.222,431.57 33.428,441.41 37.82,449.98".assertEquals(pos)
		var actual = posParsed
		posParsed.assertNotNull
		1.assertEquals(actual.splines.size)
		val spline = actual.splines.head
		spline.endp.assertNotNull
		42.762.assertEquals(spline.endp.x, 0.0)
		459.02.assertEquals(spline.endp.y, 0.0)
		10.assertEquals(spline.controlPoints.size)
		spline.startp.assertNull
		
		// set valid parsed values: spline with 4 control points
		val controlPoint0 = createPoint => [x=0 y=0]
		val controlPoint1 = createPoint => [x=1 y=1]
		val controlPoint2 = createPoint => [x=2 y=2]
		val controlPoint3 = createPoint => [x=3 y=3]
		var validPosParsed = createSplineType => [
			splines += createSpline => [
				controlPoints += #[controlPoint0, controlPoint1, controlPoint2, controlPoint3]
			]
		]
		validPosParsed.setResource
		posParsed = validPosParsed
		"0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0".assertEquals(pos)
		EcoreUtil.equals(validPosParsed, posParsed).assertTrue
		
		// set valid parsed values: spline with 4 control points and a start point
		val startPoint = createPoint => [x=10 y=11]
		validPosParsed = createSplineType => [
			splines += createSpline => [
				startp = startPoint
				controlPoints += #[controlPoint0, controlPoint1, controlPoint2, controlPoint3]
			]
		]
		validPosParsed.setResource
		posParsed = validPosParsed
		"s,10.0,11.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0".assertEquals(pos)
		EcoreUtil.equals(validPosParsed, posParsed).assertTrue
		
		// set valid parsed values: spline with 4 control points and an end point
		val endPoint = createPoint => [x=20 y=21]
		validPosParsed = createSplineType => [
			splines += createSpline => [
				controlPoints += #[controlPoint0, controlPoint1, controlPoint2, controlPoint3]
				endp = endPoint
			]
		]
		validPosParsed.setResource
		posParsed = validPosParsed
		"e,20.0,21.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0".assertEquals(pos)
		EcoreUtil.equals(validPosParsed, posParsed).assertTrue
		// set valid parsed values: spline with 4 control points, start and end point
		validPosParsed = createSplineType => [
			splines += createSpline => [
				startp = startPoint
				controlPoints += #[controlPoint0, controlPoint1, controlPoint2, controlPoint3]
				endp = endPoint
			]
		]
		validPosParsed.setResource
		posParsed = validPosParsed
		"s,10.0,11.0 e,20.0,21.0 0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0".assertEquals(pos)
		EcoreUtil.equals(validPosParsed, posParsed).assertTrue
		
		// set invalid string values
		invalidValue([pos = "s,10.0,11.0 e,20.0,21.0"],
			"Cannot set edge attribute 'pos' to 's,10.0,11.0 e,20.0,21.0'. The value 's,10.0,11.0 e,20.0,21.0' is not a syntactically correct splineType: Mismatched input '<EOF>' expecting RULE_DOUBLE."
		)
		// TODO: add test case for setting invalid parsed values
	}

	@Test def edge_style() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		styleRaw.assertNull
		style.assertNull
		styleParsed.assertNull
		
		// set valid string values
		for (validEdgeStyleItem : #["bold", "dashed", "dotted", "invis", "solid", "tapered"]) {
			style = validEdgeStyleItem
			validEdgeStyleItem.assertEquals(style)
			val expected = createStyle => [
				styleItems += createStyleItem => [
					name = validEdgeStyleItem
				]
			]
			EcoreUtil.equals(expected, styleParsed).assertTrue
		}
		
		val validEdgeStyle = ""
		style = validEdgeStyle
		validEdgeStyle.assertEquals(style)
		
		// set valid parsed values
		val styleEObject = createStyle => [
			styleItems += createStyleItem => [name="bold"]
			styleItems += createStyleItem => [name="dashed"]
		]
		styleEObject.setResource
		styleParsed = styleEObject
		"bold , dashed".assertEquals(style)
		
		// set syntactically invalid values
		invalidValue([style = "bold, "],
			"Cannot set edge attribute 'style' to 'bold, '. The value 'bold, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([style = "foo"],
			"Cannot set edge attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'."
		)
		invalidValue([style = "diagonals"],
			"Cannot set edge attribute 'style' to 'diagonals'. The style value 'diagonals' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'."
		)
	}

	@Test def edge_taillabel() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		taillabelRaw.assertNull
		taillabel.assertNull
		
		// set valid string values
		val validEdgeTailLabel = "simpleEdgeLabel"
		taillabel = validEdgeTailLabel
		validEdgeTailLabel.assertEquals(taillabel)
	}

	@Test def edge_taillp() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
			tailLpRaw.assertNull
			tailLp.assertNull
			tailLpParsed.assertNull
		
		// set valid string values
		var validEdgeTailLp = "42,0.0"
		tailLp = validEdgeTailLp
		validEdgeTailLp.assertEquals(tailLp)
		
		validEdgeTailLp = "0.0,0.0"
		tailLp = validEdgeTailLp
		validEdgeTailLp.assertEquals(tailLp)
		
		// set valid parsed values
		val validEdgeTailLpParsed = createPoint => [x=42 y=0.0]
		validEdgeTailLpParsed.setResource
		tailLpParsed = validEdgeTailLpParsed
		EcoreUtil.equals(validEdgeTailLpParsed, tailLpParsed).assertTrue
		
		// set invalid string values
		invalidValue([tailLp = "foo"],
			"Cannot set edge attribute 'tail_lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def edge_tailport() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		tailportRaw.assertNull
		tailport.assertNull
		tailportParsed.assertNull
		
		// set valid string values
		tailport = "_"
		"_".assertEquals(tailport)
		val expected = createPortPos => [ port="_" ]
		EcoreUtil.equals(expected, tailportParsed).assertTrue
		
		// set valid parsed values
		val tailPort = createPortPos => [ port="a" compassPoint="se" ]
		tailPort.setResource
		tailportParsed = tailPort
		"a:se".assertEquals(tailport)
		
		// set invalid string values
		invalidValue([tailport = "a:foo"],
			"Cannot set edge attribute 'tailport' to 'a:foo'. The value 'a:foo' is not a syntactically correct portPos: Mismatched input 'foo' expecting RULE_COMPASS_POINT_POS."
		)
	}

	@Test def edge_tailtooltip() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		tailtooltipRaw.assertNull
		tailtooltip.assertNull
		tailtooltipParsed.assertNull
		
		// set valid string values
		tailtooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(tailtooltip)
		val expected = createEscString => [
			lines += createJustifiedText => [text="line1"]
			lines += createJustifiedText => [text="line2"]
		]
		EcoreUtil.equals(expected, tailtooltipParsed).assertTrue
		
		// set valid parsed values
		val tailTooltip = createEscString => [
			lines += createJustifiedText => [text="a" justification=Justification.LEFT]
			lines += createJustifiedText => [text="b" justification=Justification.RIGHT]
		]
		tailTooltip.setResource
		tailtooltipParsed = tailTooltip
		"a\\lb\\r".assertEquals(tailtooltip)
	}

	@Test def edge_tooltip() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		tooltipRaw.assertNull
		tooltip.assertNull
		tooltipParsed.assertNull
		
		// set valid string values
		tooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(tooltip)
		val expected = createEscString => [
			lines += createJustifiedText => [text="line1"]
			lines += createJustifiedText => [text="line2"]
		]
		EcoreUtil.equals(expected, tooltipParsed).assertTrue
		
		// set valid parsed values
		val toolTip = createEscString => [
			lines += createJustifiedText => [text="a" justification=Justification.LEFT]
			lines += createJustifiedText => [text="b" justification=Justification.RIGHT]
		]
		toolTip.setResource
		tooltipParsed = toolTip
		"a\\lb\\r".assertEquals(tooltip)
	}

	@Test def edge_xlabel() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		xlabelRaw.assertNull
		xlabel.assertNull
		
		// set valid string values
		val validEdgeXLabel = "edgeXLabel"
		xlabel = validEdgeXLabel
		validEdgeXLabel.assertEquals(xlabel)
	}

	@Test def edge_xlp() {
		val n1 = new Node.Builder().buildNode
		val n2 = new Node.Builder().buildNode
		val it = new Edge.Builder(n1, n2).buildEdge
		
		// test getters if no explicit value is set
		xlpRaw.assertNull
		xlp.assertNull
		xlpParsed.assertNull
		
		// set valid string values
		xlp = "47, 11"
		xlp = "34.5, 45.3!"
		
		// set valid parsed values
		val point = createPoint => [x=33 y=54.6]
		point.setResource
		xlpParsed = point
		"33.0, 54.6".assertEquals(xlp)
		EcoreUtil.equals(point, xlpParsed).assertTrue
		
		// set invalid string values
		invalidValue([xlp = "foo"],
			"Cannot set edge attribute 'xlp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def graph_bb() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		bbRaw.assertNull
		bb.assertNull
		bbParsed.assertNull
		
		// set valid string values
		bb = "39.631,558,111.63,398"
		"39.631,558,111.63,398".assertEquals(bb)
		val r = bbParsed
		r.assertNotNull
		39.631d.assertEquals(r.llx, 0d)
		558d.assertEquals(r.lly, 0d)
		111.63d.assertEquals(r.urx, 0d)
		398d.assertEquals(r.ury, 0d)
		
		// set valid string values
		bb = "0x3.e,0x1.eP1,2.16e+07,36"
		"0x3.e,0x1.eP1,2.16e+07,36".assertEquals(bb)
		val s = bbParsed
		r.assertNotNull
		3.875d.assertEquals(s.llx, 0d)
		3.75d.assertEquals(s.lly, 0d)
		2.16E7d.assertEquals(s.urx, 0d)
		36d.assertEquals(s.ury, 0d)
		
		// set valid parsed values
		val bbEObject = createRect => [llx=10.1 lly=20.2 urx=30.3 ury=40.4]
		bbEObject.setResource
		bbParsed = bbEObject
		"10.1 , 20.2 , 30.3 , 40.4".assertEquals(bb)
		
		// set invalid string values
		invalidValue([bb = "39.631,558,111.63"],
			"Cannot set graph attribute 'bb' to '39.631,558,111.63'. The value '39.631,558,111.63' is not a syntactically correct rect: Mismatched input '<EOF>' expecting ','."
		)
	}

	@Test def graph_bgcolor() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		bgcolorRaw.assertNull
		bgcolor.assertNull
		bgcolorParsed.assertNull
		
		// set valid string values - rgb format
		bgcolor = "#a0522d"
		"#a0522d".assertEquals(bgcolor)
		var rgbColor = createRGBColor => [
			r = "a0"
			g = "52"
			b = "2d"
		]
		EcoreUtil.equals(rgbColor.createColorList, bgcolorParsed).assertTrue
		// set valid string values - rgba format
		bgcolor = "#a0522dcc"
		"#a0522dcc".assertEquals(bgcolor)
		rgbColor.a = "cc"
		EcoreUtil.equals(rgbColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid string values - hsv format
		bgcolor = ".051 .718 .627"
		".051 .718 .627".assertEquals(bgcolor)
		val hsvColor = createHSVColor => [
			h = ".051"
			s = ".718"
			v = ".627"
		]
		EcoreUtil.equals(hsvColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid string values - string format
		bgcolor = "sienna"
		"sienna".assertEquals(bgcolor)
		val stringColor = createStringColor => [
			name = "sienna"
		]
		EcoreUtil.equals(stringColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "a0"
			g = "52"
			b = "2d"
		]
		bgcolorParsed = rgbColor.createColorList
		"#a0522d".assertEquals(bgcolor)
		EcoreUtil.equals(rgbColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "cc"
		bgcolorParsed = rgbColor.createColorList
		"#a0522dcc".assertEquals(bgcolor)
		EcoreUtil.equals(rgbColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		bgcolorParsed = hsvColor.createColorList
		".051 .718 .627".assertEquals(bgcolor)
		EcoreUtil.equals(hsvColor.createColorList, bgcolorParsed).assertTrue
		
		// set valid parsed values - string format
		bgcolorParsed = stringColor.createColorList
		"sienna".assertEquals(bgcolor)
		EcoreUtil.equals(stringColor.createColorList, bgcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([bgcolor = "#gggggg"],
			"Cannot set graph attribute 'bgcolor' to '#gggggg'. The value '#gggggg' is not a syntactically correct colorList: No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'. No viable alternative at character 'g'."
		)
	}

	@Test def graph_clusterrank() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		clusterrankRaw.assertNull
		clusterrank.assertNull
		clusterrankParsed.assertNull
		
		// set valid string values
		var validGraphClusterMode = "local"
		clusterrank = validGraphClusterMode
		validGraphClusterMode.assertEquals(clusterrank)
		ClusterMode.LOCAL.assertEquals(clusterrankParsed)
		
		validGraphClusterMode = "global"
		clusterrank = validGraphClusterMode
		validGraphClusterMode.assertEquals(clusterrank)
		ClusterMode.GLOBAL.assertEquals(clusterrankParsed)
		
		validGraphClusterMode = "none"
		clusterrank = validGraphClusterMode
		validGraphClusterMode.assertEquals(clusterrank)
		ClusterMode.NONE.assertEquals(clusterrankParsed)
		
		// set valid parsed values
		var validGraphClusterModeParsed = ClusterMode.LOCAL
		clusterrankParsed = validGraphClusterModeParsed
		validGraphClusterModeParsed.toString.assertEquals(clusterrank)
		validGraphClusterModeParsed.assertEquals(clusterrankParsed)
		
		validGraphClusterModeParsed = ClusterMode.GLOBAL
		clusterrankParsed = validGraphClusterModeParsed
		validGraphClusterModeParsed.toString.assertEquals(clusterrank)
		validGraphClusterModeParsed.assertEquals(clusterrankParsed)
		
		validGraphClusterModeParsed = ClusterMode.NONE
		clusterrankParsed = validGraphClusterModeParsed
		validGraphClusterModeParsed.toString.assertEquals(clusterrank)
		validGraphClusterModeParsed.assertEquals(clusterrankParsed)
		
		// set invalid string values
		invalidValue([clusterrank = "foo"],
			"Cannot set graph attribute 'clusterrank' to 'foo'. The value 'foo' is not a syntactically correct clusterMode: Value has to be one of 'local', 'global', 'none'."
		)
	}

	@Test def cluster_color() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		colorRaw.assertNull
		color.assertNull
		colorParsed.assertNull
		
		// set valid string values - rgb format
		color = "#ffffff"
		"#ffffff".assertEquals(color)
		var rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid string values - rgba format
		color = "#ffffff00"
		"#ffffff00".assertEquals(color)
		rgbColor.a = "00"
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid string values - hsv format
		color = "0.000,0.000,1.000"
		"0.000,0.000,1.000".assertEquals(color)
		val hsvColor = createHSVColor => [
			h = "0.000"
			s = "0.000"
			v = "1.000"
		]
		EcoreUtil.equals(hsvColor, colorParsed).assertTrue
		
		// set valid string values - string format
		color = "//white"
		"//white".assertEquals(color)
		val stringColor = createStringColor => [
			name = "white"
		]
		EcoreUtil.equals(stringColor, colorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		rgbColor.setResource
		colorParsed = rgbColor
		"#ffffff".assertEquals(color)
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "00"
		colorParsed = rgbColor
		"#ffffff00".assertEquals(color)
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		colorParsed = hsvColor
		"0.000 0.000 1.000".assertEquals(color)
		EcoreUtil.equals(hsvColor, colorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		colorParsed = stringColor
		"white".assertEquals(color)
		EcoreUtil.equals(stringColor, colorParsed).assertTrue
		
		// set invalid string values
		invalidValue([color = "/white"],
			"Cannot set graph attribute 'color' to '/white'. The value '/white' is not a syntactically correct color: Mismatched input '<EOF>' expecting '/'."
		)
	}

	@Test def graph_colorscheme() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		colorschemeRaw.assertNull
		colorscheme.assertNull
		
		// set valid string values
		val validColorScheme = "svg"
		colorscheme = validColorScheme
		validColorScheme.assertEquals(colorscheme)
		
		// set invalid string values
		invalidValue([colorscheme = "foo"],
			"Cannot set graph attribute 'colorscheme' to 'foo'. The string value 'foo' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'."
		)
	}

	@Test def graph_fillcolor() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		fillcolorRaw.assertNull
		fillcolor.assertNull
		fillcolorParsed.assertNull
		
		// set valid string values - rgb format
		fillcolor = "#00ff00"
		"#00ff00".assertEquals(fillcolor)
		var rgbColor = createRGBColor => [
			r = "00"
			g = "ff"
			b = "00"
		]
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid string values - rgba format
		fillcolor = "#00ff00ff"
		"#00ff00ff".assertEquals(fillcolor)
		rgbColor.a = "ff"
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fillcolor = "0.3 .8 .7"
		"0.3 .8 .7".assertEquals(fillcolor)
		val hsvColor = createHSVColor => [
			h = "0.3"
			s = ".8"
			v = ".7"
		]
		EcoreUtil.equals(hsvColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid string values - string format
		fillcolor = "/bugn9/7"
		"/bugn9/7".assertEquals(fillcolor)
		val stringColor = createStringColor => [
			scheme = "bugn9"
			name = "7"
		]
		EcoreUtil.equals(stringColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "00"
			g = "ff"
			b = "00"
		]
		fillcolorParsed = rgbColor.createColorList
		"#00ff00".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "ff"
		fillcolorParsed = rgbColor.createColorList
		"#00ff00ff".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		fillcolorParsed = hsvColor.createColorList
		"0.3 .8 .7".assertEquals(fillcolor)
		EcoreUtil.equals(hsvColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - string format
		fillcolorParsed = stringColor.createColorList
		"/bugn9/7".assertEquals(fillcolor)
		EcoreUtil.equals(stringColor.createColorList, fillcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fillcolor = "//"],
			"Cannot set graph attribute 'fillcolor' to '//'. The value '//' is not a syntactically correct colorList: No viable alternative at input '<EOF>'."
		)
	}

	@Test def graph_fontcolor() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		fontcolorRaw.assertNull
		fontcolor.assertNull
		fontcolorParsed.assertNull
		
		// set valid string values - rgb format
		fontcolor = "#ffffff"
		"#ffffff".assertEquals(fontcolor)
		var rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid string values - rgba format
		fontcolor = "#ffffff00"
		"#ffffff00".assertEquals(fontcolor)
		rgbColor.a = "00"
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fontcolor = "0.000,0.000,1.000"
		"0.000,0.000,1.000".assertEquals(fontcolor)
		val hsvColor = createHSVColor => [
			h = "0.000"
			s = "0.000"
			v = "1.000"
		]
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue
		
		// set valid string values - string format
		fontcolor = "//white"
		"//white".assertEquals(fontcolor)
		val stringColor = createStringColor => [
			name = "white"
		]
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		rgbColor.setResource
		fontcolorParsed = rgbColor
		"#ffffff".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "00"
		fontcolorParsed = rgbColor
		"#ffffff00".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		fontcolorParsed = hsvColor
		"0.000 0.000 1.000".assertEquals(fontcolor)
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		fontcolorParsed = stringColor
		"white".assertEquals(fontcolor)
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fontcolor = "/white"],
			"Cannot set graph attribute 'fontcolor' to '/white'. The value '/white' is not a syntactically correct color: Mismatched input '<EOF>' expecting '/'."
		)
	}

	@Test def graph_fontname() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		fontnameRaw.assertNull
		fontname.assertNull
		fontnameParsed.assertNull
		
		val validPostScriptParsed = createPostScriptFontName => [ alias = PostScriptFontAlias.TIMES_ROMAN]
		val validPostScriptString = "Times-Roman"
		
		//String setter
		fontname = validPostScriptString
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPostScriptParsed.setResource
		fontnameParsed = validPostScriptParsed
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		
		val validPangoParsed = createPangoFontName => [ families += "Times"]
		val validPangoString = "Times,"
		
		//String setter
		fontname = validPangoString
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPangoParsed.setResource
		fontnameParsed = validPangoParsed
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
	}

	@Test def graph_fontsize() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		fontsizeRaw.assertNull
		fontsize.assertNull
		fontsizeParsed.assertNull
		
		// set valid string values
		val validFontsize = "22.5"
		fontsize = validFontsize
		validFontsize.assertEquals(fontsize)
		
		// set valid parsed values
		var validFontsizeParsed = new Double(5)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set valid parsed values
		validFontsizeParsed = new Double(1.0)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set syntactically invalid values
		invalidValue([fontsize = "2,5"],
			"Cannot set graph attribute 'fontsize' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([fontsize = "foo"],
			"Cannot set graph attribute 'fontsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([fontsize = "0.5"],
			"Cannot set graph attribute 'fontsize' to '0.5'. The double value '0.5' is not semantically correct: Value may not be smaller than 1.0."
		)
	}

	@Test def cluster_penwidth() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		penwidthRaw.assertNull
		penwidth.assertNull
		penwidthParsed.assertNull
		
		// set valid string values
		var validPenwidth = "22.5"
		penwidth = validPenwidth
		validPenwidth.assertEquals(validPenwidth)
		
		// set valid parsed values
		var validPenwidthParsed = new Double(5)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(validPenwidthParsed)
		
		// set minimum parsed values
		validPenwidthParsed = new Double(0.0)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(validPenwidthParsed)
		
		// set syntactically invalid values
		invalidValue([penwidth = "2,5"],
			"Cannot set graph attribute 'penwidth' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([penwidth = "foo"],
			"Cannot set graph attribute 'penwidth' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([penwidth = "-0.5"],
			"Cannot set graph attribute 'penwidth' to '-0.5'. The double value '-0.5' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def graph_forcelabels() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		forcelabelsRaw.assertNull
		forcelabels.assertNull
		forcelabelsParsed.assertNull
		
		// set valid string values
		var validGraphForceLabels = "true"
		forcelabels = validGraphForceLabels
		validGraphForceLabels.assertEquals(forcelabels)
		
		validGraphForceLabels = "false"
		forcelabels = validGraphForceLabels
		validGraphForceLabels.assertEquals(forcelabels)
		
		// set valid parsed values
		var validGraphForceLabelsParsed = true
		forcelabelsParsed = validGraphForceLabelsParsed
		validGraphForceLabelsParsed.assertEquals(forcelabelsParsed)
		
		validGraphForceLabelsParsed = false
		forcelabelsParsed = validGraphForceLabelsParsed
		validGraphForceLabelsParsed.assertEquals(forcelabelsParsed)
		
		// set invalid string values
		invalidValue([forcelabels = "foo"],
			"Cannot set graph attribute 'forcelabels' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value."
		)
	}

	@Test def graph_id() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		idRaw.assertNull
		id.assertNull
		
		// set valid string values
		val validGraphId = "graphId"
		id = validGraphId
		validGraphId.assertEquals(id)
		// TODO: add test cases for setting invalid graph id (e.g. a not unique id)
	}

	@Test def graph_label() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		labelRaw.assertNull
		label.assertNull
		
		// set valid string values
		val validGraphLabel = "graphLabel"
		label = validGraphLabel
		validGraphLabel.assertEquals(label)
	}

	@Test def graph_layout() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		layoutRaw.assertNull
		layout.assertNull
		layoutParsed.assertNull
		
		// set valid string values
		var validGraphLayout = "circo"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.CIRCO.assertEquals(layoutParsed)
		
		validGraphLayout = "dot"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.DOT.assertEquals(layoutParsed)
		
		validGraphLayout = "fdp"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.FDP.assertEquals(layoutParsed)
		
		validGraphLayout = "neato"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.NEATO.assertEquals(layoutParsed)
		
		validGraphLayout = "osage"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.OSAGE.assertEquals(layoutParsed)
		
		validGraphLayout = "sfdp"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.SFDP.assertEquals(layoutParsed)
		
		validGraphLayout = "twopi"
		layout = validGraphLayout
		validGraphLayout.assertEquals(layout)
		Layout.TWOPI.assertEquals(layoutParsed)
		
		// set valid parsed values
		var validGraphLayoutParsed = Layout.CIRCO
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.DOT
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.FDP
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.NEATO
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.OSAGE
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.SFDP
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		validGraphLayoutParsed = Layout.TWOPI
		layoutParsed = validGraphLayoutParsed
		validGraphLayoutParsed.toString.assertEquals(layout)
		validGraphLayoutParsed.assertEquals(layoutParsed)
		
		// set invalid string values
		invalidValue([layout = "foo"],
			"Cannot set graph attribute 'layout' to 'foo'. The value 'foo' is not a syntactically correct layout: Value has to be one of 'circo', 'dot', 'fdp', 'neato', 'osage', 'sfdp', 'twopi'."
		)
	}

	@Test def graph_lp() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		lpRaw.assertNull
		lp.assertNull
		lpParsed.assertNull
		
		// set valid string values
		val validGraphLp = "0.0,1.1"
		lp = validGraphLp
		validGraphLp.assertEquals(lp)
		
		// set valid parsed values
		val validGraphLpParsed = createPoint => [x=2.2 y=3.3]
		validGraphLpParsed.setResource
		lpParsed = validGraphLpParsed
		EcoreUtil.equals(validGraphLpParsed, lpParsed).assertTrue
		"2.2, 3.3".assertEquals(lp)
		
		// set invalid string values
		invalidValue([lp = "foo"],
			"Cannot set graph attribute 'lp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def graph_name() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		_getName.assertNull
		_getNameRaw.assertNull
		
		_setName = "TestGraph"
		"TestGraph".assertEquals(_getName)
		ID.fromString("TestGraph").assertEquals(_getNameRaw)
		
		val graphName = ID.fromValue("Test Graph", Type.QUOTED_STRING)
		_setNameRaw = graphName
		"Test Graph".assertEquals(_getName)
		ID.fromValue("Test Graph", Type.QUOTED_STRING).assertEquals(_getNameRaw)
		
		_setName('"Test Graph"')
		'"Test Graph"'.assertEquals(_getName)
		'"\\"Test Graph\\""'.assertEquals(_getNameRaw.toString)
	}

	@Test def graph_nodesep() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		nodesepRaw.assertNull
		nodesep.assertNull
		nodesepParsed.assertNull
		
		// set valid string values
		val validNodesep = "0.5"
		nodesep = validNodesep
		validNodesep.assertEquals(nodesep)
		
		// set valid parsed values
		var validNodesepParsed = new Double(0.02)
		nodesepParsed = validNodesepParsed
		validNodesepParsed.assertEquals(nodesepParsed)
		
		// set valid parsed values
		validNodesepParsed = new Double(0.0)
		nodesepParsed = validNodesepParsed
		validNodesepParsed.assertEquals(nodesepParsed)
		
		// set syntactically invalid values
		invalidValue([nodesep = "0,5"],
			"Cannot set graph attribute 'nodesep' to '0,5'. The value '0,5' is not a syntactically correct double: For input string: \"0,5\"."
		)
		invalidValue([nodesep = "foo"],
			"Cannot set graph attribute 'nodesep' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([nodesep = "-1"],
			"Cannot set graph attribute 'nodesep' to '-1'. The double value '-1' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def graph_outputorder() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		outputorderRaw.assertNull
		outputorder.assertNull
		outputorderParsed.assertNull
		
		// set valid string values
		var validGraphOutputMode = "breadthfirst"
		outputorder = validGraphOutputMode
		validGraphOutputMode.assertEquals(outputorder)
		OutputMode.BREADTHFIRST.assertEquals(outputorderParsed)
		
		validGraphOutputMode = "nodesfirst"
		outputorder = validGraphOutputMode
		validGraphOutputMode.assertEquals(outputorder)
		OutputMode.NODESFIRST.assertEquals(outputorderParsed)
		
		validGraphOutputMode = "edgesfirst"
		outputorder = validGraphOutputMode
		validGraphOutputMode.assertEquals(outputorder)
		OutputMode.EDGEFIRST.assertEquals(outputorderParsed)
		
		// set valid parsed values
		var validGraphOutputModeParsed = OutputMode.BREADTHFIRST
		outputorderParsed = validGraphOutputModeParsed
		validGraphOutputModeParsed.toString.assertEquals(outputorder)
		validGraphOutputModeParsed.assertEquals(outputorderParsed)
		
		validGraphOutputModeParsed = OutputMode.NODESFIRST
		outputorderParsed = validGraphOutputModeParsed
		validGraphOutputModeParsed.toString.assertEquals(outputorder)
		validGraphOutputModeParsed.assertEquals(outputorderParsed)
		
		validGraphOutputModeParsed = OutputMode.EDGEFIRST
		outputorderParsed = validGraphOutputModeParsed
		validGraphOutputModeParsed.toString.assertEquals(outputorder)
		validGraphOutputModeParsed.assertEquals(outputorderParsed)
		
		// set invalid string values
		invalidValue([outputorder = "foo"],
			"Cannot set graph attribute 'outputorder' to 'foo'. The value 'foo' is not a syntactically correct outputMode: Value has to be one of 'breadthfirst', 'nodesfirst', 'edgesfirst'."
		)
	}

	@Test def graph_pagedir() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		pagedirRaw.assertNull
		pagedir.assertNull
		pagedirParsed.assertNull
		
		// set valid string values
		var validGraphPagedir = "BL"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.BL.assertEquals(pagedirParsed)
		
		validGraphPagedir = "BR"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.BR.assertEquals(pagedirParsed)
		
		validGraphPagedir = "TL"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.TL.assertEquals(pagedirParsed)
		
		validGraphPagedir = "TR"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.TR.assertEquals(pagedirParsed)
		
		validGraphPagedir = "RB"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.RB.assertEquals(pagedirParsed)
		
		validGraphPagedir = "RT"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.RT.assertEquals(pagedirParsed)
		
		validGraphPagedir = "LB"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.LB.assertEquals(pagedirParsed)
		
		validGraphPagedir = "LT"
		pagedir = validGraphPagedir
		validGraphPagedir.assertEquals(pagedir)
		Pagedir.LT.assertEquals(pagedirParsed)
		
		// set valid parsed values
		var validGraphPagedirParsed = Pagedir.BL
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.BR
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.TL
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.TR
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.RB
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.RT
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.LB
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		validGraphPagedirParsed = Pagedir.LT
		pagedirParsed = validGraphPagedirParsed
		validGraphPagedirParsed.toString.assertEquals(pagedir)
		validGraphPagedirParsed.assertEquals(pagedirParsed)
		
		// set invalid string values
		invalidValue([pagedir = "foo"],
			"Cannot set graph attribute 'pagedir' to 'foo'. The value 'foo' is not a syntactically correct pagedir: Value has to be one of 'BL', 'BR', 'TL', 'TR', 'RB', 'RT', 'LB', 'LT'."
		)
	}

	@Test def graph_rankdir() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		rankdirRaw.assertNull
		rankdir.assertNull
		rankdirParsed.assertNull
		
		// set valid string values
		var validGraphRankdir = "LR"
		rankdir = validGraphRankdir
		validGraphRankdir.assertEquals(rankdir)
		Rankdir.LR.assertEquals(rankdirParsed)
		
		validGraphRankdir = "RL"
		rankdir = validGraphRankdir
		validGraphRankdir.assertEquals(rankdir)
		Rankdir.RL.assertEquals(rankdirParsed)
		
		validGraphRankdir = "TB"
		rankdir = validGraphRankdir
		validGraphRankdir.assertEquals(rankdir)
		Rankdir.TB.assertEquals(rankdirParsed)
		
		validGraphRankdir = "BT"
		rankdir = validGraphRankdir
		validGraphRankdir.assertEquals(rankdir)
		Rankdir.BT.assertEquals(rankdirParsed)
		
		// set valid parsed values
		var validGraphRankdirParsed = Rankdir.LR
		rankdirParsed = validGraphRankdirParsed
		validGraphRankdirParsed.toString.assertEquals(rankdir)
		validGraphRankdirParsed.assertEquals(rankdirParsed)
		
		validGraphRankdirParsed = Rankdir.RL
		rankdirParsed = validGraphRankdirParsed
		validGraphRankdirParsed.toString.assertEquals(rankdir)
		validGraphRankdirParsed.assertEquals(rankdirParsed)
		
		validGraphRankdirParsed = Rankdir.TB
		rankdirParsed = validGraphRankdirParsed
		validGraphRankdirParsed.toString.assertEquals(rankdir)
		validGraphRankdirParsed.assertEquals(rankdirParsed)
		
		validGraphRankdirParsed = Rankdir.BT
		rankdirParsed = validGraphRankdirParsed
		validGraphRankdirParsed.toString.assertEquals(rankdir)
		validGraphRankdirParsed.assertEquals(rankdirParsed)
		
		// set invalid string values
		invalidValue([rankdir = "foo"],
			"Cannot set graph attribute 'rankdir' to 'foo'. The value 'foo' is not a syntactically correct rankdir: Value has to be one of 'TB', 'LR', 'BT', 'RL'."
		)
	}

	@Test def graph_splines() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		splinesRaw.assertNull
		splines.assertNull
		splinesParsed.assertNull
		
		// set valid string values
		var validGraphSplines = "compound"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.COMPOUND.assertEquals(splinesParsed)
		
		validGraphSplines = "curved"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.CURVED.assertEquals(splinesParsed)
		
		validGraphSplines = "false"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.FALSE.assertEquals(splinesParsed)
		
		validGraphSplines = "line"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.LINE.assertEquals(splinesParsed)
		
		validGraphSplines = "none"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.NONE.assertEquals(splinesParsed)
		
		validGraphSplines = "spline"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.SPLINE.assertEquals(splinesParsed)
		
		validGraphSplines = "polyline"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.POLYLINE.assertEquals(splinesParsed)
		
		validGraphSplines = "ortho"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.ORTHO.assertEquals(splinesParsed)
		
		validGraphSplines = "true"
		splines = validGraphSplines
		validGraphSplines.assertEquals(splines)
		Splines.TRUE.assertEquals(splinesParsed)
		
		// set valid parsed values
		var validGraphSplinesParsed = Splines.COMPOUND
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.COMPOUND.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.CURVED
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.CURVED.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.EMPTY
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.EMPTY.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.FALSE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.FALSE.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.LINE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.LINE.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.NONE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.NONE.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.ORTHO
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.ORTHO.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.POLYLINE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.POLYLINE.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.SPLINE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.SPLINE.assertEquals(splinesParsed)
		
		validGraphSplinesParsed = Splines.TRUE
		splinesParsed = validGraphSplinesParsed
		validGraphSplinesParsed.toString.assertEquals(splines)
		Splines.TRUE.assertEquals(splinesParsed)
		
		// set invalid string values
		invalidValue([splines = "foo"],
			"Cannot set graph attribute 'splines' to 'foo'. The value 'foo' is not a syntactically correct splines: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value. Value has to be one of 'compound', 'curved', '', 'false', 'line', 'none', 'ortho', 'polyline', 'spline', 'true'."
		)
	}

	@Test def graph_style() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		styleRaw.assertNull
		style.assertNull
		styleParsed.assertNull
		
		// set valid string values
		for (validGraphStyleItem : #["filled", "radial"]) {
			style = validGraphStyleItem
			validGraphStyleItem.assertEquals(style)
			val expected = createStyle => [
				styleItems += createStyleItem => [
					name = validGraphStyleItem
				]
			]
			EcoreUtil.equals(expected, styleParsed).assertTrue
		}
		// set valid parsed values
		val validStyle = createStyle => [
			styleItems += createStyleItem => [
				name = "filled"
			]
		]
		validStyle.setResource
		styleParsed = validStyle
		"filled".assertEquals(style)
		
		// set syntactically invalid values
		invalidValue([style = "filled, "],
			"Cannot set graph attribute 'style' to 'filled, '. The value 'filled, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME."
		)
		// TODO: set syntactically correct, but semantically invalid values
	}

	@Test def cluster_style() {
		val it = new Graph.Builder().build
		_setName("cluster_0")
		
		// test getters if no explicit value is set
		styleRaw.assertNull
		style.assertNull
		styleParsed.assertNull
		
		// set valid string values
		for(validClusterStyleItem : #["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"]) {
			style = validClusterStyleItem
			validClusterStyleItem.assertEquals(style)
			val expected = createStyle => [
				styleItems += createStyleItem => [
					name = validClusterStyleItem
				]
			]
			EcoreUtil.equals(expected, styleParsed).assertTrue
		}
		
		val validEdgeStyle = ""
		style = validEdgeStyle
		validEdgeStyle.assertEquals(style)
		
		// set valid parsed values
		val validStyle = createStyle => [
			styleItems += createStyleItem => [name="bold"]
			styleItems += createStyleItem => [name="dashed"]
		]
		validStyle.setResource 
		styleParsed = validStyle
		"bold , dashed".assertEquals(style)
		
		// set syntactically invalid values
		invalidValue([style = "bold, "],
			"Cannot set graph attribute 'style' to 'bold, '. The value 'bold, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([style = "foo"],
			"Cannot set graph attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped'."
		)
		invalidValue([style = "diagonals"],
			"Cannot set graph attribute 'style' to 'diagonals'. The style value 'diagonals' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped'."
		)
	}

	@Test def cluster_tooltip() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		tooltipRaw.assertNull
		tooltip.assertNull
		tooltipParsed.assertNull
		
		// set valid string values
		tooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(tooltip)
		val expected = createEscString => [
			lines += createJustifiedText => [text="line1"]
			lines += createJustifiedText => [text="line2"]
		]
		EcoreUtil.equals(expected, tooltipParsed).assertTrue
		
		// set valid parsed values
		val validTooltip = createEscString => [
			lines += createJustifiedText => [text="a" justification = Justification.LEFT]
			lines += createJustifiedText => [text="b" justification = Justification.RIGHT]
		]
		validTooltip.setResource
		tooltipParsed = validTooltip
		"a\\lb\\r".assertEquals(tooltip)
	}

	@Test def graph_type() {
		// test directed graph
		var it = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).build
		GraphType.DIGRAPH.assertEquals(_getType)
		
		// test undirected graph
		it = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH).build
		GraphType.GRAPH.assertEquals(_getType)
	}
	
	@Test def node_isCluster() {
		val it = new Node.Builder().buildNode
		//initially false
		cluster.assertFalse
		
		val innerCluster = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterDummy").build
		val innerNonCluster = new Graph.Builder().build
		
		val Runnable assertLocalBehaviour = [
			nestedGraph = null
			cluster.assertFalse
			nestedGraph = innerNonCluster
			cluster.assertFalse
			nestedGraph = innerCluster
			cluster.assertTrue
		]
		
		val Runnable assertNoneGlobalBehaviour = [
			nestedGraph = null
			cluster.assertFalse
			nestedGraph = innerNonCluster
			cluster.assertFalse
			nestedGraph = innerCluster
			cluster.assertFalse
		]
		
		val extension Consumer<Graph> assertAllBehaviours = [ it |
			attributesProperty.remove(CLUSTERRANK__G)
			assertLocalBehaviour.run
			clusterrankParsed = ClusterMode.LOCAL
			assertLocalBehaviour.run
			clusterrankParsed = ClusterMode.GLOBAL
			assertNoneGlobalBehaviour.run
			clusterrankParsed = ClusterMode.NONE
			assertNoneGlobalBehaviour.run
		]
		
		val outerGraph = new Graph.Builder().nodes(it).build
		outerGraph.accept
		
		val outerOuterGraph = new Graph.Builder().node().attr([p1, p2|p1.nestedGraph = p2], outerGraph).build
		
		//values set on outer graph should not change the behaviour
		outerGraph.attributesProperty.remove(CLUSTERRANK__G)
		outerOuterGraph.accept
		outerGraph.clusterrankParsed = ClusterMode.LOCAL
		outerOuterGraph.accept
		outerGraph.clusterrankParsed = ClusterMode.GLOBAL
		outerOuterGraph.accept
		outerGraph.clusterrankParsed = ClusterMode.NONE
		outerOuterGraph.accept
	}

	@Test def node_color() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		colorRaw.assertNull
		color.assertNull
		colorParsed.assertNull
		
		// set valid string values - rgb format
		color = "#ffffff"
		"#ffffff".assertEquals(color)
		var rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid string values - rgba format
		color = "#ffffff00"
		"#ffffff00".assertEquals(color)
		rgbColor.a = "00"
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid string values - hsv format
		color = "0.000, 0.000, 1.000"
		"0.000, 0.000, 1.000".assertEquals(color)
		val hsvColor = createHSVColor => [
			h = "0.000"
			s = "0.000"
			v = "1.000"
		]
		EcoreUtil.equals(hsvColor, colorParsed).assertTrue
		
		// set valid string values - string format
		color = "/svg/white"
		"/svg/white".assertEquals(color)
		val stringColor = createStringColor => [
			scheme = "svg"
			name = "white"
		]
		EcoreUtil.equals(stringColor, colorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "ff"
			g = "ff"
			b = "ff"
		]
		rgbColor.setResource
		colorParsed = rgbColor
		"#ffffff".assertEquals(color)
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "00"
		colorParsed = rgbColor
		"#ffffff00".assertEquals(color)
		EcoreUtil.equals(rgbColor, colorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		colorParsed = hsvColor
		"0.000 0.000 1.000".assertEquals(color)
		EcoreUtil.equals(hsvColor, colorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		colorParsed = stringColor
		"/svg/white".assertEquals(color)
		EcoreUtil.equals(stringColor, colorParsed).assertTrue
		
		// set invalid string values
		invalidValue([color = "/foo/antiquewhite1"],
			"Cannot set node attribute 'color' to '/foo/antiquewhite1'. The color value '/foo/antiquewhite1' is not semantically correct: 'foo' is not a valid color scheme."
		)
		invalidValue([color = "/svg/antiquewhite1"],
			"Cannot set node attribute 'color' to '/svg/antiquewhite1'. The color value '/svg/antiquewhite1' is not semantically correct: The 'antiquewhite1' color is not valid within the 'svg' color scheme."
		)
	}

	@Test def node_colorscheme() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		colorschemeRaw.assertNull
		colorscheme.assertNull
		
		// set valid string values
		val validColorScheme = "accent3"
		colorscheme = validColorScheme
		validColorScheme.assertEquals(colorscheme)
		
		// set invalid string values
		invalidValue([colorscheme = "1"],
			"Cannot set node attribute 'colorscheme' to '1'. The string value '1' is not semantically correct: Value should be one of 'accent3', 'accent4', 'accent5', 'accent6', 'accent7', 'accent8', 'blues3', 'blues4', 'blues5', 'blues6', 'blues7', 'blues8', 'blues9', 'brbg10', 'brbg11', 'brbg3', 'brbg4', 'brbg5', 'brbg6', 'brbg7', 'brbg8', 'brbg9', 'bugn3', 'bugn4', 'bugn5', 'bugn6', 'bugn7', 'bugn8', 'bugn9', 'bupu3', 'bupu4', 'bupu5', 'bupu6', 'bupu7', 'bupu8', 'bupu9', 'dark23', 'dark24', 'dark25', 'dark26', 'dark27', 'dark28', 'gnbu3', 'gnbu4', 'gnbu5', 'gnbu6', 'gnbu7', 'gnbu8', 'gnbu9', 'greens3', 'greens4', 'greens5', 'greens6', 'greens7', 'greens8', 'greens9', 'greys3', 'greys4', 'greys5', 'greys6', 'greys7', 'greys8', 'greys9', 'oranges3', 'oranges4', 'oranges5', 'oranges6', 'oranges7', 'oranges8', 'oranges9', 'orrd3', 'orrd4', 'orrd5', 'orrd6', 'orrd7', 'orrd8', 'orrd9', 'paired10', 'paired11', 'paired12', 'paired3', 'paired4', 'paired5', 'paired6', 'paired7', 'paired8', 'paired9', 'pastel13', 'pastel14', 'pastel15', 'pastel16', 'pastel17', 'pastel18', 'pastel19', 'pastel23', 'pastel24', 'pastel25', 'pastel26', 'pastel27', 'pastel28', 'piyg10', 'piyg11', 'piyg3', 'piyg4', 'piyg5', 'piyg6', 'piyg7', 'piyg8', 'piyg9', 'prgn10', 'prgn11', 'prgn3', 'prgn4', 'prgn5', 'prgn6', 'prgn7', 'prgn8', 'prgn9', 'pubu3', 'pubu4', 'pubu5', 'pubu6', 'pubu7', 'pubu8', 'pubu9', 'pubugn3', 'pubugn4', 'pubugn5', 'pubugn6', 'pubugn7', 'pubugn8', 'pubugn9', 'puor10', 'puor11', 'puor3', 'puor4', 'puor5', 'puor6', 'puor7', 'puor8', 'puor9', 'purd3', 'purd4', 'purd5', 'purd6', 'purd7', 'purd8', 'purd9', 'purples3', 'purples4', 'purples5', 'purples6', 'purples7', 'purples8', 'purples9', 'rdbu10', 'rdbu11', 'rdbu3', 'rdbu4', 'rdbu5', 'rdbu6', 'rdbu7', 'rdbu8', 'rdbu9', 'rdgy10', 'rdgy11', 'rdgy3', 'rdgy4', 'rdgy5', 'rdgy6', 'rdgy7', 'rdgy8', 'rdgy9', 'rdpu3', 'rdpu4', 'rdpu5', 'rdpu6', 'rdpu7', 'rdpu8', 'rdpu9', 'rdylbu10', 'rdylbu11', 'rdylbu3', 'rdylbu4', 'rdylbu5', 'rdylbu6', 'rdylbu7', 'rdylbu8', 'rdylbu9', 'rdylgn10', 'rdylgn11', 'rdylgn3', 'rdylgn4', 'rdylgn5', 'rdylgn6', 'rdylgn7', 'rdylgn8', 'rdylgn9', 'reds3', 'reds4', 'reds5', 'reds6', 'reds7', 'reds8', 'reds9', 'set13', 'set14', 'set15', 'set16', 'set17', 'set18', 'set19', 'set23', 'set24', 'set25', 'set26', 'set27', 'set28', 'set310', 'set311', 'set312', 'set33', 'set34', 'set35', 'set36', 'set37', 'set38', 'set39', 'spectral10', 'spectral11', 'spectral3', 'spectral4', 'spectral5', 'spectral6', 'spectral7', 'spectral8', 'spectral9', 'svg', 'x11', 'ylgn3', 'ylgn4', 'ylgn5', 'ylgn6', 'ylgn7', 'ylgn8', 'ylgn9', 'ylgnbu3', 'ylgnbu4', 'ylgnbu5', 'ylgnbu6', 'ylgnbu7', 'ylgnbu8', 'ylgnbu9', 'ylorbr3', 'ylorbr4', 'ylorbr5', 'ylorbr6', 'ylorbr7', 'ylorbr8', 'ylorbr9', 'ylorrd3', 'ylorrd4', 'ylorrd5', 'ylorrd6', 'ylorrd7', 'ylorrd8', 'ylorrd9'."
		)
	}

	@Test def node_distortion() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		distortionRaw.assertNull
		distortion.assertNull
		distortionParsed.assertNull
		
		// set valid string values
		var validNodeDistortion = "5"
		distortion = validNodeDistortion
		validNodeDistortion.assertEquals(distortion)
		5.0.assertEquals(distortionParsed.doubleValue, 0.0)
		
		// set the minimum valid value
		validNodeDistortion = "-100.0"
		distortion = validNodeDistortion
		validNodeDistortion.assertEquals(distortion)
		(-100.0).assertEquals(distortionParsed.doubleValue, 0.0)
		
		// set valid parsed values
		var validNodeDistortionParsed = 10.0
		distortionParsed = validNodeDistortionParsed
		"10.0".assertEquals(distortion)
		validNodeDistortionParsed.assertEquals(distortionParsed, 0.0)
		
		validNodeDistortionParsed = 9.9
		distortionParsed = validNodeDistortionParsed
		"9.9".assertEquals(distortion)
		validNodeDistortionParsed.assertEquals(distortionParsed, 0.0)
		
		// set syntactically invalid values
		invalidValue([distortion = "42x"],
			"Cannot set node attribute 'distortion' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([distortion = "-100.01"],
			"Cannot set node attribute 'distortion' to '-100.01'. The double value '-100.01' is not semantically correct: Value may not be smaller than -100.0."
		)
	}

	@Test def node_fillcolor() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		fillcolorRaw.assertNull
		fillcolor.assertNull
		fillcolorParsed.assertNull
		
		// set valid string values - rgb format
		fillcolor = "#00ff00"
		"#00ff00".assertEquals(fillcolor)
		var rgbColor = createRGBColor => [
			r = "00"
			g = "ff"
			b = "00"
		]
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid string values - rgba format
		fillcolor = "#00ff00ff"
		"#00ff00ff".assertEquals(fillcolor)
		rgbColor.a = "ff"
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fillcolor = "0.3 .8 .7"
		"0.3 .8 .7".assertEquals(fillcolor)
		val hsvColor = createHSVColor => [
			h = "0.3"
			s = ".8"
			v = ".7"
		]
		EcoreUtil.equals(createColorList(hsvColor), fillcolorParsed).assertTrue
		
		// set valid string values - string format
		fillcolor = "/bugn9/7"
		"/bugn9/7".assertEquals(fillcolor)
		val stringColor = createStringColor => [
			scheme = "bugn9"
			name = "7"
		]
		EcoreUtil.equals(stringColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [
			r = "00"
			g = "ff"
			b = "00"
		]
		fillcolorParsed = rgbColor.createColorList
		"#00ff00".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "ff"
		fillcolorParsed = rgbColor.createColorList
		"#00ff00ff".assertEquals(fillcolor)
		EcoreUtil.equals(rgbColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		fillcolorParsed = hsvColor.createColorList
		"0.3 .8 .7".assertEquals(fillcolor)
		EcoreUtil.equals(hsvColor.createColorList, fillcolorParsed).assertTrue
		
		// set valid parsed values - string format
		fillcolorParsed = stringColor.createColorList
		"/bugn9/7".assertEquals(fillcolor)
		EcoreUtil.equals(stringColor.createColorList, fillcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fillcolor = "//"],
			"Cannot set node attribute 'fillcolor' to '//'. The value '//' is not a syntactically correct colorList: No viable alternative at input '<EOF>'."
		)
	}

	@Test def node_fixedsize() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		fixedsizeRaw.assertNull
		fixedsize.assertNull
		fixedsizeParsed.assertNull
		
		// set valid string values
		var validNodeFixedSize = "true"
		fixedsize = validNodeFixedSize
		validNodeFixedSize.assertEquals(fixedsize)
		
		validNodeFixedSize = "false"
		fixedsize = validNodeFixedSize
		validNodeFixedSize.assertEquals(fixedsize)
		
		// set valid parsed values
		var validNodeFixedsizeParsed = true
		fixedsizeParsed = validNodeFixedsizeParsed
		validNodeFixedsizeParsed.assertEquals(fixedsizeParsed)
		
		validNodeFixedsizeParsed = false
		fixedsizeParsed = validNodeFixedsizeParsed
		validNodeFixedsizeParsed.assertEquals(fixedsizeParsed)
		
		// set invalid string values
		invalidValue([fixedsize = "foo"],
			"Cannot set node attribute 'fixedsize' to 'foo'. The value 'foo' is not a syntactically correct bool: The given value 'foo' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value."
		)
	}

	@Test def node_fontcolor() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		fontcolorRaw.assertNull
		fontcolor.assertNull
		fontcolorParsed.assertNull
				
		// set valid string values - rgb format
		fontcolor = "#00ff00"
		"#00ff00".assertEquals(fontcolor)
		var rgbColor = createRGBColor => [r="00" g="ff" b="00"]
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		// set valid string values - rgba format
		fontcolor = "#00ff00ff"
		"#00ff00ff".assertEquals(fontcolor)
		rgbColor.a = "ff"
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid string values - hsv format
		fontcolor = "0.3, .8, .7"
		"0.3, .8, .7".assertEquals(fontcolor)
		val hsvColor = createHSVColor => [h="0.3" s=".8" v=".7"]
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue
		
		// set valid string values - string format
		fontcolor = "/brbg11/10"
		"/brbg11/10".assertEquals(fontcolor)
		val stringColor = createStringColor => [scheme="brbg11" name="10"]
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - rgb format
		rgbColor = createRGBColor => [r="00" g="ff" b="00"]
		rgbColor.setResource
		fontcolorParsed = rgbColor
		"#00ff00".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - rgba format
		rgbColor.a = "ff"
		fontcolorParsed = rgbColor
		"#00ff00ff".assertEquals(fontcolor)
		EcoreUtil.equals(rgbColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - hsv format
		hsvColor.setResource
		fontcolorParsed = hsvColor
		"0.3 .8 .7".assertEquals(fontcolor)
		EcoreUtil.equals(hsvColor, fontcolorParsed).assertTrue
		
		// set valid parsed values - string format
		stringColor.setResource
		fontcolorParsed = stringColor
		"/brbg11/10".assertEquals(fontcolor)
		EcoreUtil.equals(stringColor, fontcolorParsed).assertTrue
		
		// set invalid string values
		invalidValue([fontcolor = "///"],
			"Cannot set node attribute 'fontcolor' to '///'. The value '///' is not a syntactically correct color: No viable alternative at input '/'."
		)
	}

	@Test def node_fontname() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		fontnameRaw.assertNull
		fontname.assertNull
		fontnameParsed.assertNull
		
		val validPostScriptParsed = createPostScriptFontName => [ alias = PostScriptFontAlias.TIMES_ROMAN]
		val validPostScriptString = "Times-Roman"
		
		//String setter
		fontname = validPostScriptString
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPostScriptParsed.setResource
		fontnameParsed = validPostScriptParsed
		validPostScriptString.assertEquals(fontname)
		EcoreUtil.equals(validPostScriptParsed, fontnameParsed).assertTrue
		
		val validPangoParsed = createPangoFontName => [ families += "Times"]
		val validPangoString = "Times,"
		
		//String setter
		fontname = validPangoString
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
		//Parsed setter
		validPangoParsed.setResource
		fontnameParsed = validPangoParsed
		validPangoString.assertEquals(fontname)
		EcoreUtil.equals(validPangoParsed, fontnameParsed).assertTrue
	}

	@Test def node_fontsize() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		fontsizeRaw.assertNull
		fontsize.assertNull
		fontsizeParsed.assertNull
		
		// set valid string values
		val validFontsize = "22.5"
		fontsize = validFontsize
		validFontsize.assertEquals(fontsize)
		
		// set valid parsed values
		var validFontsizeParsed = new Double(5)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set valid parsed values
		validFontsizeParsed = new Double(1.0)
		fontsizeParsed = validFontsizeParsed
		validFontsizeParsed.assertEquals(fontsizeParsed)
		
		// set syntactically invalid values
		invalidValue([fontsize = "2,5"],
			"Cannot set node attribute 'fontsize' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([fontsize = "foo"],
			"Cannot set node attribute 'fontsize' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([fontsize = "0.5"],
			"Cannot set node attribute 'fontsize' to '0.5'. The double value '0.5' is not semantically correct: Value may not be smaller than 1.0."
		)
	}

	@Test def node_height() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		heightRaw.assertNull
		height.assertNull
		heightParsed.assertNull
		
		// set valid string values
		var validNodeHeight = "0.56"
		height = validNodeHeight
		validNodeHeight.assertEquals(height)

		// set the minimum valid value
		validNodeHeight = "0.02"
		height = validNodeHeight
		validNodeHeight.assertEquals(height)

		// set valid parsed values
		var validNodeHeightParsed = 0.1
		heightParsed = validNodeHeightParsed
		validNodeHeightParsed.assertEquals(heightParsed, 0.0)
		
		validNodeHeightParsed = 0.0
		heightParsed = validNodeHeightParsed
		validNodeHeightParsed.assertEquals(heightParsed, 0.0)
		
		validNodeHeightParsed = 9.9
		heightParsed = validNodeHeightParsed
		validNodeHeightParsed.assertEquals(heightParsed, 0.0)
		
		// set syntactically invalid values
		invalidValue([height = "47x, 11"],
			"Cannot set node attribute 'height' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([height = "-0.01"],
			"Cannot set node attribute 'height' to '-0.01'. The double value '-0.01' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def node_id() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		idRaw.assertNull
		id.assertNull
		
		// set valid string values
		val validNodeId = "nodeId"
		id = validNodeId
		validNodeId.assertEquals(id)
		
		// TODO: add test cases for setting invalid node id (e.g. a not unique id)
	}

	@Test def node_label() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		labelRaw.assertNull
		label.assertNull
		
		// set valid string values
		val validNodeLabel = "nodeLabel"
		label = validNodeLabel
		validNodeLabel.assertEquals(label)
	}

	@Test def node_name() {
		val it = new Node.Builder().buildNode
		_getName.assertNull
		_getNameRaw.assertNull
		
		_setName("TestNode")
		"TestNode".assertEquals(_getName)
		ID.fromString("TestNode").assertEquals(_getNameRaw)
	}

	@Test def node_penwidth() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		penwidthRaw.assertNull
		penwidth.assertNull
		penwidthParsed.assertNull
		
		// set valid string values
		var validPenwidth = "22.5"
		penwidth = validPenwidth
		validPenwidth.assertEquals(penwidth)
		
		validPenwidth = ""
		penwidth = validPenwidth
		validPenwidth.assertEquals(penwidth)
		
		// set valid parsed values
		var validPenwidthParsed = new Double(5)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(penwidthParsed)
		
		// set minimum parsed values
		validPenwidthParsed = new Double(0.0)
		penwidthParsed = validPenwidthParsed
		validPenwidthParsed.assertEquals(penwidthParsed)
		
		// set syntactically invalid values
		invalidValue([penwidth = "2,5"],
			"Cannot set node attribute 'penwidth' to '2,5'. The value '2,5' is not a syntactically correct double: For input string: \"2,5\"."
		)
		invalidValue([penwidth = "foo"],
			"Cannot set node attribute 'penwidth' to 'foo'. The value 'foo' is not a syntactically correct double: For input string: \"foo\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([penwidth = "-0.5"],
			"Cannot set node attribute 'penwidth' to '-0.5'. The double value '-0.5' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def node_pos() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		posRaw.assertNull
		pos.assertNull
		posParsed.assertNull
		
		// set valid string values
		pos = "47, 11"
		pos = "34.5, 45.3!"
		pos = "0x5p2,-12.3E+2"
		pos = "-221.31,936.82"
		
		// set valid parsed values
		val point = createPoint => [x=33 y=54.6 inputOnly=true]
		point.setResource
		posParsed = point
		"33.0, 54.6!".assertEquals(pos)
		EcoreUtil.equals(posParsed, point).assertTrue
		
		// set invalid string values
		invalidValue([pos = "47x, 11"],
			"Cannot set node attribute 'pos' to '47x, 11'. The value '47x, 11' is not a syntactically correct point: No viable alternative at character 'x'."
		)
	}

	@Test def node_shape() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		shapeRaw.assertNull
		shape.assertNull
		shapeParsed.assertNull
		
		// set valid (polygon based) string values
		val validPolygonBasedNodeShapes = #["assembly", "box", "box3d", "cds", "circle", "component",
			"cylinder", "diamond", "doublecircle", "doubleoctagon", "egg", "ellipse", "fivepoverhang", "folder",
			"hexagon", "house", "insulator", "invhouse", "invtrapezium", "invtriangle", "larrow", "lpromoter",
			"Mcircle", "Mdiamond", "Msquare", "none", "note", "noverhang", "octagon", "oval", "parallelogram",
			"pentagon", "plain", "plaintext", "point", "polygon", "primersite", "promoter", "proteasesite",
			"proteinstab", "rarrow", "rect", "rectangle", "restrictionsite", "ribosite", "rnastab", "rpromoter",
			"septagon", "signature", "square", "star", "tab", "terminator", "threepoverhang", "trapezium", "triangle",
			"tripleoctagon", "underline", "utr"]
		
		for (validPolygonBasedNodeShape : validPolygonBasedNodeShapes) {
			shape = validPolygonBasedNodeShape
			validPolygonBasedNodeShape.assertEquals(shape)
			val shapeParsed = createShape => [
				shape = createPolygonBasedShape => [
					shape = PolygonBasedNodeShape.get(validPolygonBasedNodeShape)
				]
			]
			EcoreUtil.equals(shapeParsed, shapeParsed).assertTrue
		}
		// set valid (record based) string values
		for (validRecordBasedNodeShape : #["record", "Mrecord"]) {
			shape = validRecordBasedNodeShape
			validRecordBasedNodeShape.assertEquals(shape)
			val shape = createShape => [
				shape = createRecordBasedShape => [
					shape = RecordBasedNodeShape.get(validRecordBasedNodeShape)
				]
			]
			EcoreUtil.equals(shape, shapeParsed).assertTrue
		}
		
		// set valid parsed values
		val validShape = createShape => [
			shape = createPolygonBasedShape => [
				shape = PolygonBasedNodeShape.BOX
			]
		]
		validShape.setResource
		shapeParsed = validShape
		"box".assertEquals(shape)
		
		// set invalid string values
		invalidValue([shape = "foo"],
			"Cannot set node attribute 'shape' to 'foo'. The value 'foo' is not a syntactically correct shape: Extraneous input 'foo' expecting EOF."
		)
	}

	@Test def node_sides() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		sidesRaw.assertNull
		sides.assertNull
		sidesParsed.assertNull
		
		// set valid string values
		var validNodeSides = "5"
		sides = validNodeSides
		validNodeSides.assertEquals(sides)
		5.assertEquals(sidesParsed.intValue)
		
		// set the minimum valid value
		validNodeSides = "0"
		sides = validNodeSides
		validNodeSides.assertEquals(sides)
		0.assertEquals(sidesParsed.intValue)
		
		// set valid parsed values
		var validNodeSidesParsed = 3
		sidesParsed = validNodeSidesParsed
		"3".assertEquals(sides)
		validNodeSidesParsed.assertEquals(sidesParsed)
		validNodeSidesParsed = 42
		sidesParsed = validNodeSidesParsed
		"42".assertEquals(sides)
		validNodeSidesParsed.assertEquals(sidesParsed)
		
		// set syntactically invalid values
		invalidValue([sides = "42x"],
			"Cannot set node attribute 'sides' to '42x'. The value '42x' is not a syntactically correct int: For input string: \"42x\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([sides = "-1"],
			"Cannot set node attribute 'sides' to '-1'. The int value '-1' is not semantically correct: Value may not be smaller than 0."
		)
	}

	@Test def node_skew() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		skewRaw.assertNull
		skew.assertNull
		skewParsed.assertNull
		
		// set valid string values
		var validNodeSkew = "5"
		skew = validNodeSkew
		assertEquals(validNodeSkew, skew)
		5.0.assertEquals(skewParsed.doubleValue, 0.0)
		
		// set the minimum valid value
		validNodeSkew = "-100.0"
		skew = validNodeSkew
		validNodeSkew.assertEquals(skew)
		(-100.0).assertEquals(skewParsed.doubleValue, 0.0)
		
		// set valid parsed values
		var validNodeSkewParsed = 10.0
		skewParsed = validNodeSkewParsed
		"10.0".assertEquals(skew)
		validNodeSkewParsed.assertEquals(skewParsed, 0.0)
		
		validNodeSkewParsed = 9.9
		skewParsed = validNodeSkewParsed
		"9.9".assertEquals(skew)
		validNodeSkewParsed.assertEquals(skewParsed, 0.0)
		
		// set syntactically invalid values
		invalidValue([skew = "42x"],
			"Cannot set node attribute 'skew' to '42x'. The value '42x' is not a syntactically correct double: For input string: \"42x\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([skew = "-100.01"],
			"Cannot set node attribute 'skew' to '-100.01'. The double value '-100.01' is not semantically correct: Value may not be smaller than -100.0."
		)
	}

	@Test def node_style() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		styleRaw.assertNull
		style.assertNull
		styleParsed.assertNull
		
		// set valid string values
		for (validNodeStyleItem : #["bold", "dashed", "diagonals", "dotted", "filled", "invis",
									"radial", "rounded", "solid", "striped", "wedged"]) {
			style = validNodeStyleItem
			validNodeStyleItem.assertEquals(style)
			val style = createStyle => [
				styleItems += createStyleItem => [name=validNodeStyleItem]
			]
			EcoreUtil.equals(style, styleParsed).assertTrue
		}
		val validNodeStyle = ""
		style = validNodeStyle
		validNodeStyle.assertEquals(style)
		
		// set valid parsed values
		val validStyle = createStyle => [
			styleItems += createStyleItem => [name="bold"]
			styleItems += createStyleItem => [name="dashed"]
		]
		validStyle.setResource
		styleParsed = validStyle
		"bold , dashed".assertEquals(style)
		
		// set syntactically invalid values
		invalidValue([style = "bold, "],
			"Cannot set node attribute 'style' to 'bold, '. The value 'bold, ' is not a syntactically correct style: Mismatched input '<EOF>' expecting RULE_NAME."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([style = "foo"],
			"Cannot set node attribute 'style' to 'foo'. The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'."
		)
		invalidValue([style = "tapered"],
			"Cannot set node attribute 'style' to 'tapered'. The style value 'tapered' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'."
		)
	}

	@Test def node_tooltip() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		tooltipRaw.assertNull
		tooltip.assertNull
		tooltipParsed.assertNull
		
		// set valid string values
		tooltip = "line1\\nline2"
		"line1\\nline2".assertEquals(tooltip)
		
		val expected = createEscString => [
			lines += createJustifiedText => [text="line1"]
			lines += createJustifiedText => [text="line2"]
		]
		EcoreUtil.equals(expected, tooltipParsed).assertTrue
		
		// set valid parsed values
		val toolTip = createEscString => [
			lines += createJustifiedText => [text="a" justification=Justification.LEFT]
			lines += createJustifiedText => [text="b" justification=Justification.RIGHT]
		]
		toolTip.setResource
		tooltipParsed = toolTip
		"a\\lb\\r".assertEquals(tooltip)
	}

	@Test def node_width() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		widthRaw.assertNull
		width.assertNull
		widthParsed.assertNull
		
		// set valid string values
		var validNodeWidth = "0.56"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)
		validNodeWidth = "76"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)
		validNodeWidth = "5E5"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)
		validNodeWidth = "0X8"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)
		validNodeWidth = "3.2e-3"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)

		// set the minimum valid value
		validNodeWidth = "0.01"
		width = validNodeWidth
		validNodeWidth.assertEquals(width)

		// set valid parsed values
		var validNodeWidthParsed = 0.1
		widthParsed = validNodeWidthParsed
		validNodeWidthParsed.assertEquals(widthParsed, 0.0)
		
		validNodeWidthParsed = 0.0
		widthParsed = validNodeWidthParsed
		validNodeWidthParsed.assertEquals(widthParsed, 0.0)
		
		validNodeWidthParsed = 9.9
		widthParsed = validNodeWidthParsed
		validNodeWidthParsed.assertEquals(widthParsed, 0.0)
		
		// set syntactically invalid values
		invalidValue([width = "47x, 11"],
			"Cannot set node attribute 'width' to '47x, 11'. The value '47x, 11' is not a syntactically correct double: For input string: \"47x, 11\"."
		)

		// set syntactically correct, but semantically invalid values
		invalidValue([width = "-1.05"],
			"Cannot set node attribute 'width' to '-1.05'. The double value '-1.05' is not semantically correct: Value may not be smaller than 0.0."
		)
	}

	@Test def node_xlabel() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		xlabelRaw.assertNull
		xlabel.assertNull
		
		// set valid string values
		val validNodeXLabel = "nodeXLabel"
		xlabel = validNodeXLabel
		validNodeXLabel.assertEquals(xlabel)
	}

	@Test def node_xlp() {
		val it = new Node.Builder().buildNode
		
		// test getters if no explicit value is set
		xlpRaw.assertNull
		xlp.assertNull
		xlpParsed.assertNull
		
		// set valid string values
		xlp = "47, 11"
		xlp = "34.5, 45.3!"
		
		// set valid parsed values
		val point = createPoint => [x=33 y=54.6 inputOnly=true]
		point.setResource
		xlpParsed = point
		"33.0, 54.6!".assertEquals(xlp)
		EcoreUtil.equals(xlpParsed, point).assertTrue
		
		// set invalid string values
		invalidValue([xlp = "foo"],
			"Cannot set node attribute 'xlp' to 'foo'. The value 'foo' is not a syntactically correct point: No viable alternative at character 'f'. No viable alternative at character 'o'. No viable alternative at character 'o'."
		)
	}

	@Test def subgraph_rank() {
		val it = new Graph.Builder().build
		
		// test getters if no explicit value is set
		rank.assertNull
		
		// set valid string values
		rank = "same"
		"same".assertEquals(rank)
		RankType.SAME.assertEquals(rankParsed)
		
		rank = "min"
		"min".assertEquals(rank)
		RankType.MIN.assertEquals(rankParsed)
		
		rank = "source"
		"source".assertEquals(rank)
		RankType.SOURCE.assertEquals(rankParsed)
		
		rank = "max"
		"max".assertEquals(rank)
		RankType.MAX.assertEquals(rankParsed)
		
		rank = "sink"
		"sink".assertEquals(rank)
		RankType.SINK.assertEquals(rankParsed)
		
		// set valid parsed values
		rankParsed = RankType.SAME
		"same".assertEquals(rank)
		RankType.SAME.assertEquals(rankParsed)
		
		rankParsed = RankType.MIN
		"min".assertEquals(rank)
		RankType.MIN.assertEquals(rankParsed)
		
		rankParsed = RankType.SOURCE
		"source".assertEquals(rank)
		RankType.SOURCE.assertEquals(rankParsed)
		
		rankParsed = RankType.MAX
		"max".assertEquals(rank)
		RankType.MAX.assertEquals(rankParsed)
		
		rankParsed = RankType.SINK
		"sink".assertEquals(rank)
		RankType.SINK.assertEquals(rankParsed)
		
		// set invalid string value
		invalidValue([rank = "foo"],
			"Cannot set graph attribute 'rank' to 'foo'. The value 'foo' is not a syntactically correct rankType: Value has to be one of 'same', 'min', 'source', 'max', 'sink'."
		)
	}

	private def invalidValue(()=>void setter, String expectedMessage) {
		try {
			setter.apply
			"IllegalArgumentException expected.".fail
		} catch (IllegalArgumentException e) {
			expectedMessage.assertEquals(e.message)
		}
	}

	private def createColorList(Color color) {
		val colorList = createColorList => [
			colorValues += createWC => [
				it.color = color
			]
		]
		colorList.setResource
		colorList
	}

	private def void setResource(EObject eObject) {
		new ResourceImpl => [contents += eObject]
	}
}
