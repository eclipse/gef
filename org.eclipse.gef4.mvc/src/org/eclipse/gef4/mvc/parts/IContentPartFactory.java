/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * A factory for creating new {@link IContentPart}s. The {@link IViewer}
 * can be configured with an {@link IContentPartFactory}. Whenever a behavior of
 * an {@link IContentPart} in that viewer needs to create another child
 * {@link IContentPart}, it can use the viewer's {@link IContentPartFactory},
 * passing in itself as context behavior.
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 * 
 */
public interface IContentPartFactory<VR> {

	/**
	 * Creates a specific {@link IContentPart} for the given <i>content</i>, in
	 * the context specified by the given <i>contextBehavior</i> and
	 * <i>contextMap</i>.
	 * 
	 * As all {@link IBehavior}s should be stateless, all data required for the
	 * <i>contextBehavior</i> to be able to deliver certain information to the
	 * factory should be encapsulated in the <i>contextMap</i>, i.e.:
	 * 
	 * <pre>
	 * create(Object target, IBehavior ctxb, Map&lt;Object, Object&gt; ctxm) {
	 * 	if (ctxb instanceof ConcreteBehavior) {
	 * 		SomeParam p = ((ConcreteBehavior) ctxb).getSomeParam(ctxm);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param content The model {@link Object} for which an {@link IContentPart} is to be created.
	 * @param contextBehavior The {@link IBehavior} which uses this factory.
	 * @param contextMap Additional context information to keep the IBehavior stateless.
	 * @return an {@link IContentPart} for the given content and context
	 */
	IContentPart<VR> createContentPart(Object content,
			IBehavior<VR> contextBehavior, Map<Object, Object> contextMap);

}
