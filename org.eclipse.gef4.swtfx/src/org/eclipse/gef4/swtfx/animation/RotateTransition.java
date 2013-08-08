package org.eclipse.gef4.swtfx.animation;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.swtfx.INode;

public class RotateTransition extends AbstractTransition {

	private INode node;
	private Angle start;
	private Angle end;

	public RotateTransition(long durationMillis, INode node, Angle start,
			Angle end) {
		super(durationMillis);
		this.node = node;
		this.start = start;
		this.end = end;
	}

	@Override
	public void doStep(double t) {
		double rad = start.rad() * (1 - t) + end.rad() * t;
		node.setRotationAngle(Angle.fromRad(rad));
	}

	@Override
	public void doUpdate() {
		node.getScene().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				node.getScene().refreshVisuals();
			}
		});
	}

}
