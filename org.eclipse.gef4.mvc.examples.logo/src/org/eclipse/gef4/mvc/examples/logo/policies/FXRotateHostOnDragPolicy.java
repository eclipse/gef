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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class FXRotateHostOnDragPolicy extends AbstractFXDragPolicy {

	private Point pivotInScene;
	private Point initialPointerLocationInScene;
	private FXTransformOperation transformOperation;
	private boolean invalidGesture = false;
	private Point2D pivotInHost;

	protected Angle computeRotationAngleCW(MouseEvent e) {
		Vector vStart = new Vector(pivotInScene, initialPointerLocationInScene);
		Vector vEnd = new Vector(pivotInScene, new Point(e.getSceneX(),
				e.getSceneY()));
		Angle angle = vStart.getAngleCW(vEnd);
		return angle;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			return;
		}

		// locally execute operation
		updateOperation(e);
		try {
			transformOperation.execute(null, null);
		} catch (ExecutionException x) {
			throw new IllegalStateException(x);
		}
	}

	@Override
	public IVisualPart<Node, ? extends Node> getHost() {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = super
				.getHost().getAnchorages();
		if (anchorages.isEmpty()) {
			return null;
		}
		// first anchorage = FXGeometricShapePart
		return anchorages.keySet().iterator().next();
	}

	@Override
	public void press(MouseEvent e) {
		// do nothing when the user does not press control
		if (!e.isControlDown()) {
			invalidGesture = true;
			return;
		}

		initialPointerLocationInScene = new Point(e.getSceneX(), e.getSceneY());
		Node hostVisual = getHost().getVisual();
		Bounds boundsInScene = hostVisual.localToScene(hostVisual
				.getLayoutBounds());
		pivotInScene = new Point(boundsInScene.getMinX()
				+ boundsInScene.getWidth() / 2, boundsInScene.getMinY()
				+ boundsInScene.getHeight() / 2);
		transformOperation = new FXTransformOperation(getHost());

		pivotInHost = hostVisual.sceneToLocal(pivotInScene.x, pivotInScene.y);
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		updateOperation(e);

		// commit changes to model
		final FXGeometricShapePart host = (FXGeometricShapePart) getHost();
		final FXGeometricShape hostContent = host.getContent();
		FXGeometryNode<IShape> hostVisual = host.getVisual();
		final IShape newGeometry = hostVisual.getGeometry();
		final IShape oldGeometry = hostContent.getGeometry();

		// determine transformation
		@SuppressWarnings("serial")
		Provider<Affine> affineProvider = host.getAdapter(AdapterKey
				.<Provider<? extends Affine>> get(
						new TypeToken<Provider<? extends Affine>>() {
						}, FXTransformOperation.TRANSFORMATION_PROVIDER_ROLE));
		AffineTransform tx = JavaFX2Geometry.toAffineTransform(affineProvider
				.get());
		final AffineTransform oldTransform = hostContent.getTransform();
		final AffineTransform newTransform = new AffineTransform(tx.getM00(),
				tx.getM10(), tx.getM01(), tx.getM11(), tx.getTranslateX(),
				tx.getTranslateY());

		// create operation to write the changes to the model
		final IUndoableOperation updateModelOperation = new AbstractOperation(
				"Update Model") {

			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				hostContent.setGeometry(newGeometry);
				hostContent.setTransform(newTransform);
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
				hostContent.setGeometry(oldGeometry);
				hostContent.setTransform(oldTransform);
				return Status.OK_STATUS;
			}
		};
		// compose operations
		IUndoableOperation compositeOperation = new ForwardUndoCompositeOperation(
				transformOperation.getLabel()) {
			{
				add(transformOperation);
				add(updateModelOperation);
			}
		};

		getHost().getRoot().getViewer().getDomain().execute(compositeOperation);
	}

	private void updateOperation(MouseEvent e) {
		Affine oldTransform = transformOperation.getOldTransform();
		AffineTransform rotate = new AffineTransform().rotate(
				computeRotationAngleCW(e).rad(), pivotInHost.getX(),
				pivotInHost.getY());
		AffineTransform newTransform = JavaFX2Geometry.toAffineTransform(
				oldTransform).concatenate(rotate);
		transformOperation.setNewTransform(Geometry2JavaFX
				.toFXAffine(newTransform));
	}

}
