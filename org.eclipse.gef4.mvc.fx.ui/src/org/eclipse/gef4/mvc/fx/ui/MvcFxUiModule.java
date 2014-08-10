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
package org.eclipse.gef4.mvc.fx.ui;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.fx.ui.view.DefaultSelectionProvider;
import org.eclipse.gef4.mvc.fx.ui.view.IFXCanvasFactory;
import org.eclipse.gef4.mvc.fx.viewer.ISceneFactory;
import org.eclipse.gef4.mvc.ui.MvcUiModule;
import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MvcFxUiModule extends MvcUiModule {

	@Override
	protected void configure() {
		super.configure();

		// bind selection provider
		bindSelectionProvider();

		// install a scene factory that creates an SwtFXScene
		bindSceneFactory();

		// install FXCanvas factory
		bindFXCanvasFactory();
	}

	protected void bindFXCanvasFactory() {
		binder().bind(IFXCanvasFactory.class).toInstance(
				new IFXCanvasFactory() {

					@Override
					public FXCanvas createCanvas(Composite parent) {
						return new SwtFXCanvas(parent, SWT.NONE);
					}
				});
	}

	protected void bindSelectionProvider() {
		binder().bind(ISelectionProvider.class).to(
				DefaultSelectionProvider.class);
	}

	protected void bindSceneFactory() {
		// overwrite binding within MvcFXModule
		binder().bind(ISceneFactory.class).toInstance(new ISceneFactory() {
			@Override
			public Scene createScene(Parent rootVisual) {
				return new Scene(rootVisual);
			}
		});

	}
}
