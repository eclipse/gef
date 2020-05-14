/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - pull up actions
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.parts;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.ui.actions.DeleteAction;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportAction;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ScrollActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.SelectAllAction;
import org.eclipse.gef.mvc.fx.ui.actions.ZoomActionGroup;
import org.eclipse.gef.mvc.fx.ui.properties.IPropertySheetPageFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

/**
 * Abstract base class for views.
 *
 * @author Alexander Nyßen (anyssen)
 *
 */
public abstract class AbstractFXView extends ViewPart {

	@Inject
	private IDomain domain;

	@Inject
	private IFXCanvasFactory canvasFactory;
	private FXCanvas canvas = null;

	@Inject(optional = true)
	private ISelectionProviderFactory selectionProviderFactory;
	private ISelectionProvider selectionProvider;

	@Inject(optional = true)
	private IPropertySheetPageFactory propertySheetPageFactory;
	private IPropertySheetPage propertySheetPage;

	private UndoRedoActionGroup undoRedoActionGroup;
	private DeleteAction deleteAction;
	private SelectAllAction selectAllAction;

	private ZoomActionGroup zoomActionGroup;
	private FitToViewportActionGroup fitToViewportActionGroup;
	private ScrollActionGroup scrollActionGroup;

	/**
	 * Constructs a new {@link AbstractFXView} that uses the given
	 * {@link Injector} to inject its members.
	 *
	 * @param injector
	 *            The {@link Injector} that is used to inject the members of
	 *            this {@link AbstractFXView}.
	 */
	// TOOD: use executable extension factory to inject this class
	public AbstractFXView(final Injector injector) {
		injector.injectMembers(this);
	}

	/**
	 * Activates this {@link AbstractFXView} by activating the {@link IDomain}
	 * that was previously injected.
	 */
	protected void activate() {
		domain.activate();
	}

	/**
	 * Create actions for this view and registers at the action bars of the
	 * view's site.
	 */
	protected void createActions() {
		IViewSite site = getViewSite();
		IActionBars actionBars = site.getActionBars();
		undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
				(IUndoContext) getAdapter(IUndoContext.class), true);
		undoRedoActionGroup.fillActionBars(actionBars);

		deleteAction = new DeleteAction();
		getContentViewer().setAdapter(deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				deleteAction);

