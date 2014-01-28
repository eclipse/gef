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

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public interface IHandlePartFactory<V> {

	public List<IHandlePart<V>> createFocusHandleParts(IContentPart<V> focused);

	public List<IHandlePart<V>> createHoverHandleParts(IContentPart<V> hovered);

	public List<IHandlePart<V>> createSelectionHandleParts(
			List<IContentPart<V>> selected);

}
