/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.parts;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXBlendImageView;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.policies.HoverFirstAnchorageOnHoverPolicy;
import org.eclipse.gef4.zest.fx.policies.PruneNodePolicy;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

public class ZestFxPruningHandlePart extends FXSegmentHandlePart {

	public static final String IMG_PRUNE = "/collapseall.png";
	public static final String IMG_PRUNE_DISABLED = "/collapseall_disabled.png";

	private boolean isVisible = false;

	public ZestFxPruningHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
		// FIXME: hover hierarchy
		setAdapter(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY),
				new HoverFirstAnchorageOnHoverPolicy());
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		if (!(anchorage instanceof NodeContentPart)) {
			throw new IllegalArgumentException("Anchorage not applicable <"
					+ anchorage + ">. Can only attach to NodeContentPart.");
		}
		super.attachToAnchorageVisual(anchorage, role);
	}

	@Override
	protected Node createVisual() {
		// get image and hover image
		final Image hoverImage = getHoverImage();
		final Image image = getImage();
		// create blending image view for both
		FXBlendImageView blendImageView = new FXBlendImageView();
		blendImageView.imageProperty().set(image);
		blendImageView.hoverImageProperty().set(hoverImage);
		// register click action
		blendImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onClicked(event);
			}
		});
		return blendImageView;
	}

	@Override
	public void doRefreshVisual() {
		boolean wasVisible = isVisible;
		super.doRefreshVisual();
		isVisible = getVisual().isVisible();
		DoubleProperty opacityProperty = getVisual().opacityProperty();
		// TODO: extract magic numbers to properties
		if (!wasVisible && isVisible) {
			opacityProperty.set(0);
			new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(
					opacityProperty, 1))).play();
		} else if (wasVisible && !isVisible) {
			opacityProperty.set(1);
			new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(
					opacityProperty, 0))).play();
		}
	}

	protected Image getHoverImage() {
		return new Image(IMG_PRUNE);
	}

	protected Image getImage() {
		return new Image(IMG_PRUNE_DISABLED);
	}

	protected void onClicked(MouseEvent event) {
		SetMultimap<IVisualPart<Node>, String> anchorages = getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node> anchorage = anchorages.keySet().iterator().next();
		PruneNodePolicy pruneNodePolicy = anchorage
				.getAdapter(PruneNodePolicy.class);
		pruneNodePolicy.prune();
	}

}