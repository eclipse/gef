/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

public interface IHoverPolicy<V> extends IPolicy<V> {

	public class Impl<V> extends AbstractPolicy<V> implements IHoverPolicy<V>{

		@Override
		public boolean isHoverable() {
			return true;
		}
	}
	
	public boolean isHoverable();
	
	// TODO: we need to add the manipulation of the hover model here and in Impl.
	
}
