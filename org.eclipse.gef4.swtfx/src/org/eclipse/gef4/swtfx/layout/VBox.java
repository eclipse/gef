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

import org.eclipse.gef4.swtfx.INode;
import org.eclipse.swt.widgets.Composite;

public class VBox extends Pane {

	public VBox(Composite parent) {
		super(parent);
	}

	@Override
	public void doLayoutChildren() {
		// resize-relocate all managed children
		double y = 0;
		for (INode child : getChildNodes()) {
			if (child.isManaged()) {
				double h = child.computePrefHeight(getWidth());
				double w = child.computePrefWidth(h);
				child.relocate(0, y);
				if (child.isResizable()) {
					child.resize(w, h);
				}
				y += h;
				y++; // XXX: double/int problem => some pixel errors
				/*
				 * TODO: Respect baseline-offset setting, allow padding/spacing
				 * constraints, allow grow-priority constraint.
				 */
			}
		}
		setHeight(y);
	}
}
