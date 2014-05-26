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
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.ui.viewer.FXCanvasViewer;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

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

	private class DefaultSelectionProvider implements ISelectionProvider {

		private ISelection selection;
		private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

		@Override
		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			selectionChangedListeners.add(listener);
		}

		@Override
		public ISelection getSelection() {
			return selection;
		}

		@Override
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			selectionChangedListeners.remove(listener);
		}

		@Override
		public void setSelection(ISelection selection) {
			final SelectionChangedEvent e = new SelectionChangedEvent(this,
					selection);
			for (final ISelectionChangedListener l : selectionChangedListeners) {
				SafeRunner.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(e);
					}
				});
			}
		}
	}

	private FXCanvas canvas = null;
	private IUndoContext undoContext;
	private IOperationHistory operationHistory;
	private FXDomain domain;
	private FXCanvasViewer viewer;

	private ISelectionProvider selectionProvider = null;
	private PropertyChangeListener selectionPropertyChangeListener = new SelectionPropertyChangeListener();
	private UndoRedoActionGroup undoRedoActionGroup;
	private IPropertySheetPage propertySheetPage;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		UndoRedoActionGroup undoRedoActionGroup = (UndoRedoActionGroup) getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(site.getActionBars());
		}

		// register selection provider (if we want to a provide selection)
		ISelectionProvider selectionProvider = (ISelectionProvider) getAdapter(ISelectionProvider.class);
		if (selectionProvider != null) {
			site.setSelectionProvider(selectionProvider);
		}
	}

	@Override
	public void dispose() {
		// unregister listener to provide selections
		getViewer().getSelectionModel().removePropertyChangeListener(
				selectionPropertyChangeListener);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = createCanvas(parent);
		viewer = createViewer(canvas);
		configureViewer(viewer);
		domain = createDomain();
		configureDomain(domain);
		viewer.setDomain(domain);
		viewer.setContents(getContents());

		// register listener to provide selection to workbench
		getViewer().getSelectionModel().addPropertyChangeListener(
				selectionPropertyChangeListener);
	}

	protected FXCanvas getCanvas() {
		return canvas;
	}

	protected FXDomain getDomain() {
		return domain;
	}

	protected FXCanvasViewer getViewer() {
		return viewer;
	}

	protected FXCanvas createCanvas(Composite parent) {
		return new SwtFXCanvas(parent, SWT.NONE);
	}

	protected FXDomain createDomain() {
		return new FXDomain();
	}

	protected FXCanvasViewer createViewer(final FXCanvas canvas) {
		return new FXCanvasViewer(canvas) {
			@Override
			protected Scene createScene(Parent rootVisual) {
				return new SwtFXScene(rootVisual);
			}
		};
	}

	protected void configureDomain(FXDomain domain) {
		domain.setOperationHistory((IOperationHistory) getAdapter(IOperationHistory.class));
		domain.setUndoContext((IUndoContext) getAdapter(IUndoContext.class));
	}

	protected void configureViewer(IFXViewer viewer) {
		viewer.setRootPart(new FXRootPart());
		viewer.setHandlePartFactory(getHandlePartFactory());
		viewer.setContentPartFactory(getContentPartFactory());
		viewer.setFeedbackPartFactory(getFeedbackPartFactory());
	}

	protected abstract List<Object> getContents();

	protected abstract IContentPartFactory<Node> getContentPartFactory();

	protected abstract IHandlePartFactory<Node> getHandlePartFactory();

	protected abstract IFeedbackPartFactory<Node> getFeedbackPartFactory();

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		// Provide a default selection provider (subclasses may overwrite by
		// handling the key and returning a different implementation
		if (ISelectionProvider.class.equals(key)) {
			if (selectionProvider == null) {
				selectionProvider = new DefaultSelectionProvider();
			}
			return selectionProvider;
		}
		// contribute to Properties view
		if (IPropertySheetPage.class.equals(key)) {
			if (propertySheetPage == null) {
				propertySheetPage = new UndoablePropertySheetPage(
						(IOperationHistory) getAdapter(IOperationHistory.class),
						(IUndoContext) getAdapter(IUndoContext.class),
						undoRedoActionGroup);
			}
			return propertySheetPage;
		}
		if(UndoRedoActionGroup.class.equals(key)){
			if(undoRedoActionGroup == null){
				undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
						(IUndoContext) getAdapter(IUndoContext.class), true);
			}
			return undoRedoActionGroup;
		}
		if (IUndoContext.class.equals(key)) {
			if (undoContext == null) {
				IWorkbench workbench = getSite().getWorkbenchWindow()
						.getWorkbench();
				undoContext = workbench.getOperationSupport().getUndoContext();
			}
			return undoContext;
		}
		if (IOperationHistory.class.equals(key)) {
			if (operationHistory == null) {
				IWorkbench workbench = getSite().getWorkbenchWindow()
						.getWorkbench();
				operationHistory = workbench.getOperationSupport()
						.getOperationHistory();
			}
			return operationHistory;
		}
		return super.getAdapter(key);
	}

}
