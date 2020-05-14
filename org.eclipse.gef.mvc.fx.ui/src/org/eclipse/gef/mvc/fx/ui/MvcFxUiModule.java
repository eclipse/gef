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
package org.eclipse.gef.mvc.fx.ui;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.ui.parts.ContentSelectionProvider;
import org.eclipse.gef.mvc.fx.ui.parts.HistoryBasedDirtyStateProvider;
import org.eclipse.gef.mvc.fx.ui.parts.IDirtyStateProvider;
import org.eclipse.gef.mvc.fx.ui.parts.IDirtyStateProviderFactory;
import org.eclipse.gef.mvc.fx.ui.parts.ISelectionProviderFactory;
import org.eclipse.gef.mvc.fx.ui.properties.IPropertySheetPageFactory;
import org.eclipse.gef.mvc.fx.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import javafx.embed.swt.FXCanvas;

/**
 * The {@link MvcFxUiModule} contains Eclipse UI specific bindings in the
 * context of an MVC.FX application.
 *
 * @author anyssen
 *
 */
public class MvcFxUiModule extends AbstractModule {

	/**
	 * Binds an {@link IFXCanvasFactory} that can be used to create an
	 * {@link FXCanvas}.
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
	 * Binds a factory for the creation of
	 * {@link HistoryBasedDirtyStateProvider} as {@link IDirtyStateProvider}.
	 */
	protected void bindIDirtyStateProviderFactory() {
		binder().bind(IDirtyStateProviderFactory.class)
				.toInstance(new IDirtyStateProviderFactory() {

					@Override
					public IDirtyStateProvider create(
							IWorkbenchPart workbenchPart) {
						return new HistoryBasedDirtyStateProvider(
								(IOperationHistory) workbenchPart
										.getAdapter(IOperationHistory.class),
								(IUndoContext) workbenchPart
										.getAdapter(IUndoContext.class));
					}
				});
	}

	/**
	 * Binds {@link IOperationHistory} to the operation history of the Eclipse
	 * workbench.
	 */
	protected void bindIOperationHistory() {
		binder().bind(IOperationHistory.class).toInstance(PlatformUI
				.getWorkbench().getOperationSupport().getOperationHistory());
	}

	/**
	 * Binds a factory for assisted injection of
	 * {@link UndoablePropertySheetPage} as {@link IPropertySheetPage}.
	 */
	protected void bindIPropertySheetPageFactory() {
		install(new FactoryModuleBuilder()
				.implement(IPropertySheetPage.class,
						UndoablePropertySheetPage.class)
				.build(IPropertySheetPageFactory.class));
	}

	/**
	 * Binds a factory for the creation of {@link ContentSelectionProvider} as
	 * {@link ISelectionProvider}.
	 */
	protected void bindISelectionProviderFactory() {
		binder().bind(ISelectionProviderFactory.class)
				.toInstance(new ISelectionProviderFactory() {

					@Override
					public ISelectionProvider create(
							IWorkbenchPart workbenchPart) {
						IViewer contentViewer = null;
						if (workbenchPart instanceof AbstractFXView) {
							contentViewer = ((AbstractFXView) workbenchPart)
									.getDomain()
									.getAdapter(AdapterKey.get(IViewer.class,
											IDomain.CONTENT_VIEWER_ROLE));
						} else if (workbenchPart instanceof AbstractFXEditor) {
							contentViewer = ((AbstractFXEditor) workbenchPart)
									.getDomain()
									.getAdapter(AdapterKey.get(IViewer.class,
											IDomain.CONTENT_VIEWER_ROLE));
						} else {
							throw new IllegalArgumentException(
									"Cannot handle " + workbenchPart);
						}
						return new ContentSelectionProvider(contentViewer);
					}
				});
	}

	@Override
	protected void configure() {
		// bindings related to workbench integration
		bindIOperationHistory();

		bindFXCanvasFactory();

		bindISelectionProviderFactory();
		bindIDirtyStateProviderFactory();
		bindIPropertySheetPageFactory();
	}

}
