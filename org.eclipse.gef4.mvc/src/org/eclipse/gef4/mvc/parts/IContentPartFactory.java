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

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * A factory for creating new {@link IContentPart}s. The {@link IVisualViewer}
 * can be configured with an {@link IContentPartFactory}. Whenever a behavior of
 * an {@link IContentPart} in that viewer needs to create another child
 * {@link IContentPart}, it can use the viewer's {@link IContentPartFactory},
 * passing in itself as context behavior.
 * 
 */
public interface IContentPartFactory<V> {

	IContentPart<V> createContentPart(Object content,
			IBehavior<V> contextBehavior);

}
