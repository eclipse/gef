/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.validation.DotHtmlLabelValidator;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

/**
 * Custom quickfixes.
 *
 * see http://www.eclipse.org/Xtext/documentation.html#quickfixes
 */
public class DotHtmlLabelQuickfixProvider
		extends org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider {

	@Fix(DotHtmlLabelValidator.HTML_TAG_IS_NOT_PROPERLY_CLOSED)
	public void fixInvalidTagName(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		String[] issueData = issue.getData();
		String openingTagName = issueData[0];
		String closingTagName = issueData[1];

		// change the opening tag name
		String label = "Change the opening tag to '" + closingTagName + "'."; //$NON-NLS-1$ //$NON-NLS-2$
		String description = "Change the opening tag from '" + openingTagName //$NON-NLS-1$
				+ "' to '" + closingTagName + "'."; //$NON-NLS-1$ //$NON-NLS-2$
		acceptor.accept(issue, label, description, null,
				new ISemanticModification() {

					@Override
					public void apply(EObject element,
							IModificationContext context) throws Exception {
						if (element instanceof HtmlTag) {
							HtmlTag htmlTag = (HtmlTag) element;
							htmlTag.setName(closingTagName);
						}
					}
				});

		// change the closing tag name
		label = "Change the closing tag to '" + openingTagName + "'."; //$NON-NLS-1$ //$NON-NLS-2$
		description = "Change the closing tag from '" + closingTagName //$NON-NLS-1$
				+ "' to '" + openingTagName + "'."; //$NON-NLS-1$ //$NON-NLS-2$
		acceptor.accept(issue, label, description, null,
				new ISemanticModification() {

					@Override
					public void apply(EObject element,
							IModificationContext context) throws Exception {
						if (element instanceof HtmlTag) {
							HtmlTag htmlTag = (HtmlTag) element;
							htmlTag.setCloseName(openingTagName);
						}
					}
				});
	}
}
