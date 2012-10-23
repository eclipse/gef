/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.internal.dot.parser.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class DotLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public DotLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	/*
	 * //Labels and icons can be computed like this:
	 * 
	 * String text(MyModel ele) { return "my "+ele.getName(); }
	 * 
	 * String image(MyModel ele) { return "MyModel.gif"; }
	 */
}
