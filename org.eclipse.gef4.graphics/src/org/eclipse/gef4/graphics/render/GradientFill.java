package org.eclipse.gef4.graphics.render;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;

/**
 * <p>
 * A GradientFill is a specific {@link IFillMode} implementation which is used
 * to fill an area with a {@link Color}-gradient.
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
public abstract class GradientFill<T extends GradientFill<?>> implements
		IFillMode {

	/**
	 * <p>
	 * The CycleMode determines the behavior of a {@link GradientFill} when
	 * applying it to an area which it does not fully fill in one iteration. For
	 * example, you can fill a {@link Rectangle} of width <code>100</code> with
	 * a {@link Linear} gradient of width <code>50</code>. Then, the CycleMode
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
		 * The default {@link CycleMode} for {@link GradientFill}s is
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
			return color;
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
			this.color = color;
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
	 * A Linear {@link GradientFill} advances only in one direction. It has
	 * definite start and end {@link Point}s and a set of
	 * {@link org.eclipse.gef4.graphics.render.GradientFill.GradientStop
	 * GradientStop}s which determine the {@link Color} at any given
	 * {@link Point}.
	 */
	public static class Linear extends GradientFill<Linear> {

		private Point start;
		private Point end;

		/**
		 * <p>
		 * Constructs a new
		 * {@link org.eclipse.gef4.graphics.render.GradientFill.Linear Linear}
		 * representing a {@link GradientFill} from the given start
		 * {@link Point} to the given end {@link Point}.
		 * </p>
		 * 
		 * <p>
		 * The
		 * {@link org.eclipse.gef4.graphics.render.GradientFill.GradientStop
		 * GradientStop}s can be added afterwards via the
		 * {@link #addStop(double, Color)} method.
		 * </p>
		 * 
		 * @param from
		 *            the {@link Point} at which the {@link GradientFill.Linear}
		 *            starts
		 * @param to
		 *            the {@link Point} at which the {@link GradientFill.Linear}
		 *            ends
		 */
		public Linear(Point from, Point to) {
			this(from, to, CycleMode.DEFAULT);
		}

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
			Vector unitDirection = direction.getNormalized();
			double d = new Vector(p).getDotProduct(unitDirection)
					/ direction.getLength();
			return d;
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
	 * A Radial {@link GradientFill} is defined by an {@link Ellipse} and a
	 * focus {@link Point}. A set of
	 * {@link org.eclipse.gef4.graphics.render.GradientFill.GradientStop
	 * GradientStop}s determine the {@link Color} at any given {@link Point}.
	 * The focus {@link Point} specifies the origin of a radial gradient
	 * (percentual distance = 0). The perimeter of the {@link Ellipse} specifies
	 * the border of a radial gradient (percentual distance = 1).
	 */
	public static class Radial extends GradientFill<Radial> {

		private Ellipse boundary;
		private Point focus;

		public Radial(Ellipse boundary) {
			this(boundary, boundary.getCenter());
		}

		public Radial(Ellipse boundary, Point focus) {
			super();

			if (boundary == null) {
				throw new IllegalArgumentException(
						"The GradientFill.Radial boundary parameter may not be null.");
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
					// TODO: Add a note to the exception's message encouraging
					// users to submit a bug ticket.
					throw new IllegalStateException(
							"There may always be an intersection. (Ellipse = "
									+ boundary + ", Line = "
									+ positiveFocusLine);
				}

				double ds = new Vector(focus, intersections[0]).getLength();
				double dp = focusLineDirection.getLength();
				ratio = ds == 0 ? 0 : dp / ds;
			}

			return ratio;
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
	 * Constructs a new {@link GradientFill} with an empty set of
	 * {@link GradientStop}s.
	 */
	public GradientFill() {
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
	 * {@link GradientFill}.
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

	abstract public double computePercentualDistance(Point p);

	// public T addStop(GradientStop gradientStop) {
	// if (gradientStop.percentualDistance > 1) {
	// throw new IllegalArgumentException(
	// "The percentual distance from a gradient's start may not exceed 1.");
	// }
	// stops.add(gradientStop.getCopy());
	// return (T) this;
	// }

	@Override
	public Color getColorAt(Point p) {
		double d = computePercentualDistance(p);
		d = normalizePercentualDistance(d);
		return getPercentualColor(d);
	}

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
		double blend = 0;

		for (int i = 0; i < stops.length; i++) {
			double stopDistance = stops[i].getPercentualDistance();

			if (normalizedDistance < stopDistance) {
				break;
			}

			from = stops[i];
			to = i == stops.length - 1 ? from : stops[i + 1];

			double nextStopDistance = to.getPercentualDistance();

			if (nextStopDistance <= stopDistance) {
				blend = 0;
			} else {
				blend = (normalizedDistance - stopDistance)
						/ (nextStopDistance - stopDistance);
			}
		}

		return to.getColor().getBlended(from.getColor(), blend);
	}

	/**
	 * Returns the sorted list of {@link GradientStop}s specified for this
	 * {@link GradientFill}.
	 * 
	 * @return the sorted list of {@link GradientStop}s
	 */
	public GradientStop[] getStops() {
		// TODO: deep copy?
		return stops.toArray(new GradientStop[] {});
	}

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
	 * Sets the {@link CycleMode} to use for this {@link GradientFill} to the
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
	 * Sets the {@link GradientStop}s of this {@link GradientFill} to the given
	 * values.
	 * 
	 * @param stops
	 *            the new {@link GradientStop}s for this {@link GradientFill}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setStops(GradientStop... stops) {
		this.stops.clear();
		if (stops == null || stops.length == 0) {
			return (T) this;
		}
		for (GradientStop gs : stops) {
			// addStop(gs);
			addStop(gs.getPercentualDistance(), gs.getColor());
		}
		return (T) this;
	}

}
