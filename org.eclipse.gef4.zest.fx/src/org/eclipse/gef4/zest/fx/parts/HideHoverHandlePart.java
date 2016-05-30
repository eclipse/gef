/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import org.eclipse.gef4.zest.fx.policies.HideFirstAnchorageOnClickPolicy;

import javafx.scene.image.Image;

/**
 * The {@link HideHoverHandlePart} is an {@link AbstractFXSegmentHandlePart}
 * that displays a "collapse" image. By default, the
 * {@link HideFirstAnchorageOnClickPolicy} is installed for
 * {@link HideHoverHandlePart}, so that the corresponding
 * {@link NodePart} can be hidden by a click on this part.
 *
 * @author mwienand
 *
 */
public class HideHoverHandlePart extends AbstractFXSegmentHandlePart<HoverOverlayImageView> {

	/**
	 * The url to the image that is displayed when hovered this part.
	 */
	private static final String IMG_HIDE = "/collapseall.png";

	/**
	 * The url to the image that is displayed when not hovering this part.
	 */
	private static final String IMG_HIDE_DISABLED = "/collapseall_disabled.png";

	@Override
	protected HoverOverlayImageView createVisual() {
		// create blending image view for both
		HoverOverlayImageView blendImageView = new HoverOverlayImageView();
		blendImageView.baseImageProperty().set(new Image(IMG_HIDE_DISABLED));
		blendImageView.overlayImageProperty().set(new Image(IMG_HIDE));
		return blendImageView;
	}

}