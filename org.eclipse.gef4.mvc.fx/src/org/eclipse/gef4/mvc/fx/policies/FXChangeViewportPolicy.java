/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #476507
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * A transactional {@link IPolicy} to change the viewport of an {@link IViewer}
 * via its {@link InfiniteCanvas}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXChangeViewportPolicy extends AbstractPolicy<Node>
		implements ITransactional {

	private FXChangeViewportOperation operation = null;
	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	@Override
	public ITransactionalOperation commit() {
		// after commit, we need to be re-initialized
		initialized = false;

		// clear operation and return current one (and formerly pushed
		// operations)
		if (operation != null && !operation.isNoOp()) {
			ITransactionalOperation commit = operation;
			operation = null;
			return commit;
		}
		return null;
	}

	@Override
	public void init() {
		FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
		operation = new FXChangeViewportOperation(viewer.getCanvas(),
				JavaFX2Geometry.toAffineTransform(
						viewer.getCanvas().getContentTransform()));
		// we are properly initialized now
		initialized = true;
	}

	/**
	 * Advances the viewport transformation by the given translation values.
	 *
	 * @param deltaTranslateX
	 *            The horizontal translation delta.
	 * @param deltaTranslateY
	 *            The vertical translation delta.
	 */
	public void scrollRelative(double deltaTranslateX, double deltaTranslateY) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		operation.setNewTx(operation.getOldTx() + deltaTranslateX);
		operation.setNewTy(operation.getOldTy() + deltaTranslateY);
		try {
			operation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Concatenates a scaling transformation to the current viewport
	 * transformation.
	 *
	 * @param relativeZoom
	 *            The scale factor.
	 * @param sceneX
	 *            The pivot x-coordinate.
	 * @param sceneY
	 *            The pivot y-coordinate.
	 */
	public void zoomRelative(double relativeZoom, double sceneX,
			double sceneY) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// System.out.println("RELATIVE ZOOM: " + relativeZoom);
		// compute transformation
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getCanvas().getContentGroup().sceneToLocal(sceneX, sceneY);
		operation.concatenateToNewTransform(new AffineTransform()
				.translate(contentGroupPivot.getX(), contentGroupPivot.getY())
				.scale(relativeZoom, relativeZoom).translate(
						-contentGroupPivot.getX(), -contentGroupPivot.getY()));
		// locally execute operation
		try {
			operation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
