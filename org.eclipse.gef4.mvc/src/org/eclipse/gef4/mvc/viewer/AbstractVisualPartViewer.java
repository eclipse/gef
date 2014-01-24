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
package org.eclipse.gef4.mvc.viewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.DefaultSelectionModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The base implementation for EditPartViewer.
 * 
 * @author hudsonr
 */
public abstract class AbstractVisualPartViewer<V> implements
		IVisualPartViewer<V> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	
	private Map<Object, IContentPart<V>> contentsToContentPartMap = new HashMap<Object, IContentPart<V>>();
	private Map<V, IVisualPart<V>> visualsToVisualPartMap = new HashMap<V, IVisualPart<V>>();

	private IEditDomain<V> editDomain;
	private IRootVisualPart<V> rootPart;
	private IContentPartFactory<V> contentPartFactory;
	private ISelectionModel<V> contentPartSelection;
	
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
	 * Constructs the viewer and calls {@link #init()}.
	 */
	public AbstractVisualPartViewer() {
		setContentPartSelection(new DefaultSelectionModel<V>());
	}

	/**
	 * @see IVisualPartViewer#getContents()
	 */
	public Object getContents() {
		IRootVisualPart<V> rootPart = getRootPart();
		if (rootPart == null) {
			return null;
		}
		IContentPart<V> contentRoot = rootPart.getRootContentPart();
		if (contentRoot == null) {
			return null;
		}
		return contentRoot.getModel();
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
	public IRootVisualPart<V> getRootPart() {
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
		if(contentPartFactory == null){
			throw new IllegalStateException("ContentPartFactory has to be set before passing contents in.");
		}
		if(rootPart == null){
			throw new IllegalStateException("Root part has to be set before passing contents in.");
		}
		IContentPart<V> rootContentPart = contentPartFactory.createRootContentPart(rootPart, contents);
		rootContentPart.setModel(contents);
		rootPart.setRootContentPart(rootContentPart);
		propertyChangeSupport.firePropertyChange(CONTENTS_PROPERTY, oldContents, contents);
	}

	/**
	 * @see IVisualPartViewer#setDomain(EditDomain)
	 */
	public void setEditDomain(IEditDomain<V> editdomain) {
		if (editDomain == editdomain)
			return;
		if (editDomain != null) {
			editDomain.setViewer(null);
		}
		this.editDomain = editdomain;
		if (editDomain != null) {
			editDomain.setViewer(this);
		}
	}

	@Override
	public ISelectionModel<V> getContentPartSelection() {
		return contentPartSelection;
	}

	@Override
	public void setContentPartSelection(
			ISelectionModel<V> contentPartSelection) {
		if (this.contentPartSelection == contentPartSelection) {
			return;
		}
		// TODO: if tools may register on selection, we need to deactivate/activate them here
		this.contentPartSelection = contentPartSelection;
	}

	/**
	 * @see IVisualPartViewer#setRootPart(IRootVisualPart)
	 */
	public void setRootPart(IRootVisualPart<V> rootEditPart) {
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
