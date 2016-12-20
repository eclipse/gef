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
 *                                 - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.gef.dot.internal.language.DotArrowTypeStandaloneSetup;
import org.eclipse.gef.dot.internal.language.DotColorStandaloneSetup;
import org.eclipse.gef.dot.internal.language.DotPointStandaloneSetup;
import org.eclipse.gef.dot.internal.language.DotShapeStandaloneSetup;
import org.eclipse.gef.dot.internal.language.DotSplineTypeStandaloneSetup;
import org.eclipse.gef.dot.internal.language.DotStyleStandaloneSetup;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.AttrStmt;
import org.eclipse.gef.dot.internal.language.dot.AttributeType;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtSubgraph;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Subgraph;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.point.Point;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.shape.Shape;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.splinetype.SplineType;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.dot.internal.language.validation.DotArrowTypeJavaValidator;
import org.eclipse.gef.dot.internal.language.validation.DotColorJavaValidator;
import org.eclipse.gef.dot.internal.language.validation.DotPointJavaValidator;
import org.eclipse.gef.dot.internal.language.validation.DotShapeJavaValidator;
import org.eclipse.gef.dot.internal.language.validation.DotSplineTypeJavaValidator;
import org.eclipse.gef.dot.internal.language.validation.DotStyleJavaValidator;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.AbstractInjectableValidator;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Injector;

/**
 * Provides access to parsers, serializers, and validators of the respective Dot
 * language grammars.
 * 
 * @author nyssen
 *
 */
// TODO: Move the contents of this class into DotAttributes, move the validator
// of DotAttributes and the parser of DotImport here. This class should be
// statically injected.
public class DotLanguageSupport {

	/**
	 * Contexts by which attributes may be used.
	 */
	public static enum Context {
		/**
		 * Graph context
		 */
		GRAPH,
		/**
		 * Edge context
		 */
		EDGE,
		/**
		 * Node context
		 */
		NODE,
		/**
		 * Subgraph context
		 */
		SUBGRAPH,
		/**
		 * Cluster context
		 */
		CLUSTER
	}

