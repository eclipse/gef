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

import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * A {@link IRootPart} is the <i>root</i> controller of an
 * {@link IVisualPartViewer}. It controls the root view and holds
 * {@link IHandlePart} and {@link IContentPart} children.
 * 
 * The {@link IRootPart} does not correspond to anything in the model, and
 * typically can not be interacted with by the User. The Root provides a
 * homogeneous context for the applications "real" {@link IVisualPart}.
 * 
 * @author anyssen
 * 
 */
public interface IRootPart<V> extends IVisualPart<V> {

	/**
	 * Returns the root's {@link IVisualPartViewer}.
	 * 
	 * @return The {@link IVisualPartViewer} this {@link IRootPart} is
	 *         attached to.
	 */
	IVisualPartViewer<V> getViewer();

	/**
	 * Sets the root's {@link IVisualPartViewer}.
	 * 
	 * @param viewer
	 *            the {@link IVisualPartViewer} this {@link IRootPart} should be
	 *            attached to.
	 */
	void setViewer(IVisualPartViewer<V> viewer);

	// TODO: support multiple content parts
	/**
	 * Sets the <i>contents</i> EditPart. A RootEditPart only has a single
	 * child, called its <i>contents</i>.
	 * 
	 * @param editpart
	 *            the contents
	 */
	void setRootContentPart(IContentPart<V> contents);

	/**
	 * Returns the <i>contents</i> EditPart. A RootEditPart only has a single
	 * child, called its <i>contents</i>.
	 * 
	 * @return the contents.
	 */
	IContentPart<V> getRootContentPart();

	// void addContentPart(IContentPart<V> contents);
	//
	// void removeContentPart(IContentPart<V> contents);

	public void addHandleParts(List<IHandlePart<V>> handleParts);

	public void removeHandleParts(List<IHandlePart<V>> handleParts);

	public List<IHandlePart<V>> getHandleParts();

}
