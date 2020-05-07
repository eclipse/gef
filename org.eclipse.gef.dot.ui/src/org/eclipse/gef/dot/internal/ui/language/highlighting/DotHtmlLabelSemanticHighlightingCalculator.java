/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

public class DotHtmlLabelSemanticHighlightingCalculator
		extends DefaultSemanticHighlightingCalculator {

	@Override
	public void doProvideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {

		// It gets a node model.
		INode root = resource.getParseResult().getRootNode();
		for (INode node : root.getAsTreeIterable()) {
			EObject grammarElement = node.getGrammarElement();
			if (grammarElement instanceof TerminalRule) {
				if ("HTML_COMMENT" //$NON-NLS-1$
						.equals(((TerminalRule) grammarElement).getName())) {
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.HTML_COMMENT);
				}
			}
			if (grammarElement instanceof RuleCall) {
				RuleCall rc = (RuleCall) grammarElement;
				AbstractRule r = rc.getRule();
				String ruleName = r.getName();
				switch (ruleName) {
				case "ATTR_VALUE": //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.HTML_ATTRIBUTE_VALUE);
					break;
				case "TAG_START_CLOSE": //$NON-NLS-1$
				case "TAG_START": //$NON-NLS-1$
				case "TAG_END": //$NON-NLS-1$
				case "TAG_END_CLOSE": //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.HTML_TAG);
					break;
				case "ASSIGN": //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.HTML_ATTRIBUTE_EQUAL_SIGN);
				default:
					EObject c = grammarElement.eContainer();
					if (c instanceof Assignment) {
						EObject semanticElement = node.getSemanticElement();
						String feature = ((Assignment) c).getFeature();
						if (r.getName().equals("ID")) { //$NON-NLS-1$
							if (semanticElement instanceof HtmlAttr
									&& "name".equals(feature)) { //$NON-NLS-1$
								acceptor.addPosition(node.getOffset(),
										node.getLength(),
										DotHighlightingConfiguration.HTML_ATTRIBUTE_NAME);
							} else if ("name".equals(feature) //$NON-NLS-1$
									|| "closeName".equals(feature)) { //$NON-NLS-1$
								acceptor.addPosition(node.getOffset(),
										node.getLength(),
										DotHighlightingConfiguration.HTML_TAG);
							}
						} else if (semanticElement instanceof HtmlContent
								&& "text".equals(feature)) { //$NON-NLS-1$
							acceptor.addPosition(node.getOffset(),
									node.getLength(),
									DotHighlightingConfiguration.HTML_CONTENT);
						}
					}
				}

			}
		}
	}
}
