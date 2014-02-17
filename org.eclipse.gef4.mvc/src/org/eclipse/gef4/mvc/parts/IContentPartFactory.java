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

import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * A factory for creating new {@link IContentPart}s. The
 * {@link IVisualViewer} can be configured with an
 * {@link IContentPartFactory}. Whenever an {@link IContentPart} in that viewer
 * needs to create another child {@link IContentPart}, it can use the viewer's
 * {@link IContentPartFactory}. The factory is also used by the viewer whenever
 * {@link IVisualViewer#setContents(Object)} is called to create the root
 * content parts.
 * 
 */
public interface IContentPartFactory<V> {

	IContentPart<V> createRootContentPart(IRootPart<V> root,
			Object objectOrLink);

	IContentPart<V> createChildContentPart(IContentPart<V> parent,
			Object objectOrLink);

}
