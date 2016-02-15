/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #321775
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlAttribute;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlContent;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlTag;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlText;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlValue;

public class Html2Text {

	public static String convertHtmlValueToString(HtmlValue htmlValue) {
		StringBuilder sb = new StringBuilder();
		EList<EObject> contents = htmlValue.getContent().getContents();
		convertContentsToString(sb, contents, true);
		return sb.toString();
	}

	protected static void convertContentsToString(StringBuilder sb,
			EList<EObject> contents, boolean trim) {
		for (int i = 0; i < contents.size(); i++) {
			Object c = contents.get(i);
			if (c instanceof HtmlTag) {
				sb.append(convertHtmlTagToString((HtmlTag) c));
			} else if (c instanceof HtmlText) {
				EList<String> fragments = ((HtmlText) c).getFragments();
				for (int j = 0; j < fragments.size(); j++) {
					String fragment = fragments.get(j);
					if (fragment.matches("^\\s+$")) {
						boolean isFirst = i == 0 && j == 0;
						boolean isLast = i == contents.size() - 1
								&& j == fragments.size() - 1;
						if (!isFirst && !isLast || !trim) {
							sb.append(" ");
						}
					} else {
						sb.append(fragment);
					}
				}
			}
		}
	}

	public static String convertHtmlTagToString(HtmlTag tag) {
		// opening tag
		StringBuilder sb = new StringBuilder("<" + tag.getName());
		// attributes
		if (!tag.getAttributes().isEmpty()) {
			for (HtmlAttribute attr : tag.getAttributes()) {
				sb.append(" " + attr.getName() + "=" + attr.getValue());
			}
		}
		// self-closing?
		if (tag.isSelfClosing()) {
			sb.append("/>");
		} else {
			// close the opening tag
			sb.append(">");
			// children and text
			for (HtmlContent content : tag.getChildren()) {
				convertContentsToString(sb, content.getContents(), false);
			}
			// closing tag
			sb.append("</" + tag.getName() + ">");
		}
		return sb.toString();
	}

}
