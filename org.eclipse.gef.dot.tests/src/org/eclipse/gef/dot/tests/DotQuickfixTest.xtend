/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *    Zoey G. Prigge (itemis AG) - quickfix to remove redundant attributes (bug #540330)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Injector
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.gef.dot.internal.DotAttributes
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.ui.editor.model.IXtextDocument
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.validation.DotValidator.INVALID_EDGE_OPERATOR
import static org.eclipse.gef.dot.internal.language.validation.DotValidator.REDUNDANT_ATTRIBUTE

import static extension org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils.getDocument
import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotQuickfixTest {

	@Inject Injector injector
	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper
	@Inject extension IssueResolutionProvider

	val deprecatedArrowShapes = #["ediamond", "open", "halfopen", "empty", "invempty"]
	val validArrowShapes = #["odiamond", "vee", "lvee", "onormal", "oinv"]

	val validNodeShapes = #[ "box", "polygon", "ellipse", "oval", "circle", "point", "egg", "triangle", "plaintext", "plain",
		"diamond", "trapezium", "parallelogram", "house", "pentagon", "hexagon", "septagon", "octagon", "doublecircle", "doubleoctagon",
		"tripleoctagon", "invtriangle", "invtrapezium", "invhouse", "Mdiamond", "Msquare", "Mcircle", "rect", "rectangle", "square", "star",
		"none", "underline", "cylinder", "note", "tab", "folder", "box3d", "component", "promoter", "cds", "terminator", "utr", "primersite",
		"restrictionsite", "fivepoverhang", "threepoverhang", "noverhang", "assembly", "signature", "insulator", "ribosite", "rnastab",
		"proteasesite", "proteinstab", "rpromoter", "rarrow", "larrow", "lpromoter", "record", "Mrecord"]

	@Test def graph_contains_directed_edge_to_node() {
		'''
			graph {
				1->2
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1--2
			}
		'''))
	}

	@Test def graph_contains_directed_multi_edge_to_node_1() {
		'''
			graph {
				1->2--3
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1--2--3
			}
		'''))
	}

	@Test def graph_contains_directed_multi_edge_to_node_2() {
		'''
			graph {
				1--2->3
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1--2--3
			}
		'''))
	}

	@Test def graph_contains_directed_edge_to_subgraph() {
		'''
			graph {
				1 -> subgraph {
					2 3
				}
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1 -- subgraph {
					2 3
				}
			}
		'''))
	}

	@Test def graph_contains_directed_multi_edge_to_subgraph_1() {
		'''
			graph {
				1 -> subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1 -- subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''))
	}

	@Test def graph_contains_directed_multi_edge_to_subgraph_2() {
		'''
			graph {
				1 -- subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '->' with '--'.", "Use valid '--' instead of invalid '->' edge operator.", '''
			graph {
				1 -- subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''))
	}

	@Test def digraph_contains_undirected_edge_to_node() {
		'''
			digraph {
				1--2
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1->2
			}
		'''))
	}

	@Test def digraph_contains_undirected_multi_edge_to_node_1() {
		'''
			digraph {
				1--2->3
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1->2->3
			}
		'''))
	}

	@Test def digraph_contains_undirected_multi_edge_to_node_2() {
		'''
			digraph {
				1->2--3
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1->2->3
			}
		'''))
	}

	@Test def digraph_contains_undirected_edge_to_subgraph() {
		'''
			digraph {
				1 -- subgraph {
					2 3
				}
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1 -> subgraph {
					2 3
				}
			}
		'''))
	}

	@Test def digraph_contains_undirected_multi_edge_to_subgraph_1() {
		'''
			digraph {
				1 -- subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1 -> subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''))
	}

	@Test def digraph_contains_undirected_multi_edge_to_subgraph_2() {
		'''
			digraph {
				1 -> subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''.testQuickfixesOn(INVALID_EDGE_OPERATOR, new Quickfix("Replace '--' with '->'.", "Use valid '->' instead of invalid '--' edge operator.", '''
			digraph {
				1 -> subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''))
	}

	@Test def edge_arrowhead_001() {
		// test unquoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead=«deprecatedArrowShape»]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',
					'''digraph{1->2[arrowhead=«validArrowShape»]}'''
				)
			)
		}
	}

	@Test def edge_arrowhead_002() {
		// test quoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead="«deprecatedArrowShape»"]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',
					'''digraph{1->2[arrowhead="«validArrowShape»"]}'''
				)
			)
		}
	}

	@Test def edge_arrowhead_003() {
		// test quoted attribute value with multiple arrowtypes (one of them is invalid)
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead="onormal«deprecatedArrowShape»"]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',
					'''digraph{1->2[arrowhead="onormal«validArrowShape»"]}'''
				)
			)
		}
	}

	@Test def edge_arrowhead_004() {
		'''graph {1--2[ arrowhead=ocrow ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowhead=crow ]}'''))
	}

	@Test def edge_arrowhead_005() {
		'''graph {1--2[ arrowhead=dotoicurve ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowhead=doticurve ]}'''))
	}

	@Test def edge_arrowhead_006() {
		'''graph {1--2[ arrowhead="ocrow" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowhead="crow" ]}'''))
	}

	@Test def edge_arrowhead_007() {
		'''graph {1--2[ arrowhead="dotoicurve" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowhead="doticurve" ]}'''))
	}

	@Test def edge_arrowhead_008() {
		'''graph {1--2[ arrowhead=ldot ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'l' modifier.", "Remove the invalid 'l' modifier.", '''graph {1--2[ arrowhead=dot ]}'''))
	}

	@Test def edge_arrowhead_009() {
		'''graph {1--2[ arrowhead=dotldot ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'l' modifier.", "Remove the invalid 'l' modifier.", '''graph {1--2[ arrowhead=dotdot ]}'''))
	}

	@Test def edge_arrowhead_010() {
		'''graph {1--2[ arrowhead="rdot" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'r' modifier.", "Remove the invalid 'r' modifier.", '''graph {1--2[ arrowhead="dot" ]}'''))
	}

	@Test def edge_arrowhead_011() {
		'''graph {1--2[ arrowhead="boxrdot" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'r' modifier.", "Remove the invalid 'r' modifier.", '''graph {1--2[ arrowhead="boxdot" ]}'''))
	}

	@Test def edge_arrowhead_012() {
		'''graph {1--2[ arrowhead=boxnone ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''graph {1--2[ arrowhead=box ]}'''))
	}

	@Test def edge_arrowhead_013() {
		'''graph {1--2[ arrowhead=nonenone ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''graph {1--2[ arrowhead=none ]}'''))
	}

	@Test def edge_arrowhead_014() {
		'''graph {1--2[ arrowhead="dotnone" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''graph {1--2[ arrowhead="dot" ]}'''))
	}

	@Test def edge_arrowhead_015() {
		'''graph {1--2[ arrowhead="nonenone" ]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''graph {1--2[ arrowhead="none" ]}'''))
	}

	@Test def edge_arrowtail_001() {
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail=«deprecatedArrowShape»]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',
					'''digraph{1->2[arrowtail=«validArrowShape»]}'''
				)
			)
		}
	}

	@Test def edge_arrowtail_002() {
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail="«deprecatedArrowShape»"]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',
					'''digraph{1->2[arrowtail="«validArrowShape»"]}'''
				)
			)
		}
	}

	@Test def edge_arrowtail_003() {
		// test quoted attribute value with multiple arrowtypes (one of them is invalid)
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail="«deprecatedArrowShape»dot"]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',
					'''digraph{1->2[arrowtail="«validArrowShape»dot"]}'''
				)
			)
		}
	}

	@Test def edge_arrowtail_004() {
		'''graph {1--2[ arrowtail=ovee ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowtail=vee ]}'''))
	}

	@Test def edge_arrowtail_005() {
		'''graph {1--2[ arrowtail=dototee ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowtail=dottee ]}'''))
	}

	@Test def edge_arrowtail_006() {
		'''graph {1--2[ arrowtail="ononebox" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowtail="nonebox" ]}'''))
	}

	@Test def edge_arrowtail_007() {
		'''graph {1--2[ arrowtail="ononedot" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'o' modifier.", "Remove the invalid 'o' modifier.", '''graph {1--2[ arrowtail="nonedot" ]}'''))
	}

	@Test def edge_arrowtail_008() {
		'''graph {1--2[ arrowtail=lnonedot ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'l' modifier.", "Remove the invalid 'l' modifier.", '''graph {1--2[ arrowtail=nonedot ]}'''))
	}

	@Test def edge_arrowtail_009() {
		'''graph {1--2[ arrowtail=dotlnonedot ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'l' modifier.", "Remove the invalid 'l' modifier.", '''graph {1--2[ arrowtail=dotnonedot ]}'''))
	}

	@Test def edge_arrowtail_010() {
		'''graph {1--2[ arrowtail="rnonedot" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'r' modifier.", "Remove the invalid 'r' modifier.", '''graph {1--2[ arrowtail="nonedot" ]}'''))
	}

	@Test def edge_arrowtail_011() {
		'''graph {1--2[ arrowtail="boxrnonedot" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'r' modifier.", "Remove the invalid 'r' modifier.", '''graph {1--2[ arrowtail="boxnonedot" ]}'''))
	}

	@Test def edge_arrowtail_012() {
		'''digraph {1->2[ arrowtail=boxnone ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''digraph {1->2[ arrowtail=box ]}'''))
	}

	@Test def edge_arrowtail_013() {
		'''digraph {1->2[ arrowtail=nonenone ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''digraph {1->2[ arrowtail=none ]}'''))
	}

	@Test def edge_arrowtail_014() {
		'''digraph {1->2[ arrowtail="dotnone" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''digraph {1->2[ arrowtail="dot" ]}'''))
	}

	@Test def edge_arrowtail_015() {
		'''digraph {1->2[ arrowtail="nonenone" ]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E, new Quickfix("Remove the 'none' arrow shape.", "Remove the last 'none' arrow shape.", '''digraph {1->2[ arrowtail="none" ]}'''))
	}

	@Test def edge_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{1--2[colorscheme=«validColorScheme»]}'''
			))
		}
		'''graph{1--2[colorscheme=foo]}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def edge_dir_001() {
		'''graph{1--2[dir=foo]}'''.testQuickfixesOn(DotAttributes.DIR__E,
			new Quickfix("Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	"graph{1--2[dir=forward]}"),
			new Quickfix("Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=back]}"),
			new Quickfix("Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=both]}"),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=none]}")
		)
	}

	@Test def edge_dir_002() {
		'''graph{1--2[dir="foo"]}'''.testQuickfixesOn(DotAttributes.DIR__E,
			new Quickfix("Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	'''graph{1--2[dir="forward"]}'''),
			new Quickfix("Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="back"]}'''),
			new Quickfix("Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="both"]}'''),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="none"]}''')
		)
	}

	@Test def edge_style_001() {
		'''graph{1--2[style=foo]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	"graph{1--2[style=bold]}"),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	"graph{1--2[style=dashed]}"),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	"graph{1--2[style=dotted]}"),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	"graph{1--2[style=invis]}"),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	"graph{1--2[style=solid]}"),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	"graph{1--2[style=tapered]}")
		)
	}

	@Test def edge_style_002() {
		'''graph{1--2[style="foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dashed"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dotted"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	'''graph{1--2[style="invis"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	'''graph{1--2[style="solid"]}'''),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	'''graph{1--2[style="tapered"]}''')
		)
	}

	@Test def edge_style_003() {
		'''graph{1--2[style="bold, bold"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'bold' style attribute value.", "Remove the redundant 'bold' style attribute value.", '''graph{1--2[style="bold"]}'''))
	}

	@Test def edge_style_004() {
		'''graph{1--2[style="dashed,dashed"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dashed' style attribute value.", "Remove the redundant 'dashed' style attribute value.", '''graph{1--2[style="dashed"]}'''))
	}

	@Test def edge_style_005() {
		'''graph{1--2[style="dashed,bold,dashed"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dashed' style attribute value.", "Remove the redundant 'dashed' style attribute value.", '''graph{1--2[style="bold,dashed"]}'''))
	}

	@Test def edge_style_006() {
		// test quoted attribute value with multiple styles (one of them is invalid)
		'''graph{1--2[style="foo, bold"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dashed, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dotted, bold"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	'''graph{1--2[style="invis, bold"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	'''graph{1--2[style="solid, bold"]}'''),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	'''graph{1--2[style="tapered, bold"]}''')
		)
	}

	@Test def edge_style_007() {
		'''graph{1--2[style="bold, foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, dashed"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, dotted"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, invis"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, solid"]}'''),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold, tapered"]}''')
		)
	}

	@Test def edge_style_008() {
		'''graph{1--2[style="setlinewidth(3)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1--2[ penwidth="3" ]}'''))
	}

	@Test def edge_style_009() {
		'''graph{1--2[style="dotted, setlinewidth(3)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1--2[style="dotted" penwidth="3" ]}'''))
	}

	@Test def edge_style_010() {
		'''graph{1--2[style="setlinewidth(3), dotted"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1--2[style="dotted" penwidth="3" ]}'''))
	}

	@Test def edge_style_011() {
		'''graph{1--2[style="dashed, setlinewidth(3), dotted"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1--2[style="dashed, dotted" penwidth="3" ]}'''))
	}

	@Test def graph_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{colorscheme=«validColorScheme»}'''
			))
		}
		'''graph{colorscheme=foo}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def graph_clusterrank_001() {
		'''graph{clusterrank=foo}'''.testQuickfixesOn(DotAttributes.CLUSTERRANK__G,
			new Quickfix("Replace 'foo' with 'local'.",		"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=local}"),
			new Quickfix("Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=global}"),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		"graph{clusterrank=none}")
		)
	}

	@Test def graph_clusterrank_002() {
		'''graph{clusterrank="foo"}'''.testQuickfixesOn(DotAttributes.CLUSTERRANK__G,
			new Quickfix("Replace 'foo' with 'local'.",		"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="local"}'''),
			new Quickfix("Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="global"}'''),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		'''graph{clusterrank="none"}''')
		)
	}

	@Test def graph_layout_001() {
		'''graph{layout=foo}'''.testQuickfixesOn(DotAttributes.LAYOUT__G,
			new Quickfix("Replace 'foo' with 'circo'.",		"Use valid 'circo' instead of invalid 'foo' graph layout.",	"graph{layout=circo}"),
			new Quickfix("Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	"graph{layout=dot}"),
			new Quickfix("Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	"graph{layout=fdp}"),
			new Quickfix("Replace 'foo' with 'neato'.",		"Use valid 'neato' instead of invalid 'foo' graph layout.",	"graph{layout=neato}"),
			new Quickfix("Replace 'foo' with 'osage'.",		"Use valid 'osage' instead of invalid 'foo' graph layout.",	"graph{layout=osage}"),
			new Quickfix("Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	"graph{layout=sfdp}"),
			new Quickfix("Replace 'foo' with 'twopi'.",		"Use valid 'twopi' instead of invalid 'foo' graph layout.",	"graph{layout=twopi}")
		)
	}

	@Test def graph_layout_002() {
		'''graph{layout="foo"}'''.testQuickfixesOn(DotAttributes.LAYOUT__G,
			new Quickfix("Replace 'foo' with 'circo'.",		"Use valid 'circo' instead of invalid 'foo' graph layout.",	'''graph{layout="circo"}'''),
			new Quickfix("Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	'''graph{layout="dot"}'''),
			new Quickfix("Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	'''graph{layout="fdp"}'''),
			new Quickfix("Replace 'foo' with 'neato'.",		"Use valid 'neato' instead of invalid 'foo' graph layout.",	'''graph{layout="neato"}'''),
			new Quickfix("Replace 'foo' with 'osage'.",		"Use valid 'osage' instead of invalid 'foo' graph layout.",	'''graph{layout="osage"}'''),
			new Quickfix("Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	'''graph{layout="sfdp"}'''),
			new Quickfix("Replace 'foo' with 'twopi'.",		"Use valid 'twopi' instead of invalid 'foo' graph layout.",	'''graph{layout="twopi"}''')
		)
	}

	@Test def graph_outputorder_001() {
		'''graph{outputorder=foo}'''.testQuickfixesOn(DotAttributes.OUTPUTORDER__G,
			new Quickfix("Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=breadthfirst}"),
			new Quickfix("Replace 'foo' with 'nodesfirst'.", 	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=nodesfirst}"),
			new Quickfix("Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=edgesfirst}")
		)
	}

	@Test def graph_outputorder_002() {
		'''graph{outputorder="foo"}'''.testQuickfixesOn(DotAttributes.OUTPUTORDER__G,
			new Quickfix("Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="breadthfirst"}'''),
			new Quickfix("Replace 'foo' with 'nodesfirst'.",  	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="nodesfirst"}'''),
			new Quickfix("Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="edgesfirst"}''')
		)
	}

	@Test def graph_pagedir_001() {
		'''graph{pagedir=foo}'''.testQuickfixesOn(DotAttributes.PAGEDIR__G,
			new Quickfix("Replace 'foo' with 'BL'.", 	"Use valid 'BL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BL}"),
			new Quickfix("Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BR}"),
			new Quickfix("Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TL}"),
			new Quickfix("Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TR}"),
			new Quickfix("Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RB}"),
			new Quickfix("Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RT}"),
			new Quickfix("Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LB}"),
			new Quickfix("Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LT}")
		)
	}

	@Test def graph_pagedir_002() {
		'''graph{pagedir="foo"}'''.testQuickfixesOn(DotAttributes.PAGEDIR__G,
			new Quickfix("Replace 'foo' with 'BL'.", 	"Use valid 'BL' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="BL"}'''),
			new Quickfix("Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="BR"}'''),
			new Quickfix("Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TL"}'''),
			new Quickfix("Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TR"}'''),
			new Quickfix("Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RB"}'''),
			new Quickfix("Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RT"}'''),
			new Quickfix("Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LB"}'''),
			new Quickfix("Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LT"}''')
		)
	}

	@Test def graph_rankdir_001() {
		'''graph{rankdir=foo}'''.testQuickfixesOn(DotAttributes.RANKDIR__G,
			new Quickfix("Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", "graph{rankdir=TB}"),
			new Quickfix("Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", "graph{rankdir=LR}"),
			new Quickfix("Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", "graph{rankdir=BT}"),
			new Quickfix("Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", "graph{rankdir=RL}")
		)
	}

	@Test def graph_rankdir_002() {
		'''graph{rankdir="foo"}'''.testQuickfixesOn(DotAttributes.RANKDIR__G,
			new Quickfix("Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="TB"}'''),
			new Quickfix("Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="LR"}'''),
			new Quickfix("Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="BT"}'''),
			new Quickfix("Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="RL"}''')
		)
	}

	@Test def node_colorscheme() {
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{node[colorscheme=«validColorScheme»]}'''
			))
		}

		'''graph{node[colorscheme=foo]}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def node_shape_001() {
		val List<Quickfix> expectedQuickfixes = newArrayList
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validNodeShape»'.''',
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',
				'''graph{1[shape=«validNodeShape»]}'''
			))
		}

		'''graph{1[shape=foo]}'''.testQuickfixesOn(DotAttributes.SHAPE__N, expectedQuickfixes)
	}

	@Test def node_shape_002() {
		val List<Quickfix> expectedQuickfixes = newArrayList
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validNodeShape»'.''',
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',
				'''graph{1[shape="«validNodeShape»"]}'''
			))
		}

		'''graph{1[shape="foo"]}'''.testQuickfixesOn(DotAttributes.SHAPE__N, expectedQuickfixes)
	}

	@Test def node_style_001() {
		'''graph{1[style=foo]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.",		"graph{1[style=bold]}"),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.",		"graph{1[style=dashed]}"),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	"graph{1[style=diagonals]}"),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.",		"graph{1[style=dotted]}"),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		"graph{1[style=filled]}"),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' node style.",		"graph{1[style=invis]}"),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		"graph{1[style=radial]}"),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		"graph{1[style=rounded]}"),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' node style.",		"graph{1[style=solid]}"),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		"graph{1[style=striped]}"),
			new Quickfix("Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		"graph{1[style=wedged]}")
		)
	}

	@Test def node_style_002() {
		'''graph{1[style="foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.",		'''graph{1[style="bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.",		'''graph{1[style="dashed"]}'''),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	'''graph{1[style="diagonals"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.",		'''graph{1[style="dotted"]}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		'''graph{1[style="filled"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' node style.",		'''graph{1[style="invis"]}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		'''graph{1[style="radial"]}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		'''graph{1[style="rounded"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' node style.",		'''graph{1[style="solid"]}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		'''graph{1[style="striped"]}'''),
			new Quickfix("Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		'''graph{1[style="wedged"]}''')
		)
	}

	@Test def node_style_003() {
		'''graph{1[style="dashed, dashed"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dashed' style attribute value.", "Remove the redundant 'dashed' style attribute value.", '''graph{1[style="dashed"]}'''))
	}

	@Test def node_style_004() {
		'''graph{1[style="dotted,dotted"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dotted' style attribute value.", "Remove the redundant 'dotted' style attribute value.", '''graph{1[style="dotted"]}'''))
	}

	@Test def node_style_005() {
		'''graph{1[style="dashed,bold,dashed"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dashed' style attribute value.", "Remove the redundant 'dashed' style attribute value.", '''graph{1[style="bold,dashed"]}'''))
	}

	@Test def node_style_006() {
		'''graph{1[style="foo, bold"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.",		'''graph{1[style="bold, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.",		'''graph{1[style="dashed, bold"]}'''),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	'''graph{1[style="diagonals, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.",		'''graph{1[style="dotted, bold"]}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		'''graph{1[style="filled, bold"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' node style.",		'''graph{1[style="invis, bold"]}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		'''graph{1[style="radial, bold"]}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		'''graph{1[style="rounded, bold"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' node style.",		'''graph{1[style="solid, bold"]}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		'''graph{1[style="striped, bold"]}'''),
			new Quickfix("Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		'''graph{1[style="wedged, bold"]}''')
		)
	}

	@Test def node_style_007() {
		'''graph{1[style="bold, foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.",		'''graph{1[style="bold, bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.",		'''graph{1[style="bold, dashed"]}'''),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	'''graph{1[style="bold, diagonals"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.",		'''graph{1[style="bold, dotted"]}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		'''graph{1[style="bold, filled"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' node style.",		'''graph{1[style="bold, invis"]}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		'''graph{1[style="bold, radial"]}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		'''graph{1[style="bold, rounded"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' node style.",		'''graph{1[style="bold, solid"]}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		'''graph{1[style="bold, striped"]}'''),
			new Quickfix("Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		'''graph{1[style="bold, wedged"]}''')
		)
	}

	@Test def node_style_008() {
		// test incomplete attribute value - no quickfixes should be offered
		'''graph{1[style="bold, "]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE)
	}

	@Test def node_style_009() {
		'''graph{1[style="setlinewidth(4)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(4)' with 'penwidth=4'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1[ penwidth="4" ]}'''))
	}

	@Test def node_style_010() {
		'''graph{1[style="dotted, setlinewidth(3)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1[style="dotted" penwidth="3" ]}'''))
	}

	@Test def node_style_011() {
		'''graph{1[style="setlinewidth(3), dotted"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1[style="dotted" penwidth="3" ]}'''))
	}

	@Test def node_style_012() {
		'''graph{1[style="dashed, setlinewidth(3), dotted"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1[style="dashed, dotted" penwidth="3" ]}'''))
	}

	@Test def node_style_013() {
		'''graph{1[style="setlinewidth"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth' with 'penwidth='.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''graph{1[ penwidth="" ]}'''))
	}

	@Test def subgraph_rank_001() {
		// test unquoted attribute value
		'''graph{subgraph{rank=foo}}'''.testQuickfixesOn(DotAttributes.RANK__S,
			new Quickfix("Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=same}}"),
			new Quickfix("Replace 'foo' with 'min'.",		"Use valid 'min' instead of invalid 'foo' subgraph rankType.", 		"graph{subgraph{rank=min}}"),
			new Quickfix("Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	"graph{subgraph{rank=source}}"),
			new Quickfix("Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=max}}"),
			new Quickfix("Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=sink}}")
		)
	}

	@Test def subgraph_rank_002() {
		// test quoted attribute value
		'''graph{subgraph{rank="foo"}}'''.testQuickfixesOn(DotAttributes.RANK__S,
			new Quickfix("Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="same"}}'''),
			new Quickfix("Replace 'foo' with 'min'.",		"Use valid 'min' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="min"}}'''),
			new Quickfix("Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	'''graph{subgraph{rank="source"}}'''),
			new Quickfix("Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="max"}}'''),
			new Quickfix("Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="sink"}}''')
		)
	}

	@Test def cluster_style_001() {
		// test unquoted attribute value
		'''graph{subgraph cluster_0{style=foo}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=bold}}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=dashed}}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=dotted}}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=filled}}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=invis}}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=radial}}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style=rounded}}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style=solid}}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style=striped}}''')
		)
	}

	@Test def cluster_style_002() {
		// test quoted attribute value
		'''graph{subgraph cluster_0{style="foo"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="bold"}}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="dashed"}}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="dotted"}}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="filled"}}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="invis"}}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="radial"}}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="rounded"}}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="solid"}}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="striped"}}''')
		)
	}

	@Test def cluster_style_003() {
		// test quoted attribute value with multiple styles
		'''graph{subgraph cluster_0{style="bold, bold"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'bold' style attribute value.", "Remove the redundant 'bold' style attribute value.", '''graph{subgraph cluster_0{style="bold"}}'''))
	}

	@Test def cluster_style_004() {
		'''graph{subgraph cluster_0{style="radial,radial"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'radial' style attribute value.", "Remove the redundant 'radial' style attribute value.", '''graph{subgraph cluster_0{style="radial"}}'''))
	}

	@Test def cluster_style_005() {
		'''graph{subgraph cluster_0{style="dashed,bold,dashed"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Remove 'dashed' style attribute value.", "Remove the redundant 'dashed' style attribute value.", '''graph{subgraph cluster_0{style="bold,dashed"}}'''))
	}

	@Test def cluster_style_006() {
		// test quoted attribute value with multiple styles (one of them is invalid)
		'''graph{subgraph cluster_0{style="foo, striped"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="bold, striped"}}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="dashed, striped"}}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="dotted, striped"}}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="filled, striped"}}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="invis, striped"}}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="radial, striped"}}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="rounded, striped"}}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="solid, striped"}}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="striped, striped"}}''')
		)
	}

	@Test def cluster_style_007() {
		'''graph{subgraph cluster_0{style="striped, foo"}}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, bold"}}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, dashed"}}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, dotted"}}'''),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, filled"}}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, invis"}}'''),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, radial"}}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="striped, rounded"}}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' graph style.",	'''graph{subgraph cluster_0{style="striped, solid"}}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' graph style.",'''graph{subgraph cluster_0{style="striped, striped"}}''')
		)
	}

	@Test def cluster_style_008() {
		// test deprecated attribute value
		'''
			graph {
				subgraph clustser_0 {
					style="setlinewidth(4)"
				}
			}
		'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(4)' with 'penwidth=4'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''
			graph {
				subgraph clustser_0 {
				penwidth="4"
				}
			}
		'''))
	}

	@Test def cluster_style_009() {
		'''
			graph {
				subgraph clustser_0 {
					style="dotted, setlinewidth(3)"
				}
			}
		'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''
			graph {
				subgraph clustser_0 {
					style="dotted"
				penwidth="3"
			}
			}
		'''))
	}

	@Test def cluster_style_010() {
		'''
			graph {
				subgraph clustser_0 {
					style="setlinewidth(3), dotted"
				}
			}
		'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''
			graph {
				subgraph clustser_0 {
					style="dotted"
				penwidth="3"
			}
			}
		'''))
	}

	@Test def cluster_style_011() {
		'''
			graph {
				subgraph clustser_0 {
					style="dashed, setlinewidth(3), dotted"
				}
			}
		'''.testQuickfixesOn(DotAttributes.STYLE__GCNE, new Quickfix("Replace 'setlinewidth(3)' with 'penwidth=3'.", "Use the 'penwidth' attribute instead of the deprecated 'setlinewidth' style.", '''
			graph {
				subgraph clustser_0 {
					style="dashed, dotted"
				penwidth="3"
			}
			}
		'''))
	}

	@Test def redundant_attribute_single() {
		'''graph{1[label="foo", label="faa"]}'''.testQuickfixesOn(REDUNDANT_ATTRIBUTE, new Quickfix("Remove 'label' attribute.", "Remove the redundant 'label' attribute.", '''graph{1[label="faa"]}'''))
	}

	@Test def redundant_attribute_mixed() {
		'''graph{1[label="foo", style="rounded", label="faa"]}'''.testQuickfixesOn(REDUNDANT_ATTRIBUTE, new Quickfix("Remove 'label' attribute.", "Remove the redundant 'label' attribute.", '''graph{1[style="rounded", label="faa"]}'''))
	}

	@Test def redundant_attribute_edge() {
		'''graph{1--2[style="dotted", style="dashed"]}'''.testQuickfixesOn(REDUNDANT_ATTRIBUTE, new Quickfix("Remove 'style' attribute.", "Remove the redundant 'style' attribute.", '''graph{1--2[style="dashed"]}'''))
	}

	@Test def redundant_attribute_attr_stmt() {
		'''graph{graph[label="dotted", label="dashed"]1}'''.testQuickfixesOn(REDUNDANT_ATTRIBUTE, new Quickfix("Remove 'label' attribute.", "Remove the redundant 'label' attribute.", '''graph{graph[label="dashed"]1}'''))
	}

	/**
	  * Test that the expected quickfixes are offered on a given validation issue in a given DSL text.
	  * 
	  * @param it The initial DSL text.
	  * @param expected The quickfixes that are expected to be offered on the given <code>issueCode</code>.
	  * Each expected quickfix should be described by the following triple:
	  * <ol>
	  * 	<li>the quickfix label</li>
	  * 	<li>the quickfix description</li>
	  * 	<li>the DSL text after the quickfix application</li>
	  * </ol>
	  */
	private def testQuickfixesOn(CharSequence it, String issueCode, Quickfix... expected) {
		val issue = getValidationIssue(issueCode)
		val actualIssueResolutions = issue.getResolutions
		assertEquals("The number of quickfixes does not match!", expected.size, actualIssueResolutions.size)
		for (i : 0..< actualIssueResolutions.size) {
			val actualIssueResolution = actualIssueResolutions.get(i)
			val expectedIssueResolution = expected.get(i)
			expectedIssueResolution.label.assertEquals(actualIssueResolution.label)
			expectedIssueResolution.description.assertEquals(actualIssueResolution.getDescription)
			expectedIssueResolution.result.assertIssueResolutionResult(actualIssueResolution, toString)
		}
	}

	private def getValidationIssue(CharSequence it, String issueCode) {
		val issues = parse.validate
		val issueCandidates = issues.filter[code == issueCode]
		assertEquals("There should be one '" + issueCode + "' validation issue!", 1, issueCandidates.size)
		issueCandidates.head
	}

	private def assertIssueResolutionResult(String expectedResult, IssueResolution actualIssueResolution, String originalText) {
		/*
		 * manually create an IModificationContext with a XtextDocument and call the
		 * apply method of the actualIssueResolution with that IModificationContext
		 */
		val document = injector.getDocument(originalText)
		val modificationContext = new TestModificationContext
		modificationContext.document = document
		new IssueResolution(actualIssueResolution.label, actualIssueResolution.description,
			actualIssueResolution.image, modificationContext, actualIssueResolution.modification,
			actualIssueResolution.relevance).apply
		val actualResult = document.get
		expectedResult.assertEquals(actualResult)
	}

	private static class TestModificationContext implements IModificationContext {
		IXtextDocument doc

		override getXtextDocument() {
			doc
		}

		override getXtextDocument(URI uri) {
			doc
		}

		def setDocument(IXtextDocument doc) {
			this.doc = doc
		}

	}

	@Data
	private static class Quickfix {
		String label
		String description
		String result
	}
}
