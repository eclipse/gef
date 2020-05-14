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
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.Map;

import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * A factory for creating new {@link IContentPart}s. The {@link IViewer} can be
 * configured with an {@link IContentPartFactory}. Whenever a behavior of an
 * {@link IContentPart} in that viewer needs to create another child
 * {@link IContentPart}, it can use the viewer's {@link IContentPartFactory},
 * passing in itself as context behavior.
 *
 */
public interface IContentPartFactory {

	/**
	 * Creates a specific {@link IContentPart} for the given <i>content</i>. As
	 * additional information might be needed by the {@link IContentPartFactory}
	 * to identify the creation context, an additional <i>contextMap</i> is
	 * passed in upon creation.
	 *
	 * @param content
	 *            The model {@link Object} for which an {@link IContentPart} is
	 *            to be created.
	 * @param contextMap
	 *            A map in which additional context information for the creation
	 *            process can be placed.
	 *
	 * @return An {@link IContentPart} for the given content and context.
	 */
	IContentPart<? extends Node> createContentPart(Object content,
			Map<Object, Object> contextMap);

}
