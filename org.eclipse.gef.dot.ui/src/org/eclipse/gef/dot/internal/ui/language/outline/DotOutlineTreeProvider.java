/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg               - initial API and implementation (bug #277380)
 *                                - custom outline labels, icons, and structure (bug #452650)
 *     Tamas Miklossy (itemis AG) - add support for HTML-Like labels (bug #321775)
 *                                - minor renamings (bug #493745)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.outline;

import java.io.InputStream;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.dot.AttrStmt;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhs;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.outline.impl.IOutlineTreeStructureProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.util.TextRegion;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Injector;

/**
 * Customization of the default outline structure.
 * 
 */
public class DotOutlineTreeProvider extends DefaultOutlineTreeProvider {

	private Injector injector;
	private int attributeValueStartOffset;

	/**
	 * Treat node statements as leafs if they have no attributes.
	 * 
	 * @param node
	 *            The 'NodeStmt' model element
	 * @return true if this node contains no attributes
	 */
	protected boolean _isLeaf(NodeStmt node) {
		return node.getAttrLists().isEmpty();
	}

	/**
	 * 'EdgeRhs' elements are displayed as leafs and not expandable.
	 * 
	 * @param edge
	 *            The 'EdgeRhs' model element
	 * @return true
	 */
	protected boolean _isLeaf(EdgeRhs edge) {
		return true;
	}

	/**
	 * Consider an attribute having a HTML_STRING value as non-leaf.
	 * 
	 * @param attribute
	 * @return
	 */
	protected boolean _isLeaf(Attribute attribute) {
		if (attribute != null && attribute.getValue() != null
				&& attribute.getValue().getType() == ID.Type.HTML_STRING) {
			return false;
		}
		return true;
	}

	/**
	 * Skip the 'AttrList' wrapper element in the outline structure.
	 *
	 * @param parent
	 *            The outline parent node.
	 * @param stmt
	 *            The attribute statement.
	 */
	protected void _createChildren(IOutlineNode parent, AttrStmt stmt) {
		if (stmt.getAttrLists().size() > 0) {
			EList<Attribute> attributes = stmt.getAttrLists().get(0)
					.getAttributes(); // skip the 'AttrList'
			for (Attribute attribute : attributes) {
				createNode(parent, attribute);
			}
		}
	}

	/**
	 * Create proper outline subtree from the dot attribute value if possible.
	 * 
	 * @param parent
	 * @param attribute
	 */
	protected void _createChildren(IOutlineNode parent, Attribute attribute) {
		if (attribute.getValue().getType() == ID.Type.HTML_STRING) {
			String htmlLabelValue = attribute.getValue().toValue();
			injector = DotActivator.getInstance().getInjector(
					DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);

			IOutlineTreeStructureProvider outlineTreeStructureProvider = injector
					.getInstance(IOutlineTreeStructureProvider.class);

			IXtextDocument xtextDocument = null;
			try {
				xtextDocument = getDocument(htmlLabelValue);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (xtextDocument != null
					&& outlineTreeStructureProvider instanceof DotHtmlLabelOutlineTreeProvider) {

				DotHtmlLabelOutlineTreeProvider dotHtmlLabelOutlineTreeProvider = (DotHtmlLabelOutlineTreeProvider) outlineTreeStructureProvider;

				dotHtmlLabelOutlineTreeProvider.setXtextDocument(xtextDocument);

				attributeValueStartOffset = getAttributeValueStartOffset(
						attribute);
				dotHtmlLabelOutlineTreeProvider
						.setOffset(attributeValueStartOffset);

				HtmlLabel htmlLabel = getModel(xtextDocument);
				outlineTreeStructureProvider.createChildren(parent, htmlLabel);
			}
		}
	}

	/**
	 * Skip the empty (containing nothing or only white-spaces) htmlContent
	 * elements, but process their tag children.
	 */
	protected void _createNode(IOutlineNode parent, HtmlContent htmlContent) {
		if (htmlContent.getText() != null
				&& !htmlContent.getText().trim().isEmpty()) {
			super._createNode(parent, htmlContent);
		} else {
			if (htmlContent.getTag() != null) {
				super._createNode(parent, htmlContent.getTag());
			}
		}
	}

	@Override
	protected EObjectNode createEObjectNode(IOutlineNode parentNode,
			EObject modelElement, Image image, Object text, boolean isLeaf) {
		if (EcoreUtil2.getContainerOfType(modelElement,
				HtmlLabel.class) != null) {
			// in case of a html-like label addition offset should be calculated
			EObjectNode eObjectNode = new EObjectNode(modelElement, parentNode,
					image, text, isLeaf);
			ICompositeNode parserNode = NodeModelUtils.getNode(modelElement);
			if (parserNode != null) {
				ITextRegion parserNodeTextRegion = parserNode.getTextRegion();
				ITextRegion newTextRegion = new TextRegion(
						parserNodeTextRegion.getOffset()
								+ attributeValueStartOffset,
						parserNodeTextRegion.getLength());
				eObjectNode.setTextRegion(newTextRegion);
			}
			if (isLocalElement(parentNode, modelElement)) {
				ITextRegion significantTextRegion = locationInFileProvider
						.getSignificantTextRegion(modelElement);
				ITextRegion shortTextRegion = new TextRegion(
						significantTextRegion.getOffset()
								+ attributeValueStartOffset,
						significantTextRegion.getLength());
				eObjectNode.setShortTextRegion(shortTextRegion);
			}
			return eObjectNode;
		} else {
			return super.createEObjectNode(parentNode, modelElement, image,
					text, isLeaf);
		}

	}

	private HtmlLabel getModel(IXtextDocument xtextDocument) {
		HtmlLabel model = xtextDocument
				.readOnly(new IUnitOfWork<HtmlLabel, XtextResource>() {
					@Override
					public HtmlLabel exec(XtextResource res) throws Exception {
						return res.getContents().size() > 0
								? (HtmlLabel) res.getContents().get(0)
								: null;
					}
				});

		return model;
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
