/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.RootEditPart
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;

import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * A {@link IRootPart} is the <i>root</i> controller of an {@link IViewer}. It
 * controls the root view and holds {@link IHandlePart} and {@link IContentPart}
 * children.
 *
 * The {@link IRootPart} does not correspond to anything in the model, and
 * typically can not be interacted with by the User. The Root provides a
 * homogeneous context for the applications "real" {@link IVisualPart}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link IRootPart}.
 *
 */
public interface IRootPart<V extends Node> extends IVisualPart<V> {

	/**
	 * Returns all children of type {@link IContentPart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IContentPart} children.
	 */
	public List<IContentPart<? extends Node>> getContentPartChildren();

	/**
	 * Returns all children of type {@link IFeedbackPart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IFeedbackPart} children.
	 */
	public List<IFeedbackPart<? extends Node>> getFeedbackPartChildren();

	/**
	 * Returns all children of type {@link IHandlePart} contained by this
	 * {@link IRootPart}.
	 *
	 * @return A list containing all {@link IHandlePart} children.
	 */
	public List<IHandlePart<? extends Node>> getHandlePartChildren();

}
