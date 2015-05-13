/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ClearHoverFocusSelectionOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;

public class FXDeletionPolicy extends DeletionPolicy<Node> {

	@Override
	public IUndoableOperation commit() {
		IUndoableOperation deleteOperation = super.commit();
		// clear interaction models
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"Delete Selected");
		fwd.add(deleteOperation);
		fwd.add(new ClearHoverFocusSelectionOperation<Node>(getHost().getRoot()
				.getViewer()));
		return fwd;
	}

}
