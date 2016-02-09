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
package org.eclipse.gef4.dot.internal.parser.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.internal.DotProperties;
import org.eclipse.gef4.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator;
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
		for (String edgeStyle : DotProperties.EDGE_STYLE_VALUES) {
			// quote values if needed, otherwise use plain attribute value
			final String validValue = DotTerminalConverters.needsToBeQuoted(
					edgeStyle) ? DotTerminalConverters.quote(edgeStyle)
							: edgeStyle;
			acceptor.accept(issue,
					"Replace '" + issue.getData()[0] + "' with '" + validValue //$NON-NLS-1$ //$NON-NLS-2$
							+ "'.", //$NON-NLS-1$
					"Use valid '" + validValue + "' instead of invalid '" //$NON-NLS-1$ //$NON-NLS-2$
							+ issue.getData()[0] + "' edge style.", //$NON-NLS-1$
					null, new ISemanticModification() {

						@Override
						public void apply(EObject element,
								IModificationContext context) throws Exception {
							((Attribute) element).setValue(validValue);
						}
					});
		}
	}

}
