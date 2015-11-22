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

import org.eclipse.gef4.zest.fx.policies.ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy;

import javafx.scene.image.Image;

/**
 * The {@link ShowHiddenNeighborsHandlePart} is an {@link AbstractHidingHandlePart}
 * that displays an expansion image. By default, the
 * {@link ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy} is installed for
 * {@link ShowHiddenNeighborsHandlePart}, so that the corresponding
 * {@link NodeContentPart} can be expanded by a click on this part.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsHandlePart extends AbstractHidingHandlePart {

	/**
	 * The url to the image that is displayed when hovering this part.
	 */
	public static final String IMG_EXPAND = "/expandall.gif";

	/**
	 * The url to the image that is displayed when not hovering this part.
	 */
	public static final String IMG_EXPAND_DISABLED = "/expandall_disabled.gif";

	@Override
	protected Image getHoverImage() {
		return new Image(IMG_EXPAND);
	}

	@Override
	protected Image getImage() {
		return new Image(IMG_EXPAND_DISABLED);
	}

}
