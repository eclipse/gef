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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.DefaultContentModel;
import org.eclipse.gef4.mvc.models.DefaultFocusModel;
import org.eclipse.gef4.mvc.models.DefaultHoverModel;
import org.eclipse.gef4.mvc.models.DefaultSelectionModel;
import org.eclipse.gef4.mvc.models.DefaultZoomModel;
import org.eclipse.gef4.mvc.models.IContentModel;
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

	private Map<Object, IContentPart<V>> contentsToContentPartMap = new HashMap<Object, IContentPart<V>>();
	private Map<V, IVisualPart<V>> visualsToVisualPartMap = new HashMap<V, IVisualPart<V>>();

	private IDomain<V> domain;
	private IRootPart<V> rootPart;

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
	 * @see IVisualPartViewer#getContentModel()
	 */
	public IContentModel getContentModel() {
		IContentModel contentModel = getDomain().getProperty(
				IContentModel.class);
		if (contentModel == null) {
			contentModel = new DefaultContentModel();
			getDomain().setProperty(IContentModel.class, contentModel);
		}
		return contentModel;
	}

	/**
	 * @see IVisualPartViewer#getDomain()
	 */
	public IDomain<V> getDomain() {
		return domain;
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

	@Override
	public List<Object> getContents() {
		return getContentModel().getContents();
	}

	/**
	 * @see IVisualPartViewer#setContents(Object)
	 */
	public void setContents(List<Object> contents) {
		if (contentPartFactory == null) {
			throw new IllegalStateException(
					"ContentPartFactory has to be set before passing contents in.");
		}
		if (rootPart == null) {
			throw new IllegalStateException(
					"Root part has to be set before passing contents in.");
		}
		getContentModel().setContents(contents);
	}

	/**
	 * @see IVisualPartViewer#setDomain(IDomain)
	 */
	public void setDomain(IDomain<V> domain) {
		if (this.domain == domain)
			return;
		if (this.domain != null) {
			this.domain.setViewer(null);
			if (rootPart != null && rootPart.isActive()) {
				rootPart.deactivate();
			}
		}
		this.domain = domain;
		if (this.domain != null) {
			this.domain.setViewer(this);
			if (rootPart != null && !rootPart.isActive()) {
				rootPart.activate();
			}
		}
	}

	@Override
	public ISelectionModel<V> getSelectionModel() {
		@SuppressWarnings("unchecked")
		ISelectionModel<V> selectionModel = getDomain().getProperty(
				ISelectionModel.class);
		if (selectionModel == null) {
			selectionModel = new DefaultSelectionModel<V>();
			getDomain().setProperty(ISelectionModel.class, selectionModel);
		}
		return selectionModel;
	}

	@Override
	public IHoverModel<V> getHoverModel() {
		@SuppressWarnings("unchecked")
		IHoverModel<V> hoverModel = getDomain().getProperty(IHoverModel.class);
		if (hoverModel == null) {
			hoverModel = new DefaultHoverModel<V>();
			getDomain().setProperty(IHoverModel.class, hoverModel);
		}
		return hoverModel;
	}

	@Override
	public IZoomModel getZoomModel() {
		IZoomModel zoomModel = getDomain().getProperty(IZoomModel.class);
		if (zoomModel == null) {
			zoomModel = new DefaultZoomModel();
			getDomain().setProperty(IZoomModel.class, zoomModel);
		}
		return zoomModel;
	}

	/**
	 * @see IVisualPartViewer#setRootPart(IRootPart)
	 */
	public void setRootPart(IRootPart<V> rootEditPart) {
		if (this.rootPart != null) {
			if (domain != null) {
				this.rootPart.deactivate();
			}
			this.rootPart.setViewer(null);
		}
		this.rootPart = rootEditPart;
		if (this.rootPart != null) {
			this.rootPart.setViewer(this);
			if (domain != null) {
				this.rootPart.activate();
			}
		}
	}

	@Override
	public IFocusModel<V> getFocusModel() {
		@SuppressWarnings("unchecked")
		IFocusModel<V> focusModel = getDomain().getProperty(IFocusModel.class);
		if (focusModel == null) {
			focusModel = new DefaultFocusModel<V>();
			getDomain().setProperty(IFocusModel.class, focusModel);
		}
		return focusModel;
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
