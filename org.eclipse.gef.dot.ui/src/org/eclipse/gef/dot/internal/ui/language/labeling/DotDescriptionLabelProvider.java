/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - Add 'Find References' support (bug #531049)
 ********************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhs;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides labels for IEObjectDescriptions and IResourceDescriptions.
 *
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 *
 * Used e.g. as a label provider displaying the 'Find References' Search Result.
 */
public class DotDescriptionLabelProvider
		extends org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider {

	@Inject
	private Provider<XtextResourceSet> resourceSetProvider;

	@Override
	public Object image(IEObjectDescription element) {
		String result = ""; //$NON-NLS-1$

		EObject eObject = getEObject(element);

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

		return !result.isEmpty() ? result : super.image(element);
	}

	@Override
	public Object text(IEObjectDescription element) {
		StringBuilder result = new StringBuilder();

		EObject eObject = getEObject(element);
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
				: super.text(element);
	}

	private EObject getEObject(IEObjectDescription element) {
		EObject eObject = element.getEObjectOrProxy();
		if (eObject.eIsProxy()) {
			XtextResourceSet resourceSet = resourceSetProvider.get();
			eObject = EcoreUtil.resolve(eObject, resourceSet);
		}
		return eObject;
	}
}
