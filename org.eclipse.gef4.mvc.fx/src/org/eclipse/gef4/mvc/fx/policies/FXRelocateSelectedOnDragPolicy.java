/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public class FXRelocateSelectedOnDragPolicy extends AbstractPolicy<Node> implements
		IDragPolicy<Node> {

	private Point initialMouseLocation = null;

	@SuppressWarnings("unchecked")
	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node> part) {
		return part.getBound(FXResizeRelocatePolicy.class);
	}

	public List<IContentPart<Node>> getTargetParts() {
		return getHost().getRoot().getViewer().getSelectionModel()
				.getSelected();
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void press(Point mouseLocation) {
		initialMouseLocation = mouseLocation.getCopy();
		for (IContentPart<Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				policy.initResizeRelocate();
			}
		}
	}

	@Override
	public void drag(Point mouseLocation, Dimension delta) {
		Point offset = mouseLocation.getTranslated(initialMouseLocation
				.getNegated());
		for (IContentPart<Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				policy.performResizeRelocate(offset.x, offset.y, 0, 0);
			}
		}
	}

	@Override
	public void release(Point mouseLocation, Dimension delta) {
		Point offset = mouseLocation.getTranslated(initialMouseLocation
				.getNegated());
		for (IContentPart<Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				policy.commitResizeRelocate(offset.x, offset.y, 0, 0);
			}
		}
		initialMouseLocation = null;
	}

}
