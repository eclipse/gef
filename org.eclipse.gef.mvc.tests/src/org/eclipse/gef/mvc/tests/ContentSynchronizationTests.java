/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.mvc.behaviors.ContentBehavior;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.models.ContentModel;
import org.eclipse.gef.mvc.tests.stubs.MvcTestsDomain;
import org.eclipse.gef.mvc.tests.stubs.MvcTestsModule;
import org.eclipse.gef.mvc.tests.stubs.MvcTestsViewer;
import org.eclipse.gef.mvc.tests.stubs.cell.Cell;
import org.eclipse.gef.mvc.viewer.IViewer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Tests for the {@link ContentBehavior}.
 *
 * @author wienand
 *
 */
public class ContentSynchronizationTests {

	private static Injector injector;
	private static MvcTestsDomain domain;
	private static MvcTestsViewer viewer;

	@BeforeClass
	public static void setUpMVC() {
		injector = Guice.createInjector(new MvcTestsModule() {
			@Override
			protected void configure() {
				super.configure();
				binder().bind(new TypeLiteral<IDomain<Object>>() {
				}).to(MvcTestsDomain.class);
				binder().bind(new TypeLiteral<IViewer<Object>>() {
				}).to(MvcTestsViewer.class);
			}
		});
		domain = new MvcTestsDomain();
		injector.injectMembers(domain);
		viewer = domain.getAdapter(MvcTestsViewer.class);

	}

	@Before
	public void activate() {
		domain.activate();
	}

	@After
	public void deactivate() {
		viewer.getAdapter(ContentModel.class).getContents().clear();
		domain.deactivate();
	}

	/**
	 * This scenario tests if the synchronization works correctly, when the
	 * contents are replaced with a previously nested content element.
	 */
	@Test
	public void replaceContentsWithNested() {
		// define data
		List<Cell> firstContents = Arrays.asList(new Cell("0", new Cell("1")));
		List<Cell> secondContents = Arrays.asList(firstContents.get(0).children.get(0));

		// no parts in the beginning
		assertNull(viewer.getContentPartMap().get(firstContents.get(0)));
		assertNull(viewer.getContentPartMap().get(secondContents.get(0)));
		viewer.getAdapter(ContentModel.class).getContents().setAll(firstContents);
		// both parts created now
		assertNotNull(viewer.getContentPartMap().get(firstContents.get(0)));
		assertNotNull(viewer.getContentPartMap().get(secondContents.get(0)));
		viewer.getAdapter(ContentModel.class).getContents().setAll(secondContents);
		// first part removed now
		assertNull(viewer.getContentPartMap().get(firstContents.get(0)));
		assertNotNull(viewer.getContentPartMap().get(secondContents.get(0)));
	}

	/**
	 * This scenario tests if the synchronization correctly identifies contents
	 * for which parts are already created in other places of the content part
	 * hierarchy.
	 */
	@Test
	public void sameContentAtDifferentPosition() {
		// create tree B
		Cell b = new Cell("1");
		// create tree A with {left = B, right = B}
		List<Cell> sameContents = Arrays.asList(new Cell("0", b, b));

		try {
			viewer.getAdapter(ContentModel.class).getContents().setAll(sameContents);
			// check if the exception is thrown when the contents are set
			fail("Exception expected");
		} catch (IllegalStateException e) {
			assertEquals(
					"Located a ContentPart which controls the same (or an equal) content element but is already bound to a parent. A content element may only be controlled by a single ContentPart.",
					e.getMessage());
		}
	}

}
