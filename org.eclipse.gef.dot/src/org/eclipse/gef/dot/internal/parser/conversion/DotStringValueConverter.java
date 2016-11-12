/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef.dot.internal.parser.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractValueConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * A converter that converts terminals that are valid STRING Dot IDs into
 * Strings.
 * 
 * @author anyssen
 *
 */
public class DotStringValueConverter extends AbstractValueConverter<String>
		implements IValueConverter<String> {

	@Override
	public String toValue(String string, INode node)
			throws ValueConverterException {
		return string;
	}

	@Override
	public String toString(String value) throws ValueConverterException {
		return value;
	}
}
