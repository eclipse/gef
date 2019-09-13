/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Robert Rudi (itemis AG) - adopted exception message for bulk changes
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.ContentBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef.mvc.tests.fx.stubs.Cell;
import org.eclipse.gef.mvc.tests.fx.stubs.CellContentPartFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Tests for the {@link ContentBehavior}.
 *
 * @author wienand
 *
 */
public class ContentSynchronizationTests {

	private static IDomain domain;
	private static IViewer viewer;

	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	@Before
	public void activate() throws Throwable {
		if (domain == null) {
			domain = Guice.createInjector(new MvcFxModule() {

				@Override
				protected void configure() {
					binder().bind(IContentPartFactory.class).to(CellContentPartFactory.class);
					super.configure();
				}
			}).getInstance(IDomain.class);
			viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
			ctx.createScene(viewer.getCanvas(), 100, 100);
		}
		ctx.runAndWait(() -> {
			domain.activate();
		});
	}

	@Test
	public void consecutiveAddUndoOrder() throws Throwable {
		List<Cell> cells = new ArrayList<>();
		setContents(cells);
		for (int i = 0; i < 5; i++) {
			cells.add(new Cell("C" + i));
			setContents(cells);
		}
		List<Cell> copy = new ArrayList<>(cells);
		for (int i = 4; i >= 0; i--) {
			cells.remove(i);
			setContents(cells);
		}
		for (int i = 0; i < 5; i++) {
			cells.add(copy.get(i));
			setContents(cells);
		}
	}

