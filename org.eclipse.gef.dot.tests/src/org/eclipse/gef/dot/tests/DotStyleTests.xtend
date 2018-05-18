/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotStyleInjectorProvider
import org.eclipse.gef.dot.internal.language.style.Style
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider
import org.eclipse.xtext.parser.antlr.Lexer
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.lex
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotStyleInjectorProvider)
class DotStyleTests {
	
	@Inject extension ParseHelper<Style>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter
	@Inject extension IAntlrTokenFileProvider

	@Inject Lexer lexer

	@Test def test_valid_style() {
		"bold".parse.assertNoErrors
	}

	@Test def testLexingNodeStyle01(){
		"bold".assertLexing('''
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle02(){
		"dashed".assertLexing('''
			RULE_NAME 'dashed'
		''')
	}
	
	@Test def testLexingNodeStyle03(){
		"diagonals".assertLexing('''
			RULE_NAME 'diagonals'
		''')
	}
	
	@Test def testLexingNodeStyle04(){
		"dotted".assertLexing('''
			RULE_NAME 'dotted'
		''')
	}
	
	@Test def testLexingNodeStyle05(){
		"filled".assertLexing('''
			RULE_NAME 'filled'
		''')
	}
	
	@Test def testLexingNodeStyle06(){
		"invis".assertLexing('''
			RULE_NAME 'invis'
		''')
	}
	
	@Test def testLexingNodeStyle07(){
		"radial".assertLexing('''
			RULE_NAME 'radial'
		''')
	}
	
	@Test def testLexingNodeStyle08(){
		"rounded".assertLexing('''
			RULE_NAME 'rounded'
		''')
	}
	
	@Test def testLexingNodeStyle09(){
		"solid".assertLexing('''
			RULE_NAME 'solid'
		''')
	}
	
	@Test def testLexingNodeStyle10(){
		"striped".assertLexing('''
			RULE_NAME 'striped'
		''')
	}
	
	@Test def testLexingNodeStyle11(){
		"wedged".assertLexing('''
			RULE_NAME 'wedged'
		''')
	}
	
	@Test def testLexingNodeStyle12(){
		"bold, dashed".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}
	
	@Test def testLexingNodeStyle13(){
		"dashed,bold".assertLexing('''
			RULE_NAME 'dashed'
			T__6 ','
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle14(){
		"bold, dotted".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'dotted'
		''')
	}
	
	@Test def testLexingNodeStyle15(){
		"dotted, bold".assertLexing('''
			RULE_NAME 'dotted'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle16(){
		"filled, dashed".assertLexing('''
			RULE_NAME 'filled'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}
	
	@Test def testLexingNodeStyle17(){
		"dashed, filled".assertLexing('''
			RULE_NAME 'dashed'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}
	
	@Test def testLexingNodeStyle18(){
		"bold, filled".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}
	
	@Test def testLexingNodeStyle19(){
		"filled, bold".assertLexing('''
			RULE_NAME 'filled'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle20(){
		"bold, diagonals".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}
	
	@Test def testLexingNodeStyle21(){
		"diagonals, bold".assertLexing('''
			RULE_NAME 'diagonals'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle22(){
		"diagonals, filled".assertLexing('''
			RULE_NAME 'diagonals'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'filled'
		''')
	}
	
	@Test def testLexingNodeStyle23(){
		"filled, diagonals".assertLexing('''
			RULE_NAME 'filled'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}
	
	@Test def testLexingNodeStyle24(){
		"diagonals, filled, bold".assertLexing('''
			RULE_NAME 'diagonals'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'filled'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingNodeStyle25(){
		"filled, bold, diagonals".assertLexing('''
			RULE_NAME 'filled'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'diagonals'
		''')
	}
	
	@Test def testLexingNodeStyle26(){
		"setlinewidth(4)".assertLexing('''
			RULE_NAME 'setlinewidth'
			T__7 '('
			RULE_NAME '4'
			T__8 ')'
		''')
	}
	
	@Test def testLexingEdgeStyle01(){
		"bold".assertLexing('''
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingEdgeStyle02(){
		"dashed".assertLexing('''
			RULE_NAME 'dashed'
		''')
	}
	
	@Test def testLexingEdgeStyle03(){
		"dotted".assertLexing('''
			RULE_NAME 'dotted'
		''')
	}
	
	@Test def testLexingEdgeStyle04(){
		"invis".assertLexing('''
			RULE_NAME 'invis'
		''')
	}
	
	@Test def testLexingEdgeStyle05(){
		"solid".assertLexing('''
			RULE_NAME 'solid'
		''')
	}
	
	@Test def testLexingEdgeStyle06(){
		"tapered".assertLexing('''
			RULE_NAME 'tapered'
		''')
	}
	
	@Test def testLexingEdgeStyle07(){
		"bold, dashed".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'dashed'
		''')
	}
	
	@Test def testLexingEdgeStyle08(){
		"dashed, bold".assertLexing('''
			RULE_NAME 'dashed'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingEdgeStyle09(){
		"bold, dotted".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'dotted'
		''')
	}
	
	@Test def testLexingEdgeStyle10(){
		"dotted, bold".assertLexing('''
			RULE_NAME 'dotted'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testLexingEdgeStyle11(){
		"bold, tapered".assertLexing('''
			RULE_NAME 'bold'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'tapered'
		''')
	}
	
	@Test def testLexingEdgeStyle12(){
		"tapered, bold".assertLexing('''
			RULE_NAME 'tapered'
			T__6 ','
			RULE_WS ' '
			RULE_NAME 'bold'
		''')
	}
	
	@Test def testAstNodeStyle01(){
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
	
	@Test def testAstNodeStyle02(){
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
	
	@Test def testAstNodeStyle03(){
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
	
	@Test def testAstNodeStyle04(){
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
	
	@Test def testAstNodeStyle05(){
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
	
	@Test def testAstNodeStyle06(){
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
	
	@Test def testAstNodeStyle07(){
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
	
	@Test def testAstNodeStyle08(){
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
	
	@Test def testAstNodeStyle09(){
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
	
	@Test def testAstNodeStyle10(){
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
	
	@Test def testAstNodeStyle11(){
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
	
	@Test def testAstNodeStyle12(){
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
	
	@Test def testAstNodeStyle13(){
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
	
	@Test def testAstNodeStyle14(){
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
	
	@Test def testAstNodeStyle15(){
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
	
	@Test def testAstNodeStyle16(){
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
	
	@Test def testAstNodeStyle17(){
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
	
	@Test def testAstNodeStyle18(){
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
	
	@Test def testAstNodeStyle19(){
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
	
	@Test def testAstNodeStyle20(){
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
	
	@Test def testAstNodeStyle21(){
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
	
	@Test def testAstNodeStyle22(){
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
	
	@Test def testAstNodeStyle23(){
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
	
	@Test def testAstNodeStyle24(){
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
	
	@Test def testAstNodeStyle25(){
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
	
	@Test def testAstNodeStyle26(){
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
	
	@Test def testAstEdgeStyle01(){
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
	
	@Test def testAstEdgeStyle02(){
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
	
	@Test def testAstEdgeStyle03(){
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
	
	@Test def testAstEdgeStyle04(){
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
	
	@Test def testAstEdgeStyle05(){
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
	
	@Test def testAstEdgeStyle06(){
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
	
	@Test def testAstEdgeStyle07(){
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
	
	@Test def testAstEdgeStyle08(){
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
	
	@Test def testAstEdgeStyle09(){
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
	
	@Test def testAstEdgeStyle10(){
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
	
	@Test def testAstEdgeStyle11(){
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
	
	@Test def testAstEdgeStyle12(){
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

	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val actual = lexer.lex(antlrTokenFile, modelAsText)
		expected.toString.trim.assertEquals(actual.toString.trim)
	}
	
	private def assertAst(CharSequence modelAsText,
			CharSequence expected) {
		val ast = modelAsText.parse
		ast.assertNoErrors
		val astString = ast.apply
		expected.toString.assertEquals(astString.toString)
	}

}
