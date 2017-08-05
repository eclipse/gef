/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.FoldedPosition;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Injector;

public class DotFoldingRegionProvider extends DefaultFoldingRegionProvider {

	private Injector injector;

	protected void computeObjectFolding(XtextResource xtextResource,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
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

	/**
	 * Compute folding regions for the dot attribute value if possible.
	 * 
	 * @param dotAttribute
	 * @param foldingRegionAcceptor
	 */
	private void computeDotAttributeValueFolding(Attribute attribute,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		if (attribute.getValue().getType() == ID.Type.HTML_STRING) {
			String htmlLabelValue = attribute.getValue().toValue();
			injector = DotActivator.getInstance().getInjector(
					DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);
			IFoldingRegionProvider htmlLabelFoldingRegionProvider = injector
					.getInstance(IFoldingRegionProvider.class);

			IXtextDocument xtextDocument = null;
			try {
				xtextDocument = getDocument(htmlLabelValue);
			} catch (Exception e) {
				e.printStackTrace();
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

	private IXtextDocument getDocument(final String currentModelToParse)
			throws Exception {
		XtextResource xtextResource = doGetResource(
				new StringInputStream(Strings.emptyIfNull(currentModelToParse)),
				// creating an in-memory EMF Resource
				URI.createURI("")); //$NON-NLS-1$

		return getDocument(xtextResource, currentModelToParse);
	}

	private IXtextDocument getDocument(final XtextResource xtextResource,
			final String model) {
		XtextDocument document = get(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		DocumentPartitioner partitioner = get(DocumentPartitioner.class);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
	}

	private <T> T get(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	private XtextResource doGetResource(InputStream in, URI uri)
			throws Exception {
		XtextResourceSet rs = get(XtextResourceSet.class);
		rs.setClasspathURIContext(getClass());
		XtextResource resource = (XtextResource) getResourceFactory()
				.createResource(uri);
		rs.getResources().add(resource);
		resource.load(in, null);
		if (resource instanceof LazyLinkingResource) {
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}
		return resource;
	}

	private IResourceFactory getResourceFactory() {
		return injector.getInstance(IResourceFactory.class);
	}

}
