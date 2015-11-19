/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.policies.FXChangeViewportPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.geometry.Bounds;
import javafx.scene.Group;

/**
 * The {@link SemanticZoomPolicy} extends the {@link FXChangeViewportPolicy} for
 * associating semantic changes with viewport changes, i.e. opening of
 * nested/nesting graphs when the zoom level is changed below/above a certain
 * threshold.
 *
 * @author mwienand
 *
 */
public class SemanticZoomPolicy extends FXChangeViewportPolicy {

	/**
	 *
	 * @author mwienand
	 *
	 */
	protected class ChangeGraphContentsOperation extends ChangeContentsOperation {
		private Graph newGraph;
		private Graph currentGraph;
		private boolean resetNewGraphViewport;

		/**
		 *
		 * @param viewer
		 *            The {@link IViewer} of which the contents are changed.
		 */
		public ChangeGraphContentsOperation(IViewer<?> viewer) {
			super(viewer);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (navigationModel.getViewportState(newGraph) != null && !resetNewGraphViewport) {
				navigationModel.addSkipNextLayout(newGraph);
			}
			return super.execute(monitor, info);
		}

		/**
		 *
		 * @param currentGraph
		 *            The new value for the 'currentGraph' attribute.
		 */
		public void setCurrentGraph(Graph currentGraph) {
			this.currentGraph = currentGraph;
		}

		/**
		 *
		 * @param newGraph
		 *            The new value for the 'newGraph' attribute.
		 */
		public void setNewGraph(Graph newGraph) {
			this.newGraph = newGraph;
		}

		/**
		 *
		 * @param resetNewGraphViewport
		 *            The new value for the 'resetNewGraphViewport' attribute.
		 */
		public void setResetNewGraphViewport(boolean resetNewGraphViewport) {
			this.resetNewGraphViewport = resetNewGraphViewport;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (navigationModel.getViewportState(currentGraph) != null) {
				navigationModel.addSkipNextLayout(currentGraph);
			}
			return super.undo(monitor, info);
		}
	}

	private ContentModel contentModel;
	private NavigationModel navigationModel;

	@Override
	protected ITransactionalOperation createOperation() {
		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation("SemanticZoom()");
		// add original change viewport operation
		rev.add(super.createOperation());
		// add change contents operation
		rev.add(new ChangeGraphContentsOperation(getHost().getRoot().getViewer()));
		// add a change viewport operation that will only be used if the
		// contents change
		rev.add(new FXChangeViewportOperation(((FXViewer) getHost().getRoot().getViewer()).getCanvas()) {
			{
				setLabel("EXTRACTED");
			}
		});
		// TODO: combine both change viewport operations
		return rev;
	}

	/**
	 * Returns a {@link ChangeGraphContentsOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return A {@link ChangeGraphContentsOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected ChangeGraphContentsOperation getChangeGraphContentsOperation() {
		return (ChangeGraphContentsOperation) getCompositeOperation().getOperations().get(1);
	}

	@Override
	protected FXChangeViewportOperation getChangeViewportOperation() {
		return (FXChangeViewportOperation) getCompositeOperation().getOperations().get(0);
	}

	/**
	 * Returns an {@link AbstractCompositeOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return An {@link AbstractCompositeOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected AbstractCompositeOperation getCompositeOperation() {
		return (AbstractCompositeOperation) super.getOperation();
	}

	/**
	 * Returns a {@link ChangeContentsOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return A {@link ChangeContentsOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected FXChangeViewportOperation getUpdateViewportOperation() {
		return (FXChangeViewportOperation) getCompositeOperation().getOperations().get(2);
	}

	@Override
	public void init() {
		contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		if (contentModel == null) {
			throw new IllegalArgumentException("ContentModel could not be obtained!");
		}

		navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
		if (navigationModel == null) {
			throw new IllegalArgumentException("NavigationModel could not be obtained!");
		}

		super.init();
	}

	/**
	 * Saves the layout and viewport for the <i>currentGraph</i>, loads the
	 * layout and viewport of the <i>newGraph</i>, and changes the viewer
	 * contents. If the <i>resetNewGraphViewport</i> flag is set to
	 * <code>true</code>, then the viewport that was saved for <i>newGraph</i>
	 * will not be restored, but instead it will be reset.
	 *
	 * @param currentGraph
	 *            The currently open {@link Graph} of which the layout and
	 *            viewport and saved.
	 * @param newGraph
	 *            The new {@link Graph} that is opened.
	 * @param resetNewGraphViewport
	 *            <code>true</code> if the viewport that was saved for
	 *            <i>newGraph</i> should not be restored, otherwise
	 *            <code>false</code>.
	 */
	protected void openGraph(final Graph currentGraph, final Graph newGraph, final boolean resetNewGraphViewport) {
		checkInitialized();
		if (newGraph != null) {
			// persist the state of the current graph
			InfiniteCanvas canvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
			ViewportState currentViewportState = new ViewportState(canvas.getHorizontalScrollOffset(),
					canvas.getVerticalScrollOffset(), canvas.getWidth(), canvas.getHeight(),
					JavaFX2Geometry.toAffineTransform(canvas.getContentTransform()));
			final NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
			navigationModel.setViewportState(currentGraph, currentViewportState);

			// obtain the stored state (or an initial one) for the new graph
			ViewportState newViewportState = navigationModel.getViewportState(newGraph);
			if (newViewportState == null || resetNewGraphViewport) {
				newViewportState = new ViewportState(0, 0, currentViewportState.getWidth(),
						currentViewportState.getHeight(), new AffineTransform());
			}

			getUpdateViewportOperation().setNewWidth(newViewportState.getWidth());
			getUpdateViewportOperation().setNewHeight(newViewportState.getHeight());
			getUpdateViewportOperation().setNewHorizontalScrollOffset(newViewportState.getTranslateX());
			getUpdateViewportOperation().setNewVerticalScrollOffset(newViewportState.getTranslateY());
			getUpdateViewportOperation().setNewContentTransform(newViewportState.getContentsTransform());

			// change contents and suppress next layout pass
			getChangeGraphContentsOperation().setNewContents(Collections.singletonList(newGraph));
			getChangeGraphContentsOperation().setNewGraph(newGraph);
			getChangeGraphContentsOperation().setCurrentGraph(currentGraph);
			getChangeGraphContentsOperation().setResetNewGraphViewport(resetNewGraphViewport);
		}
	}

