/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link ZestFxSelectionHandlePartFactory} is a specialization of the
 * {@link FXDefaultSelectionHandlePartFactory} that suppresses the generation of
 * multi selection handles.
 *
 * @author mwienand
 *
 */
public class ZestFxSelectionHandlePartFactory extends FXDefaultSelectionHandlePartFactory {

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createMultiSelectionHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}

}
