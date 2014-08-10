/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.view;

import java.util.ArrayList;
import java.util.List;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.ui.parts.FXView;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class ZestFxUiView extends FXView {

	private Object graph;

	public ZestFxUiView(Injector injector) {
		super(injector);
	}

	@Override
	protected FXCanvas createCanvas(Composite parent) {
		FXCanvas canvas = super.createCanvas(parent);
		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				Rectangle bounds = getCanvas().getBounds();
				getViewer().getViewportModel().setWidth(bounds.width);
				getViewer().getViewportModel().setHeight(bounds.height);
			}
		});
		return canvas;
	}

	@Override
	protected List<Object> getContents() {
		List<Object> contents = new ArrayList<Object>(1);
		contents.add(graph);
		return contents;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
		if (!getViewer().getContents().isEmpty()) {
			getViewer().setContents(getContents());
		}
	}

}
