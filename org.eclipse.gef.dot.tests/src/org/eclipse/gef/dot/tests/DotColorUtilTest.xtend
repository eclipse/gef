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
import org.eclipse.gef.dot.internal.language.DotColorInjectorProvider
import org.eclipse.gef.dot.internal.language.color.Color
import org.eclipse.gef.dot.internal.ui.DotColorUtil
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotColorInjectorProvider)
class DotColorUtilTest {

	@Inject extension ParseHelper<Color>
	@Inject extension ValidationTestHelper
	val dotColorUtil = new DotColorUtil

	@Test def rgb_color_white_to_zest_color() {
		"#ffffff".testZestColor("#ffffff")
	}

	@Test def rgb_color_black_to_zest_color() {
		"#000000".testZestColor("#000000")
	}

	@Test def rgb_color_red_to_zest_color() {
		"#ff0000".testZestColor("#ff0000")
	}

	@Test def rgb_color_turquoise_to_zest_color() {
		"#40e0d0".testZestColor("#40e0d0")
	}

	@Test def rgb_color_sienna_to_zest_color() {
		"#a0522d".testZestColor("#a0522d")
	}

	@Test def hsv_color_white_to_zest_color() {
		"0.000 0.000 1.000".testZestColor("hsb(0.0, 0.0%, 100.0%)")
	}

	@Test def hsv_color_black_to_zest_color() {
		"0.000 0.000 0.000".testZestColor("hsb(0.0, 0.0%, 0.0%)")
	}

	@Test def hsv_color_red_to_zest_color() {
		"0.000 1.000 1.000".testZestColor("hsb(0.0, 100.0%, 100.0%)")
	}

	@Test def hsv_color_turquoise_to_zest_color() {
		"0.482 0.714 0.878".testZestColor("hsb(173.51999999999998, 71.39999999999999%, 87.8%)")
	}

	@Test def hsv_color_sienna_to_zest_color() {
		"0.051 0.718 0.627".testZestColor("hsb(18.36, 71.8%, 62.7%)")
	}

	@Test def string_color_white_to_zest_color() {
		"white".testZestColor("#ffffff")
	}

	@Test def string_color_black_to_zest_color() {
		"black".testZestColor("#000000")
	}

	@Test def string_color_red_to_zest_color() {
		"red".testZestColor("#ff0000")
	}

	@Test def string_color_turquoise_to_zest_color() {
		"turquoise".testZestColor("#40e0d0")
	}

	@Test def string_color_sienna_to_zest_color() {
		"sienna".testZestColor("#a0522d")
	}

	@Test def rgb_color_white_to_graph_background_color() {
		"#ffffff".testGraphBackgroundColor("0xffffffff")
	}

	@Test def rgb_color_black_to_graph_background_color() {
		"#000000".testGraphBackgroundColor("0x000000ff")
	}

	@Test def rgb_color_red_to_graph_background_color() {
		"#ff0000".testGraphBackgroundColor("0xff0000ff")
	}

	@Test def rgb_color_turquoise_to_graph_background_color() {
		"#40e0d0".testGraphBackgroundColor("0x40e0d0ff")
	}

	@Test def rgb_color_sienna_to_graph_background_color() {
		"#a0522d".testGraphBackgroundColor("0xa0522dff")
	}

	@Test def hsv_color_white_to_graph_background_color() {
		"0.000 0.000 1.000".testGraphBackgroundColor("0xffffffff")
	}

	@Test def hsv_color_black_to_graph_background_color() {
		"0.000 0.000 0.000".testGraphBackgroundColor("0x000000ff")
	}

	@Test def hsv_color_red_to_graph_background_color() {
		"0.000 1.000 1.000".testGraphBackgroundColor("0xff0000ff")
	}

	@Test def hsv_color_turquoise_to_graph_background_color() {
		// TODO: check the small differences, should be 0x40e0d0ff
		"0.482 0.714 0.878".testGraphBackgroundColor("0x40e0cfff")
	}

	@Test def hsv_color_sienna_to_graph_background_color() {
		// TODO: check the small differences, should be 0xa0522dff
		"0.051 0.718 0.627".testGraphBackgroundColor("0xa0502dff")
	}

	@Test def string_color_white_to_graph_background_color() {
		"white".testGraphBackgroundColor("0xffffffff")
	}

	@Test def string_color_black_to_graph_background_color() {
		"black".testGraphBackgroundColor("0x000000ff")
	}

	@Test def string_color_red_to_graph_background_color() {
		"red".testGraphBackgroundColor("0xff0000ff")
	}

	@Test def string_color_turquoise_to_graph_background_color() {
		"turquoise".testGraphBackgroundColor("0x40e0d0ff")
	}

	@Test def string_color_sienna_to_graph_background_color() {
		"sienna".testGraphBackgroundColor("0xa0522dff")
	}

	private def testZestColor(String dotColorText, String expected) {
		// given
		val dotColor = dotColorText.parse
		dotColor.assertNoErrors
		
		// when
		val actual = dotColorUtil.computeZestColor(null, dotColor)
		
		// then
		expected.assertEquals(actual)
	}

	private def testGraphBackgroundColor(String dotColorText, String expected) {
		// given
		val dotColor = dotColorText.parse
		dotColor.assertNoErrors
		
		// when
		val actual = dotColorUtil.computeGraphBackgroundColor(null, dotColor)
		
		// then
		expected.assertEquals(actual.toString)
	}

}