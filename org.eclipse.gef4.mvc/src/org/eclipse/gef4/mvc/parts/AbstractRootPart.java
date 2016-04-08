/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The abstract base implementation of {@link IRootPart}, intended to be
 * sub-classed by clients to create their own custom {@link IRootPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link AbstractRootPart} is used in, e.g. javafx.scene.Node in
 *            case of JavaFX.
 * @param <V>
 *            The visual node used by this {@link AbstractRootPart}.
 */
public abstract class AbstractRootPart<VR, V extends VR>
		extends AbstractVisualPart<VR, V> implements IRootPart<VR, V> {

	private ReadOnlyObjectWrapper<IViewer<VR>> viewerProperty = new ReadOnlyObjectWrapper<>();

	@Override
	protected void activateChildren() {
		// activate content part children first (which might lead to the
		// creation of feedback and handle part children)
		for (IContentPart<VR, ? extends VR> child : getContentPartChildren()) {
			child.activate();
		}
		// activate remaining children
		for (IVisualPart<VR, ? extends VR> child : getChildrenUnmodifiable()) {
			if (!(child instanceof IContentPart)) {
				child.activate();
			}
		}
	}

	@Override
	public ReadOnlyObjectProperty<IViewer<VR>> adaptableProperty() {
		return viewerProperty.getReadOnlyProperty();
	}

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	protected void deactivateChildren() {
		// deactivate content part children first (which might lead to the
		// removal of feedback and handle part children)
		for (IContentPart<VR, ? extends VR> child : getContentPartChildren()) {
			child.deactivate();
		}
		// deactivate remaining children
		for (IVisualPart<VR, ? extends VR> child : getChildrenUnmodifiable()) {
			if (!(child instanceof IContentPart)) {
				child.deactivate();
			}
		}
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	public IViewer<VR> getAdaptable() {
		return getViewer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<VR, ? extends VR>> getContentPartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IContentPart.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IFeedbackPart<VR, ? extends VR>> getFeedbackPartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IFeedbackPart.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IHandlePart<VR, ? extends VR>> getHandlePartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IHandlePart.class);
	}

	@Override
	public IRootPart<VR, ? extends VR> getRoot() {
		return this;
	}

	@Override
	public IViewer<VR> getViewer() {
		return viewerProperty.get();
	}

	@Override
	public void setAdaptable(IViewer<VR> viewer) {
		IViewer<VR> oldViewer = viewerProperty.get();
		if (oldViewer != null && viewer != oldViewer) {
			unregister(oldViewer);
		}
		viewerProperty.set(viewer);
		if (viewer != null && viewer != oldViewer) {
			register(viewer);
		}
	}

	@SuppressWarnings("serial")
	@Override
	protected void unregister(IViewer<VR> viewer) {
		// synchronize content children
		ContentBehavior<VR> contentBehavior = this
				.getAdapter(new TypeToken<ContentBehavior<VR>>(getClass()) {
				});
		if (contentBehavior != null) {
			contentBehavior.synchronizeContentChildren(Collections.emptyList());
		}
		super.unregister(viewer);
	}
}