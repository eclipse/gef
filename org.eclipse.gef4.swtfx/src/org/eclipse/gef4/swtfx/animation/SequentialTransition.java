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

	public SequentialTransition(double cycleCount, boolean autoReverse,
			AbstractTransition... transitions) {
		super(getTotalDuration(transitions), cycleCount, autoReverse);
		this.transitions = transitions;
	}

	@Override
	protected Scene getScene() {
		if (transitions != null && transitions.length > 0) {
			return transitions[0].getScene();
		}
		return null;
	}

	@Override
	public void step(double t) {
		long accumDuration = 0;
		for (AbstractTransition trans : transitions) {
			long durationEnd = accumDuration + trans.getDuration();
			long currentDuration = (long) (t * getDuration());
			if (accumDuration <= currentDuration
					&& currentDuration < durationEnd) {
				double stretched = (currentDuration - accumDuration)
						/ (double) trans.getDuration();
				trans.step(stretched);
				break;
			}
			accumDuration = durationEnd;
		}
	}
}
