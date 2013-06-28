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
package org.eclipse.gef4.swt.canvas.ev;

public class EventType<T extends Event> {

	static final EventType<Event> ROOT = new EventType<Event>("ROOT");

	private EventType<? super T> superType;
	private String name;

	public EventType(EventType<? super T> superType, String name) {
		this.superType = superType;
		this.name = name;
	}

	public EventType(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	public String getName() {
		return name;
	}

	public EventType<? super T> getSuperType() {
		return superType;
	}

	public boolean isDescendantOf(EventType<?> type) {
		while (type != null) {
			if (type.getName().equals(getName())) {
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
