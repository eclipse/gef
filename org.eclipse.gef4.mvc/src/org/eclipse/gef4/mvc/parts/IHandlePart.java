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

/**
 * An {@link IHandlePart} is a controller that controls a visual, which is used
 * simply for feedback and/or tool interaction and does not correspond to
 * anything in the visualized model.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public interface IHandlePart<V> extends IVisualPart<V> {

	// interaction part
}
