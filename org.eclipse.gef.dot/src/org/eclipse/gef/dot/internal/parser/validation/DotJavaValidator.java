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

package org.eclipse.gef.dot.internal.parser.validation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.AttributeContext;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.DotLanguageSupport;
import org.eclipse.gef.dot.internal.DotLanguageSupport.IPrimitiveValueParseResult;
import org.eclipse.gef.dot.internal.DotLanguageSupport.IPrimitiveValueParser;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowtypePackage;
import org.eclipse.gef.dot.internal.parser.color.ColorPackage;
import org.eclipse.gef.dot.internal.parser.color.DotColors;
import org.eclipse.gef.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef.dot.internal.parser.dot.AttrList;
import org.eclipse.gef.dot.internal.parser.dot.AttrStmt;
import org.eclipse.gef.dot.internal.parser.dot.Attribute;
import org.eclipse.gef.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef.dot.internal.parser.dot.DotPackage;
import org.eclipse.gef.dot.internal.parser.dot.EdgeOp;
import org.eclipse.gef.dot.internal.parser.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.parser.dot.EdgeRhsSubgraph;
import org.eclipse.gef.dot.internal.parser.dot.GraphType;
import org.eclipse.gef.dot.internal.parser.dot.NodeStmt;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.point.PointPackage;
import org.eclipse.gef.dot.internal.parser.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.parser.shape.ShapePackage;
import org.eclipse.gef.dot.internal.parser.splines.Splines;
import org.eclipse.gef.dot.internal.parser.splinetype.SplinetypePackage;
import org.eclipse.gef.dot.internal.parser.style.EdgeStyle;
import org.eclipse.gef.dot.internal.parser.style.NodeStyle;
import org.eclipse.gef.dot.internal.parser.style.Style;
import org.eclipse.gef.dot.internal.parser.style.StyleItem;
import org.eclipse.gef.dot.internal.parser.style.StylePackage;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.AbstractInjectableValidator;
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
		List<Diagnostic> diagnostics = validateAttributeValue(
				DotAttributes.getContext(attribute), attribute.getName(),
				attribute.getValue());
		for (Diagnostic d : diagnostics) {
			if (d.getSeverity() == Diagnostic.ERROR) {
				getMessageAcceptor().acceptError(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attribute.getName(),
						attribute.getValue());
			} else if (d.getSeverity() == Diagnostic.WARNING) {
				getMessageAcceptor().acceptWarning(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attribute.getName(),
						attribute.getValue());
			} else if (d.getSeverity() == Diagnostic.INFO) {
				getMessageAcceptor().acceptInfo(d.getMessage(), attribute,
						DotPackage.Literals.ATTRIBUTE__VALUE,
						INSIGNIFICANT_INDEX, attribute.getName(),
						attribute.getValue());
			}
		}
	}

	/**
	 * Validate the attribute determined via name and value syntactically and
	 * semantically.
	 * 
	 * @param context
	 *            The context element the attribute is related to.
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute (may be quoted).
	 * @return A list of {@link Diagnostic} objects representing the identified
	 *         issues, or an empty list if no issues were found.
	 */
	public List<Diagnostic> validateAttributeValue(
			final AttributeContext context, final String name,
			final String value) {
		// if quoted, we need to unquote the value before parsing it
		String unquotedValue = DotTerminalConverters.unquote(value);

		// use parser (and validator) for respective attribute type
		if (DotAttributes.FORCELABELS__G.equals(name)) {
			return validateBooleanAttributeValue(DotAttributes.FORCELABELS__G,
					unquotedValue);
		} else if (DotAttributes.FIXEDSIZE__N.equals(name)) {
			return validateBooleanAttributeValue(DotAttributes.FIXEDSIZE__N,
					unquotedValue);
		} else if (DotAttributes.CLUSTERRANK__G.equals(name)) {
			return validateEnumAttributeValue(
					DotLanguageSupport.CLUSTERMODE_PARSER, name, unquotedValue,
					"clusterMode");
		} else if (DotAttributes.OUTPUTORDER__G.equals(name)) {
			return validateEnumAttributeValue(
					DotLanguageSupport.OUTPUTMODE_PARSER, name, unquotedValue,
					"outputMode");
		} else if (DotAttributes.PAGEDIR__G.equals(name)) {
			return validateEnumAttributeValue(DotLanguageSupport.PAGEDIR_PARSER,
					name, unquotedValue, "pagedir");
		} else if (DotAttributes.RANKDIR__G.equals(name)) {
			return validateEnumAttributeValue(DotLanguageSupport.RANKDIR_PARSER,
					name, unquotedValue, "rankdir");
		} else if (DotAttributes.SPLINES__G.equals(name)) {
			// XXX: splines can either be an enum or a bool value; we try both
			// options here
			List<Diagnostic> booleanCaseFindings = validateBooleanAttributeValue(
					name, unquotedValue);
			List<Diagnostic> stringCaseFindings = validateStringAttributeValue(
					name, unquotedValue, "splines string", Splines.values());
			if (booleanCaseFindings.isEmpty() || stringCaseFindings.isEmpty()) {
				return Collections.emptyList();
			} else {
				// TODO: create a better, combined error message here
				List<Diagnostic> combinedFindings = new ArrayList<>();
				combinedFindings.addAll(booleanCaseFindings);
				combinedFindings.addAll(stringCaseFindings);
				return combinedFindings;
			}
		} else if (DotAttributes.LAYOUT__G.equals(name)) {
			return validateStringAttributeValue(name, unquotedValue, "layout",
					Layout.values());
		} else if (DotAttributes.DIR__E.equals(name)) {
			// dirType enum
			return validateEnumAttributeValue(DotLanguageSupport.DIRTYPE_PARSER,
					name, unquotedValue, "dirType");
		} else if (DotAttributes.ARROWHEAD__E.equals(name)
				|| DotAttributes.ARROWTAIL__E.equals(name)) {
			// validate arrowtype using delegate parser and validator
			return validateObjectAttributeValue(
					DotLanguageSupport.ARROWTYPE_PARSER,
					DotLanguageSupport.ARROWTYPE_VALIDATOR, name, unquotedValue,
					ArrowtypePackage.Literals.ARROW_TYPE, "arrowType");
		} else if (DotAttributes.ARROWSIZE__E.equals(name)) {
			return validateDoubleAttributeValue(name, unquotedValue, 0.0);
		} else if (DotAttributes.POS__NE.equals(name)) {
			// validate point (node) or splinetype (edge) using delegate parser
			// and validator
			if (AttributeContext.NODE.equals(context)) {
				return validateObjectAttributeValue(
						DotLanguageSupport.POINT_PARSER,
						DotLanguageSupport.POINT_VALIDATOR, name, unquotedValue,
						PointPackage.Literals.POINT, "point");
			} else if (AttributeContext.EDGE.equals(context)) {
				return validateObjectAttributeValue(
						DotLanguageSupport.SPLINETYPE_PARSER,
						DotLanguageSupport.SPLINETYPE_VALIDATOR, name,
						unquotedValue, SplinetypePackage.Literals.SPLINE_TYPE,
						"splineType");
			}
		} else if (DotAttributes.SHAPE__N.equals(name)) {
			// validate shape using delegate parser and validator
			return validateObjectAttributeValue(DotLanguageSupport.SHAPE_PARSER,
					DotLanguageSupport.SHAPE_VALIDATOR, name, unquotedValue,
					ShapePackage.Literals.SHAPE, "shape");
		} else if (DotAttributes.SIDES__N.equals(name)) {
			return validateIntAttributeValue(name, unquotedValue, 0);
		} else if (DotAttributes.SKEW__N.equals(name)) {
			return validateDoubleAttributeValue(name, unquotedValue, -100.0);
		} else if (DotAttributes.DISTORTION__N.equals(name)) {
			return validateDoubleAttributeValue(name, unquotedValue, -100.0);
		} else if (DotAttributes.WIDTH__N.equals(name)) {
			return validateDoubleAttributeValue(name, unquotedValue, 0.01);
		} else if (DotAttributes.HEIGHT__N.equals(name)) {
			return validateDoubleAttributeValue(name, unquotedValue, 0.02);
		} else if (DotAttributes.STYLE__GNE.equals(name)
				&& !unquotedValue.isEmpty()) {
			// validate style using delegate parser and validator
			List<Diagnostic> grammarFindings = validateObjectAttributeValue(
					DotLanguageSupport.STYLE_PARSER,
					DotLanguageSupport.STYLE_VALIDATOR, name, unquotedValue,
					StylePackage.Literals.STYLE, "style");
			if (!grammarFindings.isEmpty()) {
				return grammarFindings;
			}
			// validate according to the corresponding NodeStyle/EdgeStyle enums
			IParseResult parseResult = DotLanguageSupport.STYLE_PARSER
					.parse(new StringReader(unquotedValue));
			Style style = (Style) parseResult.getRootASTElement();

			List<Diagnostic> findings = new ArrayList<>();
			if (AttributeContext.NODE.equals(context)) {
				// check each style item with the corresponding parser
				for (StyleItem styleItem : style.getStyleItems()) {
					findings.addAll(validateStringAttributeValue(name,
							styleItem.getName(), "style", NodeStyle.values()));
				}
			} else if (AttributeContext.EDGE.equals(context)) {
				// check each style item with the corresponding parser
				for (StyleItem styleItem : style.getStyleItems()) {
					findings.addAll(validateStringAttributeValue(name,
							styleItem.getName(), "style", EdgeStyle.values()));
				}
			}
			return findings;
		} else if (DotAttributes.HEAD_LP__E.equals(name)
				|| DotAttributes.LP__GE.equals(name)
				|| DotAttributes.TAIL_LP__E.equals(name)
				|| DotAttributes.XLP__NE.equals(name)) {
			return validateObjectAttributeValue(DotLanguageSupport.POINT_PARSER,
					DotLanguageSupport.POINT_VALIDATOR, name, unquotedValue,
					PointPackage.Literals.POINT, "point");
		} else if (DotAttributes.BGCOLOR__G.equals(name)
				|| DotAttributes.COLOR__NE.equals(name)
				|| DotAttributes.FILLCOLOR__NE.equals(name)
				|| DotAttributes.FONTCOLOR__GNE.equals(name)
				|| DotAttributes.LABELFONTCOLOR__E.equals(name)) {
			return validateObjectAttributeValue(DotLanguageSupport.COLOR_PARSER,
					DotLanguageSupport.COLOR_VALIDATOR, name, unquotedValue,
					ColorPackage.Literals.COLOR, "color");
		} else if (DotAttributes.COLORSCHEME__GNE.equals(name)) {
			return validateStringAttributeValue(name, unquotedValue,
					DotAttributes.COLORSCHEME__GNE,
					DotColors.getColorSchemes().toArray());
		}
		return Collections.emptyList();

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
		if (DotAttributes.isNodeAttribute(attribute)
				&& attribute.getName().equals(DotAttributes.STYLE__GNE)
				&& attribute.getValue().equals(NodeStyle.STRIPED.toString())) {
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
				String shapeValue = DotImport.getAttributeValue(attributeList,
						DotAttributes.SHAPE__N);
				if (shapeValue != null) {
					switch (PolygonBasedNodeShape.get(shapeValue)) {
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

	private List<Diagnostic> validateBooleanAttributeValue(
			final String attributeName, String attributeValue) {
		// parse value
		IPrimitiveValueParseResult<Boolean> parseResult = DotLanguageSupport.BOOL_PARSER
				.parse(attributeValue);
		if (parseResult.hasSyntaxErrors()) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue, "bool",
							getFormattedSyntaxErrorMessages(parseResult),
							attributeName));
		}
		// no semantic validation
		return Collections.emptyList();
	}

	private static Diagnostic createSyntacticAttributeValueProblem(
			String attributeValue, String attributeTypeName,
			String parserMessage, String issueCode) {
		return new FeatureBasedDiagnostic(Diagnostic.ERROR,
				"The value '" + attributeValue
						+ "' is not a syntactically correct "
						+ attributeTypeName + ": " + parserMessage,
				null /* current object */, DotPackage.Literals.ATTRIBUTE__VALUE,
				ValidationMessageAcceptor.INSIGNIFICANT_INDEX, CheckType.NORMAL,
				issueCode, attributeValue);
	}

	private String getFormattedSyntaxErrorMessages(IParseResult parseResult) {
		StringBuilder sb = new StringBuilder();
		for (INode n : parseResult.getSyntaxErrors()) {
			String message = n.getSyntaxErrorMessage().getMessage();
			if (!message.isEmpty()) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(message.substring(0, 1).toUpperCase()
						+ message.substring(1)
						+ (message.endsWith(".") ? "" : "."));
			}
		}
		return sb.toString();
	}

	private String getFormattedSyntaxErrorMessages(
			IPrimitiveValueParseResult<?> parseResult) {
		StringBuilder sb = new StringBuilder();
		for (Diagnostic d : parseResult.getSyntaxErrors()) {
			String message = d.getMessage();
			if (!message.isEmpty()) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(message.substring(0, 1).toUpperCase()
						+ message.substring(1)
						+ (message.endsWith(".") ? "" : "."));
			}
		}
		return sb.toString();
	}

	private List<Diagnostic> validateDoubleAttributeValue(
			final String attributeName, String attributeValue,
			double minValue) {
		// parse value
		IPrimitiveValueParseResult<Double> parseResult = DotLanguageSupport.DOUBLE_PARSER
				.parse(attributeValue);
		if (parseResult.hasSyntaxErrors()) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							"double",
							getFormattedSyntaxErrorMessages(parseResult),
							attributeName));
		} else {
			// validate value
			if (parseResult.getParsedValue().doubleValue() < minValue) {
				return Collections
						.<Diagnostic> singletonList(
								createSemanticAttributeValueProblem(
										Diagnostic.ERROR, attributeValue,
										"double",
										"Value may not be smaller than "
												+ minValue + ".",
										attributeName));
			}
			return Collections.emptyList();
		}
	}

	private List<Diagnostic> validateIntAttributeValue(
			final String attributeName, String attributeValue, int minValue) {
		// parse value
		IPrimitiveValueParseResult<Integer> parseResult = DotLanguageSupport.INT_PARSER
				.parse(attributeValue);
		if (parseResult.hasSyntaxErrors()) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue, "int",
							getFormattedSyntaxErrorMessages(parseResult),
							attributeName));
		} else {
			// validate value
			if (parseResult.getParsedValue().intValue() < minValue) {
				return Collections
						.<Diagnostic> singletonList(
								createSemanticAttributeValueProblem(
										Diagnostic.ERROR, attributeValue, "int",
										"Value may not be smaller than "
												+ minValue + ".",
										attributeName));
			}
			return Collections.emptyList();
		}
	}

	private String getFormattedValues(Object[] values) {
		StringBuilder sb = new StringBuilder();
		for (Object value : new TreeSet<>(Arrays.asList(values))) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("'" + value + "'");
		}
		return sb.toString();
	}

	private List<Diagnostic> validateEnumAttributeValue(
			final IPrimitiveValueParser<?> parser, final String attributeName,
			String attributeValue, String attributeTypeName) {
		IPrimitiveValueParseResult<?> parseResult = parser
				.parse(attributeValue);
		if (parseResult.hasSyntaxErrors()) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							attributeTypeName,
							getFormattedSyntaxErrorMessages(parseResult),
							attributeName));
		}
		// no semantic validation
		return Collections.emptyList();
	}

	private List<Diagnostic> validateStringAttributeValue(
			final String attributeName, String attributeValue,
			String attributeTypeName, Object[] validValues) {
		for (Object validValue : validValues) {
			if (validValue.toString().equals(attributeValue)) {
				return Collections.emptyList();
			}
		}
		// TODO: we should probably only issue a warning here
		return Collections
				.<Diagnostic> singletonList(
						createSemanticAttributeValueProblem(Diagnostic.ERROR,
								attributeValue, attributeTypeName,
								"Value should be one of "
										+ getFormattedValues(validValues) + ".",
								null));
	}

	private List<Diagnostic> validateObjectAttributeValue(final IParser parser,
			final AbstractDeclarativeValidator validator,
			final String attributeName, final String attributeValue,
			final EClass attributeType, final String attributeTypeName) {
		// ensure we always use the unquoted value
		IParseResult parseResult = parser
				.parse(new StringReader(attributeValue));
		if (parseResult.hasSyntaxErrors()) {
			// handle syntactical problems
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							attributeTypeName,
							getFormattedSyntaxErrorMessages(parseResult),
							attributeName));
		} else {
			// handle semantical problems
			final List<Diagnostic> diagnostics = new ArrayList<>();
			// validation is optional; if validator is provided, check for
			// semantic problems using it
			if (validator != null) {
				// we need a specific message acceptor
				validator.setMessageAcceptor(new ValidationMessageAcceptor() {

					@Override
					public void acceptError(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.ERROR, attributeValue,
								attributeTypeName, message, attributeName));
					}

					@Override
					public void acceptError(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.ERROR, attributeValue,
								attributeTypeName, message, attributeName));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.INFO, attributeValue,
								attributeTypeName, message, attributeName));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.INFO, attributeValue,
								attributeTypeName, message, attributeName));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.WARNING, attributeValue,
								attributeTypeName, message, attributeName));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.WARNING, attributeValue,
								attributeTypeName, message, attributeName));
					}
				});

				Map<Object, Object> context = new HashMap<>();
				context.put(AbstractInjectableValidator.CURRENT_LANGUAGE_NAME,
						ReflectionUtils.getPrivateFieldValue(validator,
								"languageName"));

				EObject root = parseResult.getRootASTElement();
				// validate the root element...
				validator.validate(attributeType, root,
						null /* diagnostic chain */, context);

				// ...and all its children
				for (Iterator<EObject> iterator = EcoreUtil
						.getAllProperContents(root, true); iterator
								.hasNext();) {
					validator.validate(attributeType, iterator.next(),
							null /* diagnostic chain */, context);
				}
			}
			return diagnostics;
		}
	}

	private Diagnostic createSemanticAttributeValueProblem(int severity,
			String attributeValue, String attributeTypeName,
			String validatorMessage, String issueCode) {
		return new FeatureBasedDiagnostic(severity,
				"The " + attributeTypeName + " value '" + attributeValue
						+ "' is not semantically correct: " + validatorMessage,
				null /* current object */, DotPackage.Literals.ATTRIBUTE__VALUE,
				ValidationMessageAcceptor.INSIGNIFICANT_INDEX, CheckType.NORMAL,
				issueCode, attributeValue);
	}

}
