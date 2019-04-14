/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.DotFontNameInjectorProvider
import org.eclipse.gef.dot.internal.language.fontname.FontName
import org.eclipse.gef.dot.internal.language.fontname.FontnameFactory
import org.eclipse.gef.dot.internal.language.fontname.Gravity
import org.eclipse.gef.dot.internal.language.fontname.GravityOption
import org.eclipse.gef.dot.internal.language.fontname.PostScriptFontAlias
import org.eclipse.gef.dot.internal.language.fontname.Stretch
import org.eclipse.gef.dot.internal.language.fontname.StretchOption
import org.eclipse.gef.dot.internal.language.fontname.Style
import org.eclipse.gef.dot.internal.language.fontname.StyleOption
import org.eclipse.gef.dot.internal.language.fontname.StyleOptionsElement
import org.eclipse.gef.dot.internal.language.fontname.Variant
import org.eclipse.gef.dot.internal.language.fontname.VariantOption
import org.eclipse.gef.dot.internal.language.fontname.Weight
import org.eclipse.gef.dot.internal.language.fontname.WeightOption
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotFontNameInjectorProvider)
class DotFontNameTest {

	@Inject extension ParseHelper<FontName>
	@Inject extension ValidationTestHelper
	extension FontnameFactory = FontnameFactory.eINSTANCE

	/*
	 * good syntax
	 */
	@Test def void empty_string() {
		''''''.assertTreeEquals(
			pangoFontName(null, null, null)
		).
		hasFamilyList("")
	}

