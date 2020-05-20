/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

/**
 * Provides labels for IEObjectDescriptions and IResourceDescriptions.
 *
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
public class DotHtmlLabelDescriptionLabelProvider
		extends org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider {

	// Labels and icons can be computed like this:

	// String text(IEObjectDescription ele) {
	// return ele.getName().toString();
	// }
	//
	// String image(IEObjectDescription ele) {
	// return ele.getEClass().getName() + ".gif";
	// }
}
