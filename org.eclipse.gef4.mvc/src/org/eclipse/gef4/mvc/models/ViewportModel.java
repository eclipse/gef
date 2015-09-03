/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.geometry.planar.AffineTransform;

/**
 * The {@link ViewportModel} stores viewport width and height, horizontal and
 * vertical translation, and a general contents transformation.
 */
public class ViewportModel implements IPropertyChangeNotifier {

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
		public ViewportState(double translateX, double translateY, double width,
				double height, AffineTransform contentsTransform) {
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
			if (Double.doubleToLongBits(height) != Double
					.doubleToLongBits(other.height)) {
				return false;
			}
			if (Double.doubleToLongBits(translateX) != Double
					.doubleToLongBits(other.translateX)) {
				return false;
			}
			if (Double.doubleToLongBits(translateY) != Double
					.doubleToLongBits(other.translateY)) {
				return false;
			}
			if (Double.doubleToLongBits(width) != Double
					.doubleToLongBits(other.width)) {
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
			return new ViewportState(translateX, translateY, width, height,
					contentsTransform.getCopy());
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
			result = prime * result + ((contentsTransform == null) ? 0
					: contentsTransform.hashCode());
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

	/**
	 * When the viewport translateX-position changes, this is the property name
	 * reported by a corresponding property change event.
	 */
	public static final String VIEWPORT_TRANSLATE_X_PROPERTY = "viewportTranslateX";

	/**
	 * When the viewport translateY-position changes, this is the property name
	 * reported by a corresponding property change event.
	 */
	public static final String VIEWPORT_TRANSLATE_Y_PROPERTY = "viewportTranslateY";

	/**
	 * When the viewport width changes, this is the property name reported by a
	 * corresponding property change event.
	 */
	public static final String VIEWPORT_WIDTH_PROPERTY = "viewportWidth";

	/**
	 * When the viewport height changes, this is the property name reported by a
	 * corresponding property change event.
	 */
	public static final String VIEWPORT_HEIGHT_PROPERTY = "viewportHeight";

	/**
	 * When the contents transform changes, this is the property name reported
	 * by a corresponding property change event.
	 */
	public static final String VIEWPORT_CONTENTS_TRANSFORM_PROPERTY = "viewportContentsTransform";

	private ViewportState state = new ViewportState();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Applies the given {@link ViewportState} to this {@link ViewportModel}.
	 * Only the specified properties are transferred.
	 *
	 * @param state
	 *            The {@link ViewportState} to apply.
	 * @param ignoreTranslateX
	 *            <code>true</code> if the horizontal translation should not be
	 *            transferred, otherwise <code>false</code>.
	 * @param ignoreTranslateY
	 *            <code>true</code> if the vertical translation should not be
	 *            transferred, otherwise <code>false</code>.
	 * @param ignoreWidth
	 *            <code>true</code> if the viewport width should not be
	 *            transferred, otherwise <code>false</code>.
	 * @param ignoreHeight
	 *            <code>true</code> if the viewport height should not be
	 *            transferred, otherwise <code>false</code>.
	 * @param ignoreContentsTransform
	 *            <code>true</code> if the contents transform should not be
	 *            transferred, otherwise <code>false</code>.
	 */
	public void applyState(ViewportState state, boolean ignoreTranslateX,
			boolean ignoreTranslateY, boolean ignoreWidth, boolean ignoreHeight,
			boolean ignoreContentsTransform) {
		// System.out.println("APPLY: (" + state.translateX + ", " +
		// state.translateY
		// + ", " + state.width + ", " + state.height + ") -> "
		// + state.contentsTransform);
		if (!ignoreTranslateX) {
			setTranslateX(state.translateX);
		}
		if (!ignoreTranslateY) {
			setTranslateY(state.translateY);
		}
		if (!ignoreWidth) {
			setWidth(state.width);
		}
		if (!ignoreHeight) {
			setHeight(state.height);
		}
		if (!ignoreContentsTransform) {
			setContentsTransform(state.contentsTransform);
		}
	}

	/**
	 * Returns the contents transformation.
	 *
	 * @return The contents transformation.
	 */
	public AffineTransform getContentsTransform() {
		return state.contentsTransform.getCopy();
	}

	/**
	 * Returns the height of the current viewport, i.e. rectangular area in
	 * which the viewer/editor is rendered.
	 *
	 * @return height of current viewport
	 */
	public double getHeight() {
		return state.height;
	}

	/**
	 * Returns the horizontal translation of the contents in this model.
	 *
	 * @return The horizontal translation.
	 */
	public double getTranslateX() {
		return state.translateX;
	}

	/**
	 * Returns the vertical translation of the contents in this model.
	 *
	 * @return The vertical translation.
	 */
	public double getTranslateY() {
		return state.translateY;
	}

	/**
	 * Returns the width of the current viewport, i.e. rectangular area in which
	 * the viewer/editor is rendered.
	 *
	 * @return width of current viewport
	 */
	public double getWidth() {
		return state.width;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Creates a new {@link ViewportState} representing the current state of
	 * this {@link ViewportModel}. Only the specified properties are saved in
	 * the {@link ViewportState}.
	 *
	 * @param ignoreTranslateX
	 *            <code>true</code> if the horizontal translation should not be
	 *            retrieved, otherwise <code>false</code>.
	 * @param ignoreTranslateY
	 *            <code>true</code> if the vertical translation should not be
	 *            retrieved, otherwise <code>false</code>.
	 * @param ignoreWidth
	 *            <code>true</code> if the viewport width should not be
	 *            retrieved, otherwise <code>false</code>.
	 * @param ignoreHeight
	 *            <code>true</code> if the viewport height should not be
	 *            retrieved, otherwise <code>false</code>.
	 * @param ignoreContentsTransform
	 *            <code>true</code> if the contents transform should not be
	 *            retrieved, otherwise <code>false</code>.
	 * @return A new {@link ViewportState} representing the current state of
	 *         this {@link ViewportModel}.
	 */
	public ViewportState retrieveState(boolean ignoreTranslateX,
			boolean ignoreTranslateY, boolean ignoreWidth, boolean ignoreHeight,
			boolean ignoreContentsTransform) {
		ViewportState state = new ViewportState(getTranslateX(),
				getTranslateY(), getWidth(), getHeight(),
				getContentsTransform());
		if (ignoreTranslateX) {
			state.translateX = 0;
		}
		if (ignoreTranslateY) {
			state.translateY = 0;
		}
		if (ignoreWidth) {
			state.width = 0;
		}
		if (ignoreHeight) {
			state.height = 0;
		}
		if (ignoreContentsTransform) {
			state.contentsTransform = new AffineTransform();
		}
		// System.out.println("OBTAIN: (" + state.translateX + ", "
		// + state.translateY + ", " + state.width + ", " + state.height
		// + ") -> " + state.contentsTransform);
		return state;
	}

	/**
	 * Sets the contents transformation to the given value.
	 *
	 * @param contentsTransform
	 *            The new contents transformation.
	 */
	public void setContentsTransform(AffineTransform contentsTransform) {
		if (!state.contentsTransform.equals(contentsTransform)) {
			AffineTransform oldTx = state.contentsTransform.getCopy();
			state.contentsTransform = contentsTransform.getCopy();
			// System.out.println("SET TRANSFORM: " + contentsTransform);
			pcs.firePropertyChange(VIEWPORT_CONTENTS_TRANSFORM_PROPERTY, oldTx,
					state.contentsTransform);
		}
	}

	/**
	 * <p>
	 * Sets the height of the viewport in this model. This should be called when
	 * the size of the rectangular area in which the viewer/editor is rendered
	 * changes.
	 * </p>
	 * <p>
	 * Fires a property change event for the {@link #VIEWPORT_HEIGHT_PROPERTY}.
	 * </p>
	 *
	 * @param height
	 *            new viewport height
	 */
	public void setHeight(double height) {
		double oldHeight = this.state.height;
		if (oldHeight != height) {
			this.state.height = height;
			pcs.firePropertyChange(VIEWPORT_HEIGHT_PROPERTY, oldHeight, height);
		}
	}

	/**
	 * Sets the horizontal translation of the contents in this model.
	 *
	 * @param translateX
	 *            The new horizontal translation.
	 */
	public void setTranslateX(double translateX) {
		double oldTx = this.state.translateX;
		if (oldTx != translateX) {
			this.state.translateX = translateX;
			pcs.firePropertyChange(VIEWPORT_TRANSLATE_X_PROPERTY, oldTx,
					translateX);
		}
	}

	/**
	 * Sets the vertical translation of the contents in this model.
	 *
	 * @param translateY
	 *            The new vertical translation.
	 */
	public void setTranslateY(double translateY) {
		double oldTy = this.state.translateY;
		if (oldTy != translateY) {
			this.state.translateY = translateY;
			pcs.firePropertyChange(VIEWPORT_TRANSLATE_Y_PROPERTY, oldTy,
					translateY);
		}
	}

	/**
	 * <p>
	 * Sets the width of the viewport in this model. This should be called when
	 * the size of the rectangular area in which the viewer/editor is rendered
	 * changes.
	 * </p>
	 * <p>
	 * Fires a property change event for the {@link #VIEWPORT_WIDTH_PROPERTY}.
	 * </p>
	 *
	 * @param width
	 *            new viewport width
	 */
	public void setWidth(double width) {
		double oldWidth = this.state.width;
		if (oldWidth != width) {
			this.state.width = width;
			pcs.firePropertyChange(VIEWPORT_WIDTH_PROPERTY, oldWidth, width);
		}
	}

}
