/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
