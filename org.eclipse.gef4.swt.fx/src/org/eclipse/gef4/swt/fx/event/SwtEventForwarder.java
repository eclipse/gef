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
package org.eclipse.gef4.swt.fx.event;

import org.eclipse.gef4.swt.fx.ControlNode;
import org.eclipse.gef4.swt.fx.INode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;

/*
 * TODO: Refactor SwtEventTargetSelector and SwtEventForwarder, because they both share functionality.
 */

public class SwtEventForwarder implements Listener {

	private ControlNode controlNode;

	public SwtEventForwarder(ControlNode controlNode) {
		this.controlNode = controlNode;
		addListeners();
	}

	private void addListeners() {
		for (int type : SwtEventTargetSelector.EVENT_TYPES) {
			controlNode.getControl().addListener(type, this);
		}
	}

	@Override
	public void handleEvent(org.eclipse.swt.widgets.Event event) {
		Event.fireEvent(controlNode, wrap(event, controlNode));
	}

	private Event wrap(org.eclipse.swt.widgets.Event e, INode target) {
		switch (e.type) {
		case SWT.Activate:
		case SWT.Arm:
		case SWT.Close:
		case SWT.Collapse:
		case SWT.Deactivate:
		case SWT.Deiconify:
		case SWT.DefaultSelection:
		case SWT.Dispose:
		case SWT.DragDetect:
		case SWT.Expand:
		case SWT.FocusIn:
		case SWT.FocusOut:
		case SWT.Gesture:
		case SWT.Help:
		case SWT.Hide:
		case SWT.Iconify:
		case SWT.Modify:
		case SWT.MenuDetect:
		case SWT.MouseDoubleClick:
		case SWT.MouseHorizontalWheel:
		case SWT.MouseHover:
		case SWT.Move:
		case SWT.Paint:
		case SWT.Resize:
		case SWT.Selection:
		case SWT.Show:
		case SWT.Touch:
		case SWT.Verify:
			return new SwtEvent(e, target, SwtEvent.ANY);
		case SWT.KeyDown:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_PRESSED,
					e.keyCode, e.character);
		case SWT.KeyUp:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_RELEASED,
					e.keyCode, e.character);
		case SWT.MouseDown:
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_PRESSED,
					e.button, e.count, e.x, e.y);
		case SWT.MouseEnter:
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_ENTERED_TARGET, e.button, e.count, e.x,
					e.y);
		case SWT.MouseExit:
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_EXITED_TARGET, e.button, e.count, e.x, e.y);
		case SWT.MouseMove:
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_MOVED,
					e.button, e.count, e.x, e.y);
		case SWT.MouseWheel:
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_SCROLLED,
					e.button, e.count, e.x, e.y);
		case SWT.MouseUp:
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_RELEASED,
					e.button, e.count, e.x, e.y);
		case SWT.Traverse:
			return new TraverseEvent(e.widget, target, TraverseEvent.ANY,
					e.detail, e.keyCode, e.stateMask);
		default:
			throw new IllegalArgumentException(
					"This SWT event type is not supported: " + e);
		}
	}
}
