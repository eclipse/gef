/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXResizeRelocatePolicy extends AbstractPolicy<Node> implements
		ITransactional {

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public IUndoableOperation commit() {
		// assemble commits of delegate policies to one operation
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"ResizeRelocate");
		IUndoableOperation commit = getResizePolicy().commit();
		if (commit != null) {
			fwd.add(commit);
		}
		commit = getTransformPolicy().commit();
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
		getTransformPolicy().init();
		getResizePolicy().init();
	}

	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		// relocate in middle of resize area if visual is not resizable
		if (!getHost().getVisual().isResizable()) {
			dw = 0;
			dh = 0;
			dx += dw / 2;
			dy += dh / 2;
		}
		// delegate to resize and transform policies
		getResizePolicy().performResize(dw, dh);
		getTransformPolicy().setPreConcatenation(
				new AffineTransform().setToTranslation(dx, dy));
	}

}
