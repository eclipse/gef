/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey Prigge    (itemis AG) - extract DotHoverUtils (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
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
					return DotHoverUtils.colorDescription(attribute,
							attributeValue);
				default:
					break;
				}
			}
		}

		return super.getHoverInfoAsHtml(o);
	}

}
