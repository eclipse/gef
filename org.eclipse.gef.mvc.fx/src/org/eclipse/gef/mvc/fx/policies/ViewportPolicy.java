/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzillas #476507, #510419
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.operations.ChangeViewportOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.geometry.Point2D;

/**
 * An {@link IPolicy} to change the viewport of an {@link IViewer} via its
 * {@link InfiniteCanvas}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class ViewportPolicy extends AbstractPolicy {

	private static final double DEFAULT_ZOOM_MIN = 0.0625;
	private static final double DEFAULT_ZOOM_MAX = 16d;

	@Override
	protected ITransactionalOperation createOperation() {
		InfiniteCanvas canvas = (InfiniteCanvas) getHost().getRoot().getViewer()
				.getCanvas();
		return new ChangeViewportOperation(canvas,
				FX2Geometry.toAffineTransform(canvas.getContentTransform()));
	}

	/**
	 * Centers the contents within the viewport and zooms the viewport so that
	 * the contents are fully visible.
	 */
	public void fitToSize() {
		fitToSize(DEFAULT_ZOOM_MIN, DEFAULT_ZOOM_MAX);
	}

	/**
	 * Centers the contents within the viewport and zooms the viewport so that
	 * the contents are fully visible (if possible given the specified zoom
	 * factor range).
	 *
	 * @param zoomMin
	 *            The minimum zoom factor.
	 */
	public void fitToSize(double zoomMin) {
		fitToSize(zoomMin, DEFAULT_ZOOM_MAX);
	}

	/**
	 * Centers the contents within the viewport and zooms the viewport so that
	 * the contents are fully visible (if possible given the specified zoom
	 * factor range).
	 *
	 * @param zoomMin
	 *            The minimum zoom factor.
	 * @param zoomMax
	 *            The maximum zoom factor.
	 */
	public void fitToSize(double zoomMin, double zoomMax) {
		ChangeViewportOperation viewportOperation = getChangeViewportOperation();
		InfiniteCanvas canvas = viewportOperation.getInfiniteCanvas();

		// fit-to-size only works if we have contents
		if (canvas.getContentBounds().isEmpty()) {
			return;
		}

		canvas.fitToSize(zoomMin, zoomMax);
		viewportOperation.setNewContentTransform(
				FX2Geometry.toAffineTransform(canvas.getContentTransform()));
		viewportOperation.setNewHorizontalScrollOffset(
				canvas.getHorizontalScrollOffset());
		viewportOperation
				.setNewVerticalScrollOffset(canvas.getVerticalScrollOffset());
	}

	/**
	 * Returns an {@link ChangeViewportOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return An {@link ChangeViewportOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected ChangeViewportOperation getChangeViewportOperation() {
		return (ChangeViewportOperation) super.getOperation();
	}

	/**
	 * Advances the viewport transformation by the given translation values.
	 *
	 * @param concatenate
	 *            <code>true</code> to concatenate the specified zoom to the
	 *            current transformation, <code>false</code> to not concatenate
	 *            the specified scroll to the current but to the initial
	 *            transformation.
	 * @param deltaTranslateX
	 *            The horizontal translation delta.
	 * @param deltaTranslateY
	 *            The vertical translation delta.
	 */
	// TODO: add discretize option
	public void scroll(boolean concatenate, double deltaTranslateX,
			double deltaTranslateY) {
		checkInitialized();
		ChangeViewportOperation operation = getChangeViewportOperation();
		operation.setNewHorizontalScrollOffset(
				(concatenate ? operation.getNewHorizontalScrollOffset()
						: operation.getInitialHorizontalScrollOffset())
						+ deltaTranslateX);
		operation.setNewVerticalScrollOffset(
				(concatenate ? operation.getNewVerticalScrollOffset()
						: operation.getInitialVerticalScrollOffset())
						+ deltaTranslateY);
		locallyExecuteOperation();
	}

	/**
	 * Sets the x and y translation of the viewport to the given values. Does
	 * not alter scaling.
	 *
	 * @param tx
	 *            The new x translation.
	 * @param ty
	 *            The new y translation.
	 */
	public void setScroll(double tx, double ty) {
		checkInitialized();
		// query current transformation
		AffineTransform newTransform = getChangeViewportOperation()
				.getNewContentTransform();
		// set zoom level
		getChangeViewportOperation().setNewContentTransform(new AffineTransform(
				newTransform.getM00(), newTransform.getM10(),
				newTransform.getM01(), newTransform.getM11(), tx, ty));
		locallyExecuteOperation();
	}

	/**
	 * Sets the x and y scaling of the viewport to the given zoom factor. Does
	 * not alter translation/scroll-offset.
	 *
	 * @param zoom
	 *            The new x and y scaling for the viewport.
	 */
	public void setZoom(double zoom) {
		checkInitialized();
		// query current transformation
		AffineTransform newTransform = getChangeViewportOperation()
				.getNewContentTransform();
		// set zoom level
		getChangeViewportOperation().setNewContentTransform(new AffineTransform(
				zoom, newTransform.getM10(), newTransform.getM01(), zoom,
				newTransform.getTranslateX(), newTransform.getTranslateY()));
		locallyExecuteOperation();
	}

	/**
	 * Concatenates a scaling transformation to the current viewport
	 * transformation.
	 *
	 * @param concatenate
	 *            <code>true</code> to concatenate the specified zoom to the
	 *            current transformation, <code>false</code> to not concatenate
	 *            the specified zoom to the current but to the initial
	 *            transformation.
	 * @param discretize
	 *            <code>true</code> to discretize the resulting zoom level, i.e.
	 *            round it to 6 decimal places and do not skip integer zoom
	 *            levels, <code>false</code> to not change the resulting zoom
	 *            level.
	 * @param relativeZoom
	 *            The scale factor.
	 * @param sceneX
	 *            The pivot x-coordinate.
	 * @param sceneY
	 *            The pivot y-coordinate.
	 */
	public void zoom(boolean concatenate, boolean discretize,
			double relativeZoom, double sceneX, double sceneY) {
		checkInitialized();

		double discreteZoomLevel = 0d;
		if (discretize) {
			// TODO: Improve discretization algorithm and/or make it
			// configurable.
			// query current zoom level
			double oldZoomLevel = getChangeViewportOperation()
					.getNewContentTransform().getScaleX();
			// compute next zoom level
			discreteZoomLevel = oldZoomLevel * relativeZoom;
			// round to 6 decimal places
			DecimalFormat df = new DecimalFormat("#.######");
			df.setDecimalFormatSymbols(
					DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			df.setRoundingMode(RoundingMode.HALF_EVEN);
			discreteZoomLevel = Double
					.parseDouble(df.format(discreteZoomLevel));
			// ensure integer zoom levels are not skipped
			int ozli = (int) oldZoomLevel;
			int nzli = (int) discreteZoomLevel;
			if (ozli != nzli && nzli != discreteZoomLevel
					&& ozli != oldZoomLevel) {
				discreteZoomLevel = ozli < nzli ? nzli : ozli;
			}
		}

		// transform pivot to local coordinates
		Point2D contentGroupPivot = ((InfiniteCanvas) getHost().getRoot()
				.getViewer().getCanvas()).getContentGroup().sceneToLocal(sceneX,
						sceneY);
		// compute zoom transform
		AffineTransform zoomTx = new AffineTransform()
				.translate(contentGroupPivot.getX(), contentGroupPivot.getY())
				.scale(relativeZoom, relativeZoom).translate(
						-contentGroupPivot.getX(), -contentGroupPivot.getY());
		if (concatenate) {
			// apply relative zooming
			getChangeViewportOperation()
					.concatenateToNewContentTransform(zoomTx);
		} else {
			// concatenate to original transformation
			AffineTransform newTx = getChangeViewportOperation()
					.getInitialContentTransform().getCopy().concatenate(zoomTx);
			getChangeViewportOperation().setNewContentTransform(newTx);
		}

		if (discretize) {
			double newZoomLevel = getChangeViewportOperation()
					.getNewContentTransform().getScaleX();
			if (Math.abs(newZoomLevel - discreteZoomLevel) < 0.01) {
				// // counter-act floating point errors
				setZoom(discreteZoomLevel);
			}
		}

		locallyExecuteOperation();
	}
}
