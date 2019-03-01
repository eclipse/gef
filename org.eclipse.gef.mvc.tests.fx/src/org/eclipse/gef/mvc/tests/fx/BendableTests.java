/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.ResizeTransformSelectedOnHandleDragHandler;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule.Modifiers;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.SetMultimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

public class BendableTests {

	private static class Bendable extends AbstractContentPart<Connection> implements IBendableContentPart<Connection> {

		private List<BendPoint> contentBendPoints;

		@SuppressWarnings("unused")
		public Bendable() {
		}

		public Bendable(Point... points) {
			contentBendPoints = new ArrayList<>();
			for (Point p : points) {
				contentBendPoints.add(new BendPoint(p));
			}
		}

		@Override
		protected Connection doCreateVisual() {
			return new Connection();
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return CollectionUtils.emptySetMultimap();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Connection visual) {
			setVisualBendPoints(getContentBendPoints());
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint> getContentBendPoints() {
			if (contentBendPoints == null) {
				contentBendPoints = new ArrayList<>();
				for (Point p : (List<Point>) getContent()) {
					contentBendPoints.add(new BendPoint(p));
				}
			}
			return contentBendPoints;
		}

		@Override
		public void setContentBendPoints(List<org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint> bendPoints) {
			contentBendPoints = bendPoints;
			refreshVisual();
		}
	}

	static class ContentPartFactory implements IContentPartFactory {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
			if (content instanceof List) {
				return injector.getInstance(Bendable.class);
			}
			throw new IllegalArgumentException("Unknown content: " + content + ".");
		}
	}

