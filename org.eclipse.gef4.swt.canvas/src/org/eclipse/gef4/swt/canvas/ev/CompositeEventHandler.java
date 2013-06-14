package org.eclipse.gef4.swt.canvas.ev;

import java.util.LinkedList;
import java.util.List;

public class CompositeEventHandler<T extends Event> {

	private List<IEventHandler<T>> handlers = new LinkedList<IEventHandler<T>>();
	private List<IEventHandler<T>> filters = new LinkedList<IEventHandler<T>>();

	public void addEventFilter(IEventHandler<T> filter) {
		filters.add(filter);
	}

	public void addEventHandler(IEventHandler<T> handler) {
		handlers.add(handler);
	}

	public void dispatchBubblingEvent(Event event) {
		for (IEventHandler handler : handlers) {
			handler.handle(event);
		}
	}

	public void dispatchCapturingEvent(Event event) {
		for (IEventHandler filter : filters) {
			filter.handle(event);
		}
	}

	public void removeEventFilter(IEventHandler<T> filter) {
		filters.remove(filter);
	}

	public void removeEventHandler(IEventHandler<T> handler) {
		handlers.remove(handler);
	}

}
