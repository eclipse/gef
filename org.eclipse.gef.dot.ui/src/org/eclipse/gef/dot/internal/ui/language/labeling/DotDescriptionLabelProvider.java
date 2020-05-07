/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - Add 'Find References' support (bug #531049)
 ********************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhs;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.xtext.resource.IReferenceDescription;

/**
 * Provides labels for IEObjectDescriptions and IResourceDescriptions.
 *
 * Used e.g. as a label provider displaying the 'Find References' Search Result.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider
 */
public class DotDescriptionLabelProvider
		extends org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider {

	@Override
	public Object image(IReferenceDescription referenceDescription) {
		String result = ""; //$NON-NLS-1$
		EObject eObject = getEObject(referenceDescription);

		if (eObject instanceof NodeId) {
			EObject container = eObject.eContainer();
			if (container instanceof NodeStmt) {
				// the node is a node statement
				result = "node.png"; //$NON-NLS-1$
			}
			if (container instanceof EdgeStmtNode
					|| container instanceof EdgeRhs) {
				// the node is part of an edge
				result = "edge.png"; //$NON-NLS-1$
			}
		}

		return !result.isEmpty() ? result : super.image(referenceDescription);
	}

	@Override
	public Object text(IReferenceDescription referenceDescription) {
		StringBuilder result = new StringBuilder();

		EObject eObject = getEObject(referenceDescription);
		if (eObject instanceof NodeId) {
			NodeId node1 = (NodeId) eObject;
			EObject container = eObject.eContainer();
			if (container instanceof NodeStmt) {
				// the node is a node statement
				result.append(node1.getName().toString());
				result.append(": Node"); //$NON-NLS-1$
			}
			EdgeStmtNode firstNode = null;
			if (container instanceof EdgeStmtNode) {
				// the node is located on the left side of the edge
				firstNode = (EdgeStmtNode) container;
			}
			if (container instanceof EdgeRhs) {
				// if the node is located on the right side of the edge
				firstNode = (EdgeStmtNode) container.eContainer();
			}
			if (firstNode != null) {
				result.append(firstNode.getNode().getName().toString());
				for (EdgeRhs edgeRhs : firstNode.getEdgeRHS()) {
					if (edgeRhs instanceof EdgeRhsNode) {
						result.append(" "); //$NON-NLS-1$
						result.append(edgeRhs.getOp());
						result.append(" "); //$NON-NLS-1$
						NodeId node = ((EdgeRhsNode) edgeRhs).getNode();
						result.append(node.getName().toString());
					}
				}
				result.append(": Edge"); //$NON-NLS-1$
			}
		}

		String resultText = result.toString();

		return !resultText.isEmpty() ? DotEditorUtils.style(resultText)
				: super.text(referenceDescription);
	}

	private EObject getEObject(IReferenceDescription referenceDescription) {
		URI sourceUri = referenceDescription.getSourceEObjectUri();
		EObject eObject = new ResourceSetImpl().getEObject(sourceUri, true);
		return eObject;
	}
}
