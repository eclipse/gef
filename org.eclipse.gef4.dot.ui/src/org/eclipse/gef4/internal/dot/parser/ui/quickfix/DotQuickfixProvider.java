/*******************************************************************************
 * Copyright (c) 2010, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.internal.dot.parser.dot.Attribute;
import org.eclipse.gef4.internal.dot.parser.validation.DotJavaValidator;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

/**
 * A quick-fix provider for Dot.
 * 
 * @author anyssen
 */
public class DotQuickfixProvider extends DefaultQuickfixProvider {

	@Fix(DotJavaValidator.ATTRIBUTE__INVALID_VALUE__EDGE_STYLE)
	public void fixEdgeStyleAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		for (final String edgeStyle : DotProperties.EDGE_STYLE_VALUES) {
			acceptor.accept(issue,
					"Replace '" + issue.getData()[0] + "' with '" + edgeStyle //$NON-NLS-1$ //$NON-NLS-2$
							+ "'.", //$NON-NLS-1$
					"Use valid '" + edgeStyle + "' instead of invalid '" //$NON-NLS-1$ //$NON-NLS-2$
							+ issue.getData()[0] + "' edge style.", //$NON-NLS-1$
					null, new ISemanticModification() {

						@Override
						public void apply(EObject element,
								IModificationContext context) throws Exception {
							((Attribute) element).setValue(edgeStyle);
						}
					});
		}
	}

}
