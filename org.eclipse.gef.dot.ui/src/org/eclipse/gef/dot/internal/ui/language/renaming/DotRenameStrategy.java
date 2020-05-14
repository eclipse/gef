/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #530423)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.renaming;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.ui.refactoring.IRefactoringUpdateAcceptor;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameStrategy;
import org.eclipse.xtext.ui.refactoring.impl.RefactoringException;
import org.eclipse.xtext.ui.refactoring.ui.IRenameElementContext;
import org.eclipse.xtext.util.ITextRegion;

import com.google.inject.Inject;

/**
 * The default implementation returns an EAttribute with the name 'name' and
 * type 'String'. Since in the dot language the name attribute has type ID, it
 * requires some customization.
 */
public class DotRenameStrategy extends DefaultRenameStrategy {

	@Inject
	private ILocationInFileProvider locationInFileProvider;

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
	protected EAttribute getNameAttribute(EObject targetElement) {
		for (EAttribute attribute : targetElement.eClass().getEAttributes()) {
			if (attribute.getName().equals("name")) { //$NON-NLS-1$
				return attribute;
			}
		}

		return null;
	}

	@Override
	protected String getNameAsText(String nameAsValue, String nameRuleName) {
		return nameAsValue;
	}

	@Override
	protected EObject setName(URI targetElementURI, String newName,
			ResourceSet resourceSet) {
		EObject targetElement = resourceSet.getEObject(targetElementURI, false);
		if (targetElement == null) {
			throw new RefactoringException("Target element not loaded."); //$NON-NLS-1$
		}
		targetElement.eSet(getNameAttribute(), ID.fromString(newName));
		return targetElement;
	}

	@Override
	public void createDeclarationUpdates(String newName,
			ResourceSet resourceSet,
			IRefactoringUpdateAcceptor updateAcceptor) {
		super.createDeclarationUpdates(newName, resourceSet, updateAcceptor);

		// perform renaming of the dependent element
		if (targetElement instanceof NodeId) {
			URI resourceURI = getTargetElementOriginalURI().trimFragment();

			NodeId targetNodeId = (NodeId) targetElement;
			List<NodeId> dependentNodeIds = DotAstHelper
					.getAllNodeIds(targetNodeId);

			for (NodeId dependentNodeId : dependentNodeIds) {
				ITextRegion dependentNodeIdTextRegion = locationInFileProvider
						.getFullTextRegion(dependentNodeId);
				TextEdit referenceEdit = new ReplaceEdit(
						dependentNodeIdTextRegion.getOffset(),
						dependentNodeIdTextRegion.getLength(), newName);
				updateAcceptor.accept(resourceURI, referenceEdit);
			}
		}
	}

}