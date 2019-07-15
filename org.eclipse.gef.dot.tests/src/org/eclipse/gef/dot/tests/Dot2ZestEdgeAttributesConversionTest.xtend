/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
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
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.conversion.Dot2ZestAttributesConverter
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.zest.fx.ZestProperties.*
import static extension org.junit.Assert.*
import org.junit.Ignore

/*
 * Test cases for the {@link Dot2ZestAttributesConverter#convertAttributes(Edge, Edge)} method.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class Dot2ZestEdgeAttributesConversionTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	//@Rule
	//public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	extension DotImport = new DotImport
	extension Dot2ZestAttributesConverter = new Dot2ZestAttributesConverter

	@Test def edge_id001() {
		'''
			digraph {
				1->2 [id="ID"]
			}
		'''.assertEdgeCssId('''ID''')
	}

	@Test def edge_id002() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeCssId(null)
	}

	@Test def edge_labelTooltip001() {
		'''
			graph {
				1--2 [labeltooltip="test"]
			}
		'''.assertEdgeLabelTooltip("test")
	}

	@Test def edge_labelTooltip002() {
		'''
			graph {
				1--2 [labeltooltip="\E"]
			}
		'''.assertEdgeLabelTooltip("1--2")
	}

	@Test def edge_labelTooltip003() {
		'''
			graph {
				1--2 [labeltooltip="Label tooltip of edge\n\E"]
			}
		'''.assertEdgeLabelTooltip('''
			Label tooltip of edge
			1--2'''
		)
	}

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

	@Test def edge_style010() {
		'''
			digraph {
				1->2 [style=invis]
			}
		'''.assertEdgeVisibility(false)
	}

	@Test def edge_style011() {
		'''
			digraph {
				1->2[color=red]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke: #ff0000;
		''')
	}

	@Test def edge_style012() {
		'''
			digraph {
				edge[color=red]
				1->2
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke: #ff0000;
		''')
	}

	@Test def edge_style013() {
		'''
			digraph {
				edge[color=red]
				1->2[color=""]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
		''')
	}

	@Test def edge_sourceDecoration001() {
		'''
			graph {
				1--2
			}
		'''.assertEdgeSourceDecoration(null) 
	}

	@Test def edge_sourceDecoration002() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeSourceDecoration(null)
	}

	@Test def edge_sourceDecoration003() {
		'''
			digraph {
				1->2[dir=both]
			}
		'''.assertEdgeSourceDecoration('''
			Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
		''')
	}

	@Test def edge_sourceDecoration004() {
		'''
			digraph {
				1->2[dir=both arrowsize=2.5]
			}
		'''.assertEdgeSourceDecoration('''
			Polygon[points=[0.0, 0.0, 25.0, -8.333333333333334, 25.0, 8.333333333333334], fill=0x000000ff]
		''')
	}

	@Test def edge_sourceDecoration005() {
		'''
			digraph {
				1->2[dir=both arrowtail=""]
			}
		'''.assertEdgeSourceDecoration('''
			Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
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

	@Test def edge_targetDecoration001() {
		'''
			graph {
				1--2
			}
		'''.assertEdgeTargetDecoration(null) 
	}

	@Test def edge_targetDecoration002() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeTargetDecoration('''
			Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
		''')
	}

	@Test def edge_targetDecoration003() {
		'''
			digraph {
				1->2[arrowsize=2.5]
			}
		'''.assertEdgeTargetDecoration('''
			Polygon[points=[0.0, 0.0, 25.0, -8.333333333333334, 25.0, 8.333333333333334], fill=0x000000ff]
		''')
	}
	
	@Test def edge_targetDecoration004() {
		'''
			digraph {
				1->2[arrowhead=""]
			}
		'''.assertEdgeTargetDecoration('''
			Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
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

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label001() {
		'''
			digraph {
				1->2[label="foobar"]
			}
		'''.assertEdgeLabel("foobar")
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label002() {
		'''
			digraph {
				1->2[label="foo\nbar"]
			}
		'''.assertEdgeLabel("foo\nbar")
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label003() {
		'''
			digraph {
				1->2[label="foo\nbar\nbaz"]
			}
		'''.assertEdgeLabel("foo\nbar\nbaz")
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label004() {
		'''
			digraph {
				1->2[label="foo\lbar\rbaz"]
			}
		'''.assertEdgeLabel("foo\nbar\nbaz")
	}

	@Test def edge_label005() {
		'''
			digraph {
				1->2[label="foo\nbar\nbaz\n\n"]
			}
		'''.assertEdgeLabel("foo\nbar\nbaz\n")
	}

	@Test def edge_label006() {
		'''
			digraph {
				1->2[label="foo\nbar\nbaz\r"]
			}
		'''.assertEdgeLabel("foo\nbar\nbaz")
	}

	@Test def edge_label007() {
		'''
			digraph {
				1->2[label="Test \E"]
			}
		'''.assertEdgeLabel("Test 1->2")
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label008() {
		'''
			digraph samplegraph {
				1->2[label="\E \G"]
			}
		'''.assertEdgeLabel("1->2 samplegraph")
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label009() {
		'''
			digraph {
				1->2[fontcolor=red label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label010() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label011() {
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

	@Ignore("Needs FX initialization (Failing on Travis/Jenkins)")
	@Test def edge_label012() {
		'''
			digraph {
				edge[fontsize=16]
				1->2[label=l]
			}
		'''.assertEdgeLabelCssStyle('''
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_label013() {
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
			digraph {
				1->2[xlabel="foo\lbar\rbaz"]
			}
		'''.assertEdgeExternalLabel("foo\nbar\nbaz")
	}

	@Test def edge_externalLabel005() {
		'''
			digraph {
				1->2[xlabel="foo\lbar\nbaz\n"]
			}
		'''.assertEdgeExternalLabel("foo\nbar\nbaz")
	}

	@Test def edge_externalLabel006() {
		'''
			digraph testedGraphName {
				1->2[xlabel="g: \G e:\E h:\H t:\T"]
			}
		'''.assertEdgeExternalLabel("g: testedGraphName e:1->2 h:2 t:1")
	}

	@Test def edge_externalLabel007() {
		'''
			digraph {
				1->2[fontcolor=red xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_externalLabel008() {
		'''
			digraph {
				edge[fontcolor=red]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-fill: #ff0000;
		''')
	}

	@Test def edge_externalLabel009() {
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

	@Test def edge_externalLabel010() {
		'''
			digraph {
				edge[fontsize=16]
				1->2[xlabel=x]
			}
		'''.assertEdgeExternalLabelCssStyle('''
			-fx-font-size: 16.0;
		''')
	}

	@Test def edge_externalLabel011() {
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
				1->2[taillabel="foo\lbar\rbaz\n"]
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
				1->2[headlabel="foo\lbar\rbaz\n"]
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

	@Test def edge_tooltip001() {
		'''
			digraph {
				1->2 [tooltip="test"]
			}
		'''.assertEdgeTooltip("test")
	}

	@Test def edge_tooltip002() {
		'''
			digraph {
				1->2 [tooltip="\E"]
			}
		'''.assertEdgeTooltip("1->2")
	}

	@Test def edge_tooltip003() {
		'''
			digraph {
				1->2 [tooltip="Edge tooltip of\n \E"]
			}
		'''.assertEdgeTooltip('''
			Edge tooltip of
			 1->2'''
		)
	}

	private def assertEdgeCssId(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.cssId
		expected.assertEquals(actual)
	}

	private def assertEdgeExternalLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.externalLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.labelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.sourceLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeTargetLabelCssStyle(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.targetLabelCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeStyle(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.curveCssStyle.split
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceDecoration(CharSequence dotText, String expected) {
		dotText.convertFirstEdge.sourceDecoration.assertEdgeDecoration(expected)
	}

	private def assertEdgeSourceDecorationStyles(CharSequence dotText, String... expected) {
		dotText.convertFirstEdge.sourceDecoration.assertEdgeDecorationStyles(expected)
	}

	private def assertEdgeTargetDecoration(CharSequence dotText, String expected) {
		dotText.convertFirstEdge.targetDecoration.assertEdgeDecoration(expected)
	}

	private def assertEdgeTargetDecorationStyles(CharSequence dotText, String... expected) {
		dotText.convertFirstEdge.targetDecoration.assertEdgeDecorationStyles(expected)
	}

	private def assertEdgeLabel(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.label
		expected.assertEquals(actual)
	}

	private def assertEdgeExternalLabel(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.externalLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeSourceLabel(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.sourceLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeTargetLabel(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.targetLabel
		expected.assertEquals(actual)
	}

	private def assertEdgeDecoration(javafx.scene.Node decoration, String expected) {
		if (expected === null) {
			decoration.assertNull
		} else {
			val actual = decoration.toString 
			expected.trim.assertEquals(actual)
		}
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

	private def assertEdgeVisibility(CharSequence dotText, boolean expected) {
		val invisible = dotText.convertFirstEdge.invisible
		// assert on visibility, not on invisibility
		val actual = if (invisible===null) true else !invisible
		expected.assertEquals(actual)
	}

	private def assertEdgeTooltip(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.tooltip
		expected.assertEquals(actual)
	}

	private def assertEdgeLabelTooltip(CharSequence dotText, String expected) {
		val actual = dotText.convertFirstEdge.labelTooltip
		expected.assertEquals(actual)
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

	private def convertFirstEdge(CharSequence dotText) {
		dotText.graph.edges.head.convert
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