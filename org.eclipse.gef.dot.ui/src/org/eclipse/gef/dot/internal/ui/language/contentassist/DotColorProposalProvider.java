/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
public class DotColorProposalProvider extends
		org.eclipse.gef.dot.internal.ui.language.contentassist.AbstractDotColorProposalProvider {

	@Override
	public void completeStringColor_Scheme(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		super.completeStringColor_Scheme(model, assignment, context, acceptor);

		for (String colorScheme : DotColors.getColorSchemes()) {
			acceptor.accept(createCompletionProposal(colorScheme, context));
		}
	}

	@Override
	public void completeStringColor_Name(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		super.completeStringColor_Name(model, assignment, context, acceptor);
		if (model instanceof StringColor) {
			StringColor stringColor = (StringColor) model;
			String colorScheme = stringColor.getScheme();
			if (colorScheme != null) {
				for (String colorName : DotColors.getColorNames(colorScheme)) {
					acceptor.accept(
							createCompletionProposal(colorName, context));
				}
			}
		}
	}

}
