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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.editparts.AbstractEditPart and org.eclipse.gef.editparts.AbstractGraphicalEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdaptableScope;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.InjectAdapters;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

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

	/**
	 * A {@link PropertyChangeSupport} that is used as a delegate to notify
	 * listeners about changes to this object. May be used by subclasses to
	 * trigger the notification of listeners.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private ActivatableSupport acs = new ActivatableSupport(this, pcs);
	private AdaptableSupport<IVisualPart<VR, V>> ads = new AdaptableSupport<IVisualPart<VR, V>>(
			this, pcs);

	private IVisualPart<VR, ? extends VR> parent;
	private List<IVisualPart<VR, ? extends VR>> children;

	private Multiset<IVisualPart<VR, ? extends VR>> anchoreds;
	private SetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages;

	private boolean refreshVisual = true;
	private V visual;

	/**
	 * Creates a new {@link AbstractVisualPart} instance, setting the
	 * {@link AdaptableScope} for each of its {@link IAdaptable}-compliant types
	 * (super classes implementing {@link IAdaptable} and super-interfaces
	 * extending {@link IAdaptable}) to the newly created instance (see
	 * AdaptableScopes#scopeTo(IAdaptable)).
	 */
	public AbstractVisualPart() {
		// enter adaptables scope
		AdaptableScopes.enter(this);
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
			acs.activate();
			doActivate();
		}
	}

	@Override
	public void addChild(IVisualPart<VR, ? extends VR> child) {
		addChild(child, getChildren().size());
	}

	@Override
	public void addChild(IVisualPart<VR, ? extends VR> child, int index) {
		if (getChildren().indexOf(child) >= 0) {
			throw new IllegalArgumentException(
					"Cannot add " + child + " because its already a child.");
		}

		// store old children list (for notifying property change listeners)
		List<IVisualPart<VR, ? extends VR>> oldChildren = new ArrayList<>(
				getChildren());
		addChildWithoutNotify(child, index);

		child.setParent(this);

		refreshVisual();
		addChildVisual(child, index);
		child.refreshVisual();

		if (isActive()) {
			child.activate();
		}

		pcs.firePropertyChange(CHILDREN_PROPERTY, oldChildren, getChildren());
	}

	@Override
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children) {
		for (IVisualPart<VR, ? extends VR> child : children) {
			addChild(child);
		}
	}

	@Override
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children, int index) {
		for (int i = children.size() - 1; i >= 0; i--) {
			addChild(children.get(i), index);
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
	protected void addChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to properly implement addChildVisual(IVisualPart, int) for "
						+ this.getClass());
	}

	private void addChildWithoutNotify(IVisualPart<VR, ? extends VR> child,
			int index) {
		if (children == null) {
			children = new ArrayList<>(2);
		}
		children.add(index, child);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void attachAnchored(IVisualPart<VR, ? extends VR> anchored) {
		// copy anchoreds (required for the change notification)
		Multiset<IVisualPart<VR, ? extends VR>> oldAnchoreds = anchoreds == null
				? HashMultiset.<IVisualPart<VR, ? extends VR>> create()
				: HashMultiset.create(anchoreds);

		// determine the viewer before adding the anchored
		IViewer<VR> oldViewer = getViewer();

		if (anchoreds == null) {
			anchoreds = HashMultiset.create();
		}
		anchoreds.add(anchored);

		// register if we obtain a link to the viewer
		IViewer<VR> newViewer = getViewer();
		if (oldViewer == null && newViewer != null) {
			register(newViewer);
		}

		pcs.firePropertyChange(ANCHOREDS_PROPERTY, oldAnchoreds,
				getAnchoreds());
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

		// copy anchorages by role (required for the change notification)
		SetMultimap<IVisualPart<VR, ? extends VR>, String> oldAnchorages = anchorages == null
				? HashMultimap.<IVisualPart<VR, ? extends VR>, String> create()
				: HashMultimap.create(anchorages);

		if (oldAnchorages.containsEntry(anchorage, role)) {
			throw new IllegalArgumentException("Already attached to anchorage "
					+ anchorage + " with role '" + role + "'.");
		}

		attachToAnchorageWithoutNotify(anchorage, role);
		anchorage.attachAnchored(this);

		anchorage.refreshVisual();
		attachToAnchorageVisual(anchorage, role);
		refreshVisual();

		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	/**
	 * Attaches this part's visual to the visual of the given anchorage.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart}.
	 * @param role
	 *            The anchorage role.
	 */
	protected void attachToAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement attachToAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	private void attachToAnchorageWithoutNotify(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (anchorages == null) {
			anchorages = HashMultimap.create();
		}
		anchorages.put(anchorage, role);
	}

	/**
	 * Creates this part's visual.
	 *
	 * @return This part's visual.
	 */
	protected abstract V createVisual();

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
			doDeactivate();
			acs.deactivate();
		}
	}

	@Override
	public void detachAnchored(IVisualPart<VR, ? extends VR> anchored) {
		// copy anchoreds (required for the change notification)
		Multiset<IVisualPart<VR, ? extends VR>> oldAnchoreds = anchoreds == null
				? HashMultiset.<IVisualPart<VR, ? extends VR>> create()
				: HashMultiset.create(anchoreds);

		// determine viewer before and after removing the anchored
		IViewer<VR> oldViewer = getViewer();
		anchoreds.remove(anchored);
		IViewer<VR> newViewer = getViewer();
		anchoreds.add(anchored);

		// unregister if we lose the link to the viewer
		if (oldViewer != null && newViewer == null) {
			unregister(oldViewer);
		}

		anchoreds.remove(anchored);
		if (anchoreds.size() == 0) {
			anchoreds = null;
		}

		pcs.firePropertyChange(ANCHOREDS_PROPERTY, oldAnchoreds,
				getAnchoreds());
	}

	@Override
	public void detachFromAnchorage(IVisualPart<VR, ? extends VR> anchorage) {
		detachFromAnchorage(anchorage, DEFAULT_ANCHORAGE_ROLE);
	}

	// counterpart to setParent(null) in case of hierarchy
	@Override
	public void detachFromAnchorage(IVisualPart<VR, ? extends VR> anchorage,
			String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (role == null) {
			throw new IllegalArgumentException("Role may not be null.");
		}

		// copy anchorages (required for the change notification)
		SetMultimap<IVisualPart<VR, ? extends VR>, String> oldAnchorages = anchorages == null
				? HashMultimap.<IVisualPart<VR, ? extends VR>, String> create()
				: HashMultimap.create(anchorages);

		if (!oldAnchorages.containsEntry(anchorage, role)) {
			throw new IllegalArgumentException("Not attached to anchorage "
					+ anchorage + " with role '" + role + "'.");
		}

		detachFromAnchorageWithoutNotify(anchorage, role);

		anchorage.detachAnchored(this);
		detachFromAnchorageVisual(anchorage, role);

		// TODO: send MapChangeNotification or otherwise identify changed
		// anchorage and role
		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	/**
	 * Detaches this part's visual from the visual of the given anchorage.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart}.
	 * @param role
	 *            The anchorage role.
	 */
	protected void detachFromAnchorageVisual(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		throw new UnsupportedOperationException(
				"Need to implement detachFromAnchorageVisual(IVisualPart, String) for "
						+ this.getClass());
	}

	private void detachFromAnchorageWithoutNotify(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		if (anchorages == null) {
			throw new IllegalStateException(
					"Cannot detach from anchorage: not attached.");
		}
		if (!anchorages.remove(anchorage, role)) {
			throw new IllegalStateException(
					"Cannot detach from anchorage: not attached.");
		}
		if (anchorages.isEmpty()) {
			anchorages = null;
		}
	}

	@Override
	public void dispose() {
		// leave adaptables scope
		AdaptableScopes.leave(this);

		// dispose adapters
		ads.dispose();
	}

	/**
	 * Post {@link #activate()} hook that activates this part's children.
	 */
	protected void doActivate() {
		// TODO: rather do this via property changes (so a child becomes active
		// when its parent and anchorages are active??
		for (IVisualPart<VR, ? extends VR> child : getChildren()) {
			child.activate();
		}
	}

	/**
	 * Pre {@link #deactivate()} hook that deactivates this part's children.
	 */
	protected void doDeactivate() {
		for (IVisualPart<VR, ? extends VR> child : getChildren()) {
			child.deactivate();
		}
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
	public SetMultimap<IVisualPart<VR, ? extends VR>, String> getAnchorages() {
		if (anchorages == null) {
			return Multimaps.unmodifiableSetMultimap(HashMultimap
					.<IVisualPart<VR, ? extends VR>, String> create());
		}
		return Multimaps.unmodifiableSetMultimap(anchorages);
	}

	@Override
	public Multiset<IVisualPart<VR, ? extends VR>> getAnchoreds() {
		if (anchoreds == null) {
			return Multisets
					.<IVisualPart<VR, ? extends VR>> unmodifiableMultiset(
							HashMultiset
									.<IVisualPart<VR, ? extends VR>> create());
		}
		return Multisets.unmodifiableMultiset(anchoreds);
	}

	@Override
	public Map<AdapterKey<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors() {
		return ads.getAdapters(IBehavior.class);
	}

	@Override
	public List<IVisualPart<VR, ? extends VR>> getChildren() {
		if (children == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(children);
	}

	@Override
	public IVisualPart<VR, ? extends VR> getParent() {
		return parent;
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
		for (IVisualPart<VR, ? extends VR> anchored : getAnchoreds()
				.elementSet()) {
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
			visual = createVisual();
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
		return refreshVisual;
	}

	/**
	 * Refreshes this {@link IVisualPart}'s <i>visuals</i>. Delegates to
	 * {@link #doRefreshVisual(Object)} in case {@link #isRefreshVisual()} is
	 * not set to <code>false</code>.
	 */
	@Override
	public final void refreshVisual() {
		if (visual != null && isRefreshVisual()) {
			doRefreshVisual(visual);
		}
	}

	/**
	 * Called when a link to the Viewer is obtained.
	 *
	 * @param viewer
	 *            The viewer to register at.
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
		if (getChildren().indexOf(child) < 0) {
			throw new IllegalArgumentException(
					"Cannot remove " + child + " because its not a child.");
		}
		// store old children list (for notifying property change listeners)
		List<IVisualPart<VR, ? extends VR>> oldChildren = new ArrayList<>(
				getChildren());

		if (isActive()) {
			child.deactivate();
		}
		removeChildVisual(child, getChildren().indexOf(child));
		child.setParent(null);
		removeChildWithoutNotify(child);

		pcs.firePropertyChange(CHILDREN_PROPERTY, oldChildren, getChildren());
	}

	@Override
	public void removeChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children) {
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
	protected void removeChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"Need to implement removeChildVisual(IVisualPart, int) for "
						+ this.getClass());
	}

	private void removeChildWithoutNotify(IVisualPart<VR, ? extends VR> child) {
		children.remove(child);
		if (children.size() == 0) {
			children = null;
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void reorderChild(IVisualPart<VR, ? extends VR> child, int index) {
		List<IVisualPart<VR, ? extends VR>> oldChildren = new ArrayList<>(
				getChildren());
		removeChildVisual(child, children.indexOf(child));
		removeChildWithoutNotify(child);
		addChildWithoutNotify(child, index);
		addChildVisual(child, index);
		pcs.firePropertyChange(CHILDREN_PROPERTY, oldChildren, getChildren());
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
		if (this.parent == newParent) {
			return;
		}

		// save old parent for the change notification
		IVisualPart<VR, ? extends VR> oldParent = this.parent;

		// determine viewer before and after setting the parent
		IViewer<VR> oldViewer = getViewer();
		this.parent = newParent;
		IViewer<VR> newViewer = getViewer();
		this.parent = oldParent;

		// unregister if we were registered (oldViewer != null) and the viewer
		// changes (newViewer != oldViewer)
		if (oldViewer != null && newViewer != oldViewer) {
			unregister(oldViewer);
		}

		this.parent = newParent;

		// if we obtain a link to the viewer then register visuals
		if (newViewer != null && newViewer != oldViewer) {
			register(newViewer);
		}

		pcs.firePropertyChange(PARENT_PROPERTY, oldParent, newParent);
	}

	@Override
	public void setRefreshVisual(boolean isRefreshVisual) {
		this.refreshVisual = isRefreshVisual;
	}

	/**
	 * Called when the link to the Viewer is lost.
	 *
	 * @param viewer
	 *            The viewer to unregister from.
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
