/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
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
package org.eclipse.gef.dot.internal.ui.language.folding;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.DotActivatorEx;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.FoldedPosition;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.ITextRegionWithLineInformation;

import com.google.inject.Injector;

public class DotFoldingRegionProvider extends DefaultFoldingRegionProvider {

	private List<ITextRegionWithLineInformation> acceptedRegions = new LinkedList<ITextRegionWithLineInformation>();

	protected void computeObjectFolding(XtextResource xtextResource,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		acceptedRegions.clear();

		IParseResult parseResult = xtextResource.getParseResult();
		if (parseResult != null) {
			EObject rootASTElement = parseResult.getRootASTElement();
			if (rootASTElement != null) {
				TreeIterator<EObject> allContents = rootASTElement
						.eAllContents();
				while (allContents.hasNext()) {
					EObject eObject = allContents.next();
					if (isHandled(eObject)) {
						computeObjectFolding(eObject, foldingRegionAcceptor);
					}
					if (eObject instanceof Attribute) {
						computeDotAttributeValueFolding((Attribute) eObject,
								foldingRegionAcceptor);
					}
					if (!shouldProcessContent(eObject)) {
						allContents.prune();
					}
				}
			}
		}
	}

	@Override
	protected void computeObjectFolding(EObject eObject,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		ILocationInFileProvider locationInFileProvider = getLocationInFileProvider();
		ITextRegion region = locationInFileProvider.getFullTextRegion(eObject);
		ITextRegionWithLineInformation regionWithLineInformation = (ITextRegionWithLineInformation) region;
		if (region != null) {
			ITextRegion significant = locationInFileProvider
					.getSignificantTextRegion(eObject);
			if (significant == null)
				throw new NullPointerException(
						"significant region may not be null"); //$NON-NLS-1$
			if (!isAlreadyAccepted(regionWithLineInformation)) {
				foldingRegionAcceptor.accept(region.getOffset(),
						region.getLength(), significant);
				acceptedRegions.add(regionWithLineInformation);
			}
		}
	}

	private boolean isAlreadyAccepted(
			ITextRegionWithLineInformation regionWithLineInformation) {
		for (ITextRegionWithLineInformation acceptedRegion : acceptedRegions) {
			if (equals(regionWithLineInformation, acceptedRegion)) {
				return true;
			}
		}
		return false;
	}

	private boolean equals(ITextRegionWithLineInformation region1,
			ITextRegionWithLineInformation region2) {
		return region1.getLineNumber() == region2.getLineNumber()
				&& region1.getEndLineNumber() == region2.getEndLineNumber();
	}

	/**
	 * Compute folding regions for the dot attribute value if possible.
	 * 
	 * @param dotAttribute
	 * @param foldingRegionAcceptor
	 */
	private void computeDotAttributeValueFolding(Attribute attribute,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		// The folding should be able to cope with incomplete statements
		if (attribute.getValue() != null
				&& attribute.getValue().getType() == ID.Type.HTML_STRING) {
			String htmlLabelValue = attribute.getValue().toValue();
			Injector injector = DotActivator.getInstance().getInjector(
					DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);
			IFoldingRegionProvider htmlLabelFoldingRegionProvider = injector
					.getInstance(IFoldingRegionProvider.class);

			IXtextDocument xtextDocument = null;
			try {
				xtextDocument = DotEditorUtils.getDocument(injector,
						htmlLabelValue);
			} catch (Exception e) {
				DotActivatorEx.logError(e);
			}

			if (xtextDocument != null) {
				Collection<FoldedPosition> htmlLabelFoldingRegions = htmlLabelFoldingRegionProvider
						.getFoldingRegions(xtextDocument);
				for (Iterator<FoldedPosition> iterator = htmlLabelFoldingRegions
						.iterator(); iterator.hasNext();) {
					FoldedPosition htmlFoldedPosition = iterator.next();
					int attributeValueStartOffset = getAttributeValueStartOffset(
							attribute);
					foldingRegionAcceptor.accept(
							attributeValueStartOffset
									+ htmlFoldedPosition.getOffset(),
							htmlFoldedPosition.getLength() - 1);
				}
			}
		}
	}

	private int getAttributeValueStartOffset(Attribute attribute) {
		ID attributeValue = attribute.getValue();

		List<INode> nodes = NodeModelUtils.findNodesForFeature(attribute,
				DotPackage.Literals.ATTRIBUTE__VALUE);
		if (nodes.size() != 1) {
			System.err.println(
					"Exact 1 node is expected for the attribute value: " //$NON-NLS-1$
							+ attributeValue + ", but got " + nodes.size()); //$NON-NLS-1$
			return 0;
		}

		INode node = nodes.get(0);
		int attributeValueStartOffset = node.getOffset();
		if (attributeValue.getType() == ID.Type.HTML_STRING
				|| attributeValue.getType() == ID.Type.QUOTED_STRING) {
			// +1 is needed because of the < symbol (indicating the
			// beginning of a html-like label) or " symbol (indicating the
			// beginning of a quoted string)
			attributeValueStartOffset++;
		}
		return attributeValueStartOffset;
	}
}
