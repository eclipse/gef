/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples.graph.ui.view;

import org.eclipse.gef.zest.examples.graph.ZestGraphExample;
import org.eclipse.gef.zest.examples.graph.ZestGraphExampleModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class ZestGraphExampleView extends ZestFxUiView {

	public ZestGraphExampleView() {
		super(Guice.createInjector(Modules.override(new ZestGraphExampleModule()).with(new ZestFxUiModule())));
		setGraph(ZestGraphExample.createDefaultGraph());
	}
}
