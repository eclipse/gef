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
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Provider;

public class FXSelectionFeedbackPart extends
		AbstractFXFeedbackPart<FXGeometryNode<IGeometry>> {

	private final PropertyChangeListener focusModelListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (FocusModel.VIEWER_FOCUS_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			} else if (FocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			}
		}
	};

	private final Provider<IGeometry> feedbackGeometryProvider;

	private static final Color FOCUS_COLOR = Color.rgb(125, 173, 217);

	public FXSelectionFeedbackPart(Provider<IGeometry> feedbackGeometryProvider) {
		this.feedbackGeometryProvider = feedbackGeometryProvider;
	}

	@Override
	protected FXGeometryNode<IGeometry> createVisual() {
		FXGeometryNode<IGeometry> feedbackVisual = new FXGeometryNode<IGeometry>();
		feedbackVisual.setFill(Color.TRANSPARENT);
		feedbackVisual.setMouseTransparent(true);
		feedbackVisual.setManaged(false);
		// feedbackVisual.setStrokeType(StrokeType.OUTSIDE);
		feedbackVisual.setStrokeWidth(1);
		return feedbackVisual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getRoot().getViewer().getAdapter(FocusModel.class)
				.addPropertyChangeListener(focusModelListener);
	}

	@Override
	protected void doDeactivate() {
		getRoot().getViewer().getAdapter(FocusModel.class)
				.removePropertyChangeListener(focusModelListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(FXGeometryNode<IGeometry> visual) {
		Set<IVisualPart<Node, ? extends Node>> anchorages = getAnchorages()
				.keySet();
		if (anchorages.isEmpty()) {
			return;
		}

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		visual.setGeometry(feedbackGeometry);

		IVisualPart<Node, ? extends Node> anchorage = anchorages.iterator()
				.next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();

		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
		}

		// update color according to focused and selected state
		boolean focused = viewer.getAdapter(FocusModel.class).isViewerFocused()
				&& viewer.getAdapter(FocusModel.class).getFocused() == anchorage;
		List<IContentPart<Node, ? extends Node>> selected = viewer
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
		boolean primary = selected.get(0) == anchorage;
		if (primary) {
			visual.setEffect(getPrimarySelectionFeedbackEffect(focused));
			visual.setStroke(Color.BLACK);
		} else {
			visual.setEffect(getSecondarySelectionFeedbackEffect(focused));
			visual.setStroke(Color.GREY);
		}
	}

	protected IGeometry getFeedbackGeometry() {
		return FXUtils
				.sceneToLocal(getVisual(), feedbackGeometryProvider.get());
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
