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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractVisualPart<V> implements IVisualPart<V>,
		IAdaptable {

	/**
	 * This flag is set during {@link #activate()}, and reset on
	 * {@link #deactivate()}
	 */
	protected static final int FLAG_ACTIVE = 1;

	/**
	 * The left-most bit that is reserved by this class for setting flags.
	 * Subclasses may define additional flags starting at
	 * <code>(MAX_FLAG << 1)</code>.
	 */
	protected static final int MAX_FLAG = FLAG_ACTIVE;

	private int flags;

	private Map<Class<?>, IPolicy<V>> policies;

	private IVisualPart<V> parent;
	private List<IVisualPart<V>> children;

	private List<IVisualPart<V>> anchoreds;
	private List<IVisualPart<V>> anchorages;

	/**
	 * Activates this {@link IVisualPart}, which in turn activates its policies
	 * and children. Subclasses should <em>extend</em> this method if they need
	 * to register listeners to the content. Activation indicates that the
	 * {@link IVisualPart} is realized in an {@link IVisualPartViewer}.
	 * <code>deactivate()</code> is the inverse, and is eventually called on all
	 * {@link IVisualPart}s.
	 * 
	 * @see #deactivate()
	 */
	public void activate() {
		setFlag(FLAG_ACTIVE, true);

		if (policies != null) {
			for (IPolicy<V> p : policies.values()) {
				p.activate();
			}
		}

		List<IVisualPart<V>> c = getChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).activate();
	}

	public void addChild(IVisualPart<V> child, int index) {
		Assert.isNotNull(child);
		addChildWithoutNotify(child, index);

		addChildVisual(child, index);

		child.setParent(this);

		child.refreshVisual();

		if (isActive())
			child.activate();
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
	protected abstract void addChildVisual(IVisualPart<V> child, int index);

	// TODO: make concrete, passing over the visual container to the child (as
	// in case of anchoreds)
	private void addChildWithoutNotify(IVisualPart<V> child, int index) {
		if (children == null)
			children = new ArrayList<IVisualPart<V>>(2);
		children.add(index, child);
	}

	public IRootPart<V> getRoot() {
		if (getParent() == null) {
			return null;
		}
		return getParent().getRoot();
	}

	/**
	 * Deactivates this {@link IVisualPart}, and in turn deactivates its
	 * policies and children. Subclasses should <em>extend</em> this method to
	 * remove any listeners established in {@link #activate()}
	 * 
	 * @see #activate()
	 */
	public void deactivate() {
		List<IVisualPart<V>> c = getChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).deactivate();

		if (policies != null) {
			for (IPolicy<V> p : policies.values()) {
				p.deactivate();
			}
		}

		setFlag(FLAG_ACTIVE, false);
	}

	/**
	 * Returns the specified adapter if recognized. Delegates to the Plaform
	 * adapter mechanism.
	 * <P>
	 * Additional adapter types may be added in the future. Subclasses should
	 * extend this method as needed.
	 * 
	 * @see IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class key) {
		return Platform.getAdapterManager().getAdapter(this, key);
	}

	public List<IVisualPart<V>> getChildren() {
		if (children == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(children);
	}

	/**
	 * Returns the boolean value of the given flag. Specifically, returns
	 * <code>true</code> if the bitwise AND of the specified flag and the
	 * internal flags field is non-zero.
	 * 
	 * @param flag
	 *            Bitmask indicating which flag to return
	 * @return the requested flag's value
	 * @see #setFlag(int,boolean)
	 */
	protected final boolean getFlag(int flag) {
		return (flags & flag) != 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P extends IPolicy<V>> P getPolicy(Class<? super P> key) {
		if (policies == null) {
			return null;
		}
		return (P) policies.get(key);
	}

	protected IVisualPartViewer<V> getViewer() {
		IRootPart<V> root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getViewer();
	}

	@Override
	public <P extends IPolicy<V>> void installPolicy(Class<? super P> key,
			P editPolicy) {
		if (policies == null) {
			policies = new HashMap<Class<?>, IPolicy<V>>();
		}
		policies.put(key, editPolicy);
		editPolicy.setHost(this);
		if (isActive()) {
			editPolicy.activate();
		}
	}

	/**
	 * @return <code>true</code> if this {@link IVisualPart} is active.
	 */
	@Override
	public boolean isActive() {
		return getFlag(FLAG_ACTIVE);
	}

	/**
	 * Refreshes this {@link IVisualPart}'s <i>visuals</i>. This method does
	 * nothing by default. Subclasses may override.
	 */
	public abstract void refreshVisual();

	public void removeChild(IVisualPart<V> child) {
		Assert.isNotNull(child);
		int index = getChildren().indexOf(child);
		if (index < 0)
			return;
		if (isActive())
			child.deactivate();

		child.setParent(null);
		removeChildVisual(child);
		removeChildWithoutNotify(child);
	}

	/**
	 * Removes the child's visual from this {@link IVisualPart}'s visual.
	 * 
	 * @param child
	 *            the child {@link IVisualPart}
	 */
	protected abstract void removeChildVisual(IVisualPart<V> child);

	private void removeChildWithoutNotify(IVisualPart<V> child) {
		children.remove(child);
		if (children.size() == 0) {
			children = null;
		}
	}

	@Override
	public <P extends IPolicy<V>> void uninstallPolicy(Class<P> key) {
		if (policies == null)
			return;
		IPolicy<V> editPolicy = policies.remove(key);
		if (editPolicy != null) {
			editPolicy.deactivate();
			editPolicy.setHost(null);
		}
		if (policies.size() == 0) {
			policies = null;
		}
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
	public void reorderChild(IVisualPart<V> child, int index) {
		removeChildVisual(child);
		removeChildWithoutNotify(child);
		addChildWithoutNotify(child, index);
		addChildVisual(child, index);
	}

	/**
	 * Sets the value of the specified flag. Flag values are declared as static
	 * constants. Subclasses may define additional constants above
	 * {@link #MAX_FLAG}.
	 * 
	 * @param flag
	 *            Flag being set
	 * @param value
	 *            Value of the flag to be set
	 * @see #getFlag(int)
	 */
	protected final void setFlag(int flag, boolean value) {
		if (value)
			flags |= flag;
		else
			flags &= ~flag;
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
	public void setParent(IVisualPart<V> parent) {
		if (this.parent == parent)
			return;

		if (this.parent != null) {
			unregisterFromVisualPartMap();
		}
		this.parent = parent;
		if (this.parent != null) {
			registerAtVisualPartMap();
		}
	}

	public IVisualPart<V> getParent() {
		return parent;
	}

	@Override
	public void addAnchored(IVisualPart<V> anchored) {
		if (anchoreds == null) {
			anchoreds = new ArrayList<IVisualPart<V>>();
		}
		anchoreds.add(anchored);

		addAnchoredVisual(anchored);
		anchored.addAnchorage(this);

		anchored.refreshVisual();
	}

	protected void addAnchoredVisual(IVisualPart<V> anchored) {
		IAnchor<V> anchor = getAnchor(anchored);
		anchored.attachVisualToAnchorageVisual(getVisual(), anchor);
	}

	// may return an anchor if it wants to provide an reference point or null
	protected abstract IAnchor<V> getAnchor(IVisualPart<V> anchored);

	@Override
	public void removeAnchored(IVisualPart<V> anchored) {
		anchored.removeAnchorage(this);
		removeAnchoredVisual(anchored);

		anchoreds.remove(anchored);
		if (anchoreds.size() == 0) {
			anchoreds = null;
		}
	}

	protected void removeAnchoredVisual(IVisualPart<V> anchored) {
		IAnchor<V> anchor = getAnchor(anchored);
		anchored.detachVisualFromAnchorageVisual(getVisual(), anchor);
	}

	@Override
	public List<IVisualPart<V>> getAnchoreds() {
		if (anchoreds == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	@Override
	public void addAnchorage(IVisualPart<V> anchorage) {
		if (anchorages == null) {
			anchorages = new ArrayList<IVisualPart<V>>();
		}
		anchorages.add(anchorage);
	}

	// counterpart to setParent(null) in case of hierarchy
	@Override
	public void removeAnchorage(IVisualPart<V> anchorage) {
		anchorages.remove(anchorage);
		if (anchorages.size() == 0) {
			anchorages = null;
		}
	}

	@Override
	public List<IVisualPart<V>> getAnchorages() {
		if (anchorages == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchorages);
	}

}
