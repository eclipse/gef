package org.eclipse.gef4.swtfx.animation;

public class ParallelTransition extends AbstractTransition {

	private static long getTotalDuration(AbstractTransition[] transitions) {
		long duration = 0;
		for (AbstractTransition tr : transitions) {
			duration += tr.getDuration();
		}
		return duration;
	}

	private AbstractTransition[] transitions;

	public ParallelTransition(AbstractTransition... transitions) {
		super(getTotalDuration(transitions));
		this.transitions = transitions;
	}

	@Override
	public void doStep(double t) {
		for (AbstractTransition tr : transitions) {
			tr.doStep(tr.getInterpolator().curve(t));
		}
	}

	@Override
	public void doUpdate() {
		// XXX: scene.refreshVisuals is called by all of them
		if (transitions.length < 1) {
			return;
		}
		transitions[0].doUpdate();
	}

}
