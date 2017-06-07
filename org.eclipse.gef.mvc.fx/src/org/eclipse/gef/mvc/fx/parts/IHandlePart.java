/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import javafx.scene.Node;

/**
 * An {@link IHandlePart} is a controller that controls a visual, which is used
 * simply for tool interaction and does not correspond to anything in the
 * visualized model.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractHandlePart} should be subclassed.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link IHandlePart}.
 */
public interface IHandlePart<V extends Node> extends IVisualPart<V> {

}
