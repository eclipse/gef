/*******************************************************************************
 * Copyright (c) 2010, 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * Fabian Steeg
 * - initial API and implementation; see bug 277380
 * - custom outline labels, icons, and structure; see bug 452650
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gef4.internal.dot.parser.dot.AttrList;
import org.eclipse.gef4.internal.dot.parser.dot.AttrStmt;
import org.eclipse.gef4.internal.dot.parser.dot.Attribute;
import org.eclipse.gef4.internal.dot.parser.dot.DotGraph;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeRhs;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeStmtNode;
import org.eclipse.gef4.internal.dot.parser.dot.GraphvizModel;
import org.eclipse.gef4.internal.dot.parser.dot.NodeId;
import org.eclipse.gef4.internal.dot.parser.dot.NodeStmt;
import org.eclipse.gef4.internal.dot.parser.dot.Subgraph;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels and icons for the different DOT EObjects.
 * 
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class DotLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public DotLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String image(GraphvizModel model) {
		return "file.png"; //$NON-NLS-1$
	}

	String image(DotGraph graph) {
		return "graph_outline.png"; //$NON-NLS-1$
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

	Object text(GraphvizModel model) {
		return styled(model.eResource().getURI().lastSegment() + ": File"); //$NON-NLS-1$
	}

	Object text(DotGraph graph) {
		String name = graph.getName();
		return styled((name != null ? name : "<?>") + ": Graph"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	Object text(Subgraph graph) {
		String name = graph.getName();
		return styled((name != null ? name : "<?>") + ": Subgraph"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	Object text(NodeStmt node) {
		return styled(node.getNode().getName() + ": Node"); //$NON-NLS-1$
	}

	Object text(EdgeStmtNode edge) {
		String format = "%s %s [%s %s]: Edges"; //$NON-NLS-1$
		String sourceNode = edge.getNode().getName();
		String opLiteral = edge.getEdgeRHS().get(0).getOp().getLiteral();
		int targetNodeCount = edge.getEdgeRHS().size();
		return styled(String.format(format, sourceNode, opLiteral,
				targetNodeCount, targetNodeCount > 1 ? "Nodes" : "Node")); //$NON-NLS-1$//$NON-NLS-2$
	}

	Object text(AttrStmt attr) {
		String format = "%s: Attributes"; //$NON-NLS-1$
		String attrLiteral = attr.getType().getLiteral();
		return styled(String.format(format, attrLiteral));
	}

	Object text(Attribute attr) {
		String format = "%s = %s: Attribute"; //$NON-NLS-1$
		return styled(String.format(format, attr.getName(), attr.getValue()));
	}

	Object text(AttrList attrs) {
		String format = "%s %s: Attributes"; //$NON-NLS-1$
		int attrCount = attrs.getAttributes().size();
		return styled(String.format(format, attrCount,
				attrCount > 1 ? "Attributes" : "Attribute")); //$NON-NLS-1$//$NON-NLS-2$
	}

	Object text(NodeId id) {
		return styled(id.getName() + ": Node"); //$NON-NLS-1$
	}

	Object text(EdgeRhs rhs) {
		if (!(rhs instanceof EdgeRhsNode)) {
			return super.text(rhs);
		}
		String format = "%s %s %s"; //$NON-NLS-1$
		String name = rhs.getOp().getName();
		String literal = rhs.getOp().getLiteral();
		Object targetNodeText = text(((EdgeRhsNode) rhs).getNode());
		return styled(String.format(format, name, literal, targetNodeText));
	}

	private static StyledString styled(String format) {
		StyledString styled = new StyledString(format);
		int offset = format.indexOf(':');
		styled.setStyle(offset, format.length() - offset,
				StyledString.DECORATIONS_STYLER);
		return styled;
	}
}
