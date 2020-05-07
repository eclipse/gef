/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge  - initial API and implementation (bug #454629)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class DotRecordLabelStandaloneSetup
		extends DotRecordLabelStandaloneSetupGenerated {

	public static void doSetup() {
		new DotRecordLabelStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
	}
}
