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
 *
 *******************************************************************************/

package org.eclipse.gef4.dot.internal.parser.validation;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.parser.DotArrowTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowtypePackage;
import org.eclipse.gef4.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef4.dot.internal.parser.dot.AttrStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.dot.AttributeType;
import org.eclipse.gef4.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef4.dot.internal.parser.dot.DotPackage;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeOp;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeRhsSubgraph;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeStmtNode;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeStmtSubgraph;
import org.eclipse.gef4.dot.internal.parser.dot.GraphType;
import org.eclipse.gef4.dot.internal.parser.dot.NodeStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Subgraph;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotArrowTypeParser;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Injector;

/**
 * Provides DOT-specific validation rules.
 * 
 * @author anyssen
 *
 */
public class DotJavaValidator extends AbstractDotJavaValidator {

	private static final Injector arrowTypeInjector = new DotArrowTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	private static final DotArrowTypeJavaValidator arrowTypeValidator = arrowTypeInjector
			.getInstance(DotArrowTypeJavaValidator.class);

	private static final DotArrowTypeParser arrowTypeParser = arrowTypeInjector
			.getInstance(DotArrowTypeParser.class);

	/**
	 * Error code for invalid edge 'style' attribute value. Used to bind quick
	 * fixes.
	 */
	public static final String ATTRIBUTE__INVALID_VALUE__EDGE_STYLE = "ATTRIBUTE__INVALID_VALUE__EDGE_STYLE";

	/**
	 * Error code for invalid edge 'arrow type' attribute value. Used to bind
	 * quick fixes.
	 */
	public static final String ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_TYPE = "ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_TYPE";

	/**
	 * Error code for invalid edge 'dir' attribute value. Used to bind quick
	 * fixes.
	 */
	public static final String ATTRIBUTE__INVALID_VALUE__EDGE_DIRECTION = "ATTRIBUTE__INVALID_VALUE__EDGE_DIRECTION";

	/**
	 * Error code for invalid edge 'arrowsize' attribute value. Used to bind
	 * quick fixes.
	 */
	public static final String ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_SIZE = "ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_SIZE";

	/**
	 * Checks that within an {@link Attribute} only valid attribute values are
	 * used (dependent on context, in which the attribute is specified).
	 * 
	 * @param attribute
	 *            The {@link Attribute} to validate.
	 */
	@Check
	public void checkValidAttributeValue(final Attribute attribute) {
		if (isEdgeAttribute(attribute)
				&& DotAttributes.STYLE__E.equals(attribute.getName())) {
			// 'style' can also be used for nodes or clusters, so we have to
			// check the context as well
			String unquotedValue = DotTerminalConverters
					.unquote(attribute.getValue());
			if (!DotAttributes.STYLE__E__VALUES.contains(unquotedValue)) {
				// provide (issue) code and data for quickfix
				error("Style '" + unquotedValue
						+ "' is not a valid DOT style for Edge.",
						DotPackage.eINSTANCE.getAttribute_Value(),
						ATTRIBUTE__INVALID_VALUE__EDGE_STYLE, unquotedValue);
			}
		}

		if (isEdgeAttribute(attribute) && (DotAttributes.ARROWHEAD__E
				.equals(attribute.getName())
				|| DotAttributes.ARROWTAIL__E.equals(attribute.getName()))) {
			// validate arrowhead/arrowtail using delegate parser and validator
			validateAttributeValue(arrowTypeParser, arrowTypeValidator,
					attribute, ArrowtypePackage.eINSTANCE.getArrowType(),
					ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_TYPE);
		}

		if (isEdgeAttribute(attribute)
				&& DotAttributes.ARROWSIZE__E.equals(attribute.getName())) {
			String unquotedValue = DotTerminalConverters
					.unquote(attribute.getValue());
			if (!isValidEdgeArrowSize(unquotedValue)) {
				// provide (issue) code and data for quickfix
				error("Edge Arrow Size '" + unquotedValue
						+ "' is not a valid DOT arrow size for Edge.",
						DotPackage.eINSTANCE.getAttribute_Value(),
						ATTRIBUTE__INVALID_VALUE__EDGE_ARROW_SIZE,
						unquotedValue);
			}
		}

		if (isEdgeAttribute(attribute)
				&& DotAttributes.DIR__E.equals(attribute.getName())) {
			String unquotedValue = DotTerminalConverters
					.unquote(attribute.getValue());
			if (!DotAttributes.DIR__E__VALUES.contains(unquotedValue)) {
				// provide (issue) code and data for quickfix
				error("Edge Direction '" + unquotedValue
						+ "' is not a valid DOT direction for Edge.",
						DotPackage.eINSTANCE.getAttribute_Value(),
						ATTRIBUTE__INVALID_VALUE__EDGE_DIRECTION,
						unquotedValue);
			}
		}
	}

