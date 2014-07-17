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

import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractRootPart<VR> extends AbstractVisualPart<VR>
		implements IRootPart<VR> {

	private IViewer<VR> viewer;

	public IRootPart<VR> getRoot() {
		return this;
	}

	public IViewer<VR> getViewer() {
		return viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<VR>> getContentPartChildren() {
		return PartUtils.filterParts(getChildren(), IContentPart.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IHandlePart<VR>> getHandlePartChildren() {
		return PartUtils.filterParts(getChildren(), IHandlePart.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IFeedbackPart<VR>> getFeedbackPartChildren() {
		return PartUtils.filterParts(getChildren(), IFeedbackPart.class);
	}

	/**
	 * @see IRootPart#setViewer(IViewer)
	 */
	public void setViewer(IViewer<VR> newViewer) {
		if (viewer == newViewer)
			return;
		viewer = newViewer;
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<VR> anchorage, int index) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<VR> anchorage) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

}