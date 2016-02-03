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

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
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
public class NavigateOperation extends ReverseUndoCompositeOperation {

	private ChangeContentsOperation changeContentsOperation;
	private FXChangeViewportOperation changeViewportOperation;
	private NavigationModel navigationModel;
	private Graph initialGraph;
	private ViewportState initialViewportState;

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
		super("NavigateGraph");
		initialGraph = (Graph) viewer.getAdapter(ContentModel.class).getContents().get(0);
		navigationModel = viewer.getAdapter(NavigationModel.class);

		// create sub-operations
		changeViewportOperation = new FXChangeViewportOperation(viewer.getCanvas());
		changeContentsOperation = new ChangeContentsOperation(viewer, Collections.singletonList(initialGraph));

		// arrange sub-operations
		add(changeViewportOperation);
		add(changeContentsOperation);

		// persist the state of the initial graph
		InfiniteCanvas canvas = viewer.getCanvas();
		initialViewportState = new ViewportState(canvas.getHorizontalScrollOffset(), canvas.getVerticalScrollOffset(),
				canvas.getWidth(), canvas.getHeight(), FX2Geometry.toAffineTransform(canvas.getContentTransform()));
		navigationModel.setViewportState(initialGraph, initialViewportState);
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
	 * @param finalGraph
	 *            The final {@link Graph} to be displayed within the given
	 *            {@link FXViewer}.
	 * @param isNestedGraph
	 *            Specifies whether or not the given <i>finalGraph</i> is a
	 *            nested {@link Graph}.
	 */
	public NavigateOperation(FXViewer viewer, Graph finalGraph, boolean isNestedGraph) {
		this(viewer);
		setFinalState(finalGraph, isNestedGraph);
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
	 * @param finalGraph
	 *            The {@link Graph} that is to be displayed.
	 * @param isNestedGraph
	 *            Specifies whether or not the given <i>finalGraph</i> is a
	 *            nested {@link Graph}.
	 */
	public void setFinalState(Graph finalGraph, boolean isNestedGraph) {
		// obtain the stored state (or an initial one) for the final graph
		ViewportState newViewportState = navigationModel.getViewportState(finalGraph);
		if (newViewportState == null || isNestedGraph) {
			newViewportState = new ViewportState(0, 0, initialViewportState.getWidth(),
					initialViewportState.getHeight(), new AffineTransform());
		}

		// update the change viewport operation
		changeViewportOperation.setNewWidth(newViewportState.getWidth());
		changeViewportOperation.setNewHeight(newViewportState.getHeight());
		changeViewportOperation.setNewHorizontalScrollOffset(newViewportState.getTranslateX());
		changeViewportOperation.setNewVerticalScrollOffset(newViewportState.getTranslateY());
		changeViewportOperation.setNewContentTransform(newViewportState.getContentsTransform());

		// update the change contents operation
		changeContentsOperation.setNewContents(Collections.singletonList(finalGraph));
	}

}
