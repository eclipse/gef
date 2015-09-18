/*******************************************************************************
 * Copyright (c) 2010, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation (value converter binding)
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser;

import org.eclipse.gef4.internal.dot.parser.conversion.DotTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverterService;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotRuntimeModule
		extends org.eclipse.gef4.internal.dot.parser.AbstractDotRuntimeModule {

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotTerminalConverters.class;
	}
}
