/*******************************************************************************
 * Copyright (c) 2015, 2020 itemis AG and others.
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
package org.eclipse.gef.dot.internal.language.terminals;

import java.util.Map;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

/**
 * A terminal converter for Dot.
 *
 * @author anyssen
 *
 */
public class DotTerminalConverters
		extends AbstractDeclarativeValueConverterService {

	@Inject
	private DotIDValueConverter idValueConverter;

	/**
	 * A value converter for the ID data type rule.
	 *
	 * @return A {@link DotIDValueConverter}.
	 */
	@ValueConverter(rule = "ID")
	public IValueConverter<ID> ID() {
		return idValueConverter;
	}

	/*
	 * Workaround for https://github.com/eclipse/xtext-eclipse/issues/1443
	 */
	@Override
	public IValueConverter<Object> getConverter(String lexerRule) {
		Map<String, IValueConverter<Object>> map = getConverters();
		if (map.containsKey(lexerRule)) {
			return map.get(lexerRule);
		}
		return new IValueConverter<Object>() {

			@Override
			public Object toValue(String string, INode node)
					throws ValueConverterException {
				return string;
			}

			@Override
			public String toString(Object value)
					throws ValueConverterException {
				return value.toString();
			}
		};
	}
}
