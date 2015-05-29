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
 *******************************************************************************/
package org.eclipse.gef4.layout;

/**
 * An {@link ILayoutFilter} can be used to filter layout objects, so that they
 * are not reported to any {@link ILayoutAlgorithm} by the
 * {@link ILayoutContext}.
 */
public interface ILayoutFilter {

	/**
	 * Returns <code>true</code> to indicate that the given
	 * {@link IConnectionLayout} is irrelevant for layout. Otherwise returns
	 * <code>false</code>.
	 * 
	 * @param connectionLayout
	 *            The {@link IConnectionLayout} which may be irrelevant for
	 *            layout.
	 * @return <code>true</code> to indicate that the given
	 *         {@link IConnectionLayout} is irrelevant for layout, otherwise
	 *         <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(IConnectionLayout connectionLayout);

	/**
	 * Returns <code>true</code> to indicate that the given {@link INodeLayout}
	 * is irrelevant for layout. Otherwise returns <code>false</code>.
	 * 
	 * @param nodeLayout
	 *            The {@link INodeLayout} which may be irrelevant for layout.
	 * @return <code>true</code> to indicate that the given {@link INodeLayout}
	 *         is irrelevant for layout, otherwise <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(INodeLayout nodeLayout);

}
