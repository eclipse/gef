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
 * @author mwienand
 *
 */
public abstract class AbstractViewerContributionItem extends ContributionItem {

	private IViewer viewer;
	private boolean isActive = false;
	private ChangeListener<Boolean> activationListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (newValue.booleanValue()) {
				setActive(true);
			} else {
				setActive(false);
			}
		}
	};

	/**
	 * Creates a new {@link AbstractViewerContributionItem}.
	 */
	protected AbstractViewerContributionItem() {
	}

	/**
	 */
	protected void activate() {
	}

	/**
	 */
	protected void deactivate() {
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

	/**
	 * Change {@link IViewer}.
	 *
	 * @param viewer
	 *            a
	 */
	public void init(IViewer viewer) {
		if (this.viewer == viewer) {
			// nothing changed
			return;
		}

		// unregister listeners and clean up for the old viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().removeListener(activationListener);
			setActive(false);
		}

		// save new viewer
		this.viewer = viewer;

		// register listeners and prepare for the new viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().addListener(activationListener);
			setActive(this.viewer.isActive());
		}
	}

	/**
	 *
	 * @return a
	 */
	protected boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            a
	 */
	protected void setActive(boolean isActive) {
		if (this.isActive == isActive) {
			// nothing changed
			return;
		}
		if (isActive) {
			activate();
		} else {
			deactivate();
		}
		this.isActive = isActive;
	}
}
