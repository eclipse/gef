/*******************************************************************************
 * Copyright (c) 2010, 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse def License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels and icons for the different DOT EObjects.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
public class DotLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public DotLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String image(DotAst ast) {
		return "file.png"; //$NON-NLS-1$
	}

	String image(DotGraph graph) {
		return "graph.png"; //$NON-NLS-1$
	}

	String image(Subgraph graph) {
		return "subgraph.png"; //$NON-NLS-1$
	}

	String image(NodeStmt node) {
		return "node.png"; //$NON-NLS-1$
	}

	String image(EdgeStmtNode edge) {
		return "edge.png"; //$NON-NLS-1$
	}

	String image(AttrStmt attr) {
		return "attributes.png"; //$NON-NLS-1$
	}

	String image(Attribute attr) {
		return "attribute.png"; //$NON-NLS-1$
	}

	String image(AttrList attrs) {
		return "attributes.png"; //$NON-NLS-1$
	}

	String image(NodeId attrs) {
		return "id.png"; //$NON-NLS-1$
	}

	String image(EdgeRhs rhs) {
		return "rhs.png"; //$NON-NLS-1$
	}

	String image(HtmlTag htmlTag) {
		return "html_tag.png"; //$NON-NLS-1$
	}

	String image(HtmlAttr htmlAttr) {
		return "attribute.png"; //$NON-NLS-1$
	}

	String image(HtmlContent htmlContent) {
		return "html_text.png"; //$NON-NLS-1$
	}

	Object text(DotAst model) {
		String format = "%s: File"; //$NON-NLS-1$
		return styled(format, model.eResource().getURI().lastSegment());
	}

	Object text(DotGraph graph) {
		String format = "%s: Graph"; //$NON-NLS-1$
		ID name = graph.getName();
		return styled(format, name != null ? name.toValue() : "<?>"); //$NON-NLS-1$
	}

	Object text(Subgraph graph) {
		String format = "%s: Subgraph"; //$NON-NLS-1$
		ID name = graph.getName();
		return styled(format, (name != null ? name.toValue() : "<?>")); //$NON-NLS-1$
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
			return styled(format, sourceNode, opLiteral, targetNodeCount,
					targetNodeCount > 1 ? "Nodes" : "Node"); //$NON-NLS-1$//$NON-NLS-2$
		} else {
			return styled("<?>: Edges"); //$NON-NLS-1$
		}
	}

	Object text(AttrStmt attr) {
		String format = "%s: Attributes"; //$NON-NLS-1$
		String attrLiteral = attr.getType().getLiteral();
		return styled(format, attrLiteral);
	}

	Object text(Attribute attr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		ID attributeValue = attr.getValue();
		String displayValue = attributeValue.getType() == ID.Type.HTML_STRING
				? "<HTML-Label>" //$NON-NLS-1$
				: attributeValue.toString();
		return styled(format, attr.getName(), displayValue);
	}

	Object text(AttrList attrs) {
		String format = "%s %s: Attributes"; //$NON-NLS-1$
		int attrCount = attrs.getAttributes().size();
		return styled(format, attrCount,
				attrCount > 1 ? "Attributes" : "Attribute"); //$NON-NLS-1$//$NON-NLS-2$
	}

	Object text(NodeId id) {
		String format = "%s: Node"; //$NON-NLS-1$
		return styled(format, id.getName());
	}

	Object text(EdgeRhs rhs) {
		if (rhs instanceof EdgeRhsNode) {
			String format = "%s %s %s"; //$NON-NLS-1$
			String name = rhs.getOp().getName();
			String literal = rhs.getOp().getLiteral();
			Object targetNodeText = text(((EdgeRhsNode) rhs).getNode());
			return styled(format, name, literal, targetNodeText);
		}
		return super.text(rhs);
	}

	Object text(HtmlTag htmlTag) {
		String format = htmlTag.isSelfClosing() ? "<%s/>: Tag" //$NON-NLS-1$
				: "<%s>: Tag"; //$NON-NLS-1$
		return styled(format, htmlTag.getName());
	}

	Object text(HtmlAttr htmlAttr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		return styled(format, htmlAttr.getName(), htmlAttr.getValue());
	}

	Object text(HtmlContent htmlContent) {
		String format = "%s: Text"; //$NON-NLS-1$
		String text = htmlContent.getText() == null ? "" //$NON-NLS-1$
				: htmlContent.getText().trim();

		return styled(format, text);
	}

	static StyledString styled(String format, Object... args) {
		String text = String.format(format, args);
		StyledString styled = new StyledString(text);
		int offset = text.indexOf(':');
		styled.setStyle(offset, text.length() - offset,
				StyledString.DECORATIONS_STYLER);
		return styled;
	}
}
