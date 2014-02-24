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
package org.eclipse.gef4.mvc.behaviors;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class BehaviorUtils {

	/**
	 * Adds the given list of {@link IHandlePart}s as children to the given
	 * {@link IRootPart}. Additionally, all handles are added as anchoreds to
	 * the given list of {@link IContentPart}s.
	 * 
	 * @param root
	 *            root part
	 * @param anchorages
	 *            content parts
	 * @param handles
	 *            handle parts
	 * @see #removeHandles(IRootPart, List, List)
	 */
	public static <V> void addHandles(IRootPart<V> root,
			List<IContentPart<V>> anchorages, List<IHandlePart<V>> handles) {
		if (handles != null && !handles.isEmpty()) {
			root.addChildren(handles);
			for (IContentPart<V> anchorage : anchorages) {
				anchorage.addAnchoreds(handles);
			}
		}
	}

	/**
	 * Removes the given list of {@link IHandlePart}s from the given
	 * {@link IRootPart}. Additionally, all handles are removed from the
	 * anchoreds of the given {@link IContentPart}s.
	 * 
	 * @param root
	 * @param anchorages
	 * @param handles
	 * @see #addHandles(IRootPart, List, List)
	 */
	public static <V> void removeHandles(IRootPart<V> root,
			List<IContentPart<V>> anchorages, List<IHandlePart<V>> handles) {
		if (handles != null && !handles.isEmpty()) {
			root.removeChildren(handles);
			for (IContentPart<V> anchorage : anchorages) {
				anchorage.removeAnchoreds(handles);
			}
		}
	}
	
}
