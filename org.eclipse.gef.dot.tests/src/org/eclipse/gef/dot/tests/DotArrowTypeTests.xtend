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
import java.io.FileReader
import java.util.Map
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.Token
import org.eclipse.emf.ecore.EClass
import org.eclipse.gef.dot.internal.language.DotArrowTypeInjectorProvider
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension com.google.common.io.CharStreams.*
import static extension org.junit.Assert.*
import org.eclipse.gef.dot.internal.language.parser.antlr.internal.InternalDotArrowTypeLexer

@RunWith(XtextRunner)
@InjectWith(DotArrowTypeInjectorProvider)
class DotArrowTypeTests {
	
	@Inject extension ParseHelper<ArrowType>
	@Inject extension ValidationTestHelper

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
	
	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		expected.toString.trim.assertEquals(modelAsText.lex.toString.trim)
	}
	
	private def String lex(CharSequence text) {
		val names = tokenNames()
		val lexer = new InternalDotArrowTypeLexer(new ANTLRStringStream(text.toString))
		val result = newArrayList
		while (true) {
			val token = lexer.nextToken
			if (token == Token.EOF_TOKEN)
				return result.join(System.lineSeparator)
			result += (names.get(token.type) ?: token.type) + " '" + token.text + "'"
		}
	}
	
	private def Map<Integer, String> tokenNames() {
		val file = "../org.eclipse.gef.dot/src-gen/org/eclipse/gef/dot/internal/language/parser/antlr/internal/InternalDotArrowType.tokens"
		val result = <Integer, String>newLinkedHashMap
		for (line : new FileReader(file).readLines) {
			val s = line.split("=")
			val name = s.get(0)
			result.put(Integer.parseInt(s.get(1)), if(name.startsWith("KEYWORD")) "KEYWORD" else name)
		}
		result
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
