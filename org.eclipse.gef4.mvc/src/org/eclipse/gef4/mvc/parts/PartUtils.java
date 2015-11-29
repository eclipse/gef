/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

	/**
	 * Searches the given collection of {@link IVisualPart}s for elements of the
	 * specified type.
	 *
	 * @param <T>
	 *            The type of returned elements.
	 * @param <VR>
	 *            The visual root type.
	 * @param parts
	 *            The collection of parts which is filtered.
	 * @param type
	 *            The type of returned elements.
	 * @return A list of all elements of the specified type.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IVisualPart<VR, ? extends VR>, VR> List<T> filterParts(
			Collection<? extends IVisualPart<VR, ? extends VR>> parts,
			Class<T> type) {
		List<T> filtered = new ArrayList<>();
		for (IVisualPart<VR, ? extends VR> c : parts) {
			if (type.isInstance(c)) {
				filtered.add((T) c);
			}
		}
		return filtered;
	}

	/**
	 * Collects the anchoreds of all given {@link IVisualPart}s.
	 *
	 * @param <VR>
	 *            The visual root type.
	 * @param anchorages
	 *            The collection of {@link IVisualPart}s for which the anchoreds
	 *            are collected.
	 * @return A list of all the anchoreds of all the given {@link IVisualPart}
	 *         s.
	 */
	public static <VR> List<IVisualPart<VR, ? extends VR>> getAnchoreds(
			Collection<? extends IVisualPart<VR, ? extends VR>> anchorages) {
		List<IVisualPart<VR, ? extends VR>> anchoreds = new ArrayList<>();
		for (IVisualPart<VR, ? extends VR> a : anchorages) {
			anchoreds.addAll(a.getAnchoreds());
		}
		return anchoreds;
	}

	/**
	 * Collects the anchoreds of the given {@link IVisualPart} which are
	 * registered under the specified role.
	 *
	 * @param <VR>
	 *            The visual root type.
	 * @param anchorage
	 *            The {@link IVisualPart} for which the anchoreds are collected.
	 * @param role
	 *            The role under which the anchoreds have to be registered to be
	 *            collected.
	 * @return A list of the anchoreds of the given {@link IVisualPart} which
	 *         are registered under the specified role.
	 */
	public static <VR> Set<IVisualPart<VR, ? extends VR>> getAnchoreds(
			IVisualPart<VR, ? extends VR> anchorage, String role) {
		HashSet<IVisualPart<VR, ? extends VR>> result = new HashSet<>();
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