	private List<Cell> createCells(int count) {
		List<Cell> cells = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			cells.add(new Cell("C" + i));
		}
		return cells;
	}

	@After
	public void deactivate() throws Throwable {
		ctx.runAndWait(() -> {
			viewer.getContents().clear();
			domain.deactivate();
		});
	}

	@Test
	public void positionalRemoveUndo() throws Throwable {
		for (int position = 0; position < 3; position++) {
			setContents(Collections.<Cell>emptyList());
			List<Cell> cells = createCells(3);
			setContents(cells);
			List<Cell> spliced = new ArrayList<>();
			for (int i = 0; i < cells.size(); i++) {
				if (i == position) {
					continue;
				}
				spliced.add(cells.get(i));
			}
			setContents(spliced);
			assertNull(viewer.getContentPartMap().get(cells.get(position)));
			setContents(cells);
		}
	}

	@Test
	public void positionalRemoveUndoNested() throws Throwable {
		for (int position = 0; position < 3; position++) {
			setContents(Collections.<Cell>emptyList());
			List<Cell> cells = createCells(3);
			Cell rootCell = new Cell("0", cells.toArray(new Cell[0]));
			setContents(Arrays.asList(rootCell));
			List<Cell> spliced = new ArrayList<>();
			for (int i = 0; i < cells.size(); i++) {
				if (i == position) {
					continue;
				}
				spliced.add(cells.get(i));
			}
			rootCell.children = spliced;
			IContentPart<? extends Node> rootCellPart = viewer.getContentPartMap().get(rootCell);
			ctx.runAndWait(() -> {
				rootCellPart.refreshContentChildren();
			});
			verifyPartsAndOrder(Arrays.asList(rootCell));
			assertNull(viewer.getContentPartMap().get(cells.get(position)));
			rootCell.children = cells;
			ctx.runAndWait(() -> {
				rootCellPart.refreshContentChildren();
			});
			verifyPartsAndOrder(Arrays.asList(rootCell));
		}
	}

	/**
	 * This scenario tests if the synchronization works correctly, when the contents
	 * are replaced with a previously nested content element.
	 */
	@Test
	public void replaceContentsWithNested() throws Throwable {
		// define data
		List<Cell> firstContents = Arrays.asList(new Cell("0", new Cell("1")));
		List<Cell> secondContents = Arrays.asList(firstContents.get(0).children.get(0));

		// no parts in the beginning
		Map<Object, IContentPart<? extends Node>> contentPartMap = viewer.getContentPartMap();
		assertNull(contentPartMap.get(firstContents.get(0)));
		assertNull(contentPartMap.get(secondContents.get(0)));
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(firstContents);
		});
		// both parts created now
		assertNotNull(contentPartMap.get(firstContents.get(0)));
		assertNotNull(contentPartMap.get(secondContents.get(0)));
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(secondContents);
		});
		// first part removed now
		assertNull(contentPartMap.get(firstContents.get(0)));
		assertNotNull(contentPartMap.get(secondContents.get(0)));
	}

	/**
	 * This scenario tests if the synchronization correctly identifies contents for
	 * which parts are already created in other places of the content part
	 * hierarchy.
	 */
	@Test
	public void sameContentAtDifferentPosition() throws Throwable {
		// create tree B
		Cell b = new Cell("1");
		// create tree A with {left = B, right = B}
		List<Cell> sameContents = Arrays.asList(new Cell("0", b, b));

		AtomicReference<IllegalStateException> exceptionRef = new AtomicReference<>();
		ctx.runAndWait(() -> {
			try {
				viewer.getContents().setAll(sameContents);
			} catch (IllegalStateException e) {
				exceptionRef.set(e);
			}
		});

		// check if the exception is thrown when the contents are set
		assertNotNull(exceptionRef.get());
		assertEquals(
				"Located a ContentPart which controls the same (or an equal) content element but is already bound to a viewer. A content element may only be controlled by a single ContentPart.",
				exceptionRef.get().getMessage());
	}

	private void setContents(List<Cell> cells) throws Throwable {
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(cells);
			verifyPartsAndOrder(cells);
		});
	}

	/**
	 * This scenario tests if removing the first element in a content list and
	 * adding it again works correctly.
	 */
	@Test
	public void undoRemove() throws Throwable {
		// define data
		Cell first = new Cell("1");
		Cell second = new Cell("2");
		List<Cell> both = Arrays.asList(first, second);

		// no parts in the beginning
		Map<Object, IContentPart<? extends Node>> contentPartMap = viewer.getContentPartMap();
		assertNull(contentPartMap.get(first));
		assertNull(contentPartMap.get(second));

		// create parts
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(both);
		});
		assertNotNull(contentPartMap.get(first));
		assertNotNull(contentPartMap.get(second));
		assertEquals(contentPartMap.get(first), viewer.getRootPart().getContentPartChildren().get(0));
		assertEquals(contentPartMap.get(second), viewer.getRootPart().getContentPartChildren().get(1));

		// remove first child from parent
		List<Cell> onlySecond = Arrays.asList(second);
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(onlySecond);
		});
		assertNull(contentPartMap.get(first));
		assertNotNull(contentPartMap.get(second));

		// add it again
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(both);
		});
		assertNotNull(contentPartMap.get(first));
		assertNotNull(contentPartMap.get(second));
		assertEquals(contentPartMap.get(first), viewer.getRootPart().getContentPartChildren().get(0));
		assertEquals(contentPartMap.get(second), viewer.getRootPart().getContentPartChildren().get(1));
	}

	/**
	 * This scenario tests if removing the first element in a nested content list
	 * and adding it again works correctly.
	 */
	@Test
	public void undoRemoveNested() throws Throwable {
		// define data
		Cell first = new Cell("1");
		Cell second = new Cell("2");
		Cell container = new Cell("0", first, second);
		List<Cell> fullContents = Arrays.asList(container);

		// no parts in the beginning
		Map<Object, IContentPart<? extends Node>> contentPartMap = viewer.getContentPartMap();
		assertNull(contentPartMap.get(container));
		assertNull(contentPartMap.get(first));
		assertNull(contentPartMap.get(second));

		// create parts
		ctx.runAndWait(() -> {
			viewer.getContents().setAll(fullContents);
		});
		assertNotNull(contentPartMap.get(container));
		assertNotNull(contentPartMap.get(first));
		assertNotNull(contentPartMap.get(second));

		{
			final IContentPart<? extends Node> firstContentPart = viewer.getRootPart().getContentPartChildren().get(0);
			assertEquals(contentPartMap.get(container), firstContentPart);
			assertEquals(contentPartMap.get(first), firstContentPart.getChildrenUnmodifiable().get(0));
			assertEquals(contentPartMap.get(second), firstContentPart.getChildrenUnmodifiable().get(1));

			// remove first child from parent
			ctx.runAndWait(() -> {
				container.children.remove(0);
				firstContentPart.refreshContentChildren();
			});
			assertNotNull(contentPartMap.get(container));
			assertNull(contentPartMap.get(first));
			assertNotNull(contentPartMap.get(second));

			// add it again
			ctx.runAndWait(() -> {
				container.children.add(0, first);
				firstContentPart.refreshContentChildren();
			});
		}

		assertNotNull(contentPartMap.get(container));
		assertNotNull(contentPartMap.get(first));
		assertNotNull(contentPartMap.get(second));
		final IContentPart<? extends Node> firstContentPart = viewer.getRootPart().getContentPartChildren().get(0);
		assertEquals(contentPartMap.get(container), firstContentPart);
		assertEquals(contentPartMap.get(first), firstContentPart.getChildrenUnmodifiable().get(0));
		assertEquals(contentPartMap.get(second), firstContentPart.getChildrenUnmodifiable().get(1));
	}

	private void verifyPartsAndOrder(List<Cell> cells) {
		Map<Object, IContentPart<? extends Node>> cpm = viewer.getContentPartMap();
		IRootPart<? extends Node> root = viewer.getRootPart();

		// test root children
		List<IContentPart<? extends Node>> parts = root.getContentPartChildren();
		for (int i = 0; i < cells.size(); i++) {
			Cell c = cells.get(i);
			IContentPart<? extends Node> cPart = cpm.get(c);
			assertNotNull(cPart);
			assertEquals(cPart, parts.get(i));

			// test cell's children
			ObservableList<IVisualPart<? extends Node>> cParts = cPart.getChildrenUnmodifiable();
			for (int j = 0; j < c.children.size(); j++) {
				Cell cc = c.children.get(j);
				IContentPart<? extends Node> ccPart = cpm.get(cc);
				assertNotNull(ccPart);
				assertEquals(ccPart, cParts.get(j));
			}
		}
	}
}
