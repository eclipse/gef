/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #476507
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
import org.eclipse.gef.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.policies.AbstractTransactionPolicy;
import org.eclipse.gef.mvc.policies.IPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * A transactional {@link IPolicy} to change the viewport of an {@link IViewer}
 * via its {@link InfiniteCanvas}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXChangeViewportPolicy extends AbstractTransactionPolicy<Node> {

	@Override
	protected ITransactionalOperation createOperation() {
		FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
		return new FXChangeViewportOperation(viewer.getCanvas(), FX2Geometry
				.toAffineTransform(viewer.getCanvas().getContentTransform()));
	}

	/**
	 * Returns an {@link FXChangeViewportOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return An {@link FXChangeViewportOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected FXChangeViewportOperation getChangeViewportOperation() {
		return (FXChangeViewportOperation) super.getOperation();
	}

	/**
	 * Advances the viewport transformation by the given translation values.
	 *
	 * @param concatenate
	 *            <code>true</code> to concatenate the specified zoom to the
	 *            current transformation, <code>false</code> to concatenate the
	 *            specified zoom to the initial transformation.
	 * @param deltaTranslateX
	 *            The horizontal translation delta.
	 * @param deltaTranslateY
	 *            The vertical translation delta.
	 */
	public void scroll(boolean concatenate, double deltaTranslateX,
			double deltaTranslateY) {
		checkInitialized();
		FXChangeViewportOperation operation = getChangeViewportOperation();
		operation.setNewHorizontalScrollOffset(
				concatenate ? operation.getNewHorizontalScrollOffset()
						: operation.getInitialHorizontalScrollOffset()
								+ deltaTranslateX);
		operation.setNewVerticalScrollOffset(concatenate
				? operation.getNewVerticalScrollOffset()
				: operation.getInitialVerticalScrollOffset() + deltaTranslateY);
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
	 *            current transformation, <code>false</code> to concatenate the
	 *            specified zoom to the initial transformation.
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

		double nextZoomLevel = 0d;
		if (discretize) {
			// query current zoom level
			double oldZoomLevel = getChangeViewportOperation()
					.getNewContentTransform().getScaleX();
			// compute next zoom level
			nextZoomLevel = oldZoomLevel * relativeZoom;
			// round to 6 decimal places
			DecimalFormat df = new DecimalFormat("#.######");
			df.setDecimalFormatSymbols(
					DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			df.setRoundingMode(RoundingMode.HALF_EVEN);
			nextZoomLevel = Double.parseDouble(df.format(nextZoomLevel));
			// ensure integer zoom levels are not skipped
			int ozli = (int) oldZoomLevel;
			int nzli = (int) nextZoomLevel;
			if (ozli != nzli && nzli != nextZoomLevel && ozli != oldZoomLevel) {
				nextZoomLevel = ozli < nzli ? nzli : ozli;
			}
		}

		// transform pivot to local coordinates
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getCanvas().getContentGroup().sceneToLocal(sceneX, sceneY);
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
			if (Math.abs(newZoomLevel - nextZoomLevel) < 0.01) {
				// // counter-act floating point errors
				setZoom(nextZoomLevel);
			}
		}

		locallyExecuteOperation();
	}

}
