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

	private int flags;

	private Map<Class<? extends IEditPolicy<V>>, IEditPolicy<V>> editPolicies;
	private List<IContentsEditPart<V>> children;

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

		List<IContentsEditPart<V>> c = getChildren();
		for (int i = 0; i < c.size(); i++)
			c.get(i).activate();

		fireActivated();
	}

	/**
	 * Adds a child <code>EditPart</code> to this EditPart. This method is
	 * called from {@link #synchronizeChildren()}. The following events
	 * occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #children} List, and its parent
	 * is set to <code>this</code>
	 * <LI>{@link #addChildVisual(IEditPart, int)} is called to add the
	 * child's visual
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
	protected void addChild(IContentsEditPart<V> child, int index) {
		Assert.isNotNull(child);
		addChildWithoutNotify(child, index);
		
		child.setParent(this);

		addChildVisual(child, index);

		// TODO: activation/deactivation of child
		if (isActive())
			child.activate();
		fireChildAdded(child, index);
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
	protected abstract void addChildVisual(IContentsEditPart<V> child, int index);

	private void addChildWithoutNotify(IContentsEditPart<V> child, int index) {
		if (children == null)
			children = new ArrayList<IContentsEditPart<V>>(2);
		children.add(index, child);
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
	 * method is called from {@link #synchronizeChildren()}.
	 * <P>
	 * By default, the implementation will delegate to the
	 * <code>EditPartViewer</code>'s {@link IEditPartFactory}. Subclasses may
	 * override this method instead of using a Factory.
	 * 
	 * @param model
	 *            the Child model object
	 * @return The child EditPart
	 */
	protected INodeEditPart<V> createNodeChild(Object model) {
		INodeEditPart<V> child = getViewer().getEditPartFactory()
				.createNodeEditPart(this, model);
		child.setModel(model);
		return child;
	}
	
	protected IConnectionEditPart<V> createConnectionChild(Object model) {
		IConnectionEditPart<V> child = getViewer().getEditPartFactory()
				.createConnectionEditPart(this, model);
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
		List<IContentsEditPart<V>> c = getChildren();
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
	protected void fireChildAdded(IEditPart<V> child, int index) {
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
	protected void fireRemovingChild(IEditPart<V> child, int index) {
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

	public List<IContentsEditPart<V>> getChildren(){
		if (children == null)
			return Collections.emptyList();
		return children;
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


	@SuppressWarnings("unchecked")
	@Override
	public <P extends IEditPolicy<V>> P getEditPolicy(Class<P> key) {
		if (editPolicies == null) {
			return null;
		}
		return (P) editPolicies.get(key);
	}
	
	protected abstract boolean isNodeModel(Object model);
	protected abstract boolean isConnectionModel(Object model);	

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
	 * Refreshes this EditPart's <i>visuals</i>. This method is called by
	 * {@link #refresh()}, and may also be called in response to notifications
	 * from the model. This method does nothing by default. Subclasses may
	 * override.
	 */
	public abstract void refreshVisual();

	/**
	 * Removes a child <code>EditPart</code>. This method is called from
	 * {@link #synchronizeChildren()}. The following events occur in the
	 * order listed:
	 * <OL>
	 * <LI><code>EditPartListeners</code> are notified that the child is being
	 * removed
	 * <LI><code>deactivate()</code> is called if the child is active
	 * <LI>{@link IEditPart#removeNotify()} is called on the child.
	 * <LI>{@link #removeChildVisual(IEditPart)} is called to remove the
	 * child's visual object.
	 * <LI>The child's parent is set to <code>null</code>
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #removeChildVisual(IEditPart)}.
	 * 
	 * @param child
	 *            EditPart being removed
	 * @see #addChild(IEditPart,int)
	 */
	protected void removeChild(IContentsEditPart<V> child) {
		Assert.isNotNull(child);
		int index = getChildren().indexOf(child);
		if (index < 0)
			return;
		fireRemovingChild(child, index);
		// TODO: activation/deactivation of connections?
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
	protected abstract void removeChildVisual(IContentsEditPart<V> child);

	private void removeChildWithoutNotify(IContentsEditPart<V> child) {
		getChildren().remove(child);
		if (children.size() == 0) {
			children = null;
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
	 * occupies. This method is called from {@link #synchronizeChildren()}.
	 * 
	 * @param editpart
	 *            the child being reordered
	 * @param index
	 *            new index for the child
	 */
	protected void reorderChild(IContentsEditPart<V> child, int index) {
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
	 * Updates the set of children EditParts so that it is in sync with the
	 * model children. This method is called from {@link #refresh()}, and may
	 * also be called in response to notification from the model. This method
	 * requires linear time to complete. Clients should call this method as few
	 * times as possible. Consider also calling
	 * {@link #removeNodeChild(IEditPart)} and
	 * {@link #addChild(IEditPart, int)} which run in constant time.
	 * <P>
	 * The update is performed by comparing the existing EditParts with the set
	 * of model children returned from {@link #getModelNodeChildren()}.
	 * EditParts whose models no longer exist are
	 * {@link #removeNodeChild(IEditPart) removed}. New models have their
	 * EditParts {@link #createNodeChild(Object) created}.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 * 
	 * @see #getModelChildren()
	 */
	public void synchronizeChildren() {
		int i;
		IContentsEditPart<V> editPart;
		Object model;

		List<IContentsEditPart<V>> children = getChildren();
		int size = children.size();
		Map<Object, IContentsEditPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IContentsEditPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (IContentsEditPart<V>) children.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		List<Object> modelObjects = getModelChildren();
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < children.size() && children.get(i).getModel() == model)
				continue;

			// Look to see if the EditPart is already around but in the
			// wrong location
			editPart = (IContentsEditPart<V>) modelToEditPart.get(model);

			if (editPart != null)
				reorderChild(editPart, i);
			else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				if(isNodeModel(model)){
					editPart = createNodeChild(model);
				}
				else {
					editPart = createConnectionChild(model);
				}
				addChild(editPart, i);
			}
		}

		// remove the remaining EditParts
		size = children.size();
		if (i < size) {
			List<IContentsEditPart<V>> trash = new ArrayList<IContentsEditPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(children.get(i));
			for (i = 0; i < trash.size(); i++) {
				IContentsEditPart<V> ep = trash.get(i);
				removeChild(ep);
			}
		}
	}
	
	protected void synchronize() {
		synchronizeChildren();
	}
	
	protected List<Object> getModelChildren() {
		return Collections.emptyList();
	}
	
}
