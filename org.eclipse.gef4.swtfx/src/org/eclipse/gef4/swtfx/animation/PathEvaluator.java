package org.eclipse.gef4.swtfx.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.projective.Straight3D;
import org.eclipse.gef4.geometry.projective.Vector3D;

public class PathEvaluator {

	private static final double EPSILON = 1;

	private static double[] computeAccumLengths(Line[] approxLines) {
		double[] accum = new double[approxLines.length];

		accum[0] = approxLines[0].getP1().getDistance(approxLines[0].getP2());

		for (int i = 1; i < approxLines.length; i++) {
			Line line = approxLines[i];
			accum[i] = accum[i - 1] + line.getP1().getDistance(line.getP2());
		}

		return accum;
	}

	private static double distanceToBaseline(BezierCurve curve) {
		Straight3D baseline = Straight3D.through(new Vector3D(curve.getP1()),
				new Vector3D(curve.getP2()));
		Point[] points = curve.getPoints();

		double maxDistance = 0;
		for (int i = 1; i < points.length - 1; i++) {
			// TODO: implement Line#getDistance(Point)
			double d = Math.abs(baseline.getSignedDistanceCW(new Vector3D(
					points[i])));
			if (d > maxDistance) {
				maxDistance = d;
			}
		}

		return maxDistance;
	}

	private static void toLineStrip(BezierCurve bezierCurve, double epsilon,
			List<Line> lines) {
		Stack<BezierCurve> parts = new Stack<BezierCurve>();
		parts.push(bezierCurve);

		while (!parts.isEmpty()) {
			BezierCurve curve = parts.pop();
			if (distanceToBaseline(curve) < epsilon) {
				lines.add(new Line(curve.getP1(), curve.getP2()));
			} else {
				parts.addAll(Arrays.asList(curve.split(0.5)));
			}
		}
	}

	private static Line[] toLineStrip(Path path, double maxDistance) {
		List<Line> lines = new ArrayList<Line>();

		Point[] p = null;
		Point last = null;
		Point start = null;

		for (Segment seg : path.getSegments()) {
			switch (seg.getType()) {
			case Path.Segment.MOVE_TO:
				last = seg.getPoints()[0];
				if (start == null) {
					start = last;
				}
				break;
			case Path.Segment.LINE_TO:
				lines.add(new Line(last, seg.getPoints()[0]));
				break;
			case Path.Segment.QUAD_TO:
				p = seg.getPoints();
				toLineStrip(new BezierCurve(last, p[0], p[1]), maxDistance,
						lines);
				break;
			case Path.Segment.CUBIC_TO:
				p = seg.getPoints();
				toLineStrip(new BezierCurve(last, p[0], p[1], p[2]),
						maxDistance, lines);
				break;
			case Path.Segment.CLOSE:
				lines.add(new Line(last, start));
				break;
			}
		}

		return lines.toArray(new Line[0]);
	}

	private Path path;
	private Line[] approxLines;
	private double[] accumLengths;

	public PathEvaluator(Path path) {
		setPath(path);
	}

	public Point get(double t) {
		// TODO: die if t outside [0;1]
		t = Math.min(1, Math.max(0, t));

		// find point at length = t * totalLength
		double length = t * accumLengths[accumLengths.length - 1];

		for (int i = 0; i < accumLengths.length; i++) {
			if (accumLengths[i] >= length || i == accumLengths.length - 1) {
				// approxLines[i] contains the point
				Line seg = approxLines[i];

				// truncate length to local segment
				if (i > 0) {
					length -= accumLengths[i - 1];
				}

				// return point at segment ratio = length / segment length
				// TODO: implement Line#getMagnitude() or something...
				double distance = seg.getP1().getDistance(seg.getP2());
				return seg.get(length / distance);
			}
		}

		// TODO: die here
		return null;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
		approxLines = toLineStrip(path, EPSILON);
		accumLengths = computeAccumLengths(approxLines);
	}

}
