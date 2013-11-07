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
package org.eclipse.gef4.mvc.partviewer;

import org.eclipse.gef4.mvc.parts.IConnectionEditPart;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.parts.INodeEditPart;

/**
 * A factory for creating new EditParts. {@link IEditPartViewer EditPartViewers}
 * can be configured with an <code>EditPartFactory</code>. Whenever an
 * <code>EditPart</code> in that viewer needs to create another EditPart, it can
 * use the Viewer's factory. The factory is also used by the viewer whenever
 * {@link IEditPartViewer#setContents(Object)} is called.
 * 
 * @since 2.0
 */
public interface IEditPartFactory<V> {

	/**
	 * Creates a new {@link INodeEditPart} given the specified <i>parent</i> and
	 * <i>model</i>.
	 * 
	 * @param parent
	 *            The parent in which the {@link INodeEditPart} is being created.
	 * @param model
	 *            the model of the {@link INodeEditPart} being created
	 * @return the new {@link INodeEditPart}
	 */
	INodeEditPart<V> createNodeEditPart(IEditPart<V> parent, Object model);
	
	
	IConnectionEditPart<V> createConnectionEditPart(INodeEditPart<V> sourceOrTarget, Object model);

}
