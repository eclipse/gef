/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXTransformPolicy} is a {@link ITransactional transactional}
 * {@link AbstractPolicy policy} that handles the transformation of its
 * {@link #getHost() host}.
 *
 * @author mwienand
 *
 */
public class FXTransformPolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * The role name for the <code>Provider&lt;Affine&gt;</code> that will be
	 * used to obtain the host's {@link Affine} transformation.
	 */
	public static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	/**
	 * Computes the offset which needs to be added to the given local
	 * coordinates in order to stay on the grid/snap to the grid.
	 *
	 * @param gridModel
	 *            The {@link GridModel} of the host's {@link IViewer}.
	 * @param localX
	 *            The x-coordinate in host coordinates.
	 * @param localY
	 *            The y-coordinate in host coordinates.
	 * @param gridCellWidthFraction
	 *            The granularity of the horizontal grid steps.
	 * @param gridCellHeightFraction
	 *            The granularity of the vertical grid steps.
	 * @return A {@link Dimension} representing the offset that needs to be
	 *         added to the local coordinates so that they snap to the grid.
	 */
	protected static Dimension getSnapToGridOffset(GridModel gridModel,
			final double localX, final double localY,
			final double gridCellWidthFraction,
			final double gridCellHeightFraction) {
		// TODO: pass in scene coordinates so that the snap can be computed
		// correctly even though transformations are used
		double snapOffsetX = 0, snapOffsetY = 0;
		if ((gridModel != null) && gridModel.isSnapToGrid()) {
			// determine snap width
			final double snapWidth = gridModel.getGridCellWidth()
					* gridCellWidthFraction;
			final double snapHeight = gridModel.getGridCellHeight()
					* gridCellHeightFraction;

			snapOffsetX = localX % snapWidth;
			if (snapOffsetX > (snapWidth / 2)) {
				snapOffsetX = snapWidth - snapOffsetX;
				snapOffsetX *= -1;
			}

			snapOffsetY = localY % snapHeight;
			if (snapOffsetY > (snapHeight / 2)) {
				snapOffsetY = snapHeight - snapOffsetY;
				snapOffsetY *= -1;
			}
		}
		return new Dimension(snapOffsetX, snapOffsetY);
	}

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	private FXTransformOperation transformOperation;
	private AffineTransform oldTransform;
	private Affine nodeTransform;

	@Override
	public IUndoableOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;

		IUndoableOperation commit = null;
		if (transformOperation != null && transformOperation.hasEffect()) {
			commit = transformOperation;
		}
		transformOperation = null;
		oldTransform = null;
		return commit;
	}

	/**
	 * Returns the {@link Affine} transformation that is returned by the
	 * <code>Provider&lt;Affine&gt;</code> that is installed on the
	 * {@link #getHost() host} under the {@link #TRANSFORMATION_PROVIDER_ROLE}
	 * role.
	 *
	 * @return The {@link Affine} transformation that is returned by the
	 *         <code>Provider&lt;Affine&gt;</code> that is installed on the
	 *         {@link #getHost() host} under the
	 *         {@link #TRANSFORMATION_PROVIDER_ROLE} role.
	 */
	@SuppressWarnings("serial")
	public Affine getNodeTransform() {
		if (nodeTransform == null) {
			nodeTransform = getHost().getAdapter(
					AdapterKey.get(new TypeToken<Provider<Affine>>() {
					}, TRANSFORMATION_PROVIDER_ROLE)).get();
		}
		return nodeTransform;
	}

	@Override
	public void init() {
		transformOperation = new FXTransformOperation(getNodeTransform());
		oldTransform = JavaFX2Geometry
				.toAffineTransform(transformOperation.getOldTransform());
		initialized = true;
	}

	/**
	 * Concatenates the given {@link AffineTransform} to the initial
	 * transformation.
	 *
	 * @param transform
	 *            The {@link AffineTransform} that is concatenated to the
	 *            initial transformation.
	 */
	public void setConcatenation(AffineTransform transform) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		setTransform(oldTransform.getCopy().concatenate(transform));
	}

	/**
	 * Concatenates the initial transformation to the given
	 * {@link AffineTransform}.
	 *
	 * @param transform
	 *            The {@link AffineTransform} to which the initial
	 *            transformation is concatenated.
	 */
	public void setPreConcatenation(AffineTransform transform) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		setTransform(oldTransform.getCopy().preConcatenate(transform));
	}

	/**
	 * Changes the {@link #getHost() host's} transformation to the given
	 * {@link AffineTransform}.
	 *
	 * @param newTransform
	 *            The new {@link AffineTransform} for the {@link #getHost()
	 *            host}.
	 */
	public void setTransform(AffineTransform newTransform) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// snap to grid if needed (TODO: check that this is correct)
		Dimension snapToGridOffset = getSnapToGridOffset(
				getHost().getRoot().getViewer().getAdapter(GridModel.class),
				newTransform.getTranslateX(), newTransform.getTranslateY(), 0.5,
				0.5);
		newTransform.setTransform(newTransform.getM00(), newTransform.getM10(),
				newTransform.getM01(), newTransform.getM11(),
				newTransform.getTranslateX() - snapToGridOffset.width,
				newTransform.getTranslateY() - snapToGridOffset.height);

		// update operation
		transformOperation
				.setNewTransform(Geometry2JavaFX.toFXAffine(newTransform));

		// locally execute operation
		try {
			transformOperation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
