/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse def License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.TextRegion;

/**
 * Customization of the default outline structure.
 */
public class DotHtmlLabelOutlineTreeProvider
		extends DefaultOutlineTreeProvider {

	private IXtextDocument xtextDocument;
	private int offset = 0;

	void setOffset(int offset) {
		this.offset = offset;
	}

	void setXtextDocument(IXtextDocument xtextDocument) {
		this.xtextDocument = xtextDocument;
	}

	/**
	 * Skip the root element, represent only its children.
	 */
	private void _createChildren(DocumentRootNode outlineNode,
			HtmlLabel model) {
		for (HtmlContent htmlContent : model.getParts()) {
			if (htmlContent.getText() != null
					&& !htmlContent.getText().trim().isEmpty()) {
				createNode(outlineNode, htmlContent);
			} else {
				if (htmlContent.getTag() != null) {
					createNode(outlineNode, htmlContent.getTag());
				}
			}
		}
	}

	/**
	 * Skip the empty (containing nothing or only white-spaces) htmlContent
	 * elements, but process their tag children.
	 */
	private void _createNode(IOutlineNode parent, HtmlContent htmlContent) {
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
		EObjectNode eObjectNode = new EObjectNode(modelElement, parentNode,
				image, text, isLeaf) {
			@Override
			public IXtextDocument getDocument() {
				return xtextDocument != null ? xtextDocument
						: super.getDocument();
			}
		};
		ICompositeNode parserNode = NodeModelUtils.getNode(modelElement);
		if (parserNode != null) {
			ITextRegion parserNodeTextRegion = parserNode.getTextRegion();
			ITextRegion newTextRegion = new TextRegion(
					parserNodeTextRegion.getOffset() + offset,
					parserNodeTextRegion.getLength());
			eObjectNode.setTextRegion(newTextRegion);
		}
		/* if (isLocalElement(parentNode, modelElement)) { */
		ITextRegion significantTextRegion = locationInFileProvider
				.getSignificantTextRegion(modelElement);
		ITextRegion shortTextRegion = new TextRegion(
				significantTextRegion.getOffset() + offset,
				significantTextRegion.getLength());
		eObjectNode.setShortTextRegion(shortTextRegion);
		// }
		return eObjectNode;
	}
}
