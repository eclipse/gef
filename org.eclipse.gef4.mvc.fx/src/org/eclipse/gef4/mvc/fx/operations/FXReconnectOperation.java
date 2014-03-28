/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;

public class FXReconnectOperation extends AbstractOperation {

	private IFXConnection connection;
	private IFXAnchor oldAnchor;
	private IFXAnchor newAnchor;
	private Map<Object, Object> context;

	public FXReconnectOperation(String label, IFXConnection connection,
			IFXAnchor oldAnchor, IFXAnchor newAnchor, Map<Object, Object> context) {
		super(label);
		this.connection = connection;
		this.oldAnchor = oldAnchor;
		this.newAnchor = newAnchor;
		this.context = context;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		connection.attachTo(newAnchor, context);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		connection.attachTo(oldAnchor, context);
		return Status.OK_STATUS;
	}

}
