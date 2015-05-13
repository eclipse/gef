/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
import javafx.scene.Node;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.viewer.FXCanvasSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class FXView extends ViewPart {

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

	// TOOD: use executable extension factory to inject this class
	public FXView(final Injector injector) {
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
			selectionForwarder = new SelectionForwarder<Node>(
					selectionProvider, getViewer());
		}
	}

	@Override
	public void dispose() {
		// unregister listener to provide selections
		if (selectionForwarder != null) {
			selectionForwarder.dispose();
			selectionForwarder = null;
		}

		domain.deactivate();
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
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		final UndoRedoActionGroup undoRedoActionGroup = (UndoRedoActionGroup) getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(site.getActionBars());
		}

		// register selection provider (if we want to a provide selection)
		if (selectionProvider != null) {
			site.setSelectionProvider(selectionProvider);
		}
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
