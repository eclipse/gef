/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
	AVANTGARDE_BOOK("AvantGarde-Book", Family.AVANTGARDE_FAMILY, Weight.BOOK,
			null, null),
	/**
	 * The AvantGarde-BookOblique fontname
	 */
	AVANTGARDE_BOOKOBLIQUE("AvantGarde-BookOblique", Family.AVANTGARDE_FAMILY,
			Weight.BOOK, Style.OBLIQUE, null),
	/**
	 * The AvantGarde-Demi fontname
	 */
	AVANTGARDE_DEMI("AvantGarde-Demi", Family.AVANTGARDE_FAMILY,
			Weight.SEMILIGHT /* DEMI */, null, null),
	/**
	 * The AvantGarde-DemiOblique fontname
	 */
	AVANTGARDE_DEMIOBLIQUE("AvantGarde-DemiOblique", Family.AVANTGARDE_FAMILY,
			Weight.SEMILIGHT /* DEMI */, Style.OBLIQUE, null),
	/**
	 * The Bookman-Demi fontname
	 */
	BOOKMAN_DEMI("Bookman-Demi", Family.BOOKMAN_FAMILY,
			Weight.SEMILIGHT /* DEMI */, null, null),
	/**
	 * The Bookman-DemiItalic fontname
	 */
	BOOKMAN_DEMIITALIC("Bookman-DemiItalic", Family.BOOKMAN_FAMILY,
			Weight.SEMILIGHT /* DEMI */, Style.ITALIC, null),
	/**
	 * The Bookman-Light fontname
	 */
	BOOKMAN_LIGHT("Bookman-Light", Family.BOOKMAN_FAMILY, Weight.LIGHT, null,
			null),
	/**
	 * The Bookman-LightItalic fontname
	 */
	BOOKMAN_LIGHTITALIC("Bookman-LightItalic", Family.BOOKMAN_FAMILY,
			Weight.LIGHT, Style.ITALIC, null),
	/**
	 * The Courier fontname
	 */
	COURIER("Courier", Family.COURIER_FAMILY, null, null, null),
	/**
	 * The Courier-Bold fontname
	 */
	COURIER_BOLD("Courier-Bold", Family.COURIER_FAMILY, Weight.BOLD, null,
			null),
	/**
	 * The Courier-BoldOblique fontname
	 */
	COURIER_BOLDOBLIQUE("Courier-BoldOblique", Family.COURIER_FAMILY,
			Weight.BOLD, Style.OBLIQUE, null),
	/**
	 * The Courier-Oblique fontname
	 */
	COURIER_OBLIQUE("Courier-Oblique", Family.COURIER_FAMILY, null,
			Style.OBLIQUE, null),
	/**
	 * The Helvetica fontname
	 */
	HELVETICA("Helvetica", Family.HELVETICA_FAMILY, null, null, null),
	/**
	 * The Helvetica-Bold fontname
	 */
	HELVETICA_BOLD("Helvetica-Bold", Family.HELVETICA_FAMILY, Weight.BOLD, null,
			null),
	/**
	 * The Helvetica-BoldOblique fontname
	 */
	HELVETICA_BOLDOBLIQUE("Helvetica-BoldOblique", Family.HELVETICA_FAMILY,
			Weight.BOLD, Style.OBLIQUE, null),
	/**
	 * The Helvetica-Narrow fontname
	 */
	HELVETICA_NARROW("Helvetica-Narrow", Family.HELVETICA_FAMILY, null, null,
			Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-Bold fontname
	 */
	HELVETICA_NARROW_BOLD("Helvetica-Narrow-Bold", Family.HELVETICA_FAMILY,
			Weight.BOLD, null, Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-BoldOblique fontname
	 */
	HELVETICA_NARROW_BOLDOBLIQUE("Helvetica-Narrow-BoldOblique",
			Family.HELVETICA_FAMILY, Weight.BOLD, Style.OBLIQUE,
			Stretch.CONDENSED),
	/**
	 * The Helvetica-Narrow-Oblique fontname
	 */
	HELVETICA_NARROW_OBLIQUE("Helvetica-Narrow-Oblique",
			Family.HELVETICA_FAMILY, null, Style.OBLIQUE, Stretch.CONDENSED),
	/**
	 * The Helvetica-Oblique fontname
	 */
	HELVETICA_OBLIQUE("Helvetica-Oblique", Family.HELVETICA_FAMILY, null,
			Style.OBLIQUE, null),
	/**
	 * The NewCenturySchoolbk-Bold fontname
	 */
	NEWCENTURYSCHLBK_BOLD("NewCenturySchlbk-Bold",
			Family.NEWCENTURYSCHLBK_FAMILY, Weight.BOLD, null, null),
	/**
	 * The NewCenturySchoolbk-BoldItalic fontname
	 */
	NEWCENTURYSCHLBK_BOLDITALIC("NewCenturySchlbk-BoldItalic",
			Family.NEWCENTURYSCHLBK_FAMILY, Weight.BOLD, Style.ITALIC, null),
	/**
	 * The NewCenturySchoolbk-Italic fontname
	 */
	NEWCENTURYSCHLBK_ITALIC("NewCenturySchlbk-Italic",
			Family.NEWCENTURYSCHLBK_FAMILY, null, Style.ITALIC, null),
	/**
	 * The NewCenturySchoolbk-Roman fontname
	 */
	NEWCENTURYSCHLBK_ROMAN("NewCenturySchlbk-Roman",
			Family.NEWCENTURYSCHLBK_FAMILY, Weight.NORMAL /* ROMAN */, null,
			null),
	/**
	 * The Palatino-Bold fontname
	 */
	PALATINO_BOLD("Palatino-Bold", Family.PALATINO_FAMILY, Weight.BOLD, null,
			null),
	/**
	 * The Palatino-BoldItalic fontname
	 */
	PALATINO_BOLDITALIC("Palatino-BoldItalic", Family.PALATINO_FAMILY,
			Weight.BOLD, Style.ITALIC, null),
	/**
	 * The Palatino-Italic fontname
	 */
	PALATINO_ITALIC("Palatino-Italic", Family.PALATINO_FAMILY, null,
			Style.ITALIC, null),
	/**
	 * The Palatino-Roman fontname
	 */
	PALATINO_ROMAN("Palatino-Roman", Family.PALATINO_FAMILY,
			Weight.NORMAL /* ROMAN */, null, null),
	/**
	 * The Symbol fontname
	 */
	SYMBOL("Symbol", Family.SYMBOL_FAMILY, null, null, null),
	/**
	 * The Times-Bold fontname
	 */
	TIMES_BOLD("Times-Bold", Family.TIMES_FAMILY, Weight.BOLD, null, null),
	/**
	 * The Times-BoldItalic fontname
	 */
	TIMES_BOLDITALIC("Times-BoldItalic", Family.TIMES_FAMILY, Weight.BOLD,
			Style.ITALIC, null),
	/**
	 * The Times-Italic fontname
	 */
	TIMES_ITALIC("Times-Italic", Family.TIMES_FAMILY, null, Style.ITALIC, null),
	/**
	 * The Times-Roman fontname
	 */
	TIMES_ROMAN("Times-Roman", Family.TIMES_FAMILY, null, null, null),
	/**
	 * The ZapfChancery-MediumItalic fontname
	 */
	ZAPFCHANCERY_MEDIUMITALIC("ZapfChancery-MediumItalic",
			Family.CHANCERY_FAMILY, Weight.MEDIUM, Style.ITALIC, null),
	/**
	 * The ZapfDingbats fontname
	 */
	ZAPFDINGBATS("ZapfDingbats", Family.DINGBATS_FAMILY, null, null, null);

	private final String literal;
	private final Family family;
	private final Weight weight;
	private final Style style;
	private final Stretch stretch;

	private PostScriptFontAlias(String literal, Family family, Weight weight,
			Style style, Stretch stretch) {
		this.literal = literal;
		this.family = family;
		this.weight = weight;
		this.style = style;
		this.stretch = stretch;
	}

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
	 * @return The literal PostScriptFontAlias string representation
	 */
	@Override
	public String toString() {
		return literal;
	}

	/**
	 * An enum representing the supported postscript font families
	 */
	public static enum Family {
		/**
		 * The AvantGarde postscript font family
		 */
		AVANTGARDE_FAMILY("AvantGarde", "URW Gothic L", "Charcoal",
				"Nimbus Sans L", "Verdana", "Helvetica", "Bitstream Vera Sans",
				"DejaVu Sans", "Liberation Sans", "Luxi Sans", "FreeSans",
				"sans"),
		/**
		 * The Bookman postscript font family
		 */
		BOOKMAN_FAMILY("Bookman", "URW Bookman L", "Times New Roman", "Times",
				"Nimbus Roman No9 L", "Bitstream Vera Serif", "DejaVu Serif",
				"Liberation Serif", "Luxi Serif", "FreeSerif", "serif"),
		/**
		 * The Courier postscript font family
		 */
		COURIER_FAMILY("Courier", "Nimbus Mono L", "Inconsolata", "Courier New",
				"Bitstream Vera Sans Mono", "DejaVu Sans Mono",
				"Liberation Mono", "Luxi Mono", "FreeMono", "monospace"),
		/**
		 * The Helvetica postscript font family
		 */
		HELVETICA_FAMILY("Helvetica", "Nimbus Sans L", "Arial", "Verdana",
				"Bitstream Vera Sans", "DejaVu Sans", "Liberation Sans",
				"Luxi Sans", "FreeSans", "sans"),
		/**
		 * The NewCenturySchoolbook postscript font family
		 */
		NEWCENTURYSCHLBK_FAMILY("NewCenturySchlbk", "URW Bookman L",
				"Times New Roman", "Times", "Georgia", "Bitstream Vera Serif",
				"DejaVu Serif", "Liberation Serif", "Luxi Serif", "FreeSerif",
				"serif"),
		/**
		 * The Palatino postscript font family
		 */
		PALATINO_FAMILY("Palatino", "Times New Roman", "Times",
				"Nimbus Roman No9 L", "Norasi", "Rekha", "Bitstream Vera Serif",
				"DejaVu Serif", "Liberation Serif", "Luxi Serif", "FreeSerif",
				"serif"),
		/**
		 * The Symbol postscript font family
		 */
		SYMBOL_FAMILY("Symbol", "Impact", "Copperplate Gothic Std",
				"Cooper Std", "Bauhaus Std", "fantasy"),
		/**
		 * The Times postscript font family
		 */
		TIMES_FAMILY("Times", "Nimbus Roman No9 L", "Times New Roman",
				"Charcoal", "Bitstream Vera Serif", "DejaVu Serif",
				"Liberation Serif", "Luxi Serif", "FreeSerif", "serif"),

		/**
		 * The ZapfChancery postscript font family
		 */
		CHANCERY_FAMILY("ZapfChancery", "URW Chancery L", "Charcoal",
				"Times New Roman", "Times", "Nimbus Roman No9 L",
				"Bitstream Vera Serif", "DejaVu Serif", "Liberation Serif",
				"Luxi Serif", "FreeSerif", "serif"),
		/**
		 * The ZapfDingbats postscript font family
		 */
		DINGBATS_FAMILY("ZapfDingbats", "Dingbats", "Impact",
				"Copperplate Gothic Std", "Cooper Std", "Bauhaus Std",
				"fantasy");

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