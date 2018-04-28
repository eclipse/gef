/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator
import org.eclipse.jface.text.ITextSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Event
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditor
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotEditorDoubleClickingTests extends AbstractEditorTest {
	
	/**
	 * Special symbols indicating the current cursor position
	 */
	val c = '''<|>'''
	
	override protected getEditorId() {
		DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
	}

	@Test def empty_graph() {
		'''
			grap«c»h {
			}
		'''.assertTextSelectedAfterDoubleClicking('''graph''')
	}
	
	@Test def empty_digraph() {
		'''
			d«c»igraph {
			}
		'''.assertTextSelectedAfterDoubleClicking('''digraph''')
	}
	
		@Test def edge_arrowhead_001() {
		'''
			digraph {
				1->2[arrowhead=b«c»ox]
			}
		'''.assertTextSelectedAfterDoubleClicking('''box''')
	}
	
	@Test def edge_arrowhead_002() {
		'''
			digraph {
				1->2[arrowhead="noneob«c»ox"]
			}
		'''.assertTextSelectedAfterDoubleClicking('''noneobox''')
	}
	
	@Test def edge_arrowsize_001() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=«c»1.5]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_arrowsize_002() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1«c».5]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_arrowsize_003() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1.«c»5]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_arrowsize_004() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1.5«c»]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_arrowsize_005() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="«c»1.5"]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_arrowsize_006() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1«c».5"]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1''')
	}
	
	@Test def edge_arrowsize_007() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1.«c»5"]
			}
		'''.assertTextSelectedAfterDoubleClicking('''.''')
	}
	
	@Test def edge_arrowsize_008() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1.5«c»"]
			}
		'''.assertTextSelectedAfterDoubleClicking('''1.5''')
	}
	
	@Test def edge_color_001() {
		'''
			digraph { 1->2[c«c»olor=aliceblue] }
		'''.assertTextSelectedAfterDoubleClicking('''color''')
	}
	
	@Test def edge_color_002() {
		'''
			digraph { 1->2[colo«c»r=aliceblue] }
		'''.assertTextSelectedAfterDoubleClicking('''color''')
	}
	
	@Test def edge_color_003() {
		'''
			digraph { 1->2[color«c»=aliceblue] }
		'''.assertTextSelectedAfterDoubleClicking('''color''')
	}
	
	@Test def edge_color_004() {
		'''
			digraph { 1->2[color=«c»aliceblue] }
		'''.assertTextSelectedAfterDoubleClicking('''aliceblue''')
	}
	
	@Test def edge_color_005() {
		'''
			digraph { 1->2[color=aliceblue«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''aliceblue''')
	}
	
	@Test def edge_color_006() {
		'''
			digraph { 1->2[color=«c»"#abcdef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''"#abcdef89"''')
	}
	
	@Test def edge_color_007() {
		'''
			digraph { 1->2[color="«c»#abcdef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''#abcdef89''')
	}
	
	@Test def edge_color_008() {
		'''
			digraph { 1->2[color="#«c»abcdef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_009() {
		'''
			digraph { 1->2[color="#a«c»bcdef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_010() {
		'''
			digraph { 1->2[color="#ab«c»cdef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_011() {
		'''
			digraph { 1->2[color="#abc«c»def89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_012() {
		'''
			digraph { 1->2[color="#abcd«c»ef89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_013() {
		'''
			digraph { 1->2[color="#abcde«c»f89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_014() {
		'''
			digraph { 1->2[color="#abcdef«c»89"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_015() {
		'''
			digraph { 1->2[color="#abcdef8«c»9"] }
		'''.assertTextSelectedAfterDoubleClicking('''abcdef89''')
	}
	
	@Test def edge_color_016() {
		'''
			digraph { 1->2[color="#abcdef89«c»"] }
		'''.assertTextSelectedAfterDoubleClicking('''#abcdef89''')
	}
	
	@Test def edge_color_017() {
		'''
			digraph { 1->2[color="#abcdef89"«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''"#abcdef89"''')
	}
	
	@Test def edge_color_018() {
		'''
			digraph { 1->2[color=«c»"0.200 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''"0.200 0.300 0.500"''')
	}
	
	@Test def edge_color_019() {
		'''
			digraph { 1->2[color="«c»0.200 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''0.200 0.300 0.500''')
	}
	
	@Test def edge_color_020() {
		'''
			digraph { 1->2[color="0«c».200 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''0''')
	}
	
	@Test def edge_color_021() {
		'''
			digraph { 1->2[color="0.«c»200 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''200''')
	}
	
	@Test def edge_color_022() {
		'''
			digraph { 1->2[color="0.2«c»00 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''200''')
	}

	@Test def edge_color_023() {
		'''
			digraph { 1->2[color="0.20«c»0 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''200''')
	}
	
	@Test def edge_color_024() {
		'''
			digraph { 1->2[color="0.200«c» 0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''200''')
	}
	
	@Test def edge_color_025() {
		'''
			digraph { 1->2[color="0.200 «c»0.300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking(''' ''')
	}
	
	@Test def edge_color_026() {
		'''
			digraph { 1->2[color="0.200 0«c».300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''0''')
	}
	
	@Test def edge_color_027() {
		'''
			digraph { 1->2[color="0.200 0.«c»300 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''300''')
	}
	
	@Test def edge_color_028() {
		'''
			digraph { 1->2[color="0.200 0.3«c»00 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''300''')
	}
	
	@Test def edge_color_029() {
		'''
			digraph { 1->2[color="0.200 0.30«c»0 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''300''')
	}
	
	@Test def edge_color_030() {
		'''
			digraph { 1->2[color="0.200 0.300«c» 0.500"] }
		'''.assertTextSelectedAfterDoubleClicking('''300''')
	}
	
	@Test def edge_color_031() {
		'''
			digraph { 1->2[color="0.200 0.300 «c»0.500"] }
		'''.assertTextSelectedAfterDoubleClicking(''' ''')
	}
	
	@Test def edge_color_032() {
		'''
			digraph { 1->2[color="0.200 0.300 0«c».500"] }
		'''.assertTextSelectedAfterDoubleClicking('''0''')
	}
	
	@Test def edge_color_033() {
		'''
			digraph { 1->2[color="0.200 0.300 0.«c»500"] }
		'''.assertTextSelectedAfterDoubleClicking('''500''')
	}

	@Test def edge_color_034() {
		'''
			digraph { 1->2[color="0.200 0.300 0.5«c»00"] }
		'''.assertTextSelectedAfterDoubleClicking('''500''')
	}
	
	@Test def edge_color_035() {
		'''
			digraph { 1->2[color="0.200 0.300 0.50«c»0"] }
		'''.assertTextSelectedAfterDoubleClicking('''500''')
	}

	@Test def edge_color_036() {
		'''
			digraph { 1->2[color="0.200 0.300 0.500«c»"] }
		'''.assertTextSelectedAfterDoubleClicking('''0.200 0.300 0.500''')
	}

	@Test def edge_color_037() {
		'''
			digraph { 1->2[color="0.200 0.300 0.500"«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''"0.200 0.300 0.500"''')
	}
	
	@Test def edge_colorscheme_001() {
		'''
			digraph { 1->2[«c»colorscheme=x11]}
		'''.assertTextSelectedAfterDoubleClicking('''colorscheme=x11''')
	}
	
	@Test def edge_colorscheme_002() {
		'''
			digraph { 1->2[colorscheme=«c»x11]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_003() {
		'''
			digraph { 1->2[colorscheme=x«c»11]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_004() {
		'''
			digraph { 1->2[colorscheme=x1«c»1]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_005() {
		'''
			digraph { 1->2[colorscheme=x11«c»]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_006() {
		'''
			digraph { 1->2[«c»colorscheme="x11"]}
		'''.assertTextSelectedAfterDoubleClicking('''colorscheme="x11"''')
	}
	
	@Test def edge_colorscheme_007() {
		'''
			digraph { 1->2[colorscheme=«c»"x11"]}
		'''.assertTextSelectedAfterDoubleClicking('''"x11"''')
	}
	
	@Test def edge_colorscheme_008() {
		'''
			digraph { 1->2[colorscheme="x«c»11"]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_009() {
		'''
			digraph { 1->2[colorscheme="x1«c»1"]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_010() {
		'''
			digraph { 1->2[colorscheme="x11«c»"]}
		'''.assertTextSelectedAfterDoubleClicking('''x11''')
	}
	
	@Test def edge_colorscheme_011() {
		'''
			digraph { 1->2[colorscheme="x11"«c»]}
		'''.assertTextSelectedAfterDoubleClicking('''"x11"''')
	}
	
	@Test def graph_bgcolor_001() {
		'''
			digraph {
				bgcolor=«c»"orange:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''"orange:black"''')
	}
	
	@Test def graph_bgcolor_002() {
		'''
			digraph {
				bgcolor="«c»orange:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange:black''')
	}

	@Test def graph_bgcolor_003() {
		'''
			digraph {
				bgcolor="o«c»range:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_004() {
		'''
			digraph {
				bgcolor="or«c»ange:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_005() {
		'''
			digraph {
				bgcolor="ora«c»nge:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_006() {
		'''
			digraph {
				bgcolor="oran«c»ge:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_007() {
		'''
			digraph {
				bgcolor="orang«c»e:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_008() {
		'''
			digraph {
				bgcolor="orange«c»:black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange''')
	}
	
	@Test def graph_bgcolor_009() {
		'''
			digraph {
				bgcolor="orange:«c»black"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''black''')
	}
	
	@Test def graph_bgcolor_010() {
		'''
			digraph {
				bgcolor="orange:b«c»lack"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''black''')
	}
	
	@Test def graph_bgcolor_011() {
		'''
			digraph {
				bgcolor="orange:bl«c»ack"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''black''')
	}
	
	@Test def graph_bgcolor_012() {
		'''
			digraph {
				bgcolor="orange:bla«c»ck"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''black''')
	}
	
	@Test def graph_bgcolor_013() {
		'''
			digraph {
				bgcolor="orange:blac«c»k"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''black''')
	}
	
	@Test def graph_bgcolor_014() {
		'''
			digraph {
				bgcolor="orange:black«c»"
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''orange:black''')
	}
	
	@Test def graph_bgcolor_015() {
		'''
			digraph {
				bgcolor="orange:black"«c»
				style="filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''"orange:black"''')
	}
	
	@Test def graph_nodesep_001() {
		'''
			graph { nodesep=«c»0.7 ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_nodesep_002() {
		'''
			graph { nodesep=0«c».7 ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_nodesep_003() {
		'''
			graph { nodesep=0.«c»7 ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_nodesep_004() {
		'''
			graph { nodesep=0.7«c» ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_nodesep_005() {
		'''
			graph { nodesep="«c»0.7" ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_nodesep_006() {
		'''
			graph { nodesep="0«c».7" ]}
		'''.assertTextSelectedAfterDoubleClicking('''0''')
	}
	
	@Test def graph_nodesep_007() {
		'''
			graph { nodesep="0.«c»7" ]}
		'''.assertTextSelectedAfterDoubleClicking('''.''')
	}
	
	@Test def graph_nodesep_008() {
		'''
			graph { nodesep="0.7«c»" ]}
		'''.assertTextSelectedAfterDoubleClicking('''0.7''')
	}
	
	@Test def graph_style_001() {
		'''
			graph {
				bgcolor="orange:black"
				style=«c»"filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''"filled, radial"''')
	}
	
	@Test def graph_style_002() {
		'''
			graph {
				bgcolor="orange:black"
				style="«c»filled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled, radial''')
	}
	
	@Test def graph_style_003() {
		'''
			graph {
				bgcolor="orange:black"
				style="f«c»illed, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}
	
	@Test def graph_style_004() {
		'''
			graph {
				bgcolor="orange:black"
				style="fi«c»lled, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_005() {
		'''
			graph {
				bgcolor="orange:black"
				style="fil«c»led, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}
	
	@Test def graph_style_006() {
		'''
			graph {
				bgcolor="orange:black"
				style="fill«c»ed, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}
	
	@Test def graph_style_007() {
		'''
			graph {
				bgcolor="orange:black"
				style="fille«c»d, radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}
	
	@Test def graph_style_008() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled«c», radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled''')
	}
	
	@Test def graph_style_009() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled,«c» radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking(''',''')
	}
	
	@Test def graph_style_010() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, «c»radial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_011() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, r«c»adial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_012() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, ra«c»dial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_013() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, rad«c»ial"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_014() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radi«c»al"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_015() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radia«c»l"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''radial''')
	}
	
	@Test def graph_style_016() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radial«c»"
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''filled, radial''')
	}
	
	@Test def graph_style_017() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radial"«c»
				1
			}
		'''.assertTextSelectedAfterDoubleClicking('''"filled, radial"''')
	}
	
	@Test def node_color_001() {
		'''
			graph {	1[color=«c»"/accent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''"/accent3/2"''')
	}
	
	@Test def node_color_002() {
		'''
			graph {	1[color="«c»/accent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''/accent3/2''')
	}
	
	@Test def node_color_003() {
		'''
			graph {	1[color="/«c»accent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_004() {
		'''
			graph {	1[color="/a«c»ccent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_005() {
		'''
			graph {	1[color="/ac«c»cent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_006() {
		'''
			graph {	1[color="/acc«c»ent3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_007() {
		'''
			graph {	1[color="/acce«c»nt3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_008() {
		'''
			graph {	1[color="/accen«c»t3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_009() {
		'''
			graph {	1[color="/accent«c»3/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_010() {
		'''
			graph {	1[color="/accent3«c»/2"] }
		'''.assertTextSelectedAfterDoubleClicking('''accent3''')
	}
	
	@Test def node_color_011() {
		'''
			graph {	1[color="/accent3/«c»2"] }
		'''.assertTextSelectedAfterDoubleClicking('''/''')
	}
	
	@Test def node_color_012() {
		'''
			graph {	1[color="/accent3/2«c»"] }
		'''.assertTextSelectedAfterDoubleClicking('''/accent3/2''')
	}
	
	@Test def node_color_013() {
		'''
			graph {	1[color="/accent3/2"«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''"/accent3/2"''')
	}
	
	@Test def node_height_001() {
		'''
			graph {	1[height=«c»2.3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_002() {
		'''
			graph {	1[height=2«c».3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_003() {
		'''
			graph {	1[height=2.«c»3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_004() {
		'''
			graph {	1[height=2.3«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_005() {
		'''
			graph {	1[height=«c»"2.3"] }
		'''.assertTextSelectedAfterDoubleClicking('''"2.3"''')
	}
	
	@Test def node_height_006() {
		'''
			graph {	1[height="«c»2.3"] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_007() {
		'''
			graph {	1[height="2«c».3"] }
		'''.assertTextSelectedAfterDoubleClicking('''2''')
	}

	@Test def node_height_008() {
		'''
			graph {	1[height="2.«c»3"] }
		'''.assertTextSelectedAfterDoubleClicking('''.''')
	}
	
	@Test def node_height_009() {
		'''
			graph {	1[height="2.3«c»"] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_height_010() {
		'''
			graph {	1[height="2.3"«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''"2.3"''')
	}
	
	@Ignore("activate as soon as solution for bug #532244 has been implemented")
	@Test def node_html_label_001() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Cate«c»gory</td>
						</tr>
					</table>
				>]
			}
		'''.assertTextSelectedAfterDoubleClicking('''Category''')
	}
	
	@Test def node_record_label_001() {
		'''
			graph{
				1[shape=record label=" text1 | text«c»2 "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''text2''')
	}
	
	@Test def node_style_001() {
		'''
			graph{
				1[style=" bo«c»ld, dotted "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''bold''')
	}
	
	@Test def node_style_002() {
		'''
			graph{
				1[style=" bold, dot«c»ted "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''dotted''')
	}
	
	@Test def node_width_001() {
		'''
			graph {	1[width=«c»2.3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_002() {
		'''
			graph {	1[width=2«c».3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_003() {
		'''
			graph {	1[width=2.«c»3] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_004() {
		'''
			graph {	1[width=2.3«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_005() {
		'''
			graph {	1[width=«c»"2.3"] }
		'''.assertTextSelectedAfterDoubleClicking('''"2.3"''')
	}
	
	@Test def node_width_006() {
		'''
			graph {	1[width="«c»2.3"] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_007() {
		'''
			graph {	1[width="2«c».3"] }
		'''.assertTextSelectedAfterDoubleClicking('''2''')
	}

	@Test def node_width_008() {
		'''
			graph {	1[width="2.«c»3"] }
		'''.assertTextSelectedAfterDoubleClicking('''.''')
	}
	
	@Test def node_width_009() {
		'''
			graph {	1[width="2.3«c»"] }
		'''.assertTextSelectedAfterDoubleClicking('''2.3''')
	}
	
	@Test def node_width_010() {
		'''
			graph {	1[width="2.3"«c»] }
		'''.assertTextSelectedAfterDoubleClicking('''"2.3"''')
	}
	
	/**
	  * @param it The text representing the input dot content.
	  * 	The text must contain the {@link #c} symbols indicating the current cursor position.
	  * 
	  * @param expected The text that is expected to be selected after double clicking.
	  */
	def private assertTextSelectedAfterDoubleClicking(CharSequence it, String expected) {
		
		content.openDotEditor.
		
		doubleClick(cursorPosition).
		
		assertSelectedText(expected)
	}

	private def getContent(CharSequence text) {
		text.toString.replace(c, "")
	}
	
	private def openDotEditor(String content) {
		var XtextEditor editor = null
		try {
			editor = content.createTestFile.openEditor
		} catch (Exception e) {
			e.printStackTrace
			fail(e.message)
		}
		editor
	}
	
	private def getCursorPosition(CharSequence text) {
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition==-1){
			fail('''
				The input text
				«text»
				must contain the «c» symbols indicating the current cursor position!
			''')
		}
		cursorPosition
	}
	
	private def doubleClick(XtextEditor dotEditor, int cursorPosition) {
		val viewer = dotEditor.internalSourceViewer
		
		// set the cursor position
		viewer.setSelectedRange(cursorPosition, 0)
		
		// simulate mouse down event with the left mouse button
		viewer.textWidget.notifyListeners(SWT.MouseDown,
			new Event => [
				button = 1
			]
		)
		
		dotEditor
	}
	
	private def assertSelectedText(XtextEditor dotEditor, CharSequence expectedSelectedText) {
		val actualSelectedText = (dotEditor.selectionProvider.selection as ITextSelection).text
		expectedSelectedText.assertEquals(actualSelectedText)
	}
}