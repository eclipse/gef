/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - skip feedback and handles when determining viewer (bug #498298)
 *     Robert Rudi (itemis AG) - introduce addChildren API for bulk changes on content synchronization
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.editparts.AbstractEditPart and org.eclipse.gef.editparts.AbstractGraphicalEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.InjectAdapters;
import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetProperty;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetWrapper;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.fx.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

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
import javafx.scene.Node;

/**
 * The {@link AbstractVisualPart} is an abstract implementation of the
 * {@link IVisualPart} interface.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link AbstractVisualPart}.
 */
public abstract class AbstractVisualPart<V extends Node>
		implements IVisualPart<V> {

	/**
	 * The 'default' used for attaching/detaching to anchorages, in case no
	 * explicit role is given.
	 */
	private static final String DEFAULT_ANCHORAGE_ROLE = "default";

	private ActivatableSupport acs = new ActivatableSupport(this);
	private AdaptableSupport<IVisualPart<V>> ads = new AdaptableSupport<>(this);

	private ReadOnlyObjectWrapper<IVisualPart<? extends Node>> parentProperty = new ReadOnlyObjectWrapper<>();

	private ObservableList<IVisualPart<? extends Node>> children = CollectionUtils
			.observableArrayList();
	private ObservableList<IVisualPart<? extends Node>> childrenUnmodifiable;
	private ReadOnlyListWrapperEx<IVisualPart<? extends Node>> childrenUnmodifiableProperty;

	private ObservableSetMultimap<IVisualPart<? extends Node>, String> anchorages = CollectionUtils
			.observableHashMultimap();
	private ObservableSetMultimap<IVisualPart<? extends Node>, String> anchoragesUnmodifiable;
	private ReadOnlySetMultimapWrapper<IVisualPart<? extends Node>, String> anchoragesUnmodifiableProperty;

	private ObservableMultiset<IVisualPart<? extends Node>> anchoreds = CollectionUtils
			.observableHashMultiset();
	private ObservableMultiset<IVisualPart<? extends Node>> anchoredsUnmodifiable;
	private ReadOnlyMultisetWrapper<IVisualPart<? extends Node>> anchoredsUnmodifiableProperty;

	private BooleanProperty refreshVisualProperty = new SimpleBooleanProperty(
			this, REFRESH_VISUAL_PROPERTY, true);
	private V visual;

	private ReadOnlyObjectWrapper<IViewer> viewerProperty = new ReadOnlyObjectWrapper<>();

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
		acs.activate(null, this::doActivate);
	}

	/**
	 * Activates the adapters registered at this {@link AbstractVisualPart}.
	 */
	protected void activateAdapters() {
		// XXX: We keep a sorted map of adapters so activation
		// is performed in a deterministic order
		new TreeMap<>(ads.getAdapters()).values().forEach((adapter) -> {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).activate();
			}
		});
	}

	/**
	 * Activates the children of this {@link AbstractVisualPart}.
	 */
	protected void activateChildren() {
		for (IVisualPart<? extends Node> child : children) {
			child.activate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IViewer> adaptableProperty() {
		return viewerProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	@Override
	public void addChild(IVisualPart<? extends Node> child) {
		addChild(child, children.size());
	}

	@Override
	public void addChild(IVisualPart<? extends Node> child, int index) {
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
			List<? extends IVisualPart<? extends Node>> children) {
		addChildren(children, this.children.size());
	}

	@Override
	public void addChildren(
			List<? extends IVisualPart<? extends Node>> children, int index) {
		if (!Collections.disjoint(this.children, children)) {
			List<? extends IVisualPart<? extends Node>> alreadyContainedChildren = new ArrayList<>(
					children);
			children.retainAll(this.children);
			throw new IllegalArgumentException(
					"Cannot add " + children + " as children of " + this
							+ " because the following are already children: "
							+ alreadyContainedChildren + ".");
		}
		this.children.addAll(index, children);
		boolean isActive = isActive();
		for (int i = 0; i < children.size(); i++) {
			IVisualPart<? extends Node> child = children.get(i);
			if (child.getParent() != this) {
				child.setParent(this);
			}
			child.refreshVisual();
			doAddChildVisual(child, index + i);
			if (isActive) {
				child.activate();
			}
		}
		refreshVisual();
	}

	@Override
	public ReadOnlySetMultimapProperty<IVisualPart<? extends Node>, String> anchoragesUnmodifiableProperty() {
		if (anchoragesUnmodifiableProperty == null) {
			anchoragesUnmodifiableProperty = new ReadOnlySetMultimapWrapper<>(
					getAnchoragesUnmodifiable());
		}
		return anchoragesUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMultisetProperty<IVisualPart<? extends Node>> anchoredsUnmodifiableProperty() {
		if (anchoredsUnmodifiableProperty == null) {
			anchoredsUnmodifiableProperty = new ReadOnlyMultisetWrapper<>(
					getAnchoredsUnmodifiable());
		}
		return anchoredsUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public void attachAnchored(IVisualPart<? extends Node> anchored) {
		// determine the viewer before adding the anchored
		IViewer oldViewer = getViewer();

		// register if we obtain a link to the viewer
		HashMultiset<IVisualPart<? extends Node>> newAnchoreds = HashMultiset
				.create(anchoreds);
		newAnchoreds.add(anchored);
		IViewer newViewer = determineViewer(getParent(), newAnchoreds);

		// unregister from old viewer in case we were registered (oldViewer !=
		// null) and the viewer changes (newViewer != oldViewer)
		if (oldViewer != null && newViewer != oldViewer) {
			oldViewer.unsetAdapter(this);
		}

		// detach anchoreds (and fire change notifications)
		anchoreds.add(anchored);

		// if we obtain a link to the viewer then register at new viewer
		if (newViewer != null && newViewer != oldViewer) {
			newViewer.setAdapter(this,
					String.valueOf(System.identityHashCode(this)));
		}
	}

	@Override
	public void attachToAnchorage(IVisualPart<? extends Node> anchorage) {
		attachToAnchorage(anchorage, DEFAULT_ANCHORAGE_ROLE);
	}

	@Override
	public void attachToAnchorage(IVisualPart<? extends Node> anchorage,
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

	@Override
	public ReadOnlyListProperty<IVisualPart<? extends Node>> childrenUnmodifiableProperty() {
		if (childrenUnmodifiableProperty == null) {
			childrenUnmodifiableProperty = new ReadOnlyListWrapperEx<>(this,
					CHILDREN_PROPERTY, getChildrenUnmodifiable());
		}
		return childrenUnmodifiableProperty.getReadOnlyProperty();
	}

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
		acs.deactivate(this::doDeactivate, null);
	}

	/**
	 * Deactivates the adapters registered at this {@link AbstractVisualPart}.
	 */
	protected void deactivateAdapters() {
		// XXX: We keep a sorted map of adapters so deactivation
		// is performed in a deterministic order
		new TreeMap<>(ads.getAdapters()).values().forEach((adapter) -> {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).deactivate();
			}
		});
	}

	/**
	 * Deactivates the children of this {@link AbstractVisualPart}.
	 */
	protected void deactivateChildren() {
		for (IVisualPart<? extends Node> child : children) {
			child.deactivate();
		}
	}

	@Override
	public void detachAnchored(IVisualPart<? extends Node> anchored) {
		// determine viewer before and after removing the anchored
		IViewer oldViewer = getViewer();
		HashMultiset<IVisualPart<? extends Node>> oldAnchoreds = HashMultiset
				.create(anchoreds);
		oldAnchoreds.remove(anchored);
		IViewer newViewer = determineViewer(getParent(), oldAnchoreds);

		// unregister from old viewer in case we were registered (oldViewer !=
		// null) and the viewer changes (newViewer != oldViewer)
		if (oldViewer != null && newViewer != oldViewer) {
			oldViewer.unsetAdapter(this);
		}

		// detach anchoreds (and fire change notifications)
		anchoreds.remove(anchored);

		// if we obtain a link to the viewer then register at new viewer
		if (newViewer != null && newViewer != oldViewer) {
			newViewer.setAdapter(this,
					String.valueOf(System.identityHashCode(this)));
		}
	}

	@Override
	public void detachFromAnchorage(IVisualPart<? extends Node> anchorage) {
		detachFromAnchorage(anchorage, DEFAULT_ANCHORAGE_ROLE);
	}

	@Override
	public void detachFromAnchorage(IVisualPart<? extends Node> anchorage,
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
	protected IViewer determineViewer(IVisualPart<? extends Node> parent,
			Multiset<IVisualPart<? extends Node>> anchoreds) {
		if (parent != null && parent.getRoot() != null) {
			return parent.getRoot().getViewer();
		}
		return null;
	}

	@Override
	public void dispose() {
		// dispose adapters
		ads.dispose();
	}

	/**
	 * Activates this {@link AbstractVisualPart}, which activates its children
	 * and adapters.
	 */
	protected void doActivate() {
		activateAdapters();
		activateChildren();
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
	protected void doAddChildVisual(IVisualPart<? extends Node> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to properly implement addChildVisual(IVisualPart, int) for "
						+ this.getClass());
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
			IVisualPart<? extends Node> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement doAttachToAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	/**
	 * Creates this part's visual.
	 *
	 * @return This part's visual.
	 */
	protected abstract V doCreateVisual();

	/**
	 * Deactivates this {@link AbstractVisualPart}, which deactivates its
	 * children and adapters.
	 */
	protected void doDeactivate() {
		deactivateChildren();
		deactivateAdapters();
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
			IVisualPart<? extends Node> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement detachFromAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	/**
	 * Refreshes this part's visualization based on this part's content.
	 *
	 * @param visual
	 *            This part's visual.
	 */
	protected abstract void doRefreshVisual(V visual);

	/**
	 * Removes the child's visual from this {@link IVisualPart}'s visual.
	 *
	 * @param child
	 *            The child {@link IVisualPart}.
	 * @param index
	 *            The index of the child whose visual is to be removed.
	 */
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to implement removeChildVisual(IVisualPart, int) for "
						+ this.getClass());
	}

	@Override
	public IViewer getAdaptable() {
		return viewerProperty.get();
	}

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
	public ObservableSetMultimap<IVisualPart<? extends Node>, String> getAnchoragesUnmodifiable() {
		if (anchoragesUnmodifiable == null) {
			anchoragesUnmodifiable = CollectionUtils
					.unmodifiableObservableSetMultimap(anchorages);
		}
		return anchoragesUnmodifiable;
	}

	@Override
	public ObservableMultiset<IVisualPart<? extends Node>> getAnchoredsUnmodifiable() {
		if (anchoredsUnmodifiable == null) {
			anchoredsUnmodifiable = CollectionUtils
					.unmodifiableObservableMultiset(anchoreds);
		}
		return anchoredsUnmodifiable;
	}

	@Override
	public Map<AdapterKey<? extends IBehavior>, IBehavior> getBehaviors() {
		return ads.getAdapters(IBehavior.class);
	}

	@Override
	public ObservableList<IVisualPart<? extends Node>> getChildrenUnmodifiable() {
		if (childrenUnmodifiable == null) {
			childrenUnmodifiable = FXCollections
					.unmodifiableObservableList(children);
		}
		return childrenUnmodifiable;
	}

	@Override
	public Map<AdapterKey<? extends IHandler>, IHandler> getHandlers() {
		return ads.getAdapters(IHandler.class);
	}

	@Override
	public IVisualPart<? extends Node> getParent() {
		return parentProperty.get();
	}

	@Override
	public Map<AdapterKey<? extends IPolicy>, IPolicy> getPolicies() {
		return ads.getAdapters(IPolicy.class);
	}

	@Override
	public IRootPart<? extends Node> getRoot() {
		// start at first parent as the root part will directly return itself
		IVisualPart<? extends Node> parent = getParent();
		// walk up the part hierarchy until the root part (which has no parent)
		// is found
		while (parent != null && parent.getParent() != null) {
			parent = parent.getParent();
		}
		// check if we really reached the root part
		if (parent instanceof IRootPart) {
			return (IRootPart<? extends Node>) parent;
		}
		// return null if the root part could not be determined
		return null;
	}

	@Override
	public V getVisual() {
		if (visual == null) {
			visual = doCreateVisual();
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
	public ReadOnlyObjectProperty<IVisualPart<? extends Node>> parentProperty() {
		return parentProperty.getReadOnlyProperty();
	}

	/**
	 * Refreshes this {@link IVisualPart}'s <i>visuals</i>. Delegates to
	 * {@link #doRefreshVisual(Node)} in case {@link #isRefreshVisual()} is not
	 * set to <code>false</code>.
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
	protected void register(IViewer viewer) {
		registerAtVisualPartMap(viewer, getVisual());
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
	protected void registerAtVisualPartMap(IViewer viewer, V visual) {
		viewer.getVisualPartMap().put(visual, this);
	}

	@Override
	public void removeChild(IVisualPart<? extends Node> child) {
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
			List<? extends IVisualPart<? extends Node>> children) {
		if (!this.children.containsAll(children)) {
			List<? extends IVisualPart<? extends Node>> notContainedChildren = new ArrayList<>(
					children);
			notContainedChildren.removeAll(this.children);
			throw new IllegalArgumentException(
					"Cannot remove " + children + " as children of " + this
							+ " because the following are no children: "
							+ notContainedChildren + ".");
		}
		// TODO: use children.removeAll and perform the de-registration here
		// boolean active = isActive();
		// children.forEach(child -> {
		// if (active) {
		// child.deactivate();
		// }
		// doRemoveChildVisual(child, this.children.indexOf(child));
		// child.setParent(null);
		// });
		// this.children.removeAll(children);
		for (IVisualPart<? extends Node> child : children) {
			removeChild(child);
		}
	}

	@Override
	public void reorderChild(IVisualPart<? extends Node> child, int index) {
		int oldIndex = getChildrenUnmodifiable().indexOf(child);
		if (oldIndex < 0) {
			throw new IllegalArgumentException("Cannot reorder child " + child
					+ " because it is no child.");
		}
		if (oldIndex != index) {
			removeChild(child);
			addChild(child, index);
		}
	}

	@Override
	public void setAdaptable(IViewer viewer) {
		IViewer oldViewer = viewerProperty.get();
		if (oldViewer != null && viewer != oldViewer) {
			unregister(oldViewer);
		}
		viewerProperty.set(viewer);
		if (viewer != null && viewer != oldViewer) {
			register(viewer);
		}
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
	public void setParent(IVisualPart<? extends Node> newParent) {
		IVisualPart<? extends Node> oldParent = parentProperty.get();
		// ensure there is no action if the parent did not change
		if (oldParent == newParent) {
			return;
		}

		// determine how parent change will affect the viewer reference
		final IViewer oldViewer = getViewer();
		final IViewer newViewer = determineViewer(newParent,
				getAnchoredsUnmodifiable());

		// unregister from old viewer in case we were registered (oldViewer !=
		// null) and the viewer changes (newViewer != oldViewer)
		if (oldViewer != null && newViewer != oldViewer) {
			oldViewer.unsetAdapter(this);
		}

		// change the parent property (which will notify listeners)
		parentProperty.set(newParent);

		// if we obtain a link to the viewer then register at new viewer
		if (newViewer != null && newViewer != oldViewer) {
			newViewer.setAdapter(this,
					String.valueOf(System.identityHashCode(this)));
		}
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
	protected void unregister(IViewer viewer) {
		unregisterFromVisualPartMap(viewer, getVisual());
	}

	/**
	 * Removes the given visual from the visual-part-map of the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the visual-part-map is changed.
	 * @param visual
	 *            The visual which is removed from the visual-part-map.
	 */
	protected void unregisterFromVisualPartMap(IViewer viewer, V visual) {
		Map<Node, IVisualPart<? extends Node>> registry = viewer
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
