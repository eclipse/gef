/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
