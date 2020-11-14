/*******************************************************************************
 * Copyright (c) 2014, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - improve support for html-label highlighting
 *     Zoey Prigge     (itemis AG) - arrowTypes subgrammar for deprecation highlighting (bug #552993)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Port;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

public class DotSemanticHighlightingCalculator
		extends DefaultSemanticHighlightingCalculator {

	@Override
	public void doProvideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {

		// It gets a node model.
		INode root = resource.getParseResult().getRootNode();

		String dotText = root.getText();

		for (INode node : root.getAsTreeIterable()) {
			EObject grammarElement = node.getGrammarElement();
			if (grammarElement instanceof RuleCall) {
				RuleCall rc = (RuleCall) grammarElement;
				AbstractRule r = rc.getRule();
				EObject c = grammarElement.eContainer();

				// handle ID elements specifically
				if (r.getName().equals("ID")) { //$NON-NLS-1$
					EObject semanticElement = node.getSemanticElement();
					switch (((Assignment) c).getFeature()) {
					case "name": //$NON-NLS-1$
						if (semanticElement instanceof DotGraph) {
							acceptor.addPosition(node.getOffset(),
									node.getLength(),
									DotHighlightingConfiguration.GRAPH_NAME_ID);
						} else if (semanticElement instanceof NodeStmt
								|| semanticElement instanceof NodeId) {
							acceptor.addPosition(node.getOffset(),
									node.getLength(),
									DotHighlightingConfiguration.NODE_NAME_ID);
						} else if (semanticElement instanceof Attribute) {
							acceptor.addPosition(node.getOffset(),
									node.getLength(),
									DotHighlightingConfiguration.ATTRIBUTE_NAME_ID);
						} else if (semanticElement instanceof Port) {
							acceptor.addPosition(node.getOffset(),
									node.getLength(),
									DotHighlightingConfiguration.PORT_NAME_ID);
						}
						break;
					case "value": //$NON-NLS-1$
						if (semanticElement instanceof Attribute) {
							switch (((Attribute) semanticElement).getName()
									.toValue()) {
							case DotAttributes.ARROWHEAD__E:
							case DotAttributes.ARROWTAIL__E:
								provideHighlightingForArrowTypeString(node,
										acceptor);
								break;
							}
						}
						break;
					}
				}
				if (r.getName().equals("EdgeOp")) { //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.EDGE_OP_ID);
				}
				if (r.getName().equals("HTML_STRING")) { //$NON-NLS-1$
					provideHighlightingForHtmlString(node, dotText, acceptor);
				}
			}
		}
	}

	private void provideHighlightingForArrowTypeString(INode node,
			IHighlightedPositionAcceptor acceptor) {
		String arrowTypeString = node.getText();
		int offset = node.getOffset();
		String suffix = null;

		// quoted attribute value
		if (arrowTypeString.length() > 0 && arrowTypeString.charAt(0) == '"') {
			// trim the leading '"' and trailing '"' symbols
			arrowTypeString = arrowTypeString.substring(1,
					arrowTypeString.length() - 1);
			// increase offset correspondingly
			offset++;
			// adapt highlighting to quoted style
			suffix = DotHighlightingConfiguration.QUOTED_SUFFIX;
		}

		// delegate the highlighting of the the arrowType substring to the
		// corresponding sub-grammar highlighter
		DotSubgrammarHighlighter arrowTypeHighlighter = new DotSubgrammarHighlighter(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTARROWTYPE);
		arrowTypeHighlighter.provideHightlightingFor(arrowTypeString, offset,
				acceptor, suffix);
	}

	private void provideHighlightingForHtmlString(INode node, String dotText,
			IHighlightedPositionAcceptor acceptor) {
		// highlight the leading '<' symbol
		int openingSymbolOffset = node.getOffset();
		acceptor.addPosition(openingSymbolOffset, 1,
				DotHighlightingConfiguration.HTML_TAG);

		// highlight the trailing '>' symbol
		int closingSymbolOffset = node.getOffset() + node.getText().length()
				- 1;
		acceptor.addPosition(closingSymbolOffset, 1,
				DotHighlightingConfiguration.HTML_TAG);

		// trim the leading '<' and trailing '>' symbols
		String htmlText = node.getText();

		// highlight the html indentations
		List<TextLine> htmlTextLines = TextLines.splitString(htmlText);
		int htmlTextStartOffset = node.getOffset();
		int indentationLength = getIndentationLength(node, dotText);
		/*
		 * do not hightlight the leading '<' and trailing '>' symbols, skipping
		 * the first and the last line
		 */
		for (int i = 1; i < htmlTextLines.size() - 1; i++) {
			TextLine htmlTextLine = htmlTextLines.get(i);
			int offset = htmlTextStartOffset + htmlTextLine.getRelativeOffset()
					+ indentationLength;
			int length = htmlTextLine.getLeadingWhiteSpace().length()
					- indentationLength;
			acceptor.addPosition(offset, length,
					DotHighlightingConfiguration.HTML_INDENTATION);
		}

		// delegate the highlighting of the the html-label substring to the
		// corresponding sub-grammar highlighter
		String htmlString = htmlText.substring(1, htmlText.length() - 1);
		DotSubgrammarHighlighter htmlLabelHighlighter = new DotSubgrammarHighlighter(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);
		htmlLabelHighlighter.provideHightlightingFor(htmlString,
				node.getOffset() + 1, acceptor);
	}

	/**
	 * Get the indentation length of the line containing the leading '<' symbol.
	 */
	private int getIndentationLength(INode htmlTextNode, String dotText) {
		int htmlTextStartLine = htmlTextNode.getStartLine();
		List<TextLine> dotTextLines = TextLines.splitString(dotText);
		TextLine lineContainingTheLeadingAngleSymbol = dotTextLines
				.get(htmlTextStartLine - 1);
		return lineContainingTheLeadingAngleSymbol.getLeadingWhiteSpace()
				.length();
	}
}
