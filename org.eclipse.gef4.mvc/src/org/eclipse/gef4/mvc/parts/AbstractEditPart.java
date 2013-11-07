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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef4.mvc.partviewer.IEditPartFactory;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;
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
public abstract class AbstractEditPart<V> implements IEditPart<V>, IAdaptable {

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

	private Object model;

	private int flags;

	private Map<Class<? extends IEditPolicy<V>>, IEditPolicy<V>> editPolicies;
	private List<INodeEditPart<V>> nodeChildren;

	/**
	 * call getEventListeners(Class) instead.
	 */
	EventListenerList eventListeners = new EventListenerList();

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

		List<INodeEditPart<V>> c = getNodeChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).activate();

		fireActivated();
	}

	/**
	 * Adds a child <code>EditPart</code> to this EditPart. This method is
	 * called from {@link #synchronizeNodeChildren()}. The following events
	 * occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #nodeChildren} List, and its parent
	 * is set to <code>this</code>
	 * <LI>{@link #addNodeChildVisual(IEditPart, int)} is called to add the
	 * child's visual
	 * <LI>{@link IEditPart#addNotify()} is called on the child.
	 * <LI><code>activate()</code> is called if this part is active
	 * <LI><code>EditPartListeners</code> are notified that the child has been
	 * added.
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #addNodeChildVisual(IEditPart, int)}.
	 * 
	 * @param child
	 *            The <code>EditPart</code> to add
	 * @param index
	 *            The index
	 * @see #addNodeChildVisual(IEditPart, int)
	 * @see #removeNodeChild(IEditPart)
	 * @see #reorderNodeChild(IEditPart,int)
	 */
	protected void addNodeChild(INodeEditPart<V> child, int index) {
		Assert.isNotNull(child);
		addNodeChildWithoutNotify(child, index);
		child.setParent(this);

		addNodeChildVisual(child, index);

		if (isActive())
			child.activate();
		fireNodeChildAdded(child, index);
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
	 * @see #addNodeChild(EditPart, int)
	 * @see AbstractGraphicalEditPart#removeNodeChildVisual(EditPart)
	 */
	protected abstract void addNodeChildVisual(INodeEditPart<V> child, int index);

	private void addNodeChildWithoutNotify(INodeEditPart<V> child, int index) {
		if (nodeChildren == null)
			nodeChildren = new ArrayList<INodeEditPart<V>>(2);
		nodeChildren.add(index, child);
	}

	/**
	 * Adds an EditPartListener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addEditPartListener(IEditPartListener listener) {
		eventListeners.addListener(IEditPartListener.class, listener);
	}

	/**
	 * Create the child <code>EditPart</code> for the given model object. This
	 * method is called from {@link #synchronizeNodeChildren()}.
	 * <P>
	 * By default, the implementation will delegate to the
	 * <code>EditPartViewer</code>'s {@link IEditPartFactory}. Subclasses may
	 * override this method instead of using a Factory.
	 * 
	 * @param model
	 *            the Child model object
	 * @return The child EditPart
	 */
	protected INodeEditPart<V> createChild(Object model) {
		INodeEditPart<V> child = getViewer().getEditPartFactory()
				.createNodeEditPart(this, model);
		child.setModel(model);
		return child;
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
		List<INodeEditPart<V>> c = getNodeChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).deactivate();

		if (editPolicies != null) {
			for (IEditPolicy<V> p : editPolicies.values()) {
				p.deactivate();
			}
		}

		setFlag(FLAG_ACTIVE, false);
		fireDeactivated();
	}

	/**
	 * Notifies <code>EditPartListeners</code> that this EditPart has been
	 * activated.
	 */
	protected void fireActivated() {
		Iterator listeners = getEventListeners(IEditPartListener.class);
		while (listeners.hasNext())
			((IEditPartListener) listeners.next()).partActivated(this);
	}

	/**
	 * Notifies <code>EditPartListeners</code> that a child has been added.
	 * 
	 * @param child
	 *            <code>EditPart</code> being added as child.
	 * @param index
	 *            Position child is being added into.
	 */
	protected void fireNodeChildAdded(INodeEditPart<V> child, int index) {
		Iterator listeners = getEventListeners(IEditPartListener.class);
		while (listeners.hasNext())
			((IEditPartListener) listeners.next()).childAdded(child, index);
	}

	/**
	 * Notifies <code>EditPartListeners </code> that this EditPart has been
	 * deactivated.
	 */
	protected void fireDeactivated() {
		Iterator listeners = getEventListeners(IEditPartListener.class);
		while (listeners.hasNext())
			((IEditPartListener) listeners.next()).partDeactivated(this);
	}

	/**
	 * Notifies <code>EditPartListeners</code> that a child is being removed.
	 * 
	 * @param child
	 *            <code>EditPart</code> being removed.
	 * @param index
	 *            Position of the child in children list.
	 */
	protected void fireRemovingNodeChild(INodeEditPart<V> child, int index) {
		Iterator listeners = getEventListeners(IEditPartListener.class);
		while (listeners.hasNext())
			((IEditPartListener) listeners.next()).removingChild(child, index);
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
	public Object getAdapter(Class key) {
		return Platform.getAdapterManager().getAdapter(this, key);
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getNodeChildren()
	 */
	public List<INodeEditPart<V>> getNodeChildren() {
		if (nodeChildren == null)
			return Collections.emptyList();
		return nodeChildren;
	}

	/**
	 * Returns an iterator for the specified type of listener
	 * 
	 * @param clazz
	 *            the Listener type over which to iterate
	 * @return Iterator
	 */
	private final Iterator getEventListeners(Class clazz) {
		return eventListeners.getListeners(clazz);
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

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getModel()
	 */
	public Object getModel() {
		return model;
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
	 * Returns a <code>List</code> containing the children model objects. If
	 * this EditPart's model is a container, this method should be overridden to
	 * returns its children. This is what causes children EditParts to be
	 * created.
	 * <P>
	 * Callers must not modify the returned List. Must not return
	 * <code>null</code>.
	 * 
	 * @return the List of children
	 */
	protected List<Object> getModelNodeChildren() {
		return Collections.emptyList();
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getViewer()
	 */
	protected IEditPartViewer<V> getViewer() {
		IRootEditPart<V> root = getRoot();
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
	 * Updates the set of children EditParts so that it is in sync with the
	 * model children. This method is called from {@link #refresh()}, and may
	 * also be called in response to notification from the model. This method
	 * requires linear time to complete. Clients should call this method as few
	 * times as possible. Consider also calling
	 * {@link #removeNodeChild(IEditPart)} and
	 * {@link #addNodeChild(IEditPart, int)} which run in constant time.
	 * <P>
	 * The update is performed by comparing the existing EditParts with the set
	 * of model children returned from {@link #getModelNodeChildren()}.
	 * EditParts whose models no longer exist are
	 * {@link #removeNodeChild(IEditPart) removed}. New models have their
	 * EditParts {@link #createChild(Object) created}.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 * 
	 * @see #getModelNodeChildren()
	 */
	public void synchronizeNodeChildren() {
		int i;
		INodeEditPart<V> editPart;
		Object model;

		List<INodeEditPart<V>> children = getNodeChildren();
		int size = children.size();
		Map<Object, IEditPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IEditPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (INodeEditPart<V>) children.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		List<Object> modelObjects = getModelNodeChildren();
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < children.size() && children.get(i).getModel() == model)
				continue;

			// Look to see if the EditPart is already around but in the
			// wrong location
			editPart = (INodeEditPart<V>) modelToEditPart.get(model);

			if (editPart != null)
				reorderNodeChild(editPart, i);
			else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				editPart = createChild(model);
				addNodeChild(editPart, i);
			}
		}

		// remove the remaining EditParts
		size = children.size();
		if (i < size) {
			List<INodeEditPart<V>> trash = new ArrayList<INodeEditPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(children.get(i));
			for (i = 0; i < trash.size(); i++) {
				INodeEditPart<V> ep = trash.get(i);
				removeNodeChild(ep);
			}
		}
	}

	/**
	 * Refreshes this EditPart's <i>visuals</i>. This method is called by
	 * {@link #refresh()}, and may also be called in response to notifications
	 * from the model. This method does nothing by default. Subclasses may
	 * override.
	 */
	public abstract void refreshVisual();

	/**
	 * Registers the <i>model</i> in the
	 * {@link IEditPartViewer#getEditPartRegistry()}. Subclasses should only
	 * extend this method if they need to register this EditPart in additional
	 * ways.
	 */
	protected void registerModel() {
		getViewer().getEditPartRegistry().put(getModel(), this);
	}

	/**
	 * Removes a child <code>EditPart</code>. This method is called from
	 * {@link #synchronizeNodeChildren()}. The following events occur in the
	 * order listed:
	 * <OL>
	 * <LI><code>EditPartListeners</code> are notified that the child is being
	 * removed
	 * <LI><code>deactivate()</code> is called if the child is active
	 * <LI>{@link IEditPart#removeNotify()} is called on the child.
	 * <LI>{@link #removeNodeChildVisual(IEditPart)} is called to remove the
	 * child's visual object.
	 * <LI>The child's parent is set to <code>null</code>
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #removeNodeChildVisual(IEditPart)}.
	 * 
	 * @param child
	 *            EditPart being removed
	 * @see #addNodeChild(IEditPart,int)
	 */
	protected void removeNodeChild(INodeEditPart<V> child) {
		Assert.isNotNull(child);
		int index = getNodeChildren().indexOf(child);
		if (index < 0)
			return;
		fireRemovingNodeChild(child, index);
		if (isActive())
			child.deactivate();
		removeNodeChildVisual(child);

		child.setParent(null);
		removeNodeChildWithoutNotify(child);
	}

	/**
	 * Removes the childs visual from this EditPart's visual. Subclasses should
	 * implement this method to support the visual type they introduce, such as
	 * Figures or TreeItems.
	 * 
	 * @param child
	 *            the child EditPart
	 */
	protected abstract void removeNodeChildVisual(INodeEditPart<V> child);

	private void removeNodeChildWithoutNotify(INodeEditPart<V> child) {
		getNodeChildren().remove(child);
		if (nodeChildren.size() == 0) {
			nodeChildren = null;
		}
	}

	/**
	 * No reason to override
	 * 
	 * @see IEditPart#removeEditPartListener(IEditPartListener)
	 */
	public void removeEditPartListener(IEditPartListener listener) {
		eventListeners.removeListener(IEditPartListener.class, listener);
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
	 * occupies. This method is called from {@link #synchronizeNodeChildren()}.
	 * 
	 * @param editpart
	 *            the child being reordered
	 * @param index
	 *            new index for the child
	 */
	protected void reorderNodeChild(INodeEditPart<V> child, int index) {
		removeNodeChildVisual(child);
		removeNodeChildWithoutNotify(child);
		addNodeChildWithoutNotify(child, index);
		addNodeChildVisual(child, index);
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

	/**
	 * Set the primary model object that this EditPart represents. This method
	 * is used by an <code>EditPartFactory</code> when creating an EditPart.
	 * 
	 * @see IEditPart#setModel(Object)
	 */
	public void setModel(Object model) {
		// TODO: optimize if attached model is passed in again
		this.model = model;
	}

	protected void synchronize() {
		synchronizeNodeChildren();
	}

	/**
	 * Describes this EditPart for developmental debugging purposes.
	 * 
	 * @return a description
	 */
	public String toString() {
		String c = getClass().getName();
		c = c.substring(c.lastIndexOf('.') + 1);
		return c + "( " + getModel() + " )";//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * Unregisters the <i>model</i> in the
	 * {@link IEditPartViewer#getEditPartRegistry()}. Subclasses should only
	 * extend this method if they need to unregister this EditPart in additional
	 * ways.
	 */
	protected void unregisterModel() {
		Map<Object, IEditPart<V>> registry = getViewer().getEditPartRegistry();
		if (registry.get(getModel()) == this)
			registry.remove(getModel());
	}

	protected void registerVisual() {
		getViewer().getVisualPartMap().put(getVisual(), this);
	}

	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(getVisual());
	}

}
