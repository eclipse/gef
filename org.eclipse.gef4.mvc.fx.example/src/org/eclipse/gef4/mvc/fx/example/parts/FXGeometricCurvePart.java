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
import java.util.Collections;
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
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXBendPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXReconnectPolicy;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

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

	private static class WayPointModelOperation extends AbstractOperation {

		private FXGeometricCurvePart part;
		private List<Point> oldWayPoints = new ArrayList<Point>();
		private List<Point> newWayPoints = new ArrayList<Point>();

		public WayPointModelOperation(FXGeometricCurvePart part,
				List<Point> oldWayPoints, List<Point> newWayPoints) {
			super("Change way points in model.");
			this.part = part;
			for (Point p : oldWayPoints) {
				this.oldWayPoints.add(p.getCopy());
			}
			for (Point p : newWayPoints) {
				this.newWayPoints.add(p.getCopy());
			}
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			removeCurveWayPoints();
			addCurveWayPoints(newWayPoints);
			// System.out.println(toString());
			return Status.OK_STATUS;
		}

		private void addCurveWayPoints(List<Point> wayPoints) {
			FXGeometricCurve curve = part.getContent();
			int i = 0;
			for (Point p : wayPoints) {
				curve.addWayPoint(i++, new Point(p));
			}
			ISelectionModel<Node> selm = part.getViewer().getSelectionModel();
			if (selm.getSelected().contains(part)) {
				selm.deselect(part);
				selm.select(Collections.singletonList((IContentPart<Node>) part));
			}
		}

		private void removeCurveWayPoints() {
			FXGeometricCurve curve = part.getContent();
			List<Point> wayPoints = curve.getWayPoints();
			for (int i = wayPoints.size() - 1; i >= 0; --i) {
				curve.removeWayPoint(i);
			}
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			removeCurveWayPoints();
			addCurveWayPoints(oldWayPoints);
			return Status.OK_STATUS;
		}

		@Override
		public String toString() {
			String str = "ChangeWayPoints (model):\n  from:\n";
			for (int i = 0; i < oldWayPoints.size(); i++) {
				str = str + "   - " + oldWayPoints.get(i) + "\n";
			}
			str = str + "  to:\n";
			for (int i = 0; i < newWayPoints.size(); i++) {
				str = str + "   - " + newWayPoints.get(i) + "\n";
			}
			return str;
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
		
		// transaction policies
		setAdapter(FXBendPolicy.class, new FXBendPolicy() {

			@Override
			public IUndoableOperation commit() {
				final IUndoableOperation op = super.commit();

				FXGeometricCurve curve = getContent();
				List<Point> oldWayPoints = curve.getWayPoints();
				List<Point> newWayPoints = visual.getWayPoints();
				final WayPointModelOperation modelOp = new WayPointModelOperation(
						FXGeometricCurvePart.this, oldWayPoints, newWayPoints);

				// compose both operations
				IUndoableOperation compositeOperation = new AbstractCompositeOperation(
						"Change way points.") {
					{
						add(op);
						add(modelOp);
					}
				};

				return compositeOperation;
			}
		});
		setAdapter(FXReconnectPolicy.class, new FXReconnectPolicy());
	}

	@Override
	public FXGeometricCurve getContent() {
		return (FXGeometricCurve) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (!(model instanceof FXGeometricCurve)) {
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
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		// TODO: the context should not be stored in the model, but created here
		// based on the model
		AbstractFXGeometricElement<?> anchorageContent = ((AbstractFXGeometricElementPart) anchorage)
				.getContent();
		boolean isStart = anchorageContent.getSourceAnchoreds().contains(
				getContent());
		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (isStart) {
			visual.setStartAnchorLink(new AnchorLink(anchor, new AnchorKey(visual, "START")));
		} else {
			visual.setEndAnchorLink(new AnchorLink(anchor, new AnchorKey(visual, "END")));
		}
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (anchor == visual.startAnchorLinkProperty().get().getAnchor()) {
			visual.setStartPoint(visual.getStartPoint());
		} else if (anchor == visual.endAnchorLinkProperty().get().getAnchor()) {
			visual.setEndPoint(visual.getEndPoint());
		} else {
			throw new IllegalStateException(
					"Cannot detach from unknown anchor: " + anchor);
		}
		// TODO: what if multiple points are bound to the same anchor?
	}

}
