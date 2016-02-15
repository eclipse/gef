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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlAttribute;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlTag;
import org.eclipse.gef4.dot.internal.parser.dot.HtmlValue;

public class HtmlToText {

	public static String convertHtmlValueToString(HtmlValue htmlValue) {
		StringBuilder sb = new StringBuilder();
		// add pre text
		if (htmlValue.getPre() != null) {
			EList<String> text = htmlValue.getPre().getText();
			if (text != null) {
				sb.append(stringJoin(" ", text));
			}
		}
		// add tags and post text
		int tagSize = htmlValue.getTag().size();
		int postSize = htmlValue.getPost().size();
		int maxSize = Math.max(tagSize, postSize);
		for (int i = 0; i < maxSize; i++) {
			// convert tag to html
			if (i < tagSize) {
				sb.append(convertHtmlTagToString(htmlValue.getTag().get(i)));
			}
			// add post text
			if (i < postSize) {
				EList<String> text = htmlValue.getPost().get(i).getText();
				if (text != null) {
					sb.append(stringJoin(" ", text));
				}
			}
		}
		return sb.toString();
	}

	public static String convertHtmlTagToString(HtmlTag tag) {
		// opening tag
		StringBuilder sb = new StringBuilder(
				(tag.getPreOpenWs() == null || tag.getPreOpenWs().length() == 0
						? "" : " ") + "<" + tag.getName());
		// attributes
		if (!tag.getAttributes().isEmpty()) {
			for (HtmlAttribute attr : tag.getAttributes()) {
				sb.append(
						" " + attr.getName() + "=\"" + attr.getValue() + "\"");
			}
		}
		// self-closing?
		if (tag.isSelfClosing()) {
			sb.append("/>" + (tag.getPostOpenWs() == null
					|| tag.getPostOpenWs().length() == 0 ? "" : " "));
		} else {
			// close the opening tag
			sb.append(">" + (tag.getPostOpenWs() == null
					|| tag.getPostOpenWs().length() == 0 ? "" : " "));
			// add pre text
			if (tag.getPre() != null) {
				EList<String> text = tag.getPre().getText();
				if (text != null) {
					sb.append(stringJoin(" ", text));
				}
			}
			// children and post text
			int tagSize = tag.getChildren().size();
			int postSize = tag.getPost().size();
			int max = Math.max(tagSize, postSize);
			for (int i = 0; i < max; i++) {
				// add child
				if (i < tagSize) {
					sb.append(convertHtmlTagToString(tag.getChildren().get(i)));
				}
				// add post text
				if (i < postSize) {
					EList<String> text = tag.getPost().get(i).getText();
					if (text != null) {
						sb.append(stringJoin(" ", text));
					}
				}
			}
			// closing tag
			sb.append(
					(tag.getPreCloseWs() == null
							|| tag.getPreCloseWs().length() == 0 ? "" : " ")
							+ "</" + tag.getName() + ">"
							+ (tag.getPostCloseWs() == null
									|| tag.getPostCloseWs().length() == 0 ? ""
											: " "));
		}
		return sb.toString();
	}

	private static String stringJoin(String delimiter, List<String> text) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = text.iterator();
		while (it.hasNext()) {
			String t = it.next();
			sb.append(t);
			if (it.hasNext()) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

}
