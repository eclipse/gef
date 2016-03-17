/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * A factory for creating new {@link IContentPart}s. The {@link IViewer} can be
 * configured with an {@link IContentPartFactory}. Whenever a behavior of an
 * {@link IContentPart} in that viewer needs to create another child
 * {@link IContentPart}, it can use the viewer's {@link IContentPartFactory},
 * passing in itself as context behavior.
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public interface IContentPartFactory<VR> {

	/**
	 * Creates a specific {@link IContentPart} for the given <i>content</i>. As
	 * additional information might be needed by the {@link IContentPartFactory}
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
	 * (concrete) {@link IContentPartFactory} that is based on such a query may
	 * be realized as follows:
	 *
	 * <pre>
	 * IContentPart createContentPart(Object content, IBehavior contextBehavior,
	 * 		Map&lt;Object, Object&gt; contextMap) {
	 *   if (contextBehavior instanceof ConcreteBehavior) {
	 * 	   SomeAdditionalInformation i = ((ConcreteBehavior) contextBehavior)
	 * 				.giveSomeAdditionalInformation(contextMap);
	 *     ...
	 * 	 }
	 * }
	 * </pre>
	 *
	 * @param content
	 *            The model {@link Object} for which an {@link IContentPart} is
	 *            to be created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IContentPartFactory}, or may be passed back by
	 *            the {@link IContentPartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @return An {@link IContentPart} for the given content and context.
	 */
	IContentPart<VR, ? extends VR> createContentPart(Object content,
			IBehavior<VR> contextBehavior, Map<Object, Object> contextMap);

}
