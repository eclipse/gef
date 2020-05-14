/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - pull up actions
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleViewersComposite;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.ui.MvcLogoExampleUiModule;
import org.eclipse.gef.mvc.examples.logo.ui.properties.GeometricCurvePropertySource;
import org.eclipse.gef.mvc.examples.logo.ui.properties.MvcLogoExampleViewPropertySource;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.ui.properties.SetPropertyValueOperation;
import org.eclipse.gef.mvc.fx.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef.mvc.fx.ui.properties.UndoablePropertySheetPage;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

import javafx.scene.Node;
import javafx.scene.Scene;

public class MvcLogoExampleView extends AbstractFXView {

	public static final class ChangeWayPointsOperation extends AbstractOperation
			implements ITransactionalOperation {

		private final GeometricCurve curve;
		private final List<Point> newWayPoints;
		private final List<Point> oldWayPoints;

		public ChangeWayPointsOperation(String label, GeometricCurve curve,
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
		public boolean isContentRelevant() {
			return true;
		}

		@Override
		public boolean isNoOp() {
			return oldWayPoints == newWayPoints || (oldWayPoints != null
					&& oldWayPoints.equals(newWayPoints));
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

	private UndoablePropertySheetEntry rootEntry;

	// TODO: create AbstractFXView via an executable extension factory
	// (obtaining the injector via the bundle)
	public MvcLogoExampleView() {
		super(Guice.createInjector(Modules.override(new MvcLogoExampleModule())
				.with(new MvcLogoExampleUiModule())));

		// set initial contents
		getContentViewer().getContents()
				.setAll(MvcLogoExample.createContentViewerContents());
		getPaletteViewer().getContents()
				.setAll(MvcLogoExample.createPaletteViewerContents());
	}

	@Override
	public void dispose() {
		// clear viewer models
		getContentViewer().getAdapter(SelectionModel.class).clearSelection();
		getContentViewer().getAdapter(HoverModel.class).clearHover();
		getContentViewer().getAdapter(FocusModel.class).setFocus(null);
		getContentViewer().contentsProperty().clear();
		getPaletteViewer().getAdapter(SelectionModel.class).clearSelection();
		getPaletteViewer().getAdapter(HoverModel.class).clearHover();
		getPaletteViewer().getAdapter(FocusModel.class).setFocus(null);
		getPaletteViewer().contentsProperty().clear();
		
		super.dispose();
	}

	/**
	 * The {@link UpdateSelectionHandlesOperation} can be used to update the
	 * selection handles for a given {@link IContentPart}. Re-selecting (i.e.
	 * deselect & select) is not possible in the context of a
	 * UndoablePropertySheetEntry#valueChanged(), because #valueChanged() is
	 * also called when the selection changes, and mutations from within a
	 * collection listener are forbidden.
	 */
	private static class UpdateSelectionHandlesOperation
			extends AbstractOperation implements ITransactionalOperation {

		private IContentPart<? extends Node> part;

		public UpdateSelectionHandlesOperation(
				IContentPart<? extends Node> part) {
			super("UpdateHandles");
			this.part = part;
		}

		@Override
		public boolean isContentRelevant() {
			return false;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			part.getRoot().getAdapter(SelectionBehavior.class)
					.updateHandles(part, null, null);
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
			return execute(monitor, info);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if (IPropertySheetPage.class.equals(key)) {
			// use another UndoablePropertySheetEntry, which chains undo of
			// waypoint removal
			UndoablePropertySheetPage propertySheetPage = (UndoablePropertySheetPage) super.getAdapter(
					key);
			if (rootEntry == null) {
				rootEntry = new UndoablePropertySheetEntry(this,
						(IOperationHistory) getAdapter(IOperationHistory.class),
						(IUndoContext) getAdapter(IUndoContext.class)) {
					// FIXME: Code copied from BendConnectionPolicy (see
					// #494752)
					private Point computeEndHint(Connection connection) {
						if (connection.getEndAnchor() instanceof DynamicAnchor
								&& connection.getPointsUnmodifiable()
										.size() > 1) {
							Point endPoint = connection.getEndPoint();
							Point neighbor = connection.getPoint(
									connection.getPointsUnmodifiable().size()
											- 2);
							Point translated = endPoint.getTranslated(endPoint
									.getDifference(neighbor).getScaled(0.5));
							return translated;
						}
						return null;
					}

					// FIXME: Code copied from BendConnectionPolicy (see
					// #494752)
					private Point computeStartHint(Connection connection) {
						if (connection.getStartAnchor() instanceof DynamicAnchor
								&& connection.getPointsUnmodifiable()
										.size() > 1) {
							Point startPoint = connection.getStartPoint();
							Point neighbor = connection.getPoint(1);
							Point translated = startPoint.getTranslated(
									startPoint.getDifference(neighbor)
											.getScaled(0.5));
							return translated;
						}
						return null;
					}

					@Override
					protected void valueChanged(
							UndoablePropertySheetEntry child,
							ITransactionalOperation operation) {
						// in case routing style is changed, clear the
						// waypoints (chain into a composite operation)
						if (operation instanceof SetPropertyValueOperation) {
							SetPropertyValueOperation changeRoutingStyleOperation = (SetPropertyValueOperation) operation;
							if (changeRoutingStyleOperation
									.getPropertySource() instanceof GeometricCurvePropertySource
									&& GeometricCurvePropertySource.ROUTING_STYLE_PROPERTY
											.getId()
											.equals(changeRoutingStyleOperation
													.getPropertyId())) {
								// clear way anchors using bend policy
								GeometricCurvePropertySource ps = (GeometricCurvePropertySource) changeRoutingStyleOperation
										.getPropertySource();
								IContentPart<? extends Node> contentPart = getContentViewer()
										.getContentPartMap().get(ps.getCurve());

								// preserve first and last waypoint, but clear
								// all intermediate points
								List<Point> newWaypoints = new ArrayList<>();
								List<Point> currentWaypoints = ps.getCurve()
										.getWayPointsCopy();
								// FIXME: Code copied from
								// BendConnectionPolicy (see #494752)
								newWaypoints.add(computeStartHint(
										(Connection) contentPart.getVisual()));
								newWaypoints.add(computeEndHint(
										(Connection) contentPart.getVisual()));

								ChangeWayPointsOperation clearWaypointsOperation = new ChangeWayPointsOperation(
										"Clear waypoints", ps.getCurve(),
										currentWaypoints, newWaypoints);
								AbstractCompositeOperation c = new ForwardUndoCompositeOperation(
										"Change routing style");
								c.add(changeRoutingStyleOperation);
								c.add(clearWaypointsOperation);
								c.add(new UpdateSelectionHandlesOperation(
										contentPart));
								super.valueChanged(child, c);
							} else {
								super.valueChanged(child, operation);
							}
						} else {
							super.valueChanged(child, operation);
						}
					}
				};
				propertySheetPage.setRootEntry(rootEntry);
			}
			return propertySheetPage;
		} else if (IPropertySource.class.equals(key)) {
			return new MvcLogoExampleViewPropertySource(this);
		}
		return super.getAdapter(key);
	}

	protected IViewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(IViewer.class,
				MvcLogoExampleModule.PALETTE_VIEWER_ROLE));
	}

	@Override
	protected void hookViewers() {
		// build viewers composite
		MvcLogoExampleViewersComposite viewersComposite = new MvcLogoExampleViewersComposite(
				getContentViewer(), getPaletteViewer());
		// create scene and populate canvas
		getCanvas().setScene(new Scene(viewersComposite.getComposite()));
	}
}
