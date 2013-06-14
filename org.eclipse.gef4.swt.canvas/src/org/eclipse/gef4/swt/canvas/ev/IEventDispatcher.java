package org.eclipse.gef4.swt.canvas.ev;

public interface IEventDispatcher {

	Event dispatchEvent(Event event, IEventDispatchChain tail);

}
