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

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public interface IFeedbackPartFactory<VR> {

	/**
	 * Creates specific {@link IFeedbackPart}s for the given <i>targets</i>, in
	 * the context specified by the given <i>contextBehavior</i> and
	 * <i>contextMap</i>.
	 *
	 * As all {@link IBehavior}s should be stateless, all data required for the
	 * <i>contextBehavior</i> to be able to deliver certain information to the
	 * factory should be encapsulated in the <i>contextMap</i>, i.e.:
	 *
	 * <pre>
	 * create(List targets, IBehavior ctxb, Map&lt;Object, Object&gt; ctxm) {
	 * 	if (ctxb instanceof ConcreteBehavior) {
	 * 		SomeParam p = ((ConcreteBehavior) ctxb).getSomeParam(ctxm);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @param targets
	 *            The target {@link IVisualPart}s for which feedback is to be
	 *            created
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiated the creation of
	 *            feedback
	 * @param contextMap
	 *            A map to fill in additional state-based context information
	 *            that cannot be queried from the state-less context
	 *            {@link IBehavior}
	 * @return A list of {@link IFeedbackPart}s that are to be used to indicate
	 *         feedback of the given targets.
	 */
	public List<IFeedbackPart<VR, ? extends VR>> createFeedbackParts(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			IBehavior<VR> contextBehavior, Map<Object, Object> contextMap);

}
