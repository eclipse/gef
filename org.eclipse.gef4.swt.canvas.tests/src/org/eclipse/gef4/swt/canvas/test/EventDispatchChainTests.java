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

import org.eclipse.gef4.swt.canvas.ev.AbstractEventDispatcher;
import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatchChain;
import org.junit.Test;

public class EventDispatchChainTests {

	@Test
	public void test_phases1() {
		IEventDispatchChain chain = new BasicEventDispatchChain();
		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				assert false; // we should not get here
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				event.consume(); // won't bubble
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e == null;
	}

	@Test
	public void test_phases2() {
		IEventDispatchChain chain = new BasicEventDispatchChain();
		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e != null;
		assert !e.isConsumed();
	}

	@Test
	public void test_phases3_append() {
		IEventDispatchChain chain = new BasicEventDispatchChain();

		final int[] counter = new int[1];
		counter[0] = 0;

		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 4;
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 1;
				return event;
			}
		});

		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 3;
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 2;
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e != null;
		assert !e.isConsumed();
	}

	@Test
	public void test_phases3_prepend() {
		IEventDispatchChain chain = new BasicEventDispatchChain();

		final int[] counter = new int[1];
		counter[0] = 0;

		chain.prepend(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 3;
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 2;
				return event;
			}
		});

		chain.prepend(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 4;
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 1;
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e != null;
		assert !e.isConsumed();
	}

	@Test
	public void test_phases4_append() {
		IEventDispatchChain chain = new BasicEventDispatchChain();

		final int[] counter = new int[1];
		counter[0] = 0;

		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				assert false; // we should not get here
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 1;
				return event;
			}
		});

		chain.append(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 3;
				event.consume();
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 2;
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e == null;
	}

	@Test
	public void test_phases4_prepend() {
		IEventDispatchChain chain = new BasicEventDispatchChain();

		final int[] counter = new int[1];
		counter[0] = 0;

		chain.prepend(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 3;
				event.consume();
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 2;
				return event;
			}
		});

		chain.prepend(new AbstractEventDispatcher() {
			@Override
			public Event dispatchBubblingEvent(Event event) {
				assert false; // we should not get here
				return event;
			}

			@Override
			public Event dispatchCapturingEvent(Event event) {
				counter[0]++;
				assert counter[0] == 1;
				return event;
			}
		});

		Event e = chain.dispatchEvent(new Event(this, null, EventType.ANY));
		assert e == null;
	}

}
