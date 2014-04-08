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

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class BehaviorUtils {

	/**
	 * Adds the given list of {@link IHandlePart}s as children to the given
	 * {@link IRootPart}. Additionally, all handles are added as anchoreds to
	 * the given list of {@link IContentPart}s.
	 * 
	 * @param root
	 *            The {@link IRootPart}, the anchored {@link IVisualPart}s are
	 *            to be added to as children
	 * @param anchorages
	 *            the {@link IVisualPart}s, the anchored {@link IVisualPart}s
	 *            are to be added to as anchoreds
	 * @param anchords
	 *            the {@link IVisualPart}s to be anchored
	 * @see #removeAnchoreds(IRootPart, List, List)
	 */
	public static <V> void addAnchoreds(IRootPart<V> root,
			List<IContentPart<V>> anchorages,
			List<? extends IVisualPart<V>> anchords) {
		if (anchords != null && !anchords.isEmpty()) {
			root.addChildren(anchords);
			for (IContentPart<V> anchorage : anchorages) {
				anchorage.addAnchoreds(anchords);
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
	 * @see #addAnchoreds(IRootPart, List, List)
	 */
	public static <V> void removeAnchoreds(IRootPart<V> root,
			List<IContentPart<V>> anchorages,
			List<? extends IVisualPart<V>> anchords) {
		if (anchords != null && !anchords.isEmpty()) {
			root.removeChildren(anchords);
			for (IContentPart<V> anchorage : anchorages) {
				anchorage.removeAnchoreds(anchords);
			}
		}
	}

	public static <V> List<IFeedbackPart<V>> createFeedback(
			IBehavior<V> behavior, List<IContentPart<V>> targets) {
		IVisualPart<V> host = behavior.getHost();
		IFeedbackPartFactory<V> factory = host.getRoot().getViewer()
				.getFeedbackPartFactory();
		List<IFeedbackPart<V>> feedbackParts = factory.createFeedbackParts(
				targets, behavior, Collections.emptyMap());
		return feedbackParts;
	}

	public static <V> List<IHandlePart<V>> createHandles(IBehavior<V> behavior,
			List<IContentPart<V>> targets) {
		IVisualPart<V> host = behavior.getHost();
		IHandlePartFactory<V> factory = host.getRoot().getViewer()
				.getHandlePartFactory();
		List<IHandlePart<V>> handleParts = factory.createHandleParts(targets,
				behavior, Collections.emptyMap());
		return handleParts;
	}

}
