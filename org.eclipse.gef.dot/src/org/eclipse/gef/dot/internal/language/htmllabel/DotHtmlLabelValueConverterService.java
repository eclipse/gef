/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.htmllabel;

import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;

import com.google.inject.Inject;

/**
 * A value converter service for the Dot HTML-Like Labels subgrammar.
 *
 */
public class DotHtmlLabelValueConverterService
		extends AbstractDeclarativeValueConverterService {

	@Inject
	private DotHtmlLabelIDValueConverter idValueConverter;

	/**
	 * A value converter for the ID terminal rule.
	 *
	 * @return A {@link DotHtmlLabelIDValueConverter}
	 */
	@ValueConverter(rule = "ID")
	public DotHtmlLabelIDValueConverter ID() {
		return idValueConverter;
	}
}
