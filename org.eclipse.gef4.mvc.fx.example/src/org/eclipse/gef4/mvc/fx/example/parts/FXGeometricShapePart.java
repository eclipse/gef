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

import java.awt.geom.NoninvertibleTransformException;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
import org.eclipse.gef4.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXGeometricShapePart extends AbstractFXGeometricElementPart {

	private FXGeometryNode<IShape> visual;
	private IFXAnchor anchor;

	public FXGeometricShapePart() {
		visual = new FXGeometryNode<IShape>() {
			@Override
			public void resize(double width, double height) {
				if (isResizable()) {
					super.resize(width, height);
				} else {
					// TODO: this is duplicate code, share the transform with
					// the visual, and only update the transform here
					Bounds bounds = getLayoutBounds();
					double sx = width / bounds.getWidth();
					double sy = height / bounds.getHeight();
					Point start = new Point(bounds.getMinX(), bounds.getMinY());
					Point[] p = new Point[] { start.getCopy() };
					Point.scale(p, sx, sy, 0, 0);
					AffineTransform additionalTransform = new AffineTransform()
							.scale(sx, sy).translate(-p[0].x + start.x,
									-p[0].y + start.y);
					setGeometry(getGeometry().getTransformed(
							additionalTransform));
				}
			}
		};

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
						IShape visualGeometry = visual.getGeometry();
						if (shape.getTransform() != null) {
							try {
								visualGeometry = visual
										.getGeometry()
										.getTransformed(
												AWT2Geometry
														.toAffineTransform(shape
																.getTransform()
																.createInverse()));
							} catch (NoninvertibleTransformException e) {
								e.printStackTrace();
							}
						}
						final IShape newGeometry = visualGeometry;
						final IShape oldGeometry = shape.getGeometry();
						final IUndoableOperation updateModelOperation = new AbstractOperation(
								"Update Model") {

							@Override
							public IStatus undo(IProgressMonitor monitor,
									IAdaptable info) throws ExecutionException {
								shape.setGeometry(oldGeometry);
								return Status.OK_STATUS;
							}

							@Override
							public IStatus redo(IProgressMonitor monitor,
									IAdaptable info) throws ExecutionException {
								return execute(monitor, info);
							}

							@Override
							public IStatus execute(IProgressMonitor monitor,
									IAdaptable info) throws ExecutionException {
								shape.setGeometry(newGeometry);
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
				new AbstractFXTypePolicy() {
					@Override
					public void released(KeyEvent event) {
					}

					@Override
					public void pressed(KeyEvent event) {
						if (event.getCode() != KeyCode.DELETE) {
							return;
						}
						
						// prevent deletion when other policies are running
						FXClickDragTool tool = getViewer().getDomain().getAdapter(FXClickDragTool.class);
						if (tool != null && tool.isDragging()) {
							return;
						}
						
						// TODO: encapsulate in ModelsOp (
						// TODO: advance focus to next selected
						getViewer().getFocusModel().setFocused(null);
						// TODO: remove from selection
						getViewer().getSelectionModel().deselect(FXGeometricShapePart.this);
						// )
						
						executeOperation(new AbstractFXDeleteOperation(
								"delete", (IContentPart<Node>) getParent(),
								getContent()) {
							private List<AbstractFXGeometricElement<? extends IGeometry>> shapeVisuals;

							{
								shapeVisuals = ((FXGeometricModelPart) getParent())
										.getContent().getShapeVisuals();
							}

							@Override
							public void removeContentChild() {
								// TODO: remove anchor relations
								shapeVisuals.remove(getContentChild());
							}

							@SuppressWarnings("unchecked")
							@Override
							public void addContentChild() {
								// TODO: add anchor relations
								shapeVisuals
										.add((AbstractFXGeometricElement<? extends IGeometry>) getContentChild());
							}
						});
					}
				});
	}

	@Override
	public FXGeometricShape getContent() {
		return (FXGeometricShape) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public FXGeometryNode<IShape> getVisual() {
		return (FXGeometryNode<IShape>) visual;
	}

	@Override
	public void doRefreshVisual() {
		FXGeometricShape shapeVisual = getContent();
		if (visual.getGeometry() != shapeVisual.getGeometry()) {
			// TODO: respect offset, scaling, etc.
			if (shapeVisual.getTransform() == null) {
				visual.setGeometry(shapeVisual.getGeometry());
			} else {
				visual.setGeometry(shapeVisual.getGeometry().getTransformed(
						shapeVisual.getTransform()));
			}
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

}
