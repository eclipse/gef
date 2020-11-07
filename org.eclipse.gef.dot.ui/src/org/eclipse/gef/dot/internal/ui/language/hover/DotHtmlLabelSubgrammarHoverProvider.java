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
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import java.util.Locale;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorInfo;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorUtil;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

public class DotHtmlLabelSubgrammarHoverProvider
		extends DefaultEObjectHoverProvider {
	private Attribute containingAttribute;

	@Override
	protected String getHoverInfoAsHtml(EObject o) {
		if (o instanceof HtmlAttr) {
			HtmlAttr attribute = (HtmlAttr) o;
			switch (attribute.getName().toLowerCase(Locale.ENGLISH)) {
			case "bgcolor": //$NON-NLS-1$
			case "color": //$NON-NLS-1$
				String quoted = attribute.getValue();
				if (quoted != null && quoted.length() > 2) {
					return colorDescription(containingAttribute, quoted,
							quoted.substring(1, quoted.length() - 1));
				}
			}
		}
		return super.getHoverInfoAsHtml(o);
	}

	public void setContainingAttribute(Attribute containingAttribute) {
		this.containingAttribute = containingAttribute;
	}

	private String colorDescription(Attribute attribute, String encoded,
			String value) {
		DotColorUtil colorUtil = new DotColorUtil();
		Color color = colorUtil.parseColorAttributeValue(encoded);

		DotColorInfo colorInfo = new DotColorInfo(attribute, color);
		colorInfo.calculate(value);

		return DotColors.getColorDescription(colorInfo.getColorScheme(),
				colorInfo.getColorName(), colorInfo.getColorCode());
	}
}
