/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import org.eclipse.gef.fx.nodes.HoverOverlayImageView;
import org.eclipse.gef.mvc.fx.parts.AbstractSegmentHandlePart;
import org.eclipse.gef.zest.fx.handlers.ShowHiddenNeighborsOfFirstAnchorageOnClickHandler;

import javafx.scene.image.Image;

/**
 * The {@link ShowHiddenNeighborsHoverHandlePart} is an
 * {@link AbstractSegmentHandlePart} that displays an expansion image. By
 * default, the {@link ShowHiddenNeighborsOfFirstAnchorageOnClickHandler} is
 * installed for {@link ShowHiddenNeighborsHoverHandlePart}, so that the
 * corresponding {@link NodePart} can be expanded by a click on this part.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsHoverHandlePart extends AbstractSegmentHandlePart<HoverOverlayImageView> {

	/**
	 * The url to the image that is displayed when hovering this part.
	 */
	private static final String IMG_SHOW_HIDDEN_NEIGHBORS = "/expandall.gif";

	/**
	 * The url to the image that is displayed when not hovering this part.
	 */
	private static final String IMG_SHOW_HIDDEN_NEIGHBORS_DISABLED = "/expandall_disabled.gif";

	@Override
	protected HoverOverlayImageView doCreateVisual() {
		// create blending image view for both
		HoverOverlayImageView blendImageView = new HoverOverlayImageView();
		blendImageView.baseImageProperty().set(new Image(IMG_SHOW_HIDDEN_NEIGHBORS_DISABLED));
		blendImageView.overlayImageProperty().set(new Image(IMG_SHOW_HIDDEN_NEIGHBORS));
		return blendImageView;
	}
}
