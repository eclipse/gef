/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.example.view;

import org.eclipse.gef4.zest.fx.example.ZestFXExampleApplication;
import org.eclipse.gef4.zest.fx.example.ZestFxExampleModule;
import org.eclipse.gef4.zest.fx.ui.example.ZestFxUiExampleModule;
import org.eclipse.gef4.zest.fx.ui.view.ZestFxUiView;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class ZestFxUiExampleView extends ZestFxUiView {

	public ZestFxUiExampleView() {
		super(Guice.createInjector(Modules.override(new ZestFxExampleModule())
				.with(new ZestFxUiExampleModule())));
		setGraph(ZestFXExampleApplication.DEFAULT_GRAPH);
	}

}
