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
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXPolyBezierConnectionRouter;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXBendPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
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
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
			curve.setWayPoints(newWayPoints.toArray(new Point[] {}));
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
			curve.setWayPoints(oldWayPoints.toArray(new Point[] {}));
			return Status.OK_STATUS;
		}
	}

	public static class CircleHead extends Circle implements IFXDecoration {
		public CircleHead() {
			super(5);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	private FXConnection visual;

	public FXGeometricCurvePart() {
		visual = new FXConnection();
		visual.setRouter(new FXPolyBezierConnectionRouter());

		// TODO: extract into own classes and use binding
		setAdapter(AdapterKey.get(FXResizeRelocatePolicy.class),
				new FXRelocateConnectionPolicy() {
					@Override
					public IUndoableOperation commit() {
						return chainModelChanges(super.commit());
					}
				});
		setAdapter(AdapterKey.get(FXBendPolicy.class), new FXBendPolicy() {
			@Override
			public IUndoableOperation commit() {
				return chainModelChanges(super.commit());
			}
		});
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (role.equals("START")) {
			visual.setStartAnchor(anchor);
		} else if (role.equals("END")) {
			visual.setEndAnchor(anchor);
		} else {
			throw new IllegalStateException(
					"Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	public void attachToContentAnchorage(Object contentAnchorage, String role) {
		if (!(contentAnchorage instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException(
					"Inappropriate content anchorage: wrong type.");
		}
		AbstractFXGeometricElement<?> geom = (AbstractFXGeometricElement<?>) contentAnchorage;
		if ("START".equals(role)) {
			getContent().getSourceAnchorages().add(geom);
		} else if ("END".equals(role)) {
			getContent().getTargetAnchorages().add(geom);
		}
	}

	IUndoableOperation chainModelChanges(
			final IUndoableOperation updateVisualOperation) {
		if (updateVisualOperation == null) {
			return null;
		}

		// determine old and new points
		final FXGeometricCurve curve = getContent();
		final List<Point> oldWayPoints = curve.getWayPointsCopy();
		final List<Point> newWayPoints = visual.getWayPoints();

		// create model operation
		final IUndoableOperation updateModelOperation = new ChangeWayPointsOperation(
				"Update model", curve, oldWayPoints, newWayPoints);

		// create anchorage operations
		ContentPolicy<Node> contentPolicy = this
				.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
		contentPolicy.init();
		contentPolicy.detachFromAllContentAnchorages();
		AbstractFXGeometricElement<?> sourceContentAnchorage = getAnchorageContent(visual
				.getStartAnchor());
		AbstractFXGeometricElement<?> targetContentAnchorage = getAnchorageContent(visual
				.getEndAnchor());
		if (sourceContentAnchorage != null) {
			contentPolicy.attachToContentAnchorage(sourceContentAnchorage,
					"START");
		}
		if (targetContentAnchorage != null) {
			contentPolicy.attachToContentAnchorage(targetContentAnchorage,
					"END");
		}
		final IUndoableOperation updateAnchoragesOperation = contentPolicy
				.commit();

		// compose operations
		AbstractCompositeOperation compositeOperation = new AbstractCompositeOperation(
				updateVisualOperation.getLabel()) {
			{
				add(updateVisualOperation);
				add(updateModelOperation);
				if (updateAnchoragesOperation != null) {
					add(updateAnchoragesOperation);
				}
			}
		};

		return compositeOperation;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		if (role.equals("START")) {
			visual.setStartPoint(visual.getStartPoint());
		} else if (role.equals("END")) {
			visual.setEndPoint(visual.getEndPoint());
		} else {
			throw new IllegalStateException(
					"Cannot detach from anchor with role <" + role + ">.");
		}
	}

	@Override
	public void detachFromContentAnchorage(Object contentAnchorage, String role) {
		if ("START".equals(role)) {
			getContent().getSourceAnchorages().remove(contentAnchorage);
		} else if ("END".equals(role)) {
			getContent().getTargetAnchorages().remove(contentAnchorage);
		}
	}

	@Override
	public void doRefreshVisual() {
		FXGeometricCurve content = getContent();

		List<Point> wayPoints = content.getWayPoints();
		if (content.getTransform() != null) {
			Point[] transformedWayPoints = content.getTransform()
					.getTransformed(wayPoints.toArray(new Point[] {}));
			wayPoints = Arrays.asList(transformedWayPoints);
		}
		visual.setWayPoints(wayPoints);

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

	protected AbstractFXGeometricElement<?> getAnchorageContent(IFXAnchor anchor) {
		Node anchorageNode = anchor.getAnchorage();
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
	public SetMultimap<Object, String> getContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		Set<AbstractFXGeometricElement<? extends IGeometry>> sourceAnchorages = getContent()
				.getSourceAnchorages();
		for (Object src : sourceAnchorages) {
			anchorages.put(src, "START");
		}
		Set<AbstractFXGeometricElement<? extends IGeometry>> targetAnchorages = getContent()
				.getTargetAnchorages();
		for (Object dst : targetAnchorages) {
			anchorages.put(dst, "END");
		}
		return anchorages;
	}

	@Override
	public Node getVisual() {
		return visual;
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
	public String toString() {
		return "FXGeometricCurvePart@" + System.identityHashCode(this);
	}

}