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
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdapterMap;
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

import com.google.inject.Inject;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractViewer<VR> implements IViewer<VR>,
		IAdaptable.Bound<IDomain<VR>> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private AdaptableSupport<IViewer<VR>> ads = new AdaptableSupport<IViewer<VR>>(
			this, pcs);

	private ActivatableSupport<IViewer<VR>> acs = new ActivatableSupport<IViewer<VR>>(
			this, pcs);

	private Map<Object, IContentPart<VR>> contentsToContentPartMap = new HashMap<Object, IContentPart<VR>>();
	private Map<VR, IVisualPart<VR>> visualsToVisualPartMap = new HashMap<VR, IVisualPart<VR>>();

	private IDomain<VR> domain;
	private IRootPart<VR> rootPart;

	private IContentPartFactory<VR> contentPartFactory;
	private IHandlePartFactory<VR> handlePartFactory;
	private IFeedbackPartFactory<VR> feedbackPartFactory;

	@Override
	public void activate() {
		if (!acs.isActive()) {
			if (domain == null) {
				throw new IllegalStateException(
						"Domain has to be set before activation.");
			}
			acs.activate();
			if (rootPart != null) {
				rootPart.activate();
			}
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void deactivate() {
		if (acs.isActive()) {
			if (domain == null) {
				throw new IllegalStateException(
						"Domain may not be unset before deactivation is completed.");
			}
			if (rootPart != null) {
				rootPart.deactivate();
			}
			acs.deactivate();
		}
	}

	@Override
	public IDomain<VR> getAdaptable() {
		return domain;
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<? super T> classKey) {
		return ads.getAdapter(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey) {
		return ads.getAdapters(classKey);
	}

	/**
	 * @see IViewer#getContentModel()
	 */
	@Override
	public IContentModel getContentModel() {
		IContentModel contentModel = getAdapter(AdapterKey
				.get(IContentModel.class));
		if (contentModel == null) {
			contentModel = new DefaultContentModel();
			setAdapter(AdapterKey.get(IContentModel.class), contentModel);
		}
		return contentModel;
	}

	/**
	 * @see IViewer#getContentPartFactory()
	 */
	@Override
	public IContentPartFactory<VR> getContentPartFactory() {
		return contentPartFactory;
	}

	/**
	 * @see IViewer#getContentPartMap()
	 */
	@Override
	public Map<Object, IContentPart<VR>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	@Override
	public List<? extends Object> getContents() {
		return getContentModel().getContents();
	}

	/**
	 * @see IViewer#getDomain()
	 */
	@Override
	public IDomain<VR> getDomain() {
		return domain;
	}

	@Override
	public IFeedbackPartFactory<VR> getFeedbackPartFactory() {
		return feedbackPartFactory;
	}

	@Override
	public IFocusModel<VR> getFocusModel() {
		@SuppressWarnings("unchecked")
		IFocusModel<VR> focusModel = getAdapter(AdapterKey
				.get(IFocusModel.class));
		if (focusModel == null) {
			focusModel = new DefaultFocusModel<VR>();
			setAdapter(AdapterKey.get(IFocusModel.class), focusModel);
		}
		return focusModel;
	}

	@Override
	public IHandlePartFactory<VR> getHandlePartFactory() {
		return handlePartFactory;
	}

	@Override
	public IHoverModel<VR> getHoverModel() {
		@SuppressWarnings("unchecked")
		IHoverModel<VR> hoverModel = getAdapter(AdapterKey
				.get(IHoverModel.class));
		if (hoverModel == null) {
			hoverModel = new DefaultHoverModel<VR>();
			setAdapter(AdapterKey.get(IHoverModel.class), hoverModel);
		}
		return hoverModel;
	}

	/**
	 * @see IViewer#getRootPart()
	 */
	@Override
	public IRootPart<VR> getRootPart() {
		return rootPart;
	}

	@Override
	public ISelectionModel<VR> getSelectionModel() {
		@SuppressWarnings("unchecked")
		ISelectionModel<VR> selectionModel = getAdapter(AdapterKey
				.get(ISelectionModel.class));
		if (selectionModel == null) {
			selectionModel = new DefaultSelectionModel<VR>();
			setAdapter(AdapterKey.get(ISelectionModel.class), selectionModel);
		}
		return selectionModel;
	}

	@Override
	public IViewportModel getViewportModel() {
		IViewportModel viewportModel = getAdapter(AdapterKey
				.get(IViewportModel.class));
		if (viewportModel == null) {
			viewportModel = new DefaultViewportModel();
			setAdapter(AdapterKey.get(IViewportModel.class), viewportModel);
		}
		return viewportModel;
	}

	/**
	 * @see IViewer#getVisualPartMap()
	 */
	@Override
	public Map<VR, IVisualPart<VR>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	@Override
	public IZoomModel getZoomModel() {
		IZoomModel zoomModel = getAdapter(AdapterKey.get(IZoomModel.class));
		if (zoomModel == null) {
			zoomModel = new DefaultZoomModel();
			setAdapter(AdapterKey.get(IZoomModel.class), zoomModel);
		}
		return zoomModel;
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setAdaptable(IDomain<VR> domain) {
		if (this.domain == domain)
			return;
		this.domain = domain;
	}

	@Override
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
		ads.setAdapter(key, adapter);
	}

	@Inject(optional = true)
	// IMPORTANT: if sub-classes override, they will have to transfer the inject
	// annotation.
	public void setAdapters(
			@AdapterMap Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractViewer) with those injected by Guice
		ads.setAdapters(adaptersWithKeys, false);
	}

	@Inject
	public void setContentPartFactory(IContentPartFactory<VR> factory) {
		this.contentPartFactory = factory;
	}

	/**
	 * @see IViewer#setContents(List)
	 */
	@Override
	public void setContents(List<? extends Object> contents) {
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

	@Inject
	public void setFeedbackPartFactory(IFeedbackPartFactory<VR> factory) {
		this.feedbackPartFactory = factory;
	}

	@Inject
	public void setHandlePartFactory(IHandlePartFactory<VR> factory) {
		this.handlePartFactory = factory;
	}

	@Inject
	public void setRootPart(IRootPart<VR> rootPart) {
		if(this.rootPart == rootPart){
			return;
		}
		if (this.rootPart != null) {
			if (isActive()) {
				this.rootPart.deactivate();
			}
			this.rootPart.setViewer(null);
		}
		this.rootPart = rootPart;
		if (this.rootPart != null) {
			this.rootPart.setViewer(this);
			if (isActive()) {
				this.rootPart.activate();
			}
		}
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<T> key) {
		return ads.unsetAdapter(key);
	}

}
