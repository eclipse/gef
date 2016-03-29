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
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Injector;

/**
 * Provides access to parsers, serializers, and validators of the respective Dot
 * language grammars.
 * 
 * @author nyssen
 *
 */
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
	 * The validator for splinetype attribute values.
	 */
	public static final DotSplineTypeJavaValidator SPLINETYPE_VALIDATOR = splineTypeInjector
			.getInstance(DotSplineTypeJavaValidator.class);

}
