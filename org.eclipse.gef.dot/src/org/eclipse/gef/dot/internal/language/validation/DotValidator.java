/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.DotRecordLabelStandaloneSetup;
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
import org.eclipse.gef.dot.internal.language.shape.RecordBasedNodeShape;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.validation.AbstractInjectableValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.RangeBasedDiagnostic;

import com.google.inject.Injector;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotValidator extends AbstractDotValidator {

	/**
	 * Issue Code to indicate the usage of an invalid edge operator:
	 * <ol>
	 * <li>Usage of the directed edge operator in undirected graphs</li>
	 * <li>Usage of the undirected edge operator in directed graphs</li>
	 * </ol>
	 */
	public static final String INVALID_EDGE_OPERATOR = "invalid-edge-operator";

	/**
	 * Issue Code to indicate a redundant attribute
	 */
	public static final String REDUNDANT_ATTRIBUTE = "redundant-attribute";

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

		if (attributeValue == null) {
			// if the attribute value is missing
			// (e.g in case of an incomplete model)
			return;
		}

		// give the DotColorValidator the necessary 'global' information
		DotColorValidator.considerDefaultColorScheme = true;
		DotColorValidator.globalColorScheme = DotAstHelper
				.getColorSchemeAttributeValue(attribute);

		List<Diagnostic> diagnostics = DotAttributes.validateAttributeRawValue(
				DotAttributes.getContext(attribute), attributeName,
				attributeValue);

		// reset the state of the DotColorValidator
		DotColorValidator.globalColorScheme = null;
		DotColorValidator.considerDefaultColorScheme = false;

		List<INode> nodes = NodeModelUtils.findNodesForFeature(attribute,
				DotPackage.Literals.ATTRIBUTE__VALUE);
		if (nodes.size() != 1) {
			System.err.println(
					"Exact 1 node is expected for the attribute value: "
							+ attributeValue + ", but got " + nodes.size());
			return;
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

		for (Diagnostic d : diagnostics) {
			if (d instanceof RangeBasedDiagnostic) {
				RangeBasedDiagnostic rangeBasedDiagnostic = (RangeBasedDiagnostic) d;
				String message = rangeBasedDiagnostic.getMessage();
				int length = rangeBasedDiagnostic.getLength();
				String code = rangeBasedDiagnostic.getIssueCode();
				String[] issueData = rangeBasedDiagnostic.getIssueData();
				int offset = rangeBasedDiagnostic.getOffset()
						+ attributeValueStartOffset;
				switch (d.getSeverity()) {
				case Diagnostic.ERROR:
					getMessageAcceptor().acceptError(message, attribute, offset,
							length, code, issueData);
					break;

				case Diagnostic.WARNING:
					getMessageAcceptor().acceptWarning(message, attribute,
							offset, length, code, issueData);
					break;

				case Diagnostic.INFO:
					getMessageAcceptor().acceptInfo(message, attribute, offset,
							length, code, issueData);
					break;

				}
			} else {
				switch (d.getSeverity()) {
				case Diagnostic.ERROR:
					getMessageAcceptor().acceptError(d.getMessage(), attribute,
							DotPackage.Literals.ATTRIBUTE__VALUE,
							INSIGNIFICANT_INDEX, attributeName,
							attributeValue.toValue());
					break;

				case Diagnostic.WARNING:
					getMessageAcceptor().acceptWarning(d.getMessage(),
							attribute, DotPackage.Literals.ATTRIBUTE__VALUE,
							INSIGNIFICANT_INDEX, attributeName,
							attributeValue.toValue());
					break;
				case Diagnostic.INFO:
					getMessageAcceptor().acceptInfo(d.getMessage(), attribute,
							DotPackage.Literals.ATTRIBUTE__VALUE,
							INSIGNIFICANT_INDEX, attributeName,
							attributeValue.toValue());
					break;

				}
			}
		}
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
				ID shapeValue = DotAstHelper.getAttributeValue(attributeList,
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
					DotPackage.eINSTANCE.getEdgeRhs_Op(), INVALID_EDGE_OPERATOR,
					edgeOp.toString());

		} else if (!graphDirected && edgeDirected) {
			error("EdgeOp '->' may only be used in directed graphs.",
					DotPackage.eINSTANCE.getEdgeRhs_Op(), INVALID_EDGE_OPERATOR,
					edgeOp.toString());
		}
	}

	/**
	 * Ensures that the label attribute value conforms to the recordLabel
	 * subgrammar on nodes where the shape attribute is set to a
	 * {@link RecordBasedNodeShape}.
	 *
	 * @param attribute
	 *            The attribute to validate.
	 */
	@Check
	public void checkRecordLabelValue(Attribute attribute) {
		if (DotAttributes.getContext(attribute).equals(Context.NODE)
				&& attribute.getName().toValue()
						.equals(DotAttributes.LABEL__GCNE)) {
			String shapeValue = DotAstHelper.getDependedOnAttributeValue(
					attribute, DotAttributes.SHAPE__N);
			// do not execute the record label validation in case of an html
			// string
			if (RecordBasedNodeShape.get(shapeValue) != null
					&& attribute.getValue().getType() != ID.Type.HTML_STRING) {
				doRecordLabelValidation(attribute);
			}
		}
	}

	private void doRecordLabelValidation(Attribute attribute) {
		Injector recordLabelInjector = new DotRecordLabelStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
		DotRecordLabelValidator validator = recordLabelInjector
				.getInstance(DotRecordLabelValidator.class);
		IParser parser = recordLabelInjector.getInstance(IParser.class);

		DotSubgrammarValidationMessageAcceptor messageAcceptor = new DotSubgrammarValidationMessageAcceptor(
				attribute, DotPackage.Literals.ATTRIBUTE__VALUE,
				"record-based label", getMessageAcceptor(), "\"".length());

		validator.setMessageAcceptor(messageAcceptor);

		IParseResult result = parser
				.parse(new StringReader(attribute.getValue().toValue()));

		for (INode error : result.getSyntaxErrors())
			messageAcceptor.acceptSyntaxError(error);

		Map<Object, Object> validationContext = new HashMap<Object, Object>();
		validationContext.put(AbstractInjectableValidator.CURRENT_LANGUAGE_NAME,
				ReflectionUtils.getPrivateFieldValue(validator,
						"languageName"));

		// validate both the children (loop) and root element
		Iterator<EObject> iterator = result.getRootASTElement().eAllContents();
		while (iterator.hasNext())
			validator.validate(iterator.next(), null/* diagnostic chain */,
					validationContext);

		validator.validate(result.getRootASTElement(), null, validationContext);
	}

	/**
	 * Checks that attribute lists do not contain the same attribute multiple
	 * times; issues a warning for redundant attribute values.
	 *
	 * @param attrList
	 *            An attribute list being checked.
	 */
	@Check
	public void checkRedundantAttribute(AttrList attrList) {
		Set<ID> definedAttributes = new HashSet<ID>();
		// iterate backwards as the last attribute value will be used
		for (int i = attrList.getAttributes().size() - 1; i >= 0; i--) {
			Attribute attribute = attrList.getAttributes().get(i);
			if (!definedAttributes.add(attribute.getName())) {
				warning("Redundant attribute value '"
						+ attribute.getValue().toValue() + "' for attribute '"
						+ attribute.getName() + "' is ignored.", attribute,
						null, REDUNDANT_ATTRIBUTE,
						attribute.getName().toString());
			}
		}
	}

}
