/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.view;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.fx.example.FXExampleModule;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleUiModule;
import org.eclipse.gef4.mvc.fx.ui.view.FXView;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class FXExampleView extends FXView {

	// TODO: create FXView via an executable extension factory (obtaining the
	// injector via the bundle)
	public FXExampleView() {
		super(Guice.createInjector(Modules.override(new FXExampleModule())
				.with(new FXExampleUiModule())));
	}

	@Override
	protected List<Object> getContents() {
		return Collections.<Object> singletonList(new FXGeometricModel());
	}

}
