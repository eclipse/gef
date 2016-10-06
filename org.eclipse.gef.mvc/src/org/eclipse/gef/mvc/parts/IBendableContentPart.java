/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.parts;

import java.util.List;

import org.eclipse.gef.geometry.planar.Point;

/**
 * An {@link IContentPart} that supports content related bend, i.e. manipulation
 * of control points.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link IBendableContentPart} is used in, e.g. javafx.scene.Node in
 *            case of JavaFX.
 *
 * @param <V>
 *            The visual node used by this {@link IBendableContentPart}.
 *
 */
public interface IBendableContentPart<VR, V extends VR>
		extends IContentPart<VR, V> {

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
	 * Bends the content element as specified through the given bend points.
	 *
	 * @param bendPoints
	 *            The bend points.
	 */
	public void bendContent(List<BendPoint> bendPoints);

	// TODO: Refresh
	// /**
	// * Returns the current {@link BendPoint}s of this
	// * {@link IBendableContentPart}'s content.
	// *
	// * @return The {@link BendPoint}s of this {@link IBendableContentPart}'s
	// * content.
	// */
	// public List<BendPoint> getContentBendPoints();

	/**
	 * Returns the current {@link BendPoint}s of this
	 * {@link IBendableContentPart}'s visual.
	 *
	 * @return The {@link BendPoint}s of this {@link IBendableContentPart}'s
	 *         visual.
	 */
	public List<BendPoint> getVisualBendPoints();

}
