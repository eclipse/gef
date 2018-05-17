/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation (bug #477980)
 *                                - Add support for polygon-based node shapes (bug #441352)
 *                                - modify grammar to allow empty attribute lists (bug #461506)
 *                                - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.io.File
import java.util.List
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotParserTests {

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	static List<String> dotTestHtmlLikeLabels

	@BeforeClass
	def static before() {
		DotTestUtils.registerDotSubgrammarPackages()
		initializeDotTestHtmlLikeLabels
	}

// Test cases with string input
	@Test def testEmptyString() { "".parse.assertNull }
	@Test def testEmptyGraph() { DotTestGraphs.EMPTY.verify }
	@Test def testEmptyDirectedGraph() { DotTestGraphs.EMPTY_DIRECTED.verify }
	@Test def testEmptyStrictGraph() { DotTestGraphs.EMPTY_STRICT.verify } 
	@Test def testEmptyStrictDirectedGraph() { DotTestGraphs.EMPTY_STRICT_DIRECTED.verify }
	@Test def testGraphWithOneNode() { DotTestGraphs.ONE_NODE.verify }
	@Test def testGraphWithOneNodeAndEmptyNodeAttributeList() {	DotTestGraphs.EMPTY_NODE_ATTRIBUTE_LIST.verify }
	@Test def testGraphWithOneEdge() { DotTestGraphs.ONE_EDGE.verify }
	@Test def testDirectedGraphWithOneEdge() { DotTestGraphs.ONE_DIRECTED_EDGE.verify }
	@Test def testGraphWithOneEdgeAndEmptyEdgeAttributeList() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_LIST.verify }
	@Test def testDirectedGraphWithOneEdgeAndEmptyEdgeAttributeList() { DotTestGraphs.EMPTY_DIRECTED_EDGE_ATTRIBUTE_LIST.verify }
	@Test def testGraphWithEmptyGraphAttributeStatement() { DotTestGraphs.EMPTY_GRAPH_ATTRIBUTE_STATEMENT.verify }
	@Test def testGraphWithEmptyNodeAttributeStatement() { DotTestGraphs.EMPTY_NODE_ATTRIBUTE_STATEMENT.verify }
	@Test def testGraphWithEmptyEdgeAttributeStatement() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_STATEMENT.verify }
	@Test def testNodeGroups() { DotTestGraphs.NODE_GROUPS.verify }
	@Test def testGlobalEdgeNodeColorScheme() { DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.verify }
	@Test def testColorList_BGCOLOR_G() { DotTestGraphs.COLORLIST_BGCOLOR_G.verify }
	@Test def testColorList_BGCOLOR_C() { DotTestGraphs.COLORLIST_BGCOLOR_C.verify }
	@Test def testColorList_COLOR_E() { DotTestGraphs.COLORLIST_COLOR_E.verify }
	@Test def testColorList_FILLCOLOR_N() { DotTestGraphs.COLORLIST_FILLCOLOR_N.verify }
	@Test def testColorList_FILLCOLOR_C() { DotTestGraphs.COLORLIST_FILLCOLOR_C.verify }

// Test cases with file input
	@Test def testArrowShapesDeprecated() { "arrowshapes_deprecated.dot".verifyFile}
	@Test def testArrowShapesDirectionBoth() { "arrowshapes_direction_both.dot".verifyFile }
	@Test def testArrowShapesInvalidModifiers() { "arrowshapes_invalid_modifiers.dot".verifyFile}
	@Test def testArrowShapesMultiple() { "arrowshapes_multiple.dot".verifyFile }
	@Test def testArrowShapesSingle() { "arrowshapes_single.dot".verifyFile }
	@Test def testClusteredGraph() { "clustered_graph.dot".verifyFile } //
	@Test def testColor() { "color.dot".verifyFile }
	@Test def testColoredGraph() { "colored_graph.dot".verifyFile }
	@Test def testColorSchemeGraph() { "colorscheme.dot".verifyFile }
	@Test def testCpattlPie() { "cpattl-pie.dot".verifyFile }
	@Test def testER() { "er.dot".verifyFile }
	@Test def testFancyGraph() { "fancy_graph.dot".verifyFile }
	@Test def testGrdangles() { "grdangles.dot".verifyFile }
	@Test def testGrdcluster() { "grdcluster.dot".verifyFile }
	@Test def testGrdcolors() { "grdcolors.dot".verifyFile }
	@Test def testGrdfillcolor() { "grdfillcolor.dot".verifyFile }
	@Test def testGrdlinear_angle() { "grdlinear_angle.dot".verifyFile }
	@Test def testGrdlinear_node() { "grdlinear_node.dot".verifyFile }
	@Test def testGrdlinear() { "grdlinear.dot".verifyFile }
	@Test def testGrdradial_angle() { "grdradial_angle.dot".verifyFile }
	@Test def testGrdradial_node() { "grdradial_node.dot".verifyFile }
	@Test def testGrdradial() { "grdradial.dot".verifyFile }
	@Test def testGrdshapes() { "grdshapes.dot".verifyFile }
	@Test def testHtmlLikeLabels1() { "html_like_labels1.dot".verifyFile }
	@Test def testHtmlLikeLabels2() { "html_like_labels2.dot".verifyFile }
	@Test def testHtmlLikeLabels3() { "html_like_labels3.dot".verifyFile }
	@Test def testHtmlLikeLabels4() { "html_like_labels4.dot".verifyFile }
	@Test def testLabeledGraph() { "labeled_graph.dot".verifyFile }
	@Test def testNodeShapesPolygonBased() { "nodeshapes_polygon_based.dot".verifyFile}
	@Test def testPhilo() { "philo.dot".verifyFile }
	@Test def testRecordShapeNode1() { "record_shape_node1.dot".verifyFile }
	@Test def testSimpleDigraph() { "simple_digraph.dot".verifyFile }
	@Test def testSimpleGraph() { "simple_graph.dot".verifyFile }
	@Test def testStyledGraph() { "styled_graph.dot".verifyFile }
	@Test def testStyledGraph2() { "styled_graph2.dot".verifyFile }
	@Test def testSwitch() { "switch.dot".verifyFile }

// Test cases with multi-line templates	
	@Test def testGraphColorWithCustomColorScheme() { 
		'''
			graph {
				graph[colorscheme=brbg10]
				bgcolor=5
				1
			}
		'''.verify
		
		'''
			graph {
				colorscheme=brbg10
				bgcolor=5
				1
			}
		'''.verify
	}

	@Test def testNodeColorWithCustomColorScheme() {
		'''
			graph {
				node[colorscheme=brbg10]
				1[color=5]
			}
		'''.verify
		
		'''
			graph {
				1[colorscheme=brbg10 color=5]
			}
		'''.verify
	}

	@Test def testEdgeColorWithCustomColorScheme() {
		'''
			graph {
				edge[colorscheme=brbg10]
				1--2[color=5]
			}
		'''.verify
		
		'''
			graph {
				1--2[color=5 colorscheme=brbg10]
			}
		'''.verify
	}

	@Test def testColorCaseInsensitivity() { 
		'''
			graph {
				1[color=Red, fontcolor=BLUE]
			}
		'''.verify
	}

	@Test def testCompassPtAsID() {
		// it is legal to have a portname the same as one of the compass points.
		'''
			digraph {
				y[shape=record label="{<c>C|<d>D}"]
				y:d:n->y:c:n
				}
		'''.verify

		'''graph{ y:n:n }'''.verify
		'''graph{ y:ne:n }'''.verify
		'''graph{ y:e:n }'''.verify
		'''graph{ y:se:n }'''.verify
		'''graph{ y:s:n }'''.verify
		'''graph{ y:sw:n }'''.verify
		'''graph{ y:w:n }'''.verify
		'''graph{ y:nw:n }'''.verify
		'''graph{ y:c:n }'''.verify
		'''graph{ y:_:n }'''.verify
	}

	@Test def testRecordLabelMixedWithHtmlLabel() {
		'''
			digraph {
				1[
					shape=Mrecord
					label=<<b>text</b>>
				]
			}
		'''.verify
	}

	@Test def testEmptyArrowhead() {
		'''
			digraph {
				1->2 [arrowhead=""]
			}
		'''.verify
	}

	@Test def testEmptyArrowtail() {
		'''
			digraph {
				1->2 [arrowtail=""]
			}
		'''.verify
	}

	@Test def testEmptyEdgeDir() {
		'''
			digraph {
				1->2 [dir=""]
			}
		'''.verify
	}

	@Test def testNodesepIsZero() {
		'''
			digraph {
				nodesep=0
				1
				2
			}
		'''.verify
	}

// Test cases with parameterized multi-line templates
	@Test def testClusterLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.CLUSTER_LABEL_HTML_LIKE(it).verify] }
	@Test def testEdgeHeadLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_HEADLABEL_HTML_LIKE(it).verify] }
	@Test def testEdgeLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_LABEL_HTML_LIKE(it).verify] }
	@Test def testEdgeTailLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_TAILLABEL_HTML_LIKE(it).verify] }
	@Test def testEdgeXLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_XLABEL_HTML_LIKE(it).verify] }
	@Test def testGraphLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.GRAPH_LABEL_HTML_LIKE(it).verify]}
	@Test def testNodeLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_LABEL_HTML_LIKE(it).verify] }
	@Test def testNodeXLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_XLABEL_HTML_LIKE(it).verify] }

	private def verify(CharSequence text) {
		text.toString.verify
	}

	private def verifyFile(String fileName) {
		fileName.file.read.verify
	}

	private def verify(String text) {
		text.parse.assertNoErrors
	}

	private def file(String fileName) {
		new File(DotTestUtils.RESOURCES_TESTS + fileName)
	}

	private def static initializeDotTestHtmlLikeLabels() {
		dotTestHtmlLikeLabels = newLinkedList
		for (field : DotTestHtmlLabels.declaredFields) {
			val dotTestHtmlLikeLabel = field.get(null) as String
			dotTestHtmlLikeLabels.add(dotTestHtmlLikeLabel)
		}
	}
}
