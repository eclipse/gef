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

import org.eclipse.gef4.mvc.bindings.IAdaptable;
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
 * @author anyssen
 * 
 * @param <VR>
 */
public interface IViewer<VR> extends IAdaptable {

	/**
	 * Returns the {@link Map} for registering {@link IContentPart}s by
	 * their <i>content</i>.
	 * 
	 * @return The content part map
	 */
	Map<Object, IContentPart<VR>> getContentPartMap();

	/**
	 * Returns the {@link IRootPart} of this viewer. The {@link IRootPart} is a
	 * special {@link IVisualPart} that serves as the parent to all contained
	 * {@link IContentPart}s, {@link IHandlePart}s, and {@link IFeedbackPart}s.
	 * 
	 * @see #setRootPart(IRootPart)
	 * @return The root part
	 */
	IRootPart<VR> getRootPart();

	/**
	 * Returns the {@link Map} for associating {@link IVisualPart}s with their
	 * <code>visuals</code>. This map is used for hit-testing. Hit testing is
	 * performed by first determining which visual is hit, and then mapping that
	 * part to an {@link IVisualPart}.
	 * 
	 * @return The visual part map
	 */
	Map<VR, IVisualPart<VR>> getVisualPartMap();

	/**
	 * Sets the <i>root</i> of this viewer. The root should not be confused with
	 * the <i>contents</i>.
	 * 
	 * @param root
	 *            the RootEditPart
	 * @see #getRootPart()
	 * @see #getContents()
	 */
	// TODO: inject
	void setRootPart(IRootPart<VR> root);

	List<Object> getContents();

	void setContents(List<Object> contents);

	/**
	 * Returns the {@link IContentPartFactory} for this viewer, used to create
	 * {@link IContentPart}s.
	 * 
	 * @return The {@link IContentPartFactory} being used
	 */
	IContentPartFactory<VR> getContentPartFactory();

	/**
	 * Sets the {@link IContentPartFactory} used to create {@link IContentPart}
	 * s.
	 * 
	 * @param factory
	 *            the {@link IContentPartFactory} to be used
	 * @see #getContentPartFactory()
	 */
	void setContentPartFactory(IContentPartFactory<VR> factory);

	IHandlePartFactory<VR> getHandlePartFactory();

	void setHandlePartFactory(IHandlePartFactory<VR> factory);

	IFeedbackPartFactory<VR> getFeedbackPartFactory();

	void setFeedbackPartFactory(IFeedbackPartFactory<VR> factory);

	/**
	 * Returns the {@link IDomain} this {@link IDomainBound} is bound to.
	 * 
	 * @return The {@link IDomain} this {@link IDomainBound} is bound to, or
	 *         <code>null</code> if this {@link IDomainBound} is not (yet) bound
	 *         to an {@link IDomain}.
	 */
	public abstract IDomain<VR> getDomain();

	/**
	 * Called to set/change/unset the {@link IDomain} this {@link IDomainBound}
	 * is bound to. To set or change the {@link IDomain}, pass in a valid
	 * {@link IDomain}, to unset it, pass in <code>null</code>.
	 * 
	 * @param domain
	 *            The {@link IDomain} to which this {@link IDomainBound} is
	 *            bound to
	 */
	public abstract void setDomain(IDomain<VR> domain);
	
	// TODO: remove these by getAdapter(ISelectionModel.class)
	ISelectionModel<VR> getSelectionModel();

	IHoverModel<VR> getHoverModel();

	IFocusModel<VR> getFocusModel();

	IZoomModel getZoomModel();

	IContentModel getContentModel();

	IViewportModel getViewportModel();

}
