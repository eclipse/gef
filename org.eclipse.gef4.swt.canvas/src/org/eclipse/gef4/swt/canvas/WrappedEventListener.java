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
package org.eclipse.gef4.swt.canvas;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Event;

public class WrappedEventListener implements IEventListener {

	private static final List<Integer> MOUSE_LISTENER_TYPES = Arrays.asList(
			SWT.MouseDoubleClick, SWT.MouseDown, SWT.MouseUp);
	private static final List<Integer> KEY_LISTENER_TYPES = Arrays.asList(
			SWT.KeyDown, SWT.KeyUp);
	private List<Integer> types = new LinkedList<Integer>();

	/**
	 * <p>
	 * <code>Object listener;</code>
	 * </p>
	 * <p>
	 * Stores the SWT typed listener that the user wants to get notified on
	 * corresponding events.
	 * </p>
	 * <p>
	 * The listener object is not typed as SWTEventListener because accessing
	 * that class is discouraged. Note that the constructors ensure the correct
	 * type.
	 * </p>
	 */
	private Object listener;

	public WrappedEventListener(DragDetectListener listener) {
		this.listener = listener;
		types.add(SWT.DragDetect);
	}

	public WrappedEventListener(KeyListener listener) {
		types.addAll(KEY_LISTENER_TYPES);
		this.listener = listener;
	}

	public WrappedEventListener(MouseListener listener) {
		this.listener = listener;
		types.addAll(MOUSE_LISTENER_TYPES);
	}

	public WrappedEventListener(MouseMoveListener listener) {
		this.listener = listener;
		types.add(SWT.MouseMove);
	}

	public WrappedEventListener(MouseWheelListener listener) {
		this.listener = listener;
		types.add(SWT.MouseWheel);
	}

	public Object getListenerReference() {
		return listener;
	}

