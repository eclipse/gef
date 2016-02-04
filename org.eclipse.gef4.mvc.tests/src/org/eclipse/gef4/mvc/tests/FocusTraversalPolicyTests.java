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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.FocusTraversalPolicy;
import org.eclipse.gef4.mvc.tests.stubs.Domain;
import org.eclipse.gef4.mvc.tests.stubs.Module;
import org.eclipse.gef4.mvc.tests.stubs.Viewer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;

public class FocusTraversalPolicyTests {

	private static class Cell {
		public String name = "X";
		public List<Cell> children = new ArrayList<>();

		public Cell(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "{" + name + ": " + children + "}";
		}
	}

	private static class CellContentPartFactory implements IContentPartFactory<Object> {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Object, ? extends Object> createContentPart(Object content,
				IBehavior<Object> contextBehavior, Map<Object, Object> contextMap) {
			if (content instanceof Cell) {
				return injector.getInstance(CellPart.class);
			} else {
				throw new IllegalArgumentException(content.getClass().toString());
			}
		}
	}

	private static class CellPart extends AbstractContentPart<Object, Object> {
		@Override
		protected void addChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}

		@Override
		protected Object createVisual() {
			return new Object();
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return ((Cell) getContent()).children;
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}

		@Override
		public boolean isFocusable() {
			return ((Cell) getContent()).name.startsWith("C");
		}

		@Override
		protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}
	}

	public static String NEXT = "NEXT";
	public static String PREV = "PREV";

	private static Injector injector;
	private static Domain domain;

	private static Viewer viewer;

	/**
	 * Creates a complex structure of {@link Cell}s from the given
	 * {@link String} representation.
	 * <p>
	 * Example tree:
	 *
	 * <pre>
	 * Root (R)
	 *  +- Content 0 (C0)
	 *  |   +- Sub Content 0.0 (C00)
	 *  |   +- Sub Content 0.1 (C10)
	 *  |
	 *  +- Content 1 (C1)
	 *  |   +- Sub Content 1.0 (C10)
	 *  |
	 *  +- Content 2 (C2)
	 *      +- Sub Content 2.0 (C20)
	 *      +- Sub Content 2.1 (C21)
	 * </pre>
	 *
	 * Corresponding string representation:
	 *
	 * <pre>
	 * R-C0-C00
	 * R-C0-C01
	 * R-C1-C10
	 * R-C2-C20
	 * R-C2-C21
	 * </pre>
	 *
	 * A mapping from the names (e.g. "R", "C0", "C1", "C2", "C00", etc.) to the
	 * generated {@link Cell}s is put into the given nameToCellMap.
	 *
	 * @param repr
	 *            String that describes the tree of cells to create.
	 * @param nameToCellMap
	 *            Map that is used to store cell name to Cell object mappings.
	 * @return Root {@link Cell}.
	 */
	private static Cell createCellTree(String repr, Map<String, Cell> nameToCellMap) {
		Cell root = new Cell("R");
		nameToCellMap.put("R", root);

		String[] lines = repr.split("\n");
		for (String line : lines) {
			String[] cells = line.split("-");
			Cell c = nameToCellMap.get(cells[0]);
			for (String sc : cells) {
				if (!sc.equals(c.name)) {
					Cell subCell = null;
					if (nameToCellMap.containsKey(sc)) {
						subCell = nameToCellMap.get(sc);
					} else {
						subCell = new Cell(sc);
						nameToCellMap.put(sc, subCell);
					}
					if (!c.children.contains(subCell)) {
						c.children.add(subCell);
					}
					c = subCell;
				}
			}
		}

		return root;
	}

	@BeforeClass
	public static void setUpMVC() {
		injector = Guice.createInjector(Modules.override(new Module() {
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
				// bind ContentBehavior for the CellContentPart
				AdapterMaps.getAdapterMapBinder(binder(), CellPart.class).addBinding(AdapterKey.defaultRole())
						.to(new TypeLiteral<ContentBehavior<Object>>() {
				});
			}
		}).with(new Module() {
			@Override
			protected void configure() {
				// overwrite content part factory
				binder().bind(new TypeLiteral<IContentPartFactory<Object>>() {
				}).toInstance(new CellContentPartFactory());
			}
		}));
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
		Cell root = createCellTree(String.join("\n", "R-C0", "R-C1"), cells);
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
		Cell root = createCellTree(String.join("\n", "R-C-C0-C00", "R-C-C1-C10", "R-C-C2-C20"), cells);
		viewer.getAdapter(ContentModel.class).getContents().setAll(root.children);

		// check initial state
		assertNull(focusModel.getFocus());

		// check first next/prev
		checkFocusTraversal(cells, NEXT, "C", PREV, null, PREV, "C20", NEXT, null);

		// check full cycle (forwards)
		checkFocusTraversal(cells, NEXT, "C", NEXT, "C0", NEXT, "C00", NEXT, "C1", NEXT, "C10", NEXT, "C2", NEXT, "C20",
				NEXT, null);
		// check full cycle (backwards)
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
		Cell root = createCellTree(String.join("\n", "R-V0", "R-C1", "R-V2"), cells);
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
		Cell root = createCellTree(String.join("\n", "R-V0-C0", "R-V1-C1"), cells);
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
