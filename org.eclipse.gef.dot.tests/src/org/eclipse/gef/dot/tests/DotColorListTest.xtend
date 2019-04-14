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
import org.eclipse.gef.dot.internal.language.DotColorListInjectorProvider
import org.eclipse.gef.dot.internal.language.colorlist.ColorList
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotColorListInjectorProvider)
class DotColorListTest {

	@Inject extension ParseHelper<ColorList>
	@Inject extension ValidationTestHelper
	@Inject extension DotEObjectFormatter

	@Test def one_color_value_without_weight_01() {
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

	@Test def one_color_value_without_weight_02() {
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

	@Test def one_color_value_with_weight_01() {
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

	@Test def one_color_value_with_weight_02() {
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

	@Test def one_color_value_with_weight_03() {
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

	@Test def one_color_value_with_weight_04() {
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

	@Test def one_color_value_with_weight_05() {
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

	@Test def two_color_values_without_weight_01() {
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

	@Test def two_color_values_with_weight_01() {
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

	@Test def two_color_values_with_weight_02() {
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

	@Test def two_color_values_with_weight_03() {
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

	@Test def two_color_values_with_weight_04() {
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

	@Test def two_color_values_with_weight_05() {
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

	@Test def three_color_values_with_weight_01() {
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

	@Test def three_color_values_with_weight_02() {
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

	@Test def three_color_values_with_weight_03() {
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

	@Test def three_color_values_with_weight_04() {
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

	@Test def three_color_values_with_weight_05() {
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

	@Test def three_color_values_with_weight_06() {
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

	@Test def three_color_values_with_weight_07() {
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

	@Test def three_color_values_with_weight_08() {
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

	@Test def three_color_values_with_weight_09() {
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

	@Test def three_color_values_with_weight_10() {
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

	@Test def three_color_values_with_weight_11() {
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

	@Test def three_color_values_with_weight_12() {
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

	@Test def three_color_values_with_weight_13() {
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

	@Test def three_color_values_with_weight_14() {
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

	@Test def three_color_values_with_weight_15() {
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

	@Test def three_color_values_with_weight_16() {
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

	@Test def three_color_values_with_weight_17() {
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

	@Test def three_color_values_with_weight_18() {
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

	@Test def three_color_values_with_weight_19() {
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

	@Test def three_color_values_with_weight_20() {
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

	private def assertAst(CharSequence modelAsText,
			CharSequence expected) {
		val ast = modelAsText.parse
		ast.assertNoErrors
		val astString = ast.apply
		expected.toString.assertEquals(astString.toString)
	}
}
