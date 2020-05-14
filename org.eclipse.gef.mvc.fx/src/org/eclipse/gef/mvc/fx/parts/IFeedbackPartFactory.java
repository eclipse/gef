/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.behaviors.IBehavior;

import javafx.scene.Node;

/**
 * The {@link IFeedbackPartFactory} interface specifies a factory method for the
 * creation of {@link IFeedbackPart}s for a given list of target
 * {@link IVisualPart}s, a context {@link IBehavior}, and a context {@link Map}.
 *
 * @author anyssen
 *
 */
public interface IFeedbackPartFactory {

	/**
	 * Creates specific {@link IFeedbackPart}s for the given <i>targets</i>. As
	 * additional information might be needed by the
	 * {@link IFeedbackPartFactory} to identify the creation context, an
	 * additional <i>contextMap</i> is passed in upon creation.
	 *
	 * @param targets
	 *            The target {@link IVisualPart}s for which feedback is to be
	 *            created.
	 * @param contextMap
	 *            A map in which additional context information for the creation
	 *            process can be placed.
	 * @return A list of {@link IFeedbackPart}s that are to be used to indicate
	 *         feedback for the given targets.
	 */
	public List<IFeedbackPart<? extends Node>> createFeedbackParts(
			List<? extends IVisualPart<? extends Node>> targets,
			Map<Object, Object> contextMap);

}
