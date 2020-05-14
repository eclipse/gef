/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela 
 *               Ian Bull
 *               
 ******************************************************************************/
package org.eclipse.gef.layout;

/**
 * An interface for all layout algorithms.
 * 
 * 
 */
public interface ILayoutAlgorithm {

	/**
	 * Makes this algorithm perform layout computation and apply it to its
	 * context.
	 * 
	 * @param layoutContext
	 *            The {@link LayoutContext} that provides all relevant
	 *            information about what to layout.
	 * @param clean
	 *            if true the receiver should assume that the layout context has
	 *            changed significantly and recompute the whole layout even if
	 *            it keeps track of changes with listeners. False can be used
	 *            after dynamic layout in a context is turned back on so that
	 *            layout algorithm working in background can apply accumulated
	 *            changes. Static layout algorithm can ignore this call entirely
	 *            if clean is false.
	 */
	public void applyLayout(LayoutContext layoutContext, boolean clean);
}
