/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.codemining;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorInfo;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.swt.graphics.Color;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.codemining.AbstractXtextCodeMiningProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;

public class DotCodeMiningProvider extends AbstractXtextCodeMiningProvider {

	@Override
	protected void createCodeMinings(IDocument document, XtextResource resource,
			CancelIndicator indicator, IAcceptor<? super ICodeMining> acceptor)
			throws BadLocationException {

		EList<EObject> contents = resource.getContents();
		if (contents.isEmpty()) {
			return;
		}

		EObject dotAst = contents.get(0);

		createDotGraphCodeMinings(document, dotAst, acceptor);

		createDotColorAttributesCodeMining(document, dotAst, acceptor);
	}

	private void createDotGraphCodeMinings(IDocument document, EObject dotAst,
			IAcceptor<? super ICodeMining> acceptor)
			throws BadLocationException {
		// get all graphs in the open document
		List<DotGraph> dotGraphs = EcoreUtil2.eAllOfType(dotAst,
				DotGraph.class);
		for (DotGraph dotGraph : dotGraphs) {
			int nodesCount = DotAstHelper.getNumberOfNodes(dotGraph);
			String nodesCountHeaderText = nodesCount + " node" //$NON-NLS-1$
					+ (nodesCount == 1 ? "" : "s"); //$NON-NLS-1$ //$NON-NLS-2$

			int edgesCount = DotAstHelper.getNumberOfEdges(dotGraph);
			String edgesCountHeaderText = edgesCount + " edge" //$NON-NLS-1$
					+ (edgesCount == 1 ? "" : "s"); //$NON-NLS-1$ //$NON-NLS-2$

			ICompositeNode node = NodeModelUtils.getNode(dotGraph);
			int beforeLineNumber = document.getLineOfOffset(node.getOffset());

			// create two line header code minings before the graph: one for
			// the nodes, one for the edges
			acceptor.accept(createNewLineHeaderCodeMining(beforeLineNumber,
					document, nodesCountHeaderText));
			acceptor.accept(createNewLineHeaderCodeMining(beforeLineNumber,
					document, edgesCountHeaderText));
		}
	}

	private void createDotColorAttributesCodeMining(IDocument document,
			EObject dotAst, IAcceptor<? super ICodeMining> acceptor) {
		// get all attributes in the open document
		List<Attribute> attributes = EcoreUtil2.eAllOfType(dotAst,
				Attribute.class);

		for (Attribute attribute : attributes) {
			Context attributeContext = DotAttributes.getContext(attribute);
			String attributeName = attribute.getName().toValue();

			if (attributeContext == Context.EDGE) {
				switch (attributeName) {
				case DotAttributes.COLOR__CNE:
					createColorListCodeMinings(attribute, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					createColorCodeMining(attribute, acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
				case DotAttributes.LABELFONTCOLOR__E:
					createColorCodeMining(attribute, acceptor);
					break;
				}
			} else if (attributeContext == Context.GRAPH) {
				switch (attributeName) {
				case DotAttributes.BGCOLOR__GC:
					createColorListCodeMinings(attribute, acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					createColorCodeMining(attribute, acceptor);
					break;
				}
			} else if (attributeContext == Context.NODE) {
				switch (attributeName) {
				case DotAttributes.COLOR__CNE:
					createColorCodeMining(attribute, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					createColorListCodeMinings(attribute, acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					createColorCodeMining(attribute, acceptor);
					break;
				}
			} else if (attributeContext == Context.CLUSTER) {
				switch (attributeName) {
				case DotAttributes.BGCOLOR__GC:
					createColorListCodeMinings(attribute, acceptor);
					break;
				case DotAttributes.COLOR__CNE:
					createColorCodeMining(attribute, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					createColorListCodeMinings(attribute, acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					createColorCodeMining(attribute, acceptor);
					break;
				}
			}
		}
	}

	private void createColorListCodeMinings(Attribute attribute,
			IAcceptor<? super ICodeMining> acceptor) {
		int annotationOffset = getAnnotationOffset(attribute);
		List<Color> annotationColors = getColors(attribute);

		for (Color annotationColor : annotationColors) {
			acceptor.accept(createNewColoredRectangleLineContentCodeMining(
					annotationOffset, annotationColor));
		}
	}

	private void createColorCodeMining(Attribute attribute,
			IAcceptor<? super ICodeMining> acceptor) {
		int annotationOffset = getAnnotationOffset(attribute);
		Color annotationColor = getColor(attribute);

		if (annotationColor != null) {
			acceptor.accept(createNewColoredRectangleLineContentCodeMining(
					annotationOffset, annotationColor));
		}
	}

	private ICodeMining createNewColoredRectangleLineContentCodeMining(
			int beforeCharacter, Color color) {
		return new ColoredRectangleLineContentCodeMining(
				new Position(beforeCharacter, 1), this, color);
	}

	private int getAnnotationOffset(Attribute attribute) {
		List<INode> nodes = NodeModelUtils.findNodesForFeature(attribute,
				org.eclipse.gef.dot.internal.language.dot.DotPackage.Literals.ATTRIBUTE__VALUE);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got " //$NON-NLS-1$
							+ nodes.size() + " node(s)."); //$NON-NLS-1$
		}

		INode node = nodes.get(0);

		return node.getEndOffset();

	}

	private Color getColor(Attribute attribute) {
		DotColorUtil colorUtil = new DotColorUtil();

		if (attribute.getValue() == null) {
			return null;
		}

		DotColorInfo colorInfo = colorUtil.getColorInfo(attribute);

		String hexColorCode = colorInfo.getColorCode();

		Color swtColor = null;

		try {
			swtColor = colorUtil.hex2Rgb(hexColorCode);
		} catch (Exception e) {
			// in case of invalid color attribute values
			return null;
		}

		return swtColor;
	}

	private List<Color> getColors(Attribute attribute) {
		DotColorUtil colorUtil = new DotColorUtil();

		List<DotColorInfo> colorInfos = colorUtil.getColorInfos(attribute);

		List<Color> swtColors = new ArrayList<>();

		for (DotColorInfo colorInfo : colorInfos) {
			String hexColorCode = colorInfo.getColorCode();

			Color swtColor = null;

			try {
				swtColor = colorUtil.hex2Rgb(hexColorCode);
			} catch (Exception e) {
				// in case of invalid color attribute values
			}

			if (swtColor != null) {
				swtColors.add(swtColor);
			}

		}
		return swtColors;
	}

}
