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

import javafx.scene.Node;
import javafx.scene.transform.Affine;

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

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class FXTransformPolicy extends AbstractPolicy<Node> implements
		ITransactional {

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

	public static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	private FXTransformOperation transformOperation;
	private AffineTransform oldTransform;

	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = transformOperation;
		transformOperation = null;
		oldTransform = null;
		return commit;
	}

	@SuppressWarnings("serial")
	protected Affine getNodeTransform() {
		return getHost().getAdapter(
				AdapterKey.get(new TypeToken<Provider<Affine>>() {
				}, TRANSFORMATION_PROVIDER_ROLE)).get();
	}

	@Override
	public void init() {
		transformOperation = new FXTransformOperation(getNodeTransform());
		oldTransform = JavaFX2Geometry.toAffineTransform(transformOperation
				.getOldTransform());
	}

	public void setConcatenation(AffineTransform transform) {
		setTransform(oldTransform.getCopy().concatenate(transform));
	}

	public void setPreConcatenation(AffineTransform transform) {
		setTransform(oldTransform.getCopy().preConcatenate(transform));
	}

	public void setTransform(AffineTransform newTransform) {
		// snap to grid if needed (TODO: check that this is correct)
		Dimension snapToGridOffset = getSnapToGridOffset(getHost().getRoot()
				.getViewer().getAdapter(GridModel.class),
				newTransform.getTranslateX(), newTransform.getTranslateY(),
				0.5, 0.5);
		newTransform.setTransform(newTransform.getM00(), newTransform.getM10(),
				newTransform.getM01(), newTransform.getM11(),
				newTransform.getTranslateX() - snapToGridOffset.width,
				newTransform.getTranslateY() - snapToGridOffset.height);

		// update operation
		transformOperation.setNewTransform(Geometry2JavaFX
				.toFXAffine(newTransform));

		// locally execute operation
		try {
			transformOperation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