	private static class AttributeValueParseResultImpl<T>
			implements IAttributeValueParser.IParseResult<T> {

		private T parsedValue;
		private List<Diagnostic> syntaxErrors;

		private AttributeValueParseResultImpl(T parsedValue) {
			this(parsedValue, Collections.<Diagnostic> emptyList());
		}

		private AttributeValueParseResultImpl(List<Diagnostic> syntaxErrors) {
			this(null, syntaxErrors);
		}

		private AttributeValueParseResultImpl(T parsedValue,
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
	public interface IAttributeValueParser<T> {

		/**
		 * The parse result of an {@link IAttributeValueParser}, which comprises
		 * a parsed value and/or syntax errors.
		 * 
		 * @param <T>
		 *            The java equivalent of the parsed DOT value.
		 */
		public interface IParseResult<T> {

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

		/**
		 * Parses the given raw value as a DOT primitive value.
		 * 
		 * @param attributeValue
		 *            The raw value to parse.
		 * @return An {@link IParseResult} indicating the parse result.
		 */
		IParseResult<T> parse(String attributeValue);
	}

	/**
	 * A validator for attribute values
	 * 
	 * @param <T>
	 *            The type of the attribute.
	 */
	public interface IAttributeValueValidator<T> {

		/**
		 * Validates the given attribute value.
		 * 
		 * @param attributeValue
		 *            The value to validate.
		 * @return A list of {@link Diagnostic}s that represent the validation
		 *         result.
		 */
		public List<Diagnostic> validate(T attributeValue);
	}

	/**
	 * A parser to parse a DOT primitive value type.
	 * 
	 * @param <T>
	 *            The java equivalent of the parsed DOT value.
	 */
	public interface IAttributeValueSerializer<T> {

		/**
		 * Serializes the given value.
		 * 
		 * @param value
		 *            The value to serialize.
		 * @return The string representations, to which the value was
		 *         serialized.
		 */
		String serialize(T value);
	}

	/**
	 * A generic {@link IAttributeValueParser} for enumeration values.
	 * 
	 * @param <E>
	 *            The type of enumeration to parse.
	 */
	private static class EnumValueParser<E extends Enum<E>>
			implements IAttributeValueParser<E> {

		private Class<E> definition;

		/**
		 * Creates a new parser for the given enumeration definition
		 * 
		 * @param attributeType
		 *            The enumeration class.
		 */
		public EnumValueParser(Class<E> attributeType) {
			this.definition = attributeType;
		}

		@Override
		public IParseResult<E> parse(String attributeValue) {
			if (attributeValue == null) {
				return null;
			}
			for (E value : definition.getEnumConstants()) {
				if (value.toString().equals(attributeValue)) {
					return new AttributeValueParseResultImpl<>(value);
				}
			}
			return new AttributeValueParseResultImpl<>(
					Collections.<Diagnostic> singletonList(new BasicDiagnostic(
							Diagnostic.ERROR, attributeValue, -1,
							"Value has to be one of " + getFormattedValues(
									definition.getEnumConstants()),
							new Object[] {})));
		}
	}

	/**
	 * A generic {@link IAttributeValueSerializer} for enumeration values.
	 * 
	 * @param <E>
	 *            The type of enumeration to serialize.
	 */
	private static class EnumValueSerializer<E extends Enum<E>>
			implements IAttributeValueSerializer<E> {

		private Class<E> definition;

		/**
		 * Creates a new serializer for the given enumeration definition
		 * 
		 * @param definition
		 *            The enumeration class.
		 */
		public EnumValueSerializer(Class<E> definition) {
			this.definition = definition;
		}

		@Override
		public String serialize(E value) {
			if (!definition.isAssignableFrom(value.getClass())) {
				throw new IllegalArgumentException(
						"Value does not comply to definition " + definition);
			}
			return value.toString();
		}
	}

	private static class XtextDelegateValueParser<T extends EObject>
			implements IAttributeValueParser<T> {

		private Injector injector;
		private IParser xtextParser;

		public XtextDelegateValueParser(Injector injector) {
			this.injector = injector;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IParseResult<T> parse(String attributeValue) {
			IParser xtextParser = getParser();
			org.eclipse.xtext.parser.IParseResult xtextParseResult = xtextParser
					.parse(new StringReader(attributeValue));
			if (xtextParseResult.hasSyntaxErrors()) {
				List<Diagnostic> syntaxProblems = new ArrayList<>();
				for (INode xtextSyntaxError : xtextParseResult
						.getSyntaxErrors()) {
					syntaxProblems.add(new BasicDiagnostic(Diagnostic.ERROR,
							attributeValue, -1, xtextSyntaxError
									.getSyntaxErrorMessage().getMessage(),
							new Object[] {}));
				}
				return new AttributeValueParseResultImpl<>(syntaxProblems);
			}
			return new AttributeValueParseResultImpl<>(
					(T) xtextParseResult.getRootASTElement());
		}

		protected IParser getParser() {
			if (xtextParser == null) {
				xtextParser = injector.getInstance(IParser.class);
			}
			return xtextParser;
		}
	}

	private static class XtextDelegateValueSerializer<T extends EObject>
			implements IAttributeValueSerializer<T> {

		private Injector injector;
		private ISerializer serializer;

		public XtextDelegateValueSerializer(Injector injector) {
			this.injector = injector;
		}

		@Override
		public String serialize(T value) {
			ISerializer serializer = getSerializer();
			return serializer.serialize(value);
		}

		protected ISerializer getSerializer() {
			if (serializer == null) {
				serializer = injector.getInstance(ISerializer.class);
			}
			return serializer;
		}
	}

	private static class XtextDelegateValueValidator<T extends EObject>
			implements IAttributeValueValidator<T> {

		private Injector injector;
		private Class<? extends AbstractDeclarativeValidator> validatorClass;
		private AbstractDeclarativeValidator validator;

		public XtextDelegateValueValidator(Injector injector,
				Class<? extends AbstractDeclarativeValidator> validatorClass) {
			this.injector = injector;
			this.validatorClass = validatorClass;
		}

		protected AbstractDeclarativeValidator getValidator() {
			if (validator == null) {
				validator = injector.getInstance(validatorClass);
			}
			return validator;
		}

		@Override
		public List<Diagnostic> validate(T attributeValue) {
			AbstractDeclarativeValidator validator = getValidator();

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
						diagnostics.add(new BasicDiagnostic(Diagnostic.ERROR,
								null, -1, message, new Object[] {}));
					}

					@Override
					public void acceptError(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(new BasicDiagnostic(Diagnostic.ERROR,
								null, -1, message, new Object[] {}));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(new BasicDiagnostic(Diagnostic.INFO,
								null, -1, message, new Object[] {}));
					}

					@Override
					public void acceptInfo(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(new BasicDiagnostic(Diagnostic.INFO,
								null, -1, message, new Object[] {}));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							EStructuralFeature feature, int index, String code,
							String... issueData) {
						diagnostics.add(new BasicDiagnostic(Diagnostic.WARNING,
								null, -1, message, new Object[] {}));
					}

					@Override
					public void acceptWarning(String message, EObject object,
							int offset, int length, String code,
							String... issueData) {
						diagnostics.add(new BasicDiagnostic(Diagnostic.WARNING,
								null, -1, message, new Object[] {}));
					}
				});

				Map<Object, Object> context = new HashMap<>();
				context.put(AbstractInjectableValidator.CURRENT_LANGUAGE_NAME,
						ReflectionUtils.getPrivateFieldValue(validator,
								"languageName"));

				// validate the root element...
				validator.validate(attributeValue, null /* diagnostic chain */,
						context);

				// ...and all its children
				for (Iterator<EObject> iterator = EcoreUtil
						.getAllProperContents(attributeValue, true); iterator
								.hasNext();) {
					validator.validate(iterator.next(),
							null /* diagnostic chain */, context);
				}
			}
			return diagnostics;
		}
	}

