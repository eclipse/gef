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

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for EObjects.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider
 */
public class DotRecordLabelLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public DotRecordLabelLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	// Labels and icons can be computed like this:

	// String text(Greeting ele) {
	// return "A greeting to " + ele.getName();
	// }
	//
	// String image(Greeting ele) {
	// return "Greeting.gif";
	// }
}
