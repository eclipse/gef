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
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

public class ContentModel implements IPropertyChangeNotifier {

	public static final String CONTENTS_PROPERTY = "contents";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private List<Object> contents = new ArrayList<Object>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public List<? extends Object> getContents() {
		return Collections.unmodifiableList(this.contents);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setContents(List<? extends Object> contents) {
		List<Object> oldContents = Collections
				.unmodifiableList(new ArrayList<Object>(this.contents));
		this.contents.clear();
		this.contents.addAll(contents);
		pcs.firePropertyChange(CONTENTS_PROPERTY, oldContents, getContents());
	}
}
