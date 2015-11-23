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
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import org.eclipse.gef4.fx.nodes.HoverOverlayImageView;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * The {@link AbstractHidingHandlePart} is an
 * {@link AbstractFXSegmentHandlePart} that displays an
 * {@link HoverOverlayImageView} that uses the {@link #getImage() image} and
 * {@link #getHoverImage() hover image} that are provided by subclasses.
 * <p>
 * An {@link AbstractHidingHandlePart} can only be attached to
 * {@link NodeContentPart}s due a check within
 * {@link #attachToAnchorageVisual(IVisualPart, String)}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractHidingHandlePart extends AbstractFXSegmentHandlePart<HoverOverlayImageView> {

	private boolean isVisible = false;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (!(anchorage instanceof NodeContentPart)) {
			throw new IllegalArgumentException(
					"Anchorage not applicable <" + anchorage + ">. Can only attach to NodeContentPart.");
		}
		super.attachToAnchorageVisual(anchorage, role);
	}

	@Override
	protected HoverOverlayImageView createVisual() {
		// get image and hover image
		final Image hoverImage = getHoverImage();
		final Image image = getImage();
		// create blending image view for both
		HoverOverlayImageView blendImageView = new HoverOverlayImageView();
		blendImageView.baseImageProperty().set(image);
		blendImageView.overlayImageProperty().set(hoverImage);
		return blendImageView;
	}

	@Override
	public void doRefreshVisual(HoverOverlayImageView visual) {
		boolean wasVisible = isVisible;
		super.doRefreshVisual(visual);
		isVisible = getVisual().isVisible();
		DoubleProperty opacityProperty = getVisual().opacityProperty();
		// TODO: extract magic numbers to properties
		if (!wasVisible && isVisible) {
			opacityProperty.set(0);
			new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(opacityProperty, 1))).play();
		} else if (wasVisible && !isVisible) {
			opacityProperty.set(1);
			new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(opacityProperty, 0))).play();
		}
	}

	/**
	 * Returns the {@link Image} that is displayed when hovering this part.
	 *
	 * @return The {@link Image} that is displayed when hovering this part.
	 */
	protected abstract Image getHoverImage();

	/**
	 * Returns the {@link Image} that is displayed when this part is not
	 * hovered.
	 *
	 * @return The {@link Image} that is displayed when this part is not
	 *         hovered.
	 */
	protected abstract Image getImage();

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, HoverOverlayImageView visual) {
		super.registerAtVisualPartMap(viewer, visual);
		// put base ImageView and overlay ImageView into visual->part map
		viewer.getVisualPartMap().put(visual.getBaseImageView(), this);
		viewer.getVisualPartMap().put(visual.getOverlayImageView(), this);
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer, HoverOverlayImageView visual) {
		// remove base ImageView and overlay ImageView into visual->part map
		viewer.getVisualPartMap().remove(visual.getBaseImageView());
		viewer.getVisualPartMap().remove(visual.getOverlayImageView());
		super.unregisterFromVisualPartMap(viewer, visual);
	}

}