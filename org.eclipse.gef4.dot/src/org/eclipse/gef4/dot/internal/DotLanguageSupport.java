/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.dot.internal.parser.DotArrowTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotPointStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotSplineTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dot.DotPackage;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotArrowTypeParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotPointParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotSplineTypeParser;
import org.eclipse.gef4.dot.internal.parser.validation.DotArrowTypeJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotPointJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotSplineTypeJavaValidator;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.AbstractInjectableValidator;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.FeatureBasedDiagnostic;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Injector;

/**
 * Provides access to parsers, serializers, and validators of the respective Dot
 * language grammars.
 * 
 * @author nyssen
 *
 */
// TODO: This class should remain internal, even when exposing the rest of the
// API.
public class DotLanguageSupport {

	private static final Injector arrowTypeInjector = new DotArrowTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The validator for arrowtype attribute values.
	 */
	public static final DotArrowTypeJavaValidator ARROWTYPE_VALIDATOR = arrowTypeInjector
			.getInstance(DotArrowTypeJavaValidator.class);

	/**
	 * The parser for arrowtype attribute values.
	 */
	public static final DotArrowTypeParser ARROWTYPE_PARSER = arrowTypeInjector
			.getInstance(DotArrowTypeParser.class);

	/**
	 * The serializer for arrowtype attribute values.
	 */
	public static final ISerializer ARROWTYPE_SERIALIZER = arrowTypeInjector
			.getInstance(ISerializer.class);

	private static final Injector pointInjector = new DotPointStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for point attribute values.
	 */
	public static final DotPointParser POINT_PARSER = pointInjector
			.getInstance(DotPointParser.class);

	/**
	 * The serializer for point attribute values.
	 */
	public static final ISerializer POINT_SERIALIZER = pointInjector
			.getInstance(ISerializer.class);

	/**
	 * The validator for point attribute values.
	 */
	public static final DotPointJavaValidator POINT_VALIDATOR = pointInjector
			.getInstance(DotPointJavaValidator.class);

	private static final Injector splineTypeInjector = new DotSplineTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for splinetype attribute values.
	 */
	public static final DotSplineTypeParser SPLINETYPE_PARSER = splineTypeInjector
			.getInstance(DotSplineTypeParser.class);

	/**
	 * The serializer for splinetype attribute values.
	 */
	public static final ISerializer SPLINETYPE_SERIALIZER = splineTypeInjector
			.getInstance(ISerializer.class);

	/**
	 * The validator for splinetype attribute values.
	 */
	public static final DotSplineTypeJavaValidator SPLINETYPE_VALIDATOR = splineTypeInjector
			.getInstance(DotSplineTypeJavaValidator.class);

	private static Diagnostic createSemanticAttributeValueProblem(int severity,
			String attributeValue, String attributeTypeName,
			String validatorMessage, String issueCode) {
		return new FeatureBasedDiagnostic(severity,
				"The " + attributeTypeName + " value '" + attributeValue
						+ "' is not semantically correct: " + validatorMessage,
				null /* current object */, DotPackage.Literals.ATTRIBUTE__VALUE,
				ValidationMessageAcceptor.INSIGNIFICANT_INDEX, CheckType.NORMAL,
				issueCode, attributeValue);
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

	private static String getFormattedSyntaxErrorMessages(
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

	private static String getFormattedValues(Set<String> values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("'" + value + "'");
		}
		return sb.append(".").toString();
	}

	public static Boolean parseBooleanAttributeValue(String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		// case insensitive "true" or "yes"
		if (Boolean.TRUE.toString().equalsIgnoreCase(attributeValue)
				|| "yes".equalsIgnoreCase(attributeValue)) {
			return Boolean.TRUE;
		}
		// case insensitive "false" or "no"
		if (Boolean.FALSE.toString().equalsIgnoreCase(attributeValue)
				|| "no".equalsIgnoreCase(attributeValue)) {
			return Boolean.FALSE;
		}
		// an integer value
		try {
			int parsedValue = Integer.parseInt(attributeValue);
			return parsedValue > 0 ? Boolean.TRUE : Boolean.FALSE;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double parseDoubleAttributeValue(String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		Double parsedAttributeValue;
		try {
			// TODO: use specific parser that sticks strictly to DOT double
			parsedAttributeValue = Double.parseDouble(attributeValue);
		} catch (NumberFormatException exception) {
			return null;
		}
		return parsedAttributeValue;
	}

	public static <T> T parseObjectAttributeValue(IParser parser,
			String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		IParseResult parsedAttributeValue = parser
				.parse(new StringReader(attributeValue));
		return (T) parsedAttributeValue.getRootASTElement();
	}

	public static List<Diagnostic> validateBooleanAttributeValue(
			final String attributeName, String attributeValue) {
		// parse value
		if ("true".equalsIgnoreCase(attributeValue)
				|| "yes".equalsIgnoreCase(attributeValue)) {
			return Collections.emptyList();
		} else if ("false".equalsIgnoreCase(attributeValue)
				|| "no".equalsIgnoreCase(attributeValue)) {
			return Collections.emptyList();
		} else {
			try {
				// valid boolean value
				Integer.parseInt(attributeValue);
				return Collections.emptyList();
			} catch (NumberFormatException e) {
				return Collections.<Diagnostic> singletonList(
						createSyntacticAttributeValueProblem(attributeValue,
								"boolean", e.getMessage() + ".",
								attributeName));
			}
		}
	}

	public static List<Diagnostic> validateDoubleAttributeValue(
			final String attributeName, String attributeValue,
			double minValue) {
		// parse value
		double parsedValue;
		try {
			parsedValue = Double.parseDouble(attributeValue);
		} catch (NumberFormatException e) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							"double", e.getMessage() + ".", attributeName));
		}
		// validate value
		if (parsedValue < minValue) {
			return Collections.<Diagnostic> singletonList(
					createSemanticAttributeValueProblem(Diagnostic.ERROR,
							attributeValue, "double",
							"Value may not be smaller than " + minValue + ".",
							attributeName));
		}
		return Collections.emptyList();
	}

	public static List<Diagnostic> validateEnumAttributeValue(
			final String attributeName, String attributeValue,
			String attributeTypeName, Set<String> validValues) {
		if (!validValues.contains(attributeValue)) {
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							attributeTypeName,
							"Value has to be one of "
									+ getFormattedValues(validValues),
							attributeName));
		} else {
			return Collections.emptyList();
		}
	}

	public static List<Diagnostic> validateObjectAttributeValue(
			final IParser parser, final AbstractDeclarativeValidator validator,
			final String attributeName, final String attributeValue,
			final EClass attributeType) {
		// ensure we always use the unquoted value
		IParseResult parseResult = parser
				.parse(new StringReader(attributeValue));
		if (parseResult.hasSyntaxErrors()) {
			// handle syntactical problems
			return Collections.<Diagnostic> singletonList(
					createSyntacticAttributeValueProblem(attributeValue,
							attributeType.getName().toLowerCase(),
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
								attributeType.getName().toLowerCase(), message,
								attributeName));
					}

					@Override
					public void acceptError(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.ERROR, attributeValue,
								attributeType.getName().toLowerCase(), message,
								attributeName));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.INFO, attributeValue,
								attributeType.getName().toLowerCase(), message,
								attributeName));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.INFO, attributeValue,
								attributeType.getName().toLowerCase(), message,
								attributeName));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.WARNING, attributeValue,
								attributeType.getName().toLowerCase(), message,
								attributeName));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(createSemanticAttributeValueProblem(
								Diagnostic.WARNING, attributeValue,
								attributeType.getName().toLowerCase(), message,
								attributeName));
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
}
