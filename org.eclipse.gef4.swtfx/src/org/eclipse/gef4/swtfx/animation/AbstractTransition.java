package org.eclipse.gef4.swtfx.animation;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractTransition {

	public static final int PERIOD = 30;
	public static final double INDEFINITE = -1;

	private IInterpolator interpolator = IInterpolator.LINEAR;

	private long durationMillis;
	private long startMillis;
	private boolean running;

	public AbstractTransition(long durationMillis) {
		this.durationMillis = durationMillis;
	}

	public abstract void doStep(double t);

	public abstract void doUpdate();

	public long getDuration() {
		return durationMillis;
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
				double t = deltaMillis / (double) durationMillis;

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
