/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey Gerrit Prigge (itemis AG) - additional test cases
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import javafx.scene.Group
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.Dot2ZestAttributesConverter
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.zest.fx.ZestProperties.*
import static extension org.junit.Assert.*

/*
 * Test cases for the {@link Dot2ZestAttributesConverter#convertAttributes(Edge, Edge)} method.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class Dot2ZestEdgeAttributesConversionTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	extension DotImport = new DotImport
	extension Dot2ZestAttributesConverter = new Dot2ZestAttributesConverter

	@Test def edge_style001() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
		''')
	}

	@Test def edge_style002() {
		'''
			digraph {
				1->2[color=green]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke: #00ff00;
		''')
	}

	@Test def edge_style003() {
		'''
			graph {
				1--2 [style=bold]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-width: 2;
		''')
	}

	@Test def edge_style004() {
		'''
			graph {
				1--2 [style=bold penwidth=0.5]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke-width:0.5;
		''')
	}

	@Test def edge_style005() {
		'''
			graph {
				1--2 [style=dashed]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-dash-array: 7 7;
		''')
	}

	@Test def edge_style006() {
		'''
			graph {
				1--2 [style=dotted]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-dash-array: 1 7;
		''')
	}

	@Test def edge_style007() {
		'''
			graph {
				1--2 [style=solid]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
		''')
	}
	
	@Test def edge_style008() {
		'''
			graph {
				1--2 [penwidth=3]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke-width:3.0;
		''')
	}
	
	@Test def edge_style009() {
		// test css attribute order
		// -fx-stroke-width needs to follow after -fx-stroke
		'''
			graph {
				1--2 [penwidth=3 color=yellow]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke: #ffff00;
			-fx-stroke-width:3.0;
		''')
	}

	@Test def edge_sourceDecorationStyle001() {
		'''
			digraph {
				1->2[dir=back]
			}
		'''.assertEdgeSourceDecorationStyles('''
			-fx-stroke: #000000;
			-fx-fill: #000000;
		''')
	}

	@Test def edge_sourceDecorationStyle002() {
		'''
			digraph {
				1->2[color=green arrowtail=normal dir=back]
			}
		'''.assertEdgeSourceDecorationStyles('''
			-fx-stroke: #00ff00;
			-fx-fill: #00ff00;
		''')
	}

	@Test def edge_sourceDecorationStyle003() {
		'''
			digraph {
				1->2 [dir=both color=green arrowtail=none]
			}
		'''.assertEdgeSourceDecorationStyles('''
		''')
	}

	@Test def edge_sourceDecorationStyle004() {
		'''
			digraph {
				1->2 [dir=both color=green arrowtail=nonebox]
			}
		'''.assertEdgeSourceDecorationStyles('''''', '''
			-fx-stroke: #00ff00;
			-fx-fill: #00ff00;
		''')
	}

	@Test def edge_sourceDecorationStyle005() {
		'''
			digraph {
				1->2 [dir=both color=green arrowtail=noneobox]
			}
		'''.assertEdgeSourceDecorationStyles('''''', '''
			-fx-stroke: #00ff00;
			-fx-fill: #ffffff;
		''')
	}
	
	@Test def edge_sourceDecorationStyle006() {
		// test css attribute order
		// -fx-stroke-width needs to follow after -fx-stroke
		'''
			digraph {
				1->2[dir=both color=green arrowtail=obox penwidth=0.5]
			}
		'''.assertEdgeSourceDecorationStyles('''
			-fx-stroke: #00ff00;
			-fx-fill: #ffffff;
			-fx-stroke-width: 0.5;
		''') 
	}
	
	@Test def edge_sourceDecorationStyle007() {
		// test css attribute order
		// -fx-stroke-width needs to follow after -fx-stroke
		'''
			digraph {
				1->2[dir=both arrowtail=normal penwidth=0.5]
			}
		'''.assertEdgeSourceDecorationStyles('''
			-fx-stroke: #000000;
			-fx-fill: #000000;
			-fx-stroke-width: 0.5;
		''') 
	}

	@Test def edge_targetDecorationStyle001() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeTargetDecorationStyles('''
			-fx-stroke: #000000;
			-fx-fill: #000000;
		''')
	}

	@Test def edge_targetDecorationStyle002() {
		'''
			digraph {
				1->2[color=green arrowhead=normal]
			}
		'''.assertEdgeTargetDecorationStyles('''
			-fx-stroke: #00ff00;
			-fx-fill: #00ff00;
		''')
	}

	@Test def edge_targetDecorationStyle003() {
		'''
			digraph {
				1->2[color=green arrowhead=none]
			}
		'''.assertEdgeTargetDecorationStyles('''
		''')
	}

	@Test def edge_targetDecorationStyle004() {
		'''
			digraph {
				1->2[color=green arrowhead=nonedot]
			}
		'''.assertEdgeTargetDecorationStyles('''''',
		'''
			-fx-stroke: #00ff00;
			-fx-fill: #00ff00;
		''')
	}

	@Test def edge_targetDecorationStyle005() {
		'''
			digraph {
				1->2[color=green arrowhead=onormal]
			}
		'''.assertEdgeTargetDecorationStyles('''
			-fx-stroke: #00ff00;
			-fx-fill: #ffffff;
		''')
	}
	
	@Test def edge_targetDecorationStyle006() {
		// test css attribute order
		// -fx-stroke-width needs to follow after -fx-stroke
		'''
			digraph {
				1->2[color=green arrowhead=onormal penwidth=0.5]
			}
		'''.assertEdgeTargetDecorationStyles('''
			-fx-stroke: #00ff00;
			-fx-fill: #ffffff;
			-fx-stroke-width: 0.5;
		''')
	}

	@Test def edge_targetDecorationStyle007() {
		// test css attribute order
		// -fx-stroke-width needs to follow after -fx-stroke
		'''
			digraph {
				1->2[arrowhead=onormal penwidth=0.5]
			}
		'''.assertEdgeTargetDecorationStyles('''
			-fx-stroke: #000000;
			-fx-fill: #ffffff;
			-fx-stroke-width: 0.5;
		''')
	}

	@Test def edge_label001() {
		'''
			digraph {
				1->2[label="foobar"]
			}
		'''.assertEdgeLabel("foobar")
	}

	@Test def edge_label002() {
		'''
			digraph {
				1->2[label="foo\nbar"]
			}
		'''.assertEdgeLabel("foo\nbar")
	}

	@Test def edge_label003() {
		'''
			digraph {
				1->2[label="foo\nbar\nbaz"]
			}
		'''.assertEdgeLabel("foo\nbar\nbaz")
	}

	@Test def edge_label004() {
		'''
			digraph {
				1->2[label="Test \E"]
			}
		'''.assertEdgeLabel("Test 1->2")
	}

	@Test def edge_label005() {
		'''
			digraph samplegraph {
				1->2[label="\E \G"]
			}
		'''.assertEdgeLabel("1->2 samplegraph")
	}

	@Test def edge_label006() {
		'''
			digraph {
				1->2[fontcolor=red label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_label007() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_label008() {
		mockAvailableFonts("Bitstream Vera Sans")
		'''
			digraph {
				edge[fontname=Helvetica]
				1->2[label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-font-family: "Bitstream Vera Sans";
			-fx-font-weight: 400;
			-fx-font-style: normal;
		''')
	}

	@Test def edge_label009() {
		'''
			digraph {
				edge[fontsize=16]
				1->2[label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_label010() {
		mockAvailableFonts("Bitstream Vera Sans")
		'''
			digraph {
				1->2[label=l, fontcolor=red, fontname=Helvetica, fontsize=16]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-fill: #ff0000;
			-fx-font-family: "Bitstream Vera Sans";
			-fx-font-weight: 400;
			-fx-font-style: normal;
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_externalLabel001() {
		'''
			digraph {
				1->2[xlabel="foobar"]
			}
		'''.assertEdgeExternalLabel("foobar")
	}

	@Test def edge_externalLabel002() {
		'''
			digraph {
				1->2[xlabel="foo\nbar"]
			}
		'''.assertEdgeExternalLabel("foo\nbar")
	}

	@Test def edge_externalLabel003() {
		'''
			digraph {
				1->2[xlabel="foo\nbar\nbaz"]
			}
		'''.assertEdgeExternalLabel("foo\nbar\nbaz")
	}

	@Test def edge_externalLabel004() {
		'''
			digraph testedGraphName {
				1->2[xlabel="g: \G e:\E h:\H t:\T"]
			}
		'''.assertEdgeExternalLabel("g: testedGraphName e:1->2 h:2 t:1")
	}

	@Test def edge_externalLabel005() {
		'''
			digraph {
				1->2[fontcolor=red xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_externalLabel006() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_externalLabel007() {
		mockAvailableFonts("Arial")
		'''
			digraph {
				edge[fontname=Helvetica]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-font-family: "Arial";
			-fx-font-weight: 400;
			-fx-font-style: normal;
		''')
	}

	@Test def edge_externalLabel008() {
		'''
			digraph {
				edge[fontsize=16]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_externalLabel009() {
		mockAvailableFonts("Arial")
		'''
			digraph {
				edge[fontcolor=red, fontname=Helvetica, fontsize=16]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-fill: #ff0000;
			-fx-font-family: "Arial";
			-fx-font-weight: 400;
			-fx-font-style: normal;
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_sourceLabel001() {
		'''
			digraph {
				1->2[taillabel="foobar"]
			}
		'''.assertEdgeSourceLabel("foobar")
	}

	@Test def edge_sourceLabel002() {
		'''
			digraph {
				1->2[taillabel="foo\nbar"]
			}
		'''.assertEdgeSourceLabel("foo\nbar")
	}

	@Test def edge_sourceLabel003() {
		'''
			digraph {
				1->2[taillabel="foo\nbar\nbaz"]
			}
		'''.assertEdgeSourceLabel("foo\nbar\nbaz")
	}

	@Test def edge_sourceLabel004() {
		'''
			digraph testedGraphName {
				1->2[taillabel="g: \G e:\E h:\H t:\T"]
			}
		'''.assertEdgeSourceLabel("g: testedGraphName e:1->2 h:2 t:1")
	}

	@Test def edge_sourceLabel005() {
		'''
			digraph {
				1->2[fontcolor=red taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_sourceLabel006() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_sourceLabel007() {
		'''
			digraph {
				edge[labelfontcolor=red]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_sourceLabel008() {
		'''
			digraph {
				edge[labelfontcolor=blue, fontcolor=red]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-fill: #0000ff;
		''')
	}

	@Test def edge_sourceLabel010() {
		mockAvailableFonts("Times New Roman", "Helvetica")
		'''
			digraph {
				edge[fontname=Helvetica]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-font-family: "Helvetica";
			-fx-font-weight: 400;
			-fx-font-style: normal;
		''')
	}

	@Test def edge_sourceLabel011() {
		mockAvailableFonts("Times New Roman", "Helvetica")
		'''
			digraph {
				edge[labelfontname="Times-Roman", fontname=Helvetica]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-font-family: "Times New Roman";
			-fx-font-weight: 400;
			-fx-font-style: normal;
		''')
	}

	@Test def edge_sourceLabel012() {
		'''
			digraph {
				edge[fontsize=12]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-font-size: 12.0;
		''')
	}

	@Test def edge_sourceLabel013() {
		'''
			digraph {
				edge[labelfontsize=14, fontsize=12]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-font-size: 14.0;
		''')
	}

	@Test def edge_sourceLabel014() {
		mockAvailableFonts("Courier New")
		'''
			digraph {
				edge[labelfontsize=14, fontsize=12, fontcolor=green, fontname="Courier"]
				1->2[taillabel=t]
			}
		'''.assertEdgeSourceLabelCssStyle('''
			-fx-fill: #00ff00;
			-fx-font-family: "Courier New";
			-fx-font-weight: 400;
			-fx-font-style: normal;
			-fx-font-size: 14.0;
		''')
	}

	@Test def edge_targetLabel001() {
		'''
			digraph {
				1->2[headlabel="foobar"]
			}
		'''.assertEdgeTargetLabel("foobar")
	}

	@Test def edge_targetLabel002() {
		'''
			digraph {
				1->2[headlabel="foo\nbar"]
			}
		'''.assertEdgeTargetLabel("foo\nbar")
	}

	@Test def edge_targetLabel003() {
		'''
			digraph {
				1->2[headlabel="foo\nbar\nbaz"]
			}
		'''.assertEdgeTargetLabel("foo\nbar\nbaz")
	}

	@Test def edge_targetLabel004() {
		'''
			digraph testedGraphName {
				1->2[headlabel="g: \G e:\E h:\H t:\T"]
			}
		'''.assertEdgeTargetLabel("g: testedGraphName e:1->2 h:2 t:1")
	}

	@Test def edge_targetLabel005() {
		//test against null pointer exception
		'''
			digraph {
				1->2[headlabel="\G\L"]
			}
		'''.assertEdgeTargetLabel("")
	}

	@Test def edge_targetLabel006() {
		'''
			digraph {
				1->2[fontcolor=red headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_targetLabel007() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_targetLabel008() {
		'''
			digraph {
				edge[labelfontcolor=red]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_targetLabel009() {
		'''
			digraph {
				edge[labelfontcolor=blue, fontcolor=red]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-fill: #0000ff;
		''')
	}

	@Test def edge_targetLabel010() {
		mockAvailableFonts("Arial")
		'''
			digraph {
				edge[fontname=Arial]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-font-family: "Arial";
			-fx-font-weight: 400;
			-fx-font-style: normal;
		''')
	}

	@Test def edge_targetLabel011() {
		mockAvailableFonts("Liberation Serif", "Liberation Sans")
		'''
			digraph {
				edge[labelfontname="Times-BoldItalic", fontname=Helvetica]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-font-family: "Liberation Serif";
			-fx-font-weight: 700;
			-fx-font-style: italic;
		''')
	}

	@Test def edge_targetLabel012() {
		'''
			digraph {
				edge[fontsize=12]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-font-size: 12.0;
		''')
	}

	@Test def edge_targetLabel013() {
		'''
			digraph {
				edge[labelfontsize=14, fontsize=12]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-font-size: 14.0;
		''')
	}

	@Test def edge_targetLabel014() {
		mockAvailableFonts("Courier New")
		'''
			digraph {
				edge[labelfontsize=14, fontsize=12, fontcolor=green, fontname="Courier New"]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-fill: #00ff00;
			-fx-font-family: "Courier New";
			-fx-font-weight: 400;
			-fx-font-style: normal;
			-fx-font-size: 14.0;
		''')
	}

	@Test def edge_targetLabel015() {
		mockAvailableFonts("Serif")
		'''
			digraph {
				edge[fontname="Bookman-Light"]
				1->2[headlabel=h]
			}
		'''.assertEdgeTargetLabelCssStyle('''
			-fx-font-family: "serif";
			-fx-font-weight: 300;
			-fx-font-style: normal;
		''')
	}

	private def assertEdgeExternalLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.externalLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.labelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.sourceLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeTargetLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.targetLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.curveCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceDecorationStyles(CharSequence dotText, String... expected) {
		dotText.firstEdge.convert.sourceDecoration.assertEdgeDecorationStyles(expected)
	}

	private def assertEdgeTargetDecorationStyles(CharSequence dotText, String... expected) {
		dotText.firstEdge.convert.targetDecoration.assertEdgeDecorationStyles(expected)
	}

	private def assertEdgeLabel(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.label
		expected.assertEquals(actual)
	}

	private def assertEdgeExternalLabel(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.externalLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceLabel(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.sourceLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeTargetLabel(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.targetLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeDecorationStyles(javafx.scene.Node decoration, String... expectedStyles) {
		decoration.hasSameNumberOfDecorationStylesAs(expectedStyles)

		if(decoration instanceof Group) {
			for(var i=0; i<decoration.children.size; i++) {
				decoration.children.get(i).hasStyle(expectedStyles.get(i))
			}
		} else {
			decoration.hasStyle(expectedStyles.get(0))
		}
	}

	private def hasStyle(javafx.scene.Node decoration, String expectedStyle) {
		expectedStyle.assertEquals(decoration.style.split)
	}

	private def hasSameNumberOfDecorationStylesAs(javafx.scene.Node decoration, String... expected) {
		val numberOfActualDecorationStyles = if(decoration instanceof Group) decoration.children.size else 1
		val numberOfExpectedDecorationStyles = expected.size

		assertEquals(
			"The number of expected decoration styles does not match the number of actual decoration styles.",
			numberOfExpectedDecorationStyles, numberOfActualDecorationStyles
		)
	}

	private def firstEdge(CharSequence dotText) {
		dotText.graph.edges.head
	}

	private def graph(CharSequence dotText) {
		// ensure that the input text can be parsed and the ast can be created
		val dotAst = dotText.parse
		dotAst.assertNoErrors

		dotAst.importDot.get(0)
	}

	private def convert(Edge dotEdge) {
		val zestEdge = new Edge(new Node, new Node)
		dotEdge.copy(zestEdge)
		zestEdge
	}

	private def split(String text) {
		text.replaceAll(";", ";" + System.lineSeparator)
	}

	private def mockAvailableFonts(String... availableFonts) {
		fontUtil.systemFontAccess = new DotFontAccessMock(availableFonts)
	}
}