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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

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
import org.eclipse.gef4.fx.listeners.VisualChangeListener;
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
	private VisualChangeListener vcl;
	protected boolean inResizeRelocate;

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

						inResizeRelocate = false;
						return compositeOperation;
					}

					@Override
					public void init() {
						super.init();
						inResizeRelocate = true;
					}
				});

		setAdapter(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY),
				new FXDeleteSelectedOnTypePolicy());
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		if (role.equals("link")) {
			if (vcl != null) {
				throw new IllegalStateException(
						"Only one 'link' anchorage can be attached!");
			}
			vcl = new VisualChangeListener() {
				@Override
				protected void boundsInLocalChanged(Bounds oldBounds,
						Bounds newBounds) {
				}

				@Override
				protected void localToParentTransformChanged(Node observed,
						Transform oldTransform, Transform newTransform) {
					// if we are currently being relocated (by means of mouse
					// interaction or something else), we do not want to update
					// here
					if (inResizeRelocate) {
						// MAYBE: Provide ITransactional#isInProgress() which is
						// true only if #init() was called but #commit() was
						// not,
						// yet. Then we could remove the inResizeRelocate flag.
						return;
					}
					// compute relocation
					double otx = oldTransform.getTx();
					double oty = oldTransform.getTy();
					double ntx = newTransform.getTx();
					double nty = newTransform.getTy();
					double dx = ntx - otx;
					double dy = nty - oty;
					// apply relocation to this part too
					FXResizeRelocatePolicy rrPolicy = getAdapter(FXResizeRelocatePolicy.class);
					rrPolicy.init();
					rrPolicy.performResizeRelocate(dx, dy, 0, 0);
					IUndoableOperation rrOp = rrPolicy.commit();
					/*
					 * The anchorage's RR is still in progress, that's why we
					 * are not allowed to put this operation on the undo
					 * context. Therefore we execute it only locally to keep us
					 * in sync with the anchorage.
					 *
					 * XXX: This definitely needs to change for a 'proper'
					 * implementation. The RR for this anchored should be kept
					 * in sync with the RR for the anchorage, i.e. init(),
					 * perform(), and commit() would be called for both. We
					 * still have to evaluate how to handle other anchorage
					 * changes.
					 */
					try {
						rrOp.execute(null, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			vcl.register(anchorage.getVisual(), getVisual());
		}
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		if (role.equals("link")) {
			if (vcl == null) {
				throw new IllegalStateException(
						"No 'link' anchorage is attached!");
			}
			vcl.unregister();
			vcl = null;
		}
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
			anchor = new FXChopBoxAnchor(getVisual());
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
