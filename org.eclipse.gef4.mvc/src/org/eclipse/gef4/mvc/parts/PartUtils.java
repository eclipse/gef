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
 * Provides utilities neeeded in the context of {@link IVisualPart}s.
 *
 * @author nyssen
 *
 */
public class PartUtils {

	@SuppressWarnings("unchecked")
	public static <T extends IVisualPart<VR>, VR> List<T> filterParts(
			Collection<? extends IVisualPart<VR>> parts, Class<T> type) {
		List<T> filtered = new ArrayList<T>();
		for (IVisualPart<VR> c : parts) {
			if (type.isInstance(c)) {
				filtered.add((T) c);
			}
		}
		return filtered;
	}

	// public static <VR> List<IVisualPart<VR>> getAnchorages(
	// List<? extends IVisualPart<VR>> anchoreds) {
	// List<IVisualPart<VR>> anchorages = new ArrayList<IVisualPart<VR>>();
	// for (IVisualPart<VR> a : anchoreds) {
	// anchorages.addAll(a.getAnchoragesWithRoles().keySet());
	// }
	// return anchorages;
	// }

	public static <VR> List<IVisualPart<VR>> getAnchoreds(
			Collection<? extends IVisualPart<VR>> anchorages) {
		List<IVisualPart<VR>> anchoreds = new ArrayList<IVisualPart<VR>>();
		for (IVisualPart<VR> a : anchorages) {
			anchoreds.addAll(a.getAnchoreds());
		}
		return anchoreds;
	}

	public static <VR> Set<IVisualPart<VR>> getAnchoreds(
			IVisualPart<VR> anchorage, String role) {
		HashSet<IVisualPart<VR>> result = new HashSet<IVisualPart<VR>>();
		Multiset<IVisualPart<VR>> anchoreds = anchorage.getAnchoreds();
		for (IVisualPart<VR> anchored : anchoreds) {
			if (anchored.getAnchorages().containsEntry(anchorage, role)) {
				result.add(anchored);
			}
		}
		return result;
	}
	/*
	 * TODO: IVisualPart findCommonAncestor(IVisualPart... parts)
	 *
	 * Searches the visual part hierarchy for a common ancestor of the given
	 * parts. Returns this ancestor if one is found, otherwise returns null.
	 *
	 * @param parts
	 *
	 * @return common ancestor of given parts, or null
	 *
	 * Note: This method can be transferred to here from the GEF 3.x
	 * ToolUtilities class.
	 */

}
