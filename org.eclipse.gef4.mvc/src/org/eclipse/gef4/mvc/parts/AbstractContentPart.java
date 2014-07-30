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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * The abstract base implementation of {@link IContentPart}, intended to be
 * sub-classed by clients to create their own custom {@link IContentPart}.
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractContentPart<VR> extends AbstractVisualPart<VR>
		implements IContentPart<VR> {

	private Object content;

	/**
	 * @see IContentPart#getContent()
	 */
	@Override
	public Object getContent() {
		return content;
	}

	@Override
	public Map<String, Set<? extends Object>> getContentAnchoragesByRole() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Object, Set<String>> getContentAnchoragesWithRoles() {
		Map<String, Set<? extends Object>> contentAnchoragesByRole = getContentAnchoragesByRole();
		Map<Object, Set<String>> contentAnchoragesWithRoles = new HashMap<Object, Set<String>>();

		for (String role : contentAnchoragesByRole.keySet()) {
			for (Object content : contentAnchoragesByRole.get(role)) {
				Set<String> roles = contentAnchoragesWithRoles.get(content);
				if (roles == null) {
					roles = new HashSet<String>();
					contentAnchoragesWithRoles.put(content, roles);
				}
				roles.add(role);
			}
		}

		return contentAnchoragesWithRoles;
	}

	// TODO: either provide methods to transform between anchoragesWithRoles and
	// anchoragesByRoles
	// TODO: implement the following method based on
	// #getContentAnchoragesByRole()
	// @Override
	// public Map<? extends Object, Set<String>> getContentAnchoragesWithRoles()
	// {
	// }

	@Override
	public List<? extends Object> getContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void register() {
		super.register();
		if (content != null) {
			registerAtContentPartMap();
		}
	}

	/**
	 * Registers the <i>model</i> in the {@link IViewer#getContentPartMap()}.
	 * Subclasses should only extend this method if they need to register this
	 * EditPart in additional ways.
	 */
	protected void registerAtContentPartMap() {
		getViewer().getContentPartMap().put(getContent(), this);
	}

	/**
	 * Set the primary model object that this EditPart represents. This method
	 * is used by an <code>EditPartFactory</code> when creating an EditPart.
	 * 
	 * @see IContentPart#setContent(Object)
	 */
	@Override
	public void setContent(Object content) {
		if (this.content == content) {
			return;
		}

		Object oldContent = this.content;
		if (oldContent != null && oldContent != content && getRoot() != null) {
			unregisterFromContentPartMap();
		}
		this.content = content;
		if (content != null && content != oldContent && getRoot() != null) {
			registerAtContentPartMap();
		}

		pcs.firePropertyChange(CONTENT_PROPERTY, oldContent, content);
	}

	@Override
	protected void unregister() {
		super.unregister();
		if (content != null) {
			unregisterFromContentPartMap();
		}
	}

	/**
	 * Unregisters the <i>model</i> in the {@link IViewer#getContentPartMap()}.
	 * Subclasses should only extend this method if they need to unregister this
	 * EditPart in additional ways.
	 */
	protected void unregisterFromContentPartMap() {
		Map<Object, IContentPart<VR>> registry = getViewer()
				.getContentPartMap();
		if (registry.get(getContent()) == this) {
			registry.remove(getContent());
		}
	}

}
