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

import org.eclipse.gef4.fx.ui.canvas.FXCanvasEx;
import org.eclipse.gef4.mvc.fx.ui.parts.IFXCanvasFactory;
import org.eclipse.gef4.mvc.ui.MvcUiModule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;

public class MvcFxUiModule extends MvcUiModule {

	protected void bindFXCanvasFactory() {
		binder().bind(IFXCanvasFactory.class)
				.toInstance(new IFXCanvasFactory() {

					@Override
					public FXCanvas createCanvas(Composite parent) {
						return new FXCanvasEx(parent, SWT.NONE);
					}
				});
	}

	@Override
	protected void configure() {
		super.configure();

		// install FXCanvas factory
		bindFXCanvasFactory();
	}
}
