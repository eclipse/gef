/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.parts.IResizableVisualPart;

import javafx.scene.Node;

/**
 * @author wienand
 *
 * @param <V>
 *            visual type
 */
// FIXME: no-implement
public interface IFXResizableVisualPart<V extends Node>
		extends IResizableVisualPart<Node, V> {

	@Override
	default Node getResizableVisual() {
		return getVisual();
	}

	@Override
	default Dimension getVisualSize() {
		return NodeUtils.getShapeBounds(getResizableVisual()).getSize();
	}

	@Override
	default void resizeVisual(Dimension size) {
		getResizableVisual().resize(size.width, size.height);
	}

}
