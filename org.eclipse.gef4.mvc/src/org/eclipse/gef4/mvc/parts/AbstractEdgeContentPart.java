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
 * The base implementation for
 * {@link org.eclipse.gef4.mvc.parts.IEdgeContentPart}.
 */
public abstract class AbstractEdgeContentPart<V> extends
		AbstractContentEditPart<V> implements IEdgeContentPart<V> {

	private INodeContentPart<V> source, target;
	
	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 * 
	 * @see IEditPart#setParent(IEditPart)
	 */
	@Override
	public void setParent(IVisualPart<V> parent) {
		super.setParent(parent);
		if (parent != null && source != null && target != null) {
			refreshVisual();
			synchronize();
		}
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEdgeContentPart#getSource()
	 */
	public INodeContentPart<V> getSource() {
		return source;
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEdgeContentPart#getTarget()
	 */
	public INodeContentPart<V> getTarget() {
		return target;
	}

	/**
	 * Sets the source EditPart of this connection.
	 * 
	 * @param source
	 *            EditPart which is the source.
	 */
	public void setSource(INodeContentPart<V> source) {
		if (this.source == source)
			return;

		this.source = source;
		if (this.source != null && target != null && getParent() != null) {
			refreshVisual();
			synchronize();
		}
	}

	/**
	 * Sets the target EditPart of this connection.
	 * 
	 * @param target
	 *            EditPart which is the target.
	 */
	public void setTarget(INodeContentPart<V> target) {
		if (this.target == target)
			return;
		this.target = target;
		if (source != null && this.target != null && getParent() != null) {
			refreshVisual();
			synchronize();
		}
	}

}
