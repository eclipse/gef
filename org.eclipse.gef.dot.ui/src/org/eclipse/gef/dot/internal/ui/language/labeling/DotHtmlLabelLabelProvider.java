/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 *
 * see http://www.eclipse.org/Xtext/documentation.html#labelProvider
 */
public class DotHtmlLabelLabelProvider
		extends org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider {

	@Inject
	public DotHtmlLabelLabelProvider(
			org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String image(HtmlTag htmlTag) {
		return "html_tag.png"; //$NON-NLS-1$
	}

	String image(HtmlAttr htmlAttr) {
		return "attribute.png"; //$NON-NLS-1$
	}

	String image(HtmlContent htmlContent) {
		return "html_text.png"; //$NON-NLS-1$
	}

	Object text(HtmlTag htmlTag) {
		String format = htmlTag.isSelfClosing() ? "<%s/>: Tag" //$NON-NLS-1$
				: "<%s>: Tag"; //$NON-NLS-1$
		return DotEditorUtils.style(format, htmlTag.getName());
	}

	Object text(HtmlAttr htmlAttr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		return DotEditorUtils.style(format, htmlAttr.getName(),
				htmlAttr.getValue());
	}

	Object text(HtmlContent htmlContent) {
		String format = "%s: Text"; //$NON-NLS-1$
		String text = htmlContent.getText() == null ? "" //$NON-NLS-1$
				: htmlContent.getText().trim();
		return DotEditorUtils.style(format, text);
	}
}
