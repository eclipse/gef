/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeListener;

/**
 * A configuration module that can be installed (via
 * {@link Binder#install(Module)}) to enable support for adapter map injections.
 * 
 * @author anyssen
 *
 */
public class AdapterMapInjectionSupport extends AbstractModule {

	/**
	 * Binds an {@link AdaptableTypeListener} (via
	 * {@link #bindListener(Matcher, TypeListener)}) and ensures it gets
	 * properly injected ({@link #requestInjection(Object)}).
	 */
	@Override
	protected void configure() {
		AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener();
		requestInjection(adaptableTypeListener);
		bindListener(Matchers.any(), adaptableTypeListener);
	}

}
