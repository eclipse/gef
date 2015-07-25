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

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
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

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Abstract base class for views.
 *
 * @author Alexander Nyßen (anyssen)
 *
 */
// TODO: make concrete or rename
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

	protected void activate() {
		domain.activate();
	}

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

	protected FXCanvas getCanvas() {
		return canvas;
	}

	protected FXDomain getDomain() {
		return domain;
	}

	protected FXViewer getViewer() {
		return domain.getAdapter(IViewer.class);
	}

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
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		final UndoRedoActionGroup undoRedoActionGroup = (UndoRedoActionGroup) getAdapter(
				UndoRedoActionGroup.class);
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

	protected void unhookViewers() {
		// unregister listener to provide selections
		if (selectionForwarder != null) {
			selectionForwarder.dispose();
			selectionForwarder = null;
		}
	}

}
