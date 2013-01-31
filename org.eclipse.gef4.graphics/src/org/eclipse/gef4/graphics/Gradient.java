package org.eclipse.gef4.graphics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.color.Color;

// TODO: Move gradient rendering to some other place, probably this has got to do with image synthesis.
// TODO: Decide if Gradient is a value-class. If Gradient is not a value class, which behavior should go into it? Pre-computing colors and color selection?

/**
 * <p>
 * A Gradient is a specific {@link IFillMode} implementation which is used to
 * fill an area with a {@link Color}-gradient.
 * </p>
 * 
 * <p>
 * There are two different GradientFills available:
 * <ul>
 * <li>{@link Linear}</li>
 * <li>{@link Radial}</li>
 * </ul>
 * </p>
 * 
 * @author mwienand
 * 
 * @param <T>
 *            the type parameter specifies the extending type to prevent
 *            unnecessary type casts.
 * 
 */
public abstract class Gradient<T extends Gradient<?>> {

	/**
	 * <p>
	 * The CycleMode determines the behavior of a {@link Gradient} when applying
	 * it to an area which it does not fully fill in one iteration. For example,
	 * you can fill a {@link Rectangle} of width <code>100</code> with a
	 * {@link Linear} gradient of width <code>50</code>. Then, the CycleMode
	 * decides which {@link Color} to use for the other <code>50%</code>.
	 * </p>
	 * 
	 * <p>
	 * There are three different CycleModes available:
	 * <ul>
	 * <li>{@link CycleMode#NO_CYCLE}</li>
	 * <li>{@link CycleMode#REFLECT}</li>
	 * <li>{@link CycleMode#REPEAT}</li>
	 * </ul>
	 * </p>
	 */
	public static enum CycleMode {

		/**
		 * The NO_CYCLE {@link CycleMode} determines that the last specified
		 * {@link Color} is used to fill the area in surplus.
		 * 
		 * @see {@link CycleMode#REFLECT}
		 * @see {@link CycleMode#REPEAT}
		 */
		NO_CYCLE,

		/**
		 * The REFLECT {@link CycleMode} determines that the gradient
		 * {@link Color}s are mirrored at the border. For the prior
		 * {@link Rectangle} example, the result visually equals putting a
		 * mirror in the middle.
		 * 
		 * @see {@link CycleMode#NO_CYCLE}
		 * @see {@link CycleMode#REPEAT}
		 */
		REFLECT,

		/**
		 * The REPEAT {@link CycleMode} determines that the gradient
		 * {@link Color}s are repeated.
		 * 
		 * @see {@link CycleMode#NO_CYCLE}
		 * @see {@link CycleMode#REFLECT}
		 */
		REPEAT;

		/**
		 * The default {@link CycleMode} for {@link Gradient}s is
		 * {@link CycleMode#NO_CYCLE}
		 */
		public static final CycleMode DEFAULT = NO_CYCLE;

	}

	/**
	 * A GradientStop combines a distance from the gradient start on a
	 * percentage basis with a {@link Color} which is to be used from that
	 * distance on to fill a specific area.
	 */
	public static class GradientStop {

		private double percentualDistance;
		private Color color;

		/**
		 * Constructs a new {@link GradientStop} and associates the passed-in
		 * <i>percentualDistance</i> and <i>color</i> with it.
		 * 
		 * @param percentualDistance
		 *            the percentual distance from the gradient's start from
		 *            which on to use the given {@link Color}
		 * @param color
		 *            the {@link Color} to use when reaching the given
		 *            percentual distance from the gradient's start
		 */
		public GradientStop(double percentualDistance, Color color) {
			setPercentualDistance(percentualDistance);
			setColor(color);
		}

		/**
		 * Returns the associated {@link Color}.
		 * 
		 * @return the associated {@link Color}
		 */
		public Color getColor() {
			return color.getCopy();
		}

		/**
		 * Returns the associated percentual distance from the gradient's start.
		 * 
		 * @return the associated percentual distance
		 */
		public double getPercentualDistance() {
			return percentualDistance;
		}

		/**
		 * Associates the passed-in {@link Color} with this {@link GradientStop}
		 * .
		 * 
		 * @param color
		 *            the new {@link Color} to associate with this
		 *            {@link GradientStop}
		 */
		public void setColor(Color color) {
			this.color = color.getCopy();
		}

