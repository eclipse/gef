/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
 * The {@link IHandlePartFactory} interface specifies a factory method for the
 * creation of {@link IHandlePart}s for a given list of target
 * {@link IVisualPart}s, a context {@link IBehavior}, and a context {@link Map}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public interface IHandlePartFactory<VR> {

	/**
	 * Creates specific {@link IHandlePart}s for the given <i>targets</i>. As
	 * additional information might be needed by the {@link IHandlePartFactory}
	 * to identify the creation context, the initiating <i>contextBehavior</i>
	 * is expected to pass in a reference to itself as well as a
	 * <i>contextMap</i>.
	 * <p>
	 * The <i>contextMap</i> may either directly contain the additional
	 * information needed by the factory, or it may be used as a reference to
	 * identify the creation context, in case the factory needs to query back
	 * the initiating <i>contextBehavior</i> for such information.
	 * <p>
	 * This mechanism is needed because all {@link IBehavior}s are expected to
	 * be stateless, so only the information within the <i>contextMap</i> will
	 * allow the <i>contextBehavior</i> to identify the respective creation
	 * context. A contract between a (concrete) {@link IBehavior} and a
	 * (concrete) {@link IHandlePartFactory} that is based on such a query may
	 * be realized as follows:
	 *
	 * <pre>
	 * List createHandleParts(List targets, IBehavior contextBehavior,
	 * 		Map&lt;Object, Object&gt; contextMap) {
	 *   if (contextBehavior instanceof ConcreteBehavior) {
	 * 	   SomeAdditionalInformation i = ((ConcreteBehavior) contextBehavior)
	 * 				.giveSomeAdditionalInformation(contextMap);
	 *     ...
	 * 	 }
	 * }
	 * </pre>
	 *
	 * @param targets
	 *            The target {@link IVisualPart}s for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	public List<IHandlePart<VR, ? extends VR>> createHandleParts(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			IBehavior<VR> contextBehavior, Map<Object, Object> contextMap);

}
