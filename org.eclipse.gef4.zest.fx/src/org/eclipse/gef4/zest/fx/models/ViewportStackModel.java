/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.models;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Stack;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class ViewportStackModel {

	private static class ViewportState {
		public double tx;
		public double ty;
		public AffineTransform transform;
	}

	private Stack<ViewportState> viewportStack = new Stack<ViewportState>();
	private Set<Graph> skipNextLayout = Collections
			.newSetFromMap(new IdentityHashMap<Graph, Boolean>());

	public ViewportStackModel() {
	}

	public void addSkipNextLayout(Graph graph) {
		skipNextLayout.add(graph);
	}

	public void pop(ViewportModel viewportModel) {
		if (!viewportStack.isEmpty()) {
			ViewportState state = viewportStack.pop();
			viewportModel.setTranslateX(state.tx);
			viewportModel.setTranslateY(state.ty);
			viewportModel.setContentsTransform(state.transform);
		}
	}

	public void push(ViewportModel viewportModel) {
		ViewportState state = new ViewportState();
		state.tx = viewportModel.getTranslateX();
		state.ty = viewportModel.getTranslateY();
		state.transform = viewportModel.getContentsTransform();
		viewportStack.push(state);
	}

	/**
	 * Removes the skip-next-layout-flag for the given {@link Graph}.
	 *
	 * @param graph
	 * @return <code>true</code> if the flag was set for the given {@link Graph}
	 *         , otherwise <code>false</code>.
	 */
	public boolean removeSkipNextLayout(Graph graph) {
		return skipNextLayout.remove(graph);
	}

}