	private void validateAttributeValue(final AbstractAntlrParser parser,
			final AbstractDeclarativeValidator validator,
			final Attribute attribute, final EClass attributeType,
			final String issueCode) {
		// ensure we always use the unquoted value
		final String unquotedValue = DotTerminalConverters
				.unquote(attribute.getValue());
		IParseResult parseResult = arrowTypeParser
				.parse(new StringReader(unquotedValue));
		if (parseResult.hasSyntaxErrors()) {
			// syntactical problems
			error("The value '" + unquotedValue
					+ "' is not a syntactically correct "
					+ ArrowtypePackage.eINSTANCE.getArrowType().getName() + ": "
					+ getFormattedSyntaxErrorMessages(parseResult),
					DotPackage.eINSTANCE.getAttribute_Value(), issueCode,
					unquotedValue);
		} else {
			// check for semantic problems by using DotArrowTypeJavaValidator
			Map<Object, Object> context = new HashMap<>(getContext());
			context.put(CURRENT_LANGUAGE_NAME, ReflectionUtils
					.getPrivateFieldValue(validator, "languageName"));
			// we need a specific message acceptor
			validator.setMessageAcceptor(new ValidationMessageAcceptor() {

				final String prefix = "The value '" + unquotedValue
						+ "' is not a semantically correct "
						+ attributeType.getName() + ": ";

				@Override
				public void acceptWarning(String message, EObject object,
						int offset, int length, String code,
						String... issueData) {
					getMessageAcceptor().acceptWarning(prefix + message,
							attribute, offset, length, issueCode,
							unquotedValue);
				}

				@Override
				public void acceptWarning(String message, EObject object,
						EStructuralFeature feature, int index, String code,
						String... issueData) {
					getMessageAcceptor().acceptWarning(prefix + message,
							attribute, DotPackage.Literals.ATTRIBUTE__VALUE, -1,
							issueCode, unquotedValue);
				}

				@Override
				public void acceptInfo(String message, EObject object,
						int offset, int length, String code,
						String... issueData) {
					getMessageAcceptor().acceptInfo(message, attribute, offset,
							length, issueCode, unquotedValue);
				}

				@Override
				public void acceptInfo(String message, EObject object,
						EStructuralFeature feature, int index, String code,
						String... issueData) {
					getMessageAcceptor().acceptInfo(message, attribute,
							DotPackage.Literals.ATTRIBUTE__VALUE, -1, issueCode,
							unquotedValue);
				}

				@Override
				public void acceptError(String message, EObject object,
						int offset, int length, String code,
						String... issueData) {
					getMessageAcceptor().acceptError(prefix + message,
							attribute, offset, length, issueCode,
							unquotedValue);
				}

				@Override
				public void acceptError(String message, EObject object,
						EStructuralFeature feature, int index, String code,
						String... issueData) {
					getMessageAcceptor().acceptError(prefix + message,
							attribute, DotPackage.Literals.ATTRIBUTE__VALUE, -1,
							issueCode, unquotedValue);
				}
			});
			for (Iterator<EObject> iterator = EcoreUtil.getAllProperContents(
					parseResult.getRootASTElement(), true); iterator
							.hasNext();) {
				validator.validate(attributeType, iterator.next(), getChain(),
						context);
			}
		}
	}

