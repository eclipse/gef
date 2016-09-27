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

import org.eclipse.gef.mvc.parts.IResizableContentPart;

import javafx.scene.Node;

/**
 *
 * @author wienand
 *
 * @param <V>
 *            visual type
 */
public interface IFXResizableContentPart<V extends Node>
		extends IFXResizableVisualPart<V>, IResizableContentPart<Node, V> {

}
