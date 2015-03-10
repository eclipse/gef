/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.notify;

import java.util.List;

/**
 * An {@link IListObserver} can be used to monitor changes made to an
 * {@link ObservableList}.
 * 
 * @author wienand
 *
 * @param <T>
 *            The type of the list elements.
 */
public interface IListObserver<T> {

	/**
	 * Called when changes are made to an {@link ObservableList} where this
	 * {@link IListObserver} is registered.
	 * 
	 * @param observableList
	 *            The {@link ObservableList} that changed.
	 * @param previousList
	 *            A copy of the list before the change.
	 */
	public void afterChange(ObservableList<T> observableList,
			List<T> previousList);

}