	@Inject
	private IDomain domain;

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	public Bendable[] createBendablesForResize() throws Throwable {
		// create injector
		Injector injector = Guice.createInjector(new MvcFxModule() {
			@Override
			protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				super.bindAbstractContentPartAdapters(adapterMapBinder);
				adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);
				adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizePolicy.class);
				adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickHandler.class);
			}

			protected void bindIContentPartFactory() {
				binder().bind(IContentPartFactory.class).to(ContentPartFactory.class);
			}

			private void bindSquareSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				adapterMapBinder.addBinding(AdapterKey.defaultRole())
						.to(ResizeTransformSelectedOnHandleDragHandler.class);
			}

			@Override
			protected void configure() {
				super.configure();
				bindIContentPartFactory();
				bindSquareSegmentHandlePartAdapters(
						AdapterMaps.getAdapterMapBinder(binder(), SquareSegmentHandlePart.class));
			}
		});
		injector.injectMembers(this);

		// get viewer
		IViewer viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));

		// hook viewer to scene
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// specify contents
		List<List<Point>> contents = new ArrayList<>();
		List<Point> c0 = new ArrayList<>();
		c0.add(new Point(40, 55));
		c0.add(new Point(50, 50));
		c0.add(new Point(60, 55));
		contents.add(c0);
		List<Point> c1 = new ArrayList<>();
		c1.add(new Point(30, 75));
		c1.add(new Point(40, 70));
		c1.add(new Point(50, 75));
		contents.add(c1);

		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				// activate domain
				domain.activate();
				// set contents
				viewer.getContents().setAll(contents);
			}
		});

		// return content parts for the content objects
		final Bendable p0 = (Bendable) viewer.getContentPartMap().get(contents.get(0));
		final Bendable p1 = (Bendable) viewer.getContentPartMap().get(contents.get(1));
		return new Bendable[] { p0, p1 };
	}

	@Test
	public void test_resize() {
		Point start = new Point(0, 0);
		Point end = new Point(100, 50);
		Bendable bendable = new Bendable(start, end);
		// check content bend points
		List<BendPoint> contentBendPoints = bendable.getContentBendPoints();
		assertEquals(start, contentBendPoints.get(0).getPosition());
		assertEquals(end, contentBendPoints.get(1).getPosition());
		// check size
		Dimension contentSize = bendable.getContentSize();
		assertEquals(new Rectangle(start, end).getSize(), contentSize);
		// check transform (should equal translation to offset)
		Point contentOffset = new Point(bendable.getContentTransform().getTx(), bendable.getContentTransform().getTy());
		assertEquals(start, contentOffset);
		// check resize
		Point newEnd = end.getTranslated(0, 50);
		Rectangle newBounds = new Rectangle(start, newEnd);
		bendable.setContentSize(newBounds.getSize());
		assertEquals(newBounds.getSize(), bendable.getContentSize());
		// check content offset did not change
		contentOffset = new Point(bendable.getContentTransform().getTx(), bendable.getContentTransform().getTy());
		assertEquals(start, contentOffset);
	}

	@Test
	public void test_resizeConnection() throws Throwable {
		Bendable[] bendableParts = createBendablesForResize();
		Bendable p0 = bendableParts[0];
		Bendable p1 = bendableParts[1];

		Connection v0 = p0.getVisual();
		Connection v1 = p1.getVisual();

		// select first part
		ctx.mouseMove(v0, 50, 50);
		ctx.mousePress();
		ctx.mouseRelease();

		SelectionModel selectionModel = p0.getViewer().getAdapter(SelectionModel.class);
		assertTrue(selectionModel.isSelected(p0));

		// select second part
		// XXX: Set <CTRL> modifier so that the selection is appended.
		ctx.mouseMove(v1, 40, 70);
		ctx.mousePress(Modifiers.NONE.shortcut(true));
		ctx.mouseRelease(Modifiers.NONE);

		// check selection
		assertTrue(selectionModel.isSelected(p0));
		assertTrue(selectionModel.isSelected(p1));

		// pick bottom right handle
		SelectionBehavior selBehavior = p0.getRoot().getAdapter(SelectionBehavior.class);
		Method getHandles = AbstractBehavior.class.getDeclaredMethod("getHandles", Collection.class);
		getHandles.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<IHandlePart<? extends Node>> handleParts = (List<IHandlePart<? extends Node>>) getHandles
				.invoke(selBehavior, Arrays.asList(p0, p1));

		// for (IHandlePart hp : handleParts) {
		// System.out.println(" - " +
		// hp.getVisual().getBoundsInParent().getMinX() + ", "
		// + hp.getVisual().getBoundsInParent().getMinY());
		// }
		IHandlePart<? extends Node> botRightHandlePart = handleParts.get(2);

		Bounds b0p = v0.getBoundsInParent();
		Bounds b1p = v1.getBoundsInParent();

		// use handle to resize
		ctx.mouseMove(botRightHandlePart.getVisual(), 60, 75);
		ctx.mousePress();
		ctx.mouseDrag(61, 76);
		// ctx.mouseDrag(62, 77);
		ctx.mouseRelease();
		double expDw = 1;
		double expDh = 1;

		// check that bendables have been properly resized, i.e. their bounds
		// match the resized bounds
		Bounds b0 = v0.getBoundsInParent();
		Bounds b1 = v1.getBoundsInParent();

		assertTrue(b0.getWidth() - b0p.getWidth() < expDw + 0.1);
		assertTrue(b0.getHeight() - b0p.getHeight() < expDh + 0.1);
		assertTrue(b1.getWidth() - b1p.getWidth() < expDw + 0.1);
		assertTrue(b1.getHeight() - b1p.getHeight() < expDh + 0.1);
	}

	@Test
	public void test_translate() {
		Point start = new Point(0, 0);
		Point end = new Point(100, 50);
		Bendable bendable = new Bendable(start, end);
		// check content bend points
		List<BendPoint> contentBendPoints = bendable.getContentBendPoints();
		assertEquals(start, contentBendPoints.get(0).getPosition());
		assertEquals(end, contentBendPoints.get(1).getPosition());
		// check size
		Dimension contentSize = bendable.getContentSize();
		assertEquals(new Rectangle(start, end).getSize(), contentSize);
		// check transform (should equal translation to offset)
		Point contentOffset = new Point(bendable.getContentTransform().getTx(), bendable.getContentTransform().getTy());
		assertEquals(start, contentOffset);
		// apply translation
		Point newStart = start.getTranslated(20, 50);
		Point newEnd = end.getTranslated(20, 50);
		Rectangle newBounds = new Rectangle(newStart, newEnd);
		bendable.setContentTransform(new Affine(new Translate(newStart.x, newStart.y)));
		assertEquals(newBounds.getSize(), bendable.getContentSize());
		contentOffset = new Point(bendable.getContentTransform().getTx(), bendable.getContentTransform().getTy());
		assertEquals(newStart, contentOffset);
	}
}