	/**
	 * Parses the given value as a DOT dirType.
	 */
	public static IAttributeValueParser<DirType> DIRTYPE_PARSER = new EnumValueParser<>(
			DirType.class);

	/**
	 * A serializer for {@link DirType} values.
	 */
	public static IAttributeValueSerializer<DirType> DIRTYPE_SERIALIZER = new EnumValueSerializer<>(
			DirType.class);

	/**
	 * Parses the given value as a DOT dirType.
	 */
	public static IAttributeValueParser<Layout> LAYOUT_PARSER = new EnumValueParser<>(
			Layout.class);

	/**
	 * A serializer for {@link DirType} values.
	 */
	public static IAttributeValueSerializer<Layout> LAYOUT_SERIALIZER = new EnumValueSerializer<>(
			Layout.class);

	/**
	 * Parses the given value as a {@link ClusterMode}.
	 */
	public static IAttributeValueParser<ClusterMode> CLUSTERMODE_PARSER = new EnumValueParser<>(
			ClusterMode.class);

	/**
	 * Serializes the given {@link ClusterMode} value.
	 */
	public static IAttributeValueSerializer<ClusterMode> CLUSTERMODE_SERIALIZER = new EnumValueSerializer<>(
			ClusterMode.class);

	/**
	 * Parses the given value as a DOT outputMode.
	 */
	public static IAttributeValueParser<OutputMode> OUTPUTMODE_PARSER = new EnumValueParser<>(
			OutputMode.class);

	/**
	 * Serializes the given {@link OutputMode} value.
	 */
	public static IAttributeValueSerializer<OutputMode> OUTPUTMODE_SERIALIZER = new EnumValueSerializer<>(
			OutputMode.class);

	/**
	 * Parses the given value as a DOT pagedir.
	 */
	public static IAttributeValueParser<Pagedir> PAGEDIR_PARSER = new EnumValueParser<>(
			Pagedir.class);

	/**
	 * Serializes the given {@link Pagedir} value.
	 */
	public static IAttributeValueSerializer<Pagedir> PAGEDIR_SERIALIZER = new EnumValueSerializer<>(
			Pagedir.class);

	/**
	 * A parser used to parse DOT rankdir values.
	 */
	public static IAttributeValueParser<Rankdir> RANKDIR_PARSER = new EnumValueParser<>(
			Rankdir.class);

