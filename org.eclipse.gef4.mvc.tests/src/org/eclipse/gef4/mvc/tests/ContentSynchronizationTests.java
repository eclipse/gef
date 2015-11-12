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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMapInjectionSupport;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

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

	public static class Domain extends AbstractDomain<Object> {
		@Override
		public IOperationHistory getOperationHistory() {
			return null;
		}

		@Override
		public IUndoContext getUndoContext() {
			return null;
		}
	}

	public static class FeedbackPartFactory implements IFeedbackPartFactory<Object> {
		@Override
		public List<IFeedbackPart<Object, ? extends Object>> createFeedbackParts(
				List<? extends IVisualPart<Object, ? extends Object>> targets, IBehavior<Object> contextBehavior,
				Map<Object, Object> contextMap) {
			return Collections.emptyList();
		}
	}

	public static class HandlePartFactory implements IHandlePartFactory<Object> {
		@Override
		public List<IHandlePart<Object, ? extends Object>> createHandleParts(
				List<? extends IVisualPart<Object, ? extends Object>> targets, IBehavior<Object> contextBehavior,
				Map<Object, Object> contextMap) {
			return Collections.emptyList();
		}
	}

	public static class Module extends AbstractModule {
		@Override
		protected void configure() {
			install(new AdapterMapInjectionSupport());
			// undo context and operation history (required because of field
			// injections)
			binder().bind(IUndoContext.class).to(UndoContext.class).in(AdaptableScopes.typed(IDomain.class));
			binder().bind(IOperationHistory.class).to(DefaultOperationHistory.class)
					.in(AdaptableScopes.typed(IDomain.class));
			// bind default viewer models
			binder().bind(ContentModel.class).in(AdaptableScopes.typed(IViewer.class));
			// bind factories (required because of field injections)
			binder().bind(new TypeLiteral<IHandlePartFactory<Object>>() {
			}).to(HandlePartFactory.class);
			binder().bind(new TypeLiteral<IFeedbackPartFactory<Object>>() {
			}).to(FeedbackPartFactory.class);
			binder().bind(new TypeLiteral<IContentPartFactory<Object>>() {
			}).toInstance(new ContentPartFactory());
			// bind domain, viewer, and root part
			binder().bind(new TypeLiteral<IDomain<Object>>() {
			}).to(Domain.class);
			binder().bind(new TypeLiteral<IViewer<Object>>() {
			}).to(Viewer.class);
			binder().bind(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
			}).to(RootPart.class);
			// bind Viewer as adapter for Domain
			AdapterMaps.getAdapterMapBinder(binder(), Domain.class).addBinding(AdapterKey.get(IViewer.class))
					.to(new TypeLiteral<IViewer<Object>>() {
					});
			// bind RootPart as viewer adapter
			MapBinder<AdapterKey<?>, Object> viewerAdapterMapBinder = AdapterMaps.getAdapterMapBinder(binder(),
					Viewer.class);
			viewerAdapterMapBinder.addBinding(AdapterKey.get(IRootPart.class))
					.to(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
					});
			viewerAdapterMapBinder.addBinding(AdapterKey.get(ContentModel.class)).to(ContentModel.class);
			viewerAdapterMapBinder.addBinding(AdapterKey.get(HoverModel.class))
					.to(new TypeLiteral<HoverModel<Object>>() {
					});
			viewerAdapterMapBinder.addBinding(AdapterKey.get(SelectionModel.class))
					.to(new TypeLiteral<SelectionModel<Object>>() {
					});
			// bind ContentBehavior for RootPart
			MapBinder<AdapterKey<?>, Object> rootPartAdapterMapBinder = AdapterMaps.getAdapterMapBinder(binder(),
					RootPart.class);
			rootPartAdapterMapBinder.addBinding(AdapterKey.get(ContentBehavior.class))
					.to(new TypeLiteral<ContentBehavior<Object>>() {
					});
			// bind ContentBehavior for the TreeContentPart
			AdapterMaps.getAdapterMapBinder(binder(), TreeContentPart.class)
					.addBinding(AdapterKey.get(ContentBehavior.class)).to(new TypeLiteral<ContentBehavior<Object>>() {
					});
		}
	}

	public static class RootPart extends AbstractRootPart<Object, Object> {
		@Override
		protected void addChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}

		@Override
		protected Object createVisual() {
			return this;
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}

		@Override
		protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
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
		protected void doAddContentChild(Object contentChild, int index) {
		}

		@Override
		protected void doAttachToContentAnchorage(Object contentAnchorage, String role) {
		}

		@Override
		protected void doDetachFromContentAnchorage(Object contentAnchorage, String role) {
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}

		@Override
		protected void doRemoveContentChild(Object contentChild, int index) {
		}

		@Override
		public Tree getContent() {
			return (Tree) super.getContent();
		}

		@Override
		public SetMultimap<? extends Object, String> getContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		public List<? extends Object> getContentChildren() {
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
		protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
		}
	}

	public static class Viewer extends AbstractViewer<Object> {
		@Override
		public void reveal(IVisualPart<Object, ? extends Object> visualPart) {
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
		viewer = domain.getAdapter(IViewer.class);
	}

	@Before
	public void activate() {
		domain.activate();
	}

	@After
	public void deactivate() {
		viewer.getAdapter(ContentModel.class).setContents(Collections.emptyList());
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
		viewer.getAdapter(ContentModel.class).setContents(firstContents);
		// both parts created now
		assertNotNull(viewer.getContentPartMap().get(firstContents.get(0)));
		assertNotNull(viewer.getContentPartMap().get(secondContents.get(0)));
		viewer.getAdapter(ContentModel.class).setContents(secondContents);
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

		// check if the exception is thrown when the contents are set
		boolean thrown = false;
		String expectedMessage = "Located a ContentPart which controls the same (or an equal) content element but is already bound to a parent. A content element may only be controlled by a single ContentPart.";
		String realMessage = "";
		try {
			viewer.getAdapter(ContentModel.class).setContents(sameContents);
		} catch (IllegalStateException x) {
			thrown = true;
			realMessage = x.getMessage();
		}
		assertTrue(thrown);
		assertEquals(expectedMessage, realMessage);
	}

}
