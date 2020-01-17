/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - add binding for custom value converter (bug #513196)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.formatting.DotHtmlLabelFormatter;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelValueConverterService;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotHtmlLabelRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotHtmlLabelRuntimeModule {

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotHtmlLabelFormatter.class;
	}

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotHtmlLabelValueConverterService.class;
	}

}
