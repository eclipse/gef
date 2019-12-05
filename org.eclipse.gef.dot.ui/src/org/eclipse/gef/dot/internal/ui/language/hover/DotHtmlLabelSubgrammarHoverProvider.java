/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import java.util.Locale;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
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
					return DotHoverUtils.colorDescription(containingAttribute,
							quoted, quoted.substring(1, quoted.length() - 1));
				}
			}
		}
		return super.getHoverInfoAsHtml(o);
	}

	public void setContainingAttribute(Attribute containingAttribute) {
		this.containingAttribute = containingAttribute;
	}
}
