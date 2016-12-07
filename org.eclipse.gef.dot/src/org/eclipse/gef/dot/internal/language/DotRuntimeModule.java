/*******************************************************************************
 * Copyright (c) 2010, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen (itemis AG) - initial implementation (value converter binding)
 *     Tamas Miklossy  (itemis AG) - request for static injection (#498324)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.language.terminals.DotTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverterService;

import com.google.inject.Binder;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotRuntimeModule
		extends org.eclipse.gef.dot.internal.language.AbstractDotRuntimeModule {

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotTerminalConverters.class;
	}

	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.requestStaticInjection(DotAttributes.class);
		binder.requestStaticInjection(DotImport.class);
	}
}
