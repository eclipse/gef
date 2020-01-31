/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.labeling;

import org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider;

/**
 * Provides labels for IEObjectDescriptions and IResourceDescriptions.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider
 */
public class DotRecordLabelDescriptionLabelProvider
		extends DefaultDescriptionLabelProvider {

	// Labels and icons can be computed like this:

	// String text(IEObjectDescription ele) {
	// return ele.getName().toString();
	// }
	//
	// String image(IEObjectDescription ele) {
	// return ele.getEClass().getName() + ".gif";
	// }
}
