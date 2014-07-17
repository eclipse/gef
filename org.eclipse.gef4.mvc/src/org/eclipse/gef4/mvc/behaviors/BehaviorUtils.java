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
import java.util.Map;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

// TODO: Transfer this into a utility class that can be injected (and thus replaced) in the parts/policies where its needed, providing non-static functions.
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
	 * @param anchoreds
	 *            the {@link IVisualPart}s to be anchored
	 * @see #removeAnchoreds(IRootPart, List, List)
	 */
	public static <VR> void addAnchoreds(IRootPart<VR> root,
			List<? extends IVisualPart<VR>> anchorages,
			List<? extends IVisualPart<VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.addChildren(anchoreds);
			for (IVisualPart<VR> anchored : anchoreds) {
				anchored.addAnchorages(anchorages);
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
	 * @param anchoreds
	 * @see #addAnchoreds(IRootPart, List, List)
	 */
	public static <VR> void removeAnchoreds(IRootPart<VR> root,
			List<? extends IVisualPart<VR>> anchorages,
			List<? extends IVisualPart<VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.removeChildren(anchoreds);
			for (IVisualPart<VR> anchored : anchoreds) {
				anchored.removeAnchorages(anchorages);
			}
		}
	}

	public static <VR> List<IFeedbackPart<VR>> createFeedback(
			List<IContentPart<VR>> targets, IBehavior<VR> behavior, Map<Object, Object> contextMap) {
		IVisualPart<VR> host = behavior.getAdaptable();
		IFeedbackPartFactory<VR> factory = host.getRoot().getViewer()
				.getFeedbackPartFactory();
		List<IFeedbackPart<VR>> feedbackParts = factory.createFeedbackParts(
				targets, behavior, contextMap);
		return feedbackParts;
	}

	public static <VR> List<IHandlePart<VR>> createHandles(
			List<IContentPart<VR>> targets, IBehavior<VR> behavior, Map<Object, Object> contextMap) {
		IVisualPart<VR> host = behavior.getAdaptable();
		IHandlePartFactory<VR> factory = host.getRoot().getViewer()
				.getHandlePartFactory();
		List<IHandlePart<VR>> handleParts = factory.createHandleParts(targets,
				behavior, contextMap);
		return handleParts;
	}

}
