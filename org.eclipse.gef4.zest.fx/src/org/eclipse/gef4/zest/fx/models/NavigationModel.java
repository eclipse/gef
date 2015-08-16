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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.models.ViewportModel.ViewportState;

public class NavigationModel {

	private Set<Graph> skipNextLayout = Collections.newSetFromMap(new IdentityHashMap<Graph, Boolean>());
	private Map<Graph, ViewportState> viewportStates = new HashMap<Graph, ViewportState>();

	public NavigationModel() {
	}

	// TODO: find a more robust way here -> layout should only occur, if size
	// of viewport/node changes
	public void addSkipNextLayout(Graph graph) {
		skipNextLayout.add(graph);
	}

	public ViewportState getViewportState(Graph graph) {
		return viewportStates.get(graph);
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

	public void setViewportState(Graph graph, ViewportState state) {
		viewportStates.put(graph, state);
	}
}
