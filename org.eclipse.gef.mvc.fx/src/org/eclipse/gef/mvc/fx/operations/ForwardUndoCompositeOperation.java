/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

//TODO: init label when adding nested operations
/**
 * The {@link ForwardUndoCompositeOperation} is an
 * {@link AbstractCompositeOperation} which undoes its combined operations in
 * the same order as they are executed.
 *
 * @author anyssen
 *
 */
public class ForwardUndoCompositeOperation extends AbstractCompositeOperation {

	/**
	 * Creates a new {@link ForwardUndoCompositeOperation} with the given label.
	 *
	 * @param label
	 *            The label of this operation.
	 */
	public ForwardUndoCompositeOperation(String label) {
		super(label);
	}

}
