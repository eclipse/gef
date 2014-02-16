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
package org.eclipse.gef4.mvc.policies;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;

public abstract class AbstractRelocateSelectedOnDragPolicy<V> extends
		AbstractPolicy<V> implements IDragPolicy<V> {

	public AbstractRelocateSelectedOnDragPolicy() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected IResizeRelocatePolicy<V> getResizeRelocatePolicy(IContentPart<V> part) {
		return part.getBound(IResizeRelocatePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getHost().getRoot().getViewer().getSelectionModel()
				.getSelected();
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

}