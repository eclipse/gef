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
import org.eclipse.emf.ecore.EClass
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage
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
@InjectWith(DotArrowTypeInjectorProvider)
class DotArrowTypeTest extends AbstractLexerTest {

	@Inject extension ParseHelper<ArrowType>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	override lexer() {
		lexer
	}

	@Test def lexing_box() {
		"box".assertLexing('''
			T__12 'box'
		''')

		"lbox".assertLexing('''
			T__5 'l'
			T__12 'box'
		''')

		"rbox".assertLexing('''
			T__6 'r'
			T__12 'box'
		''')

		"obox".assertLexing('''
			T__4 'o'
			T__12 'box'
		''')

		"olbox".assertLexing('''
			T__4 'o'
			T__5 'l'
			T__12 'box'
		''')

		"orbox".assertLexing('''
			T__4 'o'
			T__6 'r'
			T__12 'box'
		''')
	}

	@Test def lexing_crow() {
		"crow".assertLexing('''
			T__13 'crow'
		''')

		"lcrow".assertLexing('''
			T__5 'l'
			T__13 'crow'
		''')

		"rcrow".assertLexing('''
			T__6 'r'
			T__13 'crow'
		''')
	}

	@Test def lexing_curve() {
		"curve".assertLexing('''
			T__14 'curve'
		''')

		"lcurve".assertLexing('''
			T__5 'l'
			T__14 'curve'
		''')

		"rcurve".assertLexing('''
			T__6 'r'
			T__14 'curve'
		''')
	}

	@Test def lexing_diamond() {
		"diamond".assertLexing('''
			T__16 'diamond'
		''')

		"ldiamond".assertLexing('''
			T__5 'l'
			T__16 'diamond'
		''')

		"rdiamond".assertLexing('''
			T__6 'r'
			T__16 'diamond'
		''')

		"odiamond".assertLexing('''
			T__4 'o'
			T__16 'diamond'
		''')

		"oldiamond".assertLexing('''
			T__4 'o'
			T__5 'l'
			T__16 'diamond'
		''')

		"ordiamond".assertLexing('''
			T__4 'o'
			T__6 'r'
			T__16 'diamond'
		''')
	}

	@Test def lexing_dot() {
		"dot".assertLexing('''
			T__17 'dot'
		''')

		"odot".assertLexing('''
			T__4 'o'
			T__17 'dot'
		''')
	}

	@Test def lexing_icurve() {
		"icurve".assertLexing('''
			T__15 'icurve'
		''')

		"licurve".assertLexing('''
			T__5 'l'
			T__15 'icurve'
		''')

		"ricurve".assertLexing('''
			T__6 'r'
			T__15 'icurve'
		''')
	}

	@Test def lexing_inv() {
		"inv".assertLexing('''
			T__18 'inv'
		''')

		"linv".assertLexing('''
			T__5 'l'
			T__18 'inv'
		''')

		"rinv".assertLexing('''
			T__6 'r'
			T__18 'inv'
		''')

		"oinv".assertLexing('''
			T__4 'o'
			T__18 'inv'
		''')

		"olinv".assertLexing('''
			T__4 'o'
			T__5 'l'
			T__18 'inv'
		''')

		"orinv".assertLexing('''
			T__4 'o'
			T__6 'r'
			T__18 'inv'
		''')
	}

	@Test def lexing_none() {
		"none".assertLexing('''
			T__19 'none'
		''')
	}

	@Test def lexing_normal() {
		"normal".assertLexing('''
			T__20 'normal'
		''')

		"lnormal".assertLexing('''
			T__5 'l'
			T__20 'normal'
		''')

		"rnormal".assertLexing('''
			T__6 'r'
			T__20 'normal'
		''')

		"onormal".assertLexing('''
			T__4 'o'
			T__20 'normal'
		''')

		"olnormal".assertLexing('''
			T__4 'o'
			T__5 'l'
			T__20 'normal'
		''')

		"ornormal".assertLexing('''
			T__4 'o'
			T__6 'r'
			T__20 'normal'
		''')
	}

	@Test def lexing_tee() {
		"tee".assertLexing('''
			T__21 'tee'
		''')

		"ltee".assertLexing('''
			T__5 'l'
			T__21 'tee'
		''')

		"rtee".assertLexing('''
			T__6 'r'
			T__21 'tee'
		''')
	}

	@Test def lexing_vee() {
		"vee".assertLexing('''
			T__22 'vee'
		''')

		"lvee".assertLexing('''
			T__5 'l'
			T__22 'vee'
		''')

		"rvee".assertLexing('''
			T__6 'r'
			T__22 'vee'
		''')
	}

