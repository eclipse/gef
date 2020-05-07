/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.DotSplineTypeStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages
 * without equinox extension registry
 */
public class DotSplineTypeStandaloneSetup extends DotSplineTypeStandaloneSetupGenerated{

	public static void doSetup() {
		new DotSplineTypeStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