	@Override
	public void handleEvent(Event e) {
		switch (e.type) {
		// case SWT.Activate:
		// ((ShellListener) listener).shellActivated(new ShellEvent(e));
		// break;
		// case SWT.Arm:
		// ((ArmListener) listener).widgetArmed(new ArmEvent(e));
		// break;
		// case SWT.Close: {
		// ShellEvent event = new ShellEvent (e);
		// ((ShellListener) listener).shellClosed(event);
		// e.doit = event.doit;
		// break;
		// }
		// case SWT.Collapse:
		// if (listener instanceof TreeListener) {
		// ((TreeListener) listener).treeCollapsed(new TreeEvent(e));
		// } else {
		// ((ExpandListener) listener).itemCollapsed(new ExpandEvent(e));
		// }
		// break;
		// case SWT.Deactivate:
		// ((ShellListener) listener).shellDeactivated(new ShellEvent(e));
		// break;
		// case SWT.Deiconify:
		// ((ShellListener) listener).shellDeiconified(new ShellEvent(e));
		// break;
		// case SWT.DefaultSelection:
		// ((SelectionListener)listener).widgetDefaultSelected(new
		// SelectionEvent(e));
		// break;
		// case SWT.Dispose:
		// ((DisposeListener) listener).widgetDisposed(new DisposeEvent(e));
		// break;
		case SWT.DragDetect:
			((DragDetectListener) listener)
					.dragDetected(new DragDetectEvent(e));
			break;
		// case SWT.Expand:
		// if (listener instanceof TreeListener) {
		// ((TreeListener) listener).treeExpanded(new TreeEvent(e));
		// } else {
		// ((ExpandListener) listener).itemExpanded(new ExpandEvent(e));
		// }
		// break;
		// case SWT.FocusIn:
		// ((FocusListener) listener).focusGained(new FocusEvent(e));
		// break;
		// case SWT.FocusOut:
		// ((FocusListener) listener).focusLost(new FocusEvent(e));
		// break;
		// case SWT.Gesture: {
		// GestureEvent event = new GestureEvent(e);
		// ((GestureListener)listener).gesture(event);
		// e.doit = event.doit;
		// break;
		// }
		// case SWT.Help:
		// ((HelpListener) listener).helpRequested(new HelpEvent(e));
		// break;
		// case SWT.Hide:
		// ((MenuListener) listener).menuHidden(new MenuEvent(e));
		// break;
		// case SWT.Iconify:
		// ((ShellListener) listener).shellIconified(new ShellEvent(e));
		// break;
		case SWT.KeyDown: {
			/* Fields set by Control */
			KeyEvent event = new KeyEvent(e);
			((KeyListener) listener).keyPressed(event);
			e.doit = event.doit;
			break;
		}
		case SWT.KeyUp: {
			/* Fields set by Control */
			KeyEvent event = new KeyEvent(e);
			((KeyListener) listener).keyReleased(event);
			e.doit = event.doit;
			break;
		}
		// case SWT.Modify:
		// ((ModifyListener) listener).modifyText(new ModifyEvent(e));
		// break;
		// case SWT.MenuDetect: {
		// MenuDetectEvent event = new MenuDetectEvent(e);
		// ((MenuDetectListener) listener).menuDetected(event);
		// e.x = event.x;
		// e.y = event.y;
		// e.doit = event.doit;
		// break;
		// }
		case SWT.MouseDown:
			((MouseListener) listener).mouseDown(new MouseEvent(e));
			break;
		case SWT.MouseDoubleClick:
			((MouseListener) listener).mouseDoubleClick(new MouseEvent(e));
			break;
		// case SWT.MouseEnter:
		// ((MouseTrackListener) listener).mouseEnter (new MouseEvent (e));
		// break;
		// case SWT.MouseExit:
		// ((MouseTrackListener) listener).mouseExit (new MouseEvent (e));
		// break;
		// case SWT.MouseHover:
		// ((MouseTrackListener) listener).mouseHover (new MouseEvent (e));
		// break;
		case SWT.MouseMove:
			((MouseMoveListener) listener).mouseMove(new MouseEvent(e));
			return;
		case SWT.MouseWheel:
			((MouseWheelListener) listener).mouseScrolled(new MouseEvent(e));
			return;
		case SWT.MouseUp:
			((MouseListener) listener).mouseUp(new MouseEvent(e));
			break;
		// case SWT.Move:
		// ((ControlListener) listener).controlMoved(new ControlEvent(e));
		// break;
		// case SWT.Paint: {
		// /* Fields set by Control */
		// PaintEvent event = new PaintEvent (e);
		// ((PaintListener) listener).paintControl (event);
		// e.gc = event.gc;
		// break;
		// }
		// case SWT.Resize:
		// ((ControlListener) listener).controlResized(new ControlEvent(e));
		// break;
		// case SWT.Selection: {
		// /* Fields set by Sash */
		// SelectionEvent event = new SelectionEvent (e);
		// ((SelectionListener) listener).widgetSelected (event);
		// e.x = event.x;
		// e.y = event.y;
		// e.doit = event.doit;
		// break;
		// }
		// case SWT.Show:
		// ((MenuListener) listener).menuShown(new MenuEvent(e));
		// break;
		// case SWT.Touch:
		// ((TouchListener)listener).touch(new TouchEvent(e));
		// break;
		// case SWT.Traverse: {
		// /* Fields set by Control */
		// TraverseEvent event = new TraverseEvent (e);
		// ((TraverseListener) listener).keyTraversed (event);
		// e.detail = event.detail;
		// e.doit = event.doit;
		// break;
		// }
		// case SWT.Verify: {
		// /* Fields set by Text, RichText */
		// VerifyEvent event = new VerifyEvent (e);
		// ((VerifyListener) listener).verifyText (event);
		// e.text = event.text;
		// e.doit = event.doit;
		// break;
		// }
		default:
			throw new IllegalArgumentException(
					"The given Event type is not supported!");
		}
	}

	@Override
	public boolean handlesEvent(Event event) {
		for (Integer t : types) {
			if (t == event.type) {
				return true;
			}
		}
		return false;
	}

}