	@Test def lexing_two_primitive_shapes() {
		"invdot".assertLexing('''
			T__18 'inv'
			T__17 'dot'
		''')

		"invodot".assertLexing('''
			T__18 'inv'
			T__4 'o'
			T__17 'dot'
		''')

		"boxbox".assertLexing('''
			T__12 'box'
			T__12 'box'
		''')

		"nonenormal".assertLexing('''
			T__19 'none'
			T__20 'normal'
		''')

		"lteeoldiamond".assertLexing('''
			T__5 'l'
			T__21 'tee'
			T__4 'o'
			T__5 'l'
			T__16 'diamond'
		''')

		"nonedot".assertLexing('''
			T__19 'none'
			T__17 'dot'
		''')
	}

	@Test def lexing_three_primitive_shapes() {
		"dotodotdot".assertLexing('''
			T__17 'dot'
			T__4 'o'
			T__17 'dot'
			T__17 'dot'
		''')

		"lveerveelvee".assertLexing('''
			T__5 'l'
			T__22 'vee'
			T__6 'r'
			T__22 'vee'
			T__5 'l'
			T__22 'vee'
		''')

		"nonenonedot".assertLexing('''
			T__19 'none'
			T__19 'none'
			T__17 'dot'
		''')
	}

	@Test def lexing_four_primitive_shapes() {
		"onormalnormalonormalnormal".assertLexing('''
			T__4 'o'
			T__20 'normal'
			T__20 'normal'
			T__4 'o'
			T__20 'normal'
			T__20 'normal'
		''')

		"nonenonenonedot".assertLexing('''
			T__19 'none'
			T__19 'none'
			T__19 'none'
			T__17 'dot'
		''')

		"noneboxnonedot".assertLexing('''
			T__19 'none'
			T__12 'box'
			T__19 'none'
			T__17 'dot'
		''')

		"boxnonenonedot".assertLexing('''
			T__12 'box'
			T__19 'none'
			T__19 'none'
			T__17 'dot'
		''')
	}

	@Test def lexing_deprecated_shapes() {
		"ediamond".assertLexing('''
			T__7 'ediamond'
		''')

		"open".assertLexing('''
			T__8 'open'
		''')

		"halfopen".assertLexing('''
			T__9 'halfopen'
		''')

		"empty".assertLexing('''
			T__10 'empty'
		''')

		"invempty".assertLexing('''
			T__11 'invempty'
		''')

		"ediamondinvempty".assertLexing('''
			T__7 'ediamond'
			T__11 'invempty'
		''')

		"openbox".assertLexing('''
			T__8 'open'
			T__12 'box'
		''')
	}

