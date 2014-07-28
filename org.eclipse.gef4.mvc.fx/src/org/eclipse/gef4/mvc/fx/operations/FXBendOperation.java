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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.nodes.IFXConnection;

/**
 * An {@link FXBendOperation} can be used to manipulate an {@link IFXConnection}
 * in an undo-context.
 *
 * @author mwienand
 *
 */
public class FXBendOperation extends AbstractOperation {

	private final IFXConnection connection;
	private final List<AnchorLink> oldLinks;
	private final List<AnchorLink> newLinks;

	/**
	 * Constructs an "empty" operation which will not change anything.
	 */
	public FXBendOperation() {
		this("no-op", null, null, null);
	}

	/**
	 * Constructs a new operation from the given values.
	 *
	 * @param oldLinks
	 *            List of old {@link AnchorLink}s.
	 * @param newLinks
	 *            List of new {@link AnchorLink}s.
	 */
	public FXBendOperation(String label, IFXConnection connection,
			List<AnchorLink> oldLinks, List<AnchorLink> newLinks) {
		super(label);
		this.connection = connection;
		if (oldLinks == null) {
			this.oldLinks = Collections.emptyList();
		} else {
			this.oldLinks = new ArrayList<AnchorLink>(oldLinks);
		}
		if (newLinks == null) {
			this.newLinks = Collections.emptyList();
		} else {
			this.newLinks = new ArrayList<AnchorLink>(newLinks);
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			connection.setWayPointAnchorLinks(newLinks.subList(1,
					newLinks.size() - 1));
			connection.setStartAnchorLink(newLinks.get(0));
			connection.setEndAnchorLink(newLinks.get(newLinks.size() - 1));
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public String toString() {
		return "FXBendOperation";
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			connection.setWayPointAnchorLinks(oldLinks.subList(1,
					oldLinks.size() - 1));
			connection.setStartAnchorLink(oldLinks.get(0));
			connection.setEndAnchorLink(oldLinks.get(oldLinks.size() - 1));
		}
		return Status.OK_STATUS;
	}

}