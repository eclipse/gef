/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.core.viewers.INestedContentProvider
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.jface;

public interface INestedGraphContentProvider extends IGraphNodeContentProvider {

	public Object[] getChildren(Object node);

	public boolean hasChildren(Object node);

}
