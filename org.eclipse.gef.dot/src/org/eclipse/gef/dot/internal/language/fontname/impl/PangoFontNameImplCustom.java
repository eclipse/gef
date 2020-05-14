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
package org.eclipse.gef.dot.internal.language.fontname.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.gef.dot.internal.language.fontname.Gravity;
import org.eclipse.gef.dot.internal.language.fontname.GravityOption;
import org.eclipse.gef.dot.internal.language.fontname.Stretch;
import org.eclipse.gef.dot.internal.language.fontname.StretchOption;
import org.eclipse.gef.dot.internal.language.fontname.Style;
import org.eclipse.gef.dot.internal.language.fontname.StyleOption;
import org.eclipse.gef.dot.internal.language.fontname.StyleOptionsElement;
import org.eclipse.gef.dot.internal.language.fontname.Variant;
import org.eclipse.gef.dot.internal.language.fontname.VariantOption;
import org.eclipse.gef.dot.internal.language.fontname.Weight;
import org.eclipse.gef.dot.internal.language.fontname.WeightOption;

public class PangoFontNameImplCustom extends PangoFontNameImpl {
	public Weight getWeight() {
		for (StyleOptionsElement styleOption : getStyleOptionsList()) {
			if (styleOption instanceof WeightOption) {
				return ((WeightOption) styleOption).getWeight();
			}
		}
		return null;
	}

	public Style getStyle() {
		for (StyleOptionsElement styleOption : getStyleOptionsList()) {
			if (styleOption instanceof StyleOption) {
				return ((StyleOption) styleOption).getStyle();
			}
		}
		return null;
	}

	public Variant getVariant() {
		for (StyleOptionsElement styleOption : getStyleOptionsList()) {
			if (styleOption instanceof VariantOption) {
				return ((VariantOption) styleOption).getVariant();
			}
		}
		return null;
	}

	public Stretch getStretch() {
		for (StyleOptionsElement styleOption : getStyleOptionsList()) {
			if (styleOption instanceof StretchOption) {
				return ((StretchOption) styleOption).getStretch();
			}
		}
		return null;
	}

	public Gravity getGravity() {
		for (StyleOptionsElement styleOption : getStyleOptionsList()) {
			if (styleOption instanceof GravityOption) {
				return ((GravityOption) styleOption).getGravity();
			}
		}
		return null;
	}

	public List<String> getFontFamilies() {
		ArrayList<String> list = new ArrayList<>(getFamilies());
		if (getFinalFamily().size() > 0) {
			list.add(
					getFinalFamily().stream().collect(Collectors.joining(" ")));
		}
		return list;
	}
}
