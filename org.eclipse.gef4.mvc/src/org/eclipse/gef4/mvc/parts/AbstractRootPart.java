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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.editparts.SimpleRootEditPart.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractRootPart<V> extends AbstractVisualPart<V>
		implements IRootPart<V> {

	private IVisualViewer<V> viewer;

	public AbstractRootPart() {
		installBound(new ContentBehavior<V>());
	}

	public IRootPart<V> getRoot() {
		return this;
	}

	public IVisualViewer<V> getViewer() {
		return viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<V>> getContentPartChildren() {
		return PartUtils.filterParts(getChildren(), IContentPart.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IHandlePart<V>> getHandlePartChildren() {
		return PartUtils.filterParts(getChildren(), IHandlePart.class);
	}

	/**
	 * @see IRootPart#setViewer(EditPartViewer)
	 */
	public void setViewer(IVisualViewer<V> newViewer) {
		if (viewer == newViewer)
			return;
		viewer = newViewer;
	}

	@Override
	public void attachVisualToAnchorageVisual(IVisualPart<V> anchorage,
			V anchorageVisual) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<V> anchorage,
			V anchorageVisual) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

}