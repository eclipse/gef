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
package org.eclipse.gef4.swtfx.animation;

import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.Scene;

public class ScaleTransition extends AbstractTransition {

	private INode node;
	private double startScaleX;
	private double startScaleY;
	private double endScaleX;
	private double endScaleY;

	public ScaleTransition(Scene scene, long durationMillis, double cycleCount,
			boolean autoReverse, INode node, double startScaleX,
			double startScaleY, double endScaleX, double endScaleY) {
		super(scene, durationMillis, cycleCount, autoReverse);
		this.startScaleX = startScaleX;
		this.startScaleY = startScaleY;
		this.endScaleX = endScaleX;
		this.endScaleY = endScaleY;
		this.node = node;
	}

	@Override
	public void step(double t) {
		double sx = startScaleX * (1 - t) + endScaleX * t;
		double sy = startScaleY * (1 - t) + endScaleY * t;
		node.setScaleX(sx);
		node.setScaleY(sy);
	}

}
