package org.eclipse.gef4.swtfx.animation;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swtfx.INode;

public class PathTransition extends AbstractTransition {

	private PathEvaluator evaluator;
	private INode node;

	public PathTransition(long durationMillis, Path path, INode node) {
		super(durationMillis);
		evaluator = new PathEvaluator(path);
		this.node = node;
	}

	@Override
	public void doStep(double t) {
		Point p = evaluator.get(t);
		node.setTranslateX(p.x);
		node.setTranslateY(p.y);
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
