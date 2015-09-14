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
package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.transform.Affine;

public class FXTransformShapePolicy extends FXTransformPolicy {

	private class ModelOperation extends AbstractOperation
			implements ITransactionalOperation {
		private final FXGeometricShape hostContent;
		private final AffineTransform newTx;
		private final AffineTransform oldTx;

		public ModelOperation(FXGeometricShape hostContent,
				AffineTransform oldTransform, AffineTransform newTransform) {
			super("Update Model");
			this.hostContent = hostContent;
			this.oldTx = oldTransform;
			this.newTx = newTransform;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			hostContent.setTransform(newTx);
			return Status.OK_STATUS;
		}

		@Override
		public boolean isNoOp() {
			return oldTx == newTx || (oldTx != null && oldTx.equals(newTx));
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			hostContent.setTransform(oldTx);
			return Status.OK_STATUS;
		}
	}

	@Override
	public ITransactionalOperation commit() {
		final ITransactionalOperation updateVisualOperation = super.commit();
		if (updateVisualOperation == null) {
			return null;
		}

		// commit changes to model
		final FXGeometricShapePart host = getHost();
		final FXGeometricShape hostContent = host.getContent();

		// determine transformation
		@SuppressWarnings("serial")
		Provider<Affine> affineProvider = host
				.getAdapter(AdapterKey.<Provider<? extends Affine>> get(
						new TypeToken<Provider<? extends Affine>>() {
						}, FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE));
		AffineTransform tx = JavaFX2Geometry
				.toAffineTransform(affineProvider.get());
		final AffineTransform oldTransform = hostContent.getTransform();
		final AffineTransform newTransform = new AffineTransform(tx.getM00(),
				tx.getM10(), tx.getM01(), tx.getM11(), tx.getTranslateX(),
				tx.getTranslateY());

		// create operation to write the changes to the model
		final ITransactionalOperation updateModelOperation = new ModelOperation(
				hostContent, oldTransform, newTransform);
		// compose operations
		ITransactionalOperation compositeOperation = new ForwardUndoCompositeOperation(
				updateVisualOperation.getLabel()) {
			{
				add(updateVisualOperation);
				add(updateModelOperation);
			}
		};

		return compositeOperation;
	}

	@Override
	public FXGeometricShapePart getHost() {
		return (FXGeometricShapePart) super.getHost();
	}

}
