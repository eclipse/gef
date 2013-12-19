/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

/**
 * The baseline implementation for the {@link IEditPart} interface.
 * <P>
 * Since this is the default implementation of an interface, this document deals
 * with proper sub-classing of this implementation. This class is not the API.
 * For documentation on proper usage of the public API, see the documentation
 * for the interface itself: {@link IEditPart}.
 * <P>
 * This class assumes no visual representation. Subclasses
 * {@link AbstractGraphicalEditPart} and {@link AbstractTreeEditPart} add
 * support for {@link org.eclipse.draw2d.IFigure Figures} and
 * {@link org.eclipse.swt.widgets.TreeItem TreeItems} respectively.
 * <P>
 * AbstractEditPart provides support for children. All AbstractEditPart's can
 * potentially be containers for other EditParts.
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

	private Map<Class<? extends IEditPolicy<V>>, IEditPolicy<V>> editPolicies;

	private IVisualPart<V> parent;
	private List<IVisualPart<V>> children;

	private List<IVisualPart<V>> anchoreds;
	private List<IVisualPart<V>> anchorages;

	/**
	 * Activates this EditPart, which in turn activates its children and
	 * EditPolicies. Subclasses should <em>extend</em> this method to add
	 * listeners to the model. Activation indicates that the EditPart is
	 * realized in an EditPartViewer. <code>deactivate()</code> is the inverse,
	 * and is eventually called on all EditParts.
	 * 
	 * @see IEditPart#activate()
	 * @see #deactivate()
	 */
	public void activate() {
		setFlag(FLAG_ACTIVE, true);

		if (editPolicies != null) {
			for (IEditPolicy<V> p : editPolicies.values()) {
				p.activate();
			}
		}

		List<IVisualPart<V>> c = getChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).activate();

	}

	/**
	 * Adds a child <code>EditPart</code> to this EditPart. This method is
	 * called from {@link #synchronizeContentChildren()}. The following events
	 * occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #children} List, and its parent is
	 * set to <code>this</code>
	 * <LI>{@link #addChildVisual(IEditPart, int)} is called to add the child's
	 * visual
	 * <LI>{@link IEditPart#addNotify()} is called on the child.
	 * <LI><code>activate()</code> is called if this part is active
	 * <LI><code>EditPartListeners</code> are notified that the child has been
	 * added.
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #addChildVisual(IEditPart, int)}.
	 * 
	 * @param child
	 *            The <code>EditPart</code> to add
	 * @param index
	 *            The index
	 * @see #addChildVisual(IEditPart, int)
	 * @see #removeNodeChild(IEditPart)
	 * @see #reorderChild(IEditPart,int)
	 */
	public void addChild(IVisualPart<V> child, int index) {
		Assert.isNotNull(child);
		addChildWithoutNotify(child, index);

		child.setParent(this);
		
		addChildVisual(child, index);
		child.refreshVisual();

		if (isActive())
			child.activate();
	}

	/**
	 * Performs the addition of the child's <i>visual</i> to this EditPart's
	 * Visual. The provided subclasses {@link AbstractGraphicalEditPart} and
	 * {@link AbstractTreeEditPart} already implement this method correctly, so
	 * it is unlikely that this method should be overridden.
	 * 
	 * @param child
	 *            The EditPart being added
	 * @param index
	 *            The child's position
	 * @see #addChild(EditPart, int)
	 * @see AbstractGraphicalEditPart#removeChildVisual(EditPart)
	 */
	protected abstract void addChildVisual(IVisualPart<V> child, int index);
	//TODO: make concrete, passing over the visual container to the child (as in case of anchoreds)
	

	private void addChildWithoutNotify(IVisualPart<V> child, int index) {
		if (children == null)
			children = new ArrayList<IVisualPart<V>>(2);
		children.add(index, child);
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getRoot()
	 */
	public IRootVisualPart<V> getRoot() {
		if (getParent() == null) {
			return null;
		}
		return getParent().getRoot();
	}

	/**
	 * Deactivates this EditPart, and in turn deactivates its children and
	 * EditPolicies. Subclasses should <em>extend</em> this method to remove any
	 * listeners established in {@link #activate()}
	 * 
	 * @see IEditPart#deactivate()
	 * @see #activate()
	 */
	public void deactivate() {
		List<IVisualPart<V>> c = getChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).deactivate();

		if (editPolicies != null) {
			for (IEditPolicy<V> p : editPolicies.values()) {
				p.deactivate();
			}
		}

		setFlag(FLAG_ACTIVE, false);
	}

	/**
	 * Returns the specified adapter if recognized. Delegates to the workbench
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
	public <P extends IEditPolicy<V>> P getEditPolicy(Class<P> key) {
		if (editPolicies == null) {
			return null;
		}
		return (P) editPolicies.get(key);
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getViewer()
	 */
	protected IVisualPartViewer<V> getViewer() {
		IRootVisualPart<V> root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getViewer();
	}

	@Override
	public <P extends IEditPolicy<V>> void installEditPolicy(Class<P> key,
			P editPolicy) {
		if (editPolicies == null) {
			editPolicies = new HashMap<Class<? extends IEditPolicy<V>>, IEditPolicy<V>>();
		}
		editPolicies.put(key, editPolicy);
		editPolicy.setHost(this);
		if (isActive()) {
			editPolicy.activate();
		}
	}

	/**
	 * @return <code>true</code> if this EditPart is active.
	 */
	protected boolean isActive() {
		return getFlag(FLAG_ACTIVE);
	}

	/**
	 * Refreshes this EditPart's <i>visuals</i>. This method is called by
	 * {@link #refresh()}, and may also be called in response to notifications
	 * from the model. This method does nothing by default. Subclasses may
	 * override.
	 */
	public abstract void refreshVisual();

	/**
	 * Removes a child <code>EditPart</code>. This method is called from
	 * {@link #synchronizeContentChildren()}. The following events occur in the
	 * order listed:
	 * <OL>
	 * <LI><code>EditPartListeners</code> are notified that the child is being
	 * removed
	 * <LI><code>deactivate()</code> is called if the child is active
	 * <LI>{@link IEditPart#removeNotify()} is called on the child.
	 * <LI>{@link #removeChildVisual(IEditPart)} is called to remove the child's
	 * visual object.
	 * <LI>The child's parent is set to <code>null</code>
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #removeChildVisual(IEditPart)}.
	 * 
	 * @param child
	 *            EditPart being removed
	 * @see #addChild(IEditPart,int)
	 */
	public void removeChild(IVisualPart<V> child) {
		Assert.isNotNull(child);
		int index = getChildren().indexOf(child);
		if (index < 0)
			return;
		if (isActive())
			child.deactivate();
		removeChildVisual(child);

		child.setParent(null);
		removeChildWithoutNotify(child);
	}

	/**
	 * Removes the childs visual from this EditPart's visual. Subclasses should
	 * implement this method to support the visual type they introduce, such as
	 * Figures or TreeItems.
	 * 
	 * @param child
	 *            the child EditPart
	 */
	protected abstract void removeChildVisual(IVisualPart<V> child);

	private void removeChildWithoutNotify(IVisualPart<V> child) {
		children.remove(child);
		if (children.size() == 0) {
			children = null;
		}
	}

	@Override
	public <P extends IEditPolicy<V>> void uninstallEditPolicy(Class<P> key) {
		if (editPolicies == null)
			return;
		IEditPolicy<V> editPolicy = editPolicies.remove(key);
		if (editPolicy != null) {
			editPolicy.deactivate();
			editPolicy.setHost(null);
		}
		if (editPolicies.size() == 0) {
			editPolicies = null;
		}
	}

	/**
	 * Moves a child <code>EditPart</code> into a lower index than it currently
	 * occupies. This method is called from
	 * {@link #synchronizeContentChildren()}.
	 * 
	 * @param editpart
	 *            the child being reordered
	 * @param index
	 *            new index for the child
	 */
	protected void reorderChild(IVisualPart<V> child, int index) {
		removeChildVisual(child);
		removeChildWithoutNotify(child);
		addChildWithoutNotify(child, index);
		addChildVisual(child, index);
	}

	/**
	 * Sets the value of the specified flag. Flag values are decalared as static
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

	protected void registerVisual() {
		getViewer().getVisualPartMap().put(getVisual(), this);
	}

	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(getVisual());
	}

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 * 
	 * @see IEditPart#setParent(IEditPart)
	 */
	public void setParent(IVisualPart<V> parent) {
		if (this.parent == parent)
			return;

		if (this.parent != null) {
			unregisterVisual();
		}
		this.parent = parent;
		if (this.parent != null) {
			registerVisual();
		}
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getParent()
	 */
	public IVisualPart<V> getParent() {
		return parent;
	}

	@Override
	public void addAnchored(IVisualPart<V> anchored) {
		if(anchoreds == null){
			anchoreds = new ArrayList<IVisualPart<V>>();
		}
		anchoreds.add(anchored);
		
		anchored.addAnchorage(this);
		
		attachAnchoredVisual(anchored);
	}

	protected void attachAnchoredVisual(IVisualPart<V> anchored){
		IAnchor<V> anchor = getAnchor(anchored);
		anchored.attachVisualToAnchorageVisual(anchor);
	}

	protected abstract IAnchor<V> getAnchor(IVisualPart<V> anchored);
	
	@Override
	public void removeAnchored(IVisualPart<V> anchored) {
		detachAnchoredVisual(anchored);
		
		anchored.removeAnchorage(this);
		
		anchoreds.remove(anchored);
		if(anchoreds.size() == 0){
			anchoreds = null;
		}
	}

	protected void detachAnchoredVisual(IVisualPart<V> anchored){
		IAnchor<V> anchor = getAnchor(anchored);
		anchored.detachVisualFromAnchorageVisual(anchor);
	}

	@Override
	public List<IVisualPart<V>> getAnchoreds() {
		if(anchoreds == null){
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	@Override
	public void addAnchorage(IVisualPart<V> anchorage) {
		if(anchorages == null){
			anchorages = new ArrayList<IVisualPart<V>>();
		}
		anchorages.add(anchorage);
		// TODO: add listener here so we can update our visuals accordingly....
	}

	//  counterpart to setParent(null) in case of hierarchy
	@Override
	public void removeAnchorage(IVisualPart<V> anchorage) {
		// TODO: remove listener
		
		anchorages.remove(anchorage);
		if(anchorages.size() == 0){
			anchorages = null;
		}
	}

	@Override
	public List<IVisualPart<V>> getAnchorages() {
		if(anchorages == null){
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchorages);
	}

}
