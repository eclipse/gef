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
package org.eclipse.gef4.mvc.tests.stubs.cell;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class HandlePartFactory<VR> implements IHandlePartFactory<VR> {
	@Override
	public List<IHandlePart<VR, ? extends VR>> createHandleParts(List<? extends IVisualPart<VR, ? extends VR>> targets,
			IBehavior<VR> contextBehavior, Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}
}