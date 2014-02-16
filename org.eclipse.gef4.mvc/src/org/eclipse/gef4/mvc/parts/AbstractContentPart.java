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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editparts.AbstractEditPart and org.eclipse.gef.editparts.AbstractGraphicalEditPart.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.ContentPartSynchronizationBehavior;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public abstract class AbstractContentPart<V> extends AbstractVisualPart<V>
		implements IContentPart<V> {

	private Object content;

	public AbstractContentPart() {
		installBound(new ContentPartSynchronizationBehavior<V>());
	}

	/**
	 * @see org.eclipse.gef4.mvc.parts.IEditPart#getContent()
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * Set the primary model object that this EditPart represents. This method
	 * is used by an <code>EditPartFactory</code> when creating an EditPart.
	 * 
	 * @see IEditPart#setContent(Object)
	 */
	public void setContent(Object content) {
		if (this.content == content) {
			return;
		}
		Object oldContent = this.content;
		this.content = content;
		pcs.firePropertyChange(PARENT_PROPERTY, oldContent, content);
	}

	/**
	 * Registers the <i>model</i> in the
	 * {@link IVisualPartViewer#getContentPartMap()}. Subclasses should only
	 * extend this method if they need to register this EditPart in additional
	 * ways.
	 */
	protected void registerAtContentPartMap() {
		getViewer().getContentPartMap().put(getContent(), this);
	}

	/**
	 * Unregisters the <i>model</i> in the
	 * {@link IVisualPartViewer#getContentPartMap()}. Subclasses should only
	 * extend this method if they need to unregister this EditPart in additional
	 * ways.
	 */
	protected void unregisterFromContentPartMap() {
		Map<Object, IContentPart<V>> registry = getViewer().getContentPartMap();
		if (registry.get(getContent()) == this)
			registry.remove(getContent());
	}

	@Override
	public List<Object> getContentChildren() {
		return Collections.emptyList();
	}

	@Override
	public List<Object> getContentAnchored() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(IVisualPart<V> parent) {
		if (getParent() == parent) {
			return;
		}
		if (parent == null) {
			// remove all content children
			getBound(ContentPartSynchronizationBehavior.class)
					.synchronizeContentChildren(Collections.emptyList());
			// create content anchored as needed
			getBound(ContentPartSynchronizationBehavior.class)
					.synchronizeContentAnchored(Collections.emptyList());
		}
		super.setParent(parent);
		if (parent != null) {
			refreshVisual();
			// TODO: check if we can move this to the policy's activation (or
			// listen for the PARENT property of the host to change, rather than
			// accessing the policy directly here
			// create content children as needed
			getBound(ContentPartSynchronizationBehavior.class)
					.synchronizeContentChildren(getContentChildren());
			// create content anchored as needed
			getBound(ContentPartSynchronizationBehavior.class)
					.synchronizeContentAnchored(getContentAnchored());
		}
	}

}
