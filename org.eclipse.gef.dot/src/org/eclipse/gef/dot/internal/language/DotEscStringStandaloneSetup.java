/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

/**
 * Initialization support for running Xtext languages
 * without equinox extension registry
 */
public class DotEscStringStandaloneSetup extends DotEscStringStandaloneSetupGenerated{

	public static void doSetup() {
		new DotEscStringStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

