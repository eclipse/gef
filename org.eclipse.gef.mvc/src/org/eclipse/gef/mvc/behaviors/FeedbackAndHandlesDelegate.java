/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

/**
 * The {@link FeedbackAndHandlesDelegate} can be used by {@link IBehavior}s to
 * manage their {@link IFeedbackPart}s and {@link IHandlePart}s.
 *
 * <ul>
 * <li>Access feedback: {@link #getFeedbackPerPartMap()},
 * {@link #getFeedbackParts(IVisualPart)}
 * <li>Access handles: {@link #getHandlesPerPartMap()},
 * {@link #getHandleParts(IVisualPart)}
 * <li>Add feedback: {@link #addFeedback(IVisualPart, List, List)},
 * {@link #addFeedback(IVisualPart, List)},
 * {@link #addFeedback(IVisualPart, List, IFeedbackPartFactory)},
 * {@link #addFeedback(IVisualPart, IFeedbackPartFactory)}
 * <li>Add handles: {@link #addHandles(IVisualPart, List, List)},
 * {@link #addHandles(IVisualPart, List)},
 * {@link #addHandles(IVisualPart, List, IHandlePartFactory)},
 * {@link #addHandles(IVisualPart, IHandlePartFactory)}
 * <li>Remove feedback: {@link #removeFeedback(IVisualPart)},
 * {@link #clearFeedback()}
 * <li>Remove handles: {@link #removeHandles(IVisualPart)},
 * {@link #clearHandles()}
 * <li>Update handles (during interaction):
 * {@link #updateHandles(IVisualPart, List, IHandlePartFactory, Comparator, IHandlePart)}
 * <li>Switch adaptable scopes (for custom feedback/handle part creation):
 * {@link #switchAdaptableScopes(IVisualPart)}
 * </ul>
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
// TODO: Merge into AbstractBehavior
public class FeedbackAndHandlesDelegate<VR> {

	private Map<IVisualPart<VR, ? extends VR>, List<IFeedbackPart<VR, ? extends VR>>> feedbackPerPart = new HashMap<>();
	private Map<IVisualPart<VR, ? extends VR>, List<IHandlePart<VR, ? extends VR>>> handlesPerPart = new HashMap<>();
	private IBehavior<VR> behavior;

	// TODO: methods to query if feedback/handle generation was triggered
	// (independent from actually created parts)

	/**
	 * Constructs a new {@link FeedbackAndHandlesDelegate} that can be used to
	 * manage feedback and handles for an {@link IBehavior}, i.e. construction,
	 * destruction, updating of feedback and handles for a host, with specified
	 * targets.
	 *
	 * @param behavior
	 *            The {@link IBehavior} for which to manage feedback and
	 *            handles.
	 */
	public FeedbackAndHandlesDelegate(IBehavior<VR> behavior) {
		this.behavior = behavior;
	}

	/**
	 * Constructs feedback parts for the given host part and adds them to the
	 * list of feedback parts for that host. The host is also used as the single
	 * target part.
	 *
	 * @param hostAndTargetPart
	 *            The host and single target part for the feedback.
	 * @param feedbackPartFactory
	 *            The factory that is used to construct the feedback parts.
	 */
	public void addFeedback(IVisualPart<VR, ? extends VR> hostAndTargetPart,
			IFeedbackPartFactory<VR> feedbackPartFactory) {
		addFeedback(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart),
				feedbackPartFactory);
	}

	/**
	 * Constructs feedback parts for the given host part and adds them to the
	 * list of feedback parts for that host. The given target parts are passed
	 * to the factory and anchored-anchorage relations are established for the
	 * feedback parts and the target parts.
	 *
	 * @param hostPart
	 *            The host part that manages the feedback.
	 * @param targetParts
	 *            The target parts for the feedback.
	 * @param feedbackPartFactory
	 *            The factory that is used to create the feedback parts.
	 */
	public void addFeedback(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts,
			IFeedbackPartFactory<VR> feedbackPartFactory) {
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (feedbackPartFactory != null) {
			switchAdaptableScopes(hostPart);
			List<IFeedbackPart<VR, ? extends VR>> feedbackParts = feedbackPartFactory
					.createFeedbackParts(targetParts, behavior,
							Collections.emptyMap());
			addFeedback(hostPart, targetParts, feedbackParts);
		}
	}

	/**
	 * Adds the given feedback parts to the list of feedback parts for the given
	 * host part. Anchored-anchorage relations are established for the feedback
	 * parts and the given target parts.
	 *
	 * @param hostPart
	 *            The host part that manages the feedback.
	 * @param targetParts
	 *            The target parts for the feedback.
	 * @param feedbackParts
	 *            The feedback parts that are added.
	 */
	public void addFeedback(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts,
			List<IFeedbackPart<VR, ? extends VR>> feedbackParts) {
		System.out.println("ADD feedback BY " + getCaller() + " FOR owner="
				+ hostPart + ", targets=" + targetParts);
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (feedbackParts != null && !feedbackParts.isEmpty()) {
			if (feedbackPerPart.containsKey(hostPart)) {
				throw new IllegalStateException("Cannot add feedback for <"
						+ hostPart
						+ "> because feedback was already created for that part.");
			}
			feedbackPerPart.put(hostPart, feedbackParts);
			BehaviorUtils.<VR> addAnchoreds(hostPart.getRoot(), targetParts,
					feedbackParts);
			System.out.println(" -> Added " + feedbackParts.size() + " parts.");
		} else {
			System.out.println(" -> Nothing to do.");
		}
	}

	/**
	 * Adds the given feedback parts to the list of feedback that is managed by
	 * the given host part. The host part is also used as the single target for
	 * the feedback parts.
	 *
	 * @param hostAndTargetPart
	 *            The host and single target part for the feedback.
	 * @param feedbackParts
	 *            The feedback parts that are added to the host.
	 */
	public void addFeedback(IVisualPart<VR, ? extends VR> hostAndTargetPart,
			List<IFeedbackPart<VR, ? extends VR>> feedbackParts) {
		addFeedback(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart), feedbackParts);
	}

	/**
	 * Constructs handle parts for the given host part and adds them to the list
	 * of handle parts for that host. The host is also used as the single target
	 * part for the handles.
	 *
	 * @param hostAndTargetPart
	 *            The host and single target part for the handles.
	 * @param handlePartFactory
	 *            The factory that is used to construct the handle parts.
	 */
	public void addHandles(IVisualPart<VR, ? extends VR> hostAndTargetPart,
			IHandlePartFactory<VR> handlePartFactory) {
		addHandles(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart),
				handlePartFactory);
	}

	/**
	 * Constructs handle parts for the given host part and adds them to the list
	 * of handle parts for that host. Establishes anchored-anchorage relations
	 * between the given target parts and the handle parts.
	 *
	 * @param hostPart
	 *            The host part for the handles.
	 * @param targetParts
	 *            The target parts for the handles.
	 * @param handlePartFactory
	 *            The factory that creates the handles.
	 */
	public void addHandles(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts,
			IHandlePartFactory<VR> handlePartFactory) {
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (handlePartFactory != null) {
			switchAdaptableScopes(hostPart);
			List<IHandlePart<VR, ? extends VR>> handleParts = handlePartFactory
					.createHandleParts(targetParts, behavior,
							Collections.emptyMap());
			addHandles(hostPart, targetParts, handleParts);
		}
	}

	/**
	 * Adds the given handle parts to the given host part. Anchored-anchorage
	 * relations are established for the given target parts and the added handle
	 * parts.
	 *
	 * @param hostPart
	 *            The host part for the handles.
	 * @param targetParts
	 *            The target parts for the handles.
	 * @param handleParts
	 *            The handles.
	 */
	public void addHandles(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts,
			List<IHandlePart<VR, ? extends VR>> handleParts) {
		System.out.println("ADD handles BY " + getCaller() + " FOR owner="
				+ hostPart + ", targets=" + targetParts);
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (handleParts != null && !handleParts.isEmpty()) {
			if (handlesPerPart.containsKey(hostPart)) {
				throw new IllegalStateException("Cannot add handles for <"
						+ hostPart
						+ "> because handles were already created for that part.");
			}
			handlesPerPart.put(hostPart, handleParts);
			BehaviorUtils.<VR> addAnchoreds(hostPart.getRoot(), targetParts,
					handleParts);
			System.out.println(" -> Added " + handleParts.size() + " parts.");
		} else {
			System.out.println(" -> Nothing to do.");
		}
	}

	/**
	 * Adds the given handles to the given host part. The host part also serves
	 * as the single target part for the handles.
	 *
	 * @param hostAndTargetPart
	 *            The host and single target part for the handles.
	 * @param handleParts
	 *            The handle parts.
	 */
	public void addHandles(IVisualPart<VR, ? extends VR> hostAndTargetPart,
			List<IHandlePart<VR, ? extends VR>> handleParts) {
		addHandles(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart), handleParts);
	}

	/**
	 * Removes all feedback.
	 */
	public void clearFeedback() {
		for (IVisualPart<VR, ? extends VR> hostPart : new ArrayList<>(
				feedbackPerPart.keySet())) {
			removeFeedback(hostPart);
		}
	}

	/**
	 * Removes all handles.
	 */
	public void clearHandles() {
		for (IVisualPart<VR, ? extends VR> hostPart : new ArrayList<>(
				handlesPerPart.keySet())) {
			removeHandles(hostPart);
		}
	}

	private String getCaller() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		// find first stack trace element that is from another class
		for (int i = 0; i < stackTrace.length; i++) {
			String className = stackTrace[i].getClassName();
			if (!className.contains("FeedbackAndHandlesDelegate")
					&& className.contains(".gef.")) {
				return className;
			}
		}
		return "unknown";
	}

	/**
	 * Determines and returns the feedback parts for the given host part.
	 *
	 * @param hostPart
	 *            The host part for which to determine the feedback parts.
	 * @return The feedback parts for the given host part.
	 */
	public List<IFeedbackPart<VR, ? extends VR>> getFeedbackParts(
			IVisualPart<VR, ? extends VR> hostPart) {
		return feedbackPerPart.containsKey(hostPart)
				? feedbackPerPart.get(hostPart)
				: Collections.<IFeedbackPart<VR, ? extends VR>> emptyList();
	}

	/**
	 * Returns the feedback-per-part-map that stores the feedback parts for all
	 * hosts.
	 *
	 * @return The feedback-per-part-map that stores the feedback parts for all
	 *         hosts.
	 */
	public Map<IVisualPart<VR, ? extends VR>, List<IFeedbackPart<VR, ? extends VR>>> getFeedbackPerPartMap() {
		return feedbackPerPart;
	}

	/**
	 * Returns all handle parts for the given host part.
	 *
	 * @param hostPart
	 *            The host part for which to return the handle parts.
	 * @return The handle parts for the given host part.
	 */
	public List<IHandlePart<VR, ? extends VR>> getHandleParts(
			IVisualPart<VR, ? extends VR> hostPart) {
		return handlesPerPart.containsKey(hostPart)
				? handlesPerPart.get(hostPart)
				: Collections.<IHandlePart<VR, ? extends VR>> emptyList();
	}

	/**
	 * Returns the handles-per-part-map that stores the handle parts for all
	 * host parts.
	 *
	 * @return The handles-per-part-map that stores the handle parts for all
	 *         host parts.
	 */
	public Map<IVisualPart<VR, ? extends VR>, List<IHandlePart<VR, ? extends VR>>> getHandlesPerPartMap() {
		return handlesPerPart;
	}

	/**
	 * Removes all feedback for the given host part that also serves as the
	 * single target part.
	 *
	 * @param hostAndTargetPart
	 *            The host and single target part for which to remove feedback.
	 */
	public void removeFeedback(
			IVisualPart<VR, ? extends VR> hostAndTargetPart) {
		removeFeedback(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart));
	}

	/**
	 * Removes feedback for the given host part and the given target parts.
	 *
	 * @param hostPart
	 *            The host part.
	 * @param targetParts
	 *            The target parts.
	 */
	public void removeFeedback(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts) {
		System.out.println("REM feedback BY " + getCaller());
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (feedbackPerPart.containsKey(hostPart)) {
			List<IFeedbackPart<VR, ? extends VR>> feedbackParts = feedbackPerPart
					.remove(hostPart);
			BehaviorUtils.removeAnchoreds(hostPart.getRoot(), targetParts,
					feedbackParts);
			// XXX: Fix for bug #496227
			for (IFeedbackPart<VR, ? extends VR> fp : feedbackParts) {
				fp.dispose();
			}
			System.out
					.println(" -> Removed " + feedbackParts.size() + " parts.");
		} else {
			System.out.println(" -> Nothing to do.");
		}
	}

	/**
	 * Removes handles for the given host part that does also serve as the
	 * single target part.
	 *
	 * @param hostAndTargetPart
	 *            The host part and single target part.
	 */
	public void removeHandles(IVisualPart<VR, ? extends VR> hostAndTargetPart) {
		removeHandles(hostAndTargetPart,
				Collections.singletonList(hostAndTargetPart));
	}

	/**
	 * Removes handles for the given host part and the given target parts.
	 *
	 * @param hostPart
	 *            The host part.
	 * @param targetParts
	 *            The target parts.
	 */
	public void removeHandles(IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts) {
		System.out.println("REM handles BY " + getCaller());
		if (hostPart == null) {
			throw new IllegalArgumentException(
					"The given host part may not be null.");
		}
		if (handlesPerPart.containsKey(hostPart)) {
			List<IHandlePart<VR, ? extends VR>> handleParts = handlesPerPart
					.remove(hostPart);
			BehaviorUtils.removeAnchoreds(hostPart.getRoot(), targetParts,
					handleParts);
			// XXX: Fix for bug #496227
			for (IHandlePart<VR, ? extends VR> hp : handleParts) {
				hp.dispose();
			}
			System.out.println(" -> Removed " + handleParts.size() + " parts.");
		} else {
			System.out.println(" -> Nothing to do.");
		}
	}

	/**
	 * Switches the adaptable scopes for part creation for the given host part,
	 * i.e. switches to the host's domain, viewer, and the host itself, in that
	 * order.
	 *
	 * @param hostPart
	 *            The host part that specifies the scope for part creation.
	 */
	public void switchAdaptableScopes(IVisualPart<VR, ? extends VR> hostPart) {
		IViewer<VR> viewer = hostPart.getRoot().getViewer();
		IDomain<VR> domain = viewer.getDomain();
		AdaptableScopes.switchTo(domain);
		AdaptableScopes.switchTo(viewer);
		AdaptableScopes.switchTo(hostPart);
	}

	/**
	 * Updates the handles of the given <i>hostAndTargetPart</i>. Returns a new
	 * {@link IHandlePart} that would be replacing the given
	 * <i>interactedWith</i> handle part if that part was not preserved (which
	 * it is). The user can then apply the information of the replacement part
	 * to the preserved <i>interactedWith</i> part.
	 *
	 * @param hostPart
	 *            The host and target part for which to update handles.
	 * @param targetParts
	 *            The target parts for the handles.
	 * @param handlePartFactory
	 *            The {@link IHandlePartFactory} that is used to create new
	 *            handle parts.
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
			IVisualPart<VR, ? extends VR> hostPart,
			List<? extends IVisualPart<VR, ? extends VR>> targetParts,
			IHandlePartFactory<VR> handlePartFactory,
			Comparator<IHandlePart<VR, ? extends VR>> interactedWithComparator,
			IHandlePart<VR, ? extends VR> interactedWith) {
		// determine new handles
		switchAdaptableScopes(hostPart);
		List<IHandlePart<VR, ? extends VR>> newHandles = handlePartFactory
				.createHandleParts(targetParts, behavior,
						Collections.emptyMap());

		// compare to current handles => remove/add as needed
		IHandlePart<VR, ? extends VR> replacementHandle = null;
		if (newHandles != null && !newHandles.isEmpty()) {
			// set new handles as anchoreds so that they can be compared
			List<IHandlePart<VR, ? extends VR>> toBeAdded = new ArrayList<>(
					newHandles);
			BehaviorUtils.<VR> addAnchoreds(hostPart.getRoot(), targetParts,
					toBeAdded);

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
			List<IHandlePart<VR, ? extends VR>> currentHandleParts = getHandleParts(
					hostPart);

			if (!currentHandleParts.isEmpty()) {
				oldHandles = new ArrayList<>(currentHandleParts);
				Iterator<IHandlePart<VR, ? extends VR>> it = oldHandles
						.iterator();
				while (it.hasNext()) {
					IHandlePart<VR, ? extends VR> oldHandle = it.next();
					ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = oldHandle
							.getAnchoragesUnmodifiable();
					if (!anchorages.keySet().contains(hostPart)) {
						it.remove();
					}
				}

				if (interactedWith != null) {
					// remove interacted with handle from old handles so that it
					// is preserved
					oldHandles.remove(interactedWith);
				}

				// find handles that no longer exist
				List<IHandlePart<VR, ? extends VR>> toBeRemoved = new ArrayList<>();
				it = oldHandles.iterator();
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
				BehaviorUtils.removeAnchoreds(hostPart.getRoot(), targetParts,
						toBeRemoved);
				getHandleParts(hostPart).removeAll(toBeRemoved);
				// XXX: Fix for bug #496227
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
			BehaviorUtils.removeAnchoreds(hostPart.getRoot(), targetParts,
					toBeDisposed);
			// XXX: Fix for bug #496227
			for (IHandlePart<VR, ? extends VR> hp : toBeDisposed) {
				hp.dispose();
			}

			// add new handles that did not exist yet
			if (!getHandlesPerPartMap().containsKey(hostPart)) {
				getHandlesPerPartMap().put(hostPart,
						new ArrayList<IHandlePart<VR, ? extends VR>>());
			}
			getHandleParts(hostPart).addAll(toBeAdded);
		}

		return replacementHandle;
	}

}
