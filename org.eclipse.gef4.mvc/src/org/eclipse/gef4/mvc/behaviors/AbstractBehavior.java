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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.collections.ObservableSetMultimap;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	private IVisualPart<VR, ? extends VR> host;
	private ActivatableSupport acs = new ActivatableSupport(this);

	private List<IHandlePart<VR, ? extends VR>> handleParts;
	private List<IFeedbackPart<VR, ? extends VR>> feedbackParts;

	@Override
	public final void activate() {
		if (!acs.isActive()) {
			acs.activate();
			doActivate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	/**
	 *
	 *
	 * @param targets
	 *            A list of {@link IVisualPart}s for which feedback is added to
	 *            the viewer.
	 * @param feedback
	 *            A list of {@link IFeedbackPart}s that are added to the viewer.
	 */
	protected void addFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			List<? extends IFeedbackPart<VR, ? extends VR>> feedback) {
		if (targets != null && !targets.isEmpty()) {
			feedbackParts = new ArrayList<>(feedback);
			BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(), targets,
					feedbackParts);
		}
	}

	/**
	 *
	 * @param targets
	 *            A list of {@link IVisualPart}s for which handle parts are
	 *            added to the viewer.
	 * @param handles
	 *            A list of {@link IHandlePart}s that are added to the viewer.
	 */
	protected void addHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			List<? extends IHandlePart<VR, ? extends VR>> handles) {
		if (handles != null && !handles.isEmpty()) {
			handleParts = new ArrayList<>(handles);
			BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(), targets,
					handleParts);
		}
	}

	@Override
	public final void deactivate() {
		if (acs.isActive()) {
			doDeactivate();
			acs.deactivate();
		}
	}

	/**
	 * Post {@link #activate()} hook that may be overwritten to e.g. register
	 * listeners.
	 */
	protected void doActivate() {
		// nothing to do by default
	}

	/**
	 * Pre {@link #deactivate()} hook that may be overwritten to e.g. unregister
	 * listeners.
	 */
	protected void doDeactivate() {
		// nothing to do by default
	}

	@Override
	public IVisualPart<VR, ? extends VR> getAdaptable() {
		return getHost();
	}

	/**
	 * Returns a list containing the feedback parts most recently created by
	 * this behavior.
	 *
	 * @return A list containing the feedback parts most recently created by
	 *         this behavior.
	 */
	protected List<IFeedbackPart<VR, ? extends VR>> getFeedbackParts() {
		return feedbackParts;
	}

	/**
	 * Returns a list containing the handle parts most recently created by this
	 * behavior.
	 *
	 * @return A list containing the handle parts most recently created by this
	 *         behavior.
	 */
	protected List<IHandlePart<VR, ? extends VR>> getHandleParts() {
		return handleParts;
	}

	@Override
	public IVisualPart<VR, ? extends VR> getHost() {
		return host;
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Removes the feedback parts previously created for the given target parts.
	 *
	 * @param targets
	 *            The list of target parts for which previously created feedback
	 *            is to be removed.
	 */
	protected void removeFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			if (targets != null && !targets.isEmpty()) {
				BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(), targets,
						feedbackParts);
				feedbackParts.clear();
			}
		}
	}

	/**
	 * Removes the handle parts previously created for the given target parts.
	 *
	 * @param targets
	 *            The list of target parts for which previously created handles
	 *            are to be removed.
	 */
	protected void removeHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (handleParts != null && !handleParts.isEmpty()) {
			if (targets != null && !targets.isEmpty()) {
				BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(), targets,
						handleParts);
				handleParts.clear();
			}
		}
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.host = adaptable;
	}

	/**
	 * Adjusts the relevant adaptable scopes to refer to the host of this
	 * behavior, it's viewer, and it's domain, respectively.
	 */
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

	/**
	 * Updates the handle parts for the given target. Handle parts that are not
	 * equal to any handle part within the given list of handle parts are
	 * removed, and the remaining handle parts are added. Therefore, the given
	 * handle parts are the
	 *
	 * @param target
	 *            The target {@link IVisualPart} for which to update the
	 *            handles.
	 * @param handles
	 *            The new handles for the given target.
	 */
	protected void updateHandles(IVisualPart<VR, ? extends VR> target,
			List<? extends IHandlePart<VR, ? extends VR>> handles) {
		if (handles != null && !handles.isEmpty()) {
			// determine old handles for target
			List<IHandlePart<VR, ? extends VR>> oldHandles = new ArrayList<>(
					getHandleParts());
			Iterator<IHandlePart<VR, ? extends VR>> it = oldHandles.iterator();
			while (it.hasNext()) {
				IHandlePart<VR, ? extends VR> oldHandle = it.next();
				ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = oldHandle
						.getAnchoragesUnmodifiable();
				if (!anchorages.keySet().contains(target)) {
					it.remove();
				}
			}

			// set the handles as anchoreds so that they can be compared
			List<IHandlePart<VR, ? extends VR>> newHandles = new ArrayList<>(
					handles);
			BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(),
					Collections.singletonList(target), newHandles);

			// remove handles that no longer exist
			// TODO

			// find new handles that did not exist yet
			List<IHandlePart<VR, ? extends VR>> alreadyExists = new ArrayList<>();
			it = newHandles.iterator();
			while (it.hasNext()) {
				IHandlePart<VR, ? extends VR> newHandle = it.next();
				boolean existsAlready = false;
				for (IHandlePart<VR, ? extends VR> oldHandle : oldHandles) {
					if (oldHandle instanceof Comparable) {
						Comparable<IHandlePart<VR, ? extends VR>> comparable = (Comparable<IHandlePart<VR, ? extends VR>>) oldHandle;
						int compareTo = comparable.compareTo(newHandle);
						if (compareTo == 0) {
							existsAlready = true;
							break;
						}
					}
				}
				if (existsAlready) {
					alreadyExists.add(newHandle);
					it.remove();
				}
			}
			// remove already existing handles
			BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(),
					Collections.singletonList(target), alreadyExists);
			// add new handles that did not exist yet
			handleParts.addAll(newHandles);
		}
	}

}