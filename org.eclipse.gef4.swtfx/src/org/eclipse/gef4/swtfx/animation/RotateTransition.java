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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.Scene;

public class RotateTransition extends AbstractTransition {

	private INode node;
	private double startDeg;
	private double endDeg;

	public RotateTransition(Scene scene, long durationMillis,
			double cycleCount, boolean autoReverse, INode node,
			double startDeg, double endDeg) {
		super(scene, durationMillis, cycleCount, autoReverse);
		this.node = node;
		this.startDeg = startDeg;
		this.endDeg = endDeg;
	}

	@Override
	public void step(double t) {
		double deg = startDeg * (1 - t) + endDeg * t;
		node.setRotationAngle(Angle.fromDeg(deg));
	}

}
