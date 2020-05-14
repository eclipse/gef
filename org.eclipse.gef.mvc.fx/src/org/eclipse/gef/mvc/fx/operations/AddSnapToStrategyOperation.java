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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.handlers.ISnapToStrategy;
import org.eclipse.gef.mvc.fx.models.SnappingModel;

/**
 * The {@link RemoveSnapToStrategyOperation} can be used to add an
 * {@link ISnapToStrategy} to the list of supported strategies that is managed
 * by the {@link SnappingModel}.
 */
public class AddSnapToStrategyOperation extends AbstractOperation
		implements ITransactionalOperation {

	private List<ISnapToStrategy> initialSupportedStrategies;
	private ISnapToStrategy strategy;
	private SnappingModel model;

	/**
	 * Constructs a new {@link AddSnapToStrategyOperation} that will add the
	 * given {@link ISnapToStrategy} to the list of supported strategies within
	 * the given {@link SnappingModel}.
	 *
	 * @param model
	 *            The {@link SnappingModel} that is manipulated.
	 * @param strategy
	 *            The {@link ISnapToStrategy} that is added.
	 */
	public AddSnapToStrategyOperation(SnappingModel model,
			ISnapToStrategy strategy) {
		super("Add Snap-To Strategy");
		this.model = model;
		this.strategy = strategy;
		initialSupportedStrategies = new ArrayList<>(
				model.snapToStrategiesProperty());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!model.snapToStrategiesProperty().contains(strategy)) {
			model.snapToStrategiesProperty().add(strategy);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialSupportedStrategies.contains(strategy);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (model.snapToStrategiesProperty().contains(strategy)) {
			model.snapToStrategiesProperty().remove(strategy);
		}
		return Status.OK_STATUS;
	}
}
