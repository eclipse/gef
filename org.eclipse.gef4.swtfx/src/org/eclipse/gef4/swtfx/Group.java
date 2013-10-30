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
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.gef4.swtfx.event.IEventHandler;

public class Group extends AbstractParent {

	private boolean autoSizeChildren = true;

	public Group() {
		addEventFilter(Event.ANY, new IEventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (event.getTarget() == Group.this) {
					event.consume();
				}
			}
		});
	}

	public Group(INode... children) {
		this();
		addChildren(children);
	}

	public boolean isAutoSizeChildren() {
		return autoSizeChildren;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public void layoutChildren() {
		if (isAutoSizeChildren()) {
			super.layoutChildren();
		}
	}

	@Override
	public void resize(double width, double height) {
	}

	public void setAutoSizeChildren(boolean autoSizeChildren) {
		this.autoSizeChildren = autoSizeChildren;
	}

	@Override
	public String toString() {
		return "Group @ " + System.identityHashCode(this)
				+ " (children-count => " + getChildrenUnmodifiable().size() + ")";
	}

}
