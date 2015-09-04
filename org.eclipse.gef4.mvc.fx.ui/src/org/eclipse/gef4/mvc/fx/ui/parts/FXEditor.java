/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.mvc.fx.ui.parts;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Abstract base class for editors. The {@link FXDomain},
 * {@link IFXCanvasFactory}, and {@link ISelectionProvider} are injected into
 * the editor on construction.
 *
 * @author Alexander Nyßen (anyssen)
 * @author Matthias Wienand (mwienand)
 */
// TODO: make concrete or rename
public abstract class FXEditor extends EditorPart {

	@Inject
	private FXDomain domain;

	@Inject
	private IFXCanvasFactory canvasFactory;

	@Inject(optional = true)
	private ISelectionProvider selectionProvider;

	private SelectionForwarder<Node> selectionForwarder;

	private FXCanvas canvas = null;

	private UndoRedoActionGroup undoRedoActionGroup;
	private IPropertySheetPage propertySheetPage;

	private IOperationHistoryListener operationHistoryListener;
	private boolean isDirty;

	/**
	 * Constructs a new {@link FXEditor} and uses the given {@link Injector} to
	 * inject its members.
	 *
	 * @param injector
	 *            The {@link Injector} that is used to inject the editor's
	 *            members.
	 */
	// TOOD: use executable extension factory to inject this class
	public FXEditor(final Injector injector) {
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
		return canvasFactory.createCanvas(parent);
	}

	@Override
	public void createPartControl(final Composite parent) {
		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// hook viewer controls and selection forwarder
		hookViewers();

		// activate domain
		activate();
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

		// unregister selection provider
		if (selectionProvider != null) {
			getSite().setSelectionProvider(null);
		}

		domain.dispose();
		super.dispose();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				propertySheetPage = new UndoablePropertySheetPage(
						(IOperationHistory) getAdapter(IOperationHistory.class),
						(IUndoContext) getAdapter(IUndoContext.class),
						(UndoRedoActionGroup) getAdapter(
								UndoRedoActionGroup.class));
			}
			return propertySheetPage;
		} else if (UndoRedoActionGroup.class.equals(key)) {
			if (undoRedoActionGroup == null) {
				undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
						(IUndoContext) getAdapter(IUndoContext.class), true);
			}
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
	 * Returns the {@link FXDomain} that was previously injected into this
	 * editor.
	 *
	 * @return The {@link FXDomain} that was previously injected into this
	 *         editor.
	 */
	protected FXDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link FXViewer} of the {@link FXDomain} which was previously
	 * injected into this editor.
	 *
	 * @return The {@link FXViewer} of the {@link FXDomain} which was previously
	 *         injected into this editor.
	 */
	protected FXViewer getViewer() {
		return domain.getAdapter(IViewer.class);
	}

	/**
	 * Hooks all viewers that are part of this editor into the {@link FXCanvas}.
	 * Also registers listeners for the propagation of a selection from the
	 * Eclipse Workbench to the editor and vice versa.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		final FXViewer contentViewer = getViewer();
		canvas.setScene(new Scene(contentViewer.getScrollPane()));

		// register listener to provide selection to workbench
		if (selectionProvider != null) {
			selectionForwarder = new SelectionForwarder<Node>(selectionProvider,
					contentViewer);
		}
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
				if (event
						.getEventType() == OperationHistoryEvent.OPERATION_ADDED
						&& event.getHistory().getUndoHistory(event
								.getOperation().getContexts()[0]).length > 0) {
					setDirty(true);
				}
			}
		};
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
	 * Unhooks all viewers that are part of this editor by unregistering the
	 * selection listeners.
	 */
	// TODO: What about taking the visuals out of the canvas?
	protected void unhookViewers() {
		// unregister listener to provide selections
		if (selectionForwarder != null) {
			selectionForwarder.dispose();
			selectionForwarder = null;
		}
	}

}
