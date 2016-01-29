/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;

import javafx.scene.Node;

/**
 * The {@link AbstractFXContentPart} is an {@link IContentPart} implementation
 * that binds the VR type parameter (visual root type) to {@link Node}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual {@link Node} used by this {@link AbstractFXContentPart}
 *            .
 */
public abstract class AbstractFXContentPart<V extends Node>
		extends AbstractContentPart<Node, V> {

}
