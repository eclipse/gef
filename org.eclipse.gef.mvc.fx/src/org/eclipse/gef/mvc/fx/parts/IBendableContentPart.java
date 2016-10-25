/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

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
			this.position = position;
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
			return "BendPoint [" + (contentAnchorage != null
					? "contentAnchorage=" + contentAnchorage + ", " : "")
					+ (position != null ? "position=" + position : "") + "]";
		}
	}

	/**
	 * Transforms the given {@link List} of {@link BendPoint}s in-place using
	 * the given {@link AffineTransform}.
	 *
	 * @param bendPoints
	 *            The {@link List} of {@link BendPoint}s to transform.
	 * @param transform
	 *            The {@link AffineTransform} that is applied to the unattached
	 *            bend points.
	 * @return The given {@link List} of {@link BendPoint}s for convenience.
	 */
	public static List<BendPoint> transformBendPoints(
			List<BendPoint> bendPoints, AffineTransform transform) {
		// optimize identity transform
		if (transform == null || transform.isIdentity()) {
			return bendPoints;
		}

		for (BendPoint bp : bendPoints) {
			if (!bp.isAttached()) {
				// transform unattached bend points
				bp.getPosition().transform(transform);
			}
		}

		return bendPoints;
	}

	/**
	 * Bends the content element as specified through the given bend points.
	 *
	 * @param bendPoints
	 *            The bend points.
	 */
	public default void bendContent(List<BendPoint> bendPoints) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Bends the visual as specified by the given bend points.
	 *
	 * @param bendPoints
	 *            The bend points.
	 */
	public default void bendVisual(List<BendPoint> bendPoints) {
		if (bendPoints == null || bendPoints.size() < 2) {
			throw new IllegalArgumentException(
					"Not enough bend points supplied!");
		}

		// compute anchors for the given bend points
		List<IAnchor> newAnchors = new ArrayList<>();
		for (int i = 0; i < bendPoints.size(); i++) {
			BendPoint bp = bendPoints.get(i);
			if (bp.isAttached()) {
				// create anchor
				// TODO: verify anchor computation is correct
				IAnchorProvider anchorProvider = getRoot().getViewer()
						.getContentPartMap().get(bp.getContentAnchorage())
						.getAdapter(IAnchorProvider.class);
				if (anchorProvider == null) {
					throw new IllegalStateException(
							"Anchorage does not provide anchor!");
				}
				IAnchor anchor = anchorProvider.get(this);
				if (anchor == null) {
					throw new IllegalStateException(
							"AnchorProvider does not provide anchor!");
				}
				newAnchors.add(anchor);

				// update hints
				if (i == 0) {
					// update start point hint
					getBendableVisual()
							.setStartPointHint(bendPoints.get(0).getPosition());
				}
				if (i == bendPoints.size() - 1) {
					// update end point hint
					getBendableVisual().setEndPointHint(bendPoints
							.get(bendPoints.size() - 1).getPosition());
				}
			} else {
				// TODO: verify position is anchorage local
				newAnchors.add(new StaticAnchor(getBendableVisual(),
						bp.getPosition()));
			}
		}

		// update anchors
		getBendableVisual().setAnchors(newAnchors);
	}

	/**
	 * Returns the visual to bend.
	 *
	 * @return The visual to bend.
	 */
	public default Connection getBendableVisual() {
		return (Connection) getVisual();
	}

	/**
	 * Returns the current {@link BendPoint}s of this
	 * {@link IBendableContentPart}'s content.
	 *
	 * @return The {@link BendPoint}s of this {@link IBendableContentPart}'s
	 *         content.
	 */
	public default List<BendPoint> getContentBendPoints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public default Dimension getContentSize() {
		// XXX: Return value does not matter..
		return new Dimension();
	}

	@Override
	public default AffineTransform getContentTransform() {
		// // try to extract transform using CONTENT_TRANSFORM_KEY
		// AdapterKey<AffineTransform> CONTENT_TRANSFORM_KEY = AdapterKey
		// .get(AffineTransform.class, "CONTENT_TRANSFORM_ROLE");
		// AffineTransform contentTransform = getAdapter(CONTENT_TRANSFORM_KEY);
		// if (contentTransform == null) {
		// contentTransform = new AffineTransform();
		// setAdapter(contentTransform, CONTENT_TRANSFORM_KEY.getRole());
		// }
		// return contentTransform;

		// XXX: Return value does not matter..

		// XXX: Identity transform is fine. transformContent() adapts to the
		// visual transform by transforming bend points. therefore, the content
		// transform does not need to be altered. validation for bendables is
		// invalid.
		return new AffineTransform();
	}

	@Override
	public default Node getResizableVisual() {
		return getBendableVisual();
	}

	@Override
	public default Node getTransformableVisual() {
		return getBendableVisual();
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
		Connection connection = getBendableVisual();
		IViewer viewer = getRoot().getViewer();
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
					bendPoints
							.add(new BendPoint(anchorageContent, positionHint));
				} else {
					bendPoints.add(new BendPoint(connection.getPoint(i)));
				}
			}
		}
		return bendPoints;
	}

	@Override
	default Affine getVisualTransform() {
		return ITransformableContentPart.super.getVisualTransform();
	}

	@Override
	default void resizeContent(Dimension totalSize) {
		bendContent(getVisualBendPoints());
	}

	@Override
	default void resizeVisual(Dimension size) {
		/*
		 * Resize visual by transforming the bend points relative to the size
		 * change:
		 *
		 * 1. Determine current relative positions of bend points.
		 *
		 * 2. Compute new real positions of bend points using relative positions
		 * and new size.
		 *
		 * 3. Apply resulting bend points to the visual.
		 */

		// determine delta size
		Dimension currentSize = getVisualSize();
		// TODO: validate +/-
		double dw = size.width - currentSize.width;
		double dh = size.height - currentSize.width;

		// determine absolute positions
		Connection connection = getBendableVisual();
		Point[] points = connection.getPointsUnmodifiable()
				.toArray(new Point[] {});

		// compute relative positions
		// TODO: is it allowed to use layout bounds ???
		Bounds layoutBounds = connection.getLayoutBounds();
		double[] relX = new double[points.length];
		double[] relY = new double[points.length];
		for (int i = 0; i < points.length; i++) {
			if (connection.isConnected(i)) {
				continue;
			}
			// TODO: use size.width/height ???
			relX[i] = (points[i].x - layoutBounds.getMinX())
					/ layoutBounds.getWidth();
			relY[i] = (points[i].y - layoutBounds.getMinY())
					/ layoutBounds.getHeight();
		}

		// compute absolute new positions
		List<BendPoint> bendPoints = getVisualBendPoints();
		for (int i = 0; i < bendPoints.size(); i++) {
			BendPoint bp = bendPoints.get(i);
			if (!bp.isAttached()) {
				bp.getPosition().x += relX[i] * dw;
				bp.getPosition().y += relY[i] * dh;
			}
		}

		// apply new positions to the visual
		bendVisual(bendPoints);
	}

	@Override
	default void transformContent(AffineTransform totalTransform) {
		bendContent(getVisualBendPoints());
	}

	@Override
	default void transformVisual(Affine totalTransform) {
		// compute delta transform
		Transform deltaTransform = ITransformableContentPart
				.computeDeltaTransform(getVisualTransform(), totalTransform);

		// optimize identity transform
		if (deltaTransform.isIdentity()) {
			return;
		}

		// save total transform
		ITransformableContentPart.super.transformVisual(totalTransform);

		// determine bend points
		List<BendPoint> bendPoints = getVisualBendPoints();

		// transform bend points
		transformBendPoints(bendPoints,
				FX2Geometry.toAffineTransform(deltaTransform));

		// apply transformed bend points to the visual
		bendVisual(bendPoints);
	}

}
