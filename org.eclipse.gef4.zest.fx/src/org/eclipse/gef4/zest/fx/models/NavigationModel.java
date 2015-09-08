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
 *     Alexander Nyßen (itemis AG) - refactorings
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

/**
 * The {@link NavigationModel} manages a {@link Set} of {@link Graph}s for which
 * the next layout pass should be skipped (due to transformation or navigation
 * changes). Moreover, it manages a {@link Map} saving a {@link ViewportState}
 * per {@link Graph}, so that the scroll position and zoom factor can be
 * restored when navigating nested graphs.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class NavigationModel {

	private Set<Graph> skipNextLayout = Collections.newSetFromMap(new IdentityHashMap<Graph, Boolean>());
	private Map<Graph, ViewportState> viewportStates = new HashMap<Graph, ViewportState>();

	/**
	 * Default constructor.
	 */
	public NavigationModel() {
	}

	/**
	 * Adds the given {@link Graph} to the {@link Set} of {@link Graph}s for
	 * which the next layout pass is skipped.
	 *
	 * @param graph
	 *            The {@link Graph} that is added to the {@link Set} of
	 *            {@link Graph}s for which the next layout pass is skipped.
	 */
	// TODO: find a more robust way here -> layout should only occur, if size
	// of viewport/node changes
	public void addSkipNextLayout(Graph graph) {
		skipNextLayout.add(graph);
	}

	/**
	 * Retrieves the {@link ViewportState} that is currently saved for the given
	 * {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the saved {@link ViewportState} is
	 *            returned.
	 * @return The {@link ViewportState} that was saved for the given
	 *         {@link Graph}.
	 */
	public ViewportState getViewportState(Graph graph) {
		return viewportStates.get(graph);
	}

	/**
	 * Removes the the given {@link Graph} from the {@link Set} of {@link Graph}
	 * s for which the next layout pass is skipped.
	 *
	 * @param graph
	 *            The {@link Graph} that is removed from the {@link Set} of
	 *            {@link Graph}s for which the next layout pass is skipped.
	 * @return <code>true</code> if the the given {@link Graph} was contained in
	 *         the {@link Set}, otherwise <code>false</code>.
	 */
	public boolean removeSkipNextLayout(Graph graph) {
		return skipNextLayout.remove(graph);
	}

	/**
	 * Saves the given {@link ViewportState} for the given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} for which to save a {@link ViewportState}.
	 * @param state
	 *            The {@link ViewportState} that is saved for the given
	 *            {@link Graph}.
	 */
	public void setViewportState(Graph graph, ViewportState state) {
		viewportStates.put(graph, state);
	}

}
