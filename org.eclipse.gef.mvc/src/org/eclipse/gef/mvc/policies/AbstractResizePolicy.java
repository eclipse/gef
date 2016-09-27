/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.policies;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.operations.ResizeContentOperation;
import org.eclipse.gef.mvc.parts.IResizableContentPart;
import org.eclipse.gef.mvc.parts.IResizableVisualPart;

/**
 * Abstract base class for resize transaction policies.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractResizePolicy<VR>
		extends AbstractTransactionPolicy<VR> {

	private Dimension initialSize;

	/**
	 * Apply the new size to the host.
	 *
	 * @param dw
	 *            The width delta.
	 * @param dh
	 *            The height delta.
	 */
	protected void applySize(double dw, double dh) {
		updateResizeOperation(dw, dh);
		locallyExecuteOperation();
	}

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commitOperation = super.commit();
		if (commitOperation != null && !commitOperation.isNoOp()
				&& isContentResizable()) {
			// chain content changes
			ForwardUndoCompositeOperation composite = new ForwardUndoCompositeOperation(
					"Resize Content");
			composite.add(commitOperation);
			composite.add(createResizeContentOperation());
			commitOperation = composite;
		}

		// clear state
		initialSize = null;

		return commitOperation;
	};

	/**
	 * Create an operation to resize the content.
	 *
	 * @return The operation to resize the content.
	 */
	protected ITransactionalOperation createResizeContentOperation() {
		ResizeContentOperation<VR> resizeOperation = new ResizeContentOperation<>(
				(IResizableContentPart<VR, ? extends VR>) getHost(),
				getInitialSize(), getCurrentSize());
		return resizeOperation;
	}

	/**
	 * Returns the current size of the {@link IResizableContentPart}.
	 *
	 * @return The current size.
	 */
	protected abstract Dimension getCurrentSize();

	@Override
	public IResizableVisualPart<VR, ? extends VR> getHost() {
		return (IResizableVisualPart<VR, ? extends VR>) super.getHost();
	}

	/**
	 * Returns the initial size of the {@link IResizableContentPart}.
	 *
	 * @return The initial size.
	 */
	protected Dimension getInitialSize() {
		return initialSize;
	}

	@Override
	public void init() {
		super.init();
		initialSize = getCurrentSize();
	}

	/**
	 * Returns whether the content part supports a content resize operation.
	 *
	 * @return <code>true</code> if content resize is supported,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isContentResizable() {
		return getHost() instanceof IResizableContentPart;
	}

	/**
	 * Resizes the host by the given delta width and delta height.
	 *
	 * @param finalDw
	 *            The delta width.
	 * @param finalDh
	 *            The delta height.
	 */
	public void resize(double finalDw, double finalDh) {
		checkInitialized();
		applySize(finalDw, finalDh);
	}

	/**
	 * Update the resize operation to the new final dh and dw values.
	 *
	 * @param finalDw
	 *            The new final width delta.
	 * @param finalDh
	 *            The new final height delta.
	 */
	protected abstract void updateResizeOperation(double finalDw,
			double finalDh);

}