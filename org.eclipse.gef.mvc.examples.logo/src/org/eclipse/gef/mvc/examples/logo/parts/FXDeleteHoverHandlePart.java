/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.net.URL;

import org.eclipse.gef.fx.nodes.HoverOverlayImageView;

import javafx.scene.image.Image;

public class FXDeleteHoverHandlePart extends AbstractLogoHoverHandlePart<HoverOverlayImageView> {

	public static final String IMG_DELETE = "/delete_obj.gif";
	public static final String IMG_DELETE_DISABLED = "/delete_obj_disabled.gif";

	@Override
	protected HoverOverlayImageView createVisual() {
		URL overlayImageResource = FXDeleteHoverHandlePart.class.getResource(IMG_DELETE);
		if (overlayImageResource == null) {
			throw new IllegalStateException("Cannot find resource <" + IMG_DELETE + ">.");
		}
		Image overlayImage = new Image(overlayImageResource.toExternalForm());

		URL baseImageResource = FXDeleteHoverHandlePart.class.getResource(IMG_DELETE_DISABLED);
		if (baseImageResource == null) {
			throw new IllegalStateException("Cannot find resource <" + IMG_DELETE_DISABLED + ">.");
		}
		Image baseImage = new Image(baseImageResource.toExternalForm());

		HoverOverlayImageView blendImageView = new HoverOverlayImageView();
		blendImageView.baseImageProperty().set(baseImage);
		blendImageView.overlayImageProperty().set(overlayImage);
		return blendImageView;
	}
}
