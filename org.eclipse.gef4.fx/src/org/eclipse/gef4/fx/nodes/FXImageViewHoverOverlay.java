/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - cleanup of API
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * A {@link Group} that combines two {@link ImageView}s, realizing an overlay
 * effect (by adjusting the respective opacities) upon mouse hover.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: extract magic numbers to properties
public class FXImageViewHoverOverlay extends Group {

	private SimpleObjectProperty<Image> baseImageProperty = new SimpleObjectProperty<Image>();
	private SimpleObjectProperty<Image> overlayImageProperty = new SimpleObjectProperty<Image>();
	private ImageView baseImageView;
	private ImageView overlayImageView;

	public FXImageViewHoverOverlay() {
		createImageViews();
		registerHoverEffect();
		registerPropertyListeners();
	}

	public SimpleObjectProperty<Image> baseImageProperty() {
		return baseImageProperty;
	}

	protected void createImageViews() {
		baseImageView = new ImageView();
		overlayImageView = new ImageView();
		getChildren().addAll(baseImageView, overlayImageView);
		setBlendMode(BlendMode.SRC_OVER);
		// hide hover image, and show normal image
		overlayImageView.setOpacity(0);
		baseImageView.setOpacity(0.8); // 20% transparent
	}

	public ImageView getBaseImageView() {
		return baseImageView;
	}

	public ImageView getOverlayImageView() {
		return overlayImageView;
	}

	public SimpleObjectProperty<Image> overlayImageProperty() {
		return overlayImageProperty;
	}

	protected void registerHoverEffect() {
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				new Timeline(new KeyFrame(Duration.millis(150),
						new KeyValue(baseImageView.opacityProperty(), 0),
						new KeyValue(overlayImageView.opacityProperty(), 1)))
								.play();
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				new Timeline(new KeyFrame(Duration.millis(150),
						new KeyValue(baseImageView.opacityProperty(), 0.8),
						new KeyValue(overlayImageView.opacityProperty(), 0)))
								.play();
			}
		});
	}

	protected void registerPropertyListeners() {
		baseImageProperty.addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable,
					Image oldImage, Image newImage) {
				setImage(baseImageView, newImage);
			}
		});
		overlayImageProperty.addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable,
					Image oldImage, Image newImage) {
				setImage(overlayImageView, newImage);
			}
		});
	}

	private void setImage(ImageView imageView, Image image) {
		imageView.setImage(image);
		// translate to center
		imageView.setTranslateX(-image.getWidth() / 2);
		imageView.setTranslateY(-image.getHeight() / 2);
	}

}
