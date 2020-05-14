/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx.stubs;

import java.util.List;

import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class CellContentPart extends AbstractContentPart<Node> {

	@Override
	protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
	}

	@Override
	protected Node doCreateVisual() {
		return new Rectangle();
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
	protected void doRefreshVisual(Node visual) {
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
	}

	@Override
	public boolean isFocusable() {
		return ((Cell) getContent()).name.startsWith("C");
	}
}