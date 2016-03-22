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
package org.eclipse.gef4.mvc.policies;

import java.util.List;

import org.eclipse.gef4.mvc.operations.BendContentOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IBendableContentPart;
import org.eclipse.gef4.mvc.parts.IBendableContentPart.BendPoint;

/**
 * Abstract base implementation for bend policies.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBendPolicy<VR>
		extends AbstractTransactionPolicy<VR> {

	private List<BendPoint> initialBendPoints;

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commitOperation = super.commit();
		if (commitOperation != null && !commitOperation.isNoOp()
				&& getHost() instanceof IBendableContentPart) {
			// chain content changes
			// unconnected control points
			ForwardUndoCompositeOperation composite = new ForwardUndoCompositeOperation(
					"Bend Content");
			composite.add(commitOperation);
			BendContentOperation<VR> resizeOperation = new BendContentOperation<>(
					(IBendableContentPart<VR, ? extends VR>) getHost(),
					getInitialBendPoints(), getCurrentBendPoints());
			composite.add(resizeOperation);

			commitOperation = composite;
		}

		// clear state
		initialBendPoints = null;

		return commitOperation;
	}

	/**
	 * Returns the current control points of the content.
	 *
	 * @return The current control points.
	 */
	protected abstract List<BendPoint> getCurrentBendPoints();

	/**
	 * Returns the initial bend points before bending the content.
	 *
	 * @return The initial bend points.
	 */
	protected List<BendPoint> getInitialBendPoints() {
		return initialBendPoints;
	}

	@Override
	public void init() {
		super.init();
		initialBendPoints = getCurrentBendPoints();
	}

}