	@Test def ast_box() {
		"box".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
				]
			}
		''')

		"lbox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'box'
					}
				]
			}
		''')

		"rbox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'box'
					}
				]
			}
		''')

		"obox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'box'
					}
				]
			}
		''')

		"olbox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'l'
						shape = 'box'
					}
				]
			}
		''')

		"orbox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'r'
						shape = 'box'
					}
				]
			}
		''')
	}

	@Test def ast_crow() {
		"crow".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'crow'
					}
				]
			}
		''')

		"lcrow".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'crow'
					}
				]
			}
		''')

		"rcrow".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'crow'
					}
				]
			}
		''')
	}

	@Test def ast_curve() {
		"curve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'curve'
					}
				]
			}
		''')

		"lcurve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'curve'
					}
				]
			}
		''')

		"rcurve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'curve'
					}
				]
			}
		''')
	}

	@Test def ast_diamond() {
		"diamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'diamond'
					}
				]
			}
		''')

		"ldiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'diamond'
					}
				]
			}
		''')

		"rdiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'diamond'
					}
				]
			}
		''')

		"odiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'diamond'
					}
				]
			}
		''')

		"oldiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'l'
						shape = 'diamond'
					}
				]
			}
		''')

		"ordiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'r'
						shape = 'diamond'
					}
				]
			}
		''')
	}

	@Test def ast_dot() {
		"dot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"odot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'dot'
					}
				]
			}
		''')
	}

	@Test def ast_icurve() {
		"icurve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'icurve'
					}
				]
			}
		''')

		"licurve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'icurve'
					}
				]
			}
		''')

		"ricurve".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'icurve'
					}
				]
			}
		''')
	}

	@Test def ast_inv() {
		"inv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'inv'
					}
				]
			}
		''')

		"linv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'inv'
					}
				]
			}
		''')

		"rinv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'inv'
					}
				]
			}
		''')

		"oinv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'inv'
					}
				]
			}
		''')

		"olinv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'l'
						shape = 'inv'
					}
				]
			}
		''')

		"orinv".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'r'
						shape = 'inv'
					}
				]
			}
		''')
	}

	@Test def ast_none() {
		"none".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
				]
			}
		''')
	}

	@Test def ast_normal() {
		"normal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'normal'
					}
				]
			}
		''')

		"lnormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'normal'
					}
				]
			}
		''')

		"rnormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'normal'
					}
				]
			}
		''')

		"onormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'normal'
					}
				]
			}
		''')

		"olnormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'l'
						shape = 'normal'
					}
				]
			}
		''')

		"ornormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = 'r'
						shape = 'normal'
					}
				]
			}
		''')
	}

	@Test def ast_tee() {
		"tee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'tee'
					}
				]
			}
		''')

		"ltee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'tee'
					}
				]
			}
		''')

		"rtee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'tee'
					}
				]
			}
		''')
	}

	@Test def ast_vee() {
		"vee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'vee'
					}
				]
			}
		''')

		"lvee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'vee'
					}
				]
			}
		''')

		"rvee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'vee'
					}
				]
			}
		''')
	}

	@Test def ast_two_primitive_shapes() {
		"invdot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'inv'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"invodot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'inv'
					}
					ArrowShape {
						open = 'true'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"boxbox".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
				]
			}
		''')

		"nonenormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'normal'
					}
				]
			}
		''')

		"lteeoldiamond".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'tee'
					}
					ArrowShape {
						open = 'true'
						side = 'l'
						shape = 'diamond'
					}
				]
			}
		''')

		"nonedot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')
	}

	@Test def ast_three_primitive_shapes() {
		"dotodotdot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
					ArrowShape {
						open = 'true'
						side = null
						shape = 'dot'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"lveerveelvee".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'vee'
					}
					ArrowShape {
						open = 'false'
						side = 'r'
						shape = 'vee'
					}
					ArrowShape {
						open = 'false'
						side = 'l'
						shape = 'vee'
					}
				]
			}
		''')

		"nonenonedot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')
	}

	@Test def ast_four_primitive_shapes() {
		"onormalnormalonormalnormal".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'true'
						side = null
						shape = 'normal'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'normal'
					}
					ArrowShape {
						open = 'true'
						side = null
						shape = 'normal'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'normal'
					}
				]
			}
		''')

		"nonenonenonedot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"noneboxnonedot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')

		"boxnonenonedot".assertAst('''
			ArrowType {
				arrowShapes = [
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'none'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'dot'
					}
				]
			}
		''')
	}

	@Test def ast_deprecated_shapes() {
		"ediamond".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'ediamond'
					}
				]
			}
		''')

		"open".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'open'
					}
				]
			}
		''')

		"halfopen".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'halfopen'
					}
				]
			}
		''')

		"empty".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'empty'
					}
				]
			}
		''')

		"invempty".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'invempty'
					}
				]
			}
		''')

		"ediamondinvempty".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'ediamond'
					}
					DeprecatedArrowShape {
						shape = 'invempty'
					}
				]
			}
		''')

		"openbox".assertAst('''
			ArrowType {
				arrowShapes = [
					DeprecatedArrowShape {
						shape = 'open'
					}
					ArrowShape {
						open = 'false'
						side = null
						shape = 'box'
					}
				]
			}
		''')
	}

	@Test def invalid_open_modifier() {
		"ocrow".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)

		"lteeveeocrowdot".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)
	}

	@Test def invalid_side_modifier() {
		"rdot".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)

		"lteeveerdotbox".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)
	}

	@Test def deprecated_arrowshape() {
		"openbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)

		"lteeveeopenbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)
	}

	@Test def none_is_the_last_arrowshape() {
		"boxnone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)

		"boxdotveenone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)
	}

	@Test def void empty_arrowtype() {
		"".parseArrowType
	}

	private def assertArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage, ArrowtypePackage.eINSTANCE.arrowShape)
	}

	private def assertDeprecatedArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage,	ArrowtypePackage.eINSTANCE.deprecatedArrowShape)
	}

	private def assertArrowTypeWarning(String text, String errorProneText, String warningMessage, EClass objectType) {
		val ast = text.parseArrowType
		val offset = text.indexOf(errorProneText)
		val length = errorProneText.length
		ast.assertWarning(objectType, null, offset, length, warningMessage)

		// verify that this is the only reported issue
		1.assertEquals(ast.validate.size)
	}

	private def assertAst(CharSequence modelAsText,
			CharSequence expected) {
		val ast = modelAsText.parse
		ast.assertNoErrors
		val astString = ast.apply
		expected.toString.assertEquals(astString.toString)
	}

	private def ArrowType parseArrowType(String modelAsText) {
		var ArrowType ast = null
		try {
			ast = modelAsText.parse
		} catch (Exception e) {
			e.printStackTrace
			fail
		}

		ast.assertNotNull
		ast.assertNoErrors
		ast
	}
}
