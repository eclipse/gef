/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #496777
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

	/**
	 * The {@link LoggingMode} specifies if binding-related information and
	 * warning messages should be printed.
	 */
	public static enum LoggingMode {
		/**
		 * In {@link #DEVELOPMENT} mode, all information, warning, and error
		 * messages are printed.
		 */
		DEVELOPMENT,

		/**
		 * In {@link #PRODUCTION} mode, only error messages are printed, and
		 * information or warning messages are suppressed.
		 */
		PRODUCTION,
	}

	private LoggingMode loggingMode = LoggingMode.DEVELOPMENT;

	/**
	 * Constructs a new {@link AdapterInjectionSupport} in
	 * {@link LoggingMode#DEVELOPMENT} mode, i.e. binding-related information,
	 * warning, and error messages will be printed.
	 */
	public AdapterInjectionSupport() {
	}

	/**
	 * Constructs a new {@link AdapterInjectionSupport} and specifies the
	 * {@link LoggingMode} to use. If in {@link LoggingMode#DEVELOPMENT} mode,
	 * binding-related information, warning, and error messages will be printed.
	 * If in {@link LoggingMode#PRODUCTION} mode, only error messages will be
	 * printed, and information and warning messages will be suppressed.
	 *
	 * @param loggingMode
	 *            The {@link LoggingMode} to use.
	 */
	public AdapterInjectionSupport(LoggingMode loggingMode) {
		this.loggingMode = loggingMode;
	}

	/**
	 * Binds an {@link AdaptableTypeListener} (via
	 * {@link #bindListener(Matcher, TypeListener)}) and ensures it gets
	 * properly injected ({@link #requestInjection(Object)}).
	 */
	@Override
	protected void configure() {
		AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener(
				loggingMode);
		requestInjection(adaptableTypeListener);
		bindListener(new AbstractMatcher<TypeLiteral<?>>() {
			@Override
			public boolean matches(TypeLiteral<?> t) {
				return IAdaptable.class.isAssignableFrom(t.getRawType());
			}
		}, adaptableTypeListener);
	}

}
