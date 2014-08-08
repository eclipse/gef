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

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * A {@link IRootPart} is the <i>root</i> controller of an {@link IViewer}. It
 * controls the root view and holds {@link IHandlePart} and {@link IContentPart}
 * children.
 *
 * The {@link IRootPart} does not correspond to anything in the model, and
 * typically can not be interacted with by the User. The Root provides a
 * homogeneous context for the applications "real" {@link IVisualPart}.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractRootPart} should be sub-classed.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 *
 *
 */
public interface IRootPart<VR> extends IVisualPart<VR>,
IAdaptable.Bound<IViewer<VR>> {

	/**
	 * Returns all children of type {@link IContentPart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IContentPart} children.
	 */
	public List<IContentPart<VR>> getContentPartChildren();

	/**
	 * Returns all children of type {@link IFeedbackPart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IFeedbackPart} children.
	 */
	public List<IFeedbackPart<VR>> getFeedbackPartChildren();

	/**
	 * Returns all children of type {@link IHandlePart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IHandlePart} children.
	 */
	public List<IHandlePart<VR>> getHandlePartChildren();

	/**
	 * Returns the {@link IViewer} this {@link IRootPart} is bound to.
	 *
	 * @return The {@link IViewer} this {@link IRootPart} is attached to.
	 */
	public IViewer<VR> getViewer();

}
