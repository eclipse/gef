/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.parts;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.ui.parts.ISelectionProviderFactory;
import org.eclipse.gef4.mvc.ui.properties.IPropertySheetPageFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
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
	private FXDomain domain;

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
	private DeleteActionHandler deleteActionHandler;

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
	 * Activates this {@link AbstractFXView} by activating the {@link FXDomain}
	 * that was previously injected.
	 */
	protected void activate() {
		domain.activate();
	}

	/**
	 * Creates an {@link FXCanvas} to allow the interoperability between SWT and
	 * JavaFX using the {@link IFXCanvasFactory} that was previously injected.
	 *
	 * @param parent
	 *            The {@link Composite} that serves as the parent for the
	 *            created {@link FXCanvas}.
	 * @return The {@link FXCanvas} that is created by the previously injected
	 *         {@link IFXCanvasFactory}.
	 */
	protected FXCanvas createCanvas(final Composite parent) {
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

	/**
	 * Creates an {@link IPropertySheetPage} using the injected
	 * {@link IPropertySheetPageFactory}, if present.
	 *
	 * @return An {@link IPropertySheetPage}, or <code>null</code> in case no
	 *         factory was injected.
	 */
	protected IPropertySheetPage createPropertySheetPage() {
		if (propertySheetPageFactory != null) {
			return propertySheetPageFactory.create(this);
		}
		return null;
	}

	/**
	 * Deactivates this {@link AbstractFXView} by deactivating its
	 * {@link FXDomain} that was previously injected.
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
		if (selectionProvider != null) {
			getSite().setSelectionProvider(null);
			if (selectionProvider instanceof IDisposable) {
				((IDisposable) selectionProvider).dispose();
			}
		}

		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.dispose();
		}

		deleteActionHandler.init(null);

		domain.dispose();
		super.dispose();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class key) {
		// Provide a default selection provider (subclasses may overwrite by
		// handling the key and returning a different implementation
		// replace with binding
		if (ISelectionProvider.class.equals(key)) {
			return selectionProvider;
		}
		// contribute to Properties view
		else if (IPropertySheetPage.class.equals(key)) {
			if (propertySheetPage == null) {
				propertySheetPage = createPropertySheetPage();
			}
			return propertySheetPage;
		} else if (IUndoContext.class.equals(key)) {
			return domain.getUndoContext();
		} else if (IOperationHistory.class.equals(key)) {
			return domain.getOperationHistory();
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
	 * Returns the {@link FXViewer} of the {@link FXDomain} that was previously
	 * injected.
	 *
	 * @return The {@link FXViewer} of the {@link FXDomain} that was previously
	 *         injected.
	 */
	protected FXViewer getContentViewer() {
		return domain.getAdapter(
				AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link FXDomain} that was previously injected.
	 *
	 * @return The {@link FXDomain} that was previously injected.
	 */
	public FXDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link ISelectionProvider} used by this
	 * {@link AbstractFXView}. May be <code>null</code> in case no injection
	 * provider is used.
	 *
	 * @return {@link ISelectionProvider}
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}

	/**
	 * Hooks all viewers that are part of this {@link AbstractFXView} into the
	 * {@link FXCanvas} that was previously created by the injected
	 * {@link IFXCanvasFactory}.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		final FXViewer contentViewer = getContentViewer();
		canvas.setScene(new Scene(contentViewer.getCanvas()));
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
				(IUndoContext) getAdapter(IUndoContext.class), true);
		undoRedoActionGroup.fillActionBars(site.getActionBars());

		deleteActionHandler = new DeleteActionHandler();
		deleteActionHandler.init(getContentViewer());
		site.getActionBars().setGlobalActionHandler(
				ActionFactory.DELETE.getId(), deleteActionHandler);
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
