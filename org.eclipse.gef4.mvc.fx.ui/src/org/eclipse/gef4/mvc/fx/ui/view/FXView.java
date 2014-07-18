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
package org.eclipse.gef4.mvc.fx.ui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javafx.embed.swt.FXCanvas;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.viewer.FXCanvasSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

// TODO: inject viewer and domain
public abstract class FXView extends ViewPart {

	private class SelectionPropertyChangeListener implements
			PropertyChangeListener {
		@SuppressWarnings("rawtypes")
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (ISelectionModel.SELECTION_PROPERTY.equals(event
					.getPropertyName())) {
				// forward selection changes to selection provider (in case
				// there is any)
				ISelectionProvider selectionProvider = (ISelectionProvider) getAdapter(ISelectionProvider.class);
				if (selectionProvider != null) {
					if (event.getNewValue() == null) {
						selectionProvider
								.setSelection(StructuredSelection.EMPTY);
					} else {
						selectionProvider.setSelection(new StructuredSelection(
								(List) event.getNewValue()));
					}
				}
			}
		}
	}

	@Inject
	private FXDomain domain;

	// viewer may not be injected as field, as we need to create it inside
	// createControl()
	private FXViewer viewer;
	private FXCanvas canvas = null;

	@Inject(optional = true)
	private ISelectionProvider selectionProvider;

	private PropertyChangeListener selectionPropertyChangeListener = new SelectionPropertyChangeListener();

	private UndoRedoActionGroup undoRedoActionGroup;
	private IPropertySheetPage propertySheetPage;

	private IFXCanvasFactory canvasFactory;
	private Injector injector;

	public FXView(Injector injector) {
		this.injector = injector;
		injector.injectMembers(this);
	}

	protected FXCanvas createCanvas(Composite parent) {
		return canvasFactory.createCanvas(parent);
	}

	@Override
	public void createPartControl(Composite parent) {
		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);
		viewer = createViewer(canvas);

		// domain was already injected, bind viewer to it now
		viewer.setDomain(domain);

		// populate viewer
		viewer.setContents(getContents());

		// register listener to provide selection to workbench
		if (selectionProvider != null) {
			getViewer().getSelectionModel().addPropertyChangeListener(
					selectionPropertyChangeListener);
		}
	}

	protected FXViewer createViewer(final FXCanvas canvas) {
		FXViewer viewer = injector.getInstance(FXViewer.class);
		viewer.setSceneContainer(new FXCanvasSceneContainer(viewer, canvas));
		return viewer;
	}

	@Override
	public void dispose() {
		// unregister listener to provide selections
		if (selectionProvider != null) {
			getViewer().getSelectionModel().removePropertyChangeListener(
					selectionPropertyChangeListener);
		}
		super.dispose();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
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

	protected abstract List<Object> getContents();

	protected FXDomain getDomain() {
		return domain;
	}

	protected FXViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		UndoRedoActionGroup undoRedoActionGroup = (UndoRedoActionGroup) getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(site.getActionBars());
		}

		// register selection provider (if we want to a provide selection)
		if (selectionProvider != null) {
			site.setSelectionProvider(selectionProvider);
		}
	}

	@Inject
	public void setCanvasFactory(IFXCanvasFactory canvasFactory) {
		this.canvasFactory = canvasFactory;
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