	/*
	 * All PostScript Alias Tests
	 */ 
	@Test def avantgarde_book() {
		'''AvantGarde-Book'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.AVANTGARDE_BOOK)
		).
		hasWeight(Weight.BOOK).
		hasFamilyList(
			"AvantGarde,URW Gothic L,Charcoal,Nimbus Sans L,Verdana,Helvetica,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def avantgarde_book_oblique() {
		'''AvantGarde-BookOblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.AVANTGARDE_BOOKOBLIQUE)
		).
		hasWeight(Weight.BOOK).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"AvantGarde,URW Gothic L,Charcoal,Nimbus Sans L,Verdana,Helvetica,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def avantgarde_demi() {
		'''AvantGarde-Demi'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.AVANTGARDE_DEMI)
		).
		hasWeight(Weight.SEMILIGHT).
		hasFamilyList(
			"AvantGarde,URW Gothic L,Charcoal,Nimbus Sans L,Verdana,Helvetica,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def avantgarde_demi_oblique() {
		'''AvantGarde-DemiOblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.AVANTGARDE_DEMIOBLIQUE)
		).
		hasWeight(Weight.SEMILIGHT).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"AvantGarde,URW Gothic L,Charcoal,Nimbus Sans L,Verdana,Helvetica,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def bookman_demi() {
		'''Bookman-Demi'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.BOOKMAN_DEMI)
		).
		hasWeight(Weight.SEMILIGHT).
		hasFamilyList(
			"Bookman,URW Bookman L,Times New Roman,Times,Nimbus Roman No9 L,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def bookman_demi_italic() {
		'''Bookman-DemiItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.BOOKMAN_DEMIITALIC)
		).
		hasWeight(Weight.SEMILIGHT).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Bookman,URW Bookman L,Times New Roman,Times,Nimbus Roman No9 L,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def bookman_light() {
		'''Bookman-Light'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.BOOKMAN_LIGHT)
		).
		hasWeight(Weight.LIGHT).
		hasFamilyList(
			"Bookman,URW Bookman L,Times New Roman,Times,Nimbus Roman No9 L,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def bookman_light_italic() {
		'''Bookman-LightItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.BOOKMAN_LIGHTITALIC)
		).
		hasWeight(Weight.LIGHT).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Bookman,URW Bookman L,Times New Roman,Times,Nimbus Roman No9 L,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def courier() {
		'''Courier'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.COURIER)
		).
		hasFamilyList(
			"Courier,Nimbus Mono L,Inconsolata,Courier New,Bitstream Vera Sans Mono,DejaVu Sans Mono,Liberation Mono,Luxi Mono,FreeMono,monospace")
	}

	@Test def courier_bold() {
		'''Courier-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.COURIER_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"Courier,Nimbus Mono L,Inconsolata,Courier New,Bitstream Vera Sans Mono,DejaVu Sans Mono,Liberation Mono,Luxi Mono,FreeMono,monospace")
	}

	@Test def courier_bold_oblique() {
		'''Courier-BoldOblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.COURIER_BOLDOBLIQUE)
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"Courier,Nimbus Mono L,Inconsolata,Courier New,Bitstream Vera Sans Mono,DejaVu Sans Mono,Liberation Mono,Luxi Mono,FreeMono,monospace")
	}

	@Test def courier_oblique() {
		'''Courier-Oblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.COURIER_OBLIQUE)
		).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"Courier,Nimbus Mono L,Inconsolata,Courier New,Bitstream Vera Sans Mono,DejaVu Sans Mono,Liberation Mono,Luxi Mono,FreeMono,monospace")
	}

	@Test def helvetica() {
		'''Helvetica'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA)
		).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_bold() {
		'''Helvetica-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_bold_oblique() {
		'''Helvetica-BoldOblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_BOLDOBLIQUE)
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_narrow() {
		'''Helvetica-Narrow'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_NARROW)
		).
		hasStretch(Stretch.CONDENSED).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_narrow_bold() {
		'''Helvetica-Narrow-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_NARROW_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasStretch(Stretch.CONDENSED).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_narrow_boldoblique() {
		'''Helvetica-Narrow-BoldOblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_NARROW_BOLDOBLIQUE)
		).
		hasWeight(Weight.BOLD).
		hasStretch(Stretch.CONDENSED).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_narrow_oblique() {
		'''Helvetica-Narrow-Oblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_NARROW_OBLIQUE)
		).
		hasStyle(Style.OBLIQUE).
		hasStretch(Stretch.CONDENSED).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def helvetica_oblique() {
		'''Helvetica-Oblique'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.HELVETICA_OBLIQUE)
		).
		hasStyle(Style.OBLIQUE).
		hasFamilyList(
			"Helvetica,Nimbus Sans L,Arial,Verdana,Bitstream Vera Sans,DejaVu Sans,Liberation Sans,Luxi Sans,FreeSans,sans")
	}

	@Test def newcenturyschlbk_bold() {
		'''NewCenturySchlbk-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.NEWCENTURYSCHLBK_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"NewCenturySchlbk,URW Bookman L,Times New Roman,Times,Georgia,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def newcenturyschlbk_bold_italic() {
		'''NewCenturySchlbk-BoldItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.NEWCENTURYSCHLBK_BOLDITALIC)
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"NewCenturySchlbk,URW Bookman L,Times New Roman,Times,Georgia,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def newcenturyschlbk_italic() {
		'''NewCenturySchlbk-Italic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.NEWCENTURYSCHLBK_ITALIC)
		).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"NewCenturySchlbk,URW Bookman L,Times New Roman,Times,Georgia,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def newcenturyschlbk_roman() {
		'''NewCenturySchlbk-Roman'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.NEWCENTURYSCHLBK_ROMAN)
		).
		hasWeight(Weight.NORMAL).
		hasFamilyList(
			"NewCenturySchlbk,URW Bookman L,Times New Roman,Times,Georgia,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def palatino_bold() {
		'''Palatino-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.PALATINO_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"Palatino,Times New Roman,Times,Nimbus Roman No9 L,Norasi,Rekha,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def palatino_bold_italic() {
		'''Palatino-BoldItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.PALATINO_BOLDITALIC)
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Palatino,Times New Roman,Times,Nimbus Roman No9 L,Norasi,Rekha,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def palatino_italic() {
		'''Palatino-Italic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.PALATINO_ITALIC)
		).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Palatino,Times New Roman,Times,Nimbus Roman No9 L,Norasi,Rekha,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def palatino_roman() {
		'''Palatino-Roman'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.PALATINO_ROMAN)
		).
		hasFamilyList(
			"Palatino,Times New Roman,Times,Nimbus Roman No9 L,Norasi,Rekha,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def symbol() {
		'''Symbol'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.SYMBOL)
		).
		hasFamilyList("Symbol,Impact,Copperplate Gothic Std,Cooper Std,Bauhaus Std,fantasy")
	}

	@Test def times_bold() {
		'''Times-Bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.TIMES_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"Times,Nimbus Roman No9 L,Times New Roman,Charcoal,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def times_bold_italic() {
		'''Times-BoldItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.TIMES_BOLDITALIC)
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Times,Nimbus Roman No9 L,Times New Roman,Charcoal,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def times_italic() {
		'''Times-Italic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.TIMES_ITALIC)
		).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"Times,Nimbus Roman No9 L,Times New Roman,Charcoal,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def times_roman() {
		'''Times-Roman'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.TIMES_ROMAN)
		).
		hasFamilyList(
			"Times,Nimbus Roman No9 L,Times New Roman,Charcoal,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def zapfchancery_medium_italic() {
		'''ZapfChancery-MediumItalic'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.ZAPFCHANCERY_MEDIUMITALIC)
		).
		hasWeight(Weight.MEDIUM).
		hasStyle(Style.ITALIC).
		hasFamilyList(
			"ZapfChancery,URW Chancery L,Charcoal,Times New Roman,Times,Nimbus Roman No9 L,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	@Test def zapfdingbats() {
		'''ZapfDingbats'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.ZAPFDINGBATS)
		).
		hasFamilyList("ZapfDingbats,Dingbats,Impact,Copperplate Gothic Std,Cooper Std,Bauhaus Std,fantasy")
	}

	@Test def times_bold_variedCase() {
		'''tiMeS-bold'''.
		assertTreeEquals(
			postScriptFontName(PostScriptFontAlias.TIMES_BOLD)
		).
		hasWeight(Weight.BOLD).
		hasFamilyList(
			"Times,Nimbus Roman No9 L,Times New Roman,Charcoal,Bitstream Vera Serif,DejaVu Serif,Liberation Serif,Luxi Serif,FreeSerif,serif")
	}

	/*
	 * Pango Tests:
	 * 
	 * Pango vs PS Alias tests	
	 */
	@Test def pango_vs_psalias_001() {
		'''ZapfChancery-MediumOblique'''.
		assertTreeEquals(
			pangoFontName("ZapfChancery-MediumOblique")
		).
		hasFamilyList("ZapfChancery-MediumOblique")
	}

	@Test def pango_vs_psalias_002() {
		'''ZapfChancery-Medium'''.
		assertTreeEquals(
			pangoFontName("ZapfChancery-Medium")
		).
		hasFamilyList("ZapfChancery-Medium")
	}

	@Test def pango_vs_psalias_003() {
		'''ZapfChancery-MediumItalicBold'''.
		assertTreeEquals(
			pangoFontName("ZapfChancery-MediumItalicBold")
		).
		hasFamilyList("ZapfChancery-MediumItalicBold")
	}

	@Test def pango_vs_psalias_004() {
		'''ZapfChancery-Medium, ZapfChancery-Bold'''.
		assertTreeEquals(
			pangoFontName(familyList("ZapfChancery-Medium"), "ZapfChancery-Bold")
		).
		hasFamilyList("ZapfChancery-Medium,ZapfChancery-Bold")
	}

	@Test def pango_vs_psalias_005() {
		'''Times Bold'''.
		assertTreeEquals(
			pangoFontName("Times", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Times")
	}

	/*
	 * Various Pango Tests (digits (not) part of FontFamily)
	 */
	@Test def pango_tests_001() {
		'''Arial, Times Bold'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial"), "Times", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial,Times")
	}

	@Test def pango_tests_002() {
		'''Arial, Times,Bold'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial", "Times"), weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial,Times")
	}

	@Test def pango_tests_003() {
		'''Arial 95, Times, Bold 2'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial 95", "Times"), weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95,Times")
	}

	@Test def pango_tests_004() {
		'''Arial 95, Times Bold'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial 95"), "Times", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95,Times")
	}

	@Test def pango_tests_005() {
		'''Arial 95 95 95, Times, Courier bold'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial 95 95 95", "Times"), "Courier", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95 95 95,Times,Courier")
	}

	@Test def pango_tests_006() {
		'''Arial 95 95 95'''.
		assertTreeEquals(
			pangoFontName("Arial 95 95")
		).
		hasFamilyList("Arial 95 95")
	}

	@Test def pango_tests_007() {
		'''Arial 95 95 95 Bold 95'''.
		assertTreeEquals(
			pangoFontName("Arial 95 95 95", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95 95 95")
	}

	@Test def pango_tests_008() {
		'''Arial 95 95, 95 Bold 95'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial 95 95"), "95", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95 95,95")
	}

	@Test def pango_tests_009() {
		'''Arial 95 95, Bold 95'''.
		assertTreeEquals(
			pangoFontName(familyList("Arial 95 95"), weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95 95")
	}

	@Test def pango_tests_010() {
		'''Arial 95'''.
		assertTreeEquals(
			pangoFontName("Arial")
		).
		hasFamilyList("Arial")
	}

	@Test def pango_tests_011() {
		'''Arial 95 Bold'''.
		assertTreeEquals(
			pangoFontName("Arial 95", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial 95")
	}

	@Test def pango_tests_012() {
		'''Arial Bold 95'''.
		assertTreeEquals(
			pangoFontName("Arial", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("Arial")
	}

	@Test def pango_tests_013() {
		'''Bold Arial'''.
		assertTreeEquals(
			pangoFontName("Bold Arial")
		).
		hasWeight(null).
		hasFamilyList("Bold Arial")
	}

	@Test def pango_tests_014() {
		'''arial bold'''.
		assertTreeEquals(
			pangoFontName("arial", weightOption(Weight.BOLD))
		).
		hasWeight(Weight.BOLD).
		hasFamilyList("arial")
	}

	/*
	 * Standard Tests
	 */
	@Test def full_test_001() {
		'''Sans Bold Oblique Not-Rotated Expanded Small-Caps 15'''.
		assertTreeEquals(
			pangoFontName("Sans", weightOption(Weight.BOLD), styleOption(Style.OBLIQUE), gravityOption(Gravity.SOUTH),
				stretchOption(Stretch.EXPANDED), variantOption(Variant.SMALL_CAPS))
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.OBLIQUE).
		hasGravity(Gravity.SOUTH).
		hasStretch(Stretch.EXPANDED).
		hasVariant(Variant.SMALL_CAPS).
		hasFamilyList("Sans")
	}

	@Test def full_test_002() {
		'''Comic Sans, Arial, DejaVu Sans Bold Italic'''.
		assertTreeEquals(
			pangoFontName(familyList("Comic Sans", "Arial"), "DejaVu Sans", weightOption(Weight.BOLD), styleOption(Style.ITALIC))
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.ITALIC).
		hasFamilyList("Comic Sans,Arial,DejaVu Sans")
	}

	@Test def full_test_003() {
		'''Comic Sans, Arial, Times New Roman Bold Italic'''.
		assertTreeEquals(
			pangoFontName(familyList("Comic Sans", "Arial"), "Times New", styleOption(Style.NORMAL), weightOption(Weight.BOLD), styleOption(Style.ITALIC))
		).
		hasWeight(Weight.BOLD).
		hasStyle(Style.NORMAL).
		hasFamilyList("Comic Sans,Arial,Times New")
	}

	/*
	 * Tests for all Style Options
	 */
	@Test def gravity_all_options() {
		pangoStyleOptionTest("not-rotated", gravityOption(Gravity.SOUTH))
		pangoStyleOptionTest("south", gravityOption(Gravity.SOUTH))
		pangoStyleOptionTest("upside-down", gravityOption(Gravity.NORTH))
		pangoStyleOptionTest("north", gravityOption(Gravity.NORTH))
		pangoStyleOptionTest("rotated-left", gravityOption(Gravity.EAST))
		pangoStyleOptionTest("east", gravityOption(Gravity.EAST))
		pangoStyleOptionTest("rotated-right", gravityOption(Gravity.WEST))
		pangoStyleOptionTest("west", gravityOption(Gravity.WEST))
	}

	@Test def stretch_all_options() {
		pangoStyleOptionTest("ultra-condensed", stretchOption(Stretch.ULTRA_CONDENSED))
		pangoStyleOptionTest("extra-condensed", stretchOption(Stretch.EXTRA_CONDENSED))
		pangoStyleOptionTest("condensed", stretchOption(Stretch.CONDENSED))
		pangoStyleOptionTest("semi-condensed", stretchOption(Stretch.SEMI_CONDENSED))
		pangoStyleOptionTest("ultra-expanded", stretchOption(Stretch.ULTRA_EXPANDED))
		pangoStyleOptionTest("extra-expanded", stretchOption(Stretch.EXTRA_EXPANDED))
		pangoStyleOptionTest("expanded", stretchOption(Stretch.EXPANDED))
		pangoStyleOptionTest("semi-expanded", stretchOption(Stretch.SEMI_EXPANDED))
	}

	@Test def style_all_options() {
		pangoStyleOptionTest("roman", styleOption(Style.NORMAL))
		pangoStyleOptionTest("oblique", styleOption(Style.OBLIQUE))
		pangoStyleOptionTest("italic", styleOption(Style.ITALIC))
	}

	@Test def variant_all_options() {
		pangoStyleOptionTest("small-caps", variantOption(Variant.SMALL_CAPS))
	}

	@Test def weight_all_options() {
		pangoStyleOptionTest("regular", weightOption(Weight.NORMAL))
		pangoStyleOptionTest("thin", weightOption(Weight.THIN))
		pangoStyleOptionTest("ultra-light", weightOption(Weight.ULTRALIGHT))
		pangoStyleOptionTest("extra-light", weightOption(Weight.ULTRALIGHT))
		pangoStyleOptionTest("light", weightOption(Weight.LIGHT))
		pangoStyleOptionTest("semi-light", weightOption(Weight.SEMILIGHT))
		pangoStyleOptionTest("demi-light", weightOption(Weight.SEMILIGHT))
		pangoStyleOptionTest("book", weightOption(Weight.BOOK))
		pangoStyleOptionTest("medium", weightOption(Weight.MEDIUM))
		pangoStyleOptionTest("semi-bold", weightOption(Weight.SEMIBOLD))
		pangoStyleOptionTest("demi-bold", weightOption(Weight.SEMIBOLD))
		pangoStyleOptionTest("bold", weightOption(Weight.BOLD))
		pangoStyleOptionTest("ultra-bold", weightOption(Weight.ULTRABOLD))
		pangoStyleOptionTest("extra-bold", weightOption(Weight.ULTRABOLD))
		pangoStyleOptionTest("heavy", weightOption(Weight.HEAVY))
		pangoStyleOptionTest("black", weightOption(Weight.HEAVY))
		pangoStyleOptionTest("ultra-heavy", weightOption(Weight.ULTRAHEAVY))
		pangoStyleOptionTest("extra-heavy", weightOption(Weight.ULTRAHEAVY))
		pangoStyleOptionTest("ultra-black", weightOption(Weight.ULTRAHEAVY))
		pangoStyleOptionTest("extra-black", weightOption(Weight.ULTRAHEAVY))
	}

	/*
	 * helper methods
	 */
	private def void pangoStyleOptionTest(CharSequence sequence, StyleOptionsElement element) {
		sequence.assertTreeEquals(
			pangoFontName(element)
		)
		if (element instanceof WeightOption) {
			sequence.hasWeight(element.getWeight)
		} else if (element instanceof StyleOption) {
			sequence.hasStyle(element.getStyle)
		} else if (element instanceof GravityOption) {
			sequence.hasGravity(element.getGravity)
		} else if (element instanceof StretchOption) {
			sequence.hasStretch(element.getStretch)
		} else if (element instanceof VariantOption) {
			sequence.hasVariant(element.getVariant)
		}
	}

	private def assertTreeEquals(CharSequence sequenceForParsing, EObject expected) {
		sequenceForParsing.hasNoErrors.parse.assertTreeEquals(expected)
		sequenceForParsing
	}

	private def EObject assertTreeEquals(EObject actual, EObject expected) {
		assertEquals("Objects of different classtype ", expected.eClass, actual.eClass)
		for (attribute : expected.eClass.EAllAttributes) {
			assertEquals("Attribute " + attribute.name + " of class " + expected.eClass.name, expected.eGet(attribute),
				actual.eGet(attribute))
		}
		assertEquals("Number of Child Nodes", expected.eContents.size, actual.eContents.size)
		for (var i = 0; i < expected.eContents.size; i++) {
			actual.eContents.get(i).assertTreeEquals(expected.eContents.get(i))
		}
		actual
	}

	private def hasNoErrors(CharSequence sequence) {
		sequence.parse.assertNoErrors
		sequence
	}

	private def pangoFontName(StyleOptionsElement... styleOptions) {
		pangoFontName(null, null, styleOptions)
	}

	private def pangoFontName(String finalFamily, StyleOptionsElement... styleOptions) {
		pangoFontName(null, finalFamily, styleOptions)
	}

	private def pangoFontName(List<String> familyList, StyleOptionsElement... styleOptions) {
		pangoFontName(familyList, null, styleOptions)
	}

	private def pangoFontName(List<String> familyList, String finalFamily, StyleOptionsElement... styleOptions) {
		val pangoFontName = createPangoFontName
		if (finalFamily !== null) {
			pangoFontName.finalFamily += finalFamily.split(" ");
		}
		if (familyList !== null) {
			pangoFontName.families += familyList
		}
		if (styleOptions !== null) {
			pangoFontName.styleOptionsList += styleOptions
		}
		pangoFontName
	}

	private def postScriptFontName(PostScriptFontAlias alias) {
		createPostScriptFontName => [it.alias = alias]
	}

	private def familyList(String... families) {
		families.toList
	}

	private def gravityOption(Gravity gravity) {
		createGravityOption => [it.gravity = gravity]
	}

	private def stretchOption(Stretch stretch) {
		createStretchOption => [it.stretch = stretch]
	}

	private def styleOption(Style style) {
		createStyleOption => [it.style = style]
	}

	private def variantOption(Variant variant) {
		createVariantOption => [it.variant = variant]
	}

	private def weightOption(Weight weight) {
		createWeightOption => [it.weight = weight]
	}

	private def void hasFamilyList(CharSequence sequence, String families) {
		families.assertEquals(sequence.parse.fontFamilies.join(","));
	}

	private def hasGravity(CharSequence sequence, Gravity gravity) {
		gravity.assertEquals(sequence.parse.gravity)
		sequence
	}

	private def hasStretch(CharSequence sequence, Stretch stretch) {
		stretch.assertEquals(sequence.parse.stretch)
		sequence
	}

	private def hasStyle(CharSequence sequence, Style style) {
		style.assertEquals(sequence.parse.style)
		sequence
	}

	private def hasVariant(CharSequence sequence, Variant variant) {
		variant.assertEquals(sequence.parse.variant)
		sequence
	}

	private def hasWeight(CharSequence sequence, Weight weight) {
		weight.assertEquals(sequence.parse.weight)
		sequence
	}
}
