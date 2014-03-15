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

import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractBehavior<V> implements IBehavior<V> {

	private IVisualPart<V> host;
	private boolean active;

	private List<IHandlePart<V>> handleParts;
	private List<IFeedbackPart<V>> feedbackParts;

	public void activate() {
		active = true;
	}

	public void deactivate() {
		active = false;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public IVisualPart<V> getHost() {
		return host;
	}

	public void setHost(IVisualPart<V> host) {
		this.host = host;
	}

	protected void addHandles(List<IContentPart<V>> anchorages) {
		handleParts = BehaviorUtils.createHandles(this, anchorages);
		BehaviorUtils.<V> addAnchoreds(getHost().getRoot(), anchorages,
				handleParts);
	}

	protected void removeHandles(List<IContentPart<V>> anchorages) {
		if (handleParts != null && !handleParts.isEmpty()) {
			BehaviorUtils.<V> removeAnchoreds(getHost().getRoot(), anchorages,
					handleParts);
			handleParts.clear();
		}
	}

	protected void addFeedback(List<IContentPart<V>> targets) {
		feedbackParts = BehaviorUtils.createFeedback(this, targets);
		BehaviorUtils.<V> addAnchoreds(getHost().getRoot(), targets,
				feedbackParts);
	}

	protected void removeFeedback(List<IContentPart<V>> targets) {
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			BehaviorUtils.<V> removeAnchoreds(getHost().getRoot(), targets,
					feedbackParts);
			feedbackParts.clear();
		}
	}

}