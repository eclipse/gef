/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.parts.IContentPart;

public class ContentPartPool<VR> {

	private Map<Object, IContentPart<VR, ? extends VR>> pool = new HashMap<Object, IContentPart<VR, ? extends VR>>();

	public void add(IContentPart<VR, ? extends VR> part) {
		pool.put(part.getContent(), part);
	}

	public void clear() {
		pool.clear();
	}

	public IContentPart<VR, ? extends VR> remove(Object content) {
		return pool.remove(content);
	}
}