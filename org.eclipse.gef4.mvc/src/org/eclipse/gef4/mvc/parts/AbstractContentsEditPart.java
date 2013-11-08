package org.eclipse.gef4.mvc.parts;

import java.util.Map;

import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public abstract class AbstractContentsEditPart<V> extends AbstractEditPart<V>
		implements IContentsEditPart<V> {

	private IEditPart<V> parent;
	private Object model;

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getModel()
	 */
	public Object getModel() {
		return model;
	}

	/**
	 * Set the primary model object that this EditPart represents. This method
	 * is used by an <code>EditPartFactory</code> when creating an EditPart.
	 * 
	 * @see IEditPart#setModel(Object)
	 */
	public void setModel(Object model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
	}

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

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 * 
	 * @see IEditPart#setParent(IEditPart)
	 */
	public void setParent(IEditPart<V> parent) {
		if (this.parent == parent)
			return;

		this.parent = parent;
		if (this.parent != null) {
			registerModel();
			registerVisual();
		} else {
			unregisterVisual();
			unregisterModel();
		}
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getParent()
	 */
	public IEditPart<V> getParent() {
		return parent;
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getRoot()
	 */
	public IRootEditPart<V> getRoot() {
		if (getParent() == null) {
			return null;
		}
		return getParent().getRoot();
	}

}
