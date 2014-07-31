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
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractVisualPart<VR> implements IVisualPart<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private AdaptableSupport<IVisualPart<VR>> ads = new AdaptableSupport<IVisualPart<VR>>(
			this, pcs);

	private ActivatableSupport<IVisualPart<VR>> acs = new ActivatableSupport<IVisualPart<VR>>(
			this, pcs);

	private IVisualPart<VR> parent;
	private List<IVisualPart<VR>> children;

	private List<IVisualPart<VR>> anchoreds;
	private SetMultimap<IVisualPart<VR>, String> anchorages;

	private boolean refreshVisual = true;

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
	public void activate() {
		if (!acs.isActive()) {
			acs.activate();
			doActivate();
		}
	}

	@Override
	public void addAnchorage(IVisualPart<VR> anchorage) {
		addAnchorage(anchorage, null);
	}

	@Override
	public void addAnchorage(IVisualPart<VR> anchorage, String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}

		// copy anchorages by role (required for the change notification)
		SetMultimap<IVisualPart<VR>, String> oldAnchorages = anchorages == null ? HashMultimap
				.<IVisualPart<VR>, String> create() : HashMultimap
				.create(anchorages);

		addAnchorageWithoutNotify(anchorage, role);
		anchorage.addAnchored(this);

		anchorage.refreshVisual();
		attachToAnchorageVisual(anchorage, role);
		refreshVisual();

		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	private void addAnchorageWithoutNotify(IVisualPart<VR> anchorage,
			String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (anchorages == null) {
			anchorages = HashMultimap.create();
		}
		anchorages.put(anchorage, role);
	}

	@Override
	public void addAnchored(IVisualPart<VR> anchored) {
		if (anchoreds == null) {
			anchoreds = new ArrayList<IVisualPart<VR>>();
		}
		anchoreds.add(anchored);

		// if we obtain a link to the viewer (via anchored) then register
		// visuals
		if (parent == null && anchoreds.size() == 1) {
			register();
		}
	}

	@Override
	public void addChild(IVisualPart<VR> child) {
		addChild(child, getChildren().size());
	}

	@Override
	public void addChild(IVisualPart<VR> child, int index) {
		List<IVisualPart<VR>> oldChildren = getChildren();
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
	public void addChildren(List<? extends IVisualPart<VR>> children) {
		for (IVisualPart<VR> child : children) {
			addChild(child);
		}
	}

	@Override
	public void addChildren(List<? extends IVisualPart<VR>> children, int index) {
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
	protected abstract void addChildVisual(IVisualPart<VR> child, int index);

	private void addChildWithoutNotify(IVisualPart<VR> child, int index) {
		if (children == null) {
			children = new ArrayList<IVisualPart<VR>>(2);
		}
		children.add(index, child);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	protected abstract void attachToAnchorageVisual(IVisualPart<VR> anchorage,
			String role);

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
	public void deactivate() {
		if (acs.isActive()) {
			doDeactivate();
			acs.deactivate();
		}
	}

	protected abstract void detachFromAnchorageVisual(
			IVisualPart<VR> anchorage, String role);

	protected void doActivate() {
		// TODO: rather do this via property changes (so a child becomes active
		// when its parent and anchorages are active??
		for (IVisualPart<VR> child : getChildren()) {
			child.activate();
		}
	}

	protected void doDeactivate() {
		for (IVisualPart<VR> child : getChildren()) {
			child.deactivate();
		}
	}

	protected abstract void doRefreshVisual();

	@Override
	public <T> T getAdapter(AdapterKey<? super T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<? super T> classKey) {
		return ads.getAdapter(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public SetMultimap<IVisualPart<VR>, String> getAnchorages() {
		if (anchorages == null) {
			return Multimaps.unmodifiableSetMultimap(HashMultimap
					.<IVisualPart<VR>, String> create());
		}
		return Multimaps.unmodifiableSetMultimap(anchorages);
	}

	@Override
	public List<IVisualPart<VR>> getAnchoreds() {
		if (anchoreds == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	@Override
	public Map<AdapterKey<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors() {
		return ads.getAdapters(IBehavior.class);
	}

	@Override
	public List<IVisualPart<VR>> getChildren() {
		if (children == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(children);
	}

	@Override
	public IVisualPart<VR> getParent() {
		return parent;
	}

	@Override
	public Map<AdapterKey<? extends IPolicy<VR>>, IPolicy<VR>> getPolicies() {
		return ads.getAdapters(IPolicy.class);
	}

	@Override
	public IRootPart<VR> getRoot() {
		if (getParent() != null) {
			return getParent().getRoot();
		}
		if (getAnchoreds().size() > 0) {
			return getAnchoreds().get(0).getRoot();
		}
		return null;
	}

	protected IViewer<VR> getViewer() {
		IRootPart<VR> root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getViewer();
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
	 * {@link #doRefreshVisual()} in case {@link #isRefreshVisual()} is not set
	 * to <code>false</code>.
	 */
	@Override
	public final void refreshVisual() {
		if (isRefreshVisual()) {
			// TODO: delegate to visual behavior
			doRefreshVisual();
		}
	}

	/**
	 * Called when a link to the Viewer is obtained.
	 */
	protected void register() {
		registerAtVisualPartMap();
	}

	protected void registerAtVisualPartMap() {
		getViewer().getVisualPartMap().put(getVisual(), this);
	}

	@Override
	public void removeAnchorage(IVisualPart<VR> anchorage) {
		removeAnchorage(anchorage, null);
	}

	// counterpart to setParent(null) in case of hierarchy
	@Override
	public void removeAnchorage(IVisualPart<VR> anchorage, String role) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}

		if (anchorages == null || !anchorages.containsEntry(anchorage, role)) {
			throw new IllegalArgumentException(
					"Anchorage has to be contained under the specified role ("
							+ role + ").");
		}

		// copy anchorages (required for the change notification)
		SetMultimap<IVisualPart<VR>, String> oldAnchorages = anchorages == null ? HashMultimap
				.<IVisualPart<VR>, String> create() : HashMultimap
				.create(anchorages);

		removeAnchorageWithoutNotify(anchorage, role);

		anchorage.removeAnchored(this);
		detachFromAnchorageVisual(anchorage, role);

		// TODO: send MapChangeNotification or otherwise identify changed
		// anchorage and role
		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	private void removeAnchorageWithoutNotify(IVisualPart<VR> anchorage,
			String role) {
		if (anchorages == null) {
			throw new IllegalStateException(
					"Cannot remove anchorage: not contained.");
		}
		if (!anchorages.remove(anchorage, role)) {
			throw new IllegalStateException(
					"Cannot remove anchorage: not contained.");
		}
		if (anchorages.isEmpty()) {
			anchorages = null;
		}
	}

	@Override
	public void removeAnchored(IVisualPart<VR> anchored) {
		// if we loose the link to the viewer via the anchored, unregister
		if (parent == null && anchoreds.size() == 1) {
			unregister();
		}

		anchoreds.remove(anchored);
		if (anchoreds.size() == 0) {
			anchoreds = null;
		}
	}

	@Override
	public void removeChild(IVisualPart<VR> child) {
		int index = getChildren().indexOf(child);
		if (index < 0) {
			return;
		}
		if (isActive()) {
			child.deactivate();
		}

		child.setParent(null);
		removeChildVisual(child, index);
		List<IVisualPart<VR>> oldChildren = getChildren();
		removeChildWithoutNotify(child);

		pcs.firePropertyChange(CHILDREN_PROPERTY, oldChildren, getChildren());
	}

	@Override
	public void removeChildren(List<? extends IVisualPart<VR>> children) {
		for (IVisualPart<VR> child : children) {
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
	protected abstract void removeChildVisual(IVisualPart<VR> child, int index);

	private void removeChildWithoutNotify(IVisualPart<VR> child) {
		children.remove(child);
		if (children.size() == 0) {
			children = null;
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Moves a child {@link IVisualPart} into a lower index than it currently
	 * occupies.
	 * 
	 * @param child
	 *            the child {@link IVisualPart} being reordered
	 * @param index
	 *            new index for the child
	 */
	@Override
	public void reorderChild(IVisualPart<VR> child, int index) {
		removeChildVisual(child, children.indexOf(child));
		removeChildWithoutNotify(child);
		addChildWithoutNotify(child, index);
		addChildVisual(child, index);
	}

	@Override
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
		ads.setAdapter(key, adapter);
	}

	@Inject(optional = true)
	// IMPORTANT: if sub-classes override, they will have to transfer the inject
	// annotation.
	public void setAdapters(
			@AdapterMap Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractVisualPart) with those injected by Guice
		ads.setAdapters(adaptersWithKeys, false);
	}

	/**
	 * Sets the parent {@link IVisualPart}.
	 */
	@Override
	public void setParent(IVisualPart<VR> parent) {
		if (this.parent == parent) {
			return;
		}

		IVisualPart<VR> oldParent = this.parent;

		// unregister if we have no (remaining) link to the viewer
		if (this.parent != null) {
			if (parent == null && anchoreds == null) {
				unregister();
			}
		}

		this.parent = parent;

		// if we obtain a link to the viewer (via parent) then register visuals
		if (this.parent != null && anchoreds == null) {
			// TODO: why anchorages == null?? we cannot add anchorages before
			// adding the parent??
			register();
		}

		pcs.firePropertyChange(PARENT_PROPERTY, oldParent, parent);
	}

	@Override
	public void setRefreshVisual(boolean isRefreshVisual) {
		this.refreshVisual = isRefreshVisual;
	}

	/**
	 * Called when the link to the Viewer is lost.
	 */
	protected void unregister() {
		unregisterFromVisualPartMap();
	}

	protected void unregisterFromVisualPartMap() {
		getViewer().getVisualPartMap().remove(getVisual());
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		return ads.unsetAdapter(key);
	}

}
