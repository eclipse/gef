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
package org.eclipse.gef4.mvc.examples.logo.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef4.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef4.mvc.examples.logo.behaviors.PaletteFocusBehavior;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.ui.MvcLogoExampleUiModule;
import org.eclipse.gef4.mvc.examples.logo.ui.properties.FXCurvePropertySource;
import org.eclipse.gef4.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.SelectOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.ui.properties.SetPropertyValueOperation;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef4.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class MvcLogoExampleView extends AbstractFXView {

	public static final class ChangeWayPointsOperation extends AbstractOperation
			implements ITransactionalOperation {

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
		// set default contents (GEF logo)
		FXViewer viewer = getContentViewer();
		ContentModel contentModel = viewer.getAdapter(ContentModel.class);
		contentModel.getContents()
				.setAll(MvcLogoExample.createDefaultContents());
		// set palette contents
		FXViewer paletteViewer = getPaletteViewer();
		ContentModel paletteContentModel = paletteViewer
				.getAdapter(ContentModel.class);
		paletteContentModel.getContents()
				.setAll(MvcLogoExample.createPaletteContents());
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
				rootEntry = new UndoablePropertySheetEntry(
						(IOperationHistory) getAdapter(IOperationHistory.class),
						(IUndoContext) getAdapter(IUndoContext.class)) {
					@Override
					public void setValues(Object[] objects) {
						if (objects == null || objects.length == 0) {
							// TODO: test
							objects = new Object[] { getContentViewer()
									.getAdapter(ContentModel.class)
									.getContents().get(0) };
						}
						super.setValues(objects);
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
									.getPropertySource() instanceof FXCurvePropertySource
									&& FXCurvePropertySource.ROUTING_STYLE_PROPERTY
											.getId()
											.equals(changeRoutingStyleOperation
													.getPropertyId())) {
								// clear way anchors using bend policy
								FXCurvePropertySource ps = (FXCurvePropertySource) changeRoutingStyleOperation
										.getPropertySource();

								// preserve first and last waypoint, but clear
								// all intermediate points
								List<Point> newWaypoints = new ArrayList<>();
								List<Point> currentWaypoints = ps.getCurve()
										.getWayPointsCopy();
								if (currentWaypoints.size() > 0) {
									newWaypoints.add(currentWaypoints.get(0));
								} else {
									newWaypoints.add(new Point());
								}
								if (currentWaypoints.size() > 1) {
									newWaypoints.add(currentWaypoints
											.get(currentWaypoints.size() - 1));
								} else {
									newWaypoints.add(new Point());
								}

								ChangeWayPointsOperation clearWaypointsOperation = new ChangeWayPointsOperation(
										"Clear waypoints", ps.getCurve(),
										currentWaypoints, newWaypoints);
								AbstractCompositeOperation c = new ForwardUndoCompositeOperation(
										"Change routing style");
								c.add(changeRoutingStyleOperation);
								c.add(clearWaypointsOperation);
								// reselect
								IContentPart<Node, ? extends Node> contentPart = getContentViewer()
										.getContentPartMap().get(ps.getCurve());
								IUndoableOperation deselectOperation = new DeselectOperation<>(
										getContentViewer(),
										Collections.singletonList(contentPart));
								IUndoableOperation selectOperation = new SelectOperation<>(
										getContentViewer(),
										Collections.singletonList(contentPart));
								c.add(deselectOperation);
								c.add(selectOperation);
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
		}
		return super.getAdapter(key);
	}

	protected FXViewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(FXViewer.class,
				MvcLogoExampleModule.PALETTE_VIEWER_ROLE));
	}

	@Override
	protected void hookViewers() {
		// determine content root node
		final FXViewer contentViewer = getContentViewer();
		InfiniteCanvas contentRootNode = contentViewer.getCanvas();
		// determine palette root node
		final FXViewer paletteViewer = getPaletteViewer();
		final InfiniteCanvas paletteRootNode = paletteViewer.getCanvas();
		// arrange viewers above each other
		AnchorPane viewersPane = new AnchorPane();
		viewersPane.getChildren().addAll(contentRootNode, paletteRootNode);
		// create palette indicator
		Pane paletteIndicator = new Pane();
		paletteIndicator.setStyle("-fx-background-color: rgba(128,128,128,1);");
		paletteIndicator.setMaxSize(10d, Double.MAX_VALUE);
		paletteIndicator.setMinSize(10d, 0d);
		// show palette indicator next to the viewer area
		HBox hbox = new HBox();
		hbox.getChildren().addAll(viewersPane, paletteIndicator);
		// ensure hbox fills the whole space
		hbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hbox.setMinSize(0, 0);
		hbox.setFillHeight(true);
		// no spacing between viewers and palette indicator
		hbox.setSpacing(0d);
		// ensure viewers fill the space
		HBox.setHgrow(viewersPane, Priority.ALWAYS);
		viewersPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		AnchorPane.setBottomAnchor(contentRootNode, 0d);
		AnchorPane.setLeftAnchor(contentRootNode, 0d);
		AnchorPane.setRightAnchor(contentRootNode, 0d);
		AnchorPane.setTopAnchor(contentRootNode, 0d);
		AnchorPane.setBottomAnchor(paletteRootNode, 0d);
		AnchorPane.setRightAnchor(paletteRootNode, 0d);
		AnchorPane.setTopAnchor(paletteRootNode, 0d);
		// disable grid layer for palette
		paletteRootNode.setZoomGrid(false);
		paletteRootNode.setShowGrid(false);
		paletteRootNode.setHorizontalScrollBarPolicy(ScrollBarPolicy.NEVER);
		// set palette background
		paletteRootNode.setStyle(PaletteFocusBehavior.DEFAULT_STYLE);
		// hide palette at first
		paletteRootNode.setVisible(false);
		// register listener to show/hide palette
		paletteIndicator.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				paletteRootNode.setVisible(true);
			}
		});
		paletteRootNode.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				paletteRootNode.setVisible(false);
			}
		});
		paletteRootNode.getContentGroup().layoutBoundsProperty()
				.addListener(new ChangeListener<Bounds>() {
					@Override
					public void changed(
							ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {
						double scrollBarWidth = paletteRootNode
								.getVerticalScrollBar().isVisible()
										? paletteRootNode.getVerticalScrollBar()
												.getLayoutBounds().getWidth()
										: 0;
						paletteRootNode.setPrefWidth(
								newValue.getWidth() + scrollBarWidth);
					}
				});
		paletteRootNode.getVerticalScrollBar().visibleProperty()
				.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						double contentWidth = paletteRootNode.getContentGroup()
								.getLayoutBounds().getWidth();
						double scrollBarWidth = newValue
								? paletteRootNode.getVerticalScrollBar()
										.getLayoutBounds().getWidth()
								: 0;
						paletteRootNode
								.setPrefWidth(contentWidth + scrollBarWidth);
					}
				});
		paletteRootNode.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (event.getTarget() != paletteRootNode) {
							paletteRootNode.setVisible(false);
						}
					}
				});
		// create scene and populate canvas
		getCanvas().setScene(new Scene(hbox));
	}

}
