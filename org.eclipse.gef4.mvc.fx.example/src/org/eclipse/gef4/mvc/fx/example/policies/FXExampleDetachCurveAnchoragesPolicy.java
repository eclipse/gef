/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

// TODO: only valid for FXGeometricCurvePart (see #getHost())
public class FXExampleDetachCurveAnchoragesPolicy extends
		AbstractDetachContentAnchoragesPolicy<Node> {

	public static class Operation extends AbstractOperation {
		private final FXGeometricCurve curve;
		private final AbstractFXGeometricElement<?> contentAnchorage;
		private final String role;

		public Operation(FXGeometricCurve curve,
				AbstractFXGeometricElement<?> contentAnchorage, String role) {
			super("DetachCurveAnchorages");
			this.curve = curve;
			this.contentAnchorage = contentAnchorage;
			this.role = role;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			if ("START".equals(role)) {
				curve.getSourceAnchorages().remove(contentAnchorage);
			} else if ("END".equals(role)) {
				curve.getTargetAnchorages().remove(contentAnchorage);
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			if ("START".equals(role)) {
				curve.getSourceAnchorages().add(contentAnchorage);
			} else if ("END".equals(role)) {
				curve.getTargetAnchorages().add(contentAnchorage);
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	public IUndoableOperation getDeleteOperation(IContentPart<Node> anchorage,
			String role) {
		return new Operation(getHost().getContent(),
				(AbstractFXGeometricElement<?>) anchorage.getContent(), role);
	}

	@Override
	public FXGeometricCurvePart getHost() {
		return (FXGeometricCurvePart) super.getHost();
	}

}
