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
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.emf.ecore.EPackage;

import com.google.inject.Injector;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class DotFontNameStandaloneSetup
		extends DotFontNameStandaloneSetupGenerated {

	public static void doSetup() {
		new DotFontNameStandaloneSetup().createInjectorAndDoEMFRegistration();
	}

	@Override
	public void register(Injector injector) {
		if (!EPackage.Registry.INSTANCE.containsKey(
				"http://www.eclipse.org/gef/dot/internal/language/DotFontName")) {
			EPackage.Registry.INSTANCE.put(
					"http://www.eclipse.org/gef/dot/internal/language/DotFontName",
					org.eclipse.gef.dot.internal.language.fontname.FontnamePackage.eINSTANCE);
		}
		super.register(injector);
	}
}
