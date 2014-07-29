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
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;

public abstract class AbstractFXDeleteOperation extends AbstractOperation {

	private Object contentChild;
	private IContentPart<Node> parent;

	public AbstractFXDeleteOperation(String label, IContentPart<Node> parent,
			Object contentChild) {
		super(label);
		this.contentChild = contentChild;
		this.parent = parent;
	}
	
	public IContentPart<Node> getParent() {
		return parent;
	}
	
	public Object getContentChild() {
		return contentChild;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		removeContentChild();
		ContentBehavior contentBehavior = parent
				.getAdapter(ContentBehavior.class);
		contentBehavior.synchronizeContentChildren(parent.getContentChildren());
		return Status.OK_STATUS;
	}

	public abstract void removeContentChild();

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		addContentChild();
		ContentBehavior contentBehavior = parent
				.getAdapter(ContentBehavior.class);
		contentBehavior.synchronizeContentChildren(parent.getContentChildren());
		return Status.OK_STATUS;
	}

	public abstract void addContentChild();

}