		/**
		 * Associates the passed-in <i>percentualDistance</i> with this
		 * {@link GradientStop}.
		 * 
		 * @param percentualDistance
		 *            the new percentual distance to associate with this
		 *            {@link GradientStop}
		 */
		public void setPercentualDistance(double percentualDistance) {
			this.percentualDistance = percentualDistance;
		}

	}

	/**
	 * A Linear {@link Gradient} advances only in one direction. It has definite
	 * start and end {@link Point}s and a set of
	 * {@link org.eclipse.gef4.graphics.Gradient.GradientStop GradientStop}s
	 * which determine the {@link Color} at any given {@link Point}.
	 */
	public static class Linear extends Gradient<Linear> {

		private Point start;
		private Point end;

		/**
		 * <p>
		 * Constructs a new {@link org.eclipse.gef4.graphics.Gradient.Linear
		 * Linear} representing a {@link Gradient} from the given start
		 * {@link Point} to the given end {@link Point}.
		 * </p>
		 * 
		 * <p>
		 * The {@link org.eclipse.gef4.graphics.Gradient.GradientStop
		 * GradientStop}s can be added afterwards via the
		 * {@link #addStop(double, Color)} method.
		 * </p>
		 * 
		 * @param from
		 *            the {@link Point} at which the {@link Gradient.Linear}
		 *            starts
		 * @param to
		 *            the {@link Point} at which the {@link Gradient.Linear}
		 *            ends
		 */
		public Linear(Point from, Point to) {
			this(from, to, CycleMode.DEFAULT);
		}

		/**
		 * <p>
		 * Constructs a new {@link Gradient.Linear} from the given start
		 * {@link Point} to the given end {@link Point} spreading as specified
		 * by the given {@link org.eclipse.gef4.graphics.Gradient.CycleMode
		 * CycleMode}.
		 * </p>
		 * 
		 * <p>
		 * The {@link org.eclipse.gef4.graphics.Gradient.GradientStop
		 * GradientStop}s can be added afterwards via the
		 * {@link #addStop(double, Color)} method.
		 * </p>
		 * 
		 * @param from
		 *            the {@link Point} at which the {@link Gradient.Linear}
		 *            starts
		 * @param to
		 *            the {@link Point} at which the {@link Gradient.Linear}
		 *            ends
		 * @param cycleMode
		 *            the {@link org.eclipse.gef4.graphics.Gradient.CycleMode}
		 *            which specifies how the {@link Gradient} spreads outside
		 *            of its main area
		 */
		public Linear(Point from, Point to, CycleMode cycleMode) {
			super();
			start = new Point(from);
			end = new Point(to);
			setCycleMode(cycleMode);
		}

		@Override
		protected Linear clone() throws CloneNotSupportedException {
			return getCopy();
		}

		@Override
		public double computePercentualDistance(Point p) {
			Vector direction = new Vector(start, end);
			if (direction.isNull()) {
				return 0;
			}
			Vector unitDirection = direction.getNormalized();
			double d = new Vector(p).getDotProduct(unitDirection)
					/ direction.getLength();
			return d;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Gradient.Linear) {
				Gradient.Linear o = (Gradient.Linear) obj;
				return start.equals(o.start) && end.equals(o.end)
						&& Arrays.equals(getStops(), o.getStops());
			}
			return false;
		}

		@Override
		public Linear getCopy() {
			return new Linear(start, end, getCycleMode()).setStops(getStops());
		}

		public Point getEnd() {
			return end.getCopy();
		}

		public Point getStart() {
			return start.getCopy();
		}

		public void setEnd(Point end) {
			this.end.setLocation(end);
		}

