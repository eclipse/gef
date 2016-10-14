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

import java.util.List;

import org.eclipse.gef.mvc.parts.AbstractContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class CellContentPart<VR, V extends VR> extends AbstractContentPart<VR, V> {

	@Override
	protected void doAddChildVisual(IVisualPart<VR, ? extends VR> child, int index) {
	}

	@Override
	protected V doCreateVisual() {
		return null;
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
	protected void doRemoveChildVisual(IVisualPart<VR, ? extends VR> child, int index) {
	}
}