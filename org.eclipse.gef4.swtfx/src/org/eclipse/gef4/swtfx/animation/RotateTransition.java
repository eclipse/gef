package org.eclipse.gef4.swtfx.animation;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.swtfx.INode;

public class RotateTransition extends AbstractTransition {

	private INode node;
	private double startDeg;
	private double endDeg;

	public RotateTransition(long durationMillis, INode node, double startDeg,
			double endDeg) {
		super(durationMillis);
		this.node = node;
		this.startDeg = startDeg;
		this.endDeg = endDeg;
	}

	@Override
	public void doStep(double t) {
		double deg = startDeg * (1 - t) + endDeg * t;
		node.setRotationAngle(Angle.fromDeg(deg));
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
