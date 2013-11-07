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


/**
 * The base implementation for {@link org.eclipse.gef4.mvc.parts.IConnectionEditPart}.
 */
public abstract class AbstractConnectionEditPart<V> extends
		AbstractEditPart<V> implements IConnectionEditPart<V> {

	private INodeEditPart<V> sourceEditPart, targetEditPart;

	/**
	 * @see org.eclipse.gef4.mvc.parts.IConnectionEditPart#getSource()
	 */
	public INodeEditPart<V> getSource() {
		return sourceEditPart;
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IConnectionEditPart#getTarget()
	 */
	public INodeEditPart<V> getTarget() {
		return targetEditPart;
	}
	

	/**
	 * Sets the source EditPart of this connection.
	 * 
	 * @param editPart
	 *            EditPart which is the source.
	 */
	public void setSource(INodeEditPart<V> editPart) {
		if (sourceEditPart == editPart)
			return;
		
		if(this.sourceEditPart != null){
			unregisterVisual();
			unregisterModel();
		}
		sourceEditPart = editPart;
		if (sourceEditPart != null && targetEditPart != null){
			registerModel();
			registerVisual();
			refreshVisual();
			synchronize();
		}
	}

	/**
	 * Sets the target EditPart of this connection.
	 * 
	 * @param editPart
	 *            EditPart which is the target.
	 */
	public void setTarget(INodeEditPart<V> editPart) {
		if (targetEditPart == editPart)
			return;
		if(targetEditPart != null){
			unregisterVisual();
			unregisterModel();
		}
		targetEditPart = editPart;
		if (sourceEditPart != null && targetEditPart != null){
			registerModel();
			registerVisual();
			refreshVisual();
			synchronize();
		}
	}

}
