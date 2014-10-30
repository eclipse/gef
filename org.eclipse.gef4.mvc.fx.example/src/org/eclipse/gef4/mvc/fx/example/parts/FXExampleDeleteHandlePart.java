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
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXExampleDeleteHandlePart extends AbstractFXHandlePart {

	public static final String IMG_DELETE = "/res/delete_obj.gif";
	public static final String IMG_DELETE_DISABLED = "/res/delete_obj_disabled.gif";

	private Group blendGroup;

	public FXExampleDeleteHandlePart() {
		// TODO: Make it easy to overwrite bindings for more specific parts
		// setAdapter(AdapterKey.get(HoverPolicy.class),
		// new HoverFirstAnchoragePolicy());
	}

	@Override
	protected void doRefreshVisual() {
		// check if we have a host
		SetMultimap<IVisualPart<Node>, String> anchorages = getAnchorages();
		if (anchorages.isEmpty()) {
			return;
		}

		// determine center location of host visual
		IVisualPart<Node> host = anchorages.keys().iterator().next();
		Node hostVisual = host.getVisual();
		Bounds hostBounds = hostVisual.getLayoutBounds();
		double cx = hostVisual.getLayoutX() + hostBounds.getWidth() / 2;
		double cy = hostVisual.getLayoutY() + hostBounds.getHeight() / 2;

		// position handle at center of host
		blendGroup.setLayoutX(cx);
		blendGroup.setLayoutY(cy);

	}

	protected Image getHoverImage() {
		return new Image(IMG_DELETE);
	}

	protected Image getImage() {
		return new Image(IMG_DELETE_DISABLED);
	}

	@Override
	public Node getVisual() {
		if (blendGroup == null) {
			// get image and hover image
			final Image hoverImage = getHoverImage();
			final Image image = getImage();

			// create ImageView for both
			final ImageView imageView = new ImageView(image);
			final ImageView hoverImageView = new ImageView(hoverImage);

			// set translation to center
			imageView.setTranslateX(-image.getWidth() / 2);
			imageView.setTranslateY(-image.getHeight() / 2);
			hoverImageView.setTranslateX(-hoverImage.getWidth() / 2);
			hoverImageView.setTranslateY(-hoverImage.getHeight() / 2);

			// create group to blend images
			blendGroup = new Group(imageView, hoverImageView);
			blendGroup.setBlendMode(BlendMode.SRC_OVER);

			// TODO: extract magic numbers to properties

			// initially hide hover image
			hoverImageView.setOpacity(0);

			// initially show disabled image 20% transparent
			imageView.setOpacity(0.8);

			// register hover effect
			blendGroup.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					new Timeline(new KeyFrame(Duration.millis(150),
							new KeyValue(imageView.opacityProperty(), 0),
							new KeyValue(hoverImageView.opacityProperty(), 1)))
							.play();
				}
			});
			blendGroup.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					new Timeline(new KeyFrame(Duration.millis(150),
							new KeyValue(imageView.opacityProperty(), 0.8),
							new KeyValue(hoverImageView.opacityProperty(), 0)))
							.play();
				}
			});
		}
		return blendGroup;
	}

}
