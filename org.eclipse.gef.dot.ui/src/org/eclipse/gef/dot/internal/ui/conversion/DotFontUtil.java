/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #541056)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import java.io.StringReader;
import java.util.Locale;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.fontname.FontName;
import org.eclipse.gef.dot.internal.language.fontname.Weight;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.xtext.parser.IParser;

public class DotFontUtil {
	private SystemFontAccess access = new JavafxFontAccess();

	public String cssLocalFontFamily(FontName name) {
		return findLocalFamily(name).getFamily();
	}

	private Font findLocalFamily(FontName dotFont) {
		Font font = access.getDefault();
		for (String alternative : dotFont.getFontFamilies()) {
			font = access.font(alternative);
			if (!isDefaultFont(font)) {
				break;
			}
		}
		return font;
	}

	private boolean isDefaultFont(Font font) {
		return font == null || font.equals(access.getDefault());
	}

	public String cssWeight(FontName dotFont) {
		Weight weight = dotFont.getWeight();
		return Integer.toString(intWeight(weight));
	}

	private int intWeight(Weight weight) {
		if (weight == null) {
			weight = Weight.NORMAL;
		}
		switch (weight) {
		case THIN:
			return 100;
		case ULTRALIGHT:
			return 200;
		case LIGHT:
			return 300;
		case SEMILIGHT:
			// JavaFX does not support 350
			return 300;
		case BOOK:
			// JavaFX does not support 380
			return 400;
		case MEDIUM:
			return 500;
		case SEMIBOLD:
			return 600;
		case BOLD:
			return 700;
		case ULTRABOLD:
			return 800;
		case HEAVY:
		case ULTRAHEAVY: // normally 1000, not supported by javafx
			return 900;
		case NORMAL:
		default:
			return 400;

		}
	}

	public String cssStyle(FontName dotFont) {
		return dotFont.getStyle() != null
				? dotFont.getStyle().getName().toLowerCase(Locale.ENGLISH)
				: "normal"; //$NON-NLS-1$
	}

	public FontName parseHtmlFontFace(String face) {
		if (face == null) {
			return null;
		}
		IParser parser = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTFONTNAME)
				.getInstance(IParser.class);
		EObject rootNode = parser.parse(new StringReader(face))
				.getRootASTElement();
		if (rootNode instanceof FontName) {
			return (FontName) rootNode;
		}
		return null;
	}

	public interface SystemFontAccess {
		public Font getDefault();

		public Font font(String family);
	}

	public interface Font {
		public String getFamily();

		public boolean equals(Font font);
	}

	final class JavafxFontAccess implements SystemFontAccess {
		final class JavaFxFont implements Font {
			private final javafx.scene.text.Font fxFont;

			JavaFxFont(javafx.scene.text.Font fxFont) {
				this.fxFont = fxFont;
			}

			@Override
			public String getFamily() {
				return fxFont.getFamily();
			}

			@Override
			public boolean equals(Font font) {
				return font instanceof JavaFxFont
						&& fxFont.equals(((JavaFxFont) font).fxFont);
			}
		}

		@Override
		public Font getDefault() {
			return new JavaFxFont(javafx.scene.text.Font.getDefault());
		}

		@Override
		public Font font(String family) {
			return new JavaFxFont(javafx.scene.text.Font.font(family));
		}
	}

	public void setSystemFontAccess(SystemFontAccess access) {
		this.access = access;
	}
}
