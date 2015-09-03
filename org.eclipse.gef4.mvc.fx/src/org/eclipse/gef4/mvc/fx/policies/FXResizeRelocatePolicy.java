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
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;

// TODO: check if we really need this policy, as we now have the transform policy
public class FXResizeRelocatePolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public IUndoableOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;

		// assemble commits of delegate policies to one operation
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"ResizeRelocate");
		FXResizePolicy resizePolicy = getResizePolicy();
		IUndoableOperation commit = resizePolicy == null ? null
				: resizePolicy.commit();
		if (commit != null) {
			fwd.add(commit);
		}
		FXTransformPolicy transformPolicy = getTransformPolicy();
		commit = transformPolicy == null ? null : transformPolicy.commit();
		if (commit != null) {
			fwd.add(commit);
		}
		return fwd.unwrap();
	}

	protected double getMinimumHeight() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	protected double getMinimumWidth() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	protected FXResizePolicy getResizePolicy() {
		return getHost().getAdapter(FXResizePolicy.class);
	}

	protected FXTransformPolicy getTransformPolicy() {
		return getHost().getAdapter(FXTransformPolicy.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef4.mvc.fx.policies.ITransactionalPolicy#init()
	 */
	@Override
	public void init() {
		// initialize delegate policies
		FXTransformPolicy transformPolicy = getTransformPolicy();
		if (transformPolicy != null) {
			transformPolicy.init();
		}
		FXResizePolicy resizePolicy = getResizePolicy();
		if (resizePolicy != null) {
			resizePolicy.init();
		}
		initialized = true;
	}

	public void performResizeRelocate(double dx, double dy, double dw,
			double dh) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// relocate in middle of resize area if no resize policy is available
		FXResizePolicy resizePolicy = getResizePolicy();
		if (resizePolicy == null) {
			dw = 0;
			dh = 0;
			dx += dw / 2;
			dy += dh / 2;
		}
		// delegate to resize and transform policies
		if (resizePolicy != null) {
			resizePolicy.performResize(dw, dh);
		}
		FXTransformPolicy transformPolicy = getTransformPolicy();
		if (transformPolicy != null) {
			transformPolicy.setPreConcatenation(
					new AffineTransform().setToTranslation(dx, dy));
		}
	}

}
