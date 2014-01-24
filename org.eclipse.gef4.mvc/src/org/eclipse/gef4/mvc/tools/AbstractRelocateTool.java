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

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractRelocateTool<V> extends AbstractTool<V> {

	private Point initialMouseLocation = null;

	@SuppressWarnings("unchecked")
	protected AbstractResizeRelocatePolicy<V> getResizeRelocatePolicy(
			IContentPart<V> editPart) {
		return editPart.getEditPolicy(AbstractResizeRelocatePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getDomain().getViewer().getSelectionModel().getSelected();
	}

	public void initRelocate(Point mouseLocation) {
		initialMouseLocation = mouseLocation.getCopy();
		for (IContentPart<V> targetPart : getTargetParts()) {
			AbstractResizeRelocatePolicy<V> resizeRelocatePolicy = getResizeRelocatePolicy(targetPart);
			if (resizeRelocatePolicy != null)
				resizeRelocatePolicy.initResizeRelocate();
		}
	}

	public void performRelocate(Point mouseLocation) {
		Point delta = mouseLocation.getTranslated(initialMouseLocation
				.getNegated());
		for (IContentPart<V> targetPart : getTargetParts()) {
			AbstractResizeRelocatePolicy<V> resizeRelocatePolicy = getResizeRelocatePolicy(targetPart);
			if (resizeRelocatePolicy != null)
				resizeRelocatePolicy.performResizeRelocate(delta.x, delta.y, 0,
						0);
		}
	}

	public void commitRelocate(Point mouseLocation) {
		Point delta = mouseLocation.getTranslated(initialMouseLocation
				.getNegated());
		for (IContentPart<V> targetPart : getTargetParts()) {
			AbstractResizeRelocatePolicy<V> resizeRelocatePolicy = getResizeRelocatePolicy(targetPart);
			if (resizeRelocatePolicy != null)
				resizeRelocatePolicy.commitResizeRelocate(delta.x, delta.y, 0,
						0);
		}
		initialMouseLocation = null;
	}

}
