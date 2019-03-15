/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)     - initial implementation (bug #477980)
 *                                    - Add support for polygon-based node shapes (bug #441352)
 *                                    - modify grammar to allow empty attribute lists (bug #461506)
 *                                    - Add support for all dot attributes (bug #461506)
 *     Zoey Gerrit Prigge (itemis AG) - implement additional attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
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

import static extension org.eclipse.gef.dot.tests.DotTestUtils.content
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
	@Test def empty_string() { "".parse.assertNull }
	@Test def empty_graph() { DotTestGraphs.EMPTY.hasNoErrors }
	@Test def empty_directed_graph() { DotTestGraphs.EMPTY_DIRECTED.hasNoErrors }
	@Test def empty_strict_graph() { DotTestGraphs.EMPTY_STRICT.hasNoErrors }
	@Test def empty_strict_directed_graph() { DotTestGraphs.EMPTY_STRICT_DIRECTED.hasNoErrors }
	@Test def graph_with_one_node() { DotTestGraphs.ONE_NODE.hasNoErrors }
	@Test def graph_with_one_node_and_empty_node_attribute_list() {	DotTestGraphs.EMPTY_NODE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def graph_with_one_edge() { DotTestGraphs.ONE_EDGE.hasNoErrors }
	@Test def directed_graph_with_one_edge() { DotTestGraphs.ONE_DIRECTED_EDGE.hasNoErrors }
	@Test def graph_with_one_edge_and_empty_edge_attribute_list() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def directed_graph_with_one_edge_and_empty_edge_attribute_list() { DotTestGraphs.EMPTY_DIRECTED_EDGE_ATTRIBUTE_LIST.hasNoErrors }
	@Test def graph_with_empty_graph_attribute_statement() { DotTestGraphs.EMPTY_GRAPH_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def graph_with_empty_node_attribute_statement() { DotTestGraphs.EMPTY_NODE_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def graph_with_empty_edge_attribute_statement() { DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_STATEMENT.hasNoErrors }
	@Test def node_groups() { DotTestGraphs.NODE_GROUPS.hasNoErrors }
	@Test def global_edge_node_color_scheme() { DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.hasNoErrors }
	@Test def color_list_BGCOLOR_G() { DotTestGraphs.COLORLIST_BGCOLOR_G.hasNoErrors }
	@Test def color_list_BGCOLOR_C() { DotTestGraphs.COLORLIST_BGCOLOR_C.hasNoErrors }
	@Test def color_list_COLOR_E() { DotTestGraphs.COLORLIST_COLOR_E.hasNoErrors }
	@Test def color_list_FILLCOLOR_N() { DotTestGraphs.COLORLIST_FILLCOLOR_N.hasNoErrors }
	@Test def color_list_FILLCOLOR_C() { DotTestGraphs.COLORLIST_FILLCOLOR_C.hasNoErrors }

// Test cases with file input
	@Test def arrowshapes_deprecated() { "arrowshapes_deprecated.dot".dslFileHasNoError}
	@Test def arrowshapes_direction_both() { "arrowshapes_direction_both.dot".dslFileHasNoError }
	@Test def arrowshapes_invalid_modifiers() { "arrowshapes_invalid_modifiers.dot".dslFileHasNoError}
	@Test def arrowshapes_multiple() { "arrowshapes_multiple.dot".dslFileHasNoError }
	@Test def arrowshapes_single() { "arrowshapes_single.dot".dslFileHasNoError }
	@Test def clustered_graph() { "clustered_graph.dot".dslFileHasNoError } //
	@Test def color() { "color.dot".dslFileHasNoError }
	@Test def colored_graph() { "colored_graph.dot".dslFileHasNoError }
	@Test def colorscheme() { "colorscheme.dot".dslFileHasNoError }
	@Test def cpattl_pie() { "cpattl-pie.dot".dslFileHasNoError }
	@Test def er() { "er.dot".dslFileHasNoError }
	@Test def fancy_graph() { "fancy_graph.dot".dslFileHasNoError }
	@Test def fontname_fontsize() {"fontname_fontsize.dot".dslFileHasNoError}
	@Test def grdangles() { "grdangles.dot".dslFileHasNoError }
	@Test def grdcluster() { "grdcluster.dot".dslFileHasNoError }
	@Test def grdcolors() { "grdcolors.dot".dslFileHasNoError }
	@Test def grdfillcolor() { "grdfillcolor.dot".dslFileHasNoError }
	@Test def grdlinear_angle() { "grdlinear_angle.dot".dslFileHasNoError }
	@Test def grdlinear_node() { "grdlinear_node.dot".dslFileHasNoError }
	@Test def grdlinear() { "grdlinear.dot".dslFileHasNoError }
	@Test def grdradial_angle() { "grdradial_angle.dot".dslFileHasNoError }
	@Test def grdradial_node() { "grdradial_node.dot".dslFileHasNoError }
	@Test def grdradial() { "grdradial.dot".dslFileHasNoError }
	@Test def grdshapes() { "grdshapes.dot".dslFileHasNoError }
	@Test def html_like_labels1() { "html_like_labels1.dot".dslFileHasNoError }
	@Test def html_like_labels2() { "html_like_labels2.dot".dslFileHasNoError }
	@Test def html_like_labels3() { "html_like_labels3.dot".dslFileHasNoError }
	@Test def html_like_labels4() { "html_like_labels4.dot".dslFileHasNoError }
	@Test def labeled_graph() { "labeled_graph.dot".dslFileHasNoError }
	@Test def nodeshapes_polygon_based() { "nodeshapes_polygon_based.dot".dslFileHasNoError }
	@Test def penwidth() { "penwidth.dot".dslFileHasNoError }
	@Test def philo() { "philo.dot".dslFileHasNoError }
	@Test def record_shape_node1() { "record_shape_node1.dot".dslFileHasNoError }
	@Test def simple_digraph() { "simple_digraph.dot".dslFileHasNoError }
	@Test def simple_graph() { "simple_graph.dot".dslFileHasNoError }
	@Test def styled_graph() { "styled_graph.dot".dslFileHasNoError }
	@Test def styled_graph2() { "styled_graph2.dot".dslFileHasNoError }
	@Test def switch_() { "switch.dot".dslFileHasNoError }

// Test cases with multi-line templates
	@Test def graph_color_with_custom_color_scheme() {
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

	@Test def node_color_with_custom_color_scheme() {
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

	@Test def edge_color_with_custom_color_scheme() {
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

	@Test def color_case_insensitivity() {
		'''
			graph {
				1[color=Red, fontcolor=BLUE]
			}
		'''.hasNoErrors
	}

	@Test def compass_pt_as_id() {
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

	@Test def record_label_mixed_with_html_label() {
		'''
			digraph {
				1[
					shape=Mrecord
					label=<<b>text</b>>
				]
			}
		'''.hasNoErrors
	}

	@Test def empty_arrowhead() {
		'''
			digraph {
				1->2 [arrowhead=""]
			}
		'''.hasNoErrors
	}

	@Test def empty_arrowtail() {
		'''
			digraph {
				1->2 [arrowtail=""]
			}
		'''.hasNoErrors
	}

	@Test def empty_edge_dir() {
		'''
			digraph {
				1->2 [dir=""]
			}
		'''.hasNoErrors
	}

	@Test def zero_nodesep() {
		'''
			digraph {
				nodesep=0
				1
				2
			}
		'''.hasNoErrors
	}

	@Test def subgraph_colorscheme() {
		'''
			graph {
				subgraph cluster_0 {
					colorscheme=accent3 color=2
					1
				}
			}
		'''.hasNoErrors
	}

	@Test def cluster_style() {
		
		for(validClusterStyle : #["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped",
			"bold, dashed", "bold, filled", "bold, rounded", "bold, striped", "filled, dashed"]) {
			'''
				graph {
					subgraph cluster_0 {
						style="«validClusterStyle»"
						1
					}
				}
			'''.hasNoErrors
		}
	}

// Test cases with parameterized multi-line templates
	@Test def cluster_label_html_like() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.CLUSTER_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def edgehead_label_html_like() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_HEADLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def edge_label_html_like() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def edge_tail_label_html_like() { dotTestHtmlLikeLabels.forEach[ DotTestGraphs.EDGE_TAILLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def edge_xlabel_html_like() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.EDGE_XLABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def graph_label_html_like() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.GRAPH_LABEL_HTML_LIKE(it).hasNoErrors]}
	@Test def node_label_html_like() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_LABEL_HTML_LIKE(it).hasNoErrors] }
	@Test def node_xlabel_html_like() { dotTestHtmlLikeLabels.forEach[DotTestGraphs.NODE_XLABEL_HTML_LIKE(it).hasNoErrors] }

	private def hasNoErrors(CharSequence text) {
		text.toString.hasNoErrors
	}

	private def dslFileHasNoError(String fileName) {
		fileName.content.hasNoErrors
	}

	private def hasNoErrors(String text) {
		text.parse.assertNoErrors
	}

	private def static initializeDotTestHtmlLikeLabels() {
		dotTestHtmlLikeLabels = newLinkedList
		for (field : DotTestHtmlLabels.declaredFields) {
			val dotTestHtmlLikeLabel = field.get(null) as String
			dotTestHtmlLikeLabels.add(dotTestHtmlLikeLabel)
		}
	}
}
