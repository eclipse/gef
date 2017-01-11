/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef.zest.examples.graph.ui.view;

import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportAction;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ScrollActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ZoomActionGroup;
import org.eclipse.gef.zest.examples.graph.ZestGraphExample;
import org.eclipse.gef.zest.examples.graph.ZestGraphExampleModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class ZestGraphExampleView extends ZestFxUiView {

	private ZoomActionGroup zoomActionGroup;
	private FitToViewportActionGroup fitToViewportActionGroup;
	private ScrollActionGroup scrollActionGroup;

	public ZestGraphExampleView() {
		super(Guice.createInjector(Modules.override(new ZestGraphExampleModule()).with(new ZestFxUiModule())));
		setGraph(ZestGraphExample.createDefaultGraph());
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// create actions
		zoomActionGroup = new ZoomActionGroup(new FitToViewportAction());
		zoomActionGroup.init(getContentViewer());
		fitToViewportActionGroup = new FitToViewportActionGroup();
		fitToViewportActionGroup.init(getContentViewer());
		scrollActionGroup = new ScrollActionGroup();
		scrollActionGroup.init(getContentViewer());
		// contribute to toolbar
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager mgr = actionBars.getToolBarManager();
		zoomActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		fitToViewportActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		scrollActionGroup.fillActionBars(actionBars);
	}

	@Override
	public void dispose() {
		// dispose actions
		if (zoomActionGroup != null) {
			zoomActionGroup.dispose();
			zoomActionGroup = null;
		}
		if (scrollActionGroup != null) {
			scrollActionGroup.dispose();
			scrollActionGroup = null;
		}
		if (fitToViewportActionGroup != null) {
			fitToViewportActionGroup.dispose();
			fitToViewportActionGroup = null;
		}
		super.dispose();
	}
}
