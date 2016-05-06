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
 *     Tamas Miklossy  (itemis AG) - Add support for polygon-based node shapes (bug #441352)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.gef4.dot.internal.parser.DotArrowTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotPointStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotShapeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotSplineTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotStyleStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dir.DirType;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotArrowTypeParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotPointParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotShapeParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotSplineTypeParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotStyleParser;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef4.dot.internal.parser.validation.DotArrowTypeJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotPointJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotShapeJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotSplineTypeJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotStyleJavaValidator;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Injector;

/**
 * Provides access to parsers, serializers, and validators of the respective Dot
 * language grammars.
 * 
 * @author nyssen
 *
 */
// TODO: Merge this class into DotAttributes, moving the validator fields to
// DotJavaValidator.
public class DotLanguageSupport {

	/**
	 * The parse result of an {@link IPrimitiveValueParser}, which comprises a
	 * parsed value and/or syntax errors.
	 * 
	 * @param <T>
	 *            The java equivalent of the parsed DOT value.
	 */
	public interface IPrimitiveValueParseResult<T> {

		/**
		 * Returns the parsed (primitive) object value.
		 * 
		 * @return The parsed value, or <code>null</code> if it could not be
		 *         parsed.
		 */
		public T getParsedValue();

		/**
		 * Returns the syntax errors that occurred during the parse.
		 * 
		 * @return The list of syntax errors, if any.
		 */
		public List<Diagnostic> getSyntaxErrors();

		/**
		 * Indicates whether any syntax errors occurred during the parsing.
		 * 
		 * @return <code>true</code> in case syntax errors occurred,
		 *         <code>false</code> otherwise.
		 */
		public boolean hasSyntaxErrors();

	}

	private static class PrimitiveValueParseResultImpl<T>
			implements IPrimitiveValueParseResult<T> {

		private T parsedValue;
		private List<Diagnostic> syntaxErrors;

		private PrimitiveValueParseResultImpl(T parsedValue) {
			this(parsedValue, Collections.<Diagnostic> emptyList());
		}

		private PrimitiveValueParseResultImpl(List<Diagnostic> syntaxErrors) {
			this(null, syntaxErrors);
		}

		private PrimitiveValueParseResultImpl(T parsedValue,
				List<Diagnostic> syntaxErrors) {
			this.parsedValue = parsedValue;
			this.syntaxErrors = syntaxErrors;
		}

		@Override
		public T getParsedValue() {
			return parsedValue;
		}

		@Override
		public List<Diagnostic> getSyntaxErrors() {
			return syntaxErrors;
		}

		@Override
		public boolean hasSyntaxErrors() {
			return !syntaxErrors.isEmpty();
		}
	}

	/**
	 * A parser to parse a DOT primitive value type.
	 * 
	 * @param <T>
	 *            The java equivalent of the parsed DOT value.
	 */
	public interface IPrimitiveValueParser<T> {

		/**
		 * Parses the given raw value as a DOT primitive value.
		 * 
		 * @param rawValue
		 *            The raw value to parse.
		 * @return An {@link IPrimitiveValueParseResult} indicating the parse
		 *         result.
		 */
		IPrimitiveValueParseResult<T> parse(String rawValue);
	}

	/**
	 * Parses the given value as a DOT dirType.
	 */
	public static IPrimitiveValueParser<DirType> DIRTYPE_PARSER = new IPrimitiveValueParser<DirType>() {
		@Override
		public IPrimitiveValueParseResult<DirType> parse(String rawValue) {
			if (rawValue == null) {
				return null;
			}
			for (DirType value : DirType.values()) {
				if (value.toString().equals(rawValue)) {
					return new PrimitiveValueParseResultImpl<>(value);
				}
			}
			return new PrimitiveValueParseResultImpl<>(
					Collections.<Diagnostic> singletonList(new BasicDiagnostic(
							Diagnostic.ERROR, rawValue, -1,
							"Value has to be one of "
									+ getFormattedValues(DirType.values()),
							new Object[] {})));
		}
	};

	/**
	 * Parses the given value as a DOT rankdir.
	 */
	public static IPrimitiveValueParser<Rankdir> RANKDIR_PARSER = new IPrimitiveValueParser<Rankdir>() {
		@Override
		public IPrimitiveValueParseResult<Rankdir> parse(String rawValue) {
			if (rawValue == null) {
				return null;
			}
			for (Rankdir value : Rankdir.values()) {
				if (value.toString().equals(rawValue)) {
					return new PrimitiveValueParseResultImpl<>(value);
				}
			}
			return new PrimitiveValueParseResultImpl<>(
					Collections.<Diagnostic> singletonList(
							new BasicDiagnostic(Diagnostic.ERROR, rawValue, -1,
									"The given value '" + rawValue
											+ "' has to be one of "
											+ getFormattedValues(
													Rankdir.values()),
									new Object[] {})));
		}
	};

	private static String getFormattedValues(Object[] values) {
		StringBuilder sb = new StringBuilder();
		for (Object value : values) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("'" + value.toString() + "'");
		}
		return sb.append(".").toString();
	}

	/**
	 * A parser used to parse DOT bool values.
	 */
	public static IPrimitiveValueParser<Boolean> BOOL_PARSER = new IPrimitiveValueParser<Boolean>() {

		@Override
		public IPrimitiveValueParseResult<Boolean> parse(String rawValue) {
			if (rawValue == null) {
				return null;
			}
			// case insensitive "true" or "yes"
			if (Boolean.TRUE.toString().equalsIgnoreCase(rawValue)
					|| "yes".equalsIgnoreCase(rawValue)) {
				return new PrimitiveValueParseResultImpl<>(Boolean.TRUE);
			}
			// case insensitive "false" or "no"
			if (Boolean.FALSE.toString().equalsIgnoreCase(rawValue)
					|| "no".equalsIgnoreCase(rawValue)) {
				return new PrimitiveValueParseResultImpl<>(Boolean.FALSE);
			}
			// an integer value
			try {
				int parsedValue = Integer.parseInt(rawValue);
				return new PrimitiveValueParseResultImpl<>(
						parsedValue > 0 ? Boolean.TRUE : Boolean.FALSE);
			} catch (NumberFormatException e) {
				return new PrimitiveValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								"The given value '" + rawValue
										+ "' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value",
								new Object[] {})));
			}
		}
	};

	/**
	 * A parser used to parse DOT double values.
	 */
	public static IPrimitiveValueParser<Double> DOUBLE_PARSER = new IPrimitiveValueParser<Double>() {

		@Override
		public IPrimitiveValueParseResult<Double> parse(String rawValue) {
			if (rawValue == null) {
				return null;
			}
			try {
				// TODO: check that this resembles the DOT double interpretation
				double parsedValue = Double.parseDouble(rawValue);
				return new PrimitiveValueParseResultImpl<>(
						new Double(parsedValue));
			} catch (NumberFormatException exception) {
				return new PrimitiveValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								exception.getMessage(), new Object[] {})));
			}
		}
	};

	/**
	 * A parser used to parse DOT int values.
	 */
	public static IPrimitiveValueParser<Integer> INT_PARSER = new IPrimitiveValueParser<Integer>() {

		@Override
		public IPrimitiveValueParseResult<Integer> parse(String rawValue) {
			if (rawValue == null) {
				return null;
			}
			try {
				int parsedValue = Integer.parseInt(rawValue);
				return new PrimitiveValueParseResultImpl<>(
						new Integer(parsedValue));
			} catch (NumberFormatException exception) {
				return new PrimitiveValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								exception.getMessage(), new Object[] {})));
			}
		}
	};

	private static final Injector arrowTypeInjector = new DotArrowTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The validator for arrowtype attribute values.
	 */
	// TODO: move to dotjavaValidator
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

	private static final Injector shapeInjector = new DotShapeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The validator for shape attribute values.
	 */
	// TODO: move to dotjavaValidator
	public static final DotShapeJavaValidator SHAPE_VALIDATOR = shapeInjector
			.getInstance(DotShapeJavaValidator.class);

	/**
	 * The parser for shape attribute values.
	 */
	public static final DotShapeParser SHAPE_PARSER = shapeInjector
			.getInstance(DotShapeParser.class);

	/**
	 * The serializer for shape attribute values.
	 */
	public static final ISerializer SHAPE_SERIALIZER = shapeInjector
			.getInstance(ISerializer.class);

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

	private static final Injector styleInjector = new DotStyleStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The serializer for style attribute values.
	 */
	public static final ISerializer STYLE_SERIALIZER = styleInjector
			.getInstance(ISerializer.class);

	/**
	 * The validator for style attribute values.
	 */
	// TODO: move to DotJavaValidator
	public static final DotStyleJavaValidator STYLE_VALIDATOR = styleInjector
			.getInstance(DotStyleJavaValidator.class);

	/**
	 * The parser for style attribute values.
	 */
	public static final DotStyleParser STYLE_PARSER = styleInjector
			.getInstance(DotStyleParser.class);

	/**
	 * The validator for splinetype attribute values.
	 */
	// TODO: move to DotJavaValidator
	public static final DotSplineTypeJavaValidator SPLINETYPE_VALIDATOR = splineTypeInjector
			.getInstance(DotSplineTypeJavaValidator.class);

	/**
	 * Parses the given (unquoted) attribute, using the given
	 * {@link IPrimitiveValueParser}.
	 * 
	 * @param <T>
	 *            The (primitive) object type of the parsed value.
	 * @param parser
	 *            The parser to be used for parsing.
	 * @param attributeValue
	 *            The (unquoted) attribute value that is to be parsed.
	 * @return The parsed value, or <code>null</code> if the value could not be
	 *         parsed.
	 */
	public static <T> T parseAttributeValue(IPrimitiveValueParser<T> parser,
			String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		IPrimitiveValueParseResult<T> parsedAttributeValue = parser
				.parse(attributeValue);
		return parsedAttributeValue.getParsedValue();
	}

	/**
	 * Parses the given (unquoted) attribute, using the given {@link IParser}.
	 * 
	 * @param <T>
	 *            The type of the parsed value.
	 * @param parser
	 *            The parser to be used for parsing.
	 * @param attributeValue
	 *            The (unquoted) attribute value that is to be parsed.
	 * @return The parsed value, or <code>null</code> if the value could not be
	 *         parsed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseAttributeValue(IParser parser,
			String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		IParseResult parsedAttributeValue = parser
				.parse(new StringReader(attributeValue));
		return (T) parsedAttributeValue.getRootASTElement();
	}
}
