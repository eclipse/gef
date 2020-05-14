/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Prigge     (itemis AG) - initial API and implementation (bug #552993)
 *     Tamas Miklossy  (itemis AG) - original code in DotHtmlLabelSemanticHighlightingCalculator
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

public class DotArrowTypeSemanticHighlightingCalculator
		extends DefaultSemanticHighlightingCalculator {

	@Override
	public void doProvideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {

		// It gets a node model.
		INode root = resource.getParseResult().getRootNode();
		for (INode node : root.getAsTreeIterable()) {
			EObject grammarElement = node.getGrammarElement();
			if (grammarElement instanceof RuleCall) {
				RuleCall rc = (RuleCall) grammarElement;
				AbstractRule r = rc.getRule();
				String ruleName = r.getName();
				switch (ruleName) {
				case "DeprecatedShape": //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.DEPRECATED_ATTRIBUTE_VALUE);
					break;
				}
			}
		}
	}
}
