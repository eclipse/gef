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
package org.eclipse.gef4.mvc.tests.stubs;

import java.util.List;

import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class CellContentPart extends AbstractContentPart<Object, Object> {
	@Override
	protected void addChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
	}

	@Override
	protected Object createVisual() {
		return new Object();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return ((Cell) getContent()).children;
	}

	@Override
	protected void doRefreshVisual(Object visual) {
	}

	@Override
	public boolean isFocusable() {
		return ((Cell) getContent()).name.startsWith("C");
	}

	@Override
	protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
	}
}