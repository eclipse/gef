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

import org.eclipse.gef4.dot.internal.parser.DotArrowTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotPointStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotSplineTypeStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.DotStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotArrowTypeParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotPointParser;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotSplineTypeParser;
import org.eclipse.gef4.dot.internal.parser.validation.DotArrowTypeJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotPointJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotSplineTypeJavaValidator;
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
// TODO: This class should remain internal, even when exposing the rest of the
// API.
public class DotLanguageSupport {

	private static final Injector dotInjector = new DotStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	/**
	 * The 'main' validator of the DOT language-
	 */
	public static final DotJavaValidator DOT_VALIDATOR = dotInjector
			.getInstance(DotJavaValidator.class);

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

	public static <T> T parse(IParser parser, String attributeValue) {
		if (attributeValue == null) {
			return null;
		}
		IParseResult parsedAttributeValue = parser
				.parse(new StringReader(attributeValue));
		return (T) parsedAttributeValue.getRootASTElement();
	}

	public static Double parseDouble(String attributeValue) {
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

	public static Boolean parseBoolean(String attributeValue) {
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
}
