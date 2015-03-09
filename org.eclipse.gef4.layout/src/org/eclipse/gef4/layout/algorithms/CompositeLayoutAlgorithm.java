/*******************************************************************************
 * Copyright (c) 2005-2010 The Chisel Group and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela 
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutAlgorithm;

public class CompositeLayoutAlgorithm implements ILayoutAlgorithm {

	private ILayoutContext context = null;
	private ILayoutAlgorithm[] algorithms = null;

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
