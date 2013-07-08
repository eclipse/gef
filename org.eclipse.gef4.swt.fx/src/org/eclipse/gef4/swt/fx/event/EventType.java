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

/**
 * <p>
 * Every {@link Event} is associated with an EventType. An EventType is
 * parameterized with the corresponding Event class. Therefore, EventTypes are
 * hierarchically organized just as Event classes.
 * </p>
 * 
 * <p>
 * The root of the EventType hierarchy is defined as {@link #ROOT}. It does not
 * have a super type. Event#ANY is defined to be exactly the same object.
 * </p>
 * 
 * @author mwienand
 * 
 */
public class EventType<T extends Event> {

	public static final EventType<Event> ROOT = new EventType<Event>("ROOT");

	private EventType<? super T> superType;
	private String name;

	public EventType(EventType<? super T> superType, String name) {
		this.superType = superType;
		this.name = name;
	}

	public EventType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public EventType<? super T> getSuperType() {
		return superType;
	}

	public boolean isa(EventType<?> base) {
		if (base == null) {
			// TODO: think about it: true or false? or exception?
			return false;
		}

		EventType<?> type = this;
		while (type != null) {
			if (type == base) {
				return true;
			}
			type = type.getSuperType();
		}
		return false;
	}

	@Override
	public String toString() {
		return "EventType (" + name + " extends " + getSuperType() + ")";
	}

}
