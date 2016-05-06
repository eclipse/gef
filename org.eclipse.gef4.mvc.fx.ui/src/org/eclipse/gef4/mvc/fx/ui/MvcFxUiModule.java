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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef4.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef4.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.ui.MvcUiModule;
import org.eclipse.gef4.mvc.ui.parts.ContentSelectionProvider;
import org.eclipse.gef4.mvc.ui.parts.ISelectionProviderFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.reflect.TypeToken;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;

/**
 * The {@link MvcFxUiModule} contains Eclipse UI specific bindings in the
 * context of an MVC.FX application.
 *
 * @author anyssen
 *
 */
public class MvcFxUiModule extends MvcUiModule {

	/**
	 * Binds an {@link IFXCanvasFactory} that creates an {@link FXCanvasEx} as
	 * the container for the {@link FXViewer}.
	 */
	protected void bindFXCanvasFactory() {
		// TODO: change to assisted inject
		binder().bind(IFXCanvasFactory.class)
				.toInstance(new IFXCanvasFactory() {
					@Override
					public FXCanvas createCanvas(Composite parent, int style) {
						return new FXCanvasEx(parent, style);
					}
				});
	}

	/**
	 * Binds a factory for the creation of {@link ContentSelectionProvider} as
	 * {@link ISelectionProvider}.
	 */
	protected void bindISelectionProviderFactory() {
		binder().bind(ISelectionProviderFactory.class)
				.toInstance(new ISelectionProviderFactory() {

					@SuppressWarnings("serial")
					@Override
					public ISelectionProvider create(
							IWorkbenchPart workbenchPart) {
						SelectionModel<Node> selectionModel = null;
						if (workbenchPart instanceof AbstractFXView) {
							selectionModel = ((AbstractFXView) workbenchPart)
									.getDomain()
									.getAdapter(AdapterKey.get(FXViewer.class,
											FXDomain.CONTENT_VIEWER_ROLE))
									.getAdapter(
											new TypeToken<SelectionModel<Node>>() {
							});
						} else if (workbenchPart instanceof AbstractFXEditor) {
							selectionModel = ((AbstractFXEditor) workbenchPart)
									.getDomain()
									.getAdapter(AdapterKey.get(FXViewer.class,
											FXDomain.CONTENT_VIEWER_ROLE))
									.getAdapter(
											new TypeToken<SelectionModel<Node>>() {
							});
						} else {
							throw new IllegalArgumentException(
									"Cannot handle " + workbenchPart);
						}
						return new ContentSelectionProvider<>(selectionModel);
					}
				});
	}

	@Override
	protected void configure() {
		super.configure();
		// install FXCanvas factory
		bindFXCanvasFactory();
		bindISelectionProviderFactory();
	}

}
