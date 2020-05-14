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
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

/**
 * Provides labels for a IEObjectDescriptions and IResourceDescriptions.
 *
 * see http://www.eclipse.org/Xtext/documentation.html#labelProvider
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
