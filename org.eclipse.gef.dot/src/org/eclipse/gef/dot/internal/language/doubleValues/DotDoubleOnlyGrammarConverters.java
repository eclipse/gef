/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #559031)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.doubleValues;

import org.eclipse.gef.dot.internal.language.terminals.DotIDValueConverter;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 *
 * A class to add p0 for int hex double values to use java Double parser
 *
 */
public class DotDoubleOnlyGrammarConverters
		extends AbstractDeclarativeValueConverterService {

	/**
	 * A value converter for the ID data type rule.
	 *
	 * @return A {@link DotIDValueConverter}.
	 */
	@ValueConverter(rule = "DOUBLE")
	public IValueConverter<Double> doubleConverter() {
		return new AbstractNullSafeConverter<Double>() {

			@Override
			protected String internalToString(Double value) {
				return value.toString();
			}

			@Override
			protected Double internalToValue(String string, INode node)
					throws ValueConverterException {
				return DotDoubleUtil.parseDotDouble(string);
			}
		};
	}
}
