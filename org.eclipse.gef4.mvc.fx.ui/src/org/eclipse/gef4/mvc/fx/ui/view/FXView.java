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

import java.util.List;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;

public abstract class FXView extends ViewPart {

	private FXCanvas canvas = null;
	private IUndoContext undoContext;
	private IOperationHistory operationHistory;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		IWorkbench workbench = site.getWorkbenchWindow().getWorkbench();
		undoContext = workbench.getOperationSupport().getUndoContext();
		operationHistory = workbench.getOperationSupport()
				.getOperationHistory();

		UndoRedoActionGroup undoRedoActionGroup = new UndoRedoActionGroup(
				getSite(), undoContext, true);
		undoRedoActionGroup.fillActionBars(site.getActionBars());
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = createCanvas(parent);
		FXViewer viewer = createViewer(canvas);
		configureViewer(viewer);
		FXDomain domain = createDomain();
		configureDomain(domain);
		viewer.setDomain(domain);
		viewer.setContents(getContents());
	}

	protected FXCanvas createCanvas(Composite parent) {
		return new SwtFXCanvas(parent, SWT.NONE);
	}

	protected FXDomain createDomain() {
		return new FXDomain();
	}

	protected FXViewer createViewer(final FXCanvas canvas) {
		return new FXViewer(canvas) {
			@Override
			protected Scene createScene(Parent rootVisual) {
				return new SwtFXScene(rootVisual);
			}
		};
	}

	protected void configureDomain(FXDomain domain) {
		domain.setOperationHistory(operationHistory);
		domain.setUndoContext(undoContext);
	}

	private void configureViewer(FXViewer viewer) {
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

}
