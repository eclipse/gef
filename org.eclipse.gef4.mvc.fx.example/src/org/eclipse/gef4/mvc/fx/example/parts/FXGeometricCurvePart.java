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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXChopBoxHelper;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXBendPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	private static final class ChangeContentAnchoragesOperation extends
			AbstractOperation {

		private final FXGeometricCurve curve;
		private final AbstractFXGeometricElement<?> oldSource;
		private final AbstractFXGeometricElement<?> oldTarget;
		private final AbstractFXGeometricElement<?> newSource;
		private final AbstractFXGeometricElement<?> newTarget;

		public ChangeContentAnchoragesOperation(String label,
				FXGeometricCurve curve,
				AbstractFXGeometricElement<?> oldSource,
				AbstractFXGeometricElement<?> oldTarget,
				AbstractFXGeometricElement<?> newSource,
				AbstractFXGeometricElement<?> newTarget) {
			super(label);
			this.curve = curve;
			this.oldSource = oldSource;
			this.oldTarget = oldTarget;
			this.newSource = newSource;
			this.newTarget = newTarget;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			curve.getSourceAnchorages().clear();
			if (newSource != null)
				curve.getSourceAnchorages().add(newSource);
			curve.getTargetAnchorages().clear();
			if (newTarget != null)
				curve.getTargetAnchorages().add(newTarget);
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
			curve.getSourceAnchorages().clear();
			if (oldSource != null)
				curve.getSourceAnchorages().add(oldSource);
			curve.getTargetAnchorages().clear();
			if (oldTarget != null)
				curve.getTargetAnchorages().add(oldTarget);
			return Status.OK_STATUS;
		}

	}

	private static final class ChangeWayPointsOperation extends
			AbstractOperation {

		private final FXGeometricCurve curve;
		private final List<Point> newWayPoints;
		private final List<Point> oldWayPoints;

		public ChangeWayPointsOperation(String label, FXGeometricCurve curve,
				List<Point> oldWayPoints, List<Point> newWayPoints) {
			super(label);
			this.curve = curve;
			this.oldWayPoints = oldWayPoints;
			this.newWayPoints = newWayPoints;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			removeCurveWayPoints();
			addCurveWayPoints(oldWayPoints);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			removeCurveWayPoints();
			addCurveWayPoints(newWayPoints);
			return Status.OK_STATUS;
		}

		private void addCurveWayPoints(List<Point> wayPoints) {
			int i = 0;
			for (Point p : wayPoints) {
				curve.addWayPoint(i++, new Point(p));
			}
		}

		private void removeCurveWayPoints() {
			List<Point> wayPoints = curve.getWayPoints();
			for (int i = wayPoints.size() - 1; i >= 0; --i) {
				curve.removeWayPoint(i);
			}
		}
	}

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	public static class CircleHead extends Circle implements IFXDecoration {
		public CircleHead() {
			super(5);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	private FXCurveConnection visual;

	public FXGeometricCurvePart() {
		visual = new FXCurveConnection() {
			@Override
			public ICurve computeGeometry(Point[] points) {
				return FXGeometricCurve.constructCurveFromWayPoints(points);
			}
		};
		new FXChopBoxHelper(visual);

		// TODO: move operations and policies to their own types and use binding
		setAdapter(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY),
				new FXRelocateOnDragPolicy());
		setAdapter(
				AdapterKey.get(FXRelocateOnDragPolicy.TRANSACTION_POLICY_KEY),
				new FXResizeRelocatePolicy() {
					@Override
					public void init() {
						super.init();
					}

					@Override
					public void performResizeRelocate(double dx, double dy,
							double dw, double dh) {
						// do not relocate when there are no way points
						if (visual.getWayPointAnchorLinks().size() > 0) {
							super.performResizeRelocate(dx, dy, dw, dh);
							refreshVisual(); // TODO: should not be necessary
						}
					}

					@Override
					public IUndoableOperation commit() {
						// retrieve visual operation
						final IUndoableOperation visualOperation = super
								.commit();

						// determine model values
						FXGeometricCurve curve = getContent();
						List<Point> oldWayPoints = curve.getWayPointsCopy();
						List<Point> newWayPoints = visual.getWayPoints();
						AbstractFXGeometricElement<?> oldSource = getAnchorageContent(visual
								.getStartAnchorLink());
						AbstractFXGeometricElement<?> oldTarget = getAnchorageContent(visual
								.getEndAnchorLink());
						AbstractFXGeometricElement<?> newSource = oldSource;// getAnchorageContent(visual.getStartAnchorLink());
						AbstractFXGeometricElement<?> newTarget = oldTarget;// getAnchorageContent(visual.getEndAnchorLink());

						// create model operations
						final IUndoableOperation modelWayPointOperation = new ChangeWayPointsOperation(
								"Update model", curve, oldWayPoints,
								newWayPoints);
						final IUndoableOperation modelAnchoragesOperation = new ChangeContentAnchoragesOperation(
								"Update model", curve, oldSource, oldTarget,
								newSource, newTarget);

						// combine operations
						return new AbstractCompositeOperation(visualOperation
								.getLabel()) {
							{
								add(visualOperation);
								add(modelWayPointOperation);
								add(modelAnchoragesOperation);
							}
						};
					}
				});

		// transaction policies
		setAdapter(AdapterKey.get(FXBendPolicy.class), new FXBendPolicy() {
			@Override
			public IUndoableOperation commit() {
				// retrieve visual operation
				final IUndoableOperation updateVisualOperation = super.commit();
				if (updateVisualOperation == null) {
					return null;
				}

				// determine model values
				final FXGeometricCurve curve = getContent();
				final List<Point> oldWayPoints = curve.getWayPointsCopy();
				final List<Point> newWayPoints = visual.getWayPoints();

				final IUndoableOperation updateModelOperation = new ChangeWayPointsOperation(
						"Update model", curve, oldWayPoints, newWayPoints);

				AbstractFXGeometricElement<?> oldSource = curve
						.getSourceAnchorages().isEmpty() ? null : curve
						.getSourceAnchorages().get(0);
				AbstractFXGeometricElement<?> oldTarget = curve
						.getTargetAnchorages().isEmpty() ? null : curve
						.getTargetAnchorages().get(0);

				AbstractFXGeometricElement<?> newSource = getAnchorageContent(visual
						.getStartAnchorLink());
				AbstractFXGeometricElement<?> newTarget = getAnchorageContent(visual
						.getEndAnchorLink());
				final IUndoableOperation updateAnchoragesOperation = new ChangeContentAnchoragesOperation(
						"Update anchorages.", curve, oldSource, oldTarget,
						newSource, newTarget);

				// compose both operations
				IUndoableOperation compositeOperation = new AbstractCompositeOperation(
						updateVisualOperation.getLabel()) {
					{
						add(updateVisualOperation);
						add(updateModelOperation);
						add(updateAnchoragesOperation);
					}
				};

				return compositeOperation;
			}
		});
	}

	ChangeContentAnchoragesOperation getContentAnchoragesOperation(
			AbstractFXGeometricElement<?> newSource,
			AbstractFXGeometricElement<?> newTarget) {
		List<AbstractFXGeometricElement<? extends IGeometry>> sourceAnchorages = getContent()
				.getSourceAnchorages();
		List<AbstractFXGeometricElement<? extends IGeometry>> targetAnchorages = getContent()
				.getTargetAnchorages();
		return new ChangeContentAnchoragesOperation("anchorages", getContent(),
				sourceAnchorages.isEmpty() ? null
						: (AbstractFXGeometricElement<?>) sourceAnchorages
								.get(0),
				targetAnchorages.isEmpty() ? null
						: (AbstractFXGeometricElement<?>) targetAnchorages
								.get(0), newSource, newTarget);
	}

	protected AbstractFXGeometricElement<?> getAnchorageContent(AnchorLink link) {
		Node anchorageNode = link.getAnchor().getAnchorageNode();
		IVisualPart<Node> part = getViewer().getVisualPartMap().get(
				anchorageNode);
		if (part instanceof IContentPart) {
			Object content = ((IContentPart<Node>) part).getContent();
			if (content instanceof AbstractFXGeometricElement) {
				return (AbstractFXGeometricElement<?>) content;
			}
		}
		return null;
	}

	@Override
	public FXGeometricCurve getContent() {
		return (FXGeometricCurve) super.getContent();
	}

	@Override
	public String toString() {
		return "FXGeometricCurvePart@" + System.identityHashCode(this);
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricCurve)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void doRefreshVisual() {
		FXGeometricCurve content = getContent();

		// TODO: compare way points to identify if we need to refresh
		List<Point> wayPoints = content.getWayPoints();

		if (content.getTransform() != null) {
			Point[] transformedWayPoints = content.getTransform()
					.getTransformed(wayPoints.toArray(new Point[] {}));
			visual.setWayPoints(Arrays.asList(transformedWayPoints));
		} else {
			visual.setWayPoints(wayPoints);
		}

		// decorations
		switch (content.getSourceDecoration()) {
		case NONE:
			if (visual.getStartDecoration() != null) {
				visual.setStartDecoration(null);
			}
			break;
		case CIRCLE:
			if (visual.getStartDecoration() == null
					|| !(visual.getStartDecoration() instanceof CircleHead)) {
				visual.setStartDecoration(new CircleHead());
			}
			break;
		case ARROW:
			if (visual.getStartDecoration() == null
					|| !(visual.getStartDecoration() instanceof ArrowHead)) {
				visual.setStartDecoration(new ArrowHead());
			}
			break;
		}
		switch (content.getTargetDecoration()) {
		case NONE:
			if (visual.getEndDecoration() != null) {
				visual.setEndDecoration(null);
			}
			break;
		case CIRCLE:
			if (visual.getEndDecoration() == null
					|| !(visual.getEndDecoration() instanceof CircleHead)) {
				visual.setEndDecoration(new CircleHead());
			}
			break;
		case ARROW:
			if (visual.getEndDecoration() == null
					|| !(visual.getEndDecoration() instanceof ArrowHead)) {
				visual.setEndDecoration(new ArrowHead());
			}
			break;
		}
		Shape startDecorationVisual = visual.getStartDecoration() != null ? ((Shape) visual
				.getStartDecoration().getVisual()) : null;
		Shape endDecorationVisual = visual.getEndDecoration() != null ? ((Shape) visual
				.getEndDecoration().getVisual()) : null;

		// stroke paint
		if (visual.getCurveNode().getStroke() != content.getStroke()) {
			visual.getCurveNode().setStroke(content.getStroke());
		}
		if (startDecorationVisual != null
				&& startDecorationVisual.getStroke() != content.getStroke()) {
			startDecorationVisual.setStroke(content.getStroke());
		}
		if (endDecorationVisual != null
				&& endDecorationVisual.getStroke() != content.getStroke()) {
			endDecorationVisual.setStroke(content.getStroke());
		}

		// stroke width
		if (visual.getCurveNode().getStrokeWidth() != content.getStrokeWidth()) {
			visual.getCurveNode().setStrokeWidth(content.getStrokeWidth());
		}
		if (startDecorationVisual != null
				&& startDecorationVisual.getStrokeWidth() != content
						.getStrokeWidth()) {
			startDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}
		if (endDecorationVisual != null
				&& endDecorationVisual.getStrokeWidth() != content
						.getStrokeWidth()) {
			endDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}

		// dashes
		List<Double> dashList = new ArrayList<Double>(content.dashes.length);
		for (double d : content.dashes) {
			dashList.add(d);
		}
		if (!visual.getCurveNode().getStrokeDashArray().equals(dashList)) {
			visual.getCurveNode().getStrokeDashArray().setAll(dashList);
		}

		// apply effect
		super.doRefreshVisual();
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			int index) {
		// anchorages are ordered, thus we may use the index here
		boolean isStart = index == 0;
		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (isStart) {
			visual.setStartAnchorLink(new AnchorLink(anchor, new AnchorKey(
					visual, IFXConnection.START_ROLE)));
		} else {
			visual.setEndAnchorLink(new AnchorLink(anchor, new AnchorKey(
					visual, IFXConnection.END_ROLE)));
		}
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			int index) {
		// FIXME: after 0 is removed, end anchorage is at index 0, too
		if (index == 0) {
			if (visual.isStartConnected()) {
				visual.setStartPoint(visual.getStartPoint());
			} else {
				visual.setEndPoint(visual.getEndPoint());
			}
		} else if (index == 1) {
			visual.setEndPoint(visual.getEndPoint());
		} else {
			throw new IllegalStateException(
					"Cannot detach from anchor at index <" + index + ">.");
		}
	}

	@Override
	public List<Object> getContentAnchorages() {
		List<Object> anchorages = new ArrayList<Object>();
		anchorages.addAll(getContent().getSourceAnchorages());
		anchorages.addAll(getContent().getTargetAnchorages());
		return anchorages;
	}

}