	/**
	 * Serializes the given {@link Rankdir} value.
	 */
	public static IAttributeValueSerializer<Rankdir> RANKDIR_SERIALIZER = new EnumValueSerializer<>(
			Rankdir.class);

	/**
	 * A parser used to parse DOT {@link Splines} values.
	 */
	public static IAttributeValueParser<Splines> SPLINES_PARSER = new IAttributeValueParser<Splines>() {

		private EnumValueParser<Splines> enumParser = new EnumValueParser<>(
				Splines.class);

		@Override
		public IParseResult<Splines> parse(String attributeValue) {
			// XXX: splines can either be an enum or a bool value; we try both
			// options here and convert boolean expressions into respective
			// splines
			IParseResult<Boolean> boolResult = BOOL_PARSER
					.parse(attributeValue);
			if (!boolResult.hasSyntaxErrors()) {
				return new AttributeValueParseResultImpl<>(
						boolResult.getParsedValue() ? Splines.TRUE
								: Splines.FALSE);
			}
			IParseResult<Splines> enumResult = enumParser.parse(attributeValue);
			if (!enumResult.hasSyntaxErrors()) {
				return new AttributeValueParseResultImpl<>(
						enumResult.getParsedValue());
			}

			// TODO: create a better, combined error message here
			List<Diagnostic> combinedFindings = new ArrayList<>();
			combinedFindings.addAll(boolResult.getSyntaxErrors());
			combinedFindings.addAll(enumResult.getSyntaxErrors());
			return new AttributeValueParseResultImpl<>(combinedFindings);
		}
	};

