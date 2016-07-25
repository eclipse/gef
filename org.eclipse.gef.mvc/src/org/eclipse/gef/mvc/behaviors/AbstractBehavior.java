/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The {@link AbstractBehavior} can be used as a base class for
 * {@link IBehavior} implementations. It implements activation and deactivation
 * of its adapters, and provides methods for the addition and removal of
 * feedback and handles, as well as a method that can be used to update the
 * handles for a given target part.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	private ReadOnlyObjectWrapper<IVisualPart<VR, ? extends VR>> hostProperty = new ReadOnlyObjectWrapper<>();
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

	@Override
	public ReadOnlyObjectProperty<IVisualPart<VR, ? extends VR>> adaptableProperty() {
		return hostProperty.getReadOnlyProperty();
	}

	/**
	 * Adds the given {@link IFeedbackPart}s to the root part of the
	 * {@link #getHost()}. Moreover, the {@link IFeedbackPart}s are anchored to
	 * the given target {@link IVisualPart}s.
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
	 * Adds the given {@link IHandlePart}s to the root part of the
	 * {@link #getHost()}. Moreover, the {@link IHandlePart}s are anchored to
	 * the given target {@link IVisualPart}s.
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
		return hostProperty.get();
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
				for (IFeedbackPart<VR, ? extends VR> fp : feedbackParts) {
					fp.dispose();
				}
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
				for (IHandlePart<VR, ? extends VR> hp : handleParts) {
					hp.dispose();
				}
				handleParts.clear();
			}
		}
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.hostProperty.set(adaptable);
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
	 * @param interactedWithComparator
	 *            A function that computes the distance to the currently
	 *            interacted with handle for the passed-in handle part. Can be
	 *            <code>null</code> if no handle should be preserved.
	 * @param interactedWith
	 *            The {@link IHandlePart} that is currently interacted with and
	 *            that should be preserved, or <code>null</code>.
	 * @return The new {@link IHandlePart} for the position of the handle part
	 *         that is interacted with.
	 */
	protected IHandlePart<VR, ? extends VR> updateHandles(
			IVisualPart<VR, ? extends VR> target,
			List<? extends IHandlePart<VR, ? extends VR>> handles,
			Comparator<IHandlePart<VR, ? extends VR>> interactedWithComparator,
			IHandlePart<VR, ? extends VR> interactedWith) {
		IHandlePart<VR, ? extends VR> replacementHandle = null;

		if (handles != null && !handles.isEmpty()) {
			// set new handles as anchoreds so that they can be compared
			List<IHandlePart<VR, ? extends VR>> newHandles = new ArrayList<>(
					handles);
			BehaviorUtils.<VR> addAnchoreds(getHost().getRoot(),
					Collections.singletonList(target), newHandles);

			if (interactedWithComparator != null) {
				// find new handle at interaction position and remove it from
				// the
				// new handles
				double minDistance = -1;
				Iterator<IHandlePart<VR, ? extends VR>> it = newHandles
						.iterator();
				while (it.hasNext()) {
					IHandlePart<VR, ? extends VR> newHandle = it.next();
					double distance = interactedWithComparator
							.compare(interactedWith, newHandle);
					if (replacementHandle == null || distance < minDistance) {
						minDistance = distance;
						replacementHandle = newHandle;
					}
				}
				// remove replacement handle from new handles
				if (replacementHandle != null) {
					newHandles.remove(replacementHandle);
				}
			}

			// determine old handles for target
			List<IHandlePart<VR, ? extends VR>> oldHandles;
			if (handleParts != null) {
				oldHandles = new ArrayList<>(getHandleParts());
				Iterator<IHandlePart<VR, ? extends VR>> it = oldHandles
						.iterator();
				while (it.hasNext()) {
					IHandlePart<VR, ? extends VR> oldHandle = it.next();
					ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = oldHandle
							.getAnchoragesUnmodifiable();
					if (!anchorages.keySet().contains(target)) {
						it.remove();
					}
				}

				if (interactedWith != null) {
					// remove interacted with handle from old handles so that it
					// is
					// preserved
					oldHandles.remove(interactedWith);
				}

				// find handles that no longer exist
				List<IHandlePart<VR, ? extends VR>> toRemove = new ArrayList<>();
				it = oldHandles.iterator();
				while (it.hasNext()) {
					IHandlePart<VR, ? extends VR> oldHandle = it.next();
					boolean noLongerExists = true;
					for (IHandlePart<VR, ? extends VR> newHandle : newHandles) {
						if (newHandle instanceof Comparable) {
							@SuppressWarnings("unchecked")
							Comparable<IHandlePart<VR, ? extends VR>> comparable = (Comparable<IHandlePart<VR, ? extends VR>>) oldHandle;
							int compareTo = comparable.compareTo(newHandle);
							if (compareTo == 0) {
								noLongerExists = false;
								break;
							}
						}
					}
					if (noLongerExists) {
						toRemove.add(oldHandle);
						it.remove();
					}
				}

				// remove handles that no longer exist
				BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(),
						Collections.singletonList(target), toRemove);
				for (IHandlePart<VR, ? extends VR> hp : toRemove) {
					hp.dispose();
				}
				handleParts.removeAll(toRemove);
			} else {
				oldHandles = new ArrayList<>();
			}

			// find new handles that did not exist yet
			List<IHandlePart<VR, ? extends VR>> alreadyExists = new ArrayList<>();
			Iterator<IHandlePart<VR, ? extends VR>> it = newHandles.iterator();
			while (it.hasNext()) {
				IHandlePart<VR, ? extends VR> newHandle = it.next();
				boolean existsAlready = false;
				for (IHandlePart<VR, ? extends VR> oldHandle : oldHandles) {
					if (oldHandle instanceof Comparable) {
						@SuppressWarnings("unchecked")
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

			// add replacement handle to existing handles
			if (replacementHandle != null) {
				alreadyExists.add(replacementHandle);
			}

			// remove already existing handles
			BehaviorUtils.<VR> removeAnchoreds(getHost().getRoot(),
					Collections.singletonList(target), alreadyExists);
			for (IHandlePart<VR, ? extends VR> hp : alreadyExists) {
				hp.dispose();
			}
			// add new handles that did not exist yet
			if (handleParts == null) {
				handleParts = new ArrayList<>(newHandles);
			} else {
				handleParts.addAll(newHandles);
			}
		}

		return replacementHandle;
	}

}