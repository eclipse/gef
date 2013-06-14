package org.eclipse.gef4.swt.canvas.ev;

public class EventType<T extends Event> {

	public static final EventType<Event> ANY = new EventType<Event>();

	private EventType<? super T> superType;
	private String name;

	public EventType() {
	}

	public EventType(EventType<? super T> superType, String name) {
		this.superType = superType;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public EventType<? super T> getSuperType() {
		return superType;
	}

	@Override
	public String toString() {
		return "EventType (" + name + ")";
	}

}
