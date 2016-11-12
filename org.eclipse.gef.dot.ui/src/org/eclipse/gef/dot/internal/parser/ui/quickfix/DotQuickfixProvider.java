/*******************************************************************************
 * Copyright (c) 2010, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.parser.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef.dot.internal.parser.dot.Attribute;
import org.eclipse.gef.dot.internal.parser.style.EdgeStyle;
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

	@Fix(DotAttributes.STYLE__GNE)
	public void fixEdgeStyleAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		for (EdgeStyle edgeStyle : EdgeStyle.VALUES) {
			// quote values if needed, otherwise use plain attribute value
			// TODO: Use value converter for ID instead
			final String validValue = DotTerminalConverters
					.needsToBeQuoted(edgeStyle.toString())
							? DotTerminalConverters.quote(edgeStyle.toString())
							: edgeStyle.toString();
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
