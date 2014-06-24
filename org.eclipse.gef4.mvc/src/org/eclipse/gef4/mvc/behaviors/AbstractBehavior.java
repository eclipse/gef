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
import java.util.List;

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IVisualPart<VR> host;
	private boolean active;

	private List<IHandlePart<VR>> handleParts;
	private List<IFeedbackPart<VR>> feedbackParts;

	public void activate() {
		boolean oldActive = active;
		active = true;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	public void deactivate() {
		boolean oldActive = active;
		active = false;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setAdaptable(IVisualPart<VR> adaptable) {
		this.host = adaptable;
	}

	@Override
	public IVisualPart<VR> getAdaptable() {
		return getHost();
	}

	public IVisualPart<VR> getHost() {
		return host;
	}

	protected void addHandles(List<IContentPart<VR>> anchorages) {
		handleParts = BehaviorUtils.createHandles(this, anchorages);
		BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(), anchorages,
				handleParts);
	}

	protected void removeHandles(List<IContentPart<VR>> anchorages) {
		if (handleParts != null && !handleParts.isEmpty()) {
			BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(), anchorages,
					handleParts);
			handleParts.clear();
		}
	}

	protected void addFeedback(List<IContentPart<VR>> targets) {
		feedbackParts = BehaviorUtils.createFeedback(this, targets);
		BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(), targets,
				feedbackParts);
	}

	protected void removeFeedback(List<IContentPart<VR>> targets) {
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(), targets,
					feedbackParts);
			feedbackParts.clear();
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}