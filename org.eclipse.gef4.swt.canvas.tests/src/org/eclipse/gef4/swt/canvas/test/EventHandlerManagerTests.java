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
package org.eclipse.gef4.swt.canvas.test;

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventHandler;
import org.eclipse.gef4.swt.canvas.ev.IEventTarget;
import org.junit.Test;

public class EventHandlerManagerTests {

	public static class InputEvent extends Event {
		public InputEvent(Object source, IEventTarget target,
				EventType<? extends Event> type) {
			super(source, target, type);
		}
	}

	public static class KeyboardEvent extends InputEvent {
		public KeyboardEvent(Object source, IEventTarget target,
				EventType<? extends Event> type) {
			super(source, target, type);
		}
	}

	public static class MouseEvent extends InputEvent {
		public MouseEvent(Object source, IEventTarget target,
				EventType<? extends Event> type) {
			super(source, target, type);
		}
	}

	public static EventType<InputEvent> INPUT = new EventType<InputEvent>(
			EventType.ANY, "InputEvent");

	public static EventType<MouseEvent> MOUSE = new EventType<MouseEvent>(
			INPUT, "MouseEvent");

	public static EventType<KeyboardEvent> KEYB = new EventType<KeyboardEvent>(
			INPUT, "KeyboardEvent");

	private static SimpleTarget[] create(SimpleTarget parent,
			String... children) {
		SimpleTarget[] nodes = new SimpleTarget[children.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new SimpleTarget(parent);
		}
		return nodes;
	}

	@Test
	public void test_complex() {
		SimpleTarget root = new SimpleTarget(null);
		SimpleTarget[] plane = create(root, "a", "b", "c");
		SimpleTarget[] plane0 = create(plane[0], "d", "e");
		SimpleTarget[] plane2 = create(plane[2], "f", "g", "h", "i");
		SimpleTarget[] plane22 = create(plane2[2], "j", "k");
		// TODO
	}

	@Test
	public void test_root_filter() {
		SimpleTarget root = new SimpleTarget(null);
		root.dispatcher.addEventFilter(EventType.ANY,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						event.consume();
					}
				});
		root.dispatcher.addEventHandler(EventType.ANY,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						assert false;
					}
				});

		SimpleTarget next = new SimpleTarget(root);
		next.dispatcher.addEventFilter(EventType.ANY,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						assert false;
					}
				});
		next.dispatcher.addEventHandler(EventType.ANY,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						assert false;
					}
				});

		Event.fireEvent(next, new Event(EventType.ANY));
	}

	@Test
	public void test_special_handlers_first() {
		SimpleTarget root = new SimpleTarget(null);
		final int[] counter = new int[1];
		counter[0] = 0;
		root.dispatcher.addEventHandler(EventType.ANY,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						counter[0]++;
						assert counter[0] == 3;
					}
				});
		root.dispatcher.addEventHandler(INPUT, new IEventHandler<InputEvent>() {
			@Override
			public void handle(InputEvent event) {
				counter[0]++;
				assert counter[0] == 2;
			}
		});
		root.dispatcher.addEventHandler(MOUSE, new IEventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				counter[0]++;
				assert counter[0] == 1;
			}
		});
		root.dispatcher.addEventHandler(KEYB,
				new IEventHandler<KeyboardEvent>() {
					@Override
					public void handle(KeyboardEvent event) {
						counter[0]++;
						assert counter[0] == 4;
						event.consume(); // won't reach InputEvent-Handlers
					}
				});

		Event.fireEvent(root, new MouseEvent(root, root, MOUSE));
		Event.fireEvent(root, new KeyboardEvent(root, root, KEYB));
	}

}
