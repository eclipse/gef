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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.common.properties.PropertyChangeNotifierSupport;

/**
 * The {@link ContentModel} stores the content {@link Object}s that are
 * visualized. The {@link ContentModel} fires a property change event when the
 * contents are changed.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class ContentModel implements IPropertyChangeNotifier {

	/**
	 * Property name used when notifying listeners about content changes.
	 */
	public static final String CONTENTS_PROPERTY = "contents";

	private PropertyChangeNotifierSupport pcs = new PropertyChangeNotifierSupport(
			this);
	private List<Object> contents = new ArrayList<>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns an unmodifiable list containing the current content objects.
	 *
	 * @return An unmodifiable list containing the current content objects.
	 */
	public List<? extends Object> getContents() {
		return Collections.unmodifiableList(this.contents);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Replaces the currently stored content objects with the given list of new
	 * content objects.
	 *
	 * @param contents
	 *            A list containing the new content objects to store in this
	 *            {@link ContentModel}.
	 */
	public void setContents(List<? extends Object> contents) {
		if (!this.contents.equals(contents)) {
			List<Object> oldContents = Collections
					.unmodifiableList(new ArrayList<>(this.contents));
			this.contents.clear();
			this.contents.addAll(contents);
			pcs.firePropertyChange(CONTENTS_PROPERTY, oldContents,
					getContents());
		}
	}

}
