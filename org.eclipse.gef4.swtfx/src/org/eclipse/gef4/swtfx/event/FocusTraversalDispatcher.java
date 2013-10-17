/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.event;

import org.eclipse.gef4.swtfx.INode;
import org.eclipse.swt.SWT;

public class FocusTraversalDispatcher extends AbstractEventDispatcher {

	private INode target;

	public FocusTraversalDispatcher(INode target) {
		this.target = target;
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		if (event.getTarget() == target
				&& event.getEventType() == KeyEvent.KEY_PRESSED) {
			KeyEvent ke = (KeyEvent) event;
			int key = ke.getCode();
			int modMask = ke.getStateMask();

			if (key == SWT.TAB) {
				// TAB is pressed
				if ((modMask & SWT.SHIFT) == 0) {
					// nextFocus();
				} else {
					// SHIFT is pressed
					// prevFocus();
				}
			}
		}

		return event;
	}

	// private void focusNext(INode node) {
	// if (node instanceof IParent) {
	// INode currentFocus = node.getScene().getFocusTarget();
	// INode nextChild = nextChild((IParent) node, currentFocus);
	// if (nextChild == null) {
	// // node.getParentNode().focusNext();
	// focus(node.getParentNode());
	// }
	// }
	// }
	//
	// private void nextFocus() {
	// if (target instanceof IParent) {
	// } else if (target instanceof INode) {
	// target.focusNext();
	// }
	// }
	//
	// private void prevFocus() {
	// if (target instanceof IParent) {
	// } else if (target instanceof INode) {
	// target.focusPrev();
	// }
	// }

}
