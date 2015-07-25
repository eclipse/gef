/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.ui.view;

import org.eclipse.gef4.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef4.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef4.mvc.examples.logo.ui.MvcLogoExampleUiModule;
import org.eclipse.gef4.mvc.fx.ui.parts.FXView;
import org.eclipse.gef4.mvc.models.ContentModel;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class MvcLogoExampleView extends FXView {

	// TODO: create FXView via an executable extension factory (obtaining the
	// injector via the bundle)
	public MvcLogoExampleView() {
		super(Guice.createInjector(Modules.override(new MvcLogoExampleModule())
				.with(new MvcLogoExampleUiModule())));
		// set default contents (GEF logo)
		getViewer().getAdapter(ContentModel.class)
				.setContents(MvcLogoExample.createDefaultContents());
	}
}
