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
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Inject;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	@Inject
	// scoped to single instance within viewer
	private IFeedbackPartFactory<VR> feedbackPartFactory;

	@Inject
	// scoped to single instance within viewer
	private IHandlePartFactory<VR> handlePartFactory;

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IVisualPart<VR, ? extends VR> host;
	private boolean active;

	private List<IHandlePart<VR, ? extends VR>> handleParts;
	private List<IFeedbackPart<VR, ? extends VR>> feedbackParts;

	@Override
	public void activate() {
		boolean oldActive = active;
		active = true;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	protected void addFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		addFeedback(targets, Collections.<Object, Object> emptyMap());
	}

	protected void addFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			Map<Object, Object> contextMap) {
		if (targets != null && !targets.isEmpty()) {
			// create feedback part, adjusting the relevant adapter scopes
			// before
			switchAdaptableScopes();
			feedbackParts = feedbackPartFactory.createFeedbackParts(targets,
					this, contextMap);
			BehaviorUtils.<VR> addAnchorages(getHost().getRoot(), targets,
					feedbackParts);
		}
	}

	protected void addHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		addHandles(targets, Collections.<Object, Object> emptyMap());
	}

	protected void addHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			Map<Object, Object> contextMap) {
		if (targets != null && !targets.isEmpty()) {
			// create handle part, adjusting the relevant adaptable scopes
			// before
			switchAdaptableScopes();
			handleParts = handlePartFactory.createHandleParts(targets, this,
					contextMap);
			BehaviorUtils.<VR> addAnchorages(getHost().getRoot(), targets,
					handleParts);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	protected void switchAdaptableScopes() {
		// adjust relevant adaptable scopes before creating new part
		// TODO: move this into AdaptableScopes, making it more generic (i.e.
		// traverse adaptables)
		IVisualPart<VR, ? extends VR> host = getHost();
		IViewer<VR> viewer = host.getRoot().getViewer();
		IDomain<VR> domain = viewer.getDomain();
		AdaptableScopes.switchTo(domain);
		AdaptableScopes.switchTo(viewer);
		AdaptableScopes.switchTo(host);
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
	public IVisualPart<VR, ? extends VR> getAdaptable() {
		return getHost();
	}

	protected List<IFeedbackPart<VR, ? extends VR>> getFeedbackParts() {
		return feedbackParts;
	}

	protected List<IHandlePart<VR, ? extends VR>> getHandleParts() {
		return handleParts;
	}

	@Override
	public IVisualPart<VR, ? extends VR> getHost() {
		return host;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	protected void removeFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			if (targets != null && !targets.isEmpty()) {
				BehaviorUtils.<VR> removeAnchorages(getHost().getRoot(),
						targets, feedbackParts);
				feedbackParts.clear();
			}
		}
	}

	protected void removeHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (handleParts != null && !handleParts.isEmpty()) {
			if (targets != null && !targets.isEmpty()) {
				BehaviorUtils.<VR> removeAnchorages(getHost().getRoot(),
						targets, handleParts);
				handleParts.clear();
			}
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.host = adaptable;
	}

}