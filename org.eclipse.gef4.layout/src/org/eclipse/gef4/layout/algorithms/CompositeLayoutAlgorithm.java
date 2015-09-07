/*******************************************************************************
 * Copyright (c) 2005, 2015 The Chisel Group and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;

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

	private ILayoutContext context = null;
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

	public void applyLayout(boolean clean) {
		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i].applyLayout(clean);
		}
	}

	public void setLayoutContext(ILayoutContext context) {
		this.context = context;
		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i].setLayoutContext(context);
		}
	}

	public ILayoutContext getLayoutContext() {
		return context;
	}

}
