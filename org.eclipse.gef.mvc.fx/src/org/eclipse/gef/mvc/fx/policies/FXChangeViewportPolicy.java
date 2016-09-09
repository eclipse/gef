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
	 * Ensures that the final zoom level is rounded to 6 decimal places.
	 * Moreover, if the integer part of the zoom level changes, the integer in
	 * between the old and new zoom levels will be used as the zoom level, i.e.
	 * integer zoom levels are never skipped.
	 *
	 * @param relativeZoom
	 *            The zoom factor.
	 * @param sceneX
	 *            The x-coordinate for the pivot point in scene coordinates.
	 * @param sceneY
	 *            The y-coordinate for the pivot point in scene coordinates.
	 */
	public void roundAndZoomRelative(double relativeZoom, double sceneX,
			double sceneY) {
		// query current zoom level
		double oldZoomLevel = getChangeViewportOperation()
				.getNewContentTransform().getScaleX();
		// compute next zoom level
		double nextZoomLevel = oldZoomLevel * relativeZoom;
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
		// apply zoom level
		zoomRelative(nextZoomLevel / oldZoomLevel, sceneX, sceneY);
		double newZoomLevel = getChangeViewportOperation()
				.getNewContentTransform().getScaleX();
		if (newZoomLevel != nextZoomLevel) {
			// counter-act floating point errors
			setZoomLevel(nextZoomLevel);
		}
	}

	/**
	 * Advances the viewport's original horizontal and vertical scroll offsets
	 * by the given values.
	 *
	 * @param translateX
	 *            The horizontal translation delta.
	 * @param translateY
	 *            The vertical translation delta.
	 */
	public void scrollAbsolute(double translateX, double translateY) {
		checkInitialized();
		FXChangeViewportOperation operation = getChangeViewportOperation();
		operation.setNewHorizontalScrollOffset(
				operation.getInitialHorizontalScrollOffset() + translateX);
		operation.setNewVerticalScrollOffset(
				operation.getInitialVerticalScrollOffset() + translateY);
		locallyExecuteOperation();
	}

	/**
	 * Advances the viewport transformation by the given translation values.
	 *
	 * @param deltaTranslateX
	 *            The horizontal translation delta.
	 * @param deltaTranslateY
	 *            The vertical translation delta.
	 */
	public void scrollRelative(double deltaTranslateX, double deltaTranslateY) {
		checkInitialized();
		FXChangeViewportOperation operation = getChangeViewportOperation();
		operation.setNewHorizontalScrollOffset(
				operation.getNewHorizontalScrollOffset() + deltaTranslateX);
		operation.setNewVerticalScrollOffset(
				operation.getNewVerticalScrollOffset() + deltaTranslateY);
		locallyExecuteOperation();
	}

	/**
	 * Applies the given zoom level to the viewport. Does not alter
	 * translation/scroll-offset.
	 *
	 * @param zoomLevel
	 *            The new zoom level for the viewport.
	 */
	public void setZoomLevel(double zoomLevel) {
		checkInitialized();
		// query current transformation
		AffineTransform newTransform = getChangeViewportOperation()
				.getNewContentTransform();
		// set zoom level
		getChangeViewportOperation().setNewContentTransform(new AffineTransform(
				zoomLevel, newTransform.getM10(), newTransform.getM01(),
				zoomLevel, newTransform.getTranslateX(),
				newTransform.getTranslateY()));
		locallyExecuteOperation();
	}

	/**
	 * Concatenates a scaling transformation to the original viewport
	 * transformation.
	 *
	 * @param relativeZoom
	 *            The scale factor.
	 * @param sceneX
	 *            The pivot x-coordinate.
	 * @param sceneY
	 *            The pivot y-coordinate.
	 */
	public void zoomAbsolute(double relativeZoom, double sceneX,
			double sceneY) {
		checkInitialized();
		// transform pivot to local coordinates
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getCanvas().getContentGroup().sceneToLocal(sceneX, sceneY);
		// compute zoom transform
		AffineTransform zoomTx = new AffineTransform()
				.translate(contentGroupPivot.getX(), contentGroupPivot.getY())
				.scale(relativeZoom, relativeZoom).translate(
						-contentGroupPivot.getX(), -contentGroupPivot.getY());
		// concatenate to original transformation
		AffineTransform newTx = getChangeViewportOperation()
				.getInitialContentTransform().getCopy().concatenate(zoomTx);
		getChangeViewportOperation().setNewContentTransform(newTx);
		locallyExecuteOperation();
	}

	/**
	 * Concatenates a scaling transformation to the current viewport
	 * transformation.
	 *
	 * @param relativeZoom
	 *            The scale factor.
	 * @param sceneX
	 *            The pivot x-coordinate.
	 * @param sceneY
	 *            The pivot y-coordinate.
	 */
	public void zoomRelative(double relativeZoom, double sceneX,
			double sceneY) {
		checkInitialized();
		// transform pivot to local coordinates
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getCanvas().getContentGroup().sceneToLocal(sceneX, sceneY);
		// apply relative zooming
		getChangeViewportOperation()
				.concatenateToNewContentTransform(new AffineTransform()
						.translate(contentGroupPivot.getX(),
								contentGroupPivot.getY())
						.scale(relativeZoom, relativeZoom)
						.translate(-contentGroupPivot.getX(),
								-contentGroupPivot.getY()));
		locallyExecuteOperation();
	}

}
