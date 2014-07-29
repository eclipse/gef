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
	 * Adds the given list of anchoreds as children to the given
	 * {@link IRootPart}. Additionally, all given anchorages will be added to
	 * the given anchoreds.
	 * 
	 * @param root
	 *            The {@link IRootPart}, the anchored {@link IVisualPart}s are
	 *            to be added to as children
	 * @param anchorages
	 *            the {@link IVisualPart}s which are to be added to the given
	 *            anchoreds as anchorages.
	 * @param anchoreds
	 *            the {@link IVisualPart}s to which the given anchorages are to
	 *            be added.
	 * @see #removeAnchorages(IRootPart, List, List)
	 */
	public static <VR> void addAnchorages(IRootPart<VR> root,
			List<? extends IVisualPart<VR>> anchorages,
			List<? extends IVisualPart<VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.addChildren(anchoreds);
			for (IVisualPart<VR> anchored : anchoreds) {
				anchored.addAnchorages(anchorages);
			}
		}
	}

	public static <VR> List<IFeedbackPart<VR>> createFeedback(
			List<IContentPart<VR>> targets, IBehavior<VR> behavior,
			Map<Object, Object> contextMap) {
		IVisualPart<VR> host = behavior.getAdaptable();
		IFeedbackPartFactory<VR> factory = host.getRoot().getViewer()
				.getFeedbackPartFactory();
		List<IFeedbackPart<VR>> feedbackParts = factory.createFeedbackParts(
				targets, behavior, contextMap);
		return feedbackParts;
	}

	public static <VR> List<IHandlePart<VR>> createHandles(
			List<IContentPart<VR>> targets, IBehavior<VR> behavior,
			Map<Object, Object> contextMap) {
		IVisualPart<VR> host = behavior.getAdaptable();
		IHandlePartFactory<VR> factory = host.getRoot().getViewer()
				.getHandlePartFactory();
		List<IHandlePart<VR>> handleParts = factory.createHandleParts(targets,
				behavior, contextMap);
		return handleParts;
	}

	/**
	 * Removes the given list of anchoreds as children from the given
	 * {@link IRootPart}. Additionally removes the given anchorages from the
	 * anchoreds.
	 * 
	 * @param root
	 *            The {@link IRootPart} from which the anchoreds are to be
	 *            removed as children.
	 * @param anchorages
	 *            The anchorages to be removed from the given anchoreds.
	 * @param anchoreds
	 *            The anchoreds from which to remove the given anchorages.
	 * @see #addAnchorages(IRootPart, List, List)
	 */
	public static <VR> void removeAnchorages(IRootPart<VR> root,
			List<? extends IVisualPart<VR>> anchorages,
			List<? extends IVisualPart<VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.removeChildren(anchoreds);
			for (IVisualPart<VR> anchored : anchoreds) {
				anchored.removeAnchorages(anchorages);
			}
		}
	}

}
