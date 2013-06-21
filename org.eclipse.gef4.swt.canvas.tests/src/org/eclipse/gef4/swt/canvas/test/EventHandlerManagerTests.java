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
			EventType.ROOT, "InputEvent");

	public static EventType<MouseEvent> MOUSE = new EventType<MouseEvent>(
			INPUT, "MouseEvent");

	public static EventType<KeyboardEvent> KEYB = new EventType<KeyboardEvent>(
			INPUT, "KeyEvent");

	private static SimpleTarget[] create(SimpleTarget parent,
			String... children) {
		SimpleTarget[] nodes = new SimpleTarget[children.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new SimpleTarget(parent);
		}
		return nodes;
	}

	private <T extends Event> IEventHandler<T> createCounterHandler(
			final EventType<T> type, final int[] counter,
			final int expectation, final boolean consume) {
		return new IEventHandler<T>() {
			@Override
			public void handle(T event) {
				counter[0]++;
				assert counter[0] == expectation;
				if (consume) {
					event.consume();
				}
			}
		};
	}

	private <T extends Event> IEventHandler<T> createFailHandler(
			final EventType<T> type) {
		return new IEventHandler<T>() {
			@Override
			public void handle(T event) {
				assert false;
			}
		};
	}

	@Test
	public void test_complex() {
		SimpleTarget root = new SimpleTarget(null);
		SimpleTarget[] plane = create(root, "a", "b", "c");
		SimpleTarget[] plane0 = create(plane[0], "d", "e");
		SimpleTarget[] plane2 = create(plane[2], "f", "g", "h", "i");
		SimpleTarget[] plane22 = create(plane2[2], "j", "k");

		int[] counter = new int[1];
		counter[0] = 0;

		root.dispatcher.addEventFilter(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 1, false));
		plane[2].dispatcher.addEventFilter(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 2, false));
		plane2[2].dispatcher.addEventFilter(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 3, false));
		plane22[0].dispatcher.addEventFilter(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 4, false));

		plane22[0].dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 5, false));
		plane2[2].dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 6, false));
		plane[2].dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 7, false));
		root.dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 8, false));

		Event.fireEvent(plane22[0], new Event("", plane22[0], EventType.ROOT));

		// do it twice, just to make sure
		counter[0] = 0;
		Event.fireEvent(plane22[0], new Event("", plane22[0], EventType.ROOT));

		root.dispatcher.addEventFilter(INPUT,
				createCounterHandler(INPUT, counter, 9, false));
		plane[0].dispatcher.addEventFilter(INPUT,
				createCounterHandler(INPUT, counter, 10, false));
		plane0[0].dispatcher.addEventFilter(INPUT,
				createCounterHandler(INPUT, counter, 11, false));

		plane0[0].dispatcher.addEventHandler(INPUT,
				createCounterHandler(INPUT, counter, 12, false));
		plane[0].dispatcher.addEventHandler(INPUT,
				createCounterHandler(INPUT, counter, 13, true)); // consuming
		root.dispatcher.addEventHandler(INPUT, createFailHandler(INPUT));

		Event.fireEvent(plane0[0], new InputEvent("", plane0[0], INPUT));
	}

	@Test
	public void test_root_filter() {
		SimpleTarget root = new SimpleTarget(null);
		root.dispatcher.addEventFilter(EventType.ROOT,
				new IEventHandler<Event>() {
					@Override
					public void handle(Event event) {
						event.consume();
					}
				});
		root.dispatcher.addEventHandler(EventType.ROOT,
				createFailHandler(EventType.ROOT));

		SimpleTarget next = new SimpleTarget(root);
		next.dispatcher.addEventFilter(EventType.ROOT,
				createFailHandler(EventType.ROOT));
		next.dispatcher.addEventHandler(EventType.ROOT,
				createFailHandler(EventType.ROOT));

		Event.fireEvent(next, new Event(EventType.ROOT));
	}

	@Test
	public void test_special_handlers_first_1() {
		SimpleTarget root = new SimpleTarget(null);

		final int[] counter = new int[1];
		counter[0] = 0;

		root.dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 3, false));
		root.dispatcher.addEventHandler(INPUT,
				createCounterHandler(INPUT, counter, 2, false));
		root.dispatcher.addEventHandler(MOUSE,
				createCounterHandler(MOUSE, counter, 1, false));
		root.dispatcher.addEventHandler(KEYB,
				createCounterHandler(KEYB, counter, 4, true));

		Event.fireEvent(root, new MouseEvent(root, root, MOUSE));
		Event.fireEvent(root, new KeyboardEvent(root, root, KEYB));
	}

	@Test
	public void test_special_handlers_first_2() {
		SimpleTarget root = new SimpleTarget(null);

		final int[] counter = new int[1];
		counter[0] = 0;

		root.dispatcher.addEventHandler(KEYB,
				createCounterHandler(KEYB, counter, 4, true));
		root.dispatcher.addEventHandler(EventType.ROOT,
				createCounterHandler(EventType.ROOT, counter, 3, false));
		root.dispatcher.addEventHandler(MOUSE,
				createCounterHandler(MOUSE, counter, 1, false));
		root.dispatcher.addEventHandler(INPUT,
				createCounterHandler(INPUT, counter, 2, false));

		Event.fireEvent(root, new MouseEvent(root, root, MOUSE));
		Event.fireEvent(root, new KeyboardEvent(root, root, KEYB));
	}

}
