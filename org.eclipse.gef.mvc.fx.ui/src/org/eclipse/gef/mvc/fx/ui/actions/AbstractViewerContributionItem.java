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

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.ContributionItem;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * The {@link AbstractViewerContributionItem} {@link ContributionItem} that is
 * bound to an {@link IViewer}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerContributionItem extends ContributionItem
		implements IAdaptable.Bound<IViewer> {

	private ReadOnlyObjectWrapper<IViewer> viewerProperty = new ReadOnlyObjectWrapper<>();

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

	@Override
	public ReadOnlyObjectProperty<IViewer> adaptableProperty() {
		return viewerProperty;
	}

	@Override
	public IViewer getAdaptable() {
		return viewerProperty.get();
	}

	/**
	 * Returns the {@link IViewer} to which this
	 * {@link AbstractViewerContributionItem} is bound.
	 *
	 * @return The {@link IViewer} to which this
	 *         {@link AbstractViewerContributionItem} is bound.
	 */
	protected IViewer getViewer() {
		return getAdaptable();
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

	@Override
	public void setAdaptable(IViewer viewer) {
		if (this.viewerProperty.get() == viewer) {
			// nothing changed
			return;
		}

		// unregister listeners and clean up for the old viewer
		if (this.viewerProperty.get() != null) {
			this.viewerProperty.get().activeProperty()
					.removeListener(activationListener);
			if (this.viewerProperty.get().isActive()) {
				unregister();
			}
		}

		// save new viewer
		this.viewerProperty.set(viewer);

		// register listeners and prepare for the new viewer
		if (this.viewerProperty.get() != null) {
			this.viewerProperty.get().activeProperty()
					.addListener(activationListener);
			if (this.viewerProperty.get().isActive()) {
				register();
			}
		}
	}

	/**
	 * This method is called when this item loses an {@link IViewer} which is
	 * {@link IViewer#activeProperty() active} or when a previously obtained
	 * viewer is deactivated.
	 */
	protected void unregister() {
	}
}
