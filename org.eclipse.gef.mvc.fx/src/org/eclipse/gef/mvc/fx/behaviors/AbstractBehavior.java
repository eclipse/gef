/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

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
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;

/**
 * The {@link AbstractBehavior} can be used as a base class for
 * {@link IBehavior} implementations. It implements activation and deactivation
 * of its adapters, and provides methods for the addition and removal of
 * feedback and handles, as well as a method that can be used to update the
 * handles for a given target part.
 *
 * @author anyssen
 *
 */
public abstract class AbstractBehavior implements IBehavior {

	private ReadOnlyObjectWrapper<IVisualPart<? extends Node>> hostProperty = new ReadOnlyObjectWrapper<>();
	private ActivatableSupport acs = new ActivatableSupport(this);
	private Map<Set<IVisualPart<? extends Node>>, List<IFeedbackPart<? extends Node>>> feedbackPerTargetSet = new HashMap<>();
	private Map<Set<IVisualPart<? extends Node>>, List<IHandlePart<? extends Node>>> handlesPerTargetSet = new HashMap<>();

	@Override
	public final void activate() {
		acs.activate(null, this::doActivate);
	}

	@Override
	public final ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IVisualPart<? extends Node>> adaptableProperty() {
		return hostProperty.getReadOnlyProperty();
	}

