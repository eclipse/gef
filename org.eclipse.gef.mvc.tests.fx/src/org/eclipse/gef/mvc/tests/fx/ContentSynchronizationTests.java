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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.ContentBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef.mvc.tests.fx.stubs.Cell;
import org.eclipse.gef.mvc.tests.fx.stubs.CellContentPartFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;

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

	@After
	public void deactivate() throws Throwable {
		ctx.runAndWait(() -> {
			viewer.getContents().clear();
			domain.deactivate();
		});
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
}
