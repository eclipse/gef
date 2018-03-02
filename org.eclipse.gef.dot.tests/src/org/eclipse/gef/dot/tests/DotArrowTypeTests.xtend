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
import org.eclipse.emf.ecore.EClass
import org.eclipse.gef.dot.internal.language.DotArrowTypeInjectorProvider
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage
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
@InjectWith(DotArrowTypeInjectorProvider)
class DotArrowTypeTests {
	
	@Inject extension ParseHelper<ArrowType>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter
	@Inject extension IAntlrTokenFileProvider
	
	@Inject Lexer lexer

	@Test def void testLexingBox(){
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
	
	@Test def void testLexingCrow(){
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
		
	@Test def void testLexingCurve(){
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
	
	@Test def void testLexingDiamond(){
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
	
	@Test def void testLexingDot(){
		"dot".assertLexing('''
			T__17 'dot'
		''')
		
		"odot".assertLexing('''
			T__4 'o'
			T__17 'dot'
		''')
	}	
		
	@Test def void testLexingICurve(){
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
	
	@Test def void testLexingInv(){
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
	
	@Test def void testLexingNone(){
		"none".assertLexing('''
			T__19 'none'
		''')
	}
	
	@Test def void testLexingNormal(){
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
	
	@Test def void testLexingTee(){
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
	
	@Test def void testLexingVee(){
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
	
	@Test def void testLexingTwoPrimitiveShapes(){
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
	
	@Test def void testLexingThreePrimitiveShapes(){
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
	
	@Test def void testLexingFourPrimitiveShapes(){
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
	
	@Test def void testLexingDeprecatedShapes(){
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
	
	@Test def void testAstBox(){
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
	
	@Test def void testAstCrow(){
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
		
	@Test def void testAstCurve(){
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
	
	@Test def void testAstDiamond(){
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
	
	@Test def void testAstDot(){
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
		
	@Test def void testAstICurve(){
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
	
	@Test def void testAstInv(){
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
	
	@Test def void testAstNone(){
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
	
	@Test def void testAstNormal(){
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
	
	@Test def void testAstTee(){
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
	
	@Test def void testAstVee(){
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
	
	@Test def void testAstTwoPrimitiveShapes(){
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
	
	@Test def void testAstThreePrimitiveShapes(){
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
	
	@Test def void testAstFourPrimitiveShapes(){
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
	
	@Test def void testAstDeprecatedShapes(){
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

	@Test def void testInvalidOpenModifier() {
		"ocrow".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)
		
		"lteeveeocrowdot".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)
	}

	@Test def void testInvalidSideModifier() {
		"rdot".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)
		
		"lteeveerdotbox".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)
	}

	@Test def void testDeprecatedArrowShape() {
		"openbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)
		
		"lteeveeopenbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)
	}

	@Test def void testNoneIsTheLastArrowShape() {
		"boxnone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)
		
		"boxdotveenone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)
	}

	@Test def void testEmptyArrowType() {
		"".parseArrowType
	}

	def private void assertArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage, ArrowtypePackage.eINSTANCE.arrowShape)
	}

	def private void assertDeprecatedArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage,	ArrowtypePackage.eINSTANCE.deprecatedArrowShape)
	}

	def private void assertArrowTypeWarning(String text, String errorProneText, String warningMessage, EClass objectType) {
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
	
	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val actual = lexer.lex(antlrTokenFile, modelAsText)
		expected.toString.trim.assertEquals(actual.toString.trim)
	}
	
	def private ArrowType parseArrowType(String modelAsText){
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