		public void setStart(Point start) {
			this.start.setLocation(start);
		}

	}

	/**
	 * A Radial {@link Gradient} is defined by an {@link Ellipse} and a focus
	 * {@link Point}. A set of
	 * {@link org.eclipse.gef4.graphics.Gradient.GradientStop GradientStop}s
	 * determine the {@link Color} at any given {@link Point}. The focus
	 * {@link Point} specifies the origin of a radial gradient (percentual
	 * distance = 0). The perimeter of the {@link Ellipse} specifies the border
	 * of a radial gradient (percentual distance = 1).
	 */
	public static class Radial extends Gradient<Radial> {

		private Ellipse boundary;
		private Point focus;

		/**
		 * Constructs a new {@link Gradient.Radial} with its boundary set to the
		 * passed-in {@link Ellipse}. The {@link Ellipse#getCenter() center}
		 * {@link Point} of the {@link Ellipse} is used as the focal
		 * {@link Point}.
		 * 
		 * @param boundary
		 */
		public Radial(Ellipse boundary) {
			this(boundary, boundary.getCenter());
		}

		/**
		 * Constructs a new {@link Gradient.Radial} from the passed-in values.
		 * 
		 * @param boundary
		 * @param focus
		 */
		public Radial(Ellipse boundary, Point focus) {
			super();

			if (boundary == null) {
				throw new IllegalArgumentException(
						"The Gradient.Radial boundary parameter may not be null.");
			}
			this.boundary = boundary.getCopy();

			if (!boundary.contains(focus)) {
				throw new IllegalArgumentException(
						"The given focal Point may only lie inside the specified boundary.");
			}
			this.focus = focus.getCopy();
		}

		public Radial(Ellipse boundary, Point focus, CycleMode cycleMode) {
			this(boundary, focus);
			setCycleMode(cycleMode);
		}

		@Override
		protected Radial clone() throws CloneNotSupportedException {
			return getCopy();
		}

		@Override
		public double computePercentualDistance(Point p) {
			double ratio;

			if (p.equals(focus)) {
				return 0;
			} else {
				Vector focusLineDirection = new Vector(focus, p);

				Line positiveFocusLine = new Line(focus,
						p.getTranslated(focusLineDirection
								.getNormalized()
								.getMultiplied(
										boundary.getWidth()
												+ boundary.getHeight())
								.toPoint()));

				Point[] intersections = boundary
						.getIntersections(positiveFocusLine);

				if (intersections.length != 1) {
					throw new IllegalStateException(
							"There may always be an intersection. (Ellipse = "
									+ boundary
									+ ", Line = "
									+ positiveFocusLine
									+ ".) This is a bug. It would be awsome if you create a ticket at bugs.eclipse.org containing this error message.");
				}

				double ds = new Vector(focus, intersections[0]).getLength();
				double dp = focusLineDirection.getLength();
				ratio = ds == 0 ? 0 : dp / ds;
			}

			return ratio;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Gradient.Radial) {
				Gradient.Radial o = (Gradient.Radial) obj;
				return boundary.equals(o.boundary) && focus.equals(o.focus)
						&& Arrays.equals(getStops(), o.getStops());
			}
			return false;
		}

		public Ellipse getBoundary() {
			return boundary.getCopy();
		}

		@Override
		public Radial getCopy() {
			return new Radial(boundary, focus, getCycleMode())
					.setStops(getStops());
		}

		public Point getFocus() {
			return focus.getCopy();
		}

		public void setBoundary(Ellipse boundary) {
			this.boundary.setBounds(boundary.getBounds());
		}

		public void setFocus(Point focus) {
			this.focus.setLocation(focus);
		}

	}

	private SortedSet<GradientStop> stops;

	private CycleMode cycle;

	/**
	 * Constructs a new {@link Gradient} with an empty set of
	 * {@link GradientStop}s.
	 */
	public Gradient() {
		stops = new TreeSet<GradientStop>(new Comparator<GradientStop>() {
			@Override
			public int compare(GradientStop a, GradientStop b) {
				double aDist = a.getPercentualDistance();
				double bDist = b.getPercentualDistance();
				if (aDist == bDist) {
					return 0;
				} else if (aDist < bDist) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		cycle = CycleMode.DEFAULT;
	}

	/**
	 * Adds another {@link GradientStop} for the passed-in values to this
	 * {@link Gradient}.
	 * 
	 * @param percentualDistance
	 *            the percentual distance at which the {@link GradientStop} is
	 *            created
	 * @param color
	 *            the {@link Color} that is used from the new
	 *            {@link GradientStop} on
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T addStop(double percentualDistance, Color color) {
		if (percentualDistance > 1) {
			throw new IllegalArgumentException(
					"The percentual distance from a gradient's start may not exceed 1.");
		}
		stops.add(new GradientStop(percentualDistance, color));
		return (T) this;
	}

	/**
	 * <p>
	 * Computes the percentual distance from the origin of this {@link Gradient}
	 * to the given {@link Point}. The percentual distance is not necessarily
	 * between 0 and 1. It can be any value, depending on the absolute distance
	 * from the origin of this {@link Gradient} to the given {@link Point} and
	 * the size of the {@link Gradient}.
	 * </p>
	 * 
	 * <p>
	 * Note, that the origin of a {@link Gradient} may not be a single point.
	 * For a {@link Gradient.Linear} the origin really is an infinite line
	 * perpendicular to the direction of the {@link Gradient.Linear}. For a
	 * {@link Gradient.Radial} the origin is a single point.
	 * </p>
	 * 
	 * @param p
	 *            the {@link Point} for which its percentual distance to this
	 *            {@link Gradient}'s origin is to be computed
	 * @return the percentual distance form the origin of this {@link Gradient}
	 *         to the given {@link Point}
	 */
	abstract public double computePercentualDistance(Point p);

	public Color getColorAt(Point p) {
		double d = computePercentualDistance(p);
		d = normalizePercentualDistance(d);
		return getPercentualColor(d);
	}

	/**
	 * Returns a copy of this {@link Gradient}.
	 * 
	 * @return a copy of this {@link Gradient}
	 */
	public abstract T getCopy();

	/**
	 * Returns the currently active {@link CycleMode}.
	 * 
	 * @return the currently active {@link CycleMode}
	 */
	public CycleMode getCycleMode() {
		return cycle;
	}

	/**
	 * @param normalizedDistance
	 * @return
	 */
	private Color getPercentualColor(double normalizedDistance) {
		GradientStop[] stops = getStops();

		if (stops.length < 2) {
			throw new IllegalStateException(
					"At least 2 GradientStops required.");
		}

		GradientStop from = stops[0], to = stops[1];
		double blendRatio = 0;

		for (int i = 0; i < stops.length; i++) {
			double stopDistance = stops[i].getPercentualDistance();

			if (normalizedDistance < stopDistance) {
				break;
			}

			from = stops[i];
			to = i == stops.length - 1 ? from : stops[i + 1];

			double nextStopDistance = to.getPercentualDistance();

			if (nextStopDistance <= stopDistance) {
				blendRatio = 0;
			} else {
				blendRatio = (normalizedDistance - stopDistance)
						/ (nextStopDistance - stopDistance);
			}
		}

		return to.getColor().getBlended(from.getColor(), blendRatio);
	}

	/**
	 * Returns the sorted list of {@link GradientStop}s specified for this
	 * {@link Gradient}.
	 * 
	 * @return the sorted list of {@link GradientStop}s
	 */
	public GradientStop[] getStops() {
		GradientStop[] stops = new GradientStop[this.stops.size()];
		int i = 0;
		for (GradientStop stop : this.stops) {
			stops[i++] = new GradientStop(stop.getPercentualDistance(),
					stop.getColor());
		}
		return stops;
	}

	/**
	 * Normalizes the given percentual distance according to the current
	 * {@link CycleMode}. The normalized percentual distance is in the range
	 * <code>[0;1]</code>.
	 * 
	 * @param d
	 * @return
	 */
	private double normalizePercentualDistance(double d) {
		if (getCycleMode() == CycleMode.NO_CYCLE && d > 1) {
			d = 1;
		} else if (getCycleMode() == CycleMode.REPEAT && d > 1) {
			d = d - (int) d;
		} else if (getCycleMode() == CycleMode.REFLECT && d > 1) {
			d -= 2 * (int) (d / 2);
			if (d > 1) {
				d = 2 - d;
			}
		}
		return d;
	}

	/**
	 * Sets the {@link CycleMode} to use for this {@link Gradient} to the
	 * passed-in value.
	 * 
	 * @param cycle
	 *            the {@link CycleMode} to use
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setCycleMode(CycleMode cycle) {
		this.cycle = cycle;
		return (T) this;
	}

	/**
	 * Sets the {@link GradientStop}s of this {@link Gradient} to the given
	 * values.
	 * 
	 * @param stops
	 *            the new {@link GradientStop}s for this {@link Gradient}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setStops(GradientStop... stops) {
		this.stops.clear();
		if (stops == null || stops.length == 0) {
			return (T) this;
		}
		for (GradientStop gs : stops) {
			addStop(gs.getPercentualDistance(), gs.getColor());
		}
		return (T) this;
	}

}
