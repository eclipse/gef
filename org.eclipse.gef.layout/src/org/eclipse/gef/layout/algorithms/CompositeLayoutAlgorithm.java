/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;

/**
 * The {@link CompositeLayoutAlgorithm} combines multiple
 * {@link ILayoutAlgorithm}s. When doing a layout-pass, all the algorithms are
 * applied in sequence.
 * 
 * @author Ian Bull
 * @author Mateusz Matela
 * @author mwienand
 */
public class CompositeLayoutAlgorithm implements ILayoutAlgorithm {

	private ILayoutAlgorithm[] algorithms = null;

	/**
	 * Constructs a new {@link CompositeLayoutAlgorithm} that combines the given
	 * {@link ILayoutAlgorithm}s.
	 * 
	 * @param algorithms
	 *            The {@link ILayoutAlgorithm}s that are combined by this
	 *            {@link CompositeLayoutAlgorithm}.
	 */
	public CompositeLayoutAlgorithm(ILayoutAlgorithm[] algorithms) {
		this.algorithms = algorithms;
	}

	public void applyLayout(LayoutContext context, boolean clean) {
		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i].applyLayout(context, clean);
		}
	}

}
