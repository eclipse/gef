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
package org.eclipse.gef4.zest.fx.operations;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;

/**
 * The {@link NavigateOperation} is a {@link ReverseUndoCompositeOperation} that
 * combines a {@link ChangeContentsOperation} and an
 * {@link FXChangeViewportOperation} to navigate between nested and parent
 * {@link Graph}s.
 *
 * @author mwienand
 *
 */
public class NavigateOperation extends ReverseUndoCompositeOperation {

	private final class ChangeContentsAndSkipLayoutOperation extends ChangeContentsOperation {
		private final NavigationModel navigationModel;
		private final Graph currentGraph;
		private Graph newGraph;
		private boolean resetNewGraphViewport;

		private ChangeContentsAndSkipLayoutOperation(IViewer<?> viewer, NavigationModel navigationModel,
				Graph currentGraph) {
			super(viewer);
			this.navigationModel = navigationModel;
			this.currentGraph = currentGraph;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (navigationModel.getViewportState(newGraph) != null && !resetNewGraphViewport) {
				navigationModel.addSkipNextLayout(newGraph);
			}
			return super.execute(monitor, info);
		}

		public void setNewGraph(Graph newGraph) {
			this.newGraph = newGraph;
		}

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

	/**
	 * Creates a new {@link NavigateOperation} to open the given (nested)
	 * {@link Graph} without restoring the viewport that was saved for that
	 * {@link Graph}.
	 *
	 * @param viewer
	 *            The {@link FXViewer} of which the contents and viewport are
	 *            manipulated by the newly created {@link NavigateOperation}.
	 * @param nestedGraph
	 *            The (nested) {@link Graph} to open.
	 * @return The new {@link NavigateOperation} that can be used to open the
	 *         given (nested) {@link Graph}.
	 */
	public static NavigateOperation openNestedGraph(FXViewer viewer, Graph nestedGraph) {
		return new NavigateOperation(viewer, nestedGraph, true);
	}

	/**
	 * Creates a new {@link NavigateOperation} to open the given (parent)
	 * {@link Graph} and restores the viewport that was saved for that
	 * {@link Graph}.
	 *
	 * @param viewer
	 *            The {@link FXViewer} of which the contents and viewport are
	 *            manipulated by the newly created {@link NavigateOperation}.
	 * @param nestingGraph
	 *            The (parent) {@link Graph} to open.
	 * @return The new {@link NavigateOperation} that can be used to open the
	 *         given (parent) {@link Graph}.
	 */
	public static NavigateOperation openNestingGraph(FXViewer viewer, Graph nestingGraph) {
		return new NavigateOperation(viewer, nestingGraph, false);
	}

	private ChangeContentsAndSkipLayoutOperation changeContentsOperation;
	private FXChangeViewportOperation changeViewportOperation;

	/**
	 * Saves the layout and viewport for the currently displayed {@link Graph},
	 * loads the layout and viewport of the <i>finalGraph</i>, and changes the
	 * viewer contents. If the <i>resetFinalGraphViewport</i> flag is set to
	 * <code>true</code>, then the viewport that was saved for <i>finalGraph</i>
	 * will not be restored, but instead it will be reset.
	 *
	 * @param viewer
	 *            The {@link FXViewer} of which the contents and viewport are
	 *            manipulated.
	 * @param finalGraph
	 *            The final {@link Graph} to be displayed within the given
	 *            {@link FXViewer}.
	 * @param resetFinalGraphViewport
	 *            Flag to indicate whether or not the viewport should be reset
	 *            for the final {@link Graph}.
	 */
	public NavigateOperation(FXViewer viewer, Graph finalGraph, boolean resetFinalGraphViewport) {
		super("NavigateGraph");
		Graph initialGraph = (Graph) viewer.getAdapter(ContentModel.class).getContents().get(0);

		// create sub-operations
		final NavigationModel navigationModel = viewer.getAdapter(NavigationModel.class);
		changeViewportOperation = new FXChangeViewportOperation(viewer.getCanvas());
		changeContentsOperation = new ChangeContentsAndSkipLayoutOperation(viewer, navigationModel, initialGraph);

		// arrange sub-operations
		add(changeContentsOperation);
		add(changeViewportOperation);

		// persist the state of the current graph
		InfiniteCanvas canvas = viewer.getCanvas();
		ViewportState initialViewportState = new ViewportState(canvas.getHorizontalScrollOffset(),
				canvas.getVerticalScrollOffset(), canvas.getWidth(), canvas.getHeight(),
				JavaFX2Geometry.toAffineTransform(canvas.getContentTransform()));
		navigationModel.setViewportState(initialGraph, initialViewportState);

		// obtain the stored state (or an initial one) for the new graph
		ViewportState newViewportState = navigationModel.getViewportState(finalGraph);
		if (newViewportState == null || resetFinalGraphViewport) {
			newViewportState = new ViewportState(0, 0, initialViewportState.getWidth(),
					initialViewportState.getHeight(), new AffineTransform());
		}

		changeViewportOperation.setNewWidth(newViewportState.getWidth());
		changeViewportOperation.setNewHeight(newViewportState.getHeight());
		changeViewportOperation.setNewHorizontalScrollOffset(newViewportState.getTranslateX());
		changeViewportOperation.setNewVerticalScrollOffset(newViewportState.getTranslateY());
		changeViewportOperation.setNewContentTransform(newViewportState.getContentsTransform());

		// change contents and suppress next layout pass
		changeContentsOperation.setNewContents(Collections.singletonList(finalGraph));
		changeContentsOperation.setNewGraph(finalGraph);
		changeContentsOperation.setResetNewGraphViewport(resetFinalGraphViewport);
	}

}
