/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.policies.FocusTraversalPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef.mvc.tests.fx.stubs.Cell;
import org.eclipse.gef.mvc.tests.fx.stubs.CellContentPartFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Node;

public class FocusTraversalPolicyTests {

	public static String NEXT = "NEXT";
	public static String PREV = "PREV";

	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	private IDomain domain;
	private IViewer viewer;
	private FocusModel focusModel;
	private FocusTraversalPolicy traversePolicy;

	@Before
	public void activate() throws Throwable {
		if (domain == null) {
			Injector injector = Guice.createInjector(new MvcFxModule() {
				@Override
				protected void bindIDomainAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
					bindContentIViewerAsIDomainAdapter(adapterMapBinder);
				}

				@Override
				protected void configure() {
					super.configure();

					binder().bind(IContentPartFactory.class).to(CellContentPartFactory.class);

					// bind FocusModel
					// AdapterMaps.getAdapterMapBinder(binder(),
					// InfiniteCanvasViewer.class,
					// HistoricizingDomain.CONTENT_VIEWER_ROLE)
					// .addBinding(AdapterKey.defaultRole()).to(FocusModel.class);

					// // bind FocusTraversalPolicy
					// AdapterMaps.getAdapterMapBinder(binder(),
					// AbstractFXRootPart.class)
					// .addBinding(AdapterKey.defaultRole()).to(FocusTraversalPolicy.class);
				}
			});
			domain = injector.getInstance(IDomain.class);
			viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
			ctx.createScene(viewer.getCanvas(), 100, 100);

			ctx.runAndWait(() -> {
				focusModel = viewer.getAdapter(FocusModel.class);
				IRootPart<? extends Node> rootPart = viewer.getRootPart();
				traversePolicy = rootPart.getAdapter(FocusTraversalPolicy.class);
			});
		}
		assertNotNull(focusModel);
		assertNotNull(traversePolicy);
		ctx.runAndWait(() -> {
			domain.activate();
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
	protected void checkFocusTraversal(Map<String, Cell> cells, String... objects) throws Throwable {
		if (objects == null) {
			throw new IllegalArgumentException("objects may not be null");
		}
		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("even number of objects expected");
		}
		for (int i = 0; i < objects.length; i++) {
			if (i % 2 == 0) {
				String fta = objects[i];
				ctx.runAndWait(() -> {
					traversePolicy.init();
					if (NEXT.equals(fta)) {
						traversePolicy.focusNext();
					} else if (PREV.equals(fta)) {
						traversePolicy.focusPrevious();
					} else {
						throw new IllegalArgumentException("focus traversal action (NEXT or PREV) expected");
					}
					try {
						domain.execute(traversePolicy.commit(), new NullProgressMonitor());
					} catch (ExecutionException e) {
						e.printStackTrace();
						fail(e.getMessage());
					}
				});
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
	public void deactivate() throws Throwable {
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(Collections.emptyList());
			domain.deactivate();
		});
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
	public void test_RC0_RC1() throws Throwable {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-C0", "R-C1"), cells);

		ctx.runAndWait(() -> {
			viewer.getContents().setAll(root.children);
		});

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
	public void test_RCC0C00_RCC1C10_RCC2C20() throws Throwable {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-C-C0-C00", "R-C-C1-C10", "R-C-C2-C20"), cells);

		ctx.runAndWait(() -> {
			viewer.getContents().setAll(root.children);
		});

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
	public void test_RV0_RC1_RV2() throws Throwable {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-V0", "R-C1", "R-V2"), cells);

		ctx.runAndWait(() -> {
			viewer.getContents().setAll(root.children);
		});

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
	public void test_RV0C0_RV1C1() throws Throwable {
		Map<String, Cell> cells = new HashMap<>();
		Cell root = Cell.createCellTree(String.join("\n", "R-V0-C0", "R-V1-C1"), cells);

		ctx.runAndWait(() -> {
			viewer.getContents().setAll(root.children);
		});

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
