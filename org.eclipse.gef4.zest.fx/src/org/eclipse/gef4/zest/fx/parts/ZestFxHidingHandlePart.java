/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.zest.fx.policies.HideFirstAnchorageOnClickPolicy;

import javafx.scene.image.Image;

/**
 * The {@link ZestFxHidingHandlePart} is an {@link AbstractHidingHandlePart}
 * that displays a "collapse" image. By default, the
 * {@link HideFirstAnchorageOnClickPolicy} is installed for
 * {@link ZestFxHidingHandlePart}, so that the corresponding
 * {@link NodeContentPart} can be hidden by a click on this part.
 *
 * @author mwienand
 *
 */
public class ZestFxHidingHandlePart extends AbstractHidingHandlePart {

	/**
	 * The url to the image that is displayed when hovered this part.
	 */
	public static final String IMG_PRUNE = "/collapseall.png";

	/**
	 * The url to the image that is displayed when not hovering this part.
	 */
	public static final String IMG_PRUNE_DISABLED = "/collapseall_disabled.png";

	@Override
	protected Image getHoverImage() {
		return new Image(IMG_PRUNE);
	}

	@Override
	protected Image getImage() {
		return new Image(IMG_PRUNE_DISABLED);
	}

}