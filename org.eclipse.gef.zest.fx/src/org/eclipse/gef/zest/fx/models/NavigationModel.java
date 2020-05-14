/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Alexander Nyßen (itemis AG) - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.models;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.graph.Graph;

/**
 * The {@link NavigationModel} manages a {@link Set} of {@link Graph}s for which
 * the next layout pass should be skipped (due to transformation or navigation
 * changes). Moreover, it manages a {@link Map} saving a {@link ViewportState}
 * per {@link Graph}, so that the scroll position and zoom factor can be
 * restored when navigating nested graphs.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class NavigationModel {

	/*
	 * TODO: Store translateX, translateY, width, and height relative to the
	 * underlying contents.
	 */
	/**
	 * Representation of a viewport's state, which manifests itself in x and y
	 * translation, width and height, as well as a contents transform.
	 *
	 * @author anyssen
	 *
	 */
	public static class ViewportState {

		private double translateX = 0;
		private double translateY = 0;
		private double width = 0;
		private double height = 0;
		private AffineTransform contentsTransform = null;

		/**
		 * Creates a new {@link ViewportState} with
		 * <code>tx = ty = width = height = 0</code> and an identity transform.
		 */
		public ViewportState() {
			this(0, 0, 0, 0, new AffineTransform());
		}

		/**
		 * Creates a new {@link ViewportState} for the given translation, size,
		 * and transform.
		 *
		 * @param translateX
		 *            The horizontal translation.
		 * @param translateY
		 *            The vertical translation.
		 * @param width
		 *            The viewport width.
		 * @param height
		 *            The viewport height.
		 * @param contentsTransform
		 *            The contents transform.
		 */
		public ViewportState(double translateX, double translateY, double width, double height,
				AffineTransform contentsTransform) {
			this.translateX = translateX;
			this.translateY = translateY;
			this.width = width;
			this.height = height;
			this.contentsTransform = contentsTransform;
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
			ViewportState other = (ViewportState) obj;
			if (contentsTransform == null) {
				if (other.contentsTransform != null) {
					return false;
				}
			} else if (!contentsTransform.equals(other.contentsTransform)) {
				return false;
			}
			if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height)) {
				return false;
			}
			if (Double.doubleToLongBits(translateX) != Double.doubleToLongBits(other.translateX)) {
				return false;
			}
			if (Double.doubleToLongBits(translateY) != Double.doubleToLongBits(other.translateY)) {
				return false;
			}
			if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width)) {
				return false;
			}
			return true;
		}

		/**
		 * Returns the contents transform associated with this
		 * {@link ViewportState}.
		 *
		 * @return The contents transform.
		 */
		public AffineTransform getContentsTransform() {
			return contentsTransform;
		}

		/**
		 * Returns a copy of this {@link ViewportState}.
		 *
		 * @return A copy of this {@link ViewportState}.
		 */
		public ViewportState getCopy() {
			return new ViewportState(translateX, translateY, width, height, contentsTransform.getCopy());
		}

		/**
		 * Returns the viewport height associated with this
		 * {@link ViewportState}.
		 *
		 * @return The viewport height.
		 */
		public double getHeight() {
			return height;
		}

		/**
		 * Returns the horizontal translation associated with this
		 * {@link ViewportState}.
		 *
		 * @return The horizontal translation.
		 */
		public double getTranslateX() {
			return translateX;
		}

		/**
		 * Returns the vertical translation associated with this
		 * {@link ViewportState}.
		 *
		 * @return The vertical translation.
		 */
		public double getTranslateY() {
			return translateY;
		}

		/**
		 * Returns the viewport width associated with this {@link ViewportState}
		 * .
		 *
		 * @return The viewport width.
		 */
		public double getWidth() {
			return width;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((contentsTransform == null) ? 0 : contentsTransform.hashCode());
			long temp;
			temp = Double.doubleToLongBits(height);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(translateX);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(translateY);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(width);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
	}

	private Map<Graph, ViewportState> viewportStates = new IdentityHashMap<>();

	/**
	 * Default constructor.
	 */
	public NavigationModel() {
	}

	/**
	 * Retrieves the {@link ViewportState} that is currently saved for the given
	 * {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the saved {@link ViewportState} is
	 *            returned.
	 * @return The {@link ViewportState} that was saved for the given
	 *         {@link Graph}.
	 */
	public ViewportState getViewportState(Graph graph) {
		return viewportStates.get(graph);
	}

	/**
	 * Removes the {@link ViewportState} for the given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} for which to remove the
	 *            {@link ViewportState}.
	 */
	public void removeViewportState(Graph graph) {
		viewportStates.remove(graph);
	}

	/**
	 * Saves the given {@link ViewportState} for the given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} for which to save a {@link ViewportState}.
	 * @param state
	 *            The {@link ViewportState} that is saved for the given
	 *            {@link Graph}.
	 */
	public void setViewportState(Graph graph, ViewportState state) {
		viewportStates.put(graph, state);
	}

}
