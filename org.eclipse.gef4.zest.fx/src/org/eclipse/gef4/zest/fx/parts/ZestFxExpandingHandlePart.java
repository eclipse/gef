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

import javafx.scene.image.Image;

public class ZestFxExpandingHandlePart extends AbstractHidingHandlePart {

	public static final String IMG_EXPAND = "/expandall.gif";
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
