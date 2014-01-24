package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public abstract class AbstractContentPart<V> extends AbstractVisualPart<V>
		implements IContentPart<V> {

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
	 * {@link IVisualPartViewer#getContentPartMap()}. Subclasses should only
	 * extend this method if they need to register this EditPart in additional
	 * ways.
	 */
	protected void registerModel() {
		getViewer().getContentPartMap().put(getModel(), this);
	}

	/**
	 * Unregisters the <i>model</i> in the
	 * {@link IVisualPartViewer#getContentPartMap()}. Subclasses should only
	 * extend this method if they need to unregister this EditPart in additional
	 * ways.
	 */
	protected void unregisterModel() {
		Map<Object, IContentPart<V>> registry = getViewer().getContentPartMap();
		if (registry.get(getModel()) == this)
			registry.remove(getModel());
	}

	/**
	 * Updates the set of children EditParts so that it is in sync with the
	 * model children. This method is called from {@link #refresh()}, and may
	 * also be called in response to notification from the model. This method
	 * requires linear time to complete. Clients should call this method as few
	 * times as possible. Consider also calling
	 * {@link #removeNodeChild(IEditPart)} and {@link #addChild(IEditPart, int)}
	 * which run in constant time.
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
	protected void synchronizeContentChildren() {
		// only synchronize ContentPart children

		int i;
		IContentPart<V> editPart;
		Object model;

		List<IContentPart<V>> children = filterContentParts(getChildren());
		int size = children.size();
		Map<Object, IContentPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IContentPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (IContentPart<V>) children.get(i);
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
			editPart = (IContentPart<V>) modelToEditPart.get(model);

			if (editPart != null) {
				// TODO: this is wrong, it has to take into consideration the
				// visual parts in between
				reorderChild(editPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				editPart = findOrCreatePartFor(model);
				addChild(editPart, i);
			}
		}

		// remove the remaining EditParts
		size = children.size();
		if (i < size) {
			List<IContentPart<V>> trash = new ArrayList<IContentPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(children.get(i));
			for (i = 0; i < trash.size(); i++) {
				IContentPart<V> ep = trash.get(i);
				removeChild(ep);
			}
		}
	}

	private List<IContentPart<V>> filterContentParts(
			List<IVisualPart<V>> visualParts) {
		List<IContentPart<V>> contentParts = new ArrayList<IContentPart<V>>();
		for (IVisualPart<V> visualPart : visualParts) {
			if (visualPart instanceof IContentPart) {
				contentParts.add((IContentPart<V>) visualPart);
			}
		}
		return contentParts;
	}

	protected List<Object> getModelChildren() {
		return Collections.emptyList();
	}

	protected List<Object> getModelAnchored() {
		return Collections.emptyList();
	}

	@Override
	public void setParent(IVisualPart<V> parent) {
		if (getParent() == parent) {
			return;
		}
		if (parent == null) {
			// remove all content children
			for (IVisualPart<V> child : filterContentParts(getChildren())) {
				removeChild(child);
			}
			// remove all content anchored
			for (IVisualPart<V> anchored : filterContentParts(getAnchoreds())) {
				removeAnchored(anchored);
			}
		}
		super.setParent(parent);
		if (parent != null) {
			refreshVisual();
			// create content children as needed
			synchronizeContentChildren();
			// create content anchored as needed
			synchronizeContentAnchored();
		}
	}

	/**
	 * Create the child <code>EditPart</code> for the given model object. This
	 * method is called from {@link #synchronizeContentChildren()}.
	 * <P>
	 * By default, the implementation will delegate to the
	 * <code>EditPartViewer</code>'s {@link IContentPartFactory}. Subclasses may
	 * override this method instead of using a Factory.
	 * 
	 * @param model
	 *            the Child model object
	 * @return The child EditPart
	 */
	protected IContentPart<V> findOrCreatePartFor(Object model) {
		if (getViewer().getContentPartMap().containsKey(model)) {
			return getViewer().getContentPartMap().get(model);
		} else {
			IContentPart<V> contentPart = getViewer().getContentPartFactory()
					.createChildContentPart(this, model);
			contentPart.setModel(model);
			getViewer().getContentPartMap().put(model, contentPart);
			return contentPart;
		}
	}

	@Override
	public void removeChild(IVisualPart<V> child) {
		super.removeChild(child);
		if (child instanceof IContentPart) {
			disposeIfObsolete((IContentPart<V>) child);
		}
	}

	@Override
	public void removeAnchored(IVisualPart<V> anchored) {
		super.removeAnchored(anchored);
		if (anchored instanceof IContentPart) {
			disposeIfObsolete((IContentPart<V>) anchored);
		}
	}

	protected void disposeIfObsolete(IContentPart<V> contentPart) {
		if (contentPart.getParent() == null
				&& contentPart.getAnchorages().isEmpty()) {
			getViewer().getContentPartMap().remove(contentPart.getModel());
			contentPart.setModel(null);
		}
	}

	protected void synchronizeContentAnchored() {
		int i;
		IContentPart<V> editPart;
		Object model;

		List<IContentPart<V>> anchored = filterContentParts(getAnchoreds());
		int size = anchored.size();
		Map<Object, IContentPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IContentPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (IContentPart<V>) anchored.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		List<Object> modelObjects = getModelAnchored();
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < anchored.size() && anchored.get(i).getModel() == model)
				continue;

			// Look to see if the EditPart is already around but in the
			// wrong location
			editPart = (IContentPart<V>) modelToEditPart.get(model);

			if (editPart != null) {
				// TODO: this is wrong, it has to take into consideration the
				// visual parts in between
				// reorderChild(editPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				editPart = findOrCreatePartFor(model);
				// what if it does not exist??
				addAnchored(editPart);
			}
		}

		// remove the remaining EditParts
		size = anchored.size();
		if (i < size) {
			List<IContentPart<V>> trash = new ArrayList<IContentPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(anchored.get(i));
			for (i = 0; i < trash.size(); i++) {
				IContentPart<V> ep = trash.get(i);
				removeAnchored(ep);
			}
		}
	}

}
