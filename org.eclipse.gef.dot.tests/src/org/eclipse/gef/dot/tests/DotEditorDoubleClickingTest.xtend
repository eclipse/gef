/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG)     - initial API and implementation (bug #532244)
 *    Zoey Gerrit Prigge (itemis AG) - added additional test cases (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.ui.XtextProjectHelper

import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotEditorDoubleClickingTest extends AbstractEditorDoubleClickTextSelectionTest {

	/**
	 * The default special symbol is part of the dot record-based labels,
	 * therefore the usage of a more complex special symbols is desired.
	 */
	override String c() '''<|>'''

	override protected createFile(String content) {
		val file = super.createFile(content)

		val project = file.project
		if(!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		}

		file
	}
	
	@Test def empty_graph() {
		'''
			grap«c»h {
			}
		'''.assertSelectedTextAfterDoubleClicking('''graph''')
	}

	@Test def empty_digraph() {
		'''
			d«c»igraph {
			}
		'''.assertSelectedTextAfterDoubleClicking('''digraph''')
	}

	@Test def clicking_on_middle_letters_001() {
		'''
			digraph xyz {
			    c4->4  [t«c»ailport = s, headport = n];
			}
		'''.assertSelectedTextAfterDoubleClicking('''tailport''')
	}

	@Test def clicking_on_middle_letters_002() {
		'''graph { 1[«c»shape=none] }'''.assertSelectedTextAfterDoubleClicking("shape=none")
	}

	@Test def clicking_on_middle_letters_003() {
		'''graph { 1[s«c»hape=none] }'''.assertSelectedTextAfterDoubleClicking("shape")
	}

	@Test def clicking_on_middle_letters_004() {
		'''graph { 1[sh«c»ape=none] }'''.assertSelectedTextAfterDoubleClicking("shape")
	}

	@Test def clicking_on_middle_letters_005() {
		'''graph { 1[sha«c»pe=none] }'''.assertSelectedTextAfterDoubleClicking("shape")
	}

	@Test def clicking_on_middle_letters_006() {
		'''graph { 1[shap«c»e=none] }'''.assertSelectedTextAfterDoubleClicking("shape")
	}

	@Test def clicking_on_middle_letters_007() {
		'''graph { 1[shape«c»=none] }'''.assertSelectedTextAfterDoubleClicking("shape")
	}

	@Test def clicking_on_middle_letters_008() {
		'''graph { 1[shape=«c»none] }'''.assertSelectedTextAfterDoubleClicking("none")
	}

	@Test def clicking_on_middle_letters_009() {
		'''graph { 1[shape=n«c»one] }'''.assertSelectedTextAfterDoubleClicking("none")
	}

	@Test def clicking_on_middle_letters_010() {
		'''graph { 1[shape=no«c»ne] }'''.assertSelectedTextAfterDoubleClicking("none")
	}

	@Test def clicking_on_middle_letters_011() {
		'''graph { 1[shape=non«c»e] }'''.assertSelectedTextAfterDoubleClicking("none")
	}

	@Test def clicking_on_middle_letters_012() {
		'''graph { 1[shape=none«c»] }'''.assertSelectedTextAfterDoubleClicking("none")
	}

	@Test def edge_operator() {
		'''
			digraph {
				1 «c»-> 2
			}
		'''.assertSelectedTextAfterDoubleClicking('''->''')
	}

	@Test def edge_arrowhead_001() {
		'''
			digraph {
				1->2[arrowhead=b«c»ox]
			}
		'''.assertSelectedTextAfterDoubleClicking('''box''')
	}

	@Test def edge_arrowhead_002() {
		'''
			digraph {
				1->2[arrowhead="noneob«c»ox"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''noneobox''')
	}

	@Test def edge_arrowsize_001() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=«c»1.5]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_arrowsize_002() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1«c».5]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_arrowsize_003() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1.«c»5]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_arrowsize_004() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize=1.5«c»]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_arrowsize_005() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="«c»1.5"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_arrowsize_006() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1«c».5"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1''')
	}

	@Test def edge_arrowsize_007() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1.«c»5"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''.''')
	}

	@Test def edge_arrowsize_008() {
		'''
			digraph {
				1->2[arrowhead=box arrowsize="1.5«c»"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''1.5''')
	}

	@Test def edge_color_001() {
		'''
			digraph { 1->2[c«c»olor=aliceblue] }
		'''.assertSelectedTextAfterDoubleClicking('''color''')
	}

	@Test def edge_color_002() {
		'''
			digraph { 1->2[colo«c»r=aliceblue] }
		'''.assertSelectedTextAfterDoubleClicking('''color''')
	}

	@Test def edge_color_003() {
		'''
			digraph { 1->2[color«c»=aliceblue] }
		'''.assertSelectedTextAfterDoubleClicking('''color''')
	}

	@Test def edge_color_004() {
		'''
			digraph { 1->2[color=«c»aliceblue] }
		'''.assertSelectedTextAfterDoubleClicking('''aliceblue''')
	}

	@Test def edge_color_005() {
		'''
			digraph { 1->2[color=aliceblue«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''aliceblue''')
	}

	@Test def edge_color_006() {
		'''
			digraph { 1->2[color=«c»"#abcdef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''"#abcdef89"''')
	}

	@Test def edge_color_007() {
		'''
			digraph { 1->2[color="«c»#abcdef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''#abcdef89''')
	}

	@Test def edge_color_008() {
		'''
			digraph { 1->2[color="#«c»abcdef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_009() {
		'''
			digraph { 1->2[color="#a«c»bcdef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_010() {
		'''
			digraph { 1->2[color="#ab«c»cdef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_011() {
		'''
			digraph { 1->2[color="#abc«c»def89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_012() {
		'''
			digraph { 1->2[color="#abcd«c»ef89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_013() {
		'''
			digraph { 1->2[color="#abcde«c»f89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_014() {
		'''
			digraph { 1->2[color="#abcdef«c»89"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_015() {
		'''
			digraph { 1->2[color="#abcdef8«c»9"] }
		'''.assertSelectedTextAfterDoubleClicking('''abcdef89''')
	}

	@Test def edge_color_016() {
		'''
			digraph { 1->2[color="#abcdef89«c»"] }
		'''.assertSelectedTextAfterDoubleClicking('''#abcdef89''')
	}

	@Test def edge_color_017() {
		'''
			digraph { 1->2[color="#abcdef89"«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''"#abcdef89"''')
	}

	@Test def edge_color_018() {
		'''
			digraph { 1->2[color=«c»"0.200 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''"0.200 0.300 0.500"''')
	}

	@Test def edge_color_019() {
		'''
			digraph { 1->2[color="«c»0.200 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''0.200 0.300 0.500''')
	}

	@Test def edge_color_020() {
		'''
			digraph { 1->2[color="0«c».200 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''0''')
	}

	@Test def edge_color_021() {
		'''
			digraph { 1->2[color="0.«c»200 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''200''')
	}

	@Test def edge_color_022() {
		'''
			digraph { 1->2[color="0.2«c»00 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''200''')
	}

	@Test def edge_color_023() {
		'''
			digraph { 1->2[color="0.20«c»0 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''200''')
	}

	@Test def edge_color_024() {
		'''
			digraph { 1->2[color="0.200«c» 0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''200''')
	}

	@Test def edge_color_025() {
		'''
			digraph { 1->2[color="0.200 «c»0.300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking(''' ''')
	}

	@Test def edge_color_026() {
		'''
			digraph { 1->2[color="0.200 0«c».300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''0''')
	}

	@Test def edge_color_027() {
		'''
			digraph { 1->2[color="0.200 0.«c»300 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''300''')
	}

	@Test def edge_color_028() {
		'''
			digraph { 1->2[color="0.200 0.3«c»00 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''300''')
	}

	@Test def edge_color_029() {
		'''
			digraph { 1->2[color="0.200 0.30«c»0 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''300''')
	}

	@Test def edge_color_030() {
		'''
			digraph { 1->2[color="0.200 0.300«c» 0.500"] }
		'''.assertSelectedTextAfterDoubleClicking('''300''')
	}

	@Test def edge_color_031() {
		'''
			digraph { 1->2[color="0.200 0.300 «c»0.500"] }
		'''.assertSelectedTextAfterDoubleClicking(''' ''')
	}

	@Test def edge_color_032() {
		'''
			digraph { 1->2[color="0.200 0.300 0«c».500"] }
		'''.assertSelectedTextAfterDoubleClicking('''0''')
	}

	@Test def edge_color_033() {
		'''
			digraph { 1->2[color="0.200 0.300 0.«c»500"] }
		'''.assertSelectedTextAfterDoubleClicking('''500''')
	}

	@Test def edge_color_034() {
		'''
			digraph { 1->2[color="0.200 0.300 0.5«c»00"] }
		'''.assertSelectedTextAfterDoubleClicking('''500''')
	}

	@Test def edge_color_035() {
		'''
			digraph { 1->2[color="0.200 0.300 0.50«c»0"] }
		'''.assertSelectedTextAfterDoubleClicking('''500''')
	}

	@Test def edge_color_036() {
		'''
			digraph { 1->2[color="0.200 0.300 0.500«c»"] }
		'''.assertSelectedTextAfterDoubleClicking('''0.200 0.300 0.500''')
	}

	@Test def edge_color_037() {
		'''
			digraph { 1->2[color="0.200 0.300 0.500"«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''"0.200 0.300 0.500"''')
	}

	@Test def edge_colorscheme_001() {
		'''
			digraph { 1->2[«c»colorscheme=x11]}
		'''.assertSelectedTextAfterDoubleClicking('''colorscheme=x11''')
	}

	@Test def edge_colorscheme_002() {
		'''
			digraph { 1->2[colorscheme=«c»x11]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_003() {
		'''
			digraph { 1->2[colorscheme=x«c»11]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_004() {
		'''
			digraph { 1->2[colorscheme=x1«c»1]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_005() {
		'''
			digraph { 1->2[colorscheme=x11«c»]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_006() {
		'''
			digraph { 1->2[«c»colorscheme="x11"]}
		'''.assertSelectedTextAfterDoubleClicking('''colorscheme="x11"''')
	}

	@Test def edge_colorscheme_007() {
		'''
			digraph { 1->2[colorscheme=«c»"x11"]}
		'''.assertSelectedTextAfterDoubleClicking('''"x11"''')
	}

	@Test def edge_colorscheme_008() {
		'''
			digraph { 1->2[colorscheme="x«c»11"]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_009() {
		'''
			digraph { 1->2[colorscheme="x1«c»1"]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_010() {
		'''
			digraph { 1->2[colorscheme="x11«c»"]}
		'''.assertSelectedTextAfterDoubleClicking('''x11''')
	}

	@Test def edge_colorscheme_011() {
		'''
			digraph { 1->2[colorscheme="x11"«c»]}
		'''.assertSelectedTextAfterDoubleClicking('''"x11"''')
	}

	@Test def edge_html_label_001() {
		'''
			digraph {
				1->2[label=<<b>bold Te«c»xt</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Text''')
	}

	@Test def edge_html_label_002() {
		'''
			digraph {
				1->2[label=<<«c»b>bold Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''b''')
	}

	@Test def edge_html_label_003() {
		'''
			digraph {
				1->2[label=<«c»<b>bold Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''<''')
	}

	@Test def edge_html_label_004() {
		'''
			digraph {
				1->2[label=<<font face=«c»"Times">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''="''')
	}

	@Test def edge_html_label_005() {
		'''
			digraph {
				1->2[label=<<font face="Ti«c»mes">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Times''')
	}

	@Test def edge_html_label_006() {
		'''
			digraph {
				1->2[label=<<font face«c»="Times">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''face''')
	}

	@Test def edge_html_label_007() {
		'''
			digraph {
				1->2[label=<<font «c»face="Times">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''face''')
	}

	@Test def edge_html_label_008() {
		'''
			digraph {
				1->2[label=<<fo«c»nt face="Times">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''font''')
	}

	@Test def edge_html_label_009() {
		'''
			digraph {
				1->2[label=«c»<<font face="Times">serif Text</b>>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''<<font face="Times">serif Text</b>>''')
	}

	@Test def edge_html_label_010() {
		'''
			digraph{1->2[label=<«c»text>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_011() {
		'''
			digraph{1->2[label=<t«c»ext>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_012() {
		'''
			digraph{1->2[label=<te«c»xt>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_013() {
		'''
			digraph{1->2[label=<tex«c»t>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_014() {
		'''
			digraph{1->2[label=<text«c»>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_015() {
		'''
			digraph{1->2[label=< «c»text >]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_016() {
		'''
			digraph{1->2[label=< t«c»ext >]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_017() {
		'''
			digraph{1->2[label=< te«c»xt >]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_018() {
		'''
			digraph{1->2[label=< tex«c»t >]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_019() {
		'''
			digraph{1->2[label=< text«c» >]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_020() {
		'''
			digraph{1->2[label=<<B>«c»text</B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_021() {
		'''
			digraph{1->2[label=<<B>t«c»ext</B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_022() {
		'''
			digraph{1->2[label=<<B>te«c»xt</B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_023() {
		'''
			digraph{1->2[label=<<B>tex«c»t</B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_024() {
		'''
			digraph{1->2[label=<<B>text«c»</B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_025() {
		'''
			digraph{1->2[label=<<B> «c»text </B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_026() {
		'''
			digraph{1->2[label=<<B> t«c»ext </B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_027() {
		'''
			digraph{1->2[label=<<B> te«c»xt </B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_028() {
		'''
			digraph{1->2[label=<<B> tex«c»t </B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_html_label_029() {
		'''
			digraph{1->2[label=<<B> text«c» </B>>]}
		'''.assertSelectedTextAfterDoubleClicking('''text''')
	}

	@Test def edge_style_001() {
		'''
			graph{
				1->2[style="bo«c»ld, dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold''')
	}

	@Test def graph_bgcolor_001() {
		'''
			digraph {
				bgcolor=«c»"orange:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''"orange:black"''')
	}

	@Test def graph_bgcolor_002() {
		'''
			digraph {
				bgcolor="«c»orange:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange:black''')
	}

	@Test def graph_bgcolor_003() {
		'''
			digraph {
				bgcolor="o«c»range:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_004() {
		'''
			digraph {
				bgcolor="or«c»ange:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_005() {
		'''
			digraph {
				bgcolor="ora«c»nge:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_006() {
		'''
			digraph {
				bgcolor="oran«c»ge:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_007() {
		'''
			digraph {
				bgcolor="orang«c»e:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_008() {
		'''
			digraph {
				bgcolor="orange«c»:black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange''')
	}

	@Test def graph_bgcolor_009() {
		'''
			digraph {
				bgcolor="orange:«c»black"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''black''')
	}

	@Test def graph_bgcolor_010() {
		'''
			digraph {
				bgcolor="orange:b«c»lack"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''black''')
	}

	@Test def graph_bgcolor_011() {
		'''
			digraph {
				bgcolor="orange:bl«c»ack"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''black''')
	}

	@Test def graph_bgcolor_012() {
		'''
			digraph {
				bgcolor="orange:bla«c»ck"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''black''')
	}

	@Test def graph_bgcolor_013() {
		'''
			digraph {
				bgcolor="orange:blac«c»k"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''black''')
	}

	@Test def graph_bgcolor_014() {
		'''
			digraph {
				bgcolor="orange:black«c»"
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''orange:black''')
	}

	@Test def graph_bgcolor_015() {
		'''
			digraph {
				bgcolor="orange:black"«c»
				style="filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''"orange:black"''')
	}

	@Test def graph_nodesep_001() {
		'''
			graph { nodesep=«c»0.7 ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_nodesep_002() {
		'''
			graph { nodesep=0«c».7 ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_nodesep_003() {
		'''
			graph { nodesep=0.«c»7 ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_nodesep_004() {
		'''
			graph { nodesep=0.7«c» ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_nodesep_005() {
		'''
			graph { nodesep="«c»0.7" ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_nodesep_006() {
		'''
			graph { nodesep="0«c».7" ]}
		'''.assertSelectedTextAfterDoubleClicking('''0''')
	}

	@Test def graph_nodesep_007() {
		'''
			graph { nodesep="0.«c»7" ]}
		'''.assertSelectedTextAfterDoubleClicking('''.''')
	}

	@Test def graph_nodesep_008() {
		'''
			graph { nodesep="0.7«c»" ]}
		'''.assertSelectedTextAfterDoubleClicking('''0.7''')
	}

	@Test def graph_style_001() {
		'''
			graph {
				bgcolor="orange:black"
				style=«c»"filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''"filled, radial"''')
	}

	@Test def graph_style_002() {
		'''
			graph {
				bgcolor="orange:black"
				style="«c»filled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled, radial''')
	}

	@Test def graph_style_003() {
		'''
			graph {
				bgcolor="orange:black"
				style="f«c»illed, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_004() {
		'''
			graph {
				bgcolor="orange:black"
				style="fi«c»lled, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_005() {
		'''
			graph {
				bgcolor="orange:black"
				style="fil«c»led, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_006() {
		'''
			graph {
				bgcolor="orange:black"
				style="fill«c»ed, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_007() {
		'''
			graph {
				bgcolor="orange:black"
				style="fille«c»d, radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_008() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled«c», radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled''')
	}

	@Test def graph_style_009() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled,«c» radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking(''',''')
	}

	@Test def graph_style_010() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, «c»radial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_011() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, r«c»adial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_012() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, ra«c»dial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_013() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, rad«c»ial"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_014() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radi«c»al"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_015() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radia«c»l"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''radial''')
	}

	@Test def graph_style_016() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radial«c»"
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''filled, radial''')
	}

	@Test def graph_style_017() {
		'''
			graph {
				bgcolor="orange:black"
				style="filled, radial"«c»
				1
			}
		'''.assertSelectedTextAfterDoubleClicking('''"filled, radial"''')
	}

	@Test def node_color_001() {
		'''
			graph {	1[color=«c»"/accent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''"/accent3/2"''')
	}

	@Test def node_color_002() {
		'''
			graph {	1[color="«c»/accent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''/accent3/2''')
	}

	@Test def node_color_003() {
		'''
			graph {	1[color="/«c»accent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_004() {
		'''
			graph {	1[color="/a«c»ccent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_005() {
		'''
			graph {	1[color="/ac«c»cent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_006() {
		'''
			graph {	1[color="/acc«c»ent3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_007() {
		'''
			graph {	1[color="/acce«c»nt3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_008() {
		'''
			graph {	1[color="/accen«c»t3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_009() {
		'''
			graph {	1[color="/accent«c»3/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_010() {
		'''
			graph {	1[color="/accent3«c»/2"] }
		'''.assertSelectedTextAfterDoubleClicking('''accent3''')
	}

	@Test def node_color_011() {
		'''
			graph {	1[color="/accent3/«c»2"] }
		'''.assertSelectedTextAfterDoubleClicking('''/''')
	}

	@Test def node_color_012() {
		'''
			graph {	1[color="/accent3/2«c»"] }
		'''.assertSelectedTextAfterDoubleClicking('''/accent3/2''')
	}

	@Test def node_color_013() {
		'''
			graph {	1[color="/accent3/2"«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''"/accent3/2"''')
	}

	@Test def node_height_001() {
		'''
			graph {	1[height=«c»2.3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_002() {
		'''
			graph {	1[height=2«c».3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_003() {
		'''
			graph {	1[height=2.«c»3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_004() {
		'''
			graph {	1[height=2.3«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_005() {
		'''
			graph {	1[height=«c»"2.3"] }
		'''.assertSelectedTextAfterDoubleClicking('''"2.3"''')
	}

	@Test def node_height_006() {
		'''
			graph {	1[height="«c»2.3"] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_007() {
		'''
			graph {	1[height="2«c».3"] }
		'''.assertSelectedTextAfterDoubleClicking('''2''')
	}

	@Test def node_height_008() {
		'''
			graph {	1[height="2.«c»3"] }
		'''.assertSelectedTextAfterDoubleClicking('''.''')
	}

	@Test def node_height_009() {
		'''
			graph {	1[height="2.3«c»"] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_height_010() {
		'''
			graph {	1[height="2.3"«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''"2.3"''')
	}

	@Test def node_html_label_001() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Cate«c»gory One</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Category''')
	}

	@Test def node_html_label_002() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Category«c» One</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Category''')
	}

	@Test def node_html_label_003() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">C«c»ategory One</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Category''')
	}

	@Test def node_html_label_004() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Category «c»One</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''One''')
	}

	@Test def node_html_label_005() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td al«c»ign="center">Category</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''align''')
	}

	@Test def node_html_label_006() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="c«c»enter">Category</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''center''')
	}

	@Test def node_html_label_007() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<«c»td align="center">Category</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''td''')
	}

	@Test def node_html_label_008() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Category</td>
						</t«c»r>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''tr''')
	}

	@Test def node_html_label_009() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">«c»Category Five</td>
						</tr>
					</table>
				>]
			}
		'''.assertSelectedTextAfterDoubleClicking('''Category''')
	}

	@Test def node_record_label_001() {
		'''
			graph{
				1[shape=record label=" text1 | text«c»2 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_record_label_002() {
		'''
			graph{
				1[shape=record label=" text1 | <po«c»rt123>text2 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''port123''')
	}

	@Test def node_record_label_003() {
		'''
			graph{
				1[shape=record label=" text1 | <«c»rt123 s >text2 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''rt123''')
	}

	@Test def node_record_label_004() {
		'''
			graph{
				1[shape=record label=" text1 | <po«c»rt123 s>text2 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''port123''')
	}

	@Test def node_record_label_005() {
		'''
			graph{
				1[shape=record label=" text1 | {«c» text2 } "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''{''')
	}

	@Test def node_record_label_006() {
		'''
			graph{
				1[shape=record label=" text1 | {«c»text2 } "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_record_label_007() {
		'''
			graph{
				1[shape=record label=" text1 | {«c» text2 text3} "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''{''')
	}

	@Test def node_record_label_008() {
		'''
			graph{
				1[shape=record label=" text1 | { te«c»xt2 text3} "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_record_label_009() {
		'''
			graph{
				1[shape=record label=" text1 | text«c»2 text3 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_record_label_010() {
		'''
			graph{
				1[shape=record label=" text1 | t«c»ext2 text3 "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_record_label_011() {
		'''
			graph{
				1[shape=record label=" text1 | «c»{ text2 text3 } "]
			}
		'''.assertSelectedTextAfterDoubleClicking(''' ''')
	}

	@Test def node_record_label_012() {
		'''
			graph{
				1[shape=record label=" text1 | { text2 text3 }«c» "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''}''')
	}

	@Test def node_record_label_013() {
		'''
			graph{
				1[shape=record label=" text1 | «c»<text2> text3 } "]
			}
		'''.assertSelectedTextAfterDoubleClicking(''' ''')
	}

	@Test def node_record_label_hidden_token() {
		'''
			graph{
				1[shape=record label=" text1 | {«c»text2         text3} "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''text2''')
	}

	@Test def node_style_001() {
		'''
			graph{
				1[style="bo«c»ld"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold''')
	}

	@Test def node_style_002() {
		'''
			graph{
				1[style=«c»"bold"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''"bold"''')
	}

	@Test def node_style_003() {
		'''
			graph{
				1[style=" bo«c»ld, dotted "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold''')
	}

	@Test def node_style_004() {
		'''
			graph{
				1[style=" bold, dot«c»ted "]
			}
		'''.assertSelectedTextAfterDoubleClicking('''dotted''')
	}

	@Test def node_style_005() {
		'''
			graph{
				1[style=«c»"bold, dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''"bold, dotted"''')
	}

	@Test def node_style_006() {
		'''
			graph{
				1[style="bo«c»ld, dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold''')
	}

	@Test def node_style_007() {
		'''
			graph{
				1[style="bold«c», dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold''')
	}

	@Test def node_style_008() {
		'''
			graph{
				1[style="bold,«c» dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking(''',''')
	}

	@Test def node_style_009() {
		'''
			graph{
				1[style="bold, «c»dotted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''dotted''')
	}

	@Test def node_style_010() {
		'''
			graph{
				1[style="bold, dot«c»ted"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''dotted''')
	}

	@Test def node_style_011() {
		'''
			graph{
				1[style="bold, dotted«c»"]
			}
		'''.assertSelectedTextAfterDoubleClicking('''bold, dotted''')
	}

	@Test def node_style_012() {
		'''
			graph{
				1[style="bold, dotted"«c»]
			}
		'''.assertSelectedTextAfterDoubleClicking('''"bold, dotted"''')
	}

	@Test def node_width_001() {
		'''
			graph {	1[width=«c»2.3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_002() {
		'''
			graph {	1[width=2«c».3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_003() {
		'''
			graph {	1[width=2.«c»3] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_004() {
		'''
			graph {	1[width=2.3«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_005() {
		'''
			graph {	1[width=«c»"2.3"] }
		'''.assertSelectedTextAfterDoubleClicking('''"2.3"''')
	}

	@Test def node_width_006() {
		'''
			graph {	1[width="«c»2.3"] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_007() {
		'''
			graph {	1[width="2«c».3"] }
		'''.assertSelectedTextAfterDoubleClicking('''2''')
	}

	@Test def node_width_008() {
		'''
			graph {	1[width="2.«c»3"] }
		'''.assertSelectedTextAfterDoubleClicking('''.''')
	}

	@Test def node_width_009() {
		'''
			graph {	1[width="2.3«c»"] }
		'''.assertSelectedTextAfterDoubleClicking('''2.3''')
	}

	@Test def node_width_010() {
		'''
			graph {	1[width="2.3"«c»] }
		'''.assertSelectedTextAfterDoubleClicking('''"2.3"''')
	}

	@Test def other_attributes_001() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [c«c»olor=red, tailport=w, headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''color''')
	}

	@Test def other_attributes_002() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=r«c»ed, tailport=w, headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''red''')
	}

	@Test def other_attributes_003() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=red«c», tailport=w, headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''red''')
	}

	@Test def other_attributes_004() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=«c»red, tailport=w, headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''red''')
	}

	@Test def other_attributes_005() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=red, «c»tailport=w, headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''tailport''')
	}

	@Test def other_attributes_clicking_before_quotes() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=red, tailport=«c»"w", headport=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''"w"''')
	}

	@Test def other_attributes_equals_sign_throws_no_exception() {
		'''
			digraph {
				graph[charset=latin1, size="19,46, 11,12"]
				1
				2
				1 -> 2 [color=red, tailport=w, headport«c»=w]
			}
		'''.assertSelectedTextAfterDoubleClicking('''headport''')
	}
}