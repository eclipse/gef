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

import org.eclipse.gef4.swtfx.IPulseListener;
import org.eclipse.gef4.swtfx.PulseThread;
import org.eclipse.gef4.swtfx.Scene;

public abstract class AbstractTransition implements IPulseListener {

	private IInterpolator interpolator = IInterpolator.LINEAR;

	/**
	 * Stores the duration of one animation cycle (in millis), i.e. the time it
	 * takes for the interpolator to switch from 0 to 1 or vice versa.
	 */
	private long cycleDurationMillis;

	/**
	 * The number of cycles to execute, i.e. how often the interpolator should
	 * switch between 0 and 1. A negative value signifies indefinite cycles.
	 */
	private double cycleCount = 1;

	/**
	 * Specifies if alternating cycles reverse the animation's direction. For
	 * example, if you have a PathTransition with autoReverse set to
	 * <code>true</code>, then the object will move back and forth on the path.
	 */
	private boolean autoReverse = true;

	// /**
	// * Stores the delay (in millis) for the start of the transition.
	// */
	// private long delayMillis;
	//
	// /**
	// * Stores an {@link IEventHandler} that is executed when the animation
	// * finishes. An animation finishes after
	// * <code>cycleCount * cycleDuration</code> (millis).
	// */
	// private IEventHandler<ActionEvent> onFinished;

	private long startMillis = 0;
	private long deltaMillis = 0;
	private boolean running = false;
	private boolean paused = false;

	/**
	 * Stores the scene, so that we can (de-)register ourself as an
	 * {@link IPulseListener} on its {@link PulseThread}.
	 */
	private Scene scene;

	public AbstractTransition(Scene scene, long durationMillis,
			double cycleCount, boolean autoReverse) {
		this.scene = scene;
		this.cycleDurationMillis = durationMillis;
		this.cycleCount = cycleCount;
		this.autoReverse = autoReverse;
	}

	public long getDuration() {
		return cycleDurationMillis;
	}

	public IInterpolator getInterpolator() {
		return interpolator;
	}

	@Override
	public void handlePulse(long elapsedMs) {
		if (running && !paused) {
			long millis = System.currentTimeMillis();

			if (startMillis == 0) {
				startMillis = millis;
			}

			deltaMillis = millis - startMillis;
			double totalTime = deltaMillis / (double) cycleDurationMillis;
			int fullCycles = (int) totalTime;
			double t = totalTime - fullCycles;

			if (totalTime > cycleCount && cycleCount > 0) {
				stop();
			} else {
				if (autoReverse && fullCycles % 2 == 1) {
					t = 1 - t;
				}
				step(getInterpolator().curve(t));
			}
		}
	}

	/**
	 * Pauses animation playback.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Starts animation playback.
	 */
	public void play() {
		if (running) {
			stop();
		}
		step(0);
		scene.getPulseThread().getListeners().add(this);
		running = true;
	}

	/**
	 * Resumes animation playback if previously paused.
	 */
	public void resume() {
		startMillis = System.currentTimeMillis() - deltaMillis;
		paused = false;
	}

	public void setInterpolator(IInterpolator interpolator) {
		this.interpolator = interpolator;
	}

	/**
	 * Updates scene graph nodes according to the interpolated time <i>t</i> in
	 * range <code>[0;1]</code>. The method is called on each pulse.
	 * 
	 * @param t
	 */
	abstract public void step(double t);

	/**
	 * Stops animation playback.
	 */
	public void stop() {
		if (!running) {
			return;
		}
		running = false;
		scene.getPulseThread().getListeners().remove(this);
		startMillis = 0;
		step(1);
	}

}
