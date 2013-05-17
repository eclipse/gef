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
package org.eclipse.gef4.swt.canvas;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;

public class Group extends org.eclipse.swt.widgets.Canvas implements
		PaintListener {

	// figures are rendered below anything else
	private List<IFigure> figures = new LinkedList<IFigure>();

	public Group(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(this);
	}

	public List<IFigure> getFigures() {
		return figures;
	}

	@Override
	public void paintControl(PaintEvent e) {
		GraphicsContext g = new GraphicsContext(e.gc);

		// our rendering order is the reverse of SWT's
		for (IFigure figure : getFigures()) {
			g.save();
			g.setUpGuard();
			figure.paint(g);
			g.takeDownGuard();
			g.restore();
		}
	}

}
