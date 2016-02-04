/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.policies.FocusTraversalPolicy;
import org.eclipse.gef4.mvc.tests.stubs.Cell;
import org.eclipse.gef4.mvc.tests.stubs.Domain;
import org.eclipse.gef4.mvc.tests.stubs.Module;
import org.eclipse.gef4.mvc.tests.stubs.Viewer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class FocusTraversalPolicyTests {

	public static String NEXT = "NEXT";
	public static String PREV = "PREV";

	private static Injector injector;
	private static Domain domain;

	private static Viewer viewer;

	@BeforeClass
	public static void setUpMVC() {
		injector = Guice.createInjector(new Module() {
			@Override
			protected void configure() {
				super.configure();
				// bind FocusModel
				AdapterMaps.getAdapterMapBinder(binder(), Viewer.class).addBinding(AdapterKey.defaultRole())
						.to(new TypeLiteral<FocusModel<Object>>() {
				});
				// bind FocusTraversalPolicy
				AdapterMaps.getAdapterMapBinder(binder(), AbstractRootPart.class).addBinding(AdapterKey.defaultRole())
						.to(new TypeLiteral<FocusTraversalPolicy<Object>>() {
				});
			}
		});
		domain = new Domain();
		injector.injectMembers(domain);
		viewer = domain.getAdapter(Viewer.class);

		// ensure exceptions are not caught
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof RuntimeException) {
					throw ((RuntimeException) e);
				}
				throw new RuntimeException(e);
			}
		});
	}

	private FocusModel<Object> focusModel;

	private FocusTraversalPolicy<Object> traversePolicy;

	@SuppressWarnings("serial")
	@Before
	public void activate() {
		domain.activate();
		focusModel = viewer.getAdapter(new TypeToken<FocusModel<Object>>() {
		});
		traversePolicy = viewer.getRootPart().getAdapter(new TypeToken<FocusTraversalPolicy<Object>>() {
		});
	}

	/**
	 * Performs the given focus traversal actions and checks that the specified
	 * cells are focused afterwards. A cell is only focusable if its name starts
	 * with "C".
	 *
	 * @param cells
	 *            Mapping from cell names to {@link Cell} objects.
	 * @param objects
	 *            Alternating: Focus traversal actions ({@link #NEXT} or
	 *            {@link #PREV}) and cell names ({@link Cell#name}).
	 */
	protected void checkFocusTraversal(Map<String, Cell> cells, String... objects) {
		if (objects == null) {
			throw new IllegalArgumentException("objects may not be null");
		}
		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("even number of objects expected");
		}
		for (int i = 0; i < objects.length; i++) {
			if (i % 2 == 0) {
				String fta = objects[i];
				traversePolicy.init();
				if (NEXT.equals(fta)) {
					traversePolicy.focusNext();
				} else if (PREV.equals(fta)) {
					traversePolicy.focusPrevious();
				} else {
					throw new IllegalArgumentException("focus traversal action (NEXT or PREV) expected");
				}
				try {
					domain.execute(traversePolicy.commit());
				} catch (ExecutionException e) {
					fail();
				}
			} else {
				if (objects[i] == null) {
					assertNull("performed " + Arrays.asList(objects).subList(0, i).toString()
							+ ", expected null, but got " + focusModel.getFocus(), focusModel.getFocus());
				} else {
					assertNotNull("performed " + Arrays.asList(objects).subList(0, i).toString() + ", expected cell "
							+ objects[i] + ", but got null", focusModel.getFocus());
					Cell cell = cells.get(objects[i]);
					assertSame("performed " + Arrays.asList(objects).subList(0, i).toString(), cell,
							focusModel.getFocus().getContent());
				}
			}
		}
	}

	@After
	public void deactivate() {
		viewer.getAdapter(ContentModel.class).getContents().setAll(Collections.emptyList());
		domain.deactivate();
	}

	/**
	 * All parts are focusable, cell tree:
	 *
	 * <pre>
	 * R-C0
	 * R-C1
	 * </pre>
	 *
	 * Check initial state:
	 *
	 * <ol>
	 * <li>No focus part.
	 * </ol>
	 *
	 * Check focus next/previous:
	 *
	 * <ol>
	 * <li>Focus Next => C0 is focused.
	 * <li>Focus Previous => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * </ol>
	 *
	 * Check focus cycle (next/previous):
	 *
	 * <ol>
	 * <li>Focus Next => C0 is focused.
	 * <li>Focus Next => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Previous => C0 is focused.
	 * <li>Focus Previous => No focus part.
	 * </ol>
	 */
	@Test
	public void test_RC0_RC1() {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-C0", "R-C1"), cells);
		viewer.getAdapter(ContentModel.class).getContents().setAll(root.children);

		// check initial state
		assertNull(focusModel.getFocus());

		// check first next/prev
		checkFocusTraversal(cells, NEXT, "C0", PREV, null, PREV, "C1", NEXT, null);

		// check full cycle (forwards)
		checkFocusTraversal(cells, NEXT, "C0", NEXT, "C1", NEXT, null);
		// check full cycle (backwards)
		checkFocusTraversal(cells, PREV, "C1", PREV, "C0", PREV, null);
	}

	/**
	 * All parts are focusable, cell tree:
	 *
	 * <pre>
	 * R-C-C0-C00
	 * R-C-C1-C10
	 * R-C-C2-C20
	 * </pre>
	 *
	 * Check initial state:
	 *
	 * <ol>
	 * <li>No focus part.
	 * </ol>
	 *
	 * Check focus next/previous:
	 *
	 * <ol>
	 * <li>Focus Next => C is focused.
	 * <li>Focus Previous => No focus part.
	 * <li>Focus Previous => C20 is focused.
	 * <li>Focus Next => No focus part.
	 * </ol>
	 *
	 * Check focus cycle (next/previous):
	 *
	 * <ol>
	 * <li>Focus Next => C is focused.
	 * <li>Focus Next => C0 is focused.
	 * <li>Focus Next => C00 is focused.
	 * <li>Focus Next => C1 is focused.
	 * <li>Focus Next => C10 is focused.
	 * <li>Focus Next => C2 is focused.
	 * <li>Focus Next => C20 is focused.
	 * <li>Focus Next => No focus part.
	 * <li>Focus Previous => C20 is focused.
	 * <li>Focus Previous => C2 is focused.
	 * <li>Focus Previous => C10 is focused.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Previous => C00 is focused.
	 * <li>Focus Previous => C0 is focused.
	 * <li>Focus Previous => C is focused.
	 * <li>Focus Previous => No focus part.
	 * </ol>
	 */
	@Test
	public void test_RCC0C00_RCC1C10_RCC2C20() {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-C-C0-C00", "R-C-C1-C10", "R-C-C2-C20"), cells);
		viewer.getAdapter(ContentModel.class).getContents().setAll(root.children);

		// check initial state
		assertNull(focusModel.getFocus());

		// check first next/prev
		checkFocusTraversal(cells, NEXT, "C", PREV, null, PREV, "C20", NEXT, null);

		// check full cycle (forwards)
		checkFocusTraversal(cells, NEXT, "C", NEXT, "C0", NEXT, "C00", NEXT, "C1", NEXT, "C10", NEXT, "C2", NEXT, "C20",
				NEXT, null);
		// check full cycle (backwards)u
		checkFocusTraversal(cells, PREV, "C20", PREV, "C2", PREV, "C10", PREV, "C1", PREV, "C00", PREV, "C0", PREV, "C",
				PREV, null);
	}

	/**
	 * First and last parts are not focusable, only the part in the middle is,
	 * cell tree:
	 *
	 * <pre>
	 * R-V0
	 * R-C1
	 * R-V2
	 * </pre>
	 *
	 * Check initial state:
	 *
	 * <ol>
	 * <li>No focus part.
	 * </ol>
	 *
	 * Check focus next/previous:
	 *
	 * <ol>
	 * <li>Focus Next => C1 is focused.
	 * <li>Focus Previous => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * </ol>
	 *
	 * Check focus cycle (next/previous):
	 *
	 * <ol>
	 * <li>Focus Next => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Previous => No focus part.
	 * </ol>
	 */
	@Test
	public void test_RV0_RC1_RV2() {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-V0", "R-C1", "R-V2"), cells);
		viewer.getAdapter(ContentModel.class).getContents().setAll(root.children);

		// check initial state
		assertNull(focusModel.getFocus());

		// check first next/prev
		checkFocusTraversal(cells, NEXT, "C1", PREV, null, PREV, "C1", NEXT, null);

		// check full cycle (forwards)
		checkFocusTraversal(cells, NEXT, "C1", NEXT, null);
		// check full cycle (backwards)
		checkFocusTraversal(cells, PREV, "C1", PREV, null);
	}

	/**
	 * Intermediate parts are not focusable, cell tree:
	 *
	 * <pre>
	 * R-V0-C0
	 * R-V1-C1
	 * </pre>
	 *
	 * Check initial state:
	 *
	 * <ol>
	 * <li>No focus part.
	 * </ol>
	 *
	 * Check focus next/previous:
	 *
	 * <ol>
	 * <li>Focus Next => C0 is focused.
	 * <li>Focus Previous => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * </ol>
	 *
	 * Check focus cycle (next/previous):
	 *
	 * <ol>
	 * <li>Focus Next => C0 is focused.
	 * <li>Focus Next => C1 is focused.
	 * <li>Focus Next => No focus part.
	 * <li>Focus Previous => C1 is focused.
	 * <li>Focus Previous => C0 is focused.
	 * <li>Focus Previous => No focus part.
	 * </ol>
	 */
	@Test
	public void test_RV0C0_RV1C1() {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-V0-C0", "R-V1-C1"), cells);
		viewer.getAdapter(ContentModel.class).getContents().setAll(root.children);

		// check initial state
		assertNull(focusModel.getFocus());

		// check first next/prev
		checkFocusTraversal(cells, NEXT, "C0", PREV, null, PREV, "C1", NEXT, null);

		// check full cycle (forwards)
		checkFocusTraversal(cells, NEXT, "C0", NEXT, "C1", NEXT, null);
		// check full cycle (backwards)
		checkFocusTraversal(cells, PREV, "C1", PREV, "C0", PREV, null);
	}

}
