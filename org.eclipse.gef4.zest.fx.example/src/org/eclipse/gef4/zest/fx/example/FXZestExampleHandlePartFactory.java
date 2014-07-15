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
package org.eclipse.gef4.zest.fx.example;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class FXZestExampleHandlePartFactory extends FXDefaultHandlePartFactory {
	
	@Override
	public List<IHandlePart<Node>> createHandleParts(
			List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}
	
}
