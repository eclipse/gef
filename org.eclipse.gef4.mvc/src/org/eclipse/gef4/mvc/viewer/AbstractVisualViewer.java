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
import org.eclipse.gef4.mvc.models.DefaultViewportModel;
import org.eclipse.gef4.mvc.models.DefaultZoomModel;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IViewportModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractVisualViewer<VR> implements
		IVisualViewer<VR> {

	private Map<Object, IContentPart<VR>> contentsToContentPartMap = new HashMap<Object, IContentPart<VR>>();
	private Map<VR, IVisualPart<VR>> visualsToVisualPartMap = new HashMap<VR, IVisualPart<VR>>();

	private IDomain<VR> domain;
	private IRootPart<VR> rootPart;

	private IContentPartFactory<VR> contentPartFactory;
	private IHandlePartFactory<VR> handlePartFactory;
	private IFeedbackPartFactory<VR> feedbackPartFactory;

	/**
	 * @see IVisualViewer#setContentPartFactory(IContentPartFactory)
	 */
	public void setContentPartFactory(IContentPartFactory<VR> factory) {
		this.contentPartFactory = factory;
	}

	/**
	 * @see IVisualViewer#getContentPartFactory()
	 */
	public IContentPartFactory<VR> getContentPartFactory() {
		return contentPartFactory;
	}

	/**
	 * @see IVisualViewer#getContentModel()
	 */
	public IContentModel getContentModel() {
		IContentModel contentModel = getDomain().getAdapter(
				IContentModel.class);
		if (contentModel == null) {
			contentModel = new DefaultContentModel();
			getDomain().setAdapter(IContentModel.class, contentModel);
		}
		return contentModel;
	}

	/**
	 * @see IVisualViewer#getDomain()
	 */
	public IDomain<VR> getDomain() {
		return domain;
	}

	/**
	 * @see IVisualViewer#getContentPartMap()
	 */
	public Map<Object, IContentPart<VR>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	/**
	 * @see IVisualViewer#getRootPart()
	 */
	public IRootPart<VR> getRootPart() {
		return rootPart;
	}

	/**
	 * @see IVisualViewer#getVisualPartMap()
	 */
	public Map<VR, IVisualPart<VR>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	@Override
	public List<Object> getContents() {
		return getContentModel().getContents();
	}

	/**
	 * @see IVisualViewer#setContents(List)
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
	 * @see IVisualViewer#setDomain(IDomain)
	 */
	public void setDomain(IDomain<VR> domain) {
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
	public ISelectionModel<VR> getSelectionModel() {
		@SuppressWarnings("unchecked")
		ISelectionModel<VR> selectionModel = getDomain().getAdapter(
				ISelectionModel.class);
		if (selectionModel == null) {
			selectionModel = new DefaultSelectionModel<VR>();
			getDomain().setAdapter(ISelectionModel.class, selectionModel);
		}
		return selectionModel;
	}

	@Override
	public IHoverModel<VR> getHoverModel() {
		@SuppressWarnings("unchecked")
		IHoverModel<VR> hoverModel = getDomain().getAdapter(IHoverModel.class);
		if (hoverModel == null) {
			hoverModel = new DefaultHoverModel<VR>();
			getDomain().setAdapter(IHoverModel.class, hoverModel);
		}
		return hoverModel;
	}

	@Override
	public IZoomModel getZoomModel() {
		IZoomModel zoomModel = getDomain().getAdapter(IZoomModel.class);
		if (zoomModel == null) {
			zoomModel = new DefaultZoomModel();
			getDomain().setAdapter(IZoomModel.class, zoomModel);
		}
		return zoomModel;
	}

	/**
	 * @see IVisualViewer#setRootPart(IRootPart)
	 */
	public void setRootPart(IRootPart<VR> rootEditPart) {
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
	public IFocusModel<VR> getFocusModel() {
		@SuppressWarnings("unchecked")
		IFocusModel<VR> focusModel = getDomain().getAdapter(IFocusModel.class);
		if (focusModel == null) {
			focusModel = new DefaultFocusModel<VR>();
			getDomain().setAdapter(IFocusModel.class, focusModel);
		}
		return focusModel;
	}
	
	@Override
	public IViewportModel getViewportModel() {
		IViewportModel viewportModel = getDomain().getAdapter(IViewportModel.class);
		if (viewportModel == null) {
			viewportModel = new DefaultViewportModel();
			getDomain().setAdapter(IViewportModel.class, viewportModel);
		}
		return viewportModel;
	}

	@Override
	public IHandlePartFactory<VR> getHandlePartFactory() {
		return handlePartFactory;
	}

	@Override
	public void setHandlePartFactory(IHandlePartFactory<VR> factory) {
		this.handlePartFactory = factory;
	}
	
	@Override
	public IFeedbackPartFactory<VR> getFeedbackPartFactory() {
		return feedbackPartFactory;
	}

	@Override
	public void setFeedbackPartFactory(IFeedbackPartFactory<VR> factory) {
		this.feedbackPartFactory = factory;
	}

}
