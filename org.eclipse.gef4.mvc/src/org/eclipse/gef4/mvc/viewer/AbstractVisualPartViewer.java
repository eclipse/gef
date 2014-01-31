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
 * Note: Parts of this class have been transferred from org.eclipse.gef.ui.parts.AbstractEditPartViewer.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.viewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.DefaultFocusModel;
import org.eclipse.gef4.mvc.models.DefaultHoverModel;
import org.eclipse.gef4.mvc.models.DefaultSelectionModel;
import org.eclipse.gef4.mvc.models.DefaultZoomModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractVisualPartViewer<V> implements
		IVisualPartViewer<V> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private Map<Object, IContentPart<V>> contentsToContentPartMap = new HashMap<Object, IContentPart<V>>();
	private Map<V, IVisualPart<V>> visualsToVisualPartMap = new HashMap<V, IVisualPart<V>>();

	private IEditDomain<V> editDomain;
	private IRootPart<V> rootPart;

	// TODO: Use dependency injection to bind default implementations.
	private ISelectionModel<V> selectionModel;
	private IZoomModel zoomModel;
	private IHoverModel<V> hoverModel;
	private IFocusModel<V> focusModel;

	private IContentPartFactory<V> contentPartFactory;
	private IHandlePartFactory<V> handlePartFactory;

	/**
	 * @see IVisualPartViewer#setContentPartFactory(org.eclipse.gef4.mvc.viewer.IContentPartFactory)
	 */
	public void setContentPartFactory(IContentPartFactory<V> factory) {
		this.contentPartFactory = factory;
	}

	/**
	 * @see IVisualPartViewer#getContentPartFactory()
	 */
	public IContentPartFactory<V> getContentPartFactory() {
		return contentPartFactory;
	}

	/**
	 * @see IVisualPartViewer#getContents()
	 */
	public Object getContents() {
		IRootPart<V> rootPart = getRootPart();
		if (rootPart == null) {
			return null;
		}
		IContentPart<V> contentRoot = rootPart.getRootContentPart();
		if (contentRoot == null) {
			return null;
		}
		return contentRoot.getContent();
	}

	/**
	 * @see IVisualPartViewer#getEditDomain()
	 */
	public IEditDomain<V> getEditDomain() {
		return editDomain;
	}

	/**
	 * @see IVisualPartViewer#getContentPartMap()
	 */
	public Map<Object, IContentPart<V>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	/**
	 * @see IVisualPartViewer#getRootPart()
	 */
	public IRootPart<V> getRootPart() {
		return rootPart;
	}

	/**
	 * @see IVisualPartViewer#getVisualPartMap()
	 */
	public Map<V, IVisualPart<V>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	/**
	 * @see IVisualPartViewer#setContents(Object)
	 */
	public void setContents(Object contents) {
		Object oldContents = getContents();
		if (contentPartFactory == null) {
			throw new IllegalStateException(
					"ContentPartFactory has to be set before passing contents in.");
		}
		if (rootPart == null) {
			throw new IllegalStateException(
					"Root part has to be set before passing contents in.");
		}
		IContentPart<V> rootContentPart = contentPartFactory
				.createRootContentPart(rootPart, contents);
		rootContentPart.setContent(contents);
		rootPart.setRootContentPart(rootContentPart);
		propertyChangeSupport.firePropertyChange(CONTENTS_PROPERTY,
				oldContents, contents);
	}

	/**
	 * @see IVisualPartViewer#setDomain(EditDomain)
	 */
	public void setEditDomain(IEditDomain<V> editdomain) {
		if (editDomain == editdomain)
			return;
		if (editDomain != null) {
			editDomain.setProperty(ISelectionModel.class, null);
			editDomain.setProperty(IHoverModel.class, null);
			editDomain.setProperty(IZoomModel.class, null);
			editDomain.setProperty(IFocusModel.class, null);
			editDomain.setViewer(null);
		}
		this.editDomain = editdomain;
		if (editDomain != null) {
			editDomain.setViewer(this);
			editDomain.setProperty(ISelectionModel.class, getSelectionModel());
			editDomain.setProperty(IHoverModel.class, getHoverModel());
			editDomain.setProperty(IZoomModel.class, getZoomModel());
			editDomain.setProperty(IFocusModel.class, getFocusModel());
		}
	}

	@Override
	public ISelectionModel<V> getSelectionModel() {
		if (selectionModel == null) {
			// TODO: use dependency injection to bind this
			selectionModel = new DefaultSelectionModel<V>();
		}
		return selectionModel;
	}

	@Override
	public IHoverModel<V> getHoverModel() {
		if (hoverModel == null) {
			// TODO: use dependency injection to bind this
			hoverModel = new DefaultHoverModel<V>();
		}
		return hoverModel;
	}

	@Override
	public IZoomModel getZoomModel() {
		if (zoomModel == null) {
			// TODO: use dependency injection to bind this
			zoomModel = new DefaultZoomModel();
		}
		return zoomModel;
	}

	/**
	 * @see IVisualPartViewer#setRootPart(IRootPart)
	 */
	public void setRootPart(IRootPart<V> rootEditPart) {
		if (this.rootPart != null) {
			this.rootPart.deactivate();
			this.rootPart.setViewer(null);
		}
		this.rootPart = rootEditPart;
		if (this.rootPart != null) {
			this.rootPart.setViewer(this);
			this.rootPart.activate();
		}
	}
	
	@Override
	public IFocusModel<V> getFocusModel() {
		if (focusModel == null) {
			// TODO: use dependency injection to bind this
			focusModel = new DefaultFocusModel<V>();
		}
		return focusModel;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public IHandlePartFactory<V> getHandlePartFactory() {
		return handlePartFactory;
	}

	@Override
	public void setHandlePartFactory(IHandlePartFactory<V> factory) {
		this.handlePartFactory = factory;
	}

}
