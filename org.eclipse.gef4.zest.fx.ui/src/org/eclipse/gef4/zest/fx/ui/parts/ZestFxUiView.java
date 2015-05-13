/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.ui.parts.FXView;
import org.eclipse.gef4.mvc.models.ContentModel;

import com.google.inject.Injector;

public class ZestFxUiView extends FXView {

	private Object graph;

	public ZestFxUiView(Injector injector) {
		super(injector);
	}

	@Override
	protected List<Object> getContents() {
		List<Object> contents = new ArrayList<Object>(1);
		contents.add(graph);
		return contents;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
		if (!getViewer().getAdapter(ContentModel.class).getContents().isEmpty()) {
			getViewer().getAdapter(ContentModel.class).setContents(
					getContents());
		}
	}

}
