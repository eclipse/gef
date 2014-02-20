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
package org.eclipse.gef4.mvc.tools;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public abstract class AbstractDragTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected IDragPolicy<V> getToolPolicy(IVisualPart<V> targetPart) {
		return targetPart.getBound(IDragPolicy.class);
	}

	protected void press(List<IVisualPart<V>> targetParts,
			Point mouseLocation) {
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.press(mouseLocation);
		}
	}

	protected void drag(List<IVisualPart<V>> targetParts,
			Point mouseLocation, Dimension delta) {
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.drag(mouseLocation, delta);
		}
	}

	protected void release(List<IVisualPart<V>> targetParts,
			Point mouseLocation, Dimension delta) {
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.release(mouseLocation,
						delta);
		}
	}
}
