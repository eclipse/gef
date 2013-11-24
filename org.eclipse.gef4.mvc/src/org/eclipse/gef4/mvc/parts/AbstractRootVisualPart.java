/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

/**
 * Default implementation of RootEditPart for GraphicalViewers.
 * 
 * @author Pratik Shah
 * @since 3.2
 */
public abstract class AbstractRootVisualPart<V> extends AbstractVisualPart<V>
		implements IRootVisualPart<V> {

	private IVisualPartViewer<V> viewer;

	/**
	 * @see EditPart#getRoot()
	 */
	public IRootVisualPart<V> getRoot() {
		return this;
	}

	/**
	 * @see EditPart#getViewer()
	 */
	public IVisualPartViewer<V> getViewer() {
		return viewer;
	}
	
	/**
	 * @see IRootVisualPart#getContents()
	 */
	public IContentPart<V> getRootContentPart() {
		return rootContentPart;
	}
	
	// TODO: remove this
	private IContentPart<V> rootContentPart;

	/**
	 * @see IRootVisualPart#setContents(EditPart)
	 */
	public void setRootContentPart(IContentPart<V> rootContentPart) {
		if (this.rootContentPart == rootContentPart) {
			return;
		}
		if (this.rootContentPart != null) {
			// unregister
			removeChild(rootContentPart);
		}
		this.rootContentPart = rootContentPart;
		if (rootContentPart != null) {
			// register
			addChild(rootContentPart, 0);
		}
	}
	
	@Override
	public void addHandleParts(List<IHandlePart<V>> handleParts) {
		for(IHandlePart<V> h : handleParts){
			addChild(h, getChildren().size()-1);
		}
	}

	@Override
	public void removeHandleParts(List<IHandlePart<V>> handleParts) {
		for(IHandlePart<V> h : handleParts){
			removeChild(h);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<IHandlePart<V>> getHandleParts() {
		return filterChildren(IHandlePart.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends IVisualPart<V>> List<T> filterChildren(Class<T> type) {
		List<T> handleParts = new ArrayList<T>();
		for(IVisualPart<V> c : getChildren()){
			if(type.isInstance(c)){
				handleParts.add((T) c);
			}
		}
		return handleParts;
	}
	
	//TODO: return two lists (conent and handle parts, do not have to provide an on data field for contents parts)

	/**
	 * @see IRootVisualPart#setViewer(EditPartViewer)
	 */
	public void setViewer(IVisualPartViewer<V> newViewer) {
		if (viewer == newViewer)
			return;
		viewer = newViewer;
	}

}