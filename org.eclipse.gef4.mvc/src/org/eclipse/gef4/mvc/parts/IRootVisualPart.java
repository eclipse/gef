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
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

/**
 * A RootEditPart is the <i>root</i> of an EditPartViewer. It bridges the gap
 * between the EditPartViewer and its {@link IVisualPartViewer#getContents()
 * contents}. It does not correspond to anything in the model, and typically can
 * not be interacted with by the User. The Root provides a homogeneous context
 * for the applications "real" EditParts.
 */
public interface IRootVisualPart<V> extends IVisualPart<V> {

	/**
	 * Returns the root's EditPartViewer.
	 * 
	 * @return The <code>EditPartViewer</code>
	 */
	IVisualPartViewer<V> getViewer();

	/**
	 * Sets the root's EditPartViewer.
	 * 
	 * @param viewer
	 *            the EditPartViewer
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

//	void addContentPart(IContentPart<V> contents);
//
//	void removeContentPart(IContentPart<V> contents);

	
	public void addHandleParts(List<IHandlePart<V>> handleParts);

	public void removeHandleParts(List<IHandlePart<V>> handleParts);
	
	public List<IHandlePart<V>> getHandleParts();

}
