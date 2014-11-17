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

import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * The {@link BehaviorUtils} class provides utility methods for the
 * implementation of {@link IBehavior}s, such as the creation of
 * {@link IFeedbackPart}s and {@link IHandlePart}s, or the
 * establishment/unestablishment of anchor relations.
 */
// TODO: Transfer this into a utility class that can be injected (and thus
// replaced) in the parts/policies where its needed, providing non-static
// functions.
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
	 * @param <VR>
	 *            The visual root node of the UI toolkit this
	 *            {@link IVisualPart} is used in, e.g. javafx.scene.Node in case
	 *            of JavaFX.
	 * @see #removeAnchorages(IRootPart, List, List)
	 */
	public static <VR> void addAnchorages(IRootPart<VR, ? extends VR> root,
			List<? extends IVisualPart<VR, ? extends VR>> anchorages,
			List<? extends IVisualPart<VR, ? extends VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.addChildren(anchoreds);
			for (IVisualPart<VR, ? extends VR> anchored : anchoreds) {
				for (IVisualPart<VR, ? extends VR> anchorage : anchorages) {
					anchored.addAnchorage(anchorage);
				}
			}
		}
	}

	/**
	 * This method is called in the context of an {@link IBehavior} (
	 * {@link SelectionBehavior}, {@link HoverBehavior}, etc.) to create
	 * {@link IFeedbackPart}s for the given <i>targets</i> using the
	 * {@link IFeedbackPartFactory} of the {@link IViewer} which is associated
	 * with the {@link IBehavior#getHost() host} of the given <i>behavior</i>.
	 *
	 * @param targets
	 *            {@link List} of {@link IVisualPart}s for which feedback is to
	 *            be created.
	 * @param behavior
	 *            The {@link IBehavior} from which this method is called.
	 * @param contextMap
	 *            A {@link Map} storing state information for the
	 *            <i>behavior</i> to further specify the context as an
	 *            {@link IBehavior} is stateless.
	 * @return {@link List} of {@link IFeedbackPart}s created by the factory.
	 */
	public static <VR> List<IFeedbackPart<VR, ? extends VR>> createFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			IBehavior<VR> behavior, Map<Object, Object> contextMap) {
		IVisualPart<VR, ? extends VR> host = behavior.getAdaptable();
		IFeedbackPartFactory<VR> factory = host.getRoot().getViewer()
				.getFeedbackPartFactory();
		List<IFeedbackPart<VR, ? extends VR>> feedbackParts = factory
				.createFeedbackParts(targets, behavior, contextMap);
		return feedbackParts;
	}

	/**
	 * This method is called in the context of an {@link IBehavior} (
	 * {@link SelectionBehavior}, {@link HoverBehavior}, etc.) to create
	 * {@link IHandlePart}s for the given <i>targets</i> using the
	 * {@link IHandlePartFactory} of the {@link IViewer} which is associated
	 * with the {@link IBehavior#getHost() host} of the given <i>behavior</i>.
	 *
	 * @param targets
	 *            {@link List} of {@link IVisualPart}s for which feedback is to
	 *            be created.
	 * @param behavior
	 *            The {@link IBehavior} from which this method is called.
	 * @param contextMap
	 *            A {@link Map} storing state information for the
	 *            <i>behavior</i> to further specify the context as an
	 *            {@link IBehavior} is stateless.
	 * @return {@link List} of {@link IHandlePart}s created by the factory.
	 */
	public static <VR> List<IHandlePart<VR, ? extends VR>> createHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			IBehavior<VR> behavior, Map<Object, Object> contextMap) {
		IVisualPart<VR, ? extends VR> host = behavior.getAdaptable();
		IHandlePartFactory<VR> factory = host.getRoot().getViewer()
				.getHandlePartFactory();
		List<IHandlePart<VR, ? extends VR>> handleParts = factory
				.createHandleParts(targets, behavior, contextMap);
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
	 * @param <VR>
	 *            The visual root node of the UI toolkit this
	 *            {@link IVisualPart} is used in, e.g. javafx.scene.Node in case
	 *            of JavaFX.
	 * @see #addAnchorages(IRootPart, List, List)
	 */
	public static <VR> void removeAnchorages(IRootPart<VR, ? extends VR> root,
			List<? extends IVisualPart<VR, ? extends VR>> anchorages,
			List<? extends IVisualPart<VR, ? extends VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.removeChildren(anchoreds);
			for (IVisualPart<VR, ? extends VR> anchored : anchoreds) {
				for (IVisualPart<VR, ? extends VR> anchorage : anchorages) {
					anchored.removeAnchorage(anchorage);
				}
			}
		}
	}

}
