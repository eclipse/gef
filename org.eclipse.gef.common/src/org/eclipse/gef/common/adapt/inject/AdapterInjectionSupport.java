/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import org.eclipse.gef.common.adapt.IAdaptable;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeListener;

/**
 * A configuration module that can be installed (via
 * {@link Binder#install(Module)}) to enable support for injection of adapters
 * into {@link IAdaptable}s. The adapters that are to be injected can be
 * configured through respective adapter (map) bindings (see {@link AdapterMap}
 * ). Injection is performed on all {@link IAdaptable}s that are eligible for
 * adapter injection (see {@link InjectAdapters}).
 *
 * @author anyssen
 *
 */
public class AdapterInjectionSupport extends AbstractModule {

	private boolean isProduction = false;

	/**
	 * Constructs a new {@link AdapterInjectionSupport} in "debug" mode, i.e.
	 * binding-related infos, warnings, and errors will be printed.
	 */
	public AdapterInjectionSupport() {
	}

	/**
	 * Constructs a new {@link AdapterInjectionSupport} in "debug" or
	 * "production" mode. In debug mode, binding-related infos, warnings, and
	 * errors will be printed. In production mode, only binding-related errors
	 * will be printed.
	 *
	 * @param isProduction
	 *            <code>true</code> to suppress info and warning messages,
	 *            otherwise <code>false</code>.
	 */
	public AdapterInjectionSupport(boolean isProduction) {
		this.isProduction = isProduction;
	}

	/**
	 * Binds an {@link AdaptableTypeListener} (via
	 * {@link #bindListener(Matcher, TypeListener)}) and ensures it gets
	 * properly injected ({@link #requestInjection(Object)}).
	 */
	@Override
	protected void configure() {
		AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener(
				isProduction);
		requestInjection(adaptableTypeListener);
		bindListener(new AbstractMatcher<TypeLiteral<?>>() {
			@Override
			public boolean matches(TypeLiteral<?> t) {
				return IAdaptable.class.isAssignableFrom(t.getRawType());
			}
		}, adaptableTypeListener);
	}

}
