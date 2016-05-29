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
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
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
public class NavigateOperation extends ForwardUndoCompositeOperation {

	private final class UpdateViewportStateOperation extends AbstractOperation implements ITransactionalOperation {

		private ViewportState initialViewportState;
		private ViewportState finalViewportState;

		public UpdateViewportStateOperation(ViewportState initialViewportState) {
			super("Update Viewport State");
			this.initialViewportState = initialViewportState == null ? null : initialViewportState.getCopy();
			this.finalViewportState = initialViewportState;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (finalViewportState != null) {
				navigationModel.setViewportState(sourceGraph, finalViewportState);
			} else {
				navigationModel.removeViewportState(sourceGraph);
			}
			return Status.OK_STATUS;
		}

		@Override
		public boolean isContentRelevant() {
			return false;
		}

		@Override
		public boolean isNoOp() {
			return initialViewportState == null ? finalViewportState == null
					: initialViewportState.equals(finalViewportState);
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		public void setFinalViewportState(ViewportState finalViewportState) {
			this.finalViewportState = finalViewportState;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (initialViewportState != null) {
				navigationModel.setViewportState(sourceGraph, initialViewportState);
			} else {
				navigationModel.removeViewportState(sourceGraph);
			}
			return Status.OK_STATUS;
		}

	}

	private ChangeContentsOperation changeContentsOperation;
	private FXChangeViewportOperation changeViewportOperation;
	private NavigationModel navigationModel;
	private FXViewer viewer;
	private Graph sourceGraph;
	private UpdateViewportStateOperation updateViewportStateOperation;

	/**
	 * Creates a new {@link NavigateOperation} that saves the layout and
	 * viewport for the currently displayed {@link Graph}. The final state for
	 * the operation can later be set using
	 * {@link #setFinalState(Graph, boolean)}.
	 *
	 * @param viewer
	 *            The {@link FXViewer} of which the contents and viewport are
	 *            manipulated.
	 */
	public NavigateOperation(FXViewer viewer) {
		super("Navigate Graph");

		this.viewer = viewer;
		sourceGraph = (Graph) viewer.getAdapter(ContentModel.class).getContents().get(0);
		// retrieve initial viewport state of source graph
		navigationModel = viewer.getAdapter(NavigationModel.class);

		// create sub-operations
		changeContentsOperation = new ChangeContentsOperation(viewer, Collections.singletonList(sourceGraph));
		changeViewportOperation = new FXChangeViewportOperation(viewer.getCanvas());
		updateViewportStateOperation = new UpdateViewportStateOperation(navigationModel.getViewportState(sourceGraph));

		// arrange sub-operations
		add(changeViewportOperation);
		add(changeContentsOperation);
		add(updateViewportStateOperation);
	}

	/**
	 * Creates a new {@link NavigateOperation} that saves the layout and
	 * viewport for the currently displayed {@link Graph}, loads the layout and
	 * viewport of the <i>finalGraph</i>, and changes the viewer contents. If
	 * the <i>isNestedGraph</i> flag is set to <code>true</code>, then the
	 * viewport that was saved for <i>finalGraph</i> will not be restored, but
	 * instead it will be reset.
	 *
	 * @param viewer
	 *            The {@link FXViewer} of which the contents and viewport are
	 *            manipulated.
	 * @param targetGraph
	 *            The final {@link Graph} to be displayed within the given
	 *            {@link FXViewer}.
	 * @param isNestedGraph
	 *            Specifies whether or not the given <i>finalGraph</i> is a
	 *            nested {@link Graph}.
	 */
	public NavigateOperation(FXViewer viewer, Graph targetGraph, boolean isNestedGraph) {
		this(viewer);
		setFinalState(targetGraph, isNestedGraph);
	}

	/**
	 * Returns the {@link FXChangeViewportOperation} that is used by this
	 * {@link NavigateOperation} to update the viewport.
	 *
	 * @return The {@link FXChangeViewportOperation} that is used by this
	 *         {@link NavigateOperation} to update the viewport.
	 */
	public FXChangeViewportOperation getChangeViewportOperation() {
		return changeViewportOperation;
	}

	/**
	 * Changes this {@link NavigateOperation}'s final state so that the given
	 * <i>finalGraph</i> is opened. If the <i>isNestedGraph</i> flag is set to
	 * <code>true</code>, then the viewport that was saved for <i>finalGraph</i>
	 * will not be restored, but instead it will be reset.
	 *
	 * @param targetGraph
	 *            The {@link Graph} that is to be displayed.
	 * @param isNestedGraph
	 *            Specifies whether or not the given <i>finalGraph</i> is a
	 *            nested {@link Graph}.
	 */
	public void setFinalState(Graph targetGraph, boolean isNestedGraph) {
		// persist the state of the current graph (before zooming in)
		InfiniteCanvas canvas = viewer.getCanvas();
		if (isNestedGraph) {
			// is we are navigating to a nested graph, store our viewport
			ViewportState sourceGraphFinalViewportState = new ViewportState(canvas.getHorizontalScrollOffset(),
					canvas.getVerticalScrollOffset(), canvas.getWidth(), canvas.getHeight(),
					FX2Geometry.toAffineTransform(canvas.getContentTransform()));
			updateViewportStateOperation.setFinalViewportState(sourceGraphFinalViewportState);
			changeViewportOperation.setInitialWidth(sourceGraphFinalViewportState.getWidth());
			changeViewportOperation.setInitialHeight(sourceGraphFinalViewportState.getHeight());
			changeViewportOperation.setInitialHorizontalScrollOffset(sourceGraphFinalViewportState.getTranslateX());
			changeViewportOperation.setInitialVerticalScrollOffset(sourceGraphFinalViewportState.getTranslateY());
			changeViewportOperation.setInitialContentTransform(sourceGraphFinalViewportState.getContentsTransform());
		} else {
			updateViewportStateOperation.setFinalViewportState(null);
		}

		// update the change viewport operation
		ViewportState newViewportState = navigationModel.getViewportState(targetGraph);
		if (newViewportState == null || isNestedGraph) {
			newViewportState = new ViewportState(0, 0, canvas.getWidth(), canvas.getHeight(), new AffineTransform());
		}
		changeViewportOperation.setNewWidth(newViewportState.getWidth());
		changeViewportOperation.setNewHeight(newViewportState.getHeight());
		changeViewportOperation.setNewHorizontalScrollOffset(newViewportState.getTranslateX());
		changeViewportOperation.setNewVerticalScrollOffset(newViewportState.getTranslateY());
		changeViewportOperation.setNewContentTransform(newViewportState.getContentsTransform());

		// update the change contents operation
		changeContentsOperation.setNewContents(Collections.singletonList(targetGraph));
	}

}
