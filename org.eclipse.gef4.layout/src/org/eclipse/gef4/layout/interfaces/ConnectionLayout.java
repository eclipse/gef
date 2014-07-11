/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.interfaces;

import org.eclipse.gef4.layout.IPropertyStore;

public interface ConnectionLayout extends IPropertyStore {

	/**
	 * @return source node
	 */
	public NodeLayout getSource();

	/**
	 * @return target node
	 */
	public NodeLayout getTarget();

}
