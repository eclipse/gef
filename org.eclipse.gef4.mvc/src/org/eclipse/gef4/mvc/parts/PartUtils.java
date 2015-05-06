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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multiset;

/**
 * Provides utilities needed in the context of {@link IVisualPart}s.
 *
 * @author anyssen
 *
 */
public class PartUtils {

	@SuppressWarnings("unchecked")
	public static <T extends IVisualPart<VR, ? extends VR>, VR> List<T> filterParts(
			Collection<? extends IVisualPart<VR, ? extends VR>> parts,
			Class<T> type) {
		List<T> filtered = new ArrayList<T>();
		for (IVisualPart<VR, ? extends VR> c : parts) {
			if (type.isInstance(c)) {
				filtered.add((T) c);
			}
		}
		return filtered;
	}

	public static <VR> List<IVisualPart<VR, ? extends VR>> getAnchoreds(
			Collection<? extends IVisualPart<VR, ? extends VR>> anchorages) {
		List<IVisualPart<VR, ? extends VR>> anchoreds = new ArrayList<IVisualPart<VR, ? extends VR>>();
		for (IVisualPart<VR, ? extends VR> a : anchorages) {
			anchoreds.addAll(a.getAnchoreds());
		}
		return anchoreds;
	}

	public static <VR> Set<IVisualPart<VR, ? extends VR>> getAnchoreds(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		HashSet<IVisualPart<VR, ? extends VR>> result = new HashSet<IVisualPart<VR, ? extends VR>>();
		Multiset<IVisualPart<VR, ? extends VR>> anchoreds = anchorage
				.getAnchoreds();
		for (IVisualPart<VR, ? extends VR> anchored : anchoreds) {
			if (anchored.getAnchorages().containsEntry(anchorage, role)) {
				result.add(anchored);
			}
		}
		return result;
	}

}
