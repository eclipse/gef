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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotParserTests {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule
	
	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	static List<String> dotTestHtmlLikeLabels

	@BeforeClass
	def static before() {
		initializeDotTestHtmlLikeLabels
	}

// Test cases with string input
	@Test def testEmptyString() { "".parse.assertNull }
	@Test def testEmptyGraph() { DotTestGraphs.EMPTY.hasNoErrors }
	@Test def testEmptyDirectedGraph() { DotTestGraphs.EMPTY_DIRECTED.hasNoErrors }
	@Test def testEmptyStrictGraph() { DotTestGraphs.EMPTY_STRICT.hasNoErrors } 
	@Test def testEmptyStrictDirectedGraph() { DotTestGraphs.EMPTY_STRICT_DIRECTED.hasNoErrors }
	@Test def testGraphWithOneNode() { DotTestGraphs.ONE_NODE.hasNoErrors }
	@Test def testGraphWithOneNodeAndEmptyNodeAttributeList() {	DotTestGraphs.EMPTY_NODE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def testGraphWithOneEdge() { DotTestGraphs.ONE_EDGE.hasNoErrors }
	@Test def testDirectedGraphWithOneEdge() { DotTestGraphs.ONE_DIRECTED_EDGE.hasNoErrors }
	@Test def testGraphWithOneEdgeAndEmptyEdgeAttributeList() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def testDirectedGraphWithOneEdgeAndEmptyEdgeAttributeList() { DotTestGraphs.EMPTY_DIRECTED_EDGE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def testGraphWithEmptyGraphAttributeStatement() { DotTestGraphs.EMPTY_GRAPH_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def testGraphWithEmptyNodeAttributeStatement() { DotTestGraphs.EMPTY_NODE_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def testGraphWithEmptyEdgeAttributeStatement() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def testNodeGroups() { DotTestGraphs.NODE_GROUPS.hasNoErrors }
	@Test def testGlobalEdgeNodeColorScheme() { DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.hasNoErrors }
	@Test def testColorList_BGCOLOR_G() { DotTestGraphs.COLORLIST_BGCOLOR_G.hasNoErrors }
	@Test def testColorList_BGCOLOR_C() { DotTestGraphs.COLORLIST_BGCOLOR_C.hasNoErrors }
	@Test def testColorList_COLOR_E() { DotTestGraphs.COLORLIST_COLOR_E.hasNoErrors }
	@Test def testColorList_FILLCOLOR_N() { DotTestGraphs.COLORLIST_FILLCOLOR_N.hasNoErrors }
	@Test def testColorList_FILLCOLOR_C() { DotTestGraphs.COLORLIST_FILLCOLOR_C.hasNoErrors }

// Test cases with file input
	@Test def testArrowShapesDeprecated() { "arrowshapes_deprecated.dot".dslFileHasNoError}
	@Test def testArrowShapesDirectionBoth() { "arrowshapes_direction_both.dot".dslFileHasNoError }
	@Test def testArrowShapesInvalidModifiers() { "arrowshapes_invalid_modifiers.dot".dslFileHasNoError}
	@Test def testArrowShapesMultiple() { "arrowshapes_multiple.dot".dslFileHasNoError }
	@Test def testArrowShapesSingle() { "arrowshapes_single.dot".dslFileHasNoError }
	@Test def testClusteredGraph() { "clustered_graph.dot".dslFileHasNoError } //
	@Test def testColor() { "color.dot".dslFileHasNoError }
	@Test def testColoredGraph() { "colored_graph.dot".dslFileHasNoError }
	@Test def testColorSchemeGraph() { "colorscheme.dot".dslFileHasNoError }
	@Test def testCpattlPie() { "cpattl-pie.dot".dslFileHasNoError }
	@Test def testER() { "er.dot".dslFileHasNoError }
	@Test def testFancyGraph() { "fancy_graph.dot".dslFileHasNoError }
	@Test def testGrdangles() { "grdangles.dot".dslFileHasNoError }
	@Test def testGrdcluster() { "grdcluster.dot".dslFileHasNoError }
	@Test def testGrdcolors() { "grdcolors.dot".dslFileHasNoError }
	@Test def testGrdfillcolor() { "grdfillcolor.dot".dslFileHasNoError }
	@Test def testGrdlinear_angle() { "grdlinear_angle.dot".dslFileHasNoError }
	@Test def testGrdlinear_node() { "grdlinear_node.dot".dslFileHasNoError }
	@Test def testGrdlinear() { "grdlinear.dot".dslFileHasNoError }
	@Test def testGrdradial_angle() { "grdradial_angle.dot".dslFileHasNoError }
	@Test def testGrdradial_node() { "grdradial_node.dot".dslFileHasNoError }
	@Test def testGrdradial() { "grdradial.dot".dslFileHasNoError }
	@Test def testGrdshapes() { "grdshapes.dot".dslFileHasNoError }
	@Test def testHtmlLikeLabels1() { "html_like_labels1.dot".dslFileHasNoError }
	@Test def testHtmlLikeLabels2() { "html_like_labels2.dot".dslFileHasNoError }
	@Test def testHtmlLikeLabels3() { "html_like_labels3.dot".dslFileHasNoError }
	@Test def testHtmlLikeLabels4() { "html_like_labels4.dot".dslFileHasNoError }
	@Test def testLabeledGraph() { "labeled_graph.dot".dslFileHasNoError }
	@Test def testNodeShapesPolygonBased() { "nodeshapes_polygon_based.dot".dslFileHasNoError}
	@Test def testPhilo() { "philo.dot".dslFileHasNoError }
	@Test def testRecordShapeNode1() { "record_shape_node1.dot".dslFileHasNoError }
	@Test def testSimpleDigraph() { "simple_digraph.dot".dslFileHasNoError }
	@Test def testSimpleGraph() { "simple_graph.dot".dslFileHasNoError }
	@Test def testStyledGraph() { "styled_graph.dot".dslFileHasNoError }
	@Test def testStyledGraph2() { "styled_graph2.dot".dslFileHasNoError }
	@Test def testSwitch() { "switch.dot".dslFileHasNoError }

// Test cases with multi-line templates	
	@Test def testGraphColorWithCustomColorScheme() {
		'''
			graph {
				graph[colorscheme=brbg10]
				bgcolor=5
				1
			}
		'''.hasNoErrors
		
		'''
			graph {
				colorscheme=brbg10
				bgcolor=5
				1
			}
		'''.hasNoErrors
	}

	@Test def testNodeColorWithCustomColorScheme() {
		'''
			graph {
				node[colorscheme=brbg10]
				1[color=5]
			}
		'''.hasNoErrors
		
		'''
			graph {
				1[colorscheme=brbg10 color=5]
			}
		'''.hasNoErrors
	}

	@Test def testEdgeColorWithCustomColorScheme() {
		'''
			graph {
				edge[colorscheme=brbg10]
				1--2[color=5]
			}
		'''.hasNoErrors
		
		'''
			graph {
				1--2[color=5 colorscheme=brbg10]
			}
		'''.hasNoErrors
	}

	@Test def testColorCaseInsensitivity() {
		'''
			graph {
				1[color=Red, fontcolor=BLUE]
			}
		'''.hasNoErrors
	}

	@Test def testCompassPtAsID() {
		// it is legal to have a portname the same as one of the compass points.
		'''
			digraph {
				y[shape=record label="{<c>C|<d>D}"]
				y:d:n->y:c:n
				}
		'''.hasNoErrors

		'''graph{ y:n:n }'''.hasNoErrors
		'''graph{ y:ne:n }'''.hasNoErrors
		'''graph{ y:e:n }'''.hasNoErrors
		'''graph{ y:se:n }'''.hasNoErrors
		'''graph{ y:s:n }'''.hasNoErrors
		'''graph{ y:sw:n }'''.hasNoErrors
		'''graph{ y:w:n }'''.hasNoErrors
		'''graph{ y:nw:n }'''.hasNoErrors
		'''graph{ y:c:n }'''.hasNoErrors
		'''graph{ y:_:n }'''.hasNoErrors
	}

	@Test def testRecordLabelMixedWithHtmlLabel() {
		'''
			digraph {
				1[
					shape=Mrecord
					label=<<b>text</b>>
				]
			}
		'''.hasNoErrors
	}

	@Test def testEmptyArrowhead() {
		'''
			digraph {
				1->2 [arrowhead=""]
			}
		'''.hasNoErrors
	}

	@Test def testEmptyArrowtail() {
		'''
			digraph {
				1->2 [arrowtail=""]
			}
		'''.hasNoErrors
	}

	@Test def testEmptyEdgeDir() {
		'''
			digraph {
				1->2 [dir=""]
			}
		'''.hasNoErrors
	}

	@Test def testNodesepIsZero() {
		'''
			digraph {
				nodesep=0
				1
				2
			}
		'''.hasNoErrors
	}

// Test cases with parameterized multi-line templates
	@Test def testClusterLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.CLUSTER_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testEdgeHeadLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_HEADLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testEdgeLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testEdgeTailLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_TAILLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testEdgeXLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_XLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testGraphLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.GRAPH_LABEL_HTML_LIKE(it).hasNoErrors]}
	@Test def testNodeLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def testNodeXLabelHTMLLike() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_XLABEL_HTML_LIKE(it).hasNoErrors] }

	private def hasNoErrors(CharSequence text) {
		text.toString.hasNoErrors
	}

	private def dslFileHasNoError(String fileName) {
		fileName.file.read.hasNoErrors
	}

	private def hasNoErrors(String text) {
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
