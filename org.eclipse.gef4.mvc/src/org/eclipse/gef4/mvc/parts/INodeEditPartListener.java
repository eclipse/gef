/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;


/**
 * The listener interface for receiving Connection events from EditParts that
 * serve as connection nodes.
 */
// TODO: replace with observable lists
public interface INodeEditPartListener {

	/**
	 * Called prior to removing the connection from its source node. The source
	 * is not passed, but can still be obtained at this point by calling
	 * {@link IConnectionEditPart#getSource connection.getSource()}
	 * 
	 * @param connection
	 *            the connection
	 * @param index
	 *            the index
	 */
	void removingSourceConnection(IConnectionEditPart connection, int index);

	/**
	 * Called prior to removing the connection from its target node. The target
	 * is not passed, but can still be obtained at this point by calling
	 * {@link IConnectionEditPart#getTarget connection.getTarget()}
	 * 
	 * @param connection
	 *            the connection
	 * @param index
	 *            the index
	 */
	void removingTargetConnection(IConnectionEditPart connection, int index);

	/**
	 * Called after the connection has been added to its source node.
	 * 
	 * @param connection
	 *            the connection
	 * @param index
	 *            the index
	 */
	void sourceConnectionAdded(IConnectionEditPart connection, int index);

	/**
	 * Called after the connection has been added to its target node.
	 * 
	 * @param connection
	 *            the connection
	 * @param index
	 *            the index
	 */
	void targetConnectionAdded(IConnectionEditPart connection, int index);

}
