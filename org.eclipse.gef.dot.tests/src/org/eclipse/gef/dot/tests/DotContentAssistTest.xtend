/*******************************************************************************
 * Copyright (c) 2016, 2021 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy     (itemis AG) - initial implementation (bug #498324)
 *     Zoey Gerrit Prigge (itemis AG) - Add support for additional dot attributes (bug #461506)
 *                                    - Improve CA support for quoted attributes (bug #545801)
 *                                    - Add DotFontName content assist support (bug #542663)
 *                                    - Add cluster content assist support (bug #547639)
 *                                    - Implement subgraph template proposals (bug #547841)
 *                                    - Implement CA support for listing styles (bug #549393)
 *                                    - move expected color/font arrays to DotTestUtils for reuse
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Injector
import java.util.List
import org.eclipse.gef.dot.internal.ui.language.contentassist.DotProposalProvider
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.swt.widgets.Shell
import org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration
import org.eclipse.xtext.ui.editor.model.IXtextDocument
import org.eclipse.xtext.ui.testing.AbstractContentAssistTest
import org.eclipse.xtext.ui.testing.ContentAssistProcessorTestBuilder
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.tests.DotTestUtils.*
import static org.junit.Assert.fail

/**
 * Test cases for the {@link DotProposalProvider} class.
 */
