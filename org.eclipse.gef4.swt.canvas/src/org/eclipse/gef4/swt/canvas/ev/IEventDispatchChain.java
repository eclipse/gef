package org.eclipse.gef4.swt.canvas.ev;

public interface IEventDispatchChain {

	IEventDispatchChain append(IEventDispatchChain edc);

	Event dispatchEvent(Event event);

	IEventDispatchChain prepend(IEventDispatchChain edc);

}
