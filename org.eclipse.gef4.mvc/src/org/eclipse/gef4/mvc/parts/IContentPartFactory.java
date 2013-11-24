/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

/**
 * A factory for creating new EditParts. {@link IVisualPartViewer EditPartViewers}
 * can be configured with an <code>EditPartFactory</code>. Whenever an
 * <code>EditPart</code> in that viewer needs to create another EditPart, it can
 * use the Viewer's factory. The factory is also used by the viewer whenever
 * {@link IVisualPartViewer#setContents(Object)} is called.
 * 
 * @since 2.0
 */
// TODO: should be a contents (edit) part factory
public interface IContentPartFactory<V> {

	/**
	 * Creates a new {@link INodeContentPart} given the specified <i>parent</i> and
	 * <i>model</i>.
	 * 
	 * @param parent
	 *            The parent in which the {@link INodeContentPart} is being
	 *            created.
	 * @param model
	 *            the model of the {@link INodeContentPart} being created
	 * @return the new {@link INodeContentPart}
	 */
	// TODO: check type of parent -> content part??
	INodeContentPart<V> createNodeContentPart(IContentPart<V> parent, Object object);

	IEdgeContentPart<V> createEdgeContentPart(IContentPart<V> parent, Object link);
	
	IContentPart<V> createRootContentPart(IRootVisualPart<V> root, Object objectOrLink);

}
