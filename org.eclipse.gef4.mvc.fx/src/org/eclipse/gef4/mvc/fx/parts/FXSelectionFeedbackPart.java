/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Provider;

public class FXSelectionFeedbackPart extends FXGeometricFeedbackPart {

	private final PropertyChangeListener focusModelListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IFocusModel.VIEWER_FOCUS_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			} else if (IFocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			}
		}
	};
	private static final Color FOCUS_COLOR = Color.rgb(125, 173, 217);

	public FXSelectionFeedbackPart(Provider<IGeometry> feedbackGeometryProvider) {
		super(feedbackGeometryProvider);
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getRoot().getViewer().getFocusModel()
				.addPropertyChangeListener(focusModelListener);
	}

	@Override
	protected void doDeactivate() {
		getRoot().getViewer().getFocusModel()
				.removePropertyChangeListener(focusModelListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual() {
		Set<IVisualPart<Node>> anchorages = getAnchorages().keySet();
		if (anchorages.isEmpty()) {
			return;
		}
		super.doRefreshVisual();

		IVisualPart<Node> anchorage = anchorages.iterator().next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();

		boolean focused = viewer.getFocusModel().isViewerFocused()
				&& viewer.getFocusModel().getFocused() == anchorage;
		boolean primary = viewer.getSelectionModel().getSelected().get(0) == anchorage;
		if (primary) {
			getVisual().setEffect(getPrimarySelectionFeedbackEffect(focused));
			getVisual().setStroke(Color.BLACK);
		} else {
			getVisual().setEffect(getSecondarySelectionFeedbackEffect(focused));
			getVisual().setStroke(Color.GREY);
		}
	}

	protected Effect getPrimarySelectionFeedbackEffect(boolean focused) {
		DropShadow effect = new DropShadow();
		effect.setColor(focused ? FOCUS_COLOR : Color.GREY);
		effect.setRadius(5);
		effect.setSpread(0.6);
		return effect;
	}

	protected Effect getSecondarySelectionFeedbackEffect(boolean focused) {
		DropShadow effect = new DropShadow();
		effect.setColor(focused ? FOCUS_COLOR : Color.GREY);
		effect.setRadius(5);
		effect.setSpread(0.6);
		return effect;
	}

}
