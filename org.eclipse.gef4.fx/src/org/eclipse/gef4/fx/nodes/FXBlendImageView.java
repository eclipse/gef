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

// TODO: extract magic numbers to properties
public class FXBlendImageView extends Group {

	private SimpleObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	private SimpleObjectProperty<Image> hoverImageProperty = new SimpleObjectProperty<Image>();
	private ImageView imageView;
	private ImageView hoverImageView;

	public FXBlendImageView() {
		createImageViews();
		registerHoverEffect();
		registerPropertyListeners();
	}

	protected void createImageViews() {
		imageView = new ImageView();
		hoverImageView = new ImageView();
		getChildren().addAll(imageView, hoverImageView);
		setBlendMode(BlendMode.SRC_OVER);
		// hide hover image, and show normal image
		hoverImageView.setOpacity(0);
		imageView.setOpacity(0.8); // 20% transparent
	}

	public ImageView getHoverImageView() {
		return hoverImageView;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public SimpleObjectProperty<Image> hoverImageProperty() {
		return hoverImageProperty;
	}

	public SimpleObjectProperty<Image> imageProperty() {
		return imageProperty;
	}

	protected void registerHoverEffect() {
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(
						imageView.opacityProperty(), 0), new KeyValue(
						hoverImageView.opacityProperty(), 1))).play();
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(
						imageView.opacityProperty(), 0.8), new KeyValue(
						hoverImageView.opacityProperty(), 0))).play();
			}
		});
	}

	protected void registerPropertyListeners() {
		imageProperty.addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable,
					Image oldImage, Image newImage) {
				setImage(imageView, newImage);
			}
		});
		hoverImageProperty.addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable,
					Image oldImage, Image newImage) {
				setImage(hoverImageView, newImage);
			}
		});
	}

	protected void setImage(ImageView imageView, Image image) {
		imageView.setImage(image);
		// translate to center
		imageView.setTranslateX(-image.getWidth() / 2);
		imageView.setTranslateY(-image.getHeight() / 2);
	}

}
