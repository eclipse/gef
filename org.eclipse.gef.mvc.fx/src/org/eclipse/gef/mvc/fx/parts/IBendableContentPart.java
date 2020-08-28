/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #504480
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * An {@link IContentPart} that supports content related bend, i.e. manipulation
 * of control points.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <V>
 *            The visual node used by this {@link IBendableContentPart}.
 *
 */
public interface IBendableContentPart<V extends Node>
		extends ITransformableContentPart<V>, IResizableContentPart<V> {

	/**
	 * A representation of a bend point, which is defined either by a point or
	 * by a content anchorage to which the content is attached.
	 */
	public static class BendPoint {

		/**
		 * Computes the size from the given list of
		 * {@link IBendableContentPart.BendPoint}s as the size of the bounds
		 * around the unattached bend points.
		 *
		 * @param bendPoints
		 *            The list of {@link IBendableContentPart.BendPoint}s for
		 *            which to compute the size.
		 * @return The size of the bounds around the unattached bend points.
		 */
		static Dimension computeSize(List<BendPoint> bendPoints) {
			// determine min and max point to compute the bounds
			Point min = null;
			Point max = null;
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					Point pos = bp.getPosition();
					if (min == null) {
						// XXX: The first unattached bend-point determines the
						// initial values for min and max. Copies are used so
						// that
						// we can safely change min and max.
						min = pos.getCopy();
						max = min.getCopy();
					} else {
						// expand min and max
						if (min.x > pos.x) {
							min.x = pos.x;
						}
						if (min.y > pos.y) {
							min.y = pos.y;
						}
						if (max.x < pos.x) {
							max.x = pos.x;
						}
						if (max.y < pos.y) {
							max.y = pos.y;
						}
					}
				}
			}
			// XXX: min == null if there are no unattached bend points
			return min == null ? new Dimension()
					: new Dimension(max.x - min.x, max.y - min.y);
		}

		/**
		 * Computes the translation from the given list of
		 * {@link IBendableContentPart.BendPoint}s as the offset of the bounds
		 * around the unattached bend points.
		 *
		 * @param bendPoints
		 *            The list of {@link IBendableContentPart.BendPoint}s for
		 *            which to compute the translation.
		 * @return The translation of the bounds around the unattached bend
		 *         points.
		 */
		static Affine computeTranslation(List<BendPoint> bendPoints) {
			// iterate over the unattached bend-points to find the minimum
			Point min = null;
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					Point pos = bp.getPosition();
					if (min == null) {
						// initialize min
						// XXX: copy so it can safely be changed
						min = pos.getCopy();
					} else {
						// adjust min to the given position
						if (min.x > pos.x) {
							min.x = pos.x;
						}
						if (min.y > pos.y) {
							min.y = pos.y;
						}
					}
				}
			}
			// XXX: in case there are no unattached bend-points, an identity
			// transformation is returned
			return min == null ? new Affine()
					: new Affine(new Translate(min.x, min.y));
		}

		/**
		 * Resizes the given list of {@link IBendableContentPart.BendPoint}s
		 * according to the bounds-change that is given by the current offset,
		 * current size, and final size. The unattached
		 * {@link IBendableContentPart.BendPoint}s will remain their relative
		 * positions within their bounds.
		 *
		 * @param bendPoints
		 *            The list of {@link IBendableContentPart.BendPoint}s to
		 *            modify.
		 * @param currentX
		 *            The current x offset.
		 * @param currentY
		 *            The current y offset.
		 * @param currentSize
		 *            The current size.
		 * @param finalSize
		 *            The final size.
		 * @return The resized {@link IBendableContentPart.BendPoint}s.
		 */
		static List<BendPoint> resize(List<BendPoint> bendPoints,
				double currentX, double currentY, Dimension currentSize,
				Dimension finalSize) {
			// System.out.println(
			// "Resize from " + currentSize + " to " + finalSize + ".");

			// determine unattached bend points
			List<Point> points = new ArrayList<>();
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					points.add(bp.getPosition());
				}
			}

			// a) optimize for no unattached bend-points
			// b) optimize for a single bend-point (size = 0, 0)
			if (points.size() < 2) {
				return bendPoints;
			}

			// determine delta size
			double dw = finalSize.width - currentSize.width;
			double dh = finalSize.height - currentSize.height;

			// compute relative positions
			double[] relX = new double[points.size()];
			double[] relY = new double[relX.length];
			for (int i = 0; i < relX.length; i++) {
				Point p = points.get(i);
				relX[i] = (p.x - currentX) / currentSize.width;
				relY[i] = (p.y - currentY) / currentSize.height;
			}

			// resize bend points based on their relative positions
			// XXX: separate index for relX and relY because they only contain
			// unattached points
			int pointIndex = 0;
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					bp.getPosition().x += relX[pointIndex] * dw;
					bp.getPosition().y += relY[pointIndex] * dh;
					// XXX: increase point index only after an unattached
					// bend-point was processed
					pointIndex++;
				}
			}

			return bendPoints;
		}

		/**
		 * Transforms the given {@link List} of
		 * {@link IBendableContentPart.BendPoint}s according to the change
		 * specified by the given current and final {@link Affine}
		 * transformations.
		 *
		 * @param bendPoints
		 *            The {@link List} of
		 *            {@link IBendableContentPart.BendPoint}s to transform.
		 * @param currentTransform
		 *            The current {@link Affine} transformation.
		 * @param totalTransform
		 *            The final {@link Affine} transformation.
		 * @return The given, transformed {@link List} of
		 *         {@link IBendableContentPart.BendPoint}s.
		 */
		static List<BendPoint> transform(List<BendPoint> bendPoints,
				Affine currentTransform, Affine totalTransform) {
			// compute delta transform
			Affine inverse;
			try {
				inverse = currentTransform.createInverse();
			} catch (NonInvertibleTransformException e) {
				throw new RuntimeException(e);
			}
			Transform deltaTransform = new Affine(
					inverse.createConcatenation(totalTransform));
			// optimize for identity transform
			if (deltaTransform.isIdentity()) {
				return bendPoints;
			}

			// System.out.println("Transform by " + deltaTransform.getTx() + ",
			// "
			// + deltaTransform.getTy() + ".");

			AffineTransform tx = FX2Geometry.toAffineTransform(deltaTransform);
			// transform unattached bend points in-place
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					bp.getPosition().transform(tx);
				}
			}
			return bendPoints;
		}

		private Object contentAnchorage;

		private Point position;

		/**
		 * Creates a new attached bend point.
		 *
		 * @param contentAnchorage
		 *            The content anchorage, to which the point is attached.
		 * @param position
		 *            A position (hint) for the attached bend point.
		 */
		public BendPoint(Object contentAnchorage, Point position) {
			if (contentAnchorage == null) {
				throw new IllegalArgumentException(
						"contentAnchorage may not be null.");
			}
			if (position == null) {
				throw new IllegalArgumentException("position may not be null");
			}
			this.contentAnchorage = contentAnchorage;
			this.position = position.getCopy();
		}

		/**
		 * Creates a new unattached bend point.
		 *
		 * @param position
		 *            The position of the bend point.
		 */
		public BendPoint(Point position) {
			if (position == null) {
				throw new IllegalArgumentException("position may not be null.");
			}
			this.position = position.getCopy();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			BendPoint other = (BendPoint) obj;
			if (contentAnchorage == null) {
				if (other.contentAnchorage != null) {
					return false;
				}
			} else if (!contentAnchorage.equals(other.contentAnchorage)) {
				return false;
			}
			if (position == null) {
				if (other.position != null) {
					return false;
				}
			} else if (!position.equals(other.position)) {
				return false;
			}
			return true;
		}

		/**
		 * The content element to which the bend point is attached.
		 *
		 * @return The content element to which the bend point is attached.
		 */
		public Object getContentAnchorage() {
			return contentAnchorage;
		}

		/**
		 * The position of the unattached bend point or the (optional) position
		 * hint for an attached bend point.
		 *
		 * @return A point representing the position if the bend point is not
		 *         attached, or a position hint for an attached bend point.
		 */
		public Point getPosition() {
			return position;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((contentAnchorage == null) ? 0
					: contentAnchorage.hashCode());
			result = prime * result
					+ ((position == null) ? 0 : position.hashCode());
			return result;
		}

		/**
		 * Whether this bend point is defined through an attachment of a content
		 * anchorage.
		 *
		 * @return <code>true</code> if the bend point is defined through an
		 *         attachment, <code>false</code> if the bend point is defined
		 *         through a position.
		 */
		public boolean isAttached() {
			return contentAnchorage != null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "BendPoint ["
					+ (contentAnchorage != null
							? "contentAnchorage=" + contentAnchorage + ", "
							: "")
					+ (position != null ? "position=" + position : "") + "]";
		}
	}

	/**
	 * Default role for the first {@link BendPoint}.
	 */
	public static final String SOURCE_ROLE = "source";

	/**
	 * Default role for the last {@link BendPoint}.
	 */
	public static final String TARGET_ROLE = "target";

	/**
	 * Default role prefix for intermediary {@link BendPoint}s.
	 */
	public static final String CONTROL_ROLE_PREFIX = "bp_";

	/**
	 * Returns the visual to bend.
	 *
	 * @return The visual to bend.
	 *
	 * @deprecated This method is no longer used as part of the
	 *             {@link IBendableContentPart} contract. Reason is that
	 *             IBendableContentPart is no longer bound to a
	 *             {@link Connection} visual, while it still provides default
	 *             behavior for that specific case.
	 */
	@Deprecated
	public default Connection getBendableVisual() {
		if (this.getVisual() instanceof Connection) {
			return (Connection) this.getVisual();
		}
		throw new IllegalStateException(
				"This operation should never be called");
	}

	/**
	 * Returns the current {@link BendPoint}s of this
	 * {@link IBendableContentPart}'s content.
	 *
	 * @return The {@link BendPoint}s of this {@link IBendableContentPart}'s
	 *         content.
	 */
	public List<BendPoint> getContentBendPoints();

	@Override
	public default Dimension getContentSize() {
		return BendPoint.computeSize(getContentBendPoints());
	}

	@Override
	public default Affine getContentTransform() {
		return BendPoint.computeTranslation(getContentBendPoints());
	}

	/**
	 * Returns the role that is used to determine the {@link IAnchor} for the
	 * {@link BendPoint} at the given index of the given {@link List} of
	 * {@link BendPoint}s.
	 *
	 * @param bendPoints
	 *            The {@link List} of {@link BendPoint}s.
	 * @param index
	 *            The index specifying the {@link BendPoint} for which to
	 *            determine the role.
	 * @return The role that is used to determine the {@link IAnchor} for the
	 *         specified {@link BendPoint}.
	 */
	public default String getRole(List<BendPoint> bendPoints, int index) {
		if (index == 0) {
			return SOURCE_ROLE;
		} else if (index == bendPoints.size() - 1) {
			return TARGET_ROLE;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(CONTROL_ROLE_PREFIX);
			sb.append(index);
			return sb.toString();
		}
	}

	/**
	 * Returns the current {@link BendPoint}s of this
	 * {@link IBendableContentPart}'s visual.
	 *
	 * @return The {@link BendPoint}s of this {@link IBendableContentPart}'s
	 *         visual.
	 */
	public default List<org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint> getVisualBendPoints() {
		List<BendPoint> bendPoints = new ArrayList<>();
		IViewer viewer = getRoot().getViewer();
		Node bendableVisual = getVisual();
		if (bendableVisual instanceof Connection) {
			Connection connection = (Connection) bendableVisual;
			List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
			for (int i = 0; i < anchors.size(); i++) {
				IAnchor anchor = anchors.get(i);
				if (!connection.getRouter().wasInserted(anchor)) {
					if (connection.isConnected(i)) {
						// provide a position hint for a connected bend point
						Point positionHint = connection.getPoint(i);
						if (i == 0 && connection.getStartPointHint() != null) {
							positionHint = connection.getStartPointHint();
						}
						if (i == anchors.size() - 1
								&& connection.getEndPointHint() != null) {
							positionHint = connection.getEndPointHint();
						}
						// determine anchorage content
						Node anchorageNode = anchor.getAnchorage();
						IVisualPart<? extends Node> part = PartUtils
								.retrieveVisualPart(viewer, anchorageNode);
						Object anchorageContent = null;
						if (part instanceof IContentPart) {
							anchorageContent = ((IContentPart<? extends Node>) part)
									.getContent();
						}
						bendPoints.add(
								new BendPoint(anchorageContent, positionHint));
					} else {
						bendPoints.add(new BendPoint(connection.getPoint(i)));
					}
				}
			}
			return bendPoints;
		} else {
			throw new UnsupportedOperationException(
					"Default behavior only covers parts with a Connection visual. Please implement specific behavior for this implementation.");
		}
	}

	@Override
	default Dimension getVisualSize() {
		return BendPoint.computeSize(getVisualBendPoints());
	}

	@Override
	default Affine getVisualTransform() {
		return BendPoint.computeTranslation(getVisualBendPoints());
	}

	/**
	 * Bends the content element as specified through the given bend points.
	 *
	 * @param bendPoints
	 *            The bend points.
	 */
	public void setContentBendPoints(List<BendPoint> bendPoints);

	@Override
	default void setContentSize(Dimension totalSize) {
		// determine visual offset
		Affine visualTransform = getContentTransform();
		double currentX = visualTransform.getTx();
		double currentY = visualTransform.getTy();
		// resize content bend points
		List<BendPoint> resizedBendPoints = BendPoint.resize(
				getContentBendPoints(), currentX, currentY, getContentSize(),
				totalSize);
		setContentBendPoints(resizedBendPoints);
	}

	@Override
	default void setContentTransform(Affine totalTransform) {
		setContentBendPoints(BendPoint.transform(getContentBendPoints(),
				getContentTransform(), totalTransform));
	}

	/**
	 * Bends the visual as specified by the given bend points.
	 *
	 * @param bendPoints
	 *            The bend points.
	 */
	public default void setVisualBendPoints(List<BendPoint> bendPoints) {
		if (bendPoints == null || bendPoints.size() < 2) {
			throw new IllegalArgumentException(
					"Not enough bend points supplied!");
		}

		Node bendableVisual = getVisual();
		if (bendableVisual instanceof Connection) {
			Connection connection = (Connection) bendableVisual;
			// compute anchors for the given bend points
			List<IAnchor> newAnchors = new ArrayList<>();
			for (int i = 0; i < bendPoints.size(); i++) {
				BendPoint bp = bendPoints.get(i);
				if (bp.isAttached()) {
					// create anchor
					IAnchorProvider anchorProvider = getRoot().getViewer()
							.getContentPartMap().get(bp.getContentAnchorage())
							.getAdapter(IAnchorProvider.class);
					if (anchorProvider == null) {
						throw new IllegalStateException(
								"Anchorage does not provide anchor!");
					}
					// TODO: the role needs to be properly defined
					IAnchor anchor = anchorProvider.get(this,
							getRole(bendPoints, i));
					if (anchor == null) {
						throw new IllegalStateException(
								"AnchorProvider does not provide anchor!");
					}
					newAnchors.add(anchor);

					// update hints
					if (i == 0) {
						// update start point hint
						connection.setStartPointHint(
								bendPoints.get(0).getPosition());
					}
					if (i == bendPoints.size() - 1) {
						// update end point hint
						connection.setEndPointHint(bendPoints
								.get(bendPoints.size() - 1).getPosition());
					}
				} else {
					newAnchors.add(
							new StaticAnchor(connection, bp.getPosition()));
				}
			}

			// update anchors
			connection.setAnchors(newAnchors);
		} else {
			throw new UnsupportedOperationException(
					"Default behavior only covers IBendableContentParts with a Connection visual. Please implement specific behavior for this implementation.");
		}
	}

	@Override
	default void setVisualSize(Dimension totalSize) {
		List<BendPoint> visualBendPoints = getVisualBendPoints();
		// determine visual offset
		Affine visualTransform = BendPoint.computeTranslation(visualBendPoints);
		double currentX = visualTransform.getTx();
		double currentY = visualTransform.getTy();
		// resize visual bend points
		List<BendPoint> resizedBendPoints = BendPoint.resize(visualBendPoints,
				currentX, currentY, BendPoint.computeSize(visualBendPoints),
				totalSize);
		setVisualBendPoints(resizedBendPoints);
	}

	@Override
	default void setVisualTransform(Affine totalTransform) {
		List<BendPoint> visualBendPoints = getVisualBendPoints();
		setVisualBendPoints(BendPoint.transform(visualBendPoints,
				BendPoint.computeTranslation(visualBendPoints),
				totalTransform));
	}
}