@SuppressWarnings("restriction")
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotContentAssistTest extends AbstractContentAssistTest {
	// cursor position marker
	val c = '''<|>'''

	@BeforeClass def static void initializeStatusHandlerRegistry() {
		/**
		 * Initialize the
		 * {@link org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry}
		 * before executing the test cases, otherwise it will be initialized
		 * after the test case executions resulting in a NullPointerException.
		 * For more information, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=460996
		 */
		StatusHandlerRegistry.^default
	}

	@Test def empty() {
		'''
			«c»
		'''.testContentAssistant(#[ "strict", "graph", "digraph", "graph - Insert a template", "digraph - Insert a template"], "strict", '''
			strict
		''')

		'''
			«c»
		'''.testContentAssistant(#["strict", "graph", "digraph", "graph - Insert a template", "digraph - Insert a template"], "graph - Insert a template", '''
			graph {
				
			}
		''')

		'''
			«c»
		'''.testContentAssistant(#["strict", "graph", "digraph", "graph - Insert a template", "digraph - Insert a template"], "digraph - Insert a template", '''
			digraph {
				
			}
		''')
	}

	@Test def strict() {
		'''
			strict «c»
		'''.testContentAssistant(#["graph", "digraph"], "digraph", '''
			strict digraph
		''')
	}

	@Test def compass_pt_with_portID() {
		'''
			graph {
				1:portID:«c»
			}
		'''.testContentAssistant(#[":", "n", "ne", "e", "se", "s", "sw", "w", "nw", "c", "_"], "ne", '''
			graph {
				1:portID:ne
			}
		''')
	}

	@Test def compass_pt_without_portID() {
		'''
			graph {
				1:«c»
			}
		'''.testContentAssistant(#[":", "n", "ne", "e", "se", "s", "sw", "w", "nw", "c", "_"], "nw", '''
			graph {
				1:nw
			}
		''')
	}

	@Test def graph_multi_edge() {
		'''
			graph {
				1--2«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "[", "cluster - Insert a template", "edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"], "--", '''
			graph {
				1--2--
			}
		''')
	}

	@Test def digraph_multi_edge() {
		'''
			digraph {
				1->2«c»
			}
		'''.testContentAssistant(#["->", ":", ";", "[", "cluster - Insert a template", "edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"], "->", '''
			digraph {
				1->2->
			}
		''')
	}

	@Test def cluster_attribute_statement001() {
		'''
			graph {
				subgraph cluster_0 {
					«c»
				}
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "color", "colorscheme", "fillcolor", "fontcolor", "fontname", "fontsize",
									"id", "label", "lp", "penwidth", "style", "tooltip", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "edge - Insert a template"], "graph[]", '''
			graph {
				subgraph cluster_0 {
					graph[]
				}
			}
		''')
	}

	@Test def cluster_attribute_statement002() {
		'''
			graph {
				subgraph cluster_0 {
					1--2
					«c»
				}
			}
		'''.testContentAssistant(#["--", ":", ";", "[", "cluster - Insert a template", "edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template","{","}"], "graph[]", '''
			graph {
				subgraph cluster_0 {
					1--2
					graph[]
				}
			}
		''')
	}

	@Test def cluster_attributes() {
		// test global attribute names
		'''
			graph {
				subgraph cluster_0 {
					graph[«c»]
				}
			}
		'''.testContentAssistant(#["]", "bb", "bgcolor", "color", "colorscheme", "fillcolor", "fontcolor", "fontname", "fontsize",
									"id", "label", "lp", "penwidth", "style", "tooltip"], "fontcolor",
		'''
			graph {
				subgraph cluster_0 {
					graph[fontcolor=]
				}
			}
		''')

		// test local attribute names
		'''
			graph {
				subgraph cluster_0 {
					«c»
				}
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "color", "colorscheme", "fillcolor", "fontcolor", "fontname", "fontsize",
									"id", "label", "lp", "penwidth", "style", "tooltip", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "edge - Insert a template"], "tooltip",
		'''
			graph {
				subgraph cluster_0 {
					tooltip=
				}
			}
		''')

		// test local attribute names with prefix
		'''
			graph {
				subgraph cluster_0 {
					co«c»
				}
			}
		'''.testContentAssistant(#["color", "colorscheme", "--", ":", ";", "=", "[", "{", "}", "edge - Insert a template"], "color", '''
			graph {
				subgraph cluster_0 {
					color=
				}
			}
		''')

		'''
			graph {
				subgraph cluster_0 {
					co«c»
				}
			}
		'''.testContentAssistant(#["color", "colorscheme", "--", ":", ";", "=", "[", "{", "}", "edge - Insert a template"], "colorscheme", '''
			graph {
				subgraph cluster_0 {
					colorscheme=
				}
			}
		''')
	}

	@Test def cluster_bgcolor() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=accent3 bgcolor=«c»]
					1
				}
			}
		'''.testContentAssistant(#["1", "2", "3", "#", "/", ":", ";"], "2", '''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=accent3 bgcolor=2]
					1
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					colorscheme=accent3 bgcolor=«c»
					1
				}
			}
		'''.testContentAssistant(#["1", "2", "3", "#", "/", ":", ";"], "2", '''
			graph {
				subgraph cluster_0 {
					colorscheme=accent3 bgcolor=2
					1
				}
			}
		''')
	}

	@Test def cluster_color() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[color=«c»]
					1
				}
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "red", '''
			graph {
				subgraph cluster_0 {
					graph[color=red]
					1
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					color=«c»
					1
				}
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "green", '''
			graph {
				subgraph cluster_0 {
					color=green
					1
				}
			}
		''')
	}

	@Test def cluster_colorscheme() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=«c»]
					1
				}
			}
		'''.testContentAssistant(copyExpectedDotColorSchemes, "svg", '''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=svg]
					1
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					colorscheme=«c»
					1
				}
			}
		'''.testContentAssistant(copyExpectedDotColorSchemes, "svg", '''
			graph {
				subgraph cluster_0 {
					colorscheme=svg
					1
				}
			}
		''')
	}

	@Test def cluster_fillcolor() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=accent8
					fillcolor=«c»
					style=filled]
					1 2 3
				}
			}
		'''.testContentAssistant(#["1", "2", "3", "4", "5", "6", "7", "8", "#", "/", ":", ";"], "4", '''
			graph {
				subgraph cluster_0 {
					graph[colorscheme=accent8
					fillcolor=4
					style=filled]
					1 2 3
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					colorscheme=accent8
					fillcolor=«c»
					style=filled
					1 2 3
				}
			}
		'''.testContentAssistant(#["1", "2", "3", "4", "5", "6", "7", "8", "#", "/", ":", ";"], "4", '''
			graph {
				subgraph cluster_0 {
					colorscheme=accent8
					fillcolor=4
					style=filled
					1 2 3
				}
			}
		''')
	}

	@Test def cluster_fontcolor() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[label=CLUSTER
					fontcolor=«c»]
					1
				}
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "red", '''
			graph {
				subgraph cluster_0 {
					graph[label=CLUSTER
					fontcolor=red]
					1
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					label=CLUSTER
					fontcolor=«c»
					1
				}
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "red", '''
			graph {
				subgraph cluster_0 {
					label=CLUSTER
					fontcolor=red
					1
				}
			}
		''')
	}

	@Test def cluster_fontname() {
		//test global attribute value
		'''
			graph {
				subgraph cluster_0 {
					graph[label=CLUSTER
					fontname=«c»]
					1
				}
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			graph {
				subgraph cluster_0 {
					graph[label=CLUSTER
					fontname=Helvetica]
					1
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				subgraph cluster_0 {
					label=CLUSTER
					fontname=«c»
					1
				}
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			graph {
				subgraph cluster_0 {
					label=CLUSTER
					fontname=Helvetica
					1
				}
			}
		''')
	}

	@Test def cluster_label() {
		'''
			graph {
				subgraph cluster {
					label = «c»
				}
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				subgraph cluster {
					label = <
						
					>
				}
			}
		''')

		// test html-like label attribute
		'''
			graph {
				subgraph cluster {
					label = <
						«c»
					>
				}
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>", "<SUB></SUB>",
									"<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<U></U>",
		'''
			graph {
				subgraph cluster {
					label = <
						<U></U>
					>
				}
			}
		''')
	}

	@Test def cluster_lp() {
		'''
			graph {
				subgraph cluster {
					lp = «c»
				}
			}
		'''.testEmptyContentAssistant
	}

	@Test def cluster_penwidth() {
		'''
			graph {
				subgraph cluster {
					penwidth = «c»
				}
			}
		'''.testEmptyContentAssistant
	}

	@Test def cluster_style() {
		//test global attribute value
		'''
			graph {
				subgraph cluster {
					graph[style = «c»]
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "dotted", '''
			graph {
				subgraph cluster {
					graph[style = dotted]
				}
			}
		''')

		//test local attribute values
		'''
			graph {
				subgraph cluster {
					style = «c»
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "bold", '''
			graph {
				subgraph cluster {
					style = bold
				}
			}
		''')

		'''
			graph {
				subgraph cluster {
					style="«c»"
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "dashed", '''
			graph {
				subgraph cluster {
					style="dashed"
				}
			}
		''')

		'''
			graph {
				subgraph cluster {
					style = "«c»"
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "dotted", '''
			graph {
				subgraph cluster {
					style = "dotted"
				}
			}
		''')

		// test local attribute values with list
		'''
			graph {
				subgraph cluster {
					style = "solid,«c»"
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "striped", '''
			graph {
				subgraph cluster {
					style = "solid,striped"
				}
			}
		''')

		// test local attribute values with list and prefix
		'''
			graph {
				subgraph cluster {
					style = "solid,s«c»"
				}
			}
		'''.testContentAssistant(#["solid", "striped"], "striped", '''
			graph {
				subgraph cluster {
					style = "solid,striped"
				}
			}
		''')

		// test local attribute values with list and following attribute
		'''
			graph {
				subgraph cluster {
					style = "solid,«c»,bold"
				}
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped"], "striped", '''
			graph {
				subgraph cluster {
					style = "solid,striped,bold"
				}
			}
		''')
	}

	@Test def cluster_template() {
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template",
									"{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir",
									"rankdir", "splines", "style", "edge - Insert a template"], "cluster - Insert a template",
		'''
			graph {
				subgraph clustername {
					content
				}
			}
		''')

		'''
			graph {
				1
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "=", "[", "bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder",
									"pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "cluster - Insert a template",
		'''
			graph {
				1
				subgraph clustername {
					content
				}
			}
		''')
	}

	@Test def cluster_tooltip() {
		//test global attribute value
		'''
			graph {
				subgraph cluster {
					graph[tooltip ="«c»"]
				}
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				subgraph cluster {
					graph[tooltip ="\l"]
				}
			}
		''')

		//test global attribute value
		'''
			graph {
				subgraph cluster {
					tooltip ="«c»"
				}
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				subgraph cluster {
					tooltip ="\l"
				}
			}
		''')
	}

	@Test def edge_attribute_statement001() {
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "edge[]", "edge - Insert a template", "fontcolor",
			"fontname", "fontsize", "forcelabels", "graph[]", "id", "label", "layout", "lp", "node[]", "nodesep", "outputorder", "pagedir", "rankdir", "splines",
			"style", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "edge[]", '''
			graph {
				edge[]
			}
		''')
	}

	@Test def edge_attribute_statement002() {
		'''
			graph {
				1--2
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "[", "cluster - Insert a template" ,"edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template","{","}"], "edge[]", '''
			graph {
				1--2
				edge[]
			}
		''')
	}

	@Test def edge_attributes() {
		// test global attribute names
		'''
			graph {
				edge[«c»]
			}
		'''.testContentAssistant(#["]", "arrowhead", "arrowsize", "arrowtail", "color", "colorscheme",
			"dir", "edgetooltip", "fillcolor", "fontcolor", "fontname", "fontsize", "headlabel", "headport", "headtooltip",
			"head_lp", "id", "label", "labelfontcolor", "labelfontname", "labelfontsize", "labeltooltip", "lp", "penwidth", "pos",
			"style", "taillabel", "tailport", "tailtooltip", "tail_lp", "tooltip", "xlabel", "xlp"
		], "arrowhead", '''
			graph {
				edge[arrowhead=]
			}
		''')

		// test local attribute names
		'''
			graph {
				1--2[ «c» ]
			}
		'''.testContentAssistant(#["]", "arrowhead", "arrowsize", "arrowtail", "color", "colorscheme",
			"dir", "edgetooltip", "fillcolor", "fontcolor", "fontname", "fontsize", "headlabel", "headport", "headtooltip",
			"head_lp", "id", "label", "labelfontcolor", "labelfontname", "labelfontsize", "labeltooltip", "lp", "penwidth", "pos",
			"style", "taillabel", "tailport", "tailtooltip", "tail_lp", "tooltip", "xlabel", "xlp"
		], "arrowtail", '''
			graph {
				1--2[ arrowtail= ]
			}
		''')

		// test local attribute names with prefix
		'''
			graph {
				1--2[ a«c» ]
			}
		'''.testContentAssistant(#["=", "arrowhead", "arrowsize", "arrowtail"], "arrowsize", '''
			graph {
				1--2[ arrowsize= ]
			}
		''')
	}

	@Test def edge_arrowhead() {
		// test global attribute values
		'''
			digraph {
				edge[ arrowhead=«c» ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "box", '''
			digraph {
				edge[ arrowhead=box ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ arrowhead=«c» ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "diamond", '''
			digraph {
				1->2[ arrowhead=diamond ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ arrowhead="«c»" ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "dot", '''
			digraph {
				1->2[ arrowhead="dot" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ arrowhead=c«c» ]
			}
		'''.testContentAssistant(#["crow", "curve", ",", ";", "]"], "crow", '''
			digraph {
				1->2[ arrowhead=crow ]
			}
		''')

		'''
			digraph {
				1->2[ arrowhead=o«c» ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "diamond", "dot", "inv", "normal", ",", ";", "]"], "box", '''
			digraph {
				1->2[ arrowhead=obox ]
			}
		''')

		'''
			digraph {
				1->2[ arrowhead=l«c» ]
			}
		'''.testContentAssistant(#["l", "box", "crow", "curve", "icurve", "diamond", "inv", "normal", "tee", "vee", ",", ";", "]"], "diamond", '''
			digraph {
				1->2[ arrowhead=ldiamond ]
			}
		''')

		'''
			digraph {
				1->2[ arrowhead=ol«c» ]
			}
		'''.testContentAssistant(#["l", "box", "diamond", "inv", "normal", ",", ";", "]"], "diamond", '''
			digraph {
				1->2[ arrowhead=oldiamond ]
			}
		''')

		'''
			digraph {
				1->2[ arrowhead=ordia«c» ]
			}
		'''.testContentAssistant(#["diamond", ",", ";", "]"], "diamond", '''
			digraph {
				1->2[ arrowhead=ordiamond ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ arrowhead="c«c»" ]
			}
		'''.testContentAssistant(#["crow", "curve"], "curve", '''
			digraph {
				1->2[ arrowhead="curve" ]
			}
		''')
	}

	@Test def edge_arrowtail() {
		// test global attribute values
		'''
			digraph {
				edge[ arrowtail=«c» ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "none", '''
			digraph {
				edge[ arrowtail=none ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ arrowtail=«c» ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "normal", '''
			digraph {
				1->2[ arrowtail=normal ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ arrowtail="«c»" ]
			}
		'''.testContentAssistant(#["o", "l", "r", "box", "crow", "curve", "icurve", "diamond", "dot", "inv", "none", "normal", "tee", "vee"], "tee", '''
			digraph {
				1->2[ arrowtail="tee" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ arrowtail=d«c» ]
			}
		'''.testContentAssistant(#["diamond", "dot", ",", ";", "]"], "diamond", '''
			digraph {
				1->2[ arrowtail=diamond ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ arrowtail="n«c»" ]
			}
		'''.testContentAssistant(#["none", "normal"], "none", '''
			digraph {
				1->2[ arrowtail="none" ]
			}
		''')
	}

	@Test def edge_color() {
		// test global attribute values
		'''
			digraph {
				edge[ color=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/", ":", ";"]), "#", '''
			digraph {
				edge[ color=# ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ color=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/", ":", ";"]), "/", '''
			digraph {
				1->2[ color=/ ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ color="«c»" ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/", ":", ";"]), "#", '''
			digraph {
				1->2[ color="#" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ color=azure«c» ]
			}
		'''.testContentAssistant(#["azure", "azure1", "azure2", "azure3", "azure4", ":", ",", ";", "]"], "azure1", '''
			digraph {
				1->2[ color=azure1 ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ color="azure«c»" ]
			}
		'''.testContentAssistant(#["azure", "azure1", "azure2", "azure3", "azure4", ":", ";"], "azure2", '''
			digraph {
				1->2[ color="azure2" ]
			}
		''')

		// test local attribute value with local color scheme value
		'''
			graph {
				1--2[color=«c»; colorscheme=brbg10]
			}
		'''.testContentAssistant(#["#", "/", ":", ";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "10", '''
			graph {
				1--2[color=10; colorscheme=brbg10]
			}
		''')

		// test local attribute value with global color scheme value
		'''
			graph {
				edge[colorscheme=brbg10]
				1--2[color=«c»]
			}
		'''.testContentAssistant(#["#", "/", ":", ";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "8", '''
			graph {
				edge[colorscheme=brbg10]
				1--2[color=8]
			}
		''')
	}

	@Test def edge_colorscheme() {
		val expectedDotColorSchemes = copyExpectedDotColorSchemes

		// test global attribute values
		'''
			digraph {
				edge[ colorscheme=«c» ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "x11", '''
			digraph {
				edge[ colorscheme=x11 ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ colorscheme=«c» ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "svg", '''
			digraph {
				1->2[ colorscheme=svg ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ colorscheme="«c»" ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "accent3", '''
			digraph {
				1->2[ colorscheme="accent3" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ colorscheme=a«c» ]
			}
		'''.testContentAssistant(#["accent3", "accent4", "accent5", "accent6", "accent7", "accent8", ",", ";", "]"], "accent4", '''
			digraph {
				1->2[ colorscheme=accent4 ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ colorscheme="acc«c»" ]
			}
		'''.testContentAssistant(#["accent3", "accent4", "accent5", "accent6", "accent7", "accent8"], "accent8", '''
			digraph {
				1->2[ colorscheme="accent8" ]
			}
		''')
	}

	@Test def edge_dir() {
		// test global attribute values
		'''
			graph {
				edge[ dir=«c» ]
			}
		'''.testContentAssistant(#["forward", "back", "both", "none"], "forward", '''
			graph {
				edge[ dir=forward ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1--2[ dir=«c» ]
			}
		'''.testContentAssistant(#["forward", "back", "both", "none"], "back", '''
			graph {
				1--2[ dir=back ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ dir="«c»" ]
			}
		'''.testContentAssistant(#["forward", "back", "both", "none"], "both", '''
			graph {
				1--2[ dir="both" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1--2[ dir=f«c» ]
			}
		'''.testContentAssistant(#["forward", ",", ";", "]"], "forward", '''
			graph {
				1--2[ dir=forward ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ dir="b«c»" ]
			}
		'''.testContentAssistant(#["back", "both"], "both", '''
			graph {
				1--2[ dir="both" ]
			}
		''')
	}

	@Test def edge_edgetooltip() {
		// test global attribute values with quotes
		'''
			graph {
				edge[ edgetooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				edge[ edgetooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ edgetooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1--2[ edgetooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ edgetooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1--2[ edgetooltip="line1\r" ]
			}
		''')
	}

	@Test def edge_fillcolor() {
		// test global attribute values
		'''
			digraph {
				edge[ fillcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			digraph {
				edge[ fillcolor=# ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ fillcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "/", '''
			digraph {
				1->2[ fillcolor=/ ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ fillcolor="«c»" ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			digraph {
				1->2[ fillcolor="#" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ fillcolor=bisque«c» ]
			}
		'''.testContentAssistant(#["bisque", "bisque1", "bisque2", "bisque3", "bisque4", ",", ";", "]"], "bisque1", '''
			digraph {
				1->2[ fillcolor=bisque1 ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ fillcolor="bisque«c»" ]
			}
		'''.testContentAssistant(#["bisque", "bisque1", "bisque2", "bisque3", "bisque4"], "bisque2", '''
			digraph {
				1->2[ fillcolor="bisque2" ]
			}
		''')
	}

	@Test def edge_fontcolor() {
		// test global attribute values
		'''
			digraph {
				edge[ fontcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			digraph {
				edge[ fontcolor=# ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ fontcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "/", '''
			digraph {
				1->2[ fontcolor=/ ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ fontcolor="«c»" ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			digraph {
				1->2[ fontcolor="#" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ fontcolor=blue«c» ]
			}
		'''.testContentAssistant(#["blue", "blue1", "blue2", "blue3", "blue4", "blueviolet", ",", ";", "]"], "blue", '''
			digraph {
				1->2[ fontcolor=blue ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ fontcolor="blue«c»" ]
			}
		'''.testContentAssistant(#["blue", "blue1", "blue2", "blue3", "blue4", "blueviolet"], "blueviolet", '''
			digraph {
				1->2[ fontcolor="blueviolet" ]
			}
		''')
	}

	@Test def edge_fontname() {
		// test global attribute values
		'''
			digraph {
				edge[ fontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			digraph {
				edge[ fontname=Helvetica ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ fontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Courier-Bold", '''
			digraph {
				1->2[ fontname="Courier-Bold" ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ fontname="«c»" ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Times-Roman", '''
			digraph {
				1->2[ fontname="Times-Roman" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ fontname=Zapf«c» ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic", ",", ";", "]"], "ZapfDingbats", '''
			digraph {
				1->2[ fontname=ZapfDingbats ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ fontname="zapf«c»" ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic"], "ZapfChancery-MediumItalic", '''
			digraph {
				1->2[ fontname="ZapfChancery-MediumItalic" ]
			}
		''')
	}

	@Test def edge_headlabel() {
		// test global attribute values
		'''
			graph {
				edge[ headlabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				edge[ headlabel=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[headlabel=«c»]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			digraph {
				1->2[headlabel=<
					
				>]
			}
		''')

		// test html-like label attribute
		'''
			digraph {
				1->2[headlabel=<«c»>]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>",
		"<S></S>", "<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<TABLE></TABLE>", '''
			digraph {
				1->2[headlabel=<<TABLE></TABLE>>]
			}
		''')
	}

	@Test def edge_headlp() {
		// test global attribute values
		'''
			graph {
				edge[ head_lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ head_lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def edge_headport() {
		// test global attribute values
		'''
			graph {
				edge[ headport=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ headport=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def edge_headtooltip() {
		// test global attribute values with quotes
		'''
			graph {
				edge[ headtooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				edge[ headtooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ headtooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1--2[ headtooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ headtooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1--2[ headtooltip="line1\r" ]
			}
		''')
	}

	@Test def edge_label() {
		// test global attribute values
		'''
			graph {
				edge[ label=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				edge[ label=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1--2[ label = «c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				1--2[ label = <
					
				> ]
			}
		''')

		// test html-like label attribute
		'''
			graph {
				1--2[ label = <«c»> ]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
								"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<B></B>", '''
			graph {
				1--2[ label = <<B></B>> ]
			}
		''')
	}

	@Test def edge_labelfontcolor() {
		// test global attribute values
		'''
			digraph {
				edge [
					colorscheme=svg
					labelfontcolor=«c»
				]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/"]), "#", '''
			digraph {
				edge [
					colorscheme=svg
					labelfontcolor=#
				]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2 [
					colorscheme=svg
					labelfontcolor=«c»
				]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/"]), "/", '''
			digraph {
				1->2 [
					colorscheme=svg
					labelfontcolor=/
				]
			}
		''')

		// test local attribute values (case insensitive color scheme)
		'''
			digraph {
				1->2 [
					colorscheme=SVG
					labelfontcolor=«c»
				]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/"]), "/", '''
			digraph {
				1->2 [
					colorscheme=SVG
					labelfontcolor=/
				]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2 [
					colorscheme=svg
					labelfontcolor="«c»"
				]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/"]), "#", '''
			digraph {
				1->2 [
					colorscheme=svg
					labelfontcolor="#"
				]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2 [ labelfontcolor=gray«c» ]
			}
		'''.testContentAssistant(#["gray", "gray0", "gray1", "gray10", "gray100", "gray11", "gray12", "gray13", "gray14", "gray15",
									"gray16", "gray17", "gray18", "gray19", "gray2", "gray20", "gray21", "gray22", "gray23",
									"gray24", "gray25", "gray26", "gray27", "gray28", "gray29", "gray3", "gray30", "gray31",
									"gray32", "gray33", "gray34", "gray35", "gray36", "gray37", "gray38", "gray39", "gray4",
									"gray40", "gray41", "gray42", "gray43", "gray44", "gray45", "gray46", "gray47", "gray48",
									"gray49", "gray5", "gray50", "gray51", "gray52", "gray53", "gray54", "gray55", "gray56",
									"gray57", "gray58", "gray59", "gray6", "gray60", "gray61", "gray62", "gray63", "gray64",
									"gray65", "gray66", "gray67", "gray68", "gray69", "gray7", "gray70", "gray71", "gray72",
									"gray73", "gray74", "gray75", "gray76", "gray77", "gray78", "gray79", "gray8", "gray80",
									"gray81", "gray82", "gray83", "gray84", "gray85", "gray86", "gray87", "gray88", "gray89",
									"gray9", "gray90", "gray91", "gray92", "gray93", "gray94", "gray95", "gray96", "gray97",
									"gray98", "gray99", ",", ";", "]"],	"gray",
		'''
			digraph {
				1->2 [ labelfontcolor=gray ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2 [labelfontcolor="gray«c»"]
			}
		'''.testContentAssistant(#["gray", "gray0", "gray1", "gray10", "gray100", "gray11", "gray12", "gray13", "gray14", "gray15",
									"gray16", "gray17", "gray18", "gray19", "gray2", "gray20", "gray21", "gray22", "gray23",
									"gray24", "gray25", "gray26", "gray27", "gray28", "gray29", "gray3", "gray30", "gray31",
									"gray32", "gray33", "gray34", "gray35", "gray36", "gray37", "gray38", "gray39", "gray4",
									"gray40", "gray41", "gray42", "gray43", "gray44", "gray45", "gray46", "gray47", "gray48",
									"gray49", "gray5", "gray50", "gray51", "gray52", "gray53", "gray54", "gray55", "gray56",
									"gray57", "gray58", "gray59", "gray6", "gray60", "gray61", "gray62", "gray63", "gray64",
									"gray65", "gray66", "gray67", "gray68", "gray69", "gray7", "gray70", "gray71", "gray72",
									"gray73", "gray74", "gray75", "gray76", "gray77", "gray78", "gray79", "gray8", "gray80",
									"gray81", "gray82", "gray83", "gray84", "gray85", "gray86", "gray87", "gray88", "gray89",
									"gray9", "gray90", "gray91", "gray92", "gray93", "gray94", "gray95", "gray96", "gray97",
									"gray98", "gray99"], "gray99",
		'''
			digraph {
				1->2 [labelfontcolor="gray99"]
			}
		''')
	}

	@Test def edge_labelfontname() {
		// test global attribute values
		'''
			digraph {
				edge[ labelfontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			digraph {
				edge[ labelfontname=Helvetica ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ labelfontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Courier-Bold", '''
			digraph {
				1->2[ labelfontname="Courier-Bold" ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1->2[ labelfontname="«c»" ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Times-Roman", '''
			digraph {
				1->2[ labelfontname="Times-Roman" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1->2[ labelfontname=Zapf«c» ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic", ",", ";", "]"], "ZapfDingbats", '''
			digraph {
				1->2[ labelfontname=ZapfDingbats ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1->2[ labelfontname="zapf«c»" ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic"], "ZapfChancery-MediumItalic", '''
			digraph {
				1->2[ labelfontname="ZapfChancery-MediumItalic" ]
			}
		''')
	}

	@Test def edge_labeltooltip() {
		// test global attribute values with quotes
		'''
			graph {
				edge[ labeltooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				edge[ labeltooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ labeltooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1--2[ labeltooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ labeltooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1--2[ labeltooltip="line1\r" ]
			}
		''')
	}

	@Test def edge_lp() {
		// test global attribute values
		'''
			graph {
				edge[ lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def edge_pos() {
		// test global attribute values
		'''
			graph {
				edge[ pos=«c» ]
			}
		'''.testContentAssistant(#["e", "s"], "e", '''
			graph {
				edge[ pos=e ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1--2[ pos=«c» ]
			}
		'''.testContentAssistant(#["e", "s"], "s", '''
			graph {
				1--2[ pos=s ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ pos="«c»" ]
			}
		'''.testContentAssistant(#["e", "s"], "s", '''
			graph {
				1--2[ pos="s" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1--2[ pos=e«c» ]
			}
		'''.testContentAssistant(#["e", ",", ";", "]"], "e", '''
			graph {
				1--2[ pos=e ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ pos="s«c»" ]
			}
		'''.testContentAssistant(#["s", ","], "s", '''
			graph {
				1--2[ pos="s" ]
			}
		''')
	}

	@Test def edge_style() {
		// test global attribute values
		'''
			graph {
				edge[ style=«c» ]
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "invis", "solid", "tapered"], "bold", '''
			graph {
				edge[ style=bold ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1--2[ style=«c» ]
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "invis", "solid", "tapered"], "dashed", '''
			graph {
				1--2[ style=dashed ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ style="«c»" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "invis", "solid", "tapered"], "tapered", '''
			graph {
				1--2[ style="tapered" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1--2[ style=d«c» ]
			}
		'''.testContentAssistant(#["dashed", "dotted", ",", ";", "]"], "dotted", '''
			graph {
				1--2[ style=dotted ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ style="d«c»" ]
			}
		'''.testContentAssistant(#["dashed", "dotted"], "dashed", '''
			graph {
				1--2[ style="dashed" ]
			}
		''')

		// test local attribute values with list
		'''
			graph {
				1--2[ style="solid,«c»" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "invis", "solid", "tapered"], "dotted", '''
			graph {
				1--2[ style="solid,dotted" ]
			}
		''')

		// test local attribute values with list and prefix
		'''
			graph {
				1--2[ style="solid,d«c»" ]
			}
		'''.testContentAssistant(#["dashed", "dotted"], "dotted", '''
			graph {
				1--2[ style="solid,dotted" ]
			}
		''')

		// test local attribute values with list and following attribute
		'''
			graph {
				1--2[ style="solid,«c»,bold" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "dotted", "invis", "solid", "tapered"], "tapered", '''
			graph {
				1--2[ style="solid,tapered,bold" ]
			}
		''')
	}

	@Test def edge_taillabel() {
		// test global attribute values
		'''
			graph {
				edge[ taillabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				edge[ taillabel=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ taillabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			digraph {
				1->2[ taillabel=<
					
				> ]
			}
		''')

		// test html-like label attribute
		'''
			digraph {
				1->2[ taillabel=<
					«c»
				> ]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
									"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<TABLE></TABLE>", '''
			digraph {
				1->2[ taillabel=<
					<TABLE></TABLE>
				> ]
			}
		''')
	}

	@Test def edge_taillp() {
		// test global attribute values
		'''
			graph {
				edge[ tail_lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ tail_lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def edge_tailport() {
		// test global attribute values
		'''
			graph {
				edge[ tailport=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ tailport=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def edge_tailtooltip() {
		// test global attribute values with quotes
		'''
			graph {
				edge[ tailtooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				edge[ tailtooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ tailtooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1--2[ tailtooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ tailtooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1--2[ tailtooltip="line1\r" ]
			}
		''')
	}

	@Test def edge_template() {
		// test edge template in non-directed graphs
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template",
									"{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir",
									"rankdir", "splines", "style", "edge - Insert a template"], "edge - Insert a template",
		'''
			graph {
				source -- target
			}
		''')

		'''
			graph {
				1
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "=", "[", "bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder",
									"pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "edge - Insert a template",
		'''
			graph {
				1
				source -- target
			}
		''')

		// test edge template in directed graphs
		'''
			digraph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template",
									"{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir",
									"rankdir", "splines", "style", "edge - Insert a template"], "edge - Insert a template",
		'''
			digraph {
				source -> target
			}
		''')

		'''
			digraph {
				1
				«c»
			}
		'''.testContentAssistant(#["->", ":", ";", "=", "[", "bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "edge[]", "fontname", "fontsize", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder",
									"pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "edge - Insert a template",
		'''
			digraph {
				1
				source -> target
			}
		''')
	}

	@Test def edge_tooltip() {
		// test global attribute values with quotes
		'''
			graph {
				edge[ tooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				edge[ tooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1--2[ tooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1--2[ tooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1--2[ tooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1--2[ tooltip="line1\r" ]
			}
		''')
	}

	@Test def edge_xlabel() {
		// test global attribute values
		'''
			graph {
				edge[ xlabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				edge[ xlabel=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1->2[ xlabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			digraph {
				1->2[ xlabel=<
					
				> ]
			}
		''')

		// test html-like label attribute
		'''
			digraph {
				1->2[ xlabel=<
					«c»
				> ]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
								"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<I></I>", '''
			digraph {
				1->2[ xlabel=<
					<I></I>
				> ]
			}
		''')
	}

	@Test def edge_xlp() {
		// test global attribute values
		'''
			graph {
				edge[ xlp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1--2[ xlp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def graph_attribute_statement001() {
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "edge[]", "edge - Insert a template", "fontcolor", "fontname", "fontsize",
			"forcelabels", "graph[]", "id", "label", "layout", "lp", "node[]", "nodesep", "outputorder", "pagedir", "rankdir", "splines", "style", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "graph[]", '''
			graph {
				graph[]
			}
		''')
	}

	@Test def graph_attribute_statement002() {
		'''
			graph {
				1--2
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "[", "cluster - Insert a template", "edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template","{","}"], "graph[]", '''
			graph {
				1--2
				graph[]
			}
		''')
	}

	@Test def graph_attributes() {
		// test global attribute names
		'''
			graph {
				graph[«c»]
			}
		'''.testContentAssistant(#["]", "bb", "bgcolor", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "forcelabels",
								"id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir", "rankdir",
								"splines", "style"], "forcelabels",
		'''
			graph {
				graph[forcelabels=]
			}
		''')

		// test local attribute names
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]",
									"node[]",	"subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label",	"layout", "lp",
									"nodesep", "outputorder", "pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "rankdir",
		'''
			graph {
				rankdir=
			}
		''')

		// test local attribute names with prefix
		'''
			graph {
				la«c»
			}
		'''.testContentAssistant(#["label", "layout", "--", ":", ";", "=", "[", "{", "}", "edge - Insert a template"], "layout", '''
			graph {
				layout=
			}
		''')

		'''
			digraph {
				la«c»
			}
		'''.testContentAssistant(#["label", "layout", "->", ":", ";", "=", "[", "{", "}", "edge - Insert a template"], "label", '''
			digraph {
				label=
			}
		''')
	}

	@Test def graph_bgcolor() {
		// test global attribute values
		'''
			graph {
				graph[ colorscheme=svg bgcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/", ":", ";"]), "aliceblue", '''
			graph {
				graph[ colorscheme=svg bgcolor=aliceblue ]
			}
		''')

		// test local attribute values
		'''
			graph {
				colorscheme=svg
				bgcolor=«c»
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/", ":", ";"]), "aqua", '''
			graph {
				colorscheme=svg
				bgcolor=aqua
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				colorscheme=svg
				bgcolor="«c»"
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, #["#", "/", ":", ";"]), "aquamarine", '''
			graph {
				colorscheme=svg
				bgcolor="aquamarine"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				colorscheme=svg
				bgcolor=aqua«c»
			}
		'''.testContentAssistant(#["aqua", "aquamarine", ":", ";", "{", "}", "edge - Insert a template"], "aqua", '''
			graph {
				colorscheme=svg
				bgcolor=aqua
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				colorscheme=svg
				bgcolor="dark«c»"
			}
		'''.testContentAssistant(#[":", ";", "darkblue", "darkcyan", "darkgoldenrod", "darkgray", "darkgreen",
									"darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange",
									"darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue",
									"darkslategray", "darkslategrey", "darkturquoise", "darkviolet"], "darkturquoise",
		'''
			graph {
				colorscheme=svg
				bgcolor="darkturquoise"
			}
		''')

		// test local attribute value with local color scheme value
		'''
			graph {
				colorscheme=brbg10
				bgcolor=«c»
				1
			}
		'''.testContentAssistant(#["#", "/", ":", ";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "10", '''
			graph {
				colorscheme=brbg10
				bgcolor=10
				1
			}
		''')

		// test local attribute value with global color scheme value
		'''
			graph {
				graph[colorscheme=brbg10]
				bgcolor=«c»
				1
			}
		'''.testContentAssistant(#["#", "/", ":", ";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "10", '''
			graph {
				graph[colorscheme=brbg10]
				bgcolor=10
				1
			}
		''')
	}

	@Test def graph_clusterrank() {
		// test global attribute values
		'''
			graph {
				graph[ clusterrank=«c» ]
			}
		'''.testContentAssistant(#["local", "global", "none"], "local", '''
			graph {
				graph[ clusterrank=local ]
			}
		''')

		// test local attribute values
		'''
			graph {
				clusterrank=«c»
			}
		'''.testContentAssistant(#["local", "global", "none"], "global", '''
			graph {
				clusterrank=global
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				clusterrank="«c»"
			}
		'''.testContentAssistant(#["local", "global", "none"], "none", '''
			graph {
				clusterrank="none"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				clusterrank=l«c»
			}
		'''.testContentAssistant(#["local", ";", "{", "}", "edge - Insert a template"], "local", '''
			graph {
				clusterrank=local
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				clusterrank="g«c»"
			}
		'''.testContentAssistant(#["global"], "global", '''
			graph {
				clusterrank="global"
			}
		''')
	}

	@Test def graph_colorscheme() {
		val expectedDotColorSchemes = copyExpectedDotColorSchemes

		// test global attribute values
		'''
			graph {
				graph[ colorscheme=«c» ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "svg", '''
			graph {
				graph[ colorscheme=svg ]
			}
		''')

		// test local attribute vaules
		'''
			graph {
				colorscheme=«c»
			}
		'''.testContentAssistant(expectedDotColorSchemes, "svg", '''
			graph {
				colorscheme=svg
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				colorscheme="«c»"
			}
		'''.testContentAssistant(expectedDotColorSchemes, "blues3", '''
			graph {
				colorscheme="blues3"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				colorscheme=blues«c»
			}
		'''.testContentAssistant(#["blues3", "blues4", "blues5", "blues6", "blues7", "blues8", "blues9", ";", "{", "}", "edge - Insert a template"], "blues5", '''
			graph {
				colorscheme=blues5
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				colorscheme="blues«c»"
			}
		'''.testContentAssistant(#["blues3", "blues4", "blues5", "blues6", "blues7", "blues8", "blues9"], "blues9", '''
			graph {
				colorscheme="blues9"
			}
		''')
	}

	@Test def graph_fontcolor() {
		// test global attribute values
		'''
			graph {
				graph[ colorscheme=brbg11 fontcolor=«c» ]
			}
		'''.testContentAssistant(#["#", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"], "1", '''
			graph {
				graph[ colorscheme=brbg11 fontcolor=1 ]
			}
		''')

		// test local attribute values
		'''
			graph {
				colorscheme=brbg11 fontcolor=«c»
			}
		'''.testContentAssistant(#["#", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"], "11", '''
			graph {
				colorscheme=brbg11 fontcolor=11
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				colorscheme=brbg11 fontcolor="«c»"
			}
		'''.testContentAssistant(#["#", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"], "10", '''
			graph {
				colorscheme=brbg11 fontcolor="10"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				colorscheme=brbg11 fontcolor=1«c»
			}
		'''.testContentAssistant(#["1", "10", "11", ",", ";", "{", "}", "bb", "bgcolor", "colorscheme", "cluster - Insert a template", "clusterrank",
									"edge[]", "fontcolor", "fontname", "fontsize", "forcelabels", "graph[]", "id", "label", "layout", "lp", "node[]",
									"nodesep", "outputorder", "pagedir", "rankdir", "splines", "style", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "edge - Insert a template"], "11",
		'''
			graph {
				colorscheme=brbg11 fontcolor=11
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				colorscheme=brbg11 fontcolor="1«c»"
			}
		'''.testContentAssistant(#["1", "10", "11", ","], "11", '''
			graph {
				colorscheme=brbg11 fontcolor="11"
			}
		''')
	}

	@Test def graph_fontname() {
		// test global attribute values
		'''
			digraph {
				graph[ fontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			digraph {
				graph[ fontname=Helvetica ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				fontname=«c»
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Courier-Bold", '''
			digraph {
				fontname="Courier-Bold"
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				fontname="«c»"
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Times-Roman", '''
			digraph {
				fontname="Times-Roman"
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				fontname=Zapf«c»
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic", ";", "{", "}", "edge - Insert a template"], "ZapfDingbats", '''
			digraph {
				fontname=ZapfDingbats
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				fontname="zapf«c»"
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic"], "ZapfChancery-MediumItalic", '''
			digraph {
				fontname="ZapfChancery-MediumItalic"
			}
		''')
	}

	@Test def graph_forcelabels() {
		// test global attribute values
		'''
			graph {
				graph[ forcelabels=«c» ]
			}
		'''.testContentAssistant(#["true", "false"], "true", '''
			graph {
				graph[ forcelabels=true ]
			}
		''')

		// test local attribute values
		'''
			graph {
				forcelabels=«c»
			}
		'''.testContentAssistant(#["true", "false"], "false", '''
			graph {
				forcelabels=false
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				forcelabels="«c»"
			}
		'''.testContentAssistant(#["true", "false"], "true", '''
			graph {
				forcelabels="true"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				forcelabels=t«c»
			}
		'''.testContentAssistant(#["true", ";", "{", "}", "edge - Insert a template"], "true", '''
			graph {
				forcelabels=true
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				forcelabels="f«c»"
			}
		'''.testContentAssistant(#["false"], "false", '''
			graph {
				forcelabels="false"
			}
		''')
	}

	@Test def graph_label() {
		// test global attribute values
		'''
			graph {
				graph[ label=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				graph[ label=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			graph {
				label=«c»
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				label=<
					
				>
			}
		''')

		'''
			graph {
				label=<
					«c»
				>
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
									"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<S></S>",
		'''
			graph {
				label=<
					<S></S>
				>
			}
		''')

		// test html-like label attribute value
		'''
			graph {
				label=<
					<TABLE ROWS="«c»"></TABLE>
				>
			}
		'''.testContentAssistant(#["*"], "*", '''
			graph {
				label=<
					<TABLE ROWS="*"></TABLE>
				>
			}
		''')
	}

	@Test def graph_layout() {
		// test global attribute values
		'''
			graph {
				graph[ layout=«c» ]
			}
		'''.testContentAssistant(#["circo", "dot", "fdp", "neato", "osage", "sfdp", "twopi"], "circo", '''
			graph {
				graph[ layout=circo ]
			}
		''')

		// test local attribute values
		'''
			graph {
				layout=«c»
			}
		'''.testContentAssistant(#["circo", "dot", "fdp", "neato", "osage", "sfdp", "twopi"], "osage", '''
			graph {
				layout=osage
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				layout="«c»"
			}
		'''.testContentAssistant(#["circo", "dot", "fdp", "neato", "osage", "sfdp", "twopi"], "neato", '''
			graph {
				layout="neato"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				layout=f«c»
			}
		'''.testContentAssistant(#["fdp", ";", "{", "}", "edge - Insert a template"], "fdp", '''
			graph {
				layout=fdp
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				layout="t«c»"
			}
		'''.testContentAssistant(#["twopi"], "twopi", '''
			graph {
				layout="twopi"
			}
		''')
	}

	@Test def graph_lp() {
		// test global attribute values
		'''
			graph {
				graph[ lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				lp=«c»
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def graph_outputorder() {
		// test global attribute values
		'''
			graph {
				graph[ outputorder=«c» ]
			}
		'''.testContentAssistant(#["breadthfirst", "nodesfirst", "edgesfirst"], "breadthfirst", '''
			graph {
				graph[ outputorder=breadthfirst ]
			}
		''')

		// test local attribute values
		'''
			graph {
				outputorder=«c»
			}
		'''.testContentAssistant(#["breadthfirst", "nodesfirst", "edgesfirst"], "nodesfirst", '''
			graph {
				outputorder=nodesfirst
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				outputorder="«c»"
			}
		'''.testContentAssistant(#["breadthfirst", "nodesfirst", "edgesfirst"], "edgesfirst", '''
			graph {
				outputorder="edgesfirst"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				outputorder=b«c»
			}
		'''.testContentAssistant(#["breadthfirst", ";", "{", "}", "edge - Insert a template"], "breadthfirst", '''
			graph {
				outputorder=breadthfirst
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				outputorder="n«c»"
			}
		'''.testContentAssistant(#["nodesfirst"], "nodesfirst", '''
			graph {
				outputorder="nodesfirst"
			}
		''')
	}

	@Test def graph_pagedir() {
		// test global attribute values
		'''
			graph {
				graph[ pagedir=«c» ]
			}
		'''.testContentAssistant(#["BL", "BR", "TL", "TR", "RB", "RT", "LB", "LT"], "BL", '''
			graph {
				graph[ pagedir=BL ]
			}
		''')

		// test local attribute values
		'''
			graph {
				pagedir=«c»
			}
		'''.testContentAssistant(#["BL", "BR", "TL", "TR", "RB", "RT", "LB", "LT"], "BR", '''
			graph {
				pagedir=BR
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				pagedir="«c»"
			}
		'''.testContentAssistant(#["BL", "BR", "TL", "TR", "RB", "RT", "LB", "LT"], "TL", '''
			graph {
				pagedir="TL"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				pagedir=B«c»
			}
		'''.testContentAssistant(#["BL", "BR", ";", "{", "}", "edge - Insert a template"], "BL", '''
			graph {
				pagedir=BL
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				pagedir="T«c»"
			}
		'''.testContentAssistant(#["TL", "TR"], "TL", '''
			graph {
				pagedir="TL"
			}
		''')
	}

	@Test def graph_rankdir() {
		// test global attribute values
		'''
			graph {
				graph[ rankdir=«c» ]
			}
		'''.testContentAssistant(#["TB", "LR", "BT", "RL"], "TB", '''
			graph {
				graph[ rankdir=TB ]
			}
		''')

		// test local attribute values
		'''
			graph {
				rankdir=«c»
			}
		'''.testContentAssistant(#["TB", "LR", "BT", "RL"], "LR", '''
			graph {
				rankdir=LR
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				rankdir="«c»"
			}
		'''.testContentAssistant(#["TB", "LR", "BT", "RL"], "BT", '''
			graph {
				rankdir="BT"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				rankdir=T«c»
			}
		'''.testContentAssistant(#["TB", ";", "{", "}", "edge - Insert a template"], "TB", '''
			graph {
				rankdir=TB
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				rankdir="L«c»"
			}
		'''.testContentAssistant(#["LR"], "TB", '''
			graph {
				rankdir="LR"
			}
		''')
	}

	@Test def graph_splines() {
		// test global attribute values
		'''
			graph {
				graph[ splines=«c» ]
			}
		'''.testContentAssistant(#["compound", "curved", '""', "false", "line", "none", "ortho", "polyline", "spline", "true"], "ortho", '''
			graph {
				graph[ splines=ortho ]
			}
		''')

		// test local attribute values
		'''
			graph {
				splines=«c»
			}
		'''.testContentAssistant(#["compound", "curved", '""', "false", "line", "none", "ortho", "polyline", "spline", "true"], "polyline", '''
			graph {
				splines=polyline
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				splines="«c»"
			}
		'''.testContentAssistant(#["compound", "curved", '""', "false", "line", "none", "ortho", "polyline", "spline", "true"], "line", '''
			graph {
				splines="line"
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				splines=c«c»
			}
		'''.testContentAssistant(#["compound", "curved", ";", "{", "}", "edge - Insert a template"], "compound", '''
			graph {
				splines=compound
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				splines="c«c»"
			}
		'''.testContentAssistant(#["compound", "curved"], "curved", '''
			graph {
				splines="curved"
			}
		''')
	}

	@Test def node_attribute_statement001() {
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "edge[]", "edge - Insert a template", "fontcolor", "fontname", "fontsize",
			"forcelabels", "graph[]", "id", "label", "layout", "lp", "node[]", "nodesep", "outputorder", "pagedir", "rankdir", "splines", "style", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "node[]", '''
			graph {
				node[]
			}
		''')
	}

	@Test def node_attribute_statement002() {
		'''
			graph {
				1--2
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "[", "cluster - Insert a template", "edge[]", "edge - Insert a template", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"], "node[]", '''
			graph {
				1--2
				node[]
			}
		''')
	}

	@Test def node_attributes() {
		// test global attribute names
		'''
			graph {
				node[«c»]
			}
		'''.testContentAssistant(#["]", "color", "colorscheme", "distortion", "fillcolor", "fixedsize", "fontcolor", "fontname", "fontsize",
								"height", "id", "label", "penwidth", "pos", "shape", "sides", "skew", "style", "tooltip", "width",
								"xlabel", "xlp"], "distortion",
		'''
			graph {
				node[distortion=]
			}
		''')

		// test local attribute names
		'''
			graph {
				1[ «c» ]
			}
		'''.testContentAssistant(#["]", "color", "colorscheme", "distortion", "fillcolor", "fixedsize", "fontcolor", "fontname", "fontsize",
								"height", "id", "label", "penwidth", "pos", "shape", "sides", "skew", "style", "tooltip", "width",
								"xlabel", "xlp"], "fixedsize",
		'''
			graph {
				1[ fixedsize= ]
			}
		''')

		'''
			graph {
				1[color="blue"«c»]
			}
		'''.testContentAssistant(#[",", ";", "]", "color", "colorscheme", "distortion", "fillcolor", "fixedsize", "fontcolor", "fontname", "fontsize",
								"height", "id", "label", "penwidth", "pos", "shape", "sides", "skew", "style", "tooltip", "width",
								"xlabel", "xlp"], "fixedsize",
		'''
			graph {
				1[color="blue"fixedsize=]
			}
		''')

		// test local attribute names with prefix
		'''
			graph {
				1[ s«c» ]
			}
		'''.testContentAssistant(#["=", "shape", "sides", "skew", "style"], "shape", '''
			graph {
				1[ shape= ]
			}
		''')
	}

	@Test def node_color() {
		// test global attribute values
		'''
			graph {
				node[ color=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			graph {
				node[ color=# ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ color=«c» ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			graph {
				1[ color=# ]
			}
		''')

		'''
			graph {
				1[ color=«c»"" ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			graph {
				1[ color=#"" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ color="«c»" ]
			}
		'''.testContentAssistant(combine(expectedX11ColorNames, #["#", "/"]), "#", '''
			graph {
				1[ color="#" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ color=light«c» ]
			}
		'''.testContentAssistant(#["lightblue", "lightblue1", "lightblue2", "lightblue3", "lightblue4", "lightcoral",
									"lightcyan", "lightcyan1", "lightcyan2", "lightcyan3", "lightcyan4", "lightgoldenrod",
									"lightgoldenrod1", "lightgoldenrod2", "lightgoldenrod3", "lightgoldenrod4",
									"lightgoldenrodyellow", "lightgray", "lightgrey", "lightpink", "lightpink1",
									"lightpink2", "lightpink3", "lightpink4", "lightsalmon", "lightsalmon1", "lightsalmon2",
									"lightsalmon3", "lightsalmon4", "lightseagreen", "lightskyblue", "lightskyblue1",
									"lightskyblue2", "lightskyblue3", "lightskyblue4", "lightslateblue", "lightslategray",
									"lightslategrey", "lightsteelblue", "lightsteelblue1", "lightsteelblue2", "lightsteelblue3",
									"lightsteelblue4", "lightyellow", "lightyellow1", "lightyellow2", "lightyellow3", "lightyellow4",
									",", ";", "]"], "lightblue",
		'''
			graph {
				1[ color=lightblue ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ color="lights«c»" ]
			}
		'''.testContentAssistant(#["lightsalmon", "lightsalmon1", "lightsalmon2", "lightsalmon3", "lightsalmon4", "lightseagreen",
									"lightskyblue", "lightskyblue1", "lightskyblue2", "lightskyblue3", "lightskyblue4", "lightslateblue",
									"lightslategray", "lightslategrey", "lightsteelblue", "lightsteelblue1", "lightsteelblue2",
									"lightsteelblue3", "lightsteelblue4"], "lightskyblue",
		'''
			graph {
				1[ color="lightskyblue" ]
			}
		''')

		// test local attribute value with local color scheme value
		'''
			graph {
				1[ colorscheme=brbg10 color=«c» ]
			}
		'''.testContentAssistant(#["#", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "10", '''
			graph {
				1[ colorscheme=brbg10 color=10 ]
			}
		''')

		// test local attribute value with global color scheme value
		'''
			graph{
				node[colorscheme=brbg10]
				1[color=«c»]
			}
		'''.testContentAssistant(#["#", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"], "10", '''
			graph{
				node[colorscheme=brbg10]
				1[color=10]
			}
		''')
	}

	@Test def node_colorscheme() {
		val expectedDotColorSchemes = copyExpectedDotColorSchemes
		// test global attribute values
		'''
			graph {
				node[ colorscheme=«c» ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "brbg10", '''
			graph {
				node[ colorscheme=brbg10 ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ colorscheme=«c» ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "brbg11", '''
			graph {
				1[ colorscheme=brbg11 ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ colorscheme="«c»" ]
			}
		'''.testContentAssistant(expectedDotColorSchemes, "brbg3", '''
			graph {
				1[ colorscheme="brbg3" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ colorscheme=br«c» ]
			}
		'''.testContentAssistant(#["brbg3", "brbg4", "brbg5", "brbg6", "brbg7", "brbg8", "brbg9", "brbg10", "brbg11", ",", ";", "]"], "brbg4", '''
			graph {
				1[ colorscheme=brbg4 ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ colorscheme="brbg«c»" ]
			}
		'''.testContentAssistant(#["brbg3", "brbg4", "brbg5", "brbg6", "brbg7", "brbg8", "brbg9", "brbg10", "brbg11"], "brbg5", '''
			graph {
				1[ colorscheme="brbg5" ]
			}
		''')
	}

	@Test def node_fillcolor() {
		// test global attribute values
		'''
			graph {
				node[ colorscheme=svg fillcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, "#", "/", ":", ";"), "#", '''
			graph {
				node[ colorscheme=svg fillcolor=# ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ colorscheme=svg fillcolor=«c» ]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, "#", "/", ":", ";"), "#", '''
			graph {
				1[ colorscheme=svg fillcolor=# ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ colorscheme=svg fillcolor="«c»" ]
			}
		'''.testContentAssistant(combine(expectedSvgColorNames, "#", "/", ":", ";"), "#", '''
			graph {
				1[ colorscheme=svg fillcolor="#" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ colorscheme=svg fillcolor=sa«c» ]
			}
		'''.testContentAssistant(#["saddlebrown", "salmon", "sandybrown", ":", ",", ";", "]"], "salmon", '''
			graph {
				1[ colorscheme=svg fillcolor=salmon ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ colorscheme=svg fillcolor="sa«c»" ]
			}
		'''.testContentAssistant(#["saddlebrown", "salmon", "sandybrown", ":", ";"], "sandybrown", '''
			graph {
				1[ colorscheme=svg fillcolor="sandybrown" ]
			}
		''')
	}

	@Test def node_fixedsize() {
		// test global attribute values
		'''
			graph {
				node[ fixedsize=«c» ]
			}
		'''.testContentAssistant(#["true", "false"], "true", '''
			graph {
				node[ fixedsize=true ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ fixedsize=«c» ]
			}
		'''.testContentAssistant(#["true", "false"], "false", '''
			graph {
				1[ fixedsize=false ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ fixedsize="«c»" ]
			}
		'''.testContentAssistant(#["true", "false"], "true", '''
			graph {
				1[ fixedsize="true" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ fixedsize=f«c» ]
			}
		'''.testContentAssistant(#["false", ",", ";", "]"], "false", '''
			graph {
				1[ fixedsize=false ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ fixedsize="t«c»" ]
			}
		'''.testContentAssistant(#["true"], "true", '''
			graph {
				1[ fixedsize="true" ]
			}
		''')
	}

	@Test def node_fontcolor() {
		val expectedDotColorSchemes = copyExpectedDotColorSchemes
		// test global attribute values
		'''
			graph {
				node[ fontcolor=/«c» ]
			}
		'''.testContentAssistant(combine(expectedDotColorSchemes, "/"), "accent3", '''
			graph {
				node[ fontcolor=/accent3 ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ fontcolor=/«c» ]
			}
		'''.testContentAssistant(combine(expectedDotColorSchemes, "/"), "accent3", '''
			graph {
				1[ fontcolor=/accent3 ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ fontcolor="/accent3/«c»" ]
			}
		'''.testContentAssistant(#["/", "1", "2", "3"], "1", '''
			graph {
				1[ fontcolor="/accent3/1" ]
			}
		''')

		// test local attribute values with quotes (case insensitively)
		'''
			graph {
				1[ fontcolor="/ACCENT3/«c»" ]
			}
		'''.testContentAssistant(#["/", "1", "2", "3"], "1", '''
			graph {
				1[ fontcolor="/ACCENT3/1" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[
					colorscheme=svg
					fontcolor=w«c»
				]
			}
		'''.testContentAssistant(#["wheat", "white", "whitesmoke", ",", ";", "]"], "white", '''
			graph {
				1[
					colorscheme=svg
					fontcolor=white
				]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ fontcolor="/accent«c»" ]
			}
		'''.testContentAssistant(#["accent3", "accent4", "accent5", "accent6", "accent7", "accent8", "/"], "white", '''
			graph {
				1[ fontcolor="/accent3" ]
			}
		''')
	}

	@Test def node_fontname() {
		// test global attribute values
		'''
			digraph {
				node[ fontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Helvetica", '''
			digraph {
				node[ fontname=Helvetica ]
			}
		''')

		// test local attribute values
		'''
			digraph {
				1[ fontname=«c» ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Courier-Bold", '''
			digraph {
				1[ fontname="Courier-Bold" ]
			}
		''')

		// test local attribute values with quotes
		'''
			digraph {
				1[ fontname="«c»" ]
			}
		'''.testContentAssistant(expectedPostScriptFontNames, "Times-Roman", '''
			digraph {
				1[ fontname="Times-Roman" ]
			}
		''')

		// test local attribute values with prefix
		'''
			digraph {
				1[ fontname=Zapf«c» ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic", ",", ";", "]"], "ZapfDingbats", '''
			digraph {
				1[ fontname=ZapfDingbats ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			digraph {
				1[ fontname="zapf«c»" ]
			}
		'''.testContentAssistant(#["ZapfDingbats", "ZapfChancery-MediumItalic"], "ZapfChancery-MediumItalic", '''
			digraph {
				1[ fontname="ZapfChancery-MediumItalic" ]
			}
		''')
	}

	@Test def node_label() {
		// test global attribute values
		'''
			graph {
				node[ label=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				node[ label=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ label=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				1[ label=<
					
				> ]
			}
		''')

		// test html-like label attribute
		'''
			graph {
				1[ label=«c»<> ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				1[ label=<
					
				><> ]
			}
		''')

		'''
			graph {
				1[ label=<
					«c»
				> ]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>",	"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
									"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<FONT></FONT>", '''
			graph {
				1[ label=<
					<FONT></FONT>
				> ]
			}
		''')

		// test html-like label children tags of TABLE tag
		'''
			graph {
				1[ label=<
					<TABLE>«c»</TABLE>
				> ]
			}
		'''.testContentAssistant(#["<HR/>", "<TR></TR>"], "<TR></TR>", '''
			graph {
				1[ label=<
					<TABLE><TR></TR></TABLE>
				> ]
			}
		''')

		// test html-like label children tags of TR tag
		'''
			graph {
				1[ label=<
					<TABLE>
						<TR>«c»</TR>
					</TABLE>
				> ]
			}
		'''.testContentAssistant(#["<VR/>", "<TD></TD>"], "<TD></TD>", '''
			graph {
				1[ label=<
					<TABLE>
						<TR><TD></TD></TR>
					</TABLE>
				> ]
			}
		''')

		// test html-like label children tags of TD tag
		'''
			graph {
				1[ label=<
					<TABLE>
						<TR>
							<TD>«c»</TD>
						</TR>
					</TABLE>
				> ]
			}
		'''.testContentAssistant(#["<IMG/>", "<BR/>", "<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
									"<SUB></SUB>", "<SUP></SUP>", "<S></S>", "<TABLE></TABLE>"], "<U></U>", '''
			graph {
				1[ label=<
					<TABLE>
						<TR>
							<TD><U></U></TD>
						</TR>
					</TABLE>
				> ]
			}
		''')

		// test html-like label attribute value
		'''
			graph {
				1[ label=<
					<BR ALIGN="«c»" />
				> ]
			}
		'''.testContentAssistant(#["CENTER", "LEFT", "RIGHT"], "CENTER", '''
			graph {
				1[ label=<
					<BR ALIGN="CENTER" />
				> ]
			}
		''')

		// test html-like label valid siblings
		'''
			graph {
				1[label = <
					<TABLE></TABLE>
					«c»
				>]
			}
		'''.testEmptyContentAssistant
	}

	@Test def node_pos() {
		// test global attribute values
		'''
			graph {
				node[ lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1[ lp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def node_shape() {
		// test global attribute values
		'''
			graph {
				node[ shape=«c» ]
			}
		'''.testContentAssistant(#["Mcircle", "Mdiamond", "Mrecord", "Msquare", "assembly", "box", "box3d", "cds", "circle",
									"component", "cylinder", "diamond", "doublecircle", "doubleoctagon", "egg", "ellipse",
									"fivepoverhang", "folder", "hexagon", "house", "insulator", "invhouse", "invtrapezium",
									"invtriangle", "larrow", "lpromoter", "none", "note", "noverhang", "octagon", "oval",
									"parallelogram", "pentagon", "plain", "plaintext", "point", "polygon", "primersite",
									"promoter", "proteasesite", "proteinstab", "rarrow", "record", "rect", "rectangle",
									"restrictionsite", "ribosite", "rnastab", "rpromoter", "septagon", "signature",
									"square", "star", "tab", "terminator", "threepoverhang", "trapezium", "triangle",
									"tripleoctagon", "underline", "utr"], "Mcircle",
		'''
			graph {
				node[ shape=Mcircle ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ shape=«c» ]
			}
		'''.testContentAssistant(#["Mcircle", "Mdiamond", "Mrecord", "Msquare", "assembly", "box", "box3d", "cds", "circle",
									"component", "cylinder", "diamond", "doublecircle", "doubleoctagon", "egg", "ellipse",
									"fivepoverhang", "folder", "hexagon", "house", "insulator", "invhouse", "invtrapezium",
									"invtriangle", "larrow", "lpromoter", "none", "note", "noverhang", "octagon", "oval",
									"parallelogram", "pentagon", "plain", "plaintext", "point", "polygon", "primersite",
									"promoter", "proteasesite", "proteinstab", "rarrow", "record", "rect", "rectangle",
									"restrictionsite", "ribosite", "rnastab", "rpromoter", "septagon", "signature",
									"square", "star", "tab", "terminator", "threepoverhang", "trapezium", "triangle",
									"tripleoctagon", "underline", "utr"], "Mdiamond",
		'''
			graph {
				1[ shape=Mdiamond ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ shape="«c»" ]
			}
		'''.testContentAssistant(#["Mcircle", "Mdiamond", "Mrecord", "Msquare", "assembly", "box", "box3d", "cds", "circle",
									"component", "cylinder", "diamond", "doublecircle", "doubleoctagon", "egg", "ellipse",
									"fivepoverhang", "folder", "hexagon", "house", "insulator", "invhouse", "invtrapezium",
									"invtriangle", "larrow", "lpromoter", "none", "note", "noverhang", "octagon", "oval",
									"parallelogram", "pentagon", "plain", "plaintext", "point", "polygon", "primersite",
									"promoter", "proteasesite", "proteinstab", "rarrow", "record", "rect", "rectangle",
									"restrictionsite", "ribosite", "rnastab", "rpromoter", "septagon", "signature",
									"square", "star", "tab", "terminator", "threepoverhang", "trapezium", "triangle",
									"tripleoctagon", "underline", "utr"], "Mrecord",
		'''
			graph {
				1[ shape="Mrecord" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ shape=pr«c» ]
			}
		'''.testContentAssistant(#["primersite", "promoter", "proteasesite", "proteinstab", ",", ";", "]"], "primersite", '''
			graph {
				1[ shape=primersite ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ shape="pro«c»" ]
			}
		'''.testContentAssistant(#["promoter", "proteasesite", "proteinstab"], "proteinstab", '''
			graph {
				1[ shape="proteinstab" ]
			}
		''')
	}

	@Test def node_style() {
		// test global attribute values
		'''
			graph {
				node[ style=«c» ]
			}
		'''.testContentAssistant(#["bold", "dashed", "diagonals", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped", "wedged"], "invis", '''
			graph {
				node[ style=invis ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ style=«c» ]
			}
		'''.testContentAssistant(#["bold", "dashed", "diagonals", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped", "wedged"], "radial", '''
			graph {
				1[ style=radial ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ style="«c»" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "diagonals", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped", "wedged"], "wedged", '''
			graph {
				1[ style="wedged" ]
			}
		''')

		// test local attribute values with prefix
		'''
			graph {
				1[ style=d«c» ]
			}
		'''.testContentAssistant(#["dashed", "diagonals", "dotted", ",", ";", "]"], "diagonals", '''
			graph {
				1[ style=diagonals ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ style="s«c»" ]
			}
		'''.testContentAssistant(#["solid", "striped"], "solid", '''
			graph {
				1[ style="solid" ]
			}
		''')

		// test local attribute values with list
		'''
			graph {
				1[ style="solid,«c»" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "diagonals", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped", "wedged"], "striped", '''
			graph {
				1[ style="solid,striped" ]
			}
		''')

		// test local attribute values with list and prefix
		'''
			graph {
				1[ style="solid,s«c»" ]
			}
		'''.testContentAssistant(#["solid", "striped"], "striped", '''
			graph {
				1[ style="solid,striped" ]
			}
		''')

		// test local attribute values with list and following attribute
		'''
			graph {
				1[ style="solid,«c»,bold" ]
			}
		'''.testContentAssistant(#["bold", "dashed", "diagonals", "dotted", "filled", "invis", "radial", "rounded", "solid", "striped", "wedged"], "striped", '''
			graph {
				1[ style="solid,striped,bold" ]
			}
		''')
	}

	@Test def node_tooltip() {
		// test global attribute values with quotes
		'''
			graph {
				node[ tooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\n", '''
			graph {
				node[ tooltip="\n" ]
			}
		''')

		// test local attribute values with quotes
		'''
			graph {
				1[ tooltip="«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\l", '''
			graph {
				1[ tooltip="\l" ]
			}
		''')

		// test local attribute values with quotes and prefix
		'''
			graph {
				1[ tooltip="line1«c»" ]
			}
		'''.testContentAssistant(#["\\n", "\\l", "\\r"], "\\r", '''
			graph {
				1[ tooltip="line1\r" ]
			}
		''')
	}

	@Test def node_xlabel() {
		// test global attribute values
		'''
			graph {
				node[ xlabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				node[ xlabel=<
					
				> ]
			}
		''')

		// test local attribute values
		'''
			graph {
				1[ xlabel=«c» ]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				1[ xlabel=<
					
				> ]
			}
		''')

		'''
			graph {
				1[
					xlabel=«c»
					shape=box
				]
			}
		'''.testContentAssistant(#["HTMLLabel - Insert a template"], "HTMLLabel - Insert a template", '''
			graph {
				1[
					xlabel=<
						
					>
					shape=box
				]
			}
		''')

		// test html-like label attribute
		'''
			graph {
				1[ xlabel=<«c»> ]
			}
		'''.testContentAssistant(#["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>", "<SUB></SUB>",
									"<SUP></SUP>", "<TABLE></TABLE>", "<U></U>"], "<BR/>",
		'''
			graph {
				1[ xlabel=<<BR/>> ]
			}
		''')
	}

	@Test def node_xlp() {
		// test global attribute values
		'''
			graph {
				node[ xlp=«c» ]
			}
		'''.testEmptyContentAssistant

		// test local attribute values
		'''
			graph {
				1[ xlp=«c» ]
			}
		'''.testEmptyContentAssistant

		// no use to test local attribute values with prefix
	}

	@Test def subgraph_edge_attribute_statement() {
		'''
			graph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#["cluster - Insert a template", "edge - Insert a template", "edge[]","graph[]", "node[]", "rank", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "edge[]", '''
			graph {
				{
					edge[]
				}
			}
		''')
	}

	@Test def subgraph_graph_attribute_statement() {
		'''
			graph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#["cluster - Insert a template", "edge - Insert a template", "edge[]","graph[]", "node[]", "rank", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "graph[]", '''
			graph {
				{
					graph[]
				}
			}
		''')
	}

	@Test def subgraph_node_attribute_statement() {
		'''
			graph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#["cluster - Insert a template", "edge - Insert a template", "edge[]","graph[]", "node[]", "rank", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "node[]", '''
			graph {
				{
					node[]
				}
			}
		''')
	}

	@Test def subgraph_rank001() {
		'''
			graph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#["cluster - Insert a template", "edge - Insert a template", "edge[]","graph[]", "node[]", "rank", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}"
		], "rank", '''
			graph {
				{
					rank=
				}
			}
		''')
	}

	@Test def subgraph_rank002() {
		//test global attribute value
		'''
			graph {
				{
					graph[rank=«c»]
				}
			}
		'''.testContentAssistant(#["max", "min", "same", "sink", "source"], "same", '''
			graph {
				{
					graph[rank=same]
				}
			}
		''')

		//test local attribute value
		'''
			graph {
				{
					rank=«c»
				}
			}
		'''.testContentAssistant(#["max", "min", "same", "sink", "source"], "sink", '''
			graph {
				{
					rank=sink
				}
			}
		''')

		//test local attribute value with quotes
		'''
			graph {
				{
					rank="«c»"
				}
			}
		'''.testContentAssistant(#["max", "min", "same", "sink", "source"], "source", '''
			graph {
				{
					rank="source"
				}
			}
		''')

		//test local attribute value with prefix
		'''
			graph {
				{
					rank=m«c»
				}
			}
		'''.testContentAssistant(#[";", "edge - Insert a template", "max", "min", "{", "}"], "min", '''
			graph {
				{
					rank=min
				}
			}
		''')

		//test local attribute value with prefix and quotes
		'''
			graph {
				{
					rank="m«c»"
				}
			}
		'''.testContentAssistant(#["max", "min"], "max", '''
			graph {
				{
					rank="max"
				}
			}
		''')
	}

	@Test def subgraph_template() {
		//test anonymous template
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template",
									"{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir",
									"rankdir", "splines", "style", "edge - Insert a template"], "subgraph - Insert a template",
		'''
			graph {
				subgraph {
					content
				}
			}
		''')

		'''
			graph {
				1
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "=", "[", "bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder",
									"pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "subgraph - Insert a template",
		'''
			graph {
				1
				subgraph {
					content
				}
			}
		''')

		//test named template
		'''
			graph {
				«c»
			}
		'''.testContentAssistant(#["bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]", "node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template",
									"{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder", "pagedir",
									"rankdir", "splines", "style", "edge - Insert a template"], "subgraph (named) - Insert a template",
		'''
			graph {
				subgraph name {
					content
				}
			}
		''')

		'''
			graph {
				1
				«c»
			}
		'''.testContentAssistant(#["--", ":", ";", "=", "[", "bb", "bgcolor", "cluster - Insert a template", "clusterrank", "colorscheme", "fontcolor", "fontname", "fontsize", "edge[]", "graph[]",
									"node[]", "subgraph", "subgraph (named) - Insert a template", "subgraph - Insert a template", "{", "}", "forcelabels", "id", "label", "layout", "lp", "nodesep", "outputorder",
									"pagedir", "rankdir", "splines", "style", "edge - Insert a template"], "subgraph (named) - Insert a template",
		'''
			graph {
				1
				subgraph name {
					content
				}
			}
		''')
	}

	private def testEmptyContentAssistant(CharSequence it) {
		testContentAssistant(#[], null, null)
	}

	private def void testContentAssistant(CharSequence text, List<String> expectedProposals,
		String proposalToApply, String expectedContent) {

		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition == -1) {
			fail("Can't locate cursor position symbols '" + c + "' in the input text.")
		}

		val content = text.toString.replace(c, "")

		val builder = newBuilder.append(content).assertTextAtCursorPosition(cursorPosition, expectedProposals)

		if(proposalToApply !== null) {
			builder.applyProposal(cursorPosition, proposalToApply).expectContent(expectedContent)
		}
	}

	/**
	 * Since the Content Assists Test Cases sorts the expectations array, the
	 * array of the expected color schemes has to be copied in order to be able
	 * to reuse that array in other test cases as well.
	 *
	 * @return The copy of the expected color schemes
	 */
	private def copyExpectedDotColorSchemes() {
		expectedDotColorSchemes.copy
	}

	private def String[] copy(String[] src) {
		val dest = newArrayOfSize(src.length)
		System.arraycopy(src, 0, dest, 0, src.length)
		dest
	}

	@Inject Injector injector

	override protected newBuilder() {
		return new ContentAssistProcessorTestBuilder(injector, this) {

			protected override applyProposal(ICompletionProposal proposal, int position, IXtextDocument document) {
				val shell = new Shell
				try {
					val configuration = injector.getInstance(XtextSourceViewerConfiguration)
					val sourceViewer = getSourceViewer(shell, document, configuration)
					// use appendAndApplyProposal as a workaround
					// use null model, as document already contains model
					return appendAndApplyProposal(proposal, sourceViewer, null, position)
				} finally {
					shell.dispose
				}
			}

			/*
			 * Configure the ContentAssistProcessorTestBuilder to consider only
			 * the first part of the displayString of a proposal and ignore its
			 * replacement strings when determining the proposed text.
			 */
			override protected getProposedText(ICompletionProposal proposal) {
				val displayString = proposal.displayString
				if(":".equals(displayString)) {
					displayString
				}
				else displayString.split(":").head
			}

		}
	}
}
