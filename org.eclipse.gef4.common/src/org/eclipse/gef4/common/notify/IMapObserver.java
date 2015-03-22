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

import java.util.Map;

/**
 * An {@link IMapObserver} can be used to monitor changes made to an
 * {@link ObservableMap}.
 * 
 * @author mwienand
 *
 * @param <K> The type of the map's keys.
 * @param <V> The type of the map's values.
 */
public interface IMapObserver<K, V> {

	/**
	 * Called when changes are made to an {@link ObservableMap} where this
	 * {@link IMapObserver} is registered.
	 * 
	 * @param observableMap
	 *            The {@link ObservableMap} that changed.
	 * @param previousMap
	 *            A copy of the map before the change.
	 */
	public void afterChange(ObservableMap<K, V> observableMap,
			Map<K, V> previousMap);

}
