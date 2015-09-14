/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.policies.FXBendPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

public class FXGeometricCurvePart
		extends AbstractFXGeometricElementPart<FXConnection> {

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

	private static final class ChangeWayPointsOperation
			extends AbstractOperation {

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

	private final CircleHead START_CIRCLE_HEAD = new CircleHead();
	private final CircleHead END_CIRCLE_HEAD = new CircleHead();
	private final ArrowHead START_ARROW_HEAD = new ArrowHead();
	private final ArrowHead END_ARROW_HEAD = new ArrowHead();
	private FXGeometricCurve previousContent;

	public FXGeometricCurvePart() {
		// TODO: extract into own classes and use binding
		setAdapter(AdapterKey.get(FXTransformPolicy.class),
				new FXTransformConnectionPolicy() {
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

	@SuppressWarnings("serial")
	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		IFXAnchor anchor = anchorage
				.getAdapter(new TypeToken<Provider<? extends IFXAnchor>>() {
				}).get();
		if (role.equals("START")) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals("END")) {
			getVisual().setEndAnchor(anchor);
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
		final List<Point> newWayPoints = getVisual().getWayPoints();

		// create model operation
		final IUndoableOperation updateModelOperation = new ChangeWayPointsOperation(
				"Update Model", curve, oldWayPoints, newWayPoints);

		// create anchorage operations, start with detaching all anchorages
		ContentPolicy<Node> contentPolicy = this
				.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
		contentPolicy.init();
		contentPolicy.detachFromAllContentAnchorages();
		final IUndoableOperation detachOperation = contentPolicy.commit();

		// then attach source and target (if available)
		contentPolicy.init();
		AbstractFXGeometricElement<?> sourceContentAnchorage = getAnchorageContent(
				getVisual().getStartAnchor());
		if (sourceContentAnchorage != null) {
			contentPolicy.attachToContentAnchorage(sourceContentAnchorage,
					"START");
		}
		AbstractFXGeometricElement<?> targetContentAnchorage = getAnchorageContent(
				getVisual().getEndAnchor());
		if (targetContentAnchorage != null) {
			contentPolicy.attachToContentAnchorage(targetContentAnchorage,
					"END");
		}
		final IUndoableOperation attachOperation = contentPolicy.commit();

		// compose operations
		return new ForwardUndoCompositeOperation(
				updateVisualOperation.getLabel()) {
			{
				add(updateVisualOperation);
				add(updateModelOperation);
				if (detachOperation != null || attachOperation != null) {
					add(new ReverseUndoCompositeOperation("Change Anchorages") {
						{
							if (detachOperation != null) {
								add(detachOperation);
							}
							if (attachOperation != null) {
								add(attachOperation);
							}
						}
					});
				}
			}
		};
	}

	@Override
	protected FXConnection createVisual() {
		FXConnection visual = new FXConnection();
		visual.setRouter(new FXPolyBezierConnectionRouter());
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (role.equals("START")) {
			getVisual().setStartPoint(getVisual().getStartPoint());
		} else if (role.equals("END")) {
			getVisual().setEndPoint(getVisual().getEndPoint());
		} else {
			throw new IllegalStateException(
					"Cannot detach from anchor with role <" + role + ">.");
		}
	}

	@Override
	public void detachFromContentAnchorage(Object contentAnchorage,
			String role) {
		if ("START".equals(role)) {
			getContent().getSourceAnchorages().remove(contentAnchorage);
		} else if ("END".equals(role)) {
			getContent().getTargetAnchorages().remove(contentAnchorage);
		}
	}

	@Override
	public void doRefreshVisual(FXConnection visual) {
		FXGeometricCurve content = getContent();

		List<Point> wayPoints = content.getWayPoints();

		AffineTransform transform = content.getTransform();
		if (previousContent == null || (transform != null
				&& !transform.equals(previousContent.getTransform())
				|| transform == null
						&& previousContent.getTransform() != null)) {
			if (transform != null) {
				Point[] transformedWayPoints = transform
						.getTransformed(wayPoints.toArray(new Point[] {}));
				wayPoints = Arrays.asList(transformedWayPoints);
			}
		}

		if (!visual.getWayPoints().equals(wayPoints)) {
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
				visual.setStartDecoration(START_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getStartDecoration() == null
					|| !(visual.getStartDecoration() instanceof ArrowHead)) {
				visual.setStartDecoration(START_ARROW_HEAD);
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
				visual.setEndDecoration(END_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getEndDecoration() == null
					|| !(visual.getEndDecoration() instanceof ArrowHead)) {
				visual.setEndDecoration(END_ARROW_HEAD);
			}
			break;
		}
		Shape startDecorationVisual = visual.getStartDecoration() != null
				? ((Shape) visual.getStartDecoration().getVisual()) : null;
		Shape endDecorationVisual = visual.getEndDecoration() != null
				? ((Shape) visual.getEndDecoration().getVisual()) : null;

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
		if (visual.getCurveNode().getStrokeWidth() != content
				.getStrokeWidth()) {
			visual.getCurveNode().setStrokeWidth(content.getStrokeWidth());
		}
		if (startDecorationVisual != null && startDecorationVisual
				.getStrokeWidth() != content.getStrokeWidth()) {
			startDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}
		if (endDecorationVisual != null && endDecorationVisual
				.getStrokeWidth() != content.getStrokeWidth()) {
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

		previousContent = content;

		// apply effect
		super.doRefreshVisual(visual);
	}

	protected AbstractFXGeometricElement<?> getAnchorageContent(
			IFXAnchor anchor) {
		Node anchorageNode = anchor.getAnchorage();
		if (anchorageNode != getVisual()) {
			IVisualPart<Node, ? extends Node> part = getViewer()
					.getVisualPartMap().get(anchorageNode);
			if (part instanceof IContentPart) {
				Object content = ((IContentPart<Node, ? extends Node>) part)
						.getContent();
				if (content instanceof AbstractFXGeometricElement) {
					return (AbstractFXGeometricElement<?>) content;
				}
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
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricCurve)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setContent(model);
	}

}