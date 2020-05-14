/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.jface;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;

/**
 * The {@link ZestFxJFaceModule} contains Eclipse UI specific bindings in the
 * context of an MVC.FX application.
 *
 * @author anyssen
 *
 */
public class ZestFxJFaceModule extends ZestFxModule {

	/**
	 * Binds an {@link IFXCanvasFactory} that creates an {@link FXCanvasEx} as
	 * the container for the {@link IViewer}.
	 */
	protected void bindFXCanvasFactory() {
		binder().bind(IFXCanvasFactory.class).toInstance(new IFXCanvasFactory() {
			@Override
			public FXCanvas createCanvas(Composite parent, int style) {
				return new FXCanvasEx(parent, style);
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
