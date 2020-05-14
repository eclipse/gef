/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class DotPortPosStandaloneSetup
		extends DotPortPosStandaloneSetupGenerated {

	public static void doSetup() {
		new DotPortPosStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}
