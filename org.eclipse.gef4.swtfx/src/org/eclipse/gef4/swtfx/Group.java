/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.widgets.Composite;

public class Group implements IParent {

	private IParent parent;

	public Group() {
	}

	@Override
	public IParent getParentNode() {
		return parent;
	}

	@Override
	public Composite getSwtComposite() {
		IParent parent = this.parent;
		while (parent != null && !(parent instanceof Pane)) {
			return ((Pane) parent).getSwtComposite();
		}
		throw new IllegalStateException("Missing root Pane.");
	}

}