	/**
	 * Serializes the given {@link Splines} value.
	 */
	public static IAttributeValueSerializer<Splines> SPLINES_SERIALIZER = new EnumValueSerializer<>(
			Splines.class);

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
	 * A parser for bool values.
	 */
	public static IAttributeValueParser<Boolean> BOOL_PARSER = new IAttributeValueParser<Boolean>() {

		@Override
		public IAttributeValueParser.IParseResult<Boolean> parse(
				String rawValue) {
			if (rawValue == null) {
				return null;
			}
			// case insensitive "true" or "yes"
			if (Boolean.TRUE.toString().equalsIgnoreCase(rawValue)
					|| "yes".equalsIgnoreCase(rawValue)) {
				return new AttributeValueParseResultImpl<>(Boolean.TRUE);
			}
			// case insensitive "false" or "no"
			if (Boolean.FALSE.toString().equalsIgnoreCase(rawValue)
					|| "no".equalsIgnoreCase(rawValue)) {
				return new AttributeValueParseResultImpl<>(Boolean.FALSE);
			}
			// an integer value
			try {
				int parsedValue = Integer.parseInt(rawValue);
				return new AttributeValueParseResultImpl<>(
						parsedValue > 0 ? Boolean.TRUE : Boolean.FALSE);
			} catch (NumberFormatException e) {
				return new AttributeValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								"The given value '" + rawValue
										+ "' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value",
								new Object[] {})));
			}
		}
	};

	/**
	 * A serializer for bool values.
	 */
	public static IAttributeValueSerializer<Boolean> BOOL_SERIALIZER = new IAttributeValueSerializer<Boolean>() {

		@Override
		public String serialize(Boolean value) {
			return Boolean.toString(value);
		}
	};

	/**
	 * A parser for double values.
	 */
	public static IAttributeValueParser<Double> DOUBLE_PARSER = new IAttributeValueParser<Double>() {

		@Override
		public IAttributeValueParser.IParseResult<Double> parse(
				String rawValue) {
			if (rawValue == null) {
				return null;
			}
			try {
				// TODO: check that this resembles the DOT double interpretation
				double parsedValue = Double.parseDouble(rawValue);
				return new AttributeValueParseResultImpl<>(
						new Double(parsedValue));
			} catch (NumberFormatException exception) {
				return new AttributeValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								exception.getMessage(), new Object[] {})));
			}
		}
	};

	/**
	 * A serializer for double values.
	 */
	public static IAttributeValueSerializer<Double> DOUBLE_SERIALIZER = new IAttributeValueSerializer<Double>() {

		@Override
		public String serialize(Double value) {
			return Double.toString(value);
		}
	};

	/**
	 * A parser used to parse DOT int values.
	 */
	public static IAttributeValueParser<Integer> INT_PARSER = new IAttributeValueParser<Integer>() {

		@Override
		public IAttributeValueParser.IParseResult<Integer> parse(
				String rawValue) {
			if (rawValue == null) {
				return null;
			}
			try {
				int parsedValue = Integer.parseInt(rawValue);
				return new AttributeValueParseResultImpl<>(
						new Integer(parsedValue));
			} catch (NumberFormatException exception) {
				return new AttributeValueParseResultImpl<>(Collections
						.<Diagnostic> singletonList(new BasicDiagnostic(
								Diagnostic.ERROR, rawValue, -1,
								exception.getMessage(), new Object[] {})));
			}
		}
	};

	/**
	 * A serializer for int values.
	 */
	public static IAttributeValueSerializer<Integer> INT_SERIALIZER = new IAttributeValueSerializer<Integer>() {

		@Override
		public String serialize(Integer value) {
			return Integer.toString(value);
		}
	};

	private static final Injector arrowTypeInjector = new DotArrowTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The validator for arrowtype attribute values.
	 */
	// TODO: move to DotJavaValidator
	public static final IAttributeValueValidator<ArrowType> ARROWTYPE_VALIDATOR = new XtextDelegateValueValidator<>(
			arrowTypeInjector, DotArrowTypeJavaValidator.class);

	/**
	 * The parser for arrowtype attribute values.
	 */
	public static final IAttributeValueParser<ArrowType> ARROWTYPE_PARSER = new XtextDelegateValueParser<>(
			arrowTypeInjector);

	/**
	 * The serializer for arrowtype attribute values.
	 */
	public static final IAttributeValueSerializer<ArrowType> ARROWTYPE_SERIALIZER = new XtextDelegateValueSerializer<>(
			arrowTypeInjector);

	private static final Injector colorInjector = new DotColorStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for color attribute values.
	 */
	public static final IAttributeValueParser<Color> COLOR_PARSER = new XtextDelegateValueParser<>(
			colorInjector);

	/**
	 * The serializer for color attribute values.
	 */
	public static final IAttributeValueSerializer<Color> COLOR_SERIALIZER = new XtextDelegateValueSerializer<>(
			colorInjector);

	private static final Injector pointInjector = new DotPointStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for point attribute values.
	 */
	public static final IAttributeValueParser<Point> POINT_PARSER = new XtextDelegateValueParser<>(
			pointInjector);

	/**
	 * The serializer for point attribute values.
	 */
	public static final IAttributeValueSerializer<Point> POINT_SERIALIZER = new XtextDelegateValueSerializer<>(
			pointInjector);

	private static final Injector shapeInjector = new DotShapeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for shape attribute values.
	 */
	public static final IAttributeValueParser<Shape> SHAPE_PARSER = new XtextDelegateValueParser<>(
			shapeInjector);

	/**
	 * The serializer for shape attribute values.
	 */
	public static final IAttributeValueSerializer<Shape> SHAPE_SERIALIZER = new XtextDelegateValueSerializer<>(
			shapeInjector);

	private static final Injector splineTypeInjector = new DotSplineTypeStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The parser for splinetype attribute values.
	 */
	public static final IAttributeValueParser<SplineType> SPLINETYPE_PARSER = new XtextDelegateValueParser<>(
			splineTypeInjector);

	/**
	 * The serializer for splinetype attribute values.
	 */
	public static final IAttributeValueSerializer<SplineType> SPLINETYPE_SERIALIZER = new XtextDelegateValueSerializer<>(
			splineTypeInjector);

	private static final Injector styleInjector = new DotStyleStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The serializer for style attribute values.
	 */
	public static final IAttributeValueSerializer<Style> STYLE_SERIALIZER = new XtextDelegateValueSerializer<>(
			styleInjector);

	/**
	 * The parser for style attribute values.
	 */
	public static final IAttributeValueParser<Style> STYLE_PARSER = new XtextDelegateValueParser<>(
			styleInjector);

	/**
	 * Validator for Color types.
	 */
	public static final IAttributeValueValidator<Color> COLOR_VALIDATOR = new XtextDelegateValueValidator<>(
			colorInjector, DotColorJavaValidator.class);

	/**
	 * Validator for SplineType types.
	 */
	public static final IAttributeValueValidator<SplineType> SPLINETYPE_VALIDATOR = new XtextDelegateValueValidator<>(
			splineTypeInjector, DotSplineTypeJavaValidator.class);

	/**
	 * Validator for Point types.
	 */
	public static final IAttributeValueValidator<Point> POINT_VALIDATOR = new XtextDelegateValueValidator<>(
			pointInjector, DotPointJavaValidator.class);

	/**
	 * Validator for Shape types.
	 */
	public static final IAttributeValueValidator<Shape> SHAPE_VALIDATOR = new XtextDelegateValueValidator<>(
			shapeInjector, DotShapeJavaValidator.class);

	/**
	 * Validator for Style types.
	 */
	public static final IAttributeValueValidator<Style> STYLE_VALIDATOR = new XtextDelegateValueValidator<>(
			styleInjector, DotStyleJavaValidator.class);

	/**
	 * Serialize the given attribute value using the given serializer.
	 * 
	 * @param <T>
	 *            The (primitive) object type of the to be serialized value.
	 * @param serializer
	 *            The {@link IAttributeValueSerializer} to use for serializing.
	 * @param attributeValue
	 *            The value to serialize.
	 * @return The serialized value.
	 */
	public static <T> String serializeAttributeValue(
			IAttributeValueSerializer<T> serializer, T attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		return serializer.serialize(attributeValue);
	}

	/**
	 * Parses the given (unquoted) attribute, using the given
	 * {@link IAttributeValueParser}.
	 * 
	 * @param <T>
	 *            The (primitive) object type of the parsed value.
	 * @param parser
	 *            The parser to be used for parsing.
	 * @param attributeValue
	 *            The attribute value that is to be parsed.
	 * @return The parsed value, or <code>null</code> if the value could not be
	 *         parsed.
	 */
	public static <T> T parseAttributeValue(IAttributeValueParser<T> parser,
			String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		IAttributeValueParser.IParseResult<T> parsedAttributeValue = parser
				.parse(attributeValue);
		return parsedAttributeValue.getParsedValue();
	}

	/**
	 * Determine the context in which the given {@link EObject} is used.
	 * 
	 * @param eObject
	 *            The {@link EObject} for which the context is to be determined.
	 * @return the context in which the given {@link EObject} is used.
	 */
	public static Context getContext(EObject eObject) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (EcoreUtil2.getContainerOfType(eObject, EdgeStmtNode.class) != null
				|| EcoreUtil2.getContainerOfType(eObject,
						EdgeStmtSubgraph.class) != null) {
			return Context.EDGE;
		}
		// global AttrStmt with AttributeType 'edge'
		AttrStmt attrStmt = EcoreUtil2.getContainerOfType(eObject,
				AttrStmt.class);
		if (attrStmt != null && AttributeType.EDGE.equals(attrStmt.getType())) {
			return Context.EDGE;
		}

		// attribute nested below NodeStmt
		if (EcoreUtil2.getContainerOfType(eObject, NodeStmt.class) != null) {
			return Context.NODE;
		}
		// global AttrStmt with AttributeType 'node'
		if (attrStmt != null && AttributeType.NODE.equals(attrStmt.getType())) {
			return Context.NODE;
		}

		// attribute nested below Subgraph
		Subgraph subgraph = EcoreUtil2.getContainerOfType(eObject,
				Subgraph.class);
		if (subgraph != null) {
			if (subgraph.getName().toValue().startsWith("cluster")) {
				return Context.CLUSTER;
			}
			return Context.SUBGRAPH;
		}

		// attribute is neither edge nor node nor subgraph attribute
		return Context.GRAPH;
	}
}
