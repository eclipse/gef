/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #536795)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor.autoedit;

import java.io.StringReader;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Injector;

public class DotAutoEditStrategy implements IAutoEditStrategy {

	@SuppressWarnings("nls")
	@Override
	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {
		String text = command.text;
		if (">".equals(text)) {
			command.caretOffset = command.offset + text.length();
			command.text = text + computeEndTag(document, command);
			command.shiftsCaret = false;
		}
	}

	private String computeEndTag(IDocument document, DocumentCommand command) {

		IUnitOfWork<String, XtextResource> endTagComputationWork = new IUnitOfWork<String, XtextResource>() {

			@Override
			public String exec(XtextResource state) throws Exception {
				HtmlTag openTag = findOpenTag(state, command);
				if (openTag != null) {
					return "</" + openTag.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					return ""; //$NON-NLS-1$
				}
			}

		};

		return ((XtextDocument) document).readOnly(endTagComputationWork);
	}

	private HtmlTag findOpenTag(XtextResource resource,
			DocumentCommand command) {
		if (!resource.getContents().isEmpty()) {
			EObject dotAst = resource.getContents().get(0);
			INode rootNode = NodeModelUtils.getNode(dotAst);
			int cursorPosition = command.offset;
			ILeafNode leafNode = NodeModelUtils.findLeafNodeAtOffset(rootNode,
					cursorPosition);

			String leafNodeText = leafNode.getText();

			String htmlLabelText = extractHtmlLabelContent(leafNodeText);

			if (htmlLabelText == null) {
				return null;
			}

			int htmlLabelStartOffset = leafNode.getOffset() + 1
					+ leafNodeText.substring(1).indexOf('<');
			int htmlLabelCursorPosition = cursorPosition - htmlLabelStartOffset;

			return findOpenTag(htmlLabelText, htmlLabelCursorPosition);

		}
		return null;
	}

	private HtmlTag findOpenTag(String htmlLabelText, int offset) {
		Injector htmlLabelInjector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);
		IParser htmlLabelParser = htmlLabelInjector.getInstance(IParser.class);

		HtmlLabel htmlLabel = (HtmlLabel) htmlLabelParser
				.parse(new StringReader(htmlLabelText)).getRootASTElement();

		if (htmlLabel != null) {
			HtmlContent result = findContentAtOffset(htmlLabel.getParts(),
					offset);

			if (result != null) {
				return result.getTag();
			}
		}

		return null;
	}

	private HtmlContent findContentAtOffset(List<HtmlContent> contents,
			int offset) {
		HtmlContent result = null;

		for (HtmlContent content : contents) {
			HtmlTag tag = content.getTag();
			if (tag != null) {
				INode node = NodeModelUtils.getNode(content);
				if (node.getOffset() <= offset
						&& offset <= (node.getOffset() + node.getLength())) {
					if (result == null) {
						result = content;
					}
				}

				HtmlContent result2 = findContentAtOffset(tag.getChildren(),
						offset);
				if (result2 != null) {
					result = result2;
				}
			}
		}

		return result;
	}

	private String extractHtmlLabelContent(String text) {
		int beginnIndex = text.indexOf('<');
		int endIndex = text.lastIndexOf('>');
		if (beginnIndex != -1 && endIndex != -1 && beginnIndex < endIndex) {
			return text.substring(beginnIndex + 1, endIndex).trim();
		}
		return null;
	}

}
