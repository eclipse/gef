package org.eclipse.gef4.swt.canvas.ev;

public abstract class BasicEventDispatcher implements IEventDispatcher {

	// private BasicEventDispatcher nextDispatcher;
	// private BasicEventDispatcher prevDispatcher;

	// public Event dispatchBubblingEvent(Event event) {
	// return event;
	// }
	//
	// public Event dispatchCapturingEvent(Event event) {
	// return event;
	// }

	public abstract Event dispatchBubblingEvent(Event event);

	public abstract Event dispatchCapturingEvent(Event event);

	@Override
	public Event dispatchEvent(Event event, IEventDispatchChain tail) {
		event = dispatchCapturingEvent(event);

		// TODO: is event == null even possible?
		if (event != null && !event.isConsumed()) {
			// forward the event to the rest of the chain
			event = tail.dispatchEvent(event);

			// TODO: is event == null even possible?
			if (event != null && !event.isConsumed()) {
				event = dispatchBubblingEvent(event);
			}
		}

		// TODO: is event == null even possible?
		return event == null || event.isConsumed() ? null : event;
	}

	// public BasicEventDispatcher getNextDispatcher() {
	// return nextDispatcher;
	// }
	//
	// public BasicEventDispatcher getPreviousDispatcher() {
	// return prevDispatcher;
	// }
	//
	// public void insertNextDispatcher(BasicEventDispatcher nextDispatcher) {
	// if (this.nextDispatcher != null) {
	// this.nextDispatcher.prevDispatcher = nextDispatcher;
	// }
	// nextDispatcher.nextDispatcher = this.nextDispatcher;
	// nextDispatcher.prevDispatcher = this;
	// this.nextDispatcher = nextDispatcher;
	// }

}
