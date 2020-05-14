/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #470612
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
import org.eclipse.gef.mvc.fx.ui.actions.SelectAllAction;
import org.eclipse.gef.mvc.fx.ui.properties.IPropertySheetPageFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

/**
 * Abstract base class for editors. The {@link HistoricizingDomain},
 * {@link IFXCanvasFactory}, and {@link ISelectionProvider} are injected into
 * the editor on construction.
 *
 * @author Alexander Nyßen (anyssen)
 * @author Matthias Wienand (mwienand)
 */
public abstract class AbstractFXEditor extends EditorPart {

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

	@Inject(optional = true)
	private IDirtyStateProviderFactory dirtyStateProviderFactory;
	private IDirtyStateProvider dirtyStateProvider;
	private ChangeListener<Boolean> dirtyStateNotifier;

	private UndoRedoActionGroup undoRedoActionGroup;

	private DeleteAction deleteAction;

	private SelectAllAction selectAllAction;

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
	 * Activates the editor by activating its {@link IDomain}.
	 */
	protected void activate() {
		domain.activate();
	}

	/**
	 * Creates the actions for this editor and registers them in the editor's
	 * site action bar.
	 *
	 */
	protected void createActions() {
		undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
				(IUndoContext) getAdapter(IUndoContext.class), true);

		deleteAction = new DeleteAction();
		getContentViewer().setAdapter(deleteAction);

		selectAllAction = new SelectAllAction();
		getContentViewer().setAdapter(selectAllAction);
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
	private FXCanvas createCanvas(final Composite parent) {
		return canvasFactory.createCanvas(parent, SWT.NONE);
	}

	private IDirtyStateProvider createDirtyStateProvider() {
		if (dirtyStateProviderFactory != null) {
			return dirtyStateProviderFactory.create(this);
		}
		return null;
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
			if (selectionProvider != null) {
				getSite().setSelectionProvider(selectionProvider);
			}
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
	 * Deactivates the editor by deactivating its {@link IDomain}.
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

		// The support class used for handling the dirty state
		if (dirtyStateProvider != null) {
			if (dirtyStateNotifier != null) {
				dirtyStateProvider.dirtyProperty()
						.removeListener(dirtyStateNotifier);
			}
			if (dirtyStateProvider instanceof IDisposable) {
				((IDisposable) dirtyStateProvider).dispose();
			}
			dirtyStateProvider = null;
			dirtyStateNotifier = null;
		}

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
	 * Dispose the actions created by this editor.
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
		// contribute to Properties view
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
		if (UndoRedoActionGroup.class.equals(key)) {
			// used by action bar contributor
			return undoRedoActionGroup;
		}
		if (DeleteAction.class.equals(key)) {
			// used by action bar contributor
			return deleteAction;
		}
		if (SelectAllAction.class.equals(key)) {
			// used by action bar contributor
			return selectAllAction;
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
	 * Returns the {@link IViewer} of the {@link IDomain} which was previously
	 * injected into this editor.
	 *
	 * @return The {@link IViewer} of the {@link IDomain} which was previously
	 *         injected into this editor.
	 */
	public IViewer getContentViewer() {
		return domain.getAdapter(
				AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link IDomain} that was previously injected into this
	 * editor.
	 *
	 * @return The {@link IDomain} that was previously injected into this
	 *         editor.
	 */
	public IDomain getDomain() {
		return domain;
	}

	/**
	 * Hooks all viewers that are part of this editor into the {@link FXCanvas}.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		canvas.setScene(new Scene(getContentViewer().getCanvas()));
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);

		createActions();

		dirtyStateProvider = createDirtyStateProvider();
		if (dirtyStateProvider != null) {
			dirtyStateNotifier = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					AbstractFXEditor.this.firePropertyChange(PROP_DIRTY);
				}
			};
			dirtyStateProvider.dirtyProperty().addListener(dirtyStateNotifier);
		}
	}

	@Override
	public boolean isDirty() {
		if (dirtyStateProvider == null) {
			return false;
		}
		return dirtyStateProvider.isDirty();
	}

	/**
	 * Marks the current state of the editor to be non-dirty. Should be called
	 * from {@link #doSave(org.eclipse.core.runtime.IProgressMonitor)} and
	 * {@link #doSaveAs()} in case of successful save.
	 */
	protected void markNonDirty() {
		if (dirtyStateProvider != null) {
			dirtyStateProvider.markNonDirty();
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
