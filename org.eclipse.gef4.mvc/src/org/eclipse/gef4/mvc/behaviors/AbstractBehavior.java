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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IVisualPart<VR> host;
	private boolean active;

	private List<IHandlePart<VR>> handleParts;
	private List<IFeedbackPart<VR>> feedbackParts;

	@Override
	public void activate() {
		boolean oldActive = active;
		active = true;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	protected void addFeedback(List<? extends IVisualPart<VR>> targets) {
		addFeedback(targets, Collections.<Object, Object> emptyMap());
	}

	protected void addFeedback(List<? extends IVisualPart<VR>> targets,
			Map<Object, Object> contextMap) {
		@SuppressWarnings("unchecked")
		List<IContentPart<VR>> contentParts = PartUtils.filterParts(targets,
				IContentPart.class);
		if (contentParts != null && !contentParts.isEmpty()) {
			feedbackParts = BehaviorUtils.createFeedback(contentParts, this,
					contextMap);
			BehaviorUtils.<VR> addAnchorages(getHost().getRoot(), contentParts,
					feedbackParts);
		}
	}

	protected void addHandles(List<? extends IVisualPart<VR>> targets) {
		// create handles for content parts only
		addHandles(targets, Collections.<Object, Object> emptyMap());
	}

	protected void addHandles(List<? extends IVisualPart<VR>> targets,
			Map<Object, Object> contextMap) {
		@SuppressWarnings("unchecked")
		List<IContentPart<VR>> contentParts = PartUtils.filterParts(targets,
				IContentPart.class);
		if (contentParts != null && !contentParts.isEmpty()) {
			handleParts = BehaviorUtils.createHandles(contentParts, this,
					contextMap);
			BehaviorUtils.<VR> addAnchorages(getHost().getRoot(), contentParts,
					handleParts);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void deactivate() {
		boolean oldActive = active;
		active = false;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	@Override
	public IVisualPart<VR> getAdaptable() {
		return getHost();
	}

	protected List<IFeedbackPart<VR>> getFeedbackParts() {
		return feedbackParts;
	}

	protected List<IHandlePart<VR>> getHandleParts() {
		return handleParts;
	}

	@Override
	public IVisualPart<VR> getHost() {
		return host;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	protected void removeFeedback(List<? extends IVisualPart<VR>> targets) {
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<IContentPart<VR>> contentParts = PartUtils.filterParts(
					targets, IContentPart.class);
			if (contentParts != null && !contentParts.isEmpty()) {
				BehaviorUtils.<VR> removeAnchorages(getHost().getRoot(),
						contentParts, feedbackParts);
				feedbackParts.clear();
			}
		}
	}

	protected void removeHandles(List<? extends IVisualPart<VR>> targets) {
		if (handleParts != null && !handleParts.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<IContentPart<VR>> contentParts = PartUtils.filterParts(
					targets, IContentPart.class);
			if (contentParts != null && !contentParts.isEmpty()) {
				BehaviorUtils.<VR> removeAnchorages(getHost().getRoot(),
						contentParts, handleParts);
				handleParts.clear();
			}
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setAdaptable(IVisualPart<VR> adaptable) {
		this.host = adaptable;
	}

}