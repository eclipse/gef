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
package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utilities neeeded in the context of {@link IVisualPart}s.
 * @author nyssen
 *
 */
public class PartUtils {

	@SuppressWarnings("unchecked")
	public static <T extends IVisualPart<V>, V> List<T> filterParts(List<? extends IVisualPart<V>> parts, Class<T> type) {
		List<T> handleParts = new ArrayList<T>();
		for (IVisualPart<V> c : parts) {
			if (type.isInstance(c)) {
				handleParts.add((T) c);
			}
		}
		return handleParts;
	}
}
