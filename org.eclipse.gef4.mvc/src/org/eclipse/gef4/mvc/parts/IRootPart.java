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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.RootEditPart
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * A {@link IRootPart} is the <i>root</i> controller of an
 * {@link IViewer}. It controls the root view and holds
 * {@link IHandlePart} and {@link IContentPart} children.
 * 
 * The {@link IRootPart} does not correspond to anything in the model, and
 * typically can not be interacted with by the User. The Root provides a
 * homogeneous context for the applications "real" {@link IVisualPart}.
 * 
 * @author anyssen
 * 
 */
public interface IRootPart<VR> extends IVisualPart<VR> {

	public List<IContentPart<VR>> getContentPartChildren();

	public List<IHandlePart<VR>> getHandlePartChildren();
	
	// TODO: add feedback part children
	
	/**
	 * Returns the {@link IViewer} this {@link IViewerBound} is bound to.
	 * 
	 * @return The {@link IViewer} this {@link IRootPart} is
	 *         attached to.
	 */
	public abstract IViewer<VR> getViewer();

	/**
	 * Sets the {@link IViewer} this {@link IViewerBound} is to be bound to.
	 * 
	 * @param viewer
	 *            the {@link IViewer} this {@link IViewerBound} should be
	 *            attached to.
	 */
	public abstract void setViewer(IViewer<VR> viewer);
}
