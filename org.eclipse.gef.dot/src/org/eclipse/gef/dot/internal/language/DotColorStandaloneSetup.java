/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.DotColorStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages
 * without equinox extension registry
 */
public class DotColorStandaloneSetup extends DotColorStandaloneSetupGenerated{

	public static void doSetup() {
		new DotColorStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}