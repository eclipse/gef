/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.codemining;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.codemining.AbstractXtextCodeMiningProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;

public class DotCodeMiningProvider extends AbstractXtextCodeMiningProvider {

	@Override
	protected void createCodeMinings(IDocument document, XtextResource resource,
			CancelIndicator indicator, IAcceptor<? super ICodeMining> acceptor)
			throws BadLocationException {

		EList<EObject> contents = resource.getContents();
		if (contents.isEmpty()) {
			return;
		}

		// get all graphs in the open document
		List<DotGraph> dotGraphs = EcoreUtil2.eAllOfType(contents.get(0),
				DotGraph.class);
		for (DotGraph dotGraph : dotGraphs) {
			int nodesCount = DotAstHelper.getNumberOfNodes(dotGraph);
			String nodesCountHeaderText = nodesCount + " node" //$NON-NLS-1$
					+ (nodesCount == 1 ? "" : "s"); //$NON-NLS-1$ //$NON-NLS-2$

			int edgesCount = DotAstHelper.getNumberOfEdges(dotGraph);
			String edgesCountHeaderText = edgesCount + " edge" //$NON-NLS-1$
					+ (edgesCount == 1 ? "" : "s"); //$NON-NLS-1$ //$NON-NLS-2$

			ICompositeNode node = NodeModelUtils.getNode(dotGraph);
			int beforeLineNumber = document.getLineOfOffset(node.getOffset());

			// create two line header code minings before the graph: one for
			// the nodes, one for the edges
			acceptor.accept(createNewLineHeaderCodeMining(beforeLineNumber,
					document, nodesCountHeaderText));
			acceptor.accept(createNewLineHeaderCodeMining(beforeLineNumber,
					document, edgesCountHeaderText));
		}
	}
}
