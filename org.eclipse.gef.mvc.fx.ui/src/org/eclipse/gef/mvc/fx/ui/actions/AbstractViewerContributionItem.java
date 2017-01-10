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

import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.ContributionItem;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * The {@link AbstractViewerContributionItem} is a specialization of
 * {@link ContributionItem}
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerContributionItem extends ContributionItem
		implements IViewerDependent {

	private IViewer viewer;
	private ChangeListener<Boolean> activationListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (newValue.booleanValue()) {
				register();
			} else {
				unregister();
			}
		}
	};

	/**
	 * Constructs a new {@link AbstractViewerContributionItem}.
	 */
	protected AbstractViewerContributionItem() {
	}

	/**
	 * Returns the {@link IViewer} for which this {@link IViewerAction} was
	 * {@link #init(IViewer) initialized}.
	 *
	 * @return The {@link IViewer} for which this {@link IViewerAction} was
	 *         {@link #init(IViewer) initialized}.
	 */
	protected IViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewer viewer) {
		if (this.viewer == viewer) {
			// nothing changed
			return;
		}

		// unregister listeners and clean up for the old viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().removeListener(activationListener);
			if (this.viewer.isActive()) {
				unregister();
			}
		}

		// save new viewer
		this.viewer = viewer;

		// register listeners and prepare for the new viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().addListener(activationListener);
			if (this.viewer.isActive()) {
				register();
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return getViewer() != null && getViewer().isActive();
	}

	/**
	 * This method is called when this item obtains an {@link IViewer} which is
	 * {@link IViewer#activeProperty() active} or when a previously obtained
	 * viewer is activated.
	 */
	protected void register() {
	}

	/**
	 * This method is called when this item loses an {@link IViewer} which is
	 * {@link IViewer#activeProperty() active} or when a previously obtained
	 * viewer is deactivated.
	 */
	protected void unregister() {
	}
}
