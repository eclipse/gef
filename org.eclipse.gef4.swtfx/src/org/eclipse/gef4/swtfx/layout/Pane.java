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
package org.eclipse.gef4.swtfx.layout;

import org.eclipse.gef4.swtfx.AbstractParent;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.swt.widgets.Composite;

public class Pane extends AbstractParent {

	public Pane(Composite parent) {
		super(parent);
		if (parent instanceof IParent) {
			((IParent) parent).addChildNodes(this);
		}
	}

	@Override
	public void setParentNode(IParent parent) {
		IParent parentNode = getParentNode();
		if (parentNode != null) {
			parentNode.getChildNodes().remove(this);
		}
		parent.getChildNodes().add(this);
	}

}
