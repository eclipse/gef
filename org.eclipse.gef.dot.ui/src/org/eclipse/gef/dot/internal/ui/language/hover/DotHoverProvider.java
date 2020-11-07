/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey Prigge    (itemis AG) - extract DotHoverUtils (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorInfo;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorUtil;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

public class DotHoverProvider extends DefaultEObjectHoverProvider {

	@Override
	protected String getHoverInfoAsHtml(EObject o) {
		if (o instanceof Attribute) {
			Attribute attribute = (Attribute) o;
			String attributeName = attribute.getName().toValue();
			ID attributeValue = attribute.getValue();
			if (attributeValue != null) {
				switch (attributeName) {
				case DotAttributes.BGCOLOR__GC:
				case DotAttributes.COLOR__CNE:
				case DotAttributes.FILLCOLOR__CNE:
				case DotAttributes.FONTCOLOR__GCNE:
				case DotAttributes.LABELFONTCOLOR__E:
					DotColorUtil colorUtil = new DotColorUtil();
					DotColorInfo colorInfo = colorUtil.getColorInfo(attribute);
					return DotColors.getColorDescription(
							colorInfo.getColorScheme(),
							colorInfo.getColorName(), colorInfo.getColorCode());
				default:
					break;
				}
			}
		}

		return super.getHoverInfoAsHtml(o);
	}

}
