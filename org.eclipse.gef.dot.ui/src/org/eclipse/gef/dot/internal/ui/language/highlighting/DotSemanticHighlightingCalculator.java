/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - minor refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Port;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;

public class DotSemanticHighlightingCalculator
		extends DefaultSemanticHighlightingCalculator {

	@Override
	public void doProvideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor) {

		// It gets a node model.
		INode root = resource.getParseResult().getRootNode();
		for (INode node : root.getAsTreeIterable()) {
			EObject grammarElement = node.getGrammarElement();
			if (grammarElement instanceof RuleCall) {
				RuleCall rc = (RuleCall) grammarElement;
				AbstractRule r = rc.getRule();
				EObject c = grammarElement.eContainer();

				// handle ID elements specifically
				if (r.getName().equals("ID") //$NON-NLS-1$
						&& ((Assignment) c).getFeature().equals("name")) { //$NON-NLS-1$
					if (node.getSemanticElement() instanceof DotGraph) {
						acceptor.addPosition(node.getOffset(), node.getLength(),
								DotHighlightingConfiguration.GRAPH_NAME_ID);
					} else if (node.getSemanticElement() instanceof NodeStmt
							|| node.getSemanticElement() instanceof NodeId) {
						acceptor.addPosition(node.getOffset(), node.getLength(),
								DotHighlightingConfiguration.NODE_NAME_ID);
					} else if (node.getSemanticElement() instanceof Attribute) {
						acceptor.addPosition(node.getOffset(), node.getLength(),
								DotHighlightingConfiguration.ATTRIBUTE_NAME_ID);
					} else if (node.getSemanticElement() instanceof Port) {
						acceptor.addPosition(node.getOffset(), node.getLength(),
								DotHighlightingConfiguration.PORT_NAME_ID);
					}
				}
				if (r.getName().equals("EdgeOp")) { //$NON-NLS-1$
					acceptor.addPosition(node.getOffset(), node.getLength(),
							DotHighlightingConfiguration.EDGE_OP_ID);
				}
			}
		}
	}
}
