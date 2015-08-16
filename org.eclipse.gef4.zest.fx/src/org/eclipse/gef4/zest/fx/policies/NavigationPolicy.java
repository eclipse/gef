/**
 *
 */
package org.eclipse.gef4.zest.fx.policies;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.policies.FXChangeViewportPolicy;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.models.ViewportModel.ViewportState;
import org.eclipse.gef4.mvc.operations.ChangeContentsOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.zest.fx.models.NavigationModel;

import javafx.scene.Node;

/**
 * @author anyssen
 *
 */
public class NavigationPolicy extends AbstractPolicy<Node>implements ITransactional {

	private ChangeContentsOperation changeContentsOperation = null;

	@Override
	public IUndoableOperation commit() {
		ReverseUndoCompositeOperation commit = new ReverseUndoCompositeOperation("Open Graph");
		// add change viewport operation
		FXChangeViewportPolicy changeViewportPolicy = getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
		commit.add(changeViewportPolicy.commit());
		commit.add(changeContentsOperation);
		changeContentsOperation = null;
		return commit.unwrap();
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
		// obtain ViewportModel
		final ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		if (viewportModel == null) {
			throw new IllegalStateException("ViewportModel could not be obtained!");
		}
		// obtain FXChangeViewportPolicy
		FXChangeViewportPolicy changeViewportPolicy = getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
		if (changeViewportPolicy == null) {
			throw new IllegalStateException("FXChangeViewportPolicy could not be obtained!");
		}
		changeViewportPolicy.init();
	}

	protected void openGraph(final Graph currentGraph, final Graph newGraph, final boolean resetNewGraphViewport) {
		if (newGraph != null) {
			// persist the state of the current graph
			final ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
			ViewportState currentViewportState = viewportModel.retrieveState(false, false, true, true, false);
			final NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
			navigationModel.setViewportState(currentGraph, currentViewportState);

			// obtain the stored state (or an initial one) for the new graph
			ViewportState newViewportState = navigationModel.getViewportState(newGraph);
			if (newViewportState == null || resetNewGraphViewport) {
				newViewportState = new ViewportState();
			}

			// apply viewport changes
			// TODO: do not violate encapsulation of viewport change policy here
			FXChangeViewportPolicy changeViewportPolicy = getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
			FXChangeViewportOperation changeViewportOperation = ReflectionUtils
					.getPrivateFieldValue(changeViewportPolicy, "operation");
			changeViewportOperation.setNewTx(newViewportState.getTranslateX());
			changeViewportOperation.setNewTy(newViewportState.getTranslateY());
			changeViewportOperation.setNewTransform(newViewportState.getContentsTransform().getCopy());
			try {
				changeViewportOperation.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

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
			try {
				changeContentsOperation.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void openNestedGraph(Graph newGraph) {
		ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		final Graph currentGraph = (Graph) contentModel.getContents().get(0);
		openGraph(currentGraph, newGraph, true);
	}

	public void openNestingGraph(Graph newGraph) {
		ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		final Graph currentGraph = (Graph) contentModel.getContents().get(0);
		openGraph(currentGraph, newGraph, false);
	}
}
