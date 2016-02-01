/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - contribution for Bugzilla #450231
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link FXFocusBehavior} can be registered on an {@link IVisualPart} to
 * transfer the focus information from the {@link FocusModel} to the part's
 * visualization.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXFocusBehavior extends AbstractBehavior<Node>
		implements PropertyChangeListener {

	private ChangeListener<IContentPart<Node, ? extends Node>> focusObserver = new ChangeListener<IContentPart<Node, ? extends Node>>() {

		@Override
		public void changed(
				ObservableValue<? extends IContentPart<Node, ? extends Node>> observable,
				IContentPart<Node, ? extends Node> oldValue,
				IContentPart<Node, ? extends Node> newValue) {
			if (newValue == getHost()) {
				applyFocus();
			}
		}
	};

	/**
	 * Assigns keyboard focus to the visualization of the host.
	 */
	protected void applyFocus() {
		getHost().getVisual().requestFocus();
	}

	@SuppressWarnings("serial")
	@Override
	protected void doActivate() {
		getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<FocusModel<Node>>() {
				}).focusProperty().addListener(focusObserver);
	}

	@SuppressWarnings("serial")
	@Override
	protected void doDeactivate() {
		getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<FocusModel<Node>>() {
				}).focusProperty().removeListener(focusObserver);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (FocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
			if (evt.getNewValue() == getHost()) {
				applyFocus();
			}
		}
	}
}
