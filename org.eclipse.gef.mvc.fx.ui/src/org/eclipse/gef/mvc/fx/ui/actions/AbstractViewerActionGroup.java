/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The {@link AbstractViewerActionGroup} is is a specialization of
 * {@link ActionGroup} that is bound to an {@link IViewer}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerActionGroup extends ActionGroup
		implements IAdaptable.Bound<IViewer> {

	private ReadOnlyObjectWrapper<IViewer> viewerProperty = new ReadOnlyObjectWrapper<>();

	private List<IAdaptable.Bound<IViewer>> contributions = null;

	@Override
	public ReadOnlyObjectProperty<IViewer> adaptableProperty() {
		return viewerProperty;
	}

	/**
	 * Returns a list containing all {@link AbstractViewerAction}s or
	 * {@link AbstractViewerContributionItem}s contributed by this
	 * {@link AbstractViewerActionGroup}.
	 *
	 * @return The {@link AbstractViewerAction}s and
	 *         {@link AbstractViewerContributionItem}s.
	 */
	public abstract List<IAdaptable.Bound<IViewer>> createContributions();

	@Override
	public void fillActionBars(IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		for (IAdaptable.Bound<IViewer> va : getContributions()) {
			if (va instanceof IAction) {
				tbm.add((IAction) va);
			} else if (va instanceof IContributionItem) {
				tbm.add((IContributionItem) va);
			}
		}
	}

	@Override
	public IViewer getAdaptable() {
		return viewerProperty.get();
	}

	/**
	 * Returns a list containing all {@link AbstractViewerAction}s contributed
	 * by this {@link AbstractViewerActionGroup}.
	 *
	 * @return The {@link AbstractViewerAction}s.
	 */
	public final List<IAdaptable.Bound<IViewer>> getContributions() {
		if (contributions == null) {
			contributions = createContributions();
		}
		return contributions;
	}

	@Override
	public void setAdaptable(IViewer adaptable) {
		this.viewerProperty.set(adaptable);
		for (IAdaptable.Bound<IViewer> va : getContributions()) {
			va.setAdaptable(adaptable);
		}
	}

}
