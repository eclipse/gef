package org.eclipse.gef4.swt.canvas.ev;

public interface IEventHandler<T extends Event> {

	void handle(T event);

}
