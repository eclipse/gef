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
package org.eclipse.gef.mvc.tests.stubs.cell;

import java.util.Map;

import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class CellContentPartFactory<VR> implements IContentPartFactory<VR> {

	@Inject
	private Injector injector;

	@SuppressWarnings("unchecked")
	@Override
	public IContentPart<VR, ? extends VR> createContentPart(Object content, IBehavior<VR> contextBehavior,
			Map<Object, Object> contextMap) {
		if (content instanceof Cell) {
			return injector.getInstance(CellContentPart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	}
}