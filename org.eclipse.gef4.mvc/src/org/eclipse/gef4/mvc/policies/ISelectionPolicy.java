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
package org.eclipse.gef4.mvc.policies;

public interface ISelectionPolicy<V> extends IPolicy<V> {

	public class Impl<V> extends AbstractPolicy<V> implements ISelectionPolicy<V> {
		@Override
		public boolean isSelectable() {
			return true;
		}
	}
	
	public boolean isSelectable();
	
}
