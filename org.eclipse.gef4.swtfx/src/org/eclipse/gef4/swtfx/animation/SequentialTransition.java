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

import org.eclipse.gef4.swtfx.Scene;

public class SequentialTransition extends AbstractTransition {

	private static long getTotalDuration(AbstractTransition[] transitions) {
		long duration = 0;
		for (AbstractTransition t : transitions) {
			duration += t.getDuration();
		}
		return duration;
	}

	private AbstractTransition[] transitions;

	public SequentialTransition(Scene scene, double cycleCount,
			boolean autoReverse, AbstractTransition... transitions) {
		super(scene, getTotalDuration(transitions), cycleCount, autoReverse);
		this.transitions = transitions;
	}

	@Override
	public void step(double t) {
		for (AbstractTransition trans : transitions) {
			trans.step(t);
		}
	}
}
