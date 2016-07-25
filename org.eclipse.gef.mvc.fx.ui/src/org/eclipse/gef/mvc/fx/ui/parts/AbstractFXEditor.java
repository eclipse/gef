/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #470612
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.parts;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.ui.parts.ISelectionProviderFactory;
import org.eclipse.gef.mvc.ui.properties.IPropertySheetPageFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

/**
 * Abstract base class for editors. The {@link FXDomain},
 * {@link IFXCanvasFactory}, and {@link ISelectionProvider} are injected into
 * the editor on construction.
 *
 * @author Alexander Nyßen (anyssen)
 * @author Matthias Wienand (mwienand)
 */
public abstract class AbstractFXEditor extends EditorPart {

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

	private IOperationHistoryListener operationHistoryListener;
	private boolean isDirty;

	private UndoRedoActionGroup undoRedoActionGroup;

	/**
	 * Constructs a new {@link AbstractFXEditor} and uses the given
	 * {@link Injector} to inject its members.
	 *
	 * @param injector
	 *            The {@link Injector} that is used to inject the editor's
	 *            members.
	 */
	// TOOD: use executable extension factory to inject this class
	public AbstractFXEditor(final Injector injector) {
		injector.injectMembers(this);
	}

	/**
	 * Activates the editor by activating its {@link FXDomain}.
	 */
	protected void activate() {
		domain.activate();
	}

	/**
	 * Uses the {@link IFXCanvasFactory} to create the {@link FXCanvas} that
	 * allows the interoperability between SWT and JavaFX.
	 *
	 * @param parent
	 *            The parent {@link Composite} in which the {@link FXCanvas} is
	 *            created.
	 * @return The {@link FXCanvas} created by the {@link IFXCanvasFactory}.
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
	 * Deactivates the editor by deactivating its {@link FXDomain}.
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

		// unregister operation history listener
		domain.getOperationHistory()
				.removeOperationHistoryListener(operationHistoryListener);
		operationHistoryListener = null;

		// unregister selection provider
		if (selectionProvider != null) {
			getSite().setSelectionProvider(null);
			if (selectionProvider instanceof IDisposable) {
				((IDisposable) selectionProvider).dispose();
			}
		}

		// XXX: The propertySheetPage does not need to be disposed, as this is
		// already done by the PropertySheet (view) when this view is closed.
		propertySheetPage = null;
		propertySheetPageFactory = null;

		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.dispose();
			undoRedoActionGroup = null;
		}

		domain.dispose();
		domain = null;

		canvasFactory = null;
		if (!canvas.isDisposed()) {
			canvas.dispose();
		}
		canvas = null;

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
		} else if (UndoRedoActionGroup.class.equals(key)) {
			// used by action bar contributor
			return undoRedoActionGroup;
		} else if (IUndoContext.class.equals(key)) {
			return domain.getUndoContext();
		} else if (IOperationHistory.class.equals(key)) {
			return domain.getOperationHistory();
		}
		return super.getAdapter(key);
	}

	/**
	 * Returns the {@link FXCanvas} that was previously created by the
	 * {@link IFXCanvasFactory} which was previously injected into this editor.
	 *
	 * @return The {@link FXCanvas} that was previously created by the
	 *         {@link IFXCanvasFactory}.
	 */
	protected FXCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Returns the {@link FXViewer} of the {@link FXDomain} which was previously
	 * injected into this editor.
	 *
	 * @return The {@link FXViewer} of the {@link FXDomain} which was previously
	 *         injected into this editor.
	 */
	protected FXViewer getContentViewer() {
		return domain.getAdapter(
				AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link FXDomain} that was previously injected into this
	 * editor.
	 *
	 * @return The {@link FXDomain} that was previously injected into this
	 *         editor.
	 */
	public FXDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link ISelectionProvider} used by this
	 * {@link AbstractFXEditor}. May be <code>null</code> in case no injection
	 * provider is used.
	 *
	 * @return {@link ISelectionProvider}
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}

	/**
	 * Hooks all viewers that are part of this editor into the {@link FXCanvas}.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		final FXViewer contentViewer = getContentViewer();
		canvas.setScene(new Scene(contentViewer.getCanvas()));
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);

		// register selection provider (if we want to a provide selection)
		if (selectionProvider != null) {
			site.setSelectionProvider(selectionProvider);
		}

		operationHistoryListener = new IOperationHistoryListener() {
			@Override
			public void historyNotification(final OperationHistoryEvent event) {
				IUndoableOperation operation = event.getOperation();
				if (event
						.getEventType() == OperationHistoryEvent.OPERATION_ADDED
						&& event.getHistory().getUndoHistory(
								operation.getContexts()[0]).length > 0) {
					if (!(operation instanceof ITransactionalOperation)
							|| ((ITransactionalOperation) operation)
									.isContentRelevant()) {
						setDirty(true);
					}
				}
			}
		};

		undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
				(IUndoContext) getAdapter(IUndoContext.class), true);
		undoRedoActionGroup.fillActionBars(site.getActionBars());

		getDomain().getOperationHistory()
				.addOperationHistoryListener(operationHistoryListener);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * Sets the dirty flag of this editor to the given value.
	 *
	 * @param isDirty
	 *            <code>true</code> to indicate that the editor's contents
	 *            changed, otherwise <code>false</code>.
	 */
	protected void setDirty(boolean isDirty) {
		if (this.isDirty != isDirty) {
			this.isDirty = isDirty;
			firePropertyChange(PROP_DIRTY);
		}
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	/**
	 * Unhooks all viewers that are part of this editor.
	 */
	protected void unhookViewers() {
		// TODO: What about taking the visuals out of the canvas?
	}

}
