/*******************************************************************************
 * Copyright (c) 2010, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Fabian Steeg - initial API and implementation (bug #277380, #452650)
 *    Tamas Miklossy (itemis AG) - minor refactorings
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gef.dot.internal.language.dot.AttrList;
import org.eclipse.gef.dot.internal.language.dot.AttrStmt;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotAst;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhs;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Subgraph;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.AttributeTypeElements;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.EdgeOpElements;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.GraphTypeElements;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.SubgraphElements;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for EObjects.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
public class DotLabelProvider extends DefaultEObjectLabelProvider {

	private static final String IMAGE_ATTRIBUTE = "attribute.png"; //$NON-NLS-1$
	private static final String IMAGE_ATTRIBUTES = "attributes.png"; //$NON-NLS-1$
	private static final String IMAGE_EDGE = "edge.png"; //$NON-NLS-1$
	private static final String IMAGE_FILE = "file.png"; //$NON-NLS-1$
	private static final String IMAGE_GRAPH = "graph.png"; //$NON-NLS-1$
	private static final String IMAGE_HTML_TAG = "html_tag.png"; //$NON-NLS-1$
	private static final String IMAGE_HTML_TEXT = "html_text.png"; //$NON-NLS-1$
	private static final String IMAGE_ID = "id.png"; //$NON-NLS-1$
	private static final String IMAGE_NODE = "node.png"; //$NON-NLS-1$
	private static final String IMAGE_RHS = "rhs.png"; //$NON-NLS-1$
	private static final String IMAGE_SUBGRAPH = "subgraph.png"; //$NON-NLS-1$

	@Inject
	private DotGrammarAccess grammarAccess;

	@Inject
	public DotLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String image(DotAst ast) {
		return IMAGE_FILE;
	}

	String image(DotGraph graph) {
		return IMAGE_GRAPH;
	}

	String image(Subgraph graph) {
		return IMAGE_SUBGRAPH;
	}

	String image(NodeStmt node) {
		return IMAGE_NODE;
	}

	String image(EdgeStmtNode edge) {
		return IMAGE_EDGE;
	}

	String image(AttrStmt attr) {
		return IMAGE_ATTRIBUTES;
	}

	String image(Attribute attr) {
		return IMAGE_ATTRIBUTE;
	}

	String image(AttrList attrs) {
		return IMAGE_ATTRIBUTES;
	}

	String image(NodeId attrs) {
		return IMAGE_ID;
	}

	String image(EdgeRhs rhs) {
		return IMAGE_RHS;
	}

	String image(HtmlTag htmlTag) {
		return IMAGE_HTML_TAG;
	}

	String image(HtmlAttr htmlAttr) {
		return IMAGE_ATTRIBUTE;
	}

	String image(HtmlContent htmlContent) {
		return IMAGE_HTML_TEXT;
	}

	Object image(Keyword keyword) {
		GraphTypeElements graphTypeElements = grammarAccess
				.getGraphTypeAccess();
		AttributeTypeElements attributeTypeElements = grammarAccess
				.getAttributeTypeAccess();
		SubgraphElements subgraphElements = grammarAccess.getSubgraphAccess();
		EdgeOpElements edgeOpElements = grammarAccess.getEdgeOpAccess();

		if (keyword == graphTypeElements.getGraphGraphKeyword_0_0()
				|| keyword == graphTypeElements
						.getDigraphDigraphKeyword_1_0()) {
			return IMAGE_GRAPH;
		}

		if (keyword == attributeTypeElements.getGraphGraphKeyword_0_0()
				|| keyword == attributeTypeElements.getNodeNodeKeyword_1_0()
				|| keyword == attributeTypeElements.getEdgeEdgeKeyword_2_0()) {
			return IMAGE_ATTRIBUTES;
		}

		if (keyword == subgraphElements.getSubgraphKeyword_1_0()) {
			return IMAGE_SUBGRAPH;
		}

		if (keyword == edgeOpElements
				.getDirectedHyphenMinusGreaterThanSignKeyword_0_0()
				|| keyword == edgeOpElements
						.getUndirectedHyphenMinusHyphenMinusKeyword_1_0()) {
			return IMAGE_EDGE;
		}
		return super.image(keyword);
	}

	Object text(DotAst model) {
		String format = "%s: File"; //$NON-NLS-1$
		return DotEditorUtils.style(format,
				model.eResource().getURI().lastSegment());
	}

	Object text(DotGraph graph) {
		String format = "%s: Graph"; //$NON-NLS-1$
		ID name = graph.getName();
		return DotEditorUtils.style(format,
				name != null ? name.toValue() : "<?>"); //$NON-NLS-1$
	}

	Object text(Subgraph graph) {
		String format = "%s: Subgraph"; //$NON-NLS-1$
		ID name = graph.getName();
		return DotEditorUtils.style(format,
				(name != null ? name.toValue() : "<?>")); //$NON-NLS-1$
	}

	Object text(NodeStmt node) {
		return text(node.getNode());
	}

	Object text(EdgeStmtNode edge) {
		EList<EdgeRhs> edgeRHS = edge.getEdgeRHS();
		// The label provider (used e.g. within the outline view) should be able
		// to cope with incomplete statements
		if (edgeRHS != null && edgeRHS.size() > 0) {
			String format = "%s %s [%s %s]: Edges"; //$NON-NLS-1$
			String sourceNode = edge.getNode().getName().toValue();
			String opLiteral = edge.getEdgeRHS().get(0).getOp().getLiteral();
			int targetNodeCount = edge.getEdgeRHS().size();
			return DotEditorUtils.style(format, sourceNode, opLiteral,
					targetNodeCount, targetNodeCount > 1 ? "Nodes" : "Node"); //$NON-NLS-1$//$NON-NLS-2$
		} else {
			return DotEditorUtils.style("<?>: Edges"); //$NON-NLS-1$
		}
	}

	Object text(AttrStmt attr) {
		String format = "%s: Attributes"; //$NON-NLS-1$
		String attrLiteral = attr.getType().getLiteral();
		return DotEditorUtils.style(format, attrLiteral);
	}

	Object text(Attribute attr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		ID attributeValue = attr.getValue();
		String displayValue = attributeValue.getType() == ID.Type.HTML_STRING
				? "<HTML-Label>" //$NON-NLS-1$
				: attributeValue.toString();
		return DotEditorUtils.style(format, attr.getName(), displayValue);
	}

	Object text(AttrList attrs) {
		String format = "%s %s: Attributes"; //$NON-NLS-1$
		int attrCount = attrs.getAttributes().size();
		return DotEditorUtils.style(format, attrCount,
				attrCount > 1 ? "Attributes" : "Attribute"); //$NON-NLS-1$//$NON-NLS-2$
	}

	Object text(NodeId id) {
		String format = "%s: Node"; //$NON-NLS-1$
		return DotEditorUtils.style(format, id.getName());
	}

	Object text(EdgeRhs rhs) {
		if (rhs instanceof EdgeRhsNode) {
			String format = "%s %s %s"; //$NON-NLS-1$
			String name = rhs.getOp().getName();
			String literal = rhs.getOp().getLiteral();
			Object targetNodeText = text(((EdgeRhsNode) rhs).getNode());
			return DotEditorUtils.style(format, name, literal, targetNodeText);
		}
		return super.text(rhs);
	}

	Object text(HtmlTag htmlTag) {
		String format = htmlTag.isSelfClosing() ? "<%s/>: Tag" //$NON-NLS-1$
				: "<%s>: Tag"; //$NON-NLS-1$
		return DotEditorUtils.style(format, htmlTag.getName());
	}

	Object text(HtmlAttr htmlAttr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		return DotEditorUtils.style(format, htmlAttr.getName(),
				htmlAttr.getValue());
	}

	Object text(HtmlContent htmlContent) {
		String format = "%s: Text"; //$NON-NLS-1$
		String text = htmlContent.getText() == null ? "" //$NON-NLS-1$
				: htmlContent.getText().trim();
		return DotEditorUtils.style(format, text);
	}
}
