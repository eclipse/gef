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
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

/**
 * Default implementation of RootEditPart for GraphicalViewers.
 * 
 * @author Pratik Shah
 * @since 3.2
 */
public abstract class AbstractRootEditPart<V> extends AbstractEditPart<V>
		implements IRootEditPart<V> {

	private List<Object> contents;
	private IEditPartViewer<V> viewer;

	/**
	 * @see IRootEditPart#getContents()
	 */
	public List<Object> getContents() {
		if (contents == null) {
			return Collections.unmodifiableList(Collections.emptyList());
		}
		return Collections.unmodifiableList(contents);
	}

	/**
	 * @see EditPart#getRoot()
	 */
	public IRootEditPart<V> getRoot() {
		return this;
	}

	/**
	 * @see EditPart#getViewer()
	 */
	public IEditPartViewer<V> getViewer() {
		return viewer;
	}

	/**
	 * @see IRootEditPart#setContents(EditPart)
	 */
	public void setContents(List<Object> contents) {
		if (contents != null) {
			this.contents = new ArrayList<Object>(contents);
		}
		else {
			this.contents = null;
		}
		synchronize();
	}

	@Override
	protected List<Object> getModelChildren() {
		return getContents();
	}

	/**
	 * @see IRootEditPart#setViewer(EditPartViewer)
	 */
	public void setViewer(IEditPartViewer<V> newViewer) {
		if (viewer == newViewer)
			return;
		viewer = newViewer;
	}

}