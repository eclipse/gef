/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzillas #476278 and #476507
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

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
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;

import javafx.scene.Node;

/**
 * The {@link NavigationPolicy} is an {@link AbstractTransactionPolicy} that can
 * be used to navigate between {@link Graph}s in a nested {@link Graph}s
 * scenario. It saves the layout and viewport of the current {@link Graph} when
 * opening another {@link Graph} and restores those values when navigating back
 * to the {@link Graph}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class NavigationPolicy extends AbstractTransactionPolicy<Node> {

	private ChangeContentsOperation changeContentsOperation;
	private FXChangeViewportOperation changeViewportOperation;

	@Override
	public ITransactionalOperation commit() {
		if (!isInitialized()) {
			return null;
		}

		// TODO: This violates the contract of AbstracTransactionalPolicy,
		// because locallyExecuteOperation() may not be called at any time.
		getCompositeOperation().add(changeContentsOperation);
		getCompositeOperation().add(changeViewportOperation);

		AbstractCompositeOperation commit = (AbstractCompositeOperation) super.commit();

		changeViewportOperation = null;
		changeContentsOperation = null;
		return commit;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("Open Graph");
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

	@Override
	public void init() {
		// obtain ContentModel
		ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		if (contentModel == null) {
			throw new IllegalArgumentException("ContentModel could not be obtained!");
		}

		// obtain NavigationModel
		final NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
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

			changeViewportOperation = new FXChangeViewportOperation(canvas,
					newViewportState.getContentsTransform().getCopy());
			changeViewportOperation.setNewWidth(newViewportState.getWidth());
			changeViewportOperation.setNewHeight(newViewportState.getHeight());
			changeViewportOperation.setNewHorizontalScrollOffset(newViewportState.getTranslateX());
			changeViewportOperation.setNewVerticalScrollOffset(newViewportState.getTranslateY());

			// change contents and suppress next layout pass
			changeContentsOperation = new ChangeContentsOperation(getHost().getRoot().getViewer(),
					Collections.singletonList(newGraph)) {
				@Override
				public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					if (navigationModel.getViewportState(newGraph) != null && !resetNewGraphViewport) {
						navigationModel.addSkipNextLayout(newGraph);
					}
					return super.execute(monitor, info);
				}

				@Override
				public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					if (navigationModel.getViewportState(currentGraph) != null) {
						navigationModel.addSkipNextLayout(currentGraph);
					}
					return super.undo(monitor, info);
				}
			};
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

}
