/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
import javax.inject.Named
import org.eclipse.gef.dot.internal.language.DotColorListInjectorProvider
import org.eclipse.gef.dot.internal.language.colorlist.ColorList
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.lex
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotColorListInjectorProvider)
class DotColorListTests {
	
	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer
	@Inject extension ParseHelper<ColorList>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter
	
	@Test def void oneColorValueWithWeightLexerTest() {
		"#3030FF;1".assertLexing('''
			NumberSign '#'
			RULE_HEXADECIMAL_DIGIT '3'
			RULE_HEXADECIMAL_DIGIT '0'
			RULE_HEXADECIMAL_DIGIT '3'
			RULE_HEXADECIMAL_DIGIT '0'
			RULE_HEXADECIMAL_DIGIT 'F'
			RULE_HEXADECIMAL_DIGIT 'F'
			Semicolon ';'
			RULE_COLOR_NUMBER '1'
		''')
	}

	@Test def void oneColorValueWithoutWeight01() {
		"#E0E0E0".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'E0'
							g = 'E0'
							b = 'E0'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithoutWeight02() {
		"#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithWeight01() {
		"#3030FF;1".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '1'
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithWeight02() {
		"#C0C0C0;1".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '1'
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithWeight03() {
		"#C0C0C0;0.5".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.5'
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithWeight04() {
		"#C0C0C0;0.99408284".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.99408284'
					}
				]
			}
		''')
	}
	
	@Test def void oneColorValueWithWeight05() {
		"#3030FF;0.0213903743".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0213903743'
					}
				]
			}
		''')
	}
	
	@Test def void twoColorValuesWithoutWeight01() {
		"#C0C0C0:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = null
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}

	@Test def void twoColorValuesWithWeight01() {
		"#3030FF;0.0213903743:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0213903743'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void twoColorValuesWithWeight02() {
		"#C0C0C0;1:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '1'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void twoColorValuesWithWeight03() {
		"#3030FF;1:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '1'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void twoColorValuesWithWeight04() {
		"#C0C0C0;0.5:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.5'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void twoColorValuesWithWeight05() {
		"#C0C0C0;0.99408284:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.99408284'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}

	@Test def void threeColorValuesWithWeight01() {
		"#3030FF;0.5:#C0C0C0;0.5:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.5'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.5'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight02() {
		"#3030FF;1:#C0C0C0;1:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '1'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '1'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight03() {
		"#3030FF;0.36:#C0C0C0;0.08:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.36'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.08'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight04() {
		"#3030FF;0.0119926199:#C0C0C0;0.73800738:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0119926199'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.73800738'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight05() {
		"#3030FF;0.0555555556:#C0C0C0;0.037037037:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0555555556'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.037037037'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight06() {
		"#3030FF;0.0105485232:#C0C0C0;0.723628692:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0105485232'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.723628692'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight07() {
		"#3030FF;0.0186915888:#C0C0C0;0.981308411:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0186915888'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.981308411'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight08() {
		"#3030FF;0.0107719928:#C0C0C0;0.854578097:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0107719928'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.854578097'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight09() {
		"#3030FF;0.0498812352:#C0C0C0;0.250593824:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0498812352'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.250593824'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight10() {
		"#3030FF;0.0416666667:#C0C0C0;0.958333333:#8080FF".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.0416666667'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.958333333'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = null
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight11() {
		"#3030FF;0.1:#C0C0C0;0.2:#8080FF;0.7".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.1'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.7'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight12() {
		"#3030FF;0.2:#C0C0C0;0.2:#8080FF;0.6".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.6'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight13() {
		"#3030FF;0.3:#8080FF;0.2:#C0C0C0;0.5".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.3'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.5'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight14() {
		"#3030FF;0.4:#C0C0C0;0.2:#8080FF;0.4".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.4'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.4'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight15() {
		"#3030FF;0.5:#C0C0C0;0.2:#8080FF;0.3".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.5'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.3'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight16() {
		"#3030FF;0.6:#C0C0C0;0.2:#8080FF;0.2".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.6'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.2'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight17() {
		"#313233;0.7:#C0C1C2;0.2:#8080FF;0.1".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '31'
							g = '32'
							b = '33'
							a = null
						}
						weight = '0.7'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C1'
							b = 'C2'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.1'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight18() {
		"#3030FF;0.8:#C0C0C0;0.1:#8080FF;0.1".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.8'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.1'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.1'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight19() {
		"#3030FF;0.1:#C0C0C0;0.3:#8080FF;0.6".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.1'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.3'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.6'
					}
				]
			}
		''')
	}
	
	@Test def void threeColorValuesWithWeight20() {
		"#3030FF;0.2:#C0C0C0;0.4:#8080FF;0.4".assertAst('''
			ColorList {
				colorValues = [
					WC {
						color = RGBColor {
							r = '30'
							g = '30'
							b = 'FF'
							a = null
						}
						weight = '0.2'
					}
					WC {
						color = RGBColor {
							r = 'C0'
							g = 'C0'
							b = 'C0'
							a = null
						}
						weight = '0.4'
					}
					WC {
						color = RGBColor {
							r = '80'
							g = '80'
							b = 'FF'
							a = null
						}
						weight = '0.4'
					}
				]
			}
		''')
	}
	
	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val tokenFilePath = "../org.eclipse.gef.dot/src-gen/org/eclipse/gef/dot/internal/language/parser/antlr/lexer/CustomInternalDotColorListLexer.tokens";
		val actual = modelAsText.lex(lexer, tokenFilePath)
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