		selectAllAction = new SelectAllAction();
		getContentViewer().setAdapter(selectAllAction);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
				selectAllAction);

		zoomActionGroup = new ZoomActionGroup(new FitToViewportAction());
		getContentViewer().setAdapter(zoomActionGroup);
		fitToViewportActionGroup = new FitToViewportActionGroup();
		getContentViewer().setAdapter(fitToViewportActionGroup);
		scrollActionGroup = new ScrollActionGroup();
		getContentViewer().setAdapter(scrollActionGroup);

		IToolBarManager mgr = actionBars.getToolBarManager();
		zoomActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		fitToViewportActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		scrollActionGroup.fillActionBars(actionBars);
	}

	private FXCanvas createCanvas(final Composite parent) {
		return canvasFactory.createCanvas(parent, SWT.NONE);
	}

	@Override
	public void createPartControl(final Composite parent) {
		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// hook viewer controls and selection forwarder
		hookViewers();

		// register selection provider (if we want to a provide selection)
		if (selectionProviderFactory != null) {
			selectionProvider = selectionProviderFactory.create(this);
			getSite().setSelectionProvider(selectionProvider);
		}

		// activate domain
		activate();
	}

	private IPropertySheetPage createPropertySheetPage() {
		if (propertySheetPageFactory != null) {
			return propertySheetPageFactory.create(this);
		}
		return null;
	}

	/**
	 * Deactivates this {@link AbstractFXView} by deactivating its
	 * {@link IDomain} that was previously injected.
	 */
	protected void deactivate() {
		domain.deactivate();
	}

	@Override
	public void dispose() {
		// deactivate domain
		deactivate();

		// unhook selection forwarder
		unhookViewers();

		// unregister selection provider
		selectionProviderFactory = null;
		if (selectionProvider != null) {
			getSite().setSelectionProvider(null);
			if (selectionProvider instanceof IDisposable) {
				((IDisposable) selectionProvider).dispose();
			}
			selectionProvider = null;
		}

		// XXX: The propertySheetPage does not need to be disposed, as this is
		// already done by the PropertySheet (view) when this view is closed.
		propertySheetPage = null;
		propertySheetPageFactory = null;

		disposeActions();

		domain.dispose();
		domain = null;

		canvasFactory = null;
		if (!canvas.isDisposed()) {
			canvas.dispose();
		}
		canvas = null;

		super.dispose();
	}

	/**
	 * Dispose the actions created by this view.
	 */
	protected void disposeActions() {
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.dispose();
			undoRedoActionGroup = null;
		}

		if (deleteAction != null) {
			getContentViewer().unsetAdapter(deleteAction);
			deleteAction = null;
		}

		if (selectAllAction != null) {
			getContentViewer().unsetAdapter(selectAllAction);
			selectAllAction = null;
		}

		if (fitToViewportActionGroup != null) {
			getContentViewer().unsetAdapter(fitToViewportActionGroup);
			fitToViewportActionGroup.dispose();
			fitToViewportActionGroup = null;
		}
		if (zoomActionGroup != null) {
			getContentViewer().unsetAdapter(zoomActionGroup);
			zoomActionGroup.dispose();
			zoomActionGroup = null;
		}
		if (scrollActionGroup != null) {
			getContentViewer().unsetAdapter(scrollActionGroup);
			scrollActionGroup.dispose();
			scrollActionGroup = null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class key) {
		// Provide a default selection provider (subclasses may overwrite by
		// handling the key and returning a different implementation
		// replace with binding
		if (ISelectionProvider.class.equals(key)) {
			if (selectionProvider != null) {
				return selectionProvider;
			}
		}
		// contribute to Properties view (only created if required)
		if (IPropertySheetPage.class.equals(key)) {
			if (propertySheetPage == null) {
				propertySheetPage = createPropertySheetPage();
			}
			if (propertySheetPage != null) {
				return propertySheetPage;
			}
		}
		if (IUndoContext.class.equals(key)) {
			if (domain instanceof HistoricizingDomain) {
				return ((HistoricizingDomain) domain).getUndoContext();
			}
		}
		if (IOperationHistory.class.equals(key)) {
			if (domain instanceof HistoricizingDomain) {
				return ((HistoricizingDomain) domain).getOperationHistory();
			}
		}
		return super.getAdapter(key);
	}

	/**
	 * Returns the {@link FXCanvas} that was previously created by the injected
	 * {@link IFXCanvasFactory}.
	 *
	 * @return The {@link FXCanvas} that was previously created by the injected
	 *         {@link IFXCanvasFactory}.
	 */
	protected FXCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Returns the {@link IViewer} of the {@link IDomain} that was previously
	 * injected.
	 *
	 * @return The {@link IViewer} of the {@link IDomain} that was previously
	 *         injected.
	 */
	public IViewer getContentViewer() {
		return domain.getAdapter(
				AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link IDomain} that was previously injected.
	 *
	 * @return The {@link IDomain} that was previously injected.
	 */
	public IDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link ActionGroup} that manages the fit-to-viewport actions.
	 *
	 * @return the {@link ActionGroup} that manages the fit-to-viewport actions.
	 * @since 5.1
	 */
	protected FitToViewportActionGroup getFitToViewportActionGroup() {
		return fitToViewportActionGroup;
	}

	/**
	 * Hooks all viewers that are part of this {@link AbstractFXView} into the
	 * {@link FXCanvas} that was previously created by the injected
	 * {@link IFXCanvasFactory}.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		canvas.setScene(new Scene(getContentViewer().getCanvas()));
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		createActions();
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	/**
	 * Unhooks all viewers that are part of this {@link AbstractFXView}.
	 */
	protected void unhookViewers() {
		// TODO: What about taking the visuals out of the canvas?
	}
}
