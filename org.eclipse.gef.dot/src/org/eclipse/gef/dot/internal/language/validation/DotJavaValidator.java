/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     Tamas Miklossy  - Add support for arrowType edge decorations (bug #477980)
 *                     - Add support for polygon-based node shapes (bug #441352)
 *                     - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/

package org.eclipse.gef.dot.internal.language.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.language.dot.AttrList;
import org.eclipse.gef.dot.internal.language.dot.AttrStmt;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.dot.EdgeOp;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsSubgraph;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.FeatureBasedDiagnostic;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

/**
 * Provides DOT-specific validation rules.
 * 
 * @author anyssen
 *
 */
public class DotJavaValidator extends AbstractDotJavaValidator {

	/**
	 * Checks that within an {@link Attribute} only valid attribute values are
	 * used (dependent on context, in which the attribute is specified).
	 * 
	 * @param attribute
	 *            The {@link Attribute} to validate.
	 */
	@Check
	public void checkValidAttributeValue(final Attribute attribute) {
		String attributeName = attribute.getName().toValue();
		ID attributeValue = attribute.getValue();
		List<Diagnostic> diagnostics = convertToFeatureBasedDiagnostic(
				DotAttributes.validateAttributeRawValue(
						DotAttributes.getContext(attribute), attributeName,
						attributeValue),
				attributeName, attributeValue);
		for (Diagnostic d : diagnostics) {
			if (d.getSeverity() == Diagnostic.ERROR) {
				getMessageAcceptor().acceptError(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attributeName,
						attributeValue.toValue());
			} else if (d.getSeverity() == Diagnostic.WARNING) {
				getMessageAcceptor().acceptWarning(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attributeName,
						attributeValue.toValue());
			} else if (d.getSeverity() == Diagnostic.INFO) {
				getMessageAcceptor().acceptInfo(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attributeName,
						attributeValue.toValue());
			}
		}
	}

	private List<Diagnostic> convertToFeatureBasedDiagnostic(
			List<Diagnostic> diagnostics, String attributeName,
			ID attributeValue) {
		List<Diagnostic> result = new ArrayList<>();
		for (Diagnostic d : diagnostics) {
			result.add(new FeatureBasedDiagnostic(d.getSeverity(),
					d.getMessage(), null /* current object */,
					DotPackage.Literals.ATTRIBUTE__VALUE,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					CheckType.NORMAL, attributeName, attributeValue.toValue()));
		}
		return result;
	}

	/**
	 * Ensures that the 'striped' node style is used only for
	 * rectangularly-shaped nodes ('box', 'rect', 'rectangle' and 'square').
	 * 
	 * @param attribute
	 *            The node style attribute to validate.
	 */
	@Check
	public void checkValidCombinationOfNodeShapeAndStyle(Attribute attribute) {
		if (DotAttributes.getContext(attribute) == Context.NODE
				&& attribute.getName().toValue()
						.equals(DotAttributes.STYLE__GCNE)
				&& attribute.getValue().toValue()
						.equals(NodeStyle.STRIPED.toString())) {
			EList<AttrList> attributeList = null;
			NodeStmt node = EcoreUtil2.getContainerOfType(attribute,
					NodeStmt.class);
			if (node != null) {
				attributeList = node.getAttrLists();
			} else {
				AttrStmt attrStmt = EcoreUtil2.getContainerOfType(attribute,
						AttrStmt.class);
				if (attrStmt != null) {
					attributeList = attrStmt.getAttrLists();
				}
			}

			if (attributeList != null) {
				// TODO: DotImport should not be referenced here
				ID shapeValue = DotImport.getAttributeValue(attributeList,
						DotAttributes.SHAPE__N);
				// if the shape value is not explicitly set, use the default
				// shape value for evaluation
				if (shapeValue == null) {
					shapeValue = ID.fromString(
							PolygonBasedNodeShape.ELLIPSE.toString());
				}
				switch (PolygonBasedNodeShape.get(shapeValue.toValue())) {
				case BOX:
				case RECT:
				case RECTANGLE:
				case SQUARE:
					break;
				default:
					error("The style 'striped' is only supported with clusters and rectangularly-shaped nodes, such as 'box', 'rect', 'rectangle', 'square'.",
							DotPackage.eINSTANCE.getAttribute_Value());
				}
			}
		}
	}

	/**
	 * Ensures that within {@link EdgeRhsNode}, '-&gt;' is used in directed
	 * graphs, while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsNode
	 *            The EdgeRhsNode to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(EdgeRhsNode edgeRhsNode) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsNode.getOp(), EcoreUtil2
				.getContainerOfType(edgeRhsNode, DotGraph.class).getType());
	}

	/**
	 * Ensures that within {@link EdgeRhsSubgraph} '-&gt;' is used in directed
	 * graphs, while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsSubgraph
	 *            The EdgeRhsSubgraph to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(
			EdgeRhsSubgraph edgeRhsSubgraph) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsSubgraph.getOp(), EcoreUtil2
				.getContainerOfType(edgeRhsSubgraph, DotGraph.class).getType());
	}

	private void checkEdgeOpCorrespondsToGraphType(EdgeOp edgeOp,
			GraphType graphType) {
		boolean edgeDirected = edgeOp.equals(EdgeOp.DIRECTED);
		boolean graphDirected = graphType.equals(GraphType.DIGRAPH);
		if (graphDirected && !edgeDirected) {
			error("EdgeOp '--' may only be used in undirected graphs.",
					DotPackage.eINSTANCE.getEdgeRhs_Op());

		} else if (!graphDirected && edgeDirected) {
			error("EdgeOp '->' may only be used in directed graphs.",
					DotPackage.eINSTANCE.getEdgeRhs_Op());
		}
	}
}
