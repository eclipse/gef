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

import java.util.Locale;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.nodemodel.INode;

public class DotFontNameConverters
		extends AbstractDeclarativeValueConverterService {

	/**
	 * A value converter for the PostScriptAlias datatype rule
	 *
	 * @return An IValueConverter<PostScriptFontAlias>
	 */
	@ValueConverter(rule = "PostScriptAlias")
	public IValueConverter<PostScriptFontAlias> postScriptFontAlias() {
		return new AbstractNullSafeConverter<PostScriptFontAlias>() {

			@Override
			protected String internalToString(PostScriptFontAlias value) {
				return value.toString();
			}

			@Override
			protected PostScriptFontAlias internalToValue(String string,
					INode node) throws ValueConverterException {
				String postscriptComparator = string.toUpperCase(Locale.ENGLISH)
						.replaceAll("-", "_");
				return PostScriptFontAlias.valueOf(postscriptComparator);
			}
		};
	}

	@ValueConverter(rule = "Style")
	public IValueConverter<Style> style() {
		return new AbstractNullSafeConverter<Style>() {

			@Override
			protected String internalToString(Style value) {
				return value.getLiteral();
			}

			@Override
			protected Style internalToValue(String string, INode node)
					throws ValueConverterException {
				switch (string.toLowerCase(Locale.ENGLISH)) {
				case "oblique":
					return Style.OBLIQUE;
				case "italic":
					return Style.ITALIC;
				case "roman":
				default:
					return Style.NORMAL;
				}
			}
		};
	}

	@ValueConverter(rule = "Variant")
	public IValueConverter<Variant> variant() {
		return new AbstractNullSafeConverter<Variant>() {

			@Override
			protected String internalToString(Variant value) {
				return value.getLiteral();
			}

			@Override
			protected Variant internalToValue(String string, INode node)
					throws ValueConverterException {
				switch (string.toLowerCase(Locale.ENGLISH)) {
				case "small-caps":
					return Variant.SMALL_CAPS;
				default:
					return Variant.NORMAL;
				}
			}
		};
	}

	@ValueConverter(rule = "Weight")
	public IValueConverter<Weight> weight() {
		return new AbstractNullSafeConverter<Weight>() {

			@Override
			protected String internalToString(Weight value) {
				return value.getLiteral();
			}

			@Override
			protected Weight internalToValue(String string, INode node)
					throws ValueConverterException {
				switch (string.toLowerCase(Locale.ENGLISH)) {
				case "thin":
					return Weight.THIN;
				case "ultra-light":
				case "extra-light":
					return Weight.ULTRALIGHT;
				case "light":
					return Weight.LIGHT;
				case "semi-light":
				case "demi-light":
					return Weight.SEMILIGHT;
				case "book":
					return Weight.BOOK;
				case "medium":
					return Weight.MEDIUM;
				case "semi-bold":
				case "demi-bold":
					return Weight.SEMIBOLD;
				case "bold":
					return Weight.BOLD;
				case "ultra-bold":
				case "extra-bold":
					return Weight.ULTRABOLD;
				case "heavy":
				case "black":
					return Weight.HEAVY;
				case "ultra-heavy":
				case "extra-heavy":
				case "ultra-black":
				case "extra-black":
					return Weight.ULTRAHEAVY;
				case "regular":
				default:
					return Weight.NORMAL;
				}
			}
		};
	}

	@ValueConverter(rule = "Stretch")
	public IValueConverter<Stretch> stretch() {
		return new AbstractNullSafeConverter<Stretch>() {

			@Override
			protected String internalToString(Stretch value) {
				return value.getLiteral();
			}

			@Override
			protected Stretch internalToValue(String string, INode node)
					throws ValueConverterException {
				switch (string.toLowerCase(Locale.ENGLISH)) {
				case "ultra-condensed":
					return Stretch.ULTRA_CONDENSED;
				case "extra-condensed":
					return Stretch.EXTRA_CONDENSED;
				case "condensed":
					return Stretch.CONDENSED;
				case "semi-condensed":
					return Stretch.SEMI_CONDENSED;
				case "semi-expanded":
					return Stretch.SEMI_EXPANDED;
				case "expanded":
					return Stretch.EXPANDED;
				case "extra-expanded":
					return Stretch.EXTRA_EXPANDED;
				case "ultra-expanded":
					return Stretch.ULTRA_EXPANDED;
				default:
					return Stretch.NORMAL;
				}
			}
		};
	}

	@ValueConverter(rule = "Gravity")
	public IValueConverter<Gravity> gravity() {
		return new AbstractNullSafeConverter<Gravity>() {

			@Override
			protected String internalToString(Gravity value) {
				return value.getLiteral();
			}

			@Override
			protected Gravity internalToValue(String string, INode node)
					throws ValueConverterException {
				switch (string.toLowerCase(Locale.ENGLISH)) {
				case "upside-down":
				case "north":
					return Gravity.NORTH;
				case "rotated-left":
				case "east":
					return Gravity.EAST;
				case "rotated-right":
				case "west":
					return Gravity.WEST;
				case "not-rotated":
				case "south":
				default:
					return Gravity.SOUTH;
				}
			}
		};
	}
}
