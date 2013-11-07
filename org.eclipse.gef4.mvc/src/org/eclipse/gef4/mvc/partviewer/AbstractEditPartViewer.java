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
package org.eclipse.gef4.mvc.partviewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.parts.IRootEditPart;

/**
 * The base implementation for EditPartViewer.
 * 
 * @author hudsonr
 */
public abstract class AbstractEditPartViewer<V> implements IEditPartViewer<V> {

	private IEditPartFactory<V> factory;
	private Map<Object, IEditPart<V>> mapModelToEditPart = new HashMap<Object, IEditPart<V>>();
	private Map<V, IEditPart<V>> mapVisualToEditPart = new HashMap<V, IEditPart<V>>();

	private IEditDomain<V> domain;
	private IRootEditPart<V> rootEditPart;

	/**
	 * Constructs the viewer and calls {@link #init()}.
	 */
	public AbstractEditPartViewer() {
	}

	/**
	 * @see IEditPartViewer#getContents()
	 */
	public Object getContents() {
		return getRootEditPart().getContents();
	}

	/**
	 * @see IEditPartViewer#getEditDomain()
	 */
	public IEditDomain<V> getEditDomain() {
		return domain;
	}

	/**
	 * @see IEditPartViewer#getEditPartFactory()
	 */
	public IEditPartFactory<V> getEditPartFactory() {
		return factory;
	}

	/**
	 * @see IEditPartViewer#getEditPartRegistry()
	 */
	public Map<Object, IEditPart<V>> getEditPartRegistry() {
		return mapModelToEditPart;
	}

	/**
	 * @see IEditPartViewer#getRootEditPart()
	 */
	public IRootEditPart<V> getRootEditPart() {
		return rootEditPart;
	}

	/**
	 * @see IEditPartViewer#getVisualPartMap()
	 */
	public Map<V, IEditPart<V>> getVisualPartMap() {
		return mapVisualToEditPart;
	}

	/**
	 * @see IEditPartViewer#setContents(Object)
	 */
	public void setContents(Object contents) {
		getRootEditPart().setContents(contents);
	}

	/**
	 * @see IEditPartViewer#setDomain(EditDomain)
	 */
	public void setEditDomain(IEditDomain<V> editdomain) {
		if (domain == editdomain)
			return;
		if (domain != null) {
			domain.setViewer(null);
		}
		this.domain = editdomain;
		if (domain != null) {
			domain.setViewer(this);
		}
	}

	/**
	 * @see IEditPartViewer#setEditPartFactory(org.eclipse.gef4.mvc.viewer.IEditPartFactory)
	 */
	public void setEditPartFactory(IEditPartFactory<V> factory) {
		this.factory = factory;
	}

	/**
	 * @see IEditPartViewer#setRootEditPart(IRootEditPart)
	 */
	public void setRootEditPart(IRootEditPart<V> editpart) {
		if (rootEditPart != null) {
			rootEditPart.deactivate();
			rootEditPart.setViewer(null);
		}
		rootEditPart = editpart;
		rootEditPart.setViewer(this);
		rootEditPart.activate();
	}

}
