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

public class ParallelTransition extends AbstractTransition {

	/**
	 * Return longest duration of given transitions.
	 */
	private static long getTotalDuration(AbstractTransition[] transitions) {
		long duration = 0;
		for (AbstractTransition tr : transitions) {
			duration = Math.max(duration, tr.getDuration());
		}
		return duration;
	}

	private AbstractTransition[] transitions;

	public ParallelTransition(double cycleCount, boolean autoReverse,
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
		for (AbstractTransition tr : transitions) {
			tr.step(tr.getInterpolator().curve(t));
		}
	}

}
