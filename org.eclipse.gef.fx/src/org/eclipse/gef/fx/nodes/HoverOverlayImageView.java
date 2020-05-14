/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.nodes;

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
 * @author anyssen
 * @author mwienand
 *
 */
// TODO: extract magic numbers to properties
public class HoverOverlayImageView extends Group {

	private SimpleObjectProperty<Image> baseImageProperty = new SimpleObjectProperty<>();
	private SimpleObjectProperty<Image> overlayImageProperty = new SimpleObjectProperty<>();
	private ImageView baseImageView;
	private ImageView overlayImageView;

	/**
	 * Constructs a new {@link HoverOverlayImageView}.
	 */
	public HoverOverlayImageView() {
		createImageViews();
		registerHoverEffect();
		registerPropertyListeners();
	}

	/**
	 * Returns the {@link SimpleObjectProperty} which stores the base
	 * {@link Image} of this {@link HoverOverlayImageView}.
	 *
	 * @return The {@link SimpleObjectProperty} which stores the base
	 *         {@link Image} of this {@link HoverOverlayImageView}.
	 */
	public SimpleObjectProperty<Image> baseImageProperty() {
		return baseImageProperty;
	}

	/**
	 * Creates the {@link ImageView}s for the base and overlay image. Sets the
	 * opacity of the overlay {@link ImageView} to <code>0%</code> and the
	 * opacity of the base {@link ImageView} to <code>80%</code>.
	 */
	protected void createImageViews() {
		baseImageView = new ImageView();
		overlayImageView = new ImageView();
		getChildren().addAll(baseImageView, overlayImageView);
		setBlendMode(BlendMode.SRC_OVER);
		// hide hover image, and show normal image
		overlayImageView.setOpacity(0);
		baseImageView.setOpacity(0.8); // 20% transparent
	}

	/**
	 * Returns the {@link ImageView} which displays the base {@link Image}.
	 *
	 * @return The {@link ImageView} which displays the base {@link Image}.
	 */
	public ImageView getBaseImageView() {
		return baseImageView;
	}

	/**
	 * Returns the {@link ImageView} which displays the overlay {@link Image}.
	 *
	 * @return The {@link ImageView} which displays the overlay {@link Image}.
	 */
	public ImageView getOverlayImageView() {
		return overlayImageView;
	}

	/**
	 * Returns the {@link SimpleObjectProperty} which stores the overlay
	 * {@link Image} of this {@link HoverOverlayImageView}.
	 *
	 * @return The {@link SimpleObjectProperty} which stores the overlay
	 *         {@link Image} of this {@link HoverOverlayImageView}.
	 */
	public SimpleObjectProperty<Image> overlayImageProperty() {
		return overlayImageProperty;
	}

	/**
	 * Registers event listeners realizing the overlay effect on mouse hover.
	 */
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

	/**
	 * Registers property listeners for updating the {@link ImageView} s.
	 */
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
