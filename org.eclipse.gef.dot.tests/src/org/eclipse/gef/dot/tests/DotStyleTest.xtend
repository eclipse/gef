/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.name.Named
import org.eclipse.gef.dot.internal.language.style.Style
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotStyleInjectorProvider)
class DotStyleTest extends AbstractLexerTest {

	@Inject extension ParseHelper<Style>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	override lexer() {
		lexer
	}

	@Test def valid_style() {
		"bold".parse.assertNoErrors
	}

	@Test def lexing_node_style_01() {
		"bold".assertLexing('''
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_02() {
		"dashed".assertLexing('''
			RULE_NAME 'dashed'
		''')
	}

	@Test def lexing_node_style_03() {
		"diagonals".assertLexing('''
			RULE_NAME 'diagonals'
		''')
	}

	@Test def lexing_node_style_04() {
		"dotted".assertLexing('''
			RULE_NAME 'dotted'
		''')
	}

	@Test def lexing_node_style_05() {
		"filled".assertLexing('''
			RULE_NAME 'filled'
		''')
	}

	@Test def lexing_node_style_06() {
		"invis".assertLexing('''
			RULE_NAME 'invis'
		''')
	}

	@Test def lexing_node_style_07() {
		"radial".assertLexing('''
			RULE_NAME 'radial'
		''')
	}

	@Test def lexing_node_style_08() {
		"rounded".assertLexing('''
			RULE_NAME 'rounded'
		''')
	}

	@Test def lexing_node_style_09() {
		"solid".assertLexing('''
			RULE_NAME 'solid'
		''')
	}

	@Test def lexing_node_style_10() {
		"striped".assertLexing('''
			RULE_NAME 'striped'
		''')
	}

	@Test def lexing_node_style_11() {
		"wedged".assertLexing('''
			RULE_NAME 'wedged'
		''')
	}

	@Test def lexing_node_style_12() {
		"bold, dashed".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}

	@Test def lexing_node_style_13() {
		"dashed,bold".assertLexing('''
			RULE_NAME 'dashed'
			',' ','
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_14() {
		"bold, dotted".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'dotted'
		''')
	}

	@Test def lexing_node_style_15() {
		"dotted, bold".assertLexing('''
			RULE_NAME 'dotted'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_16() {
		"filled, dashed".assertLexing('''
			RULE_NAME 'filled'
			',' ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}

	@Test def lexing_node_style_17() {
		"dashed, filled".assertLexing('''
			RULE_NAME 'dashed'
			',' ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}

	@Test def lexing_node_style_18() {
		"bold, filled".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}

	@Test def lexing_node_style_19() {
		"filled, bold".assertLexing('''
			RULE_NAME 'filled'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_20() {
		"bold, diagonals".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}

	@Test def lexing_node_style_21() {
		"diagonals, bold".assertLexing('''
			RULE_NAME 'diagonals'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_22() {
		"diagonals, filled".assertLexing('''
			RULE_NAME 'diagonals'
			',' ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}

	@Test def lexing_node_style_23() {
		"filled, diagonals".assertLexing('''
			RULE_NAME 'filled'
			',' ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}

	@Test def lexing_node_style_24() {
		"diagonals, filled, bold".assertLexing('''
			RULE_NAME 'diagonals'
			',' ','
			RULE_WS ' '
			RULE_NAME 'filled'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_node_style_25() {
		"filled, bold, diagonals".assertLexing('''
			RULE_NAME 'filled'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}

	@Test def lexing_node_style_26() {
		"setlinewidth(4)".assertLexing('''
			RULE_NAME 'setlinewidth'
			'(' '('
			RULE_NAME '4'
			')' ')'
		''')
	}

	@Test def lexing_edge_style_01() {
		"bold".assertLexing('''
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_edge_style_02() {
		"dashed".assertLexing('''
			RULE_NAME 'dashed'
		''')
	}

	@Test def lexing_edge_style_03() {
		"dotted".assertLexing('''
			RULE_NAME 'dotted'
		''')
	}

	@Test def lexing_edge_style_04() {
		"invis".assertLexing('''
			RULE_NAME 'invis'
		''')
	}

	@Test def lexing_edge_style_05() {
		"solid".assertLexing('''
			RULE_NAME 'solid'
		''')
	}

	@Test def lexing_edge_style_06() {
		"tapered".assertLexing('''
			RULE_NAME 'tapered'
		''')
	}

	@Test def lexing_edge_style_07() {
		"bold, dashed".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}

	@Test def lexing_edge_style_08() {
		"dashed, bold".assertLexing('''
			RULE_NAME 'dashed'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_edge_style_09() {
		"bold, dotted".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'dotted'
		''')
	}

	@Test def lexing_edge_style_10() {
		"dotted, bold".assertLexing('''
			RULE_NAME 'dotted'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def lexing_edge_style_11() {
		"bold, tapered".assertLexing('''
			RULE_NAME 'bold'
			',' ','
			RULE_WS ' '
			RULE_NAME 'tapered'
		''')
	}

	@Test def lexing_edge_style_12() {
		"tapered, bold".assertLexing('''
			RULE_NAME 'tapered'
			',' ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}

	@Test def ast_node_style_01() {
		"bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_02() {
		"dashed".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_03() {
		"diagonals".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_04() {
		"dotted".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_05() {
		"filled".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'filled'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_06() {
		"invis".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'invis'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_07() {
		"radial".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'radial'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_08() {
		"rounded".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'rounded'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_09() {
		"solid".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'solid'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_10() {
		"striped".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'striped'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_11() {
		"wedged".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'wedged'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_12() {
		"bold, dashed".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_13() {
		"dashed, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_14() {
		"bold, dotted".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_15() {
		"dotted, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_16() {
		"filled, dashed".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'filled'
						args = [
						]
					}
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_17() {
		"dashed, filled".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
					StyleItem {
						name = 'filled'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_18() {
		"bold, filled".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'filled'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_19() {
		"filled, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'filled'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_20() {
		"bold, diagonals".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_21() {
		"diagonals, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_22() {
		"diagonals, filled".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
					StyleItem {
						name = 'filled'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_23() {
		"filled, diagonals".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'filled'
						args = [
						]
					}
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_24() {
		"diagonals, filled, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
					StyleItem {
						name = 'filled'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_25() {
		"filled, bold, diagonals".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'filled'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'diagonals'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_node_style_26() {
		"setlinewidth(4)".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'setlinewidth'
						args = [
							'4'
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_01() {
		"bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_02() {
		"dashed".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_03() {
		"dotted".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_04() {
		"invis".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'invis'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_05() {
		"solid".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'solid'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_06() {
		"tapered".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'tapered'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_07() {
		"bold, dashed".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_08() {
		"dashed, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dashed'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_09() {
		"bold, dotted".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_10() {
		"dotted, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'dotted'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_11() {
		"bold, tapered".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'bold'
						args = [
						]
					}
					StyleItem {
						name = 'tapered'
						args = [
						]
					}
				]
			}
		''')
	}

	@Test def ast_edge_style_12() {
		"tapered, bold".assertAst('''
			Style {
				styleItems = [
					StyleItem {
						name = 'tapered'
						args = [
						]
					}
					StyleItem {
						name = 'bold'
						args = [
						]
					}
				]
			}
		''')
	}

	private def assertAst(CharSequence modelAsText,
			CharSequence expected) {
		val ast = modelAsText.parse
		ast.assertNoErrors
		val astString = ast.apply
		expected.toString.assertEquals(astString.toString)
	}
}
