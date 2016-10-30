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
package org.eclipse.gef.zest.fx.parts;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link ZestFxSelectionHandlePartFactory} is a specialization of the
 * {@link DefaultSelectionHandlePartFactory} that suppresses the generation of
 * multi selection handles.
 *
 * @author mwienand
 *
 */
public class ZestFxSelectionHandlePartFactory extends DefaultSelectionHandlePartFactory {

	@Override
	protected List<IHandlePart<? extends Node>> createMultiSelectionHandleParts(
			List<? extends IVisualPart<? extends Node>> targets, Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}

}
