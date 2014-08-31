/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class FXGeometricShapePart extends AbstractFXGeometricElementPart {

	private FXGeometryNode<IShape> visual;
	private IFXAnchor anchor;

	public FXGeometricShapePart() {
		visual = new FXGeometryNode<IShape>();

		// TODO: inject these adapters
		// interaction policies
		setAdapter(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY),
				new FXRelocateOnDragPolicy());

		// transaction policies
		setAdapter(AdapterKey.get(FXResizeRelocatePolicy.class),
				new FXResizeRelocatePolicy() {
			@Override
			public IUndoableOperation commit() {
				final IUndoableOperation updateVisualOperation = super
						.commit();
				if (updateVisualOperation == null) {
					return null;
				}

				// commit changes to model
				final FXGeometricShape shape = getContent();
				final IShape newGeometry = visual.getGeometry();
				final IShape oldGeometry = shape.getGeometry();

				final AffineTransform oldTransform = shape
						.getTransform();
				final AffineTransform newTransform = new AffineTransform(
						1, 0, 0, 1, visual.getLayoutX(), visual
						.getLayoutY());

				final IUndoableOperation updateModelOperation = new AbstractOperation(
						"Update Model") {

					@Override
					public IStatus execute(IProgressMonitor monitor,
							IAdaptable info) throws ExecutionException {
						shape.setGeometry(newGeometry);
						shape.setTransform(newTransform);
						return Status.OK_STATUS;
					}

					@Override
					public IStatus redo(IProgressMonitor monitor,
							IAdaptable info) throws ExecutionException {
						return execute(monitor, info);
					}

					@Override
					public IStatus undo(IProgressMonitor monitor,
							IAdaptable info) throws ExecutionException {
						shape.setGeometry(oldGeometry);
						shape.setTransform(oldTransform);
						return Status.OK_STATUS;
					}
				};
				// compose both operations
				IUndoableOperation compositeOperation = new AbstractCompositeOperation(
						updateVisualOperation.getLabel()) {
					{
						add(updateVisualOperation);
						add(updateModelOperation);
					}
				};

				return compositeOperation;
			}
		});

		setAdapter(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY),
				new FXDeleteSelectedOnTypePolicy());
	}

	@Override
	public void doRefreshVisual() {
		FXGeometricShape shapeVisual = getContent();
		if (visual.getGeometry() != shapeVisual.getGeometry()) {
			visual.setGeometry(shapeVisual.getGeometry());

		}

		if (shapeVisual.getTransform() != null) {
			visual.relocate(shapeVisual.getTransform().getTranslateX()
					+ visual.getLayoutBounds().getMinX(), shapeVisual
					.getTransform().getTranslateY()
					+ visual.getLayoutBounds().getMinY());
		}

		// apply stroke paint
		if (visual.getStroke() != shapeVisual.getStroke()) {
			visual.setStroke(shapeVisual.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != shapeVisual.getStrokeWidth()) {
			visual.setStrokeWidth(shapeVisual.getStrokeWidth());
		}

		if (visual.getFill() != shapeVisual.getFill()) {
			visual.setFill(shapeVisual.getFill());
		}

		// apply effect
		super.doRefreshVisual();
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			anchor = new FXChopBoxAnchor(getVisual()) {
				@Override
				protected IShape getAnchorageReferenceShape() {
					// return the visual's geometry within the coordinate system
					// of the anchorage Node
					return getVisual().getGeometry();
				}
			};
		}
		return anchor;
	}

	@Override
	public FXGeometricShape getContent() {
		return (FXGeometricShape) super.getContent();
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		for (AbstractFXGeometricElement<? extends IGeometry> anchorage : getContent()
				.getAnchorages()) {
			anchorages.put(anchorage, "link");
		}
		return anchorages;
	}

	@Override
	public FXGeometryNode<IShape> getVisual() {
		return visual;
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setContent(model);
	}

}
