/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;

import javafx.scene.Node;

/**
 * The {@link BendVisualOperation} is an {@link ITransactionalOperation} that
 * can be used to manipulate the visual bend points of an
 * {@link IBendableContentPart}.
 */
public class BendVisualOperation extends AbstractOperation
		implements ITransactionalOperation {

	private IBendableContentPart<? extends Node> part;
	private List<BendPoint> initialBendPoints = new ArrayList<>();
	private List<BendPoint> finalBendPoints = new ArrayList<>();

	/**
	 *
	 * @param part
	 *            a
	 */
	public BendVisualOperation(IBendableContentPart<? extends Node> part) {
		super("Bend");
		this.part = part;
		initialBendPoints.addAll(part.getVisualBendPoints());
		finalBendPoints.addAll(part.getVisualBendPoints());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		part.setVisualBendPoints(finalBendPoints);
		return Status.OK_STATUS;
	}

	/**
	 * @return a
	 */
	public List<BendPoint> getFinalBendPoints() {
		return finalBendPoints;
	}

	/**
	 * @return a
	 */
	public List<BendPoint> getInitialBendPoints() {
		return initialBendPoints;
	}

	/**
	 * @return a
	 */
	public IBendableContentPart<? extends Node> getPart() {
		return part;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialBendPoints.equals(finalBendPoints);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 *
	 * @param finalBendPoints
	 *            a
	 */
	public void setFinalBendPoints(List<BendPoint> finalBendPoints) {
		this.finalBendPoints.clear();
		this.finalBendPoints.addAll(finalBendPoints);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		part.setVisualBendPoints(initialBendPoints);
		return Status.OK_STATUS;
	}
}