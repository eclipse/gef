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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.reflect.Types;
import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

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

	private Map<Set<IVisualPart<VR, ? extends VR>>, List<IFeedbackPart<VR, ? extends VR>>> feedbackPerTargetSetMap = new HashMap<>();
	private Map<Set<IVisualPart<VR, ? extends VR>>, List<IHandlePart<VR, ? extends VR>>> handlesPerTargetSetMap = new HashMap<>();

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
	 * Adds feedback for the given target part.
	 *
	 * @param target
	 *            The target part for which to add feedback.
	 */
	protected void addFeedback(IVisualPart<VR, ? extends VR> target) {
		addFeedback(Collections.singletonList(target));
	}

	/**
	 * Adds feedback for the given target parts.
	 *
	 * @param targets
	 *            The target parts for which to add feedback.
	 */
	protected void addFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<VR, ? extends VR>> targetSet = createTargetSet(targets);

		// check if feedback was already created for the target set
		if (hasFeedback(targetSet)) {
			throw new IllegalStateException(
					"Feedback was already added for the given set of target parts.");
		}

		// determine feedback part factory for the target set
		IFeedbackPartFactory<VR> factory = getFeedbackPartFactory(
				targets.get(0).getRoot().getViewer(),
				getFeedbackPartFactoryRole());

		// generate feedback parts
		List<IFeedbackPart<VR, ? extends VR>> feedbackParts = null;
		if (factory != null) {
			feedbackParts = factory.createFeedbackParts(targets, this,
					Collections.emptyMap());
		}
		if (feedbackParts == null) {
			// XXX: An empty list is put into the feedback per target set map,
			// so that we know that feedback was generated for that target set.
			feedbackParts = Collections.emptyList();
		}

		// store feedback parts for the target set
		getFeedbackPerTargetSetMap().put(targetSet, feedbackParts);

		// add feedback to the viewer
		if (!feedbackParts.isEmpty()) {
			BehaviorUtils.<VR> addAnchoreds(targets.get(0).getRoot(), targets,
					feedbackParts);
		}
	}

	/**
	 * Adds handles for the given target part.
	 *
	 * @param target
	 *            The target part for which to add feedback.
	 */
	protected void addHandles(IVisualPart<VR, ? extends VR> target) {
		addHandles(Collections.singletonList(target));
	}

	/**
	 * Adds handles for the given target parts.
	 *
	 * @param targets
	 *            The target parts for which to add handles.
	 */
	protected void addHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<VR, ? extends VR>> targetSet = createTargetSet(targets);

		// check if handle was already created for the target set
		if (hasHandles(targetSet)) {
			throw new IllegalStateException(
					"Handles were already added for the given set of target parts.");
		}

		// determine handle part factory for the target set
		IHandlePartFactory<VR> factory = getHandlePartFactory(
				targets.get(0).getRoot().getViewer(),
				getHandlePartFactoryRole());

		// generate handle parts
		List<IHandlePart<VR, ? extends VR>> handleParts = null;
		if (factory != null) {
			handleParts = factory.createHandleParts(targets, this,
					Collections.emptyMap());
		}
		if (handleParts == null) {
			// XXX: An empty list is put into the handles per target set map,
			// so that we know that handles were generated for that target set.
			handleParts = Collections.emptyList();
		}

		// store handle parts for the target set
		getHandlesPerTargetSetMap().put(targetSet, handleParts);

		// add handles to the viewer
		if (!handleParts.isEmpty()) {
			BehaviorUtils.<VR> addAnchoreds(targets.get(0).getRoot(), targets,
					handleParts);
		}
	}

	/**
	 * Removes all feedback.
	 */
	protected void clearFeedback() {
		Set<Set<IVisualPart<VR, ? extends VR>>> keys = getFeedbackPerTargetSetMap()
				.keySet();
		for (Set<IVisualPart<VR, ? extends VR>> key : keys) {
			removeFeedback(key);
		}
	}

	/**
	 * Removes all handles.
	 */
	protected void clearHandles() {
		Set<Set<IVisualPart<VR, ? extends VR>>> keys = getHandlesPerTargetSetMap()
				.keySet();
		for (Set<IVisualPart<VR, ? extends VR>> key : keys) {
			removeHandles(key);
		}
	}

	private Set<IVisualPart<VR, ? extends VR>> createTargetSet(
			Collection<? extends IVisualPart<VR, ? extends VR>> targets) {
		Set<IVisualPart<VR, ? extends VR>> targetSet = Collections
				.newSetFromMap(
						new IdentityHashMap<IVisualPart<VR, ? extends VR>, Boolean>());
		targetSet.addAll(targets);
		return targetSet;
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
	 * Returns the {@link IFeedbackPartFactory} that is registered as an adapter
	 * at the given {@link IViewer} under the given role.
	 *
	 * @param viewer
	 *            The {@link IViewer} where the {@link IFeedbackPartFactory} is
	 *            registered.
	 * @param role
	 *            The role under which the {@link IFeedbackPartFactory} is
	 *            registered.
	 * @return The {@link IFeedbackPartFactory} that is registered as an adapter
	 *         at the given {@link IViewer} under the given role.
	 */
	@SuppressWarnings("serial")
	protected IFeedbackPartFactory<VR> getFeedbackPartFactory(
			IViewer<VR> viewer, String role) {
		return viewer.getAdapter(
				AdapterKey.get(new TypeToken<IFeedbackPartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())), role));
	}

	/**
	 * Returns the role under which the {@link IFeedbackPartFactory} for this
	 * {@link IBehavior} is registered.
	 *
	 * @return The role under which the {@link IFeedbackPartFactory} for this
	 *         {@link IBehavior} is registered.
	 */
	protected abstract String getFeedbackPartFactoryRole();

	/**
	 * Returns the map that stores the feedback parts per target part set.
	 *
	 * @return The map that stores the feedback parts per target part set.
	 */
	protected Map<Set<IVisualPart<VR, ? extends VR>>, List<IFeedbackPart<VR, ? extends VR>>> getFeedbackPerTargetSetMap() {
		return feedbackPerTargetSetMap;
	}

	/**
	 * Returns the {@link IHandlePartFactory} that is registered as an adapter
	 * at the given {@link IViewer} under the given role.
	 *
	 * @param viewer
	 *            The {@link IViewer} where the {@link IHandlePartFactory} is
	 *            registered.
	 * @param role
	 *            The role under which the {@link IHandlePartFactory} is
	 *            registered.
	 * @return The {@link IHandlePartFactory} that is registered as an adapter
	 *         at the given {@link IViewer} under the given role.
	 */
	@SuppressWarnings("serial")
	protected IHandlePartFactory<VR> getHandlePartFactory(IViewer<VR> viewer,
			String role) {
		return viewer.getAdapter(
				AdapterKey.get(new TypeToken<IHandlePartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())), role));
	}

	/**
	 * Returns the role under which the {@link IHandlePartFactory} for this
	 * {@link IBehavior} is registered.
	 *
	 * @return The role under which the {@link IHandlePartFactory} for this
	 *         {@link IBehavior} is registered.
	 */
	protected abstract String getHandlePartFactoryRole();

	/**
	 * Returns the map that stores the handle parts per target part set.
	 *
	 * @return The map that stores the handle parts per target part set.
	 */
	protected Map<Set<IVisualPart<VR, ? extends VR>>, List<IHandlePart<VR, ? extends VR>>> getHandlesPerTargetSetMap() {
		return handlesPerTargetSetMap;
	}

	@Override
	public IVisualPart<VR, ? extends VR> getHost() {
		return hostProperty.get();
	}

	/**
	 * Returns <code>true</code> if feedback was added for the given set of
	 * target parts, even if no feedback parts were generated for the given set
	 * of target parts. Otherwise returns <code>false</code>.
	 *
	 * @param targets
	 *            The set of target parts.
	 * @return <code>true</code> if feedback was added for the given set of
	 *         target parts, even if no feedback parts were generated, otherwise
	 *         <code>false</code>.
	 */
	protected boolean hasFeedback(
			Collection<? extends IVisualPart<VR, ? extends VR>> targets) {
		return getFeedbackPerTargetSetMap()
				.containsKey(createTargetSet(targets));
	}

	/**
	 * Returns <code>true</code> if feedback was added for the given target
	 * part, even if no feedback parts were generated for the given target part.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param target
	 *            The target part.
	 * @return <code>true</code> if feedback was added for the given target
	 *         part, even if no feedback parts were generated, otherwise
	 *         <code>false</code>.
	 */
	protected boolean hasFeedback(IVisualPart<VR, ? extends VR> target) {
		return hasFeedback(Collections.singletonList(target));
	}

	/**
	 * Returns <code>true</code> if handles were added for the given set of
	 * target parts, even if no handle parts were generated for the given set of
	 * target parts. Otherwise returns <code>false</code>.
	 *
	 * @param targets
	 *            The set of target parts.
	 * @return <code>true</code> if handles were added for the given set of
	 *         target parts, even if no handle parts were generated, otherwise
	 *         <code>false</code>.
	 */
	protected boolean hasHandles(
			Collection<? extends IVisualPart<VR, ? extends VR>> targets) {
		return getHandlesPerTargetSetMap()
				.containsKey(createTargetSet(targets));
	}

	/**
	 * Returns <code>true</code> if handles were added for the given target
	 * part, even if no handle parts were generated for the given target part.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param target
	 *            The target part.
	 * @return <code>true</code> if handles were added for the given target
	 *         part, even if no handles parts were generated, otherwise
	 *         <code>false</code>.
	 */
	protected boolean hasHandles(IVisualPart<VR, ? extends VR> target) {
		return hasHandles(Collections.singletonList(target));
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Removes feedback for the given target.
	 *
	 * @param target
	 *            The target for which to remove feedback.
	 */
	protected void removeFeedback(IVisualPart<VR, ? extends VR> target) {
		if (target == null) {
			throw new IllegalArgumentException(
					"The given target part may not be null.");
		}
		removeFeedback(Collections.singletonList(target));
	}

	/**
	 * Removes feedback for the given targets.
	 *
	 * @param targets
	 *            The list of target parts.
	 */
	protected void removeFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be empty.");
		}
		removeFeedback(createTargetSet(targets));
	}

	/**
	 * Removes feedback for the given target parts.
	 *
	 * @param targetSet
	 *            The target parts.
	 */
	protected void removeFeedback(
			Set<? extends IVisualPart<VR, ? extends VR>> targetSet) {
		if (targetSet == null) {
			throw new IllegalArgumentException(
					"The given set of target parts may not be null.");
		}
		if (targetSet.isEmpty()) {
			throw new IllegalArgumentException(
					"The given set of target parts may not be empty.");
		}

		// check if feedback was created for the target set
		if (!hasFeedback(targetSet)) {
			throw new IllegalStateException(
					"Feedback was not added for the given set of target parts.");
		}

		// remove feedback parts from the feedback per target set map
		List<IFeedbackPart<VR, ? extends VR>> feedbackParts = getFeedbackPerTargetSetMap()
				.remove(targetSet);

		// remove feedback from the viewer
		if (!feedbackParts.isEmpty()) {
			BehaviorUtils.removeAnchoreds(targetSet.iterator().next().getRoot(),
					targetSet, feedbackParts);
		}
		for (IFeedbackPart<VR, ? extends VR> fp : feedbackParts) {
			fp.dispose();
		}
	}

	/**
	 * Removes handles for the given target.
	 *
	 * @param target
	 *            The target for which to remove handles.
	 */
	protected void removeHandles(IVisualPart<VR, ? extends VR> target) {
		removeHandles(Collections.singletonList(target));
	}

	/**
	 * Removes handles for the given target parts.
	 *
	 * @param targets
	 *            The target parts.
	 */
	protected void removeHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be empty.");
		}
		removeHandles(createTargetSet(targets));
	}

	/**
	 * Removes handles for the given target parts.
	 *
	 * @param targetSet
	 *            The target parts.
	 */
	protected void removeHandles(
			Set<? extends IVisualPart<VR, ? extends VR>> targetSet) {
		if (targetSet == null) {
			throw new IllegalArgumentException(
					"The given set of target parts may not be null.");
		}
		if (targetSet.isEmpty()) {
			throw new IllegalArgumentException(
					"The given set of target parts may not be empty.");
		}

		// check if handles were created for the target set
		if (!hasHandles(targetSet)) {
			throw new IllegalStateException(
					"Handles were not added for the given set of target parts.");
		}

		// remove handle parts from the handles per target set map
		List<IHandlePart<VR, ? extends VR>> handleParts = getHandlesPerTargetSetMap()
				.remove(targetSet);

		// remove handles from the viewer
		if (!handleParts.isEmpty()) {
			BehaviorUtils.removeAnchoreds(targetSet.iterator().next().getRoot(),
					targetSet, handleParts);
		}
		for (IHandlePart<VR, ? extends VR> hp : handleParts) {
			hp.dispose();
		}
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.hostProperty.set(adaptable);
	}

	/**
	 * Updates the handles of the given <i>target</i> part. Returns a new
	 * {@link IHandlePart} that would be replacing the given
	 * <i>interactedWith</i> handle part if that part was not preserved (which
	 * it is). The user can then apply the information of the replacement part
	 * to the preserved <i>interactedWith</i> part.
	 *
	 * @param target
	 *            The target part for the handles.
	 * @param interactedWithComparator
	 *            A {@link Comparator} that can be used to identify a new handle
	 *            at the same position as the handle that is currently
	 *            interacted with. Can be <code>null</code> if no handle should
	 *            be preserved.
	 * @param interactedWith
	 *            The {@link IHandlePart} that is interacted with and therefore,
	 *            should be preserved, or <code>null</code>.
	 * @return The new {@link IHandlePart} for the position of the handle part
	 *         that is interacted with so that its information can be applied to
	 *         the preserved handle part.
	 */
	public IHandlePart<VR, ? extends VR> updateHandles(
			IVisualPart<VR, ? extends VR> target,
			Comparator<IHandlePart<VR, ? extends VR>> interactedWithComparator,
			IHandlePart<VR, ? extends VR> interactedWith) {
		return updateHandles(Collections.singletonList(target),
				interactedWithComparator, interactedWith);
	}

	/**
	 * Updates the handles of the given <i>targets</i>. Returns a new
	 * {@link IHandlePart} that would be replacing the given
	 * <i>interactedWith</i> handle part if that part was not preserved (which
	 * it is). The user can then apply the information of the replacement part
	 * to the preserved <i>interactedWith</i> part.
	 *
	 * @param targets
	 *            The target parts for the handles.
	 * @param interactedWithComparator
	 *            A {@link Comparator} that can be used to identify a new handle
	 *            at the same position as the handle that is currently
	 *            interacted with. Can be <code>null</code> if no handle should
	 *            be preserved.
	 * @param interactedWith
	 *            The {@link IHandlePart} that is interacted with and therefore,
	 *            should be preserved, or <code>null</code>.
	 * @return The new {@link IHandlePart} for the position of the handle part
	 *         that is interacted with so that its information can be applied to
	 *         the preserved handle part.
	 */
	public IHandlePart<VR, ? extends VR> updateHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			Comparator<IHandlePart<VR, ? extends VR>> interactedWithComparator,
			IHandlePart<VR, ? extends VR> interactedWith) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<VR, ? extends VR>> targetSet = createTargetSet(targets);

		// determine handle part factory for the target set
		IRootPart<VR, ? extends VR> root = targets.get(0).getRoot();
		IViewer<VR> viewer = root.getViewer();
		IHandlePartFactory<VR> handlePartFactory = getHandlePartFactory(viewer,
				getHandlePartFactoryRole());

		// determine new handles
		List<IHandlePart<VR, ? extends VR>> newHandles = handlePartFactory
				.createHandleParts(targets, this, Collections.emptyMap());

		// compare to current handles => remove/add as needed
		IHandlePart<VR, ? extends VR> replacementHandle = null;
		if (newHandles != null && !newHandles.isEmpty()) {
			// set new handles as anchoreds so that they can be compared
			List<IHandlePart<VR, ? extends VR>> toBeAdded = new ArrayList<>(
					newHandles);
			BehaviorUtils.<VR> addAnchoreds(root, targets, toBeAdded);

			if (interactedWithComparator != null) {
				// find new handle at interaction position and remove it from
				// the new handles
				double minDistance = -1;
				Iterator<IHandlePart<VR, ? extends VR>> it = toBeAdded
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
					toBeAdded.remove(replacementHandle);
				}
			}

			// determine old handles for target
			List<IHandlePart<VR, ? extends VR>> oldHandles;
			List<IHandlePart<VR, ? extends VR>> currentHandleParts = getHandlesPerTargetSetMap()
					.get(targetSet);

			if (!currentHandleParts.isEmpty()) {
				oldHandles = new ArrayList<>(currentHandleParts);
				if (interactedWith != null) {
					// remove interacted with handle from old handles so that it
					// is preserved
					oldHandles.remove(interactedWith);
				}

				// find handles that no longer exist
				List<IHandlePart<VR, ? extends VR>> toBeRemoved = new ArrayList<>();
				Iterator<IHandlePart<VR, ? extends VR>> it = oldHandles
						.iterator();
				while (it.hasNext()) {
					IHandlePart<VR, ? extends VR> oldHandle = it.next();
					boolean noLongerExists = true;
					for (IHandlePart<VR, ? extends VR> newHandle : toBeAdded) {
						if (newHandle instanceof Comparable && newHandle
								.getClass() == oldHandle.getClass()) {
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
						toBeRemoved.add(oldHandle);
						it.remove();
					}
				}

				// remove handles that no longer exist
				BehaviorUtils.removeAnchoreds(root, targets, toBeRemoved);
				getHandlesPerTargetSetMap().get(targetSet)
						.removeAll(toBeRemoved);
				for (IHandlePart<VR, ? extends VR> hp : toBeRemoved) {
					hp.dispose();
				}
			} else {
				oldHandles = new ArrayList<>();
			}

			// find new handles that did not exist yet
			List<IHandlePart<VR, ? extends VR>> toBeDisposed = new ArrayList<>();
			Iterator<IHandlePart<VR, ? extends VR>> it = toBeAdded.iterator();
			while (it.hasNext()) {
				IHandlePart<VR, ? extends VR> newHandle = it.next();
				boolean existsAlready = false;
				for (IHandlePart<VR, ? extends VR> oldHandle : oldHandles) {
					if (oldHandle instanceof Comparable
							&& newHandle.getClass() == oldHandle.getClass()) {
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
					toBeDisposed.add(newHandle);
					it.remove();
				}
			}

			// add replacement handle to existing handles
			if (replacementHandle != null) {
				toBeDisposed.add(replacementHandle);
			}

			// remove already existing handles
			BehaviorUtils.removeAnchoreds(root, targets, toBeDisposed);
			for (IHandlePart<VR, ? extends VR> hp : toBeDisposed) {
				hp.dispose();
			}

			// add new handles that did not exist yet
			if (!getHandlesPerTargetSetMap().containsKey(targetSet)) {
				getHandlesPerTargetSetMap().put(targetSet,
						new ArrayList<IHandlePart<VR, ? extends VR>>());
			} else {
				getHandlesPerTargetSetMap().put(targetSet,
						new ArrayList<IHandlePart<VR, ? extends VR>>(
								getHandlesPerTargetSetMap().get(targetSet)));
			}
			getHandlesPerTargetSetMap().get(targetSet).addAll(toBeAdded);
		}

		return replacementHandle;
	}

}