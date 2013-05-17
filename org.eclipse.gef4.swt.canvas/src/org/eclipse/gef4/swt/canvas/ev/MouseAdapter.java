package org.eclipse.gef4.swt.canvas.ev;

import org.eclipse.gef4.swt.canvas.IEventListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

abstract public class MouseAdapter implements IEventListener {

	public MouseAdapter() {
	}

	@Override
	public void handleEvent(Object event) {
		assert event instanceof Event;
		Event e = (Event) event;
		switch (e.type) {
		case SWT.MouseDoubleClick:
			onMouseDoubleClick(e);
			break;
		case SWT.MouseDown:
			onMouseDown(e);
			break;
		case SWT.MouseHorizontalWheel:
			onMouseHorizontalWheel(e);
			break;
		case SWT.MouseMove:
			onMouseMove(e);
			break;
		case SWT.MouseUp:
			onMouseUp(e);
			break;
		case SWT.MouseVerticalWheel:
			onMouseVerticalWheel(e);
			break;
		default:
			throw new IllegalStateException(
					"This MouseAdapter does not handle this event type: '"
							+ e.type + "'");
		}
	}

	@Override
	public boolean handlesEvent(Object event) {
		if (event instanceof Event) {
			Event e = (Event) event;
			return e.type == SWT.MouseDoubleClick || e.type == SWT.MouseDown
					|| e.type == SWT.MouseHorizontalWheel
					|| e.type == SWT.MouseMove || e.type == SWT.MouseUp
					|| e.type == SWT.MouseVerticalWheel;
		}
		return false;
	}

	protected void onMouseDoubleClick(Object event) {
	}

	protected void onMouseDown(Event e) {
	}

	protected void onMouseHorizontalWheel(Event e) {
	}

	protected void onMouseMove(Event e) {
	}

	protected void onMouseUp(Event e) {
	}

	protected void onMouseVerticalWheel(Event e) {
	}

}
