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
package org.eclipse.gef.dot.internal.language.fontname;

import java.util.Arrays;
import java.util.List;

/**
 * The graphviz supported postscript font names in graphviz's
 * src/common/ps_font_equiv.txt
 */
public enum PostScriptFontAlias {

	/**
	 * The AvantGarde-Book fontname
	 */
	AVANTGARDE_BOOK(Family.AVANTGARDE_FAMILY, Weight.BOOK, null, null),
	/**
	 * The AvantGarde-BookOblique fontname
	 */
	AVANTGARDE_BOOKOBLIQUE(Family.AVANTGARDE_FAMILY, Weight.BOOK, Style.OBLIQUE,
			null),
	/**
	 * The AvantGarde-Demi fontname
	 */
	AVANTGARDE_DEMI(Family.AVANTGARDE_FAMILY, Weight.SEMILIGHT /* DEMI */, null,
			null),
	/**
	 * The AvantGarde-DemiOblique fontname
	 */
	AVANTGARDE_DEMIOBLIQUE(Family.AVANTGARDE_FAMILY,
			Weight.SEMILIGHT /* DEMI */, Style.OBLIQUE, null),
	/**
	 * The Bookman-Demi fontname
	 */
	BOOKMAN_DEMI(Family.BOOKMAN_FAMILY, Weight.SEMILIGHT /* DEMI */, null,
			null),
	/**
	 * The Bookman-DemiItalic fontname
	 */
	BOOKMAN_DEMIITALIC(Family.BOOKMAN_FAMILY, Weight.SEMILIGHT /* DEMI */,
			Style.ITALIC, null),
	/**
	 * The Bookman-Light fontname
	 */
	BOOKMAN_LIGHT(Family.BOOKMAN_FAMILY, Weight.LIGHT, null, null),
	/**
	 * The Bookman-LightItalic fontname
	 */
	BOOKMAN_LIGHTITALIC(Family.BOOKMAN_FAMILY, Weight.LIGHT, Style.ITALIC,
			null),
	/**
	 * The Courier fontname
	 */
	COURIER(Family.COURIER_FAMILY, null, null, null),
	/**
	 * The Courier-Bold fontname
	 */
	COURIER_BOLD(Family.COURIER_FAMILY, Weight.BOLD, null, null),
	/**
	 * The Courier-BoldOblique fontname
	 */
	COURIER_BOLDOBLIQUE(Family.COURIER_FAMILY, Weight.BOLD, Style.OBLIQUE,
			null),
	/**
	 * The Courier-Oblique fontname
	 */
	COURIER_OBLIQUE(Family.COURIER_FAMILY, null, Style.OBLIQUE, null),
	/**
	 * The Helvetica fontname
	 */
	HELVETICA(Family.HELVETICA_FAMILY, null, null, null),
	/**
	 * The Helvetica-Bold fontname
	 */
	HELVETICA_BOLD(Family.HELVETICA_FAMILY, Weight.BOLD, null, null),
	/**
	 * The Helvetica-BoldOblique fontname
	 */
	HELVETICA_BOLDOBLIQUE(Family.HELVETICA_FAMILY, Weight.BOLD, Style.OBLIQUE,
			null),
	/**
	 * The Helvetica-Narrow fontname
	 */
	HELVETICA_NARROW(Family.HELVETICA_FAMILY, null, null, Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-Bold fontname
	 */
	HELVETICA_NARROW_BOLD(Family.HELVETICA_FAMILY, Weight.BOLD, null,
			Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-BoldOblique fontname
	 */
	HELVETICA_NARROW_BOLDOBLIQUE(Family.HELVETICA_FAMILY, Weight.BOLD, null,
			Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-Oblique fontname
	 */
	HELVETICA_NARROW_OBLIQUE(Family.HELVETICA_FAMILY, null, Style.OBLIQUE,
			Stretch.CONDENSED),
	/**
	 * The Helvetica-Oblique fontname
	 */
	HELVETICA_OBLIQUE(Family.HELVETICA_FAMILY, null, Style.OBLIQUE, null),
	/**
	 * The NewCenturySchoolbk-Bold fontname
	 */
	NEWCENTURYSCHLBK_BOLD(Family.NEWCENTURYSCHLBK_FAMILY, Weight.BOLD, null,
			null),
	/**
	 * The NewCenturySchoolbk-BoldItalic fontname
	 */
	NEWCENTURYSCHLBK_BOLDITALIC(Family.NEWCENTURYSCHLBK_FAMILY, Weight.BOLD,
			Style.ITALIC, null),
	/**
	 * The NewCenturySchoolbk-Italic fontname
	 */
	NEWCENTURYSCHLBK_ITALIC(Family.NEWCENTURYSCHLBK_FAMILY, null, Style.ITALIC,
			null),
	/**
	 * The NewCenturySchoolbk-Roman fontname
	 */
	NEWCENTURYSCHLBK_ROMAN(Family.NEWCENTURYSCHLBK_FAMILY,
			Weight.NORMAL /* ROMAN */, null, null),
	/**
	 * The Palatino-Bold fontname
	 */
	PALATINO_BOLD(Family.PALATINO_FAMILY, Weight.BOLD, null, null),
	/**
	 * The Palatino-BoldItalic fontname
	 */
	PALATINO_BOLDITALIC(Family.PALATINO_FAMILY, Weight.BOLD, Style.ITALIC,
			null),
	/**
	 * The Palatino-Italic fontname
	 */
	PALATINO_ITALIC(Family.PALATINO_FAMILY, null, Style.ITALIC, null),
	/**
	 * The Palatino-Roman fontname
	 */
	PALATINO_ROMAN(Family.PALATINO_FAMILY, Weight.NORMAL /* ROMAN */, null,
			null),
	/**
	 * The Symbol fontname
	 */
	SYMBOL(Family.SYMBOL_FAMILY, null, null, null),
	/**
	 * The Times-Bold fontname
	 */
	TIMES_BOLD(Family.TIMES_FAMILY, Weight.BOLD, null, null),
	/**
	 * The Times-BoldItalic fontname
	 */
	TIMES_BOLDITALIC(Family.TIMES_FAMILY, Weight.BOLD, Style.ITALIC, null),
	/**
	 * The Times-Italic fontname
	 */
	TIMES_ITALIC(Family.TIMES_FAMILY, null, Style.ITALIC, null),
	/**
	 * The Times-Roman fontname
	 */
	TIMES_ROMAN(Family.TIMES_FAMILY, null, null, null),
	/**
	 * The ZapfChancery-MediumItalic fontname
	 */
	ZAPFCHANCERY_MEDIUMITALIC(Family.CHANCERY_FAMILY, Weight.MEDIUM,
			Style.ITALIC, null),
	/**
	 * The ZapfDingbats fontname
	 */
	ZAPFDINGBATS(Family.DINGBATS_FAMILY, null, null, null);

	private final Weight weight;
	private final Style style;
	private final Stretch stretch;

	private PostScriptFontAlias(Family family, Weight weight, Style style,
			Stretch stretch) {
		this.family = family;
		this.weight = weight;
		this.style = style;
		this.stretch = stretch;
	}

	private final Family family;

	/**
	 * @return The font's font family
	 */
	public Family getFamily() {
		return family;
	}

	/**
	 * @return The font's postscript weight
	 */
	public Weight getWeight() {
		return weight;
	}

	/**
	 * @return The font's postscript style
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @return The font's postscript stretch
	 */
	public Stretch getStretch() {
		return stretch;
	}

	/**
	 * An enum representing the supported postscript font families
	 */
	public static enum Family {
		/**
		 * The AvantGarde postscript font family
		 */
		AVANTGARDE_FAMILY("AvantGarde", "sans", "URW Gothic L", "Charcoal",
				"Nimbus Sans L", "Verdana", "Helvetica", "Bitstream Vera Sans",
				"DejaVu Sans", "Liberation Sans", "Luxi Sans", "FreeSans"),
		/**
		 * The Bookman postscript font family
		 */
		BOOKMAN_FAMILY("Bookman", "serif", "URW Bookman L", "Times New Roman",
				"Times", "Nimbus Roman No9 L", "Bitstream Vera Serif",
				"DejaVu Serif", "Liberation Serif", "Luxi Serif", "FreeSerif"),
		/**
		 * The Courier postscript font family
		 */
		COURIER_FAMILY("Courier", "monospace", "Nimbus Mono L", "Inconsolata",
				"Courier New", "Bitstream Vera Sans Mono", "DejaVu Sans Mono",
				"Liberation Mono", "Luxi Mono", "FreeMono"),
		/**
		 * The Helvetica postscript font family
		 */
		HELVETICA_FAMILY("Helvetica", "sans", "Nimbus Sans L", "Arial",
				"Verdana", "Bitstream Vera Sans", "DejaVu Sans",
				"Liberation Sans", "Luxi Sans", "FreeSans"),
		/**
		 * The NewCenturySchoolbook postscript font family
		 */
		NEWCENTURYSCHLBK_FAMILY("NewCenturySchlbk", "serif", "URW Bookman L",
				"Times New Roman", "Times", "Georgia", "Bitstream Vera Serif",
				"DejaVu Serif", "Liberation Serif", "Luxi Serif", "FreeSerif"),
		/**
		 * The Palatino postscript font family
		 */
		PALATINO_FAMILY("Palatino", "serif", "Times New Roman", "Times",
				"Nimbus Roman No9 L", "Norasi", "Rekha", "Bitstream Vera Serif",
				"DejaVu Serif", "Liberation Serif", "Luxi Serif", "FreeSerif"),
		/**
		 * The Symbol postscript font family
		 */
		SYMBOL_FAMILY("Symbol", "fantasy", "Impact", "Copperplate Gothic Std",
				"Cooper Std", "Bauhaus Std"),
		/**
		 * The Times postscript font family
		 */
		TIMES_FAMILY("Times", "serif", "Nimbus Roman No9 L", "Times New Roman",
				"Charcoal", "Bitstream Vera Serif", "DejaVu Serif",
				"Liberation Serif", "Luxi Serif", "FreeSerif"),

		/**
		 * The ZapfChancery postscript font family
		 */
		CHANCERY_FAMILY("ZapfChancery", "serif", "URW Chancery L", "Charcoal",
				"Times New Roman", "Times", "Nimbus Roman No9 L",
				"Bitstream Vera Serif", "DejaVu Serif", "Liberation Serif",
				"Luxi Serif", "FreeSerif"),
		/**
		 * The ZapfDingbats postscript font family
		 */
		DINGBATS_FAMILY("ZapfDingbats", "fantasy", "Dingbats", "Impact",
				"Copperplate Gothic Std", "Cooper Std", "Bauhaus Std");

		private final List<String> families;

		private Family(String... families) {
			this.families = Arrays.asList(families);
		}

		/**
		 * @return A list of system specific font families to be tried for this
		 *         postscript font family alias
		 */
		public List<String> getFamilies() {
			return families;
		}
	}
}