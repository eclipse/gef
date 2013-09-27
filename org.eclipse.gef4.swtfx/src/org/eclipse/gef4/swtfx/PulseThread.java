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
package org.eclipse.gef4.swtfx;

import java.util.ArrayList;
import java.util.List;

/**
 * A PulseThread invokes a list of {@link IPulseListener}s at a fixed rate. The
 * pulse rate (also referred to as frequency, frame rate, or frames per second
 * (fps)) is set by the user. The pulse rate is fixed, i.e. the PulseThread will
 * sleep an appropriate amount of time between listener invocations if
 * necessary. If the PulseThread cannot satisfy the {@link #setDesiredFps(int)},
 * it will invoke listeners as fast as possible. The real frame rate is always
 * accessible via {@link #getRealFps()}.
 * 
 * @author mwienand
 * 
 */
public class PulseThread extends Thread {

	public static final int DEFAULT_FPS = 30;

	/**
	 * Reference to the {@link Scene} which this PulseThread belongs to.
	 */
	private Scene scene;

	/**
	 * Desired frames per second (FPS), i.e. desired frequency of the pulse in
	 * Hz.
	 */
	private int desiredFps;

	/**
	 * Real frames per second (FPS), i.e. real frequency of the pulse in Hz. If
	 * {@link #desiredFps} cannot be satisfied, <i>realFps</i> will differ from
	 * that value. <i>realFps</i> is measured by counting frames for one second.
	 * Therefore, it only updated once per second.
	 */
	private long realFps;

	/**
	 * Time to wait between pulses. This is calculated based on the
	 * {@link #desiredFps}.
	 */
	private long desiredElapsedMs;

	/**
	 * List of PulseListener s to notify on each pulse.
	 */
	private List<IPulseListener> listeners;

	public PulseThread(Scene scene) {
		this(scene, DEFAULT_FPS);
	}

	public PulseThread(Scene scene, int desiredFps) {
		this.scene = scene;
		setDesiredFps(desiredFps);
		listeners = new ArrayList<IPulseListener>();
	}

	public long getDesiredFps() {
		return desiredFps;
	}

	/**
	 * You have to manage the listeners by yourself. Use the returned
	 * {@link List} to add/remove listeners.
	 * 
	 * @return {@link List} of registered {@link IPulseListener}s
	 */
	public List<IPulseListener> getListeners() {
		return listeners;
	}

	public long getRealFps() {
		return realFps;
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void run() {
		// initialize fps counter
		long counterMs = 1000;
		long frames = 0;

		/*
		 * To accommodate for differing execution times we go to sleep after
		 * each execution (if we have some time left). The amount of sleep that
		 * we get is based on previous iterations as follows: we use a variable
		 * <i>sleepMs</i> which is incremented if we did not sleep enough and
		 * decremented if we have slept too long in the past.
		 * 
		 * This variable is initialized to <code>10</code> here as most
		 * operating systems provide a timer granularity of 10 millis.
		 */
		long sleepMs = 10;

		// initialize timer
		long lastMs = System.currentTimeMillis();
		long nowMs = lastMs;
		long elapsedMs = 0;

		// stop pulse when scene is disposed
		while (!scene.isDisposed()) {
			// execute pulse
			for (IPulseListener l : listeners) {
				l.handlePulse(elapsedMs);
			}

			// update timer
			nowMs = System.currentTimeMillis();
			elapsedMs = nowMs - lastMs;
			lastMs = nowMs;

			// update fps
			counterMs -= elapsedMs;
			frames++;
			if (counterMs < 0) {
				realFps = frames;
				counterMs = 1000;
				frames = 0;
			}

			// take a (very short) break
			if (elapsedMs < desiredElapsedMs) {
				sleepMs++;
				try {
					Thread.sleep(sleepMs);
				} catch (InterruptedException e) {
					// we do not expect to get interrupted often!
					// one interruption does not cause any damage, though
					// TODO: build a protection against frequent interruptions
				}
			} else if (elapsedMs > desiredElapsedMs && sleepMs > 0) {
				sleepMs--;
			}
		}
	}

	/**
	 * Sets the desired FPS (frames per second), i.e. the frequency of the pulse
	 * (in Hz). If possible, the same number of pulses will be generated every
	 * second.
	 * 
	 * @param desiredFps
	 */
	public void setDesiredFps(int desiredFps) {
		this.desiredFps = desiredFps;
		desiredElapsedMs = 1000 / desiredFps;
	}

}