	// TODO: replace by usage of validator instance inside DotAttributes
	public static String getFormattedSyntaxErrorMessages(
			IParseResult parseResult) {
		StringBuilder sb = new StringBuilder();
		for (INode n : parseResult.getSyntaxErrors()) {
			String message = n.getSyntaxErrorMessage().getMessage();
			if (!message.isEmpty()) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(message.substring(0, 1).toUpperCase()
						+ message.substring(1) + ".");
			}
		}
		return sb.toString();
	}

	/**
	 * @param arrowsize
	 *            the edge arrow size to check for validity
	 * 
	 * @return true if the edge arrowsize is valid, false otherwise
	 */
	// TODO: can be made private as soon as DotAttributes invokes validator
	// generically
	public static boolean isValidEdgeArrowSize(String arrowsize) {
		double arrowSizeDouble;
		try {
			arrowSizeDouble = Double.parseDouble(arrowsize);
		} catch (NumberFormatException exception) {
			return false;
		}
		return arrowSizeDouble >= 0.0;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * node. That is, it is either nested below an {@link NodeStmt} or used
	 * within an {@link AttrStmt} of type {@link AttributeType#NODE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of an node, <code>false</code> otherwise.
	 */
	// TODO: move to DotAttributes
	public static boolean isNodeAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (getAncestorOfType(attribute, NodeStmt.class) != null) {
			return true;
		}
		// global AttrStmt with AttributeType 'node'
		AttrStmt attrStmt = getAncestorOfType(attribute, AttrStmt.class);
		return attrStmt != null
				&& AttributeType.NODE.equals(attrStmt.getType());
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * subgraph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of subgraph, <code>false</code> otherwise.
	 */
	// TODO: move to DotAttributes
	public static boolean isSubgraphAttribute(Attribute attribute) {
		if (isEdgeAttribute(attribute) || isNodeAttribute(attribute)) {
			return false;
		}
		// attribute nested below Subgraph
		return getAncestorOfType(attribute, Subgraph.class) != null;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * top-level graph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of a top-level graph, <code>false</code> otherwise.
	 */
	// TODO: move to DotAttributes
	public static boolean isGraphAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (isEdgeAttribute(attribute) || isNodeAttribute(attribute)
				|| isSubgraphAttribute(attribute)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of an
	 * edge. That is, it is either nested below an {@link EdgeStmtNode} or an
	 * {@link EdgeStmtSubgraph}, or used within an {@link AttrStmt} of type
	 * {@link AttributeType#EDGE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of an edge, <code>false</code> otherwise.
	 */
	// TODO: move to DotAttributes
	public static boolean isEdgeAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (getAncestorOfType(attribute, EdgeStmtNode.class) != null
				|| getAncestorOfType(attribute,
						EdgeStmtSubgraph.class) != null) {
			return true;
		}
		// global AttrStmt with AttributeType 'edge'
		AttrStmt attrStmt = getAncestorOfType(attribute, AttrStmt.class);
		return attrStmt != null
				&& AttributeType.EDGE.equals(attrStmt.getType());
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
		checkEdgeOpCorrespondsToGraphType(edgeRhsNode.getOp(),
				getAncestorOfType(edgeRhsNode, DotGraph.class).getType());
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
		checkEdgeOpCorrespondsToGraphType(edgeRhsSubgraph.getOp(),
				getAncestorOfType(edgeRhsSubgraph, DotGraph.class).getType());
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

	@SuppressWarnings("unchecked")
	private static <T extends EObject> T getAncestorOfType(EObject eObject,
			Class<T> type) {
		EObject container = eObject.eContainer();
		while (container != null
				&& !type.isAssignableFrom(container.getClass())) {
			container = container.eContainer();
		}
		return (T) container;
	}

}
