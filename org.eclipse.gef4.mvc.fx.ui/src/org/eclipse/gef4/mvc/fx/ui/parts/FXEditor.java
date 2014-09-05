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
package org.eclipse.gef4.mvc.fx.ui.parts;

import java.util.List;

import javafx.embed.swt.FXCanvas;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.viewer.FXCanvasSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author anyssen
 */
public abstract class FXEditor extends EditorPart {

	@Inject
	private FXDomain domain;

	@Inject
	private IFXCanvasFactory canvasFactory;

	@Inject(optional = true)
	private ISelectionProvider selectionProvider;

	private SelectionForwarder selectionForwarder;

	private FXCanvas canvas = null;

	private UndoRedoActionGroup undoRedoActionGroup;
	private IPropertySheetPage propertySheetPage;

	private IOperationHistoryListener operationHistoryListener;

	// TOOD: use executable extension factory to inject this class
	public FXEditor(final Injector injector) {
		injector.injectMembers(this);
	}

	protected FXCanvas createCanvas(final Composite parent) {
		return canvasFactory.createCanvas(parent);
	}

	@Override
	public void createPartControl(final Composite parent) {
		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// domain was already injected, hook viewer to controls (via scene
		// container)
		final FXViewer viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXCanvasSceneContainer(canvas));

		// activate domain
		domain.activate();

		// populate viewer
		viewer.getAdapter(ContentModel.class).setContents(getContents());

		// register listener to provide selection to workbench
		if (selectionProvider != null) {
			selectionForwarder = new SelectionForwarder(selectionProvider);
			getViewer().getAdapter(SelectionModel.class)
					.addPropertyChangeListener(selectionForwarder);
		}
	}

	@Override
	public void dispose() {
		domain.deactivate();

		domain.getOperationHistory().removeOperationHistoryListener(
				operationHistoryListener);
		domain.getOperationHistory().dispose(domain.getUndoContext(), true,
				true, true);

		// unregister listener to provide selections
		if (selectionProvider != null) {
			getViewer().getAdapter(SelectionModel.class)
					.removePropertyChangeListener(selectionForwarder);
		}
		super.dispose();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
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
						(UndoRedoActionGroup) getAdapter(UndoRedoActionGroup.class));
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

	protected FXCanvas getCanvas() {
		return canvas;
	}

	protected abstract List<? extends Object> getContents();

	protected FXDomain getDomain() {
		return domain;
	}

	protected FXViewer getViewer() {
		return domain.getAdapter(IViewer.class);
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
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		};

		getDomain().getOperationHistory().addOperationHistoryListener(
				operationHistoryListener);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
