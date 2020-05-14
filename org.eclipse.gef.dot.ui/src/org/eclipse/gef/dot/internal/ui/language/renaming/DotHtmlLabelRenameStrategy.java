/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.renaming;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.refactoring.IRefactoringUpdateAcceptor;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameStrategy;
import org.eclipse.xtext.ui.refactoring.ui.IRenameElementContext;

public class DotHtmlLabelRenameStrategy extends DefaultRenameStrategy {

	private EObject targetElement;

	@Override
	public boolean initialize(EObject targetElement,
			IRenameElementContext context) {
		if (super.initialize(targetElement, context)) {
			this.targetElement = targetElement;
			return true;
		}
		return false;
	}

	@Override
	protected String getNameAsText(String nameAsValue, String nameRuleName) {
		return nameAsValue;
	}

	@Override
	public void createDeclarationUpdates(String newName,
			ResourceSet resourceSet,
			IRefactoringUpdateAcceptor updateAcceptor) {
		super.createDeclarationUpdates(newName, resourceSet, updateAcceptor);

		// perform renaming of the dependent element
		URI resourceURI = getTargetElementOriginalURI().trimFragment();

		if (targetElement instanceof HtmlTag) {
			HtmlTag htmlTag = (HtmlTag) targetElement;
			if (!htmlTag.isSelfClosing()) {
				List<INode> nodes = NodeModelUtils.findNodesForFeature(htmlTag,
						HtmllabelPackage.Literals.HTML_TAG__CLOSE_NAME);
				if (nodes.size() == 1) {
					INode node = nodes.get(0);
					TextEdit referenceEdit = new ReplaceEdit(node.getOffset(),
							node.getLength(), newName);
					updateAcceptor.accept(resourceURI, referenceEdit);
				} else {
					System.err.println(
							"Exact 1 node is expected for the name of the html closing tag, but got " //$NON-NLS-1$
									+ nodes.size());
				}

			}
		}
	}

}
