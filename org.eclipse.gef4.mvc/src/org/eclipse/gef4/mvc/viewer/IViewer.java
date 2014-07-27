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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPartViewer.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.viewer;

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IViewportModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractViewer} should be subclassed.
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public interface IViewer<VR> extends IAdaptable, IActivatable {

	/**
	 * Returns the {@link Map} for registering {@link IContentPart}s by their
	 * <i>content</i>.
	 * 
	 * @return The content part map
	 */
	public Map<Object, IContentPart<VR>> getContentPartMap();

	/**
	 * Returns the {@link IRootPart} of this viewer. The {@link IRootPart} is a
	 * special {@link IVisualPart} that serves as the parent to all contained
	 * {@link IContentPart}s, {@link IHandlePart}s, and {@link IFeedbackPart}s.
	 * 
	 * @return The {@link IRootPart} of this viewer.
	 */
	public IRootPart<VR> getRootPart();

	/**
	 * Returns the {@link Map} for registering {@link IVisualPart}s by their
	 * <i>visual</i>. This map is used for hit-testing. Hit testing is performed
	 * by first determining which visual is hit, and then mapping that to an
	 * {@link IVisualPart}.
	 * 
	 * @return The visual part map
	 */
	public Map<VR, IVisualPart<VR>> getVisualPartMap();

	public List<Object> getContents();

	public void setContents(List<Object> contents);

	/**
	 * Returns the {@link IContentPartFactory} for this viewer, used to create
	 * {@link IContentPart}s.
	 * 
	 * @return The {@link IContentPartFactory} being used
	 */
	public IContentPartFactory<VR> getContentPartFactory();

	/**
	 * Returns the {@link IHandlePartFactory} for this viewer, used to create
	 * {@link IHandlePart}s.
	 * 
	 * @return The {@link IHandlePartFactory} being used
	 */
	public IHandlePartFactory<VR> getHandlePartFactory();

	/**
	 * Returns the {@link IFeedbackPartFactory} for this viewer, used to create
	 * {@link IFeedbackPart}s.
	 * 
	 * @return The {@link IFeedbackPartFactory} being used
	 */
	public IFeedbackPartFactory<VR> getFeedbackPartFactory();

	/**
	 * Returns the {@link IDomain} this {@link IViewer} is bound to.
	 * 
	 * @return The {@link IDomain} this {@link IViewer} is bound to, or
	 *         <code>null</code> if this {@link IViewer} is not (yet) bound to
	 *         an {@link IDomain}.
	 */
	public IDomain<VR> getDomain();

	// TODO: remove this and the following getters, as these are not mandatory;
	// replace them by getAdapter(ISelectionModel.class) calls.
	public ISelectionModel<VR> getSelectionModel();

	public IHoverModel<VR> getHoverModel();

	public IFocusModel<VR> getFocusModel();

	public IZoomModel getZoomModel();

	public IContentModel getContentModel();

	public IViewportModel getViewportModel();

}
