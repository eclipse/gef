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

public class HBox extends Pane {

	public HBox(Composite parent) {
		super(parent);
	}

	@Override
	public void doLayoutChildren() {
		// resizeRelocate() all managed children

		// TODO: Figure out how resizing affects layouting.

		// System.out.println(this + " :: w x h = " + getWidth() + " x "
		// + getHeight());

		double x = 0;
		for (INode child : getChildNodes()) {
			if (child.isManaged()) {
				double w = child.computePrefWidth(getHeight());
				double h = child.computePrefHeight(w);
				child.relocate(x, 0);
				if (child.isResizable()) {
					child.resize(w, h);
				}
				x += w;
				x++; // XXX: double/int problem => some pixel errors
				/*
				 * TODO: Respect baseline-offset setting, allow padding/spacing
				 * constraints, allow grow-priority constraint.
				 */
			}
		}
		setWidth(x);
	}

}
