/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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

import org.eclipse.gef4.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.common.dispose.IDisposable;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.ObservableList;

/**
 * The {@link ContentModel} stores the content {@link Object}s that are
 * visualized. The {@link ContentModel} fires a property change event when the
 * contents are changed.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class ContentModel implements IDisposable {

	/**
	 * Name of the {@link #contentsProperty()}.
	 */
	public static final String CONTENTS_PROPERTY = "contents";

	private ObservableList<Object> contents = CollectionUtils
			.observableArrayList();

	private ReadOnlyListWrapper<Object> contentsProperty = new ReadOnlyListWrapperEx<>(
			this, CONTENTS_PROPERTY, contents);

	/**
	 * A read-only property containing the current content objects.
	 *
	 * @return A read-only list property named {@link #CONTENTS_PROPERTY}.
	 */
	public ReadOnlyListProperty<Object> contentsProperty() {
		return contentsProperty.getReadOnlyProperty();
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		contents.clear();
	}

	/**
	 * Returns an {@link ObservableList} containing the content objects.
	 *
	 * @return An {@link ObservableList}.
	 */
	public ObservableList<Object> getContents() {
		return contents;
	}

}
