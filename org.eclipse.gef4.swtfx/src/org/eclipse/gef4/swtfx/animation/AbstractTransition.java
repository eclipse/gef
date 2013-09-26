package org.eclipse.gef4.swtfx.animation;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractTransition {

	private static final int PERIOD = 30;
	public static final double INDEFINITE = -1;

	private IInterpolator interpolator = IInterpolator.LINEAR;

	/**
	 * Stores the duration of one animation cycle (in millis), i.e. the time it
	 * takes for the interpolator to switch from 0 to 1 or vice versa.
	 */
	private long cycleDurationMillis;

	// /**
	// * The number of cycles to execute, i.e. how often the interpolator should
	// * switch between 0 and 1.
	// */
	// private double cycleCount;
	//
	// /**
	// * Specifies if alternating cycles reverse the animation's direction. For
	// * example, if you have a PathTransition with autoReverse set to
	// * <code>true</code>, then the object will move back and forth on the
	// path.
	// */
	// private boolean autoReverse;
	//
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

	private long startMillis;
	private boolean running;

	public AbstractTransition(long durationMillis) {
		this.cycleDurationMillis = durationMillis;
	}

	public abstract void doStep(double t);

	public abstract void doUpdate();

	public long getDuration() {
		return cycleDurationMillis;
	}

	public IInterpolator getInterpolator() {
		return interpolator;
	}

	public void play() {
		if (running) {
			stop();
		}

		running = true;
		step(0);

		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				long millis = System.currentTimeMillis();

				if (startMillis == 0) {
					startMillis = millis;
				}

				long deltaMillis = millis - startMillis;
				double t = deltaMillis / (double) cycleDurationMillis;

				if (t > 1) {
					cancel();
					stop();
				} else {
					step(getInterpolator().curve(t));
				}
			}
		}, 0, PERIOD);
	}

	public void setInterpolator(IInterpolator interpolator) {
		this.interpolator = interpolator;
	}

	public void step(double t) {
		doStep(t);
		doUpdate();
	}

	public void stop() {
		if (!running) {
			return;
		}
		running = false;
		startMillis = 0;
		step(1);
	}

}
