/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef4.mvc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tests.stubs.Domain;
import org.eclipse.gef4.mvc.tests.stubs.Module;
import org.eclipse.gef4.mvc.tests.stubs.Viewer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Tests for the {@link ContentBehavior}.
 *
 * @author wienand
 *
 */
public class ContentSynchronizationTests {

	public static class ContentPartFactory implements IContentPartFactory<Object> {
		@Inject
		private Provider<TreeContentPart> treeContentPartProvider;

		@Override
		public IContentPart<Object, ? extends Object> createContentPart(Object content,
				IBehavior<Object> contextBehavior, Map<Object, Object> contextMap) {
			if (content instanceof Tree) {
				return treeContentPartProvider.get();
			}
			throw new IllegalArgumentException("Unknown content type: " + content);
		}
	}

	public static class Tree {
		public int data;
		public Tree left;
		public Tree right;

		public Tree(int data) {
			this(data, null, null);
		}

		public Tree(int data, Tree left) {
			this(data, left, null);
		}

		public Tree(int data, Tree left, Tree right) {
			this.data = data;
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Tree) {
				Tree o = (Tree) obj;
				return data == o.data && left == o.left && right == o.right;
			}
			return false;
		}
	}

	public static class TreeContentPart extends AbstractContentPart<Object, Object> {
		@Override
		protected void addChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}

		@Override
		protected Object createVisual() {
			return this;
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			Tree tree = getContent();
			if (tree.left == null) {
				if (tree.right == null) {
					return Collections.emptyList();
				}
				return Arrays.asList(tree.right);
			}
			if (tree.right == null) {
				return Arrays.asList(tree.left);
			}
			return Arrays.asList(tree.left, tree.right);
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}

		@Override
		public Tree getContent() {
			return (Tree) super.getContent();
		}

		@Override
		protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}
	}

	private static Injector injector;
	private static Domain domain;
	private static Viewer viewer;

	@BeforeClass
	public static void setUpMVC() {
		injector = Guice.createInjector(new Module());
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

	@Before
	public void activate() {
		domain.activate();
	}

	@After
	public void deactivate() {
		viewer.getAdapter(ContentModel.class).getContents().setAll(Collections.emptyList());
		domain.deactivate();
	}

	/**
	 * This scenario tests if the synchronization works correctly, when the
	 * contents are replaced with a previously nested content element.
	 */
	@Test
	public void replaceContentsWithNested() {
		// define data
		List<Tree> firstContents = Arrays.asList(new Tree(0, new Tree(1)));
		List<Tree> secondContents = Arrays.asList(firstContents.get(0).left);

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
		Tree b = new Tree(1);
		// create tree A with {left = B, right = B}
		List<Tree> sameContents = Arrays.asList(new Tree(0, b, b));

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
