package org.eclipse.gef4.mvc.policies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.PartUtilities;

public class ContentPartSynchronizationPolicy<V> extends AbstractPolicy<V> implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		if(getHost() == getHost().getRoot()){
			getHost().getRoot().getViewer().getContentModel().addPropertyChangeListener(this);;
		}
		else {
			getHost().addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void deactivate() {
		if(getHost() == getHost().getRoot()){
			getHost().getRoot().getViewer().getContentModel().removePropertyChangeListener(this);;
		}
		else {
			getHost().removePropertyChangeListener(this);
		}
		super.deactivate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(IContentModel.CONTENTS_PROPERTY.equals(event.getPropertyName())){
			synchronizeContentChildren((List<Object>)event.getNewValue());
		}
		else if (IContentPart.CONTENT_PROPERTY.equals(event.getPropertyName())){
			synchronizeContentChildren(((IContentPart<V>)getHost()).getContentChildren());
			synchronizeContentAnchored(((IContentPart<V>)getHost()).getContentAnchored());
		}
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
	 * @see #getContentChildren()
	 */
	@SuppressWarnings("unchecked")
	public void synchronizeContentChildren(List<Object> contentChildren) {
		// only synchronize ContentPart children

		int i;
		IContentPart<V> editPart;
		Object model;

		@SuppressWarnings("unchecked")
		List<IContentPart<V>> contentPartChildren = PartUtilities.filterParts(
				getHost().getChildren(), IContentPart.class);
		int size = contentPartChildren.size();
		Map<Object, IContentPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IContentPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (IContentPart<V>) contentPartChildren.get(i);
				modelToEditPart.put(editPart.getContent(), editPart);
			}
		}

		List<Object> modelObjects = contentChildren;
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < size && contentPartChildren.get(i).getContent() == model)
				continue;

			// Look to see if the EditPart is already around but in the
			// wrong location
			editPart = (IContentPart<V>) modelToEditPart.get(model);

			if (editPart != null) {
				// TODO: this is wrong, it has to take into consideration the
				// visual parts in between
				getHost().reorderChild(editPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				editPart = findOrCreatePartFor(model);
				System.out.println(getHost() + "Adding child for content " + editPart.getContent());
				getHost().addChild(editPart, i);
			}
		}

		// remove the remaining EditParts
		contentPartChildren = PartUtilities.filterParts(
				getHost().getChildren(), IContentPart.class);
		size = contentPartChildren.size();
		if (i < size) {
			List<IContentPart<V>> trash = new ArrayList<IContentPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(contentPartChildren.get(i));
			for (i = 0; i < trash.size(); i++) {
				IContentPart<V> ep = trash.get(i);
				System.out.println(getHost() + " Removing child for content " + ep.getContent());
				getHost().removeChild(ep);
				disposeIfObsolete(ep);
			}
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
		Map<Object, IContentPart<V>> contentPartMap = getHost().getRoot().getViewer().getContentPartMap();
		if (contentPartMap.containsKey(model)) {
			return contentPartMap.get(model);
		} else {
			IContentPartFactory<V> contentPartFactory = getHost().getRoot().getViewer().getContentPartFactory();
			IContentPart<V> contentPart = getHost() == getHost().getRoot() ? contentPartFactory
					.createRootContentPart((IRootPart<V>) getHost(), model) : contentPartFactory.createChildContentPart((IContentPart<V>) getHost(), model);
			contentPart.setContent(model);
			contentPartMap.put(model, contentPart);
			return contentPart;
		}
	}

	
	@SuppressWarnings("unchecked")
	public void synchronizeContentAnchored(List<Object> contentAnchored) {
		int i;
		IContentPart<V> editPart;
		Object model;

		@SuppressWarnings("unchecked")
		List<IContentPart<V>> anchored = PartUtilities.filterParts(
				getHost().getAnchoreds(), IContentPart.class);
		int size = anchored.size();
		Map<Object, IContentPart<V>> modelToEditPart = Collections.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IContentPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = (IContentPart<V>) anchored.get(i);
				modelToEditPart.put(editPart.getContent(), editPart);
			}
		}

		List<Object> modelObjects = contentAnchored;
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < anchored.size() && anchored.get(i).getContent() == model)
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
				getHost().addAnchored(editPart);
			}
		}

		// remove the remaining EditParts
		anchored = PartUtilities.filterParts(
				getHost().getAnchoreds(), IContentPart.class);
		size = anchored.size();
		if (i < size) {
			List<IContentPart<V>> trash = new ArrayList<IContentPart<V>>(size
					- i);
			for (; i < size; i++)
				trash.add(anchored.get(i));
			for (i = 0; i < trash.size(); i++) {
				IContentPart<V> ep = trash.get(i);
				getHost().removeAnchored(ep);
				disposeIfObsolete(ep);
			}
		}
	}
	
	protected void disposeIfObsolete(IContentPart<V> contentPart) {
		if (contentPart.getParent() == null
				&& contentPart.getAnchorages().isEmpty()) {
			getHost().getRoot().getViewer().getContentPartMap().remove(contentPart.getContent());
			contentPart.setContent(null);
		}
	}
}
