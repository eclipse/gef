/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.List;

import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

/**
 * The {@link AbstractViewerActionGroup} is an {@link ActionGroup} that knows
 * which {@link IViewerAction viewer-actions} it contributes (see
 * {@link #createViewerDependents()}). Additionally, it can be initialized with an
 * {@link IViewer}, and will subsequently {@link IViewerAction#init(IViewer)
 * initialize} the contained {@link IViewerAction viewer-actions}. Also, it will
 * {@link IViewerAction#dispose() dispose} its {@link IViewerAction}s when
 * {@link #dispose() disposed}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerActionGroup extends ActionGroup {

	private List<IViewerDependent> viewerDependents = null;

	/**
	 * Returns a list containing all {@link IViewerAction}s contributed by this
	 * {@link AbstractViewerActionGroup}.
	 *
	 * @return The {@link IViewerAction}s.
	 */
	public abstract List<IViewerDependent> createViewerDependents();

	@Override
	public void dispose() {
		for (IViewerDependent va : getViewerDependents()) {
			va.dispose();
		}
		super.dispose();
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		for (IViewerDependent va : getViewerDependents()) {
			if (va instanceof IAction) {
				tbm.add((IAction) va);
			} else if (va instanceof IContributionItem) {
				tbm.add((IContributionItem) va);
			}
		}
	}

	/**
	 * Returns a list containing all {@link IViewerAction}s contributed by this
	 * {@link AbstractViewerActionGroup}.
	 *
	 * @return The {@link IViewerAction}s.
	 */
	public final List<IViewerDependent> getViewerDependents() {
		if (viewerDependents == null) {
			viewerDependents = createViewerDependents();
		}
		return viewerDependents;
	}

	/**
	 * Initializes this {@link AbstractViewerActionGroup} with the given
	 * {@link IViewer}. Passes on the given {@link IViewer} to the contained
	 * {@link #getViewerDependents()}, i.e. calls
	 * {@link IViewerAction#init(IViewer)}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for this
	 *            {@link AbstractViewerActionGroup}, or <code>null</code>.
	 */
	public void init(IViewer viewer) {
		for (IViewerDependent va : getViewerDependents()) {
			va.init(viewer);
		}
	}
}
