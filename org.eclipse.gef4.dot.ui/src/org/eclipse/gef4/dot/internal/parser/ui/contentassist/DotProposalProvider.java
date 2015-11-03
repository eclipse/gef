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
package org.eclipse.gef4.dot.internal.parser.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.services.DotGrammarAccess;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.google.inject.Inject;

/**
 * A proposal provider for Dot.
 * 
 * @author anyssen
 */
public class DotProposalProvider extends AbstractDotProposalProvider {

	@Inject
	DotGrammarAccess dotGrammarAccess;

	@Override
	public void completePort_Compass_pt(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		for (Keyword k : EcoreUtil2.getAllContentsOfType(
				dotGrammarAccess.getCOMPASS_PTRule().getAlternatives(),
				Keyword.class)) {
			acceptor.accept(createCompletionProposal(k.getValue(), context));
		}
		super.completePort_Compass_pt(model, assignment, context, acceptor);
	}

	@Override
	public void completeAttribute_Value(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof Attribute) {
			Attribute attribute = (Attribute) model;
			if (DotJavaValidator.isEdgeAttribute(attribute)
					&& DotProperties.EDGE_STYLE.equals(attribute.getName())) {
				for (String edgeStyle : DotProperties.EDGE_STYLE_VALUES) {
					// quote attribute value if needed only
					final String proposedValue = DotTerminalConverters
							.needsToBeQuoted(edgeStyle)
									? DotTerminalConverters.quote(edgeStyle)
									: edgeStyle;
					acceptor.accept(
							createCompletionProposal(proposedValue, context));
				}
			} else {
				super.completeAttribute_Value(model, assignment, context,
						acceptor);
			}
		} else {
			super.completeAttribute_Value(model, assignment, context, acceptor);
		}
	}

}
