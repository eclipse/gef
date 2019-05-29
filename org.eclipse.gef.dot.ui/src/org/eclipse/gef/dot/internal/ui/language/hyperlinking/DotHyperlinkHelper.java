/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hyperlinking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor;
import org.eclipse.xtext.util.ITextRegion;

public class DotHyperlinkHelper extends HyperlinkHelper {

	@Override
	public void createHyperlinksByOffset(XtextResource resource, int offset,
			IHyperlinkAcceptor acceptor) {

		EObject eObject = getEObjectAtOffsetHelper().resolveElementAt(resource,
				offset);
		if (eObject instanceof NodeId) {
			NodeId nodeId = (NodeId) eObject;
			IRegion hyperlinkRegion = getHyperlinkRegion(nodeId);
			EObject container = nodeId.eContainer();

			// if the node is either left or right part of an edge
			if (container instanceof EdgeStmtNode
					|| container instanceof EdgeRhsNode) {
				createHyperlinkToNodeDefinition(resource, hyperlinkRegion,
						nodeId, acceptor);
			}
		}
	}

	private void createHyperlinkToNodeDefinition(XtextResource resource,
			IRegion hyperlinkRegion, NodeId nodeId,
			IHyperlinkAcceptor acceptor) {

		NodeId targetSemanticObject = DotAstHelper.getNodeId(nodeId);
		if (targetSemanticObject != null) {
			createHyperlinksTo(resource, (Region) hyperlinkRegion,
					targetSemanticObject, acceptor);
		}
	}

	private IRegion getHyperlinkRegion(NodeId nodeId) {
		ITextRegion textRegion = NodeModelUtils.findActualNodeFor(nodeId)
				.getTextRegion();
		int offset = textRegion.getOffset();
		int length = textRegion.getLength();

		if (nodeId.getName().getType() == Type.QUOTED_STRING) {
			offset += 1;
			length -= 2;
		}
		return new Region(offset, length);
	}
}