	/**
	 * Adds the given anchoreds as children to the root part and anchors them to
	 * the given target parts.
	 *
	 * @param targets
	 *            The anchorages for the anchoreds.
	 * @param anchoreds
	 *            The anchored (feedback or handle) parts.
	 */
	protected void addAnchoreds(
			Collection<? extends IVisualPart<? extends Node>> targets,
			List<? extends IVisualPart<? extends Node>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			targets.iterator().next().getRoot().addChildren(anchoreds);
			for (IVisualPart<? extends Node> anchored : anchoreds) {
				for (IVisualPart<? extends Node> anchorage : targets) {
					// XXX: When adding feedback and handles, the anchorage
					// should not need to refresh its visuals
					boolean refreshVisual = anchorage.isRefreshVisual();
					anchorage.setRefreshVisual(false);
					anchored.attachToAnchorage(anchorage);
					anchorage.setRefreshVisual(refreshVisual);
				}
			}
		}
	}

	/**
	 * Adds the given anchoreds as children to the root part and anchors them to
	 * the given target parts. The given index determines the position where the
	 * anchoreds are inserted into the children list of the root part. The index
	 * can be used to control the z-order.
	 *
	 * @param targets
	 *            The target parts.
	 * @param anchoreds
	 *            The anchored (feedback or handle) parts.
	 * @param insertionIndex
	 *            The insertion index (controlling the z-order).
	 */
	protected void addAnchoreds(
			Collection<? extends IVisualPart<? extends Node>> targets,
			List<? extends IVisualPart<? extends Node>> anchoreds,
			int insertionIndex) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			targets.iterator().next().getRoot().addChildren(anchoreds,
					insertionIndex);
			for (IVisualPart<? extends Node> anchored : anchoreds) {
				for (IVisualPart<? extends Node> anchorage : targets) {
					// XXX: When adding feedback and handles, the anchorage
					// should not need to refresh its visuals
					boolean refreshVisual = anchorage.isRefreshVisual();
					anchorage.setRefreshVisual(false);
					anchored.attachToAnchorage(anchorage);
					anchorage.setRefreshVisual(refreshVisual);
				}
			}
		}
	}

	/**
	 * Adds feedback for the given target part.
	 *
	 * @param target
	 *            The target part for which to add feedback.
	 */
	protected void addFeedback(IVisualPart<? extends Node> target) {
		addFeedback(Collections.singletonList(target));
	}

	/**
	 * Adds feedback for the given target parts.
	 *
	 * @param targets
	 *            The target parts for which to add feedback.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void addFeedback(
			List<? extends IVisualPart<? extends Node>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<? extends Node>> targetSet = createTargetSet(targets);

		// check if feedback was already created for the target set
		if (hasFeedback(targetSet)) {
			throw new IllegalStateException(
					"Feedback was already added for the given set of target parts.");
		}

		// determine feedback part factory for the target set
		IFeedbackPartFactory factory = getFeedbackPartFactory(
				targets.get(0).getRoot().getViewer());

		// generate feedback parts
		List<IFeedbackPart<? extends Node>> feedbackParts = null;
		if (factory != null) {
			feedbackParts = factory.createFeedbackParts(targets,
					Collections.emptyMap());
		}
		if (feedbackParts == null) {
			// XXX: An empty list is put into the feedback per target set map,
			// so that we know that feedback was generated for that target set.
			feedbackParts = Collections.emptyList();
		}

		// store feedback parts for the target set
		getFeedbackPerTargetSet().put(targetSet, feedbackParts);

		// add feedback to the viewer
		if (!feedbackParts.isEmpty()) {
			addAnchoreds(targets, feedbackParts);
		}
	}

	/**
	 * Adds handles for the given target part.
	 *
	 * @param target
	 *            The target part for which to add feedback.
	 */
	protected void addHandles(IVisualPart<? extends Node> target) {
		addHandles(Collections.singletonList(target));
	}

	/**
	 * Adds handles for the given target parts.
	 *
	 * @param targets
	 *            The target parts for which to add handles.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void addHandles(
			List<? extends IVisualPart<? extends Node>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<? extends Node>> targetSet = createTargetSet(targets);

		// check if handle was already created for the target set
		if (hasHandles(targetSet)) {
			throw new IllegalStateException(
					"Handles were already added for the given set of target parts.");
		}

		// determine handle part factory for the target set
		IHandlePartFactory factory = getHandlePartFactory(
				targets.get(0).getRoot().getViewer());

		// generate handle parts
		List<IHandlePart<? extends Node>> handleParts = null;
		if (factory != null) {
			handleParts = factory.createHandleParts(targets,
					Collections.emptyMap());
		}
		if (handleParts == null) {
			// XXX: An empty list is put into the handles per target set map,
			// so that we know that handles were generated for that target set.
			handleParts = Collections.emptyList();
		}

		// store handle parts for the target set
		getHandlesPerTargetSet().put(targetSet, handleParts);

		// add handles to the viewer
		if (!handleParts.isEmpty()) {
			addAnchoreds(targets, handleParts);
		}
	}

	/**
	 * Removes all feedback.
	 */
	protected void clearFeedback() {
		Set<Set<IVisualPart<? extends Node>>> keys = getFeedbackPerTargetSet()
				.keySet();
		for (Set<IVisualPart<? extends Node>> key : new ArrayList<>(keys)) {
			removeFeedback(key);
		}
	}

	/**
	 * Removes all handles.
	 */
	protected void clearHandles() {
		Set<Set<IVisualPart<? extends Node>>> keys = getHandlesPerTargetSet()
				.keySet();
		for (Set<IVisualPart<? extends Node>> key : new ArrayList<>(keys)) {
			removeHandles(key);
		}
	}

	private Set<IVisualPart<? extends Node>> createTargetSet(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		Set<IVisualPart<? extends Node>> targetSet = Collections.newSetFromMap(
				new IdentityHashMap<IVisualPart<? extends Node>, Boolean>());
		targetSet.addAll(targets);
		return targetSet;
	}

	@Override
	public final void deactivate() {
		acs.deactivate(this::doDeactivate, null);
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
	public IVisualPart<? extends Node> getAdaptable() {
		return getHost();
	}

	/**
	 * Returns a list that contains all {@link IHandlePart}s that were generated
	 * for the given target parts by this {@link IBehavior}. If no handle parts
	 * were generated for the given target parts, an empty list is returned.
	 *
	 * @param targets
	 *            A collection of target parts.
	 * @return A list that contains all handle parts that were generated for the
	 *         given target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected List<IFeedbackPart<? extends Node>> getFeedback(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		List<IFeedbackPart<? extends Node>> list = getFeedbackPerTargetSet()
				.get(targets instanceof Set
						? ((Set<? extends IVisualPart<? extends Node>>) targets)
						: createTargetSet(targets));
		return list == null ? Collections.emptyList() : list;
	}

	/**
	 * Returns a list that contains all {@link IHandlePart}s that were generated
	 * for the given target part by this {@link IBehavior}. If no handle parts
	 * were generated for the given target part, an empty list is returned.
	 *
	 * @param target
	 *            The target part.
	 * @return A list that contains all handle parts that were generated for the
	 *         given target part.
	 */
	protected List<IFeedbackPart<? extends Node>> getFeedback(
			IVisualPart<? extends Node> target) {
		return getFeedback(Collections.singletonList(target));
	}

	/**
	 * Returns the {@link IFeedbackPartFactory} that should be used for feedback
	 * creation.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to determine the
	 *            {@link IFeedbackPartFactory} for this {@link IBehavior}.
	 * @return The {@link IFeedbackPartFactory} that should be used for feedback
	 *         creation.
	 */
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		throw new UnsupportedOperationException(
				"The default mechanism for generation of feedback depends on a feedback "
						+ "part factory that needs to be made accessible by implementing "
						+ this.getClass()
						+ "#getFeedbackPartFactory(IViewer) method. In order to query the "
						+ "factory from the viewer using a dedicated role, an implementation "
						+ "can delegate to getFeedbackPartFactory(IViewer, String).");
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
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer,
			String role) {
		return viewer
				.getAdapter(AdapterKey.get(IFeedbackPartFactory.class, role));
	}

	/**
	 * Returns the map that stores the feedback parts per target part set.
	 *
	 * @return The map that stores the feedback parts per target part set.
	 */
	protected Map<Set<IVisualPart<? extends Node>>, List<IFeedbackPart<? extends Node>>> getFeedbackPerTargetSet() {
		return feedbackPerTargetSet;
	}

	/**
	 * Returns the {@link IHandlePartFactory} that should be used for handle
	 * creation.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to determine the
	 *            {@link IHandlePartFactory} for this {@link IBehavior}.
	 * @return The {@link IHandlePartFactory} that should be used for feedback
	 *         creation.
	 */
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		throw new UnsupportedOperationException(
				"The default mechanism for generation of handles depends on a handle "
						+ "part factory that needs to be made accessible by implementing "
						+ this.getClass()
						+ "#getHandlePartFactory(IViewer) method. In order to query the "
						+ "factory from the viewer using a dedicated role, an implementation "
						+ "can delegate to getHandlePartFactory(IViewer, String).");
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
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer,
			String role) {
		return viewer
				.getAdapter(AdapterKey.get(IHandlePartFactory.class, role));
	}

	/**
	 * Returns a list that contains all {@link IHandlePart}s that were generated
	 * for the given target parts by this {@link IBehavior}. If no handle parts
	 * were generated for the given target parts, an empty list is returned.
	 *
	 * @param targets
	 *            A collection of target parts.
	 * @return A list that contains all handle parts that were generated for the
	 *         given target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected List<IHandlePart<? extends Node>> getHandles(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		List<IHandlePart<? extends Node>> list = getHandlesPerTargetSet()
				.get(targets instanceof Set
						? ((Set<? extends IVisualPart<? extends Node>>) targets)
						: createTargetSet(targets));
		return list == null ? Collections.emptyList() : list;
	}

	/**
	 * Returns a list that contains all {@link IHandlePart}s that were generated
	 * for the given target part by this {@link IBehavior}. If no handle parts
	 * were generated for the given target part, an empty list is returned.
	 *
	 * @param target
	 *            The target part.
	 * @return A list that contains all handle parts that were generated for the
	 *         given target part.
	 */
	protected List<IHandlePart<? extends Node>> getHandles(
			IVisualPart<? extends Node> target) {
		return getHandles(Collections.singletonList(target));
	}

	/**
	 * Returns the map that stores the handle parts per target part set.
	 *
	 * @return The map that stores the handle parts per target part set.
	 */
	protected Map<Set<IVisualPart<? extends Node>>, List<IHandlePart<? extends Node>>> getHandlesPerTargetSet() {
		return handlesPerTargetSet;
	}

	@Override
	public IVisualPart<? extends Node> getHost() {
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
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected boolean hasFeedback(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		return getFeedbackPerTargetSet().containsKey(targets instanceof Set
				? ((Set<? extends IVisualPart<? extends Node>>) targets)
				: createTargetSet(targets));
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
	protected boolean hasFeedback(IVisualPart<? extends Node> target) {
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
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected boolean hasHandles(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		return getHandlesPerTargetSet().containsKey(targets instanceof Set
				? ((Set<? extends IVisualPart<? extends Node>>) targets)
				: createTargetSet(targets));
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
	protected boolean hasHandles(IVisualPart<? extends Node> target) {
		return hasHandles(Collections.singletonList(target));
	}

	@Override
	public final boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Removes the given anchoreds as children from the root part and as
	 * anchoreds from the given target parts.
	 *
	 * @param targets
	 *            The anchorages of the anchoreds.
	 * @param anchoreds
	 *            The anchoreds (feedback or handles) that are to be removed.
	 */
	protected void removeAnchoreds(
			Collection<? extends IVisualPart<? extends Node>> targets,
			List<? extends IVisualPart<? extends Node>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			for (IVisualPart<? extends Node> anchored : anchoreds) {
				for (IVisualPart<? extends Node> anchorage : targets) {
					// XXX: When removing feedback and handles, the anchorage
					// should not need to refresh its visuals
					boolean refreshVisual = anchorage.isRefreshVisual();
					anchorage.setRefreshVisual(false);
					anchored.detachFromAnchorage(anchorage);
					anchorage.setRefreshVisual(refreshVisual);
				}
			}
			anchoreds.iterator().next().getRoot().removeChildren(anchoreds);
		}
	}

	/**
	 * Removes feedback for the given targets.
	 *
	 * @param targets
	 *            The list of target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void removeFeedback(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be empty.");
		}
		removeFeedback(targets instanceof Set
				? ((Set<? extends IVisualPart<? extends Node>>) targets)
				: createTargetSet(targets));
	}

	/**
	 * Removes feedback for the given target.
	 *
	 * @param target
	 *            The target for which to remove feedback.
	 */
	protected void removeFeedback(IVisualPart<? extends Node> target) {
		if (target == null) {
			throw new IllegalArgumentException(
					"The given target part may not be null.");
		}
		removeFeedback(Collections.singletonList(target));
	}

	/**
	 * Removes feedback for the given target parts.
	 *
	 * @param targetSet
	 *            The target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void removeFeedback(
			Set<? extends IVisualPart<? extends Node>> targetSet) {
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
		List<IFeedbackPart<? extends Node>> feedbackParts = getFeedbackPerTargetSet()
				.remove(targetSet);

		// remove feedback from the viewer
		if (!feedbackParts.isEmpty()) {
			removeAnchoreds(targetSet, feedbackParts);
		}
		for (IFeedbackPart<? extends Node> fp : feedbackParts) {
			fp.dispose();
		}
	}

	/**
	 * Removes handles for the given target parts.
	 *
	 * @param targets
	 *            The target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void removeHandles(
			Collection<? extends IVisualPart<? extends Node>> targets) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given list of target parts may not be empty.");
		}
		removeHandles(targets instanceof Set
				? ((Set<? extends IVisualPart<? extends Node>>) targets)
				: createTargetSet(targets));
	}

	/**
	 * Removes handles for the given target.
	 *
	 * @param target
	 *            The target for which to remove handles.
	 */
	protected void removeHandles(IVisualPart<? extends Node> target) {
		removeHandles(Collections.singletonList(target));
	}

	/**
	 * Removes handles for the given target parts.
	 *
	 * @param targetSet
	 *            The target parts.
	 */
	// TODO: Unify parameter types (List vs Set vs Collection)
	protected void removeHandles(
			Set<? extends IVisualPart<? extends Node>> targetSet) {
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
		List<IHandlePart<? extends Node>> handleParts = getHandlesPerTargetSet()
				.remove(targetSet);

		// remove handles from the viewer
		if (!handleParts.isEmpty()) {
			removeAnchoreds(targetSet, handleParts);
		}
		for (IHandlePart<? extends Node> hp : handleParts) {
			hp.dispose();
		}
	}

	@Override
	public void setAdaptable(IVisualPart<? extends Node> adaptable) {
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
	public IHandlePart<? extends Node> updateHandles(
			IVisualPart<? extends Node> target,
			Comparator<IHandlePart<? extends Node>> interactedWithComparator,
			IHandlePart<? extends Node> interactedWith) {
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
	// TODO: Unify parameter types (List vs Set vs Collection)
	public IHandlePart<? extends Node> updateHandles(
			List<? extends IVisualPart<? extends Node>> targets,
			Comparator<IHandlePart<? extends Node>> interactedWithComparator,
			IHandlePart<? extends Node> interactedWith) {
		if (targets == null) {
			throw new IllegalArgumentException(
					"The given target parts may not be null.");
		}
		if (targets.isEmpty()) {
			throw new IllegalArgumentException(
					"The given collection of target parts may not be empty.");
		}

		// compute target set
		Set<IVisualPart<? extends Node>> targetSet = createTargetSet(targets);

		// recomputation of handles is only allowed if there already are
		// handles for the targets
		if (!hasHandles(targetSet)) {
			return null;
		}

		// determine handle part factory for the target set
		IRootPart<? extends Node> root = targets.get(0).getRoot();
		IViewer viewer = root.getViewer();
		IHandlePartFactory handlePartFactory = getHandlePartFactory(viewer);

		// determine new handles
		List<IHandlePart<? extends Node>> newHandles = handlePartFactory
				.createHandleParts(targets, Collections.emptyMap());

		// compare to current handles => remove/add as needed
		IHandlePart<? extends Node> replacementHandle = null;
		if (newHandles != null && !newHandles.isEmpty()) {
			// set new handles as anchoreds so that they can be compared
			List<IHandlePart<? extends Node>> toBeAdded = new ArrayList<>(
					newHandles);
			addAnchoreds(targets, toBeAdded);

			if (interactedWithComparator != null) {
				// find new handle at interaction position and remove it from
				// the new handles
				double minDistance = -1;
				Iterator<IHandlePart<? extends Node>> it = toBeAdded.iterator();
				while (it.hasNext()) {
					IHandlePart<? extends Node> newHandle = it.next();
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
			List<IHandlePart<? extends Node>> oldHandles;
			List<IHandlePart<? extends Node>> currentHandleParts = getHandlesPerTargetSet()
					.get(targetSet);
			if (currentHandleParts != null && !currentHandleParts.isEmpty()) {
				oldHandles = new ArrayList<>(currentHandleParts);
				if (interactedWith != null) {
					// remove interacted with handle from old handles so that it
					// is preserved
					oldHandles.remove(interactedWith);
				}

				// find handles that no longer exist
				List<IHandlePart<? extends Node>> toBeRemoved = new ArrayList<>();
				Iterator<IHandlePart<? extends Node>> it = oldHandles
						.iterator();
				while (it.hasNext()) {
					IHandlePart<? extends Node> oldHandle = it.next();
					boolean noLongerExists = true;
					for (IHandlePart<? extends Node> newHandle : toBeAdded) {
						if (newHandle instanceof Comparable && newHandle
								.getClass() == oldHandle.getClass()) {
							@SuppressWarnings("unchecked")
							Comparable<IHandlePart<? extends Node>> comparable = (Comparable<IHandlePart<? extends Node>>) oldHandle;
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
				removeAnchoreds(targets, toBeRemoved);
				getHandlesPerTargetSet().get(targetSet).removeAll(toBeRemoved);
				for (IHandlePart<? extends Node> hp : toBeRemoved) {
					hp.dispose();
				}
			} else {
				oldHandles = new ArrayList<>();
			}

			// find new handles that did not exist yet
			List<IHandlePart<? extends Node>> toBeDisposed = new ArrayList<>();
			Iterator<IHandlePart<? extends Node>> it = toBeAdded.iterator();
			while (it.hasNext()) {
				IHandlePart<? extends Node> newHandle = it.next();
				boolean existsAlready = false;
				for (IHandlePart<? extends Node> oldHandle : oldHandles) {
					if (oldHandle instanceof Comparable
							&& newHandle.getClass() == oldHandle.getClass()) {
						@SuppressWarnings("unchecked")
						Comparable<IHandlePart<? extends Node>> comparable = (Comparable<IHandlePart<? extends Node>>) oldHandle;
						int compareTo = comparable.compareTo(newHandle);
						if (compareTo == 0) {
							existsAlready = true;
							// refresh already existing handle so that it has
							// the opportunity to adapt its appearance to its
							// host (same index, different role)
							oldHandle.refreshVisual();
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
			removeAnchoreds(targets, toBeDisposed);
			for (IHandlePart<? extends Node> hp : toBeDisposed) {
				hp.dispose();
			}

			// add new handles that did not exist yet
			if (!getHandlesPerTargetSet().containsKey(targetSet)) {
				getHandlesPerTargetSet().put(targetSet,
						new ArrayList<IHandlePart<? extends Node>>());
			} else {
				getHandlesPerTargetSet().put(targetSet, new ArrayList<>(
						getHandlesPerTargetSet().get(targetSet)));
			}
			getHandlesPerTargetSet().get(targetSet).addAll(toBeAdded);
		}
		return replacementHandle;
	}

}