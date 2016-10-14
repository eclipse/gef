/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - skip feedback and handles when determining viewer (bug #498298)
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.editparts.AbstractEditPart and org.eclipse.gef.editparts.AbstractGraphicalEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdaptableScope;
import org.eclipse.gef.common.adapt.inject.InjectAdapters;
import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetProperty;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetWrapper;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.policies.IPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.reflect.TypeToken;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * The {@link AbstractVisualPart} is an abstract implementation of the
 * {@link IVisualPart} interface.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link AbstractVisualPart} is used in, e.g. javafx.scene.Node in
 *            case of JavaFX.
 * @param <V>
 *            The visual node used by this {@link AbstractVisualPart}.
 */
public abstract class AbstractVisualPart<VR, V extends VR>
		implements IVisualPart<VR, V> {

	/**
	 * The 'default' used for attaching/detaching to anchorages, in case no
	 * explicit role is given.
	 */
	private static final String DEFAULT_ANCHORAGE_ROLE = "default";

	private ActivatableSupport acs = new ActivatableSupport(this);
	private AdaptableSupport<IVisualPart<VR, V>> ads = new AdaptableSupport<>(
			this);

	private ReadOnlyObjectWrapper<IVisualPart<VR, ? extends VR>> parentProperty = new ReadOnlyObjectWrapper<>();

	private ObservableList<IVisualPart<VR, ? extends VR>> children = CollectionUtils
			.observableArrayList();
	private ObservableList<IVisualPart<VR, ? extends VR>> childrenUnmodifiable;
	private ReadOnlyListWrapperEx<IVisualPart<VR, ? extends VR>> childrenUnmodifiableProperty;

	private ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = CollectionUtils
			.observableHashMultimap();
	private ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> anchoragesUnmodifiable;
	private ReadOnlySetMultimapWrapper<IVisualPart<VR, ? extends VR>, String> anchoragesUnmodifiableProperty;

	private ObservableMultiset<IVisualPart<VR, ? extends VR>> anchoreds = CollectionUtils
			.observableHashMultiset();
	private ObservableMultiset<IVisualPart<VR, ? extends VR>> anchoredsUnmodifiable;
	private ReadOnlyMultisetWrapper<IVisualPart<VR, ? extends VR>> anchoredsUnmodifiableProperty;

	private BooleanProperty refreshVisualProperty = new SimpleBooleanProperty(
			this, REFRESH_VISUAL_PROPERTY, true);
	private V visual;

	/**
	 * Creates a new {@link AbstractVisualPart} instance, setting the
	 * {@link AdaptableScope} for each of its {@link IAdaptable}-compliant types
	 * (super classes implementing {@link IAdaptable} and super-interfaces
	 * extending {@link IAdaptable}) to the newly created instance (see
	 * AdaptableScopes#scopeTo(IAdaptable)).
	 */
	public AbstractVisualPart() {
	}

	/**
	 * Activates this {@link IVisualPart} (if it is not already active) by
	 * setting (and propagating) the new active state first and delegating to
	 * {@link #doActivate()} afterwards. During the call to
	 * {@link #doActivate()}, {@link #isActive()} will thus already return
	 * <code>true</code>. If the {@link IVisualPart} is already active, this
	 * operation will be a no-op.
	 *
	 * @see #deactivate()
	 * @see #isActive()
	 */
	@Override
	public final void activate() {
		if (!acs.isActive()) {
			// System.out.println("Activate " + this);
			acs.activate();
			activateChildren();
			doActivate();
		}
	}

	/**
	 * Activates the children of this {@link AbstractVisualPart}.
	 */
	protected void activateChildren() {
		for (IVisualPart<VR, ? extends VR> child : children) {
			child.activate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	@Override
	public void addChild(IVisualPart<VR, ? extends VR> child) {
		addChild(child, children.size());
	}

	@Override
	public void addChild(IVisualPart<VR, ? extends VR> child, int index) {
		if (children.contains(child)) {
			throw new IllegalArgumentException("Cannot add " + child
					+ " as child of " + this + " because its already a child.");
		}

		// System.out.println(
		// "Add child " + child + " to " + this + " with index " + index);

		children.add(index, child);
		child.setParent(this);

		refreshVisual();
		doAddChildVisual(child, index);
		child.refreshVisual();

		if (isActive()) {
			child.activate();
		}
	}

	@Override
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children) {
		addChildren(children, this.children.size());
	}

	@Override
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children, int index) {
		if (!Collections.disjoint(this.children, children)) {
			List<? extends IVisualPart<VR, ? extends VR>> alreadyContainedChildren = new ArrayList<>(
					children);
			children.retainAll(this.children);
			throw new IllegalArgumentException(
					"Cannot add " + children + " as children of " + this
							+ " because the following are already children: "
							+ alreadyContainedChildren + ".");
		}
		for (int i = 0; i < children.size(); i++) {
			addChild(children.get(i), index + i);
		}
	}

	/**
	 * Performs the addition of the child's <i>visual</i> to this
	 * {@link IVisualPart}'s visual.
	 *
	 * @param child
	 *            The {@link IVisualPart} being added
	 * @param index
	 *            The child's position
	 * @see #addChild(IVisualPart, int)
	 */
	protected void doAddChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to properly implement addChildVisual(IVisualPart, int) for "
						+ this.getClass());
	}

	@Override
	public ReadOnlySetMultimapProperty<IVisualPart<VR, ? extends VR>, String> anchoragesUnmodifiableProperty() {
		if (anchoragesUnmodifiableProperty == null) {
			anchoragesUnmodifiableProperty = new ReadOnlySetMultimapWrapper<>(
					getAnchoragesUnmodifiable());
		}
		return anchoragesUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMultisetProperty<IVisualPart<VR, ? extends VR>> anchoredsUnmodifiableProperty() {
		if (anchoredsUnmodifiableProperty == null) {
			anchoredsUnmodifiableProperty = new ReadOnlyMultisetWrapper<>(
					getAnchoredsUnmodifiable());
		}
		return anchoredsUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public void attachAnchored(IVisualPart<VR, ? extends VR> anchored) {
		// determine the viewer before adding the anchored
		IViewer<VR> oldViewer = getViewer();

		// register if we obtain a link to the viewer
		HashMultiset<IVisualPart<VR, ? extends VR>> newAnchoreds = HashMultiset
				.create(anchoreds);
		newAnchoreds.add(anchored);
		IViewer<VR> newViewer = determineViewer(getParent(), newAnchoreds);
		if (oldViewer == null && newViewer != null) {
			register(newViewer);
		}

		// attach to the anchoreds (and fire change notifications)
		anchoreds.add(anchored);
	}

	@Override
	public void attachToAnchorage(IVisualPart<VR, ? extends VR> anchorage) {
		attachToAnchorage(anchorage, DEFAULT_ANCHORAGE_ROLE);
	}

	@Override
	public void attachToAnchorage(IVisualPart<VR, ? extends VR> anchorage,
			String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (role == null) {
			throw new IllegalArgumentException("Role may not be null.");
		}

		if (anchorages.containsEntry(anchorage, role)) {
			throw new IllegalArgumentException("Already attached to anchorage "
					+ anchorage + " with role '" + role + "'.");
		}

		// System.out.println("Attach " + this + " to anchorage " + anchorage
		// + " with role " + role);

		// attach
		anchorages.put(anchorage, role);
		anchorage.attachAnchored(this);

		// attach visuals
		anchorage.refreshVisual();
		doAttachToAnchorageVisual(anchorage, role);
		refreshVisual();
	}

	/**
	 * Attaches this part's visual to the visual of the given anchorage.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart}.
	 * @param role
	 *            The anchorage role.
	 */
	protected void doAttachToAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement attachToAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	@Override
	public ReadOnlyListProperty<IVisualPart<VR, ? extends VR>> childrenProperty() {
		if (childrenUnmodifiableProperty == null) {
			childrenUnmodifiableProperty = new ReadOnlyListWrapperEx<>(this,
					CHILDREN_PROPERTY, getChildrenUnmodifiable());
		}
		return childrenUnmodifiableProperty.getReadOnlyProperty();
	}

	/**
	 * Creates this part's visual.
	 *
	 * @return This part's visual.
	 */
	protected abstract V doCreateVisual();

	/**
	 * Deactivates this {@link IVisualPart} (if it is active) by delegating to
	 * {@link #doDeactivate()} first and setting (and propagating) the new
	 * active state afterwards. During the call to {@link #doDeactivate()},
	 * {@link #isActive()} will thus still return <code>true</code>. If the
	 * {@link IVisualPart} is not active, this operation will be a no-op.
	 *
	 * @see #activate()
	 * @see #isActive()
	 */
	@Override
	public final void deactivate() {
		if (acs.isActive()) {
			// System.out.println("Deactivate " + this);
			doDeactivate();
			deactivateChildren();
			acs.deactivate();
		}
	}

	/**
	 * Deactivates the children of this {@link AbstractVisualPart}.
	 */
	protected void deactivateChildren() {
		for (IVisualPart<VR, ? extends VR> child : children) {
			child.deactivate();
		}
	}

	@Override
	public void detachAnchored(IVisualPart<VR, ? extends VR> anchored) {
		// determine viewer before and after removing the anchored
		IViewer<VR> oldViewer = getViewer();
		HashMultiset<IVisualPart<VR, ? extends VR>> oldAnchoreds = HashMultiset
				.create(anchoreds);
		oldAnchoreds.remove(anchored);
		IViewer<VR> newViewer = determineViewer(getParent(), oldAnchoreds);

		// unregister if we lose the link to the viewer
		if (oldViewer != null && newViewer == null) {
			unregister(oldViewer);
		}

		// detach anchoreds (and fire change notifications)
		anchoreds.remove(anchored);
	}

	@Override
	public void detachFromAnchorage(IVisualPart<VR, ? extends VR> anchorage) {
		detachFromAnchorage(anchorage, DEFAULT_ANCHORAGE_ROLE);
	}

	@Override
	public void detachFromAnchorage(IVisualPart<VR, ? extends VR> anchorage,
			String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (role == null) {
			throw new IllegalArgumentException("Role may not be null.");
		}

		if (!anchorages.containsEntry(anchorage, role)) {
			throw new IllegalArgumentException("Not attached to anchorage "
					+ anchorage + " with role '" + role + "'.");
		}

		// System.out.println("Detach " + this + " from anchorage " + anchorage
		// + " with role " + role);

		// detach visuals
		doDetachFromAnchorageVisual(anchorage, role);

		// detach
		anchorage.detachAnchored(this);
		anchorages.remove(anchorage, role);
	}

	/**
	 * Detaches this part's visual from the visual of the given anchorage.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart}.
	 * @param role
	 *            The anchorage role.
	 */
	protected void doDetachFromAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement detachFromAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	/**
	 * Determines the viewer reference via the given parent or any of the given
	 * anchoreds.
	 *
	 * @param parent
	 *            The parent to obtain the viewer from.
	 * @param anchoreds
	 *            The anchoreds to alternatively obtain the viewer from.
	 * @return The viewer, if it could be determined via the parent or any of
	 *         the anchoreds.
	 */
	protected IViewer<VR> determineViewer(IVisualPart<VR, ? extends VR> parent,
			Multiset<IVisualPart<VR, ? extends VR>> anchoreds) {
		IViewer<VR> newViewer = null;
		if (parent != null && parent.getRoot() != null) {
			// the new viewer will be determined via the new
			// parent's root
			newViewer = parent.getRoot().getViewer();
		} else {
			// the new viewer will be determined via the current
			// anchoreds
			for (IVisualPart<VR, ? extends VR> anchored : anchoreds
					.elementSet()) {
				// skip feedback and handles (bug #498298)
				if (anchored instanceof IFeedbackPart
						|| anchored instanceof IHandlePart) {
					continue;
				}
				// determine root via anchored
				IRootPart<VR, ? extends VR> root = anchored.getRoot();
				if (root != null) {
					newViewer = root.getViewer();
					break;
				}
			}
		}
		return newViewer;
	}

	@Override
	public void dispose() {
		// dispose adapters
		ads.dispose();
	}

	/**
	 * Post {@link #activate()} hook. Does nothing by default
	 */
	protected void doActivate() {
		// nothing to do by default
	}

	/**
	 * Pre {@link #deactivate()} hook. Does nothing by default
	 */
	protected void doDeactivate() {
		// nothing to do by default
	}

	/**
	 * Refreshes this part's visualization based on this part's content.
	 *
	 * @param visual
	 *            This part's visual.
	 */
	protected abstract void doRefreshVisual(V visual);

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return ads.getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(TypeToken<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return ads.getAdapterKey(adapter);
	}

	@Override
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return ads.getAdapters();
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return ads.getAdapters(key);
	}

	@Override
	public ObservableSetMultimap<IVisualPart<VR, ? extends VR>, String> getAnchoragesUnmodifiable() {
		if (anchoragesUnmodifiable == null) {
			anchoragesUnmodifiable = CollectionUtils
					.unmodifiableObservableSetMultimap(anchorages);
		}
		return anchoragesUnmodifiable;
	}

	@Override
	public ObservableMultiset<IVisualPart<VR, ? extends VR>> getAnchoredsUnmodifiable() {
		if (anchoredsUnmodifiable == null) {
			anchoredsUnmodifiable = CollectionUtils
					.unmodifiableObservableMultiset(anchoreds);
		}
		return anchoredsUnmodifiable;
	}

	@Override
	public Map<AdapterKey<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors() {
		return ads.getAdapters(IBehavior.class);
	}

	@Override
	public ObservableList<IVisualPart<VR, ? extends VR>> getChildrenUnmodifiable() {
		if (childrenUnmodifiable == null) {
			childrenUnmodifiable = FXCollections
					.unmodifiableObservableList(children);
		}
		return childrenUnmodifiable;
	}

	@Override
	public IVisualPart<VR, ? extends VR> getParent() {
		return parentProperty.get();
	}

	@Override
	public Map<AdapterKey<? extends IPolicy<VR>>, IPolicy<VR>> getPolicies() {
		return ads.getAdapters(IPolicy.class);
	}

	@Override
	public IRootPart<VR, ? extends VR> getRoot() {
		if (getParent() != null) {
			IRootPart<VR, ? extends VR> root = getParent().getRoot();
			if (root != null) {
				return root;
			}
		}
		for (IVisualPart<VR, ? extends VR> anchored : getAnchoredsUnmodifiable()
				.elementSet()) {
			// skip feedback and handles (bug #498298)
			if (anchored instanceof IFeedbackPart
					|| anchored instanceof IHandlePart) {
				continue;
			}
			IRootPart<VR, ? extends VR> root = anchored.getRoot();
			if (root != null) {
				return root;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link IViewer} that contains this part.
	 *
	 * @return The {@link IViewer} that contains this part.
	 */
	protected IViewer<VR> getViewer() {
		IRootPart<VR, ? extends VR> root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getViewer();
	}

	@Override
	public V getVisual() {
		if (visual == null) {
			visual = doCreateVisual();
			IViewer<VR> viewer = getViewer();
			if (viewer != null) {
				registerAtVisualPartMap(viewer, visual);
			}
		}
		return visual;
	}

	/**
	 * @return <code>true</code> if this {@link IVisualPart} is active.
	 */
	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	@Override
	public boolean isRefreshVisual() {
		return refreshVisualProperty.get();
	}

	@Override
	public ReadOnlyObjectProperty<IVisualPart<VR, ? extends VR>> parentProperty() {
		return parentProperty.getReadOnlyProperty();
	}

	/**
	 * Refreshes this {@link IVisualPart}'s <i>visuals</i>. Delegates to
	 * {@link #doRefreshVisual(Object)} in case {@link #isRefreshVisual()} is
	 * not set to <code>false</code>.
	 */
	@Override
	public final void refreshVisual() {
		if (visual != null && isRefreshVisual()) {
			// System.out.println("Refresh visual of " + this);
			doRefreshVisual(visual);
		}
	}

	@Override
	public BooleanProperty refreshVisualProperty() {
		return refreshVisualProperty;
	}

	/**
	 * Called when a link to the {@link IViewer} is obtained. Registers this
	 * {@link IVisualPart} for its "main" visual (i.e. the one returned by
	 * {@link #getVisual()}) at the {@link IViewer#getVisualPartMap()} of the
	 * given {@link IViewer}. To simplify matters, this {@link IVisualPart} only
	 * has to register itself for its "main" visual, i.e. if the "main" visual
	 * contains a number of children visuals, it does not need to register
	 * itself for those children visuals. Therefore, if the visualization
	 * changes dynamically, the registration at the visual-part-map does not
	 * need to be updated. Consequently, when looking up an {@link IVisualPart}
	 * for a given visual in the visual-part-map, it is required to walk up the
	 * visual hierarchy until a registered visual is found.
	 *
	 * @param viewer
	 *            The {@link IViewer} to register at.
	 */
	protected void register(IViewer<VR> viewer) {
		// TODO: Check if the guard (visual != null) really is necessary.
		if (visual != null) {
			registerAtVisualPartMap(viewer, visual);
		}
	}

	/**
	 * Registers this part for the given visual in the visual-part-map of the
	 * given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the visual-part-map is extended.
	 * @param visual
	 *            The visual for which this part is registered in the viewer's
	 *            visual-part-map.
	 */
	protected void registerAtVisualPartMap(IViewer<VR> viewer, V visual) {
		viewer.getVisualPartMap().put(visual, this);
	}

	@Override
	public void removeChild(IVisualPart<VR, ? extends VR> child) {
		if (!children.contains(child)) {
			throw new IllegalArgumentException("Cannot remove " + child
					+ " as child of " + this + " because it is no child.");
		}

		// System.out.println("Remove child " + child + " from " + this + ".");

		if (isActive()) {
			child.deactivate();
		}

		doRemoveChildVisual(child, children.indexOf(child));

		child.setParent(null);
		children.remove(child);
	}

	@Override
	public void removeChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children) {
		if (!this.children.containsAll(children)) {
			List<? extends IVisualPart<VR, ? extends VR>> notContainedChildren = new ArrayList<>(
					children);
			notContainedChildren.removeAll(this.children);
			throw new IllegalArgumentException(
					"Cannot remove " + children + " as children of " + this
							+ " because the following are no children: "
							+ notContainedChildren + ".");
		}
		// TODO: use children.removeAll and perform the de-registration here
		for (IVisualPart<VR, ? extends VR> child : children) {
			removeChild(child);
		}
	}

	/**
	 * Removes the child's visual from this {@link IVisualPart}'s visual.
	 *
	 * @param child
	 *            The child {@link IVisualPart}.
	 * @param index
	 *            The index of the child whose visual is to be removed.
	 */
	protected void doRemoveChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to implement removeChildVisual(IVisualPart, int) for "
						+ this.getClass());
	}

	@Override
	public void reorderChild(IVisualPart<VR, ? extends VR> child, int index) {
		int oldIndex = getChildrenUnmodifiable().indexOf(child);
		if (oldIndex < 0) {
			throw new IllegalArgumentException("Cannot reorder child " + child
					+ " because it is no child.");
		}
		// TODO: this could be made more performant (reordering the children and
		// visuals)
		removeChild(child);
		addChild(child, index);
	}

	@Override
	public <T> void setAdapter(T adapter) {
		ads.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(T adapter, String role) {
		ads.setAdapter(adapter, role);
	}

	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		ads.setAdapter(adapterType, adapter);
	}

	@InjectAdapters
	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		ads.setAdapter(adapterType, adapter, role);
	}

	/**
	 * Sets the parent {@link IVisualPart}.
	 */
	@Override
	public void setParent(IVisualPart<VR, ? extends VR> newParent) {
		IVisualPart<VR, ? extends VR> oldParent = parentProperty.get();
		// ensure there is no action if the parent did not change
		if (oldParent == newParent) {
			return;
		}

		// determine how parent change will affect the viewer reference
		final IViewer<VR> oldViewer = getViewer();
		final IViewer<VR> newViewer = determineViewer(newParent,
				getAnchoredsUnmodifiable());

		// unregister from old viewer in case we were registered (oldViewer !=
		// null) and the viewer changes (newViewer != oldViewer)
		if (oldViewer != null && newViewer != oldViewer) {
			unregister(oldViewer);
		}

		// if we obtain a link to the viewer then register at new viewer
		if (newViewer != null && newViewer != oldViewer) {
			register(newViewer);
		}

		// change the parent property (which will notify listeners)
		parentProperty.set(newParent);
	}

	@Override
	public void setRefreshVisual(boolean isRefreshVisual) {
		refreshVisualProperty.set(isRefreshVisual);
	}

	/**
	 * Called when the link to the {@link IViewer} is lost. Unregisters this
	 * {@link IVisualPart} for its "main" visual (i.e. the one returned by
	 * {@link #getVisual()}) from the {@link IViewer#getVisualPartMap()} of the
	 * given {@link IViewer}. To simplify matters, this {@link IVisualPart} only
	 * has to unregister itself for its "main" visual, i.e. if the "main" visual
	 * contains a number of children visuals, it does not need to unregister
	 * itself for those children visuals. Therefore, if the visualization
	 * changes dynamically, the registration at the visual-part-map does not
	 * need to be updated. Consequently, when looking up an {@link IVisualPart}
	 * for a given visual in the visual-part-map, it is required to walk up the
	 * visual hierarchy until a registered visual is found.
	 *
	 * @param viewer
	 *            The {@link IViewer} to unregister from.
	 */
	protected void unregister(IViewer<VR> viewer) {
		if (visual != null) {
			unregisterFromVisualPartMap(viewer, visual);
		}
	}

	/**
	 * Removes the given visual from the visual-part-map of the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the visual-part-map is changed.
	 * @param visual
	 *            The visual which is removed from the visual-part-map.
	 */
	protected void unregisterFromVisualPartMap(IViewer<VR> viewer, V visual) {
		Map<VR, IVisualPart<VR, ? extends VR>> registry = viewer
				.getVisualPartMap();
		if (registry.get(visual) != this) {
			throw new IllegalArgumentException("Not registered under visual");
		}
		registry.remove(visual);
	}

	@Override
	public <T> void unsetAdapter(T adapter) {
		ads.unsetAdapter(adapter);
	}

}
