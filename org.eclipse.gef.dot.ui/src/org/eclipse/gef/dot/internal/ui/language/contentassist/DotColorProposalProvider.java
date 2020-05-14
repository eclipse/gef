/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControl;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
public class DotColorProposalProvider extends
		org.eclipse.gef.dot.internal.ui.language.contentassist.AbstractDotColorProposalProvider {

	/**
	 * Represents the color scheme that is defined in the DOT ast. If this color
	 * scheme is not defined, the default color scheme should be used in the
	 * proposal provider.
	 */
	static String globalColorScheme = null;

	private final String defaultColorScheme = "x11"; //$NON-NLS-1$

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
		// start with the default color scheme
		String colorScheme = defaultColorScheme;

		if (model instanceof StringColor
				&& ((StringColor) model).getScheme() != null) {
			colorScheme = ((StringColor) model).getScheme();
		} else if (globalColorScheme != null) {
			colorScheme = globalColorScheme;
		}

		for (String colorName : DotColors
				.getColorNames(colorScheme.toLowerCase())) {
			ICompletionProposal completionProposal = createCompletionProposal(
					colorName, context);
			if (completionProposal instanceof ConfigurableCompletionProposal) {
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
				String colorCode = DotColors.get(colorScheme, colorName);
				// add color image to the proposal
				Image image = DotActivator.getInstance().getImageRegistry()
						.get(colorCode);
				configurableCompletionProposal.setImage(image);
				// add color description to the proposal
				String colorDescription = DotColors.getColorDescription(
						colorScheme.toLowerCase(), colorName, colorCode);
				configurableCompletionProposal
						.setAdditionalProposalInfo(colorDescription);
				acceptor.accept(configurableCompletionProposal);
			}
		}
	}

	/**
	 * This customization is needed to render the additional proposal
	 * information in html form properly.
	 */
	@Override
	protected ConfigurableCompletionProposal doCreateProposal(String proposal,
			StyledString displayString, Image image, int replacementOffset,
			int replacementLength) {
		return new ConfigurableCompletionProposal(proposal, replacementOffset,
				replacementLength, proposal.length(), image, displayString,
				null, null) {
			@Override
			public IInformationControlCreator getInformationControlCreator() {
				return new IInformationControlCreator() {

					@Override
					public IInformationControl createInformationControl(
							Shell parent) {
						/**
						 * These information has been taken from the
						 * org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider.HoverControlCreator
						 * class
						 */
						String font = "org.eclipse.jdt.ui.javadocfont"; //$NON-NLS-1$
						String tooltipAffordanceString = EditorsUI
								.getTooltipAffordanceString();
						return new XtextBrowserInformationControl(parent, font,
								tooltipAffordanceString);
					}
				};
			}
		};
	}
}
