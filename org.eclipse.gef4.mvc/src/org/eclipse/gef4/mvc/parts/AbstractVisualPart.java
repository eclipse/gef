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

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Inject;

/**
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractVisualPart<VR> implements IVisualPart<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private AdaptableSupport<IVisualPart<VR>> as = new AdaptableSupport<IVisualPart<VR>>(
			this);

	private IVisualPart<VR> parent;
	private List<IVisualPart<VR>> children;

	private List<IVisualPart<VR>> anchoreds;
	private List<IVisualPart<VR>> anchorages;

	private boolean refreshVisual = true;
	private boolean isActive = false;

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
		if (!isActive) {
			isActive = true;
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, false, true);
			doActivate();
		}
	}

	protected void doActivate() {
		// TODO: rather do this via property changes (so a child becomes active
		// when its parent and anchorages are active??
		for (IVisualPart<VR> child : getChildren()) {
			child.activate();
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

		if (isActive())
			child.activate();

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

	@Override
	public void removeChildren(List<? extends IVisualPart<VR>> children) {
		for (IVisualPart<VR> child : children) {
			removeChild(child);
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
		if (children == null)
			children = new ArrayList<IVisualPart<VR>>(2);
		children.add(index, child);
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
		if (isActive) {
			doDeactivate();
			isActive = false;
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, true, false);
		}
	}

	protected void doDeactivate() {
		for (IVisualPart<VR> child : getChildren()) {
			child.deactivate();
		}
	}

	@Override
	public List<IVisualPart<VR>> getChildren() {
		if (children == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return as.getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return as.getAdapter(key);
	}

	protected IViewer<VR> getViewer() {
		IRootPart<VR> root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getViewer();
	}

	@Override
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Inject
	// IMPORTANT: if sub-classes override, they will have to transfer the inject annotation.
	public void setAdapters(
			@AdapterMap(AbstractVisualPart.class) Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractVisualPart) with those injected by Guice
		as.setAdapters(adaptersWithKeys, false);
	}
	
	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey) {
		return as.getAdapters(classKey);
	}

	@Override
	public Map<AdapterKey<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors() {
		return as.getAdapters(IBehavior.class);
	}

	@Override
	public Map<AdapterKey<? extends IPolicy<VR>>, IPolicy<VR>> getPolicies() {
		return as.getAdapters(IPolicy.class);
	}

	/**
	 * @return <code>true</code> if this {@link IVisualPart} is active.
	 */
	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean isRefreshVisual() {
		return refreshVisual;
	}

	protected abstract void doRefreshVisual();

	@Override
	public void setRefreshVisual(boolean isRefreshVisual) {
		this.refreshVisual = isRefreshVisual;
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

	@Override
	public void removeChild(IVisualPart<VR> child) {
		int index = getChildren().indexOf(child);
		if (index < 0)
			return;
		if (isActive())
			child.deactivate();

		child.setParent(null);
		removeChildVisual(child);
		List<IVisualPart<VR>> oldChildren = getChildren();
		removeChildWithoutNotify(child);

		pcs.firePropertyChange(CHILDREN_PROPERTY, oldChildren, getChildren());
	}

	/**
	 * Removes the child's visual from this {@link IVisualPart}'s visual.
	 * 
	 * @param child
	 *            the child {@link IVisualPart}
	 */
	protected abstract void removeChildVisual(IVisualPart<VR> child);

	private void removeChildWithoutNotify(IVisualPart<VR> child) {
		children.remove(child);
		if (children.size() == 0) {
			children = null;
		}
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<T> key) {
		return as.unsetAdapter(key);
	}

	@Override
	public void reorderAnchorage(IVisualPart<VR> anchorage, int index) {
		detachFromAnchorageVisual(anchorage);
		removeAnchorageWithoutNotify(anchorage);
		addAnchorageWithoutNotify(anchorage, index);
		attachToAnchorageVisual(anchorage, index);
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
		removeChildVisual(child);
		removeChildWithoutNotify(child);
		addChildWithoutNotify(child, index);
		addChildVisual(child, index);
	}

	protected void registerAtVisualPartMap() {
		getViewer().getVisualPartMap().put(getVisual(), this);
	}

	protected void unregisterFromVisualPartMap() {
		getViewer().getVisualPartMap().remove(getVisual());
	}

	/**
	 * Sets the parent {@link IVisualPart}.
	 */
	@Override
	public void setParent(IVisualPart<VR> parent) {
		if (this.parent == parent)
			return;

		IVisualPart<VR> oldParent = this.parent;

		// unregister if we have no (remaining) link to the viewer
		if (this.parent != null) {
			if (parent == null && anchorages == null) {
				unregister();
			}
		}

		this.parent = parent;

		// if we obtain a link to the viewer (via parent) then register visuals
		if (this.parent != null && anchorages == null) {
			register();
		}

		pcs.firePropertyChange(PARENT_PROPERTY, oldParent, parent);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public IVisualPart<VR> getParent() {
		return parent;
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

	protected abstract void attachToAnchorageVisual(IVisualPart<VR> anchorage,
			int index);

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

	protected abstract void detachFromAnchorageVisual(IVisualPart<VR> anchorage);

	@Override
	public List<IVisualPart<VR>> getAnchoreds() {
		if (anchoreds == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	@Override
	public void addAnchorage(IVisualPart<VR> anchorage) {
		addAnchorage(anchorage, getAnchorages().size());
	}

	@Override
	public void addAnchorage(IVisualPart<VR> anchorage, int index) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}

		List<IVisualPart<VR>> oldAnchorages = new ArrayList<IVisualPart<VR>>(
				getAnchorages());
		addAnchorageWithoutNotify(anchorage, index);
		anchorage.addAnchored(this);
		
		anchorage.refreshVisual();
		attachToAnchorageVisual(anchorage, index);
		refreshVisual();

		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	@Override
	public void addAnchorages(List<? extends IVisualPart<VR>> anchorages) {
		for (IVisualPart<VR> anchorage : anchorages) {
			addAnchorage(anchorage);
		}
	}

	@Override
	public void addAnchorages(List<? extends IVisualPart<VR>> anchorages,
			int index) {
		for (int i = anchorages.size() - 1; i >= 0; i--) {
			addAnchorage(anchorages.get(i), index);
		}
	}

	private void addAnchorageWithoutNotify(IVisualPart<VR> anchorage, int index) {
		if (anchorages == null) {
			anchorages = new ArrayList<IVisualPart<VR>>();
		}
		anchorages.add(index, anchorage);
	}

	/**
	 * Called when a link to the Viewer is obtained.
	 */
	protected void register() {
		registerAtVisualPartMap();
	}

	// counterpart to setParent(null) in case of hierarchy
	@Override
	public void removeAnchorage(IVisualPart<VR> anchorage) {
		if (anchorage == null) {
			throw new IllegalArgumentException("Anchorage may not be null.");
		}
		if (anchorages == null || !anchorages.contains(anchorage)) {
			throw new IllegalArgumentException("Anchorage has to be contained.");
		}

		List<IVisualPart<VR>> oldAnchorages = new ArrayList<IVisualPart<VR>>(
				anchorages);
		removeAnchorageWithoutNotify(anchorage);

		anchorage.removeAnchored(this);
		detachFromAnchorageVisual(anchorage);

		pcs.firePropertyChange(ANCHORAGES_PROPERTY, oldAnchorages,
				getAnchorages());
	}

	@Override
	public void removeAnchorages(List<? extends IVisualPart<VR>> anchorages) {
		for (IVisualPart<VR> anchorage : anchorages) {
			removeAnchorage(anchorage);
		}
	}

	private void removeAnchorageWithoutNotify(IVisualPart<VR> anchorage) {
		anchorages.remove(anchorage);
		if (anchorages.size() == 0) {
			anchorages = null;
		}
	}

	/**
	 * Called when the link to the Viewer is lost.
	 */
	protected void unregister() {
		unregisterFromVisualPartMap();
	}

	@Override
	public List<IVisualPart<VR>> getAnchorages() {
		if (anchorages == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchorages);
	}

}