	/**
	 * Opens the given {@link Graph} without restoring the viewport that was
	 * saved for that {@link Graph}.
	 *
	 * @param newGraph
	 *            The {@link Graph} to open.
	 */
	public void openNestedGraph(Graph newGraph) {
		// ensure we have been properly initialized
		checkInitialized();

		ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		final Graph currentGraph = (Graph) contentModel.getContents().get(0);
		openGraph(currentGraph, newGraph, true);
	}

	/**
	 * Opens the given {@link Graph} and restores the viewport that was saved
	 * for that {@link Graph}.
	 *
	 * @param newGraph
	 *            The {@link Graph} to open.
	 */
	public void openNestingGraph(Graph newGraph) {
		// ensure we have been properly initialized
		checkInitialized();

		ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		final Graph currentGraph = (Graph) contentModel.getContents().get(0);
		openGraph(currentGraph, newGraph, false);
	}

	@Override
	public void zoomRelative(double relativeZoom, double sceneX, double sceneY) {
		checkInitialized();

		double initialZoomLevel = getChangeViewportOperation().getInitialContentTransform().getScaleX();
		double finalZoomLevel = initialZoomLevel * relativeZoom;

		if (initialZoomLevel < finalZoomLevel && finalZoomLevel > 3) {
			// filter the current content parts for nesting nodes
			List<NodeContentPart> nestingNodeContentParts = PartUtils
					.filterParts(getHost().getRoot().getViewer().getContentPartMap().values(), NodeContentPart.class);
			for (NodeContentPart nodePart : new ArrayList<NodeContentPart>(nestingNodeContentParts)) {
				if (nodePart.getContent().getNestedGraph() == null) {
					nestingNodeContentParts.remove(nodePart);
				}
			}

			// determine the nesting node that is at least partially visible and
			// nearest to the center of the viewport
			double centerDistance = Double.MAX_VALUE;
			NodeContentPart centerPart = null;

			InfiniteCanvas infiniteCanvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
			org.eclipse.gef4.geometry.planar.Rectangle viewportBounds = new org.eclipse.gef4.geometry.planar.Rectangle(
					0, 0, infiniteCanvas.getWidth(), infiniteCanvas.getHeight());

			for (NodeContentPart nodePart : new ArrayList<NodeContentPart>(nestingNodeContentParts)) {
				Group visual = nodePart.getVisual();
				Bounds boundsInScene = visual.localToScene(visual.getLayoutBounds());
				org.eclipse.gef4.geometry.planar.Rectangle boundsInViewport = JavaFX2Geometry
						.toRectangle(infiniteCanvas.sceneToLocal(boundsInScene));
				if (boundsInViewport.touches(viewportBounds)) {
					double distance = boundsInViewport.getCenter().getDistance(viewportBounds.getCenter());
					if (distance < centerDistance) {
						centerPart = nodePart;
					}
				}
			}

			// open nested graph
			if (centerPart != null) {
				openNestedGraph(centerPart.getContent().getNestedGraph());
			}
		} else if (initialZoomLevel > finalZoomLevel && finalZoomLevel < 0.7) {
			// TODO open nesting (i.e. parent) graph
			final Graph currentGraph = (Graph) contentModel.getContents().get(0);
			final Graph nestingGraph = currentGraph.getNestingNode() != null ? currentGraph.getNestingNode().getGraph()
					: null;
			if (nestingGraph != null) {
				openNestingGraph(nestingGraph);
			}
		}

		// update change viewport operation
		super.zoomRelative(relativeZoom, sceneX, sceneY);
	}

}
