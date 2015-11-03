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
package org.eclipse.gef4.mvc.fx.tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

public class FXBendAnchorTests {

	private static class AnchoragePart extends AbstractFXContentPart<Rectangle> {
		@Override
		protected Rectangle createVisual() {
			return new Rectangle(100, 100);
		}

		@Override
		protected void doRefreshVisual(Rectangle visual) {
			org.eclipse.gef4.geometry.planar.Rectangle rect = (org.eclipse.gef4.geometry.planar.Rectangle) getContent();
			visual.setX(rect.getX());
			visual.setY(rect.getY());
			visual.setWidth(rect.getWidth());
			visual.setHeight(rect.getHeight());
		}
	}

	private static class ConnectionContent {
		public org.eclipse.gef4.geometry.planar.Rectangle anchorageStart;
		public org.eclipse.gef4.geometry.planar.Rectangle anchorageEnd;

		public ConnectionContent(org.eclipse.gef4.geometry.planar.Rectangle start,
				org.eclipse.gef4.geometry.planar.Rectangle end) {
			anchorageStart = start;
			anchorageEnd = end;
		}

		public Point getWayPoint() {
			Point delta = anchorageEnd.getCenter().getTranslated(anchorageStart.getCenter().getNegated());
			return anchorageStart.getCenter().getTranslated(delta.getScaled(0.5));
		}
	}

	private static class ConnectionPart extends AbstractFXContentPart<FXConnection> {
		public static final String START_ROLE = "start";
		public static final String END_ROLE = "end";

		@SuppressWarnings("serial")
		@Override
		protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
			IFXAnchor anchor = anchorage.getAdapter(new TypeToken<Provider<? extends IFXAnchor>>() {
			}).get();
			if (role.equals(START_ROLE)) {
				getVisual().setStartAnchor(anchor);
			} else if (role.equals(END_ROLE)) {
				getVisual().setEndAnchor(anchor);
			} else {
				throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
			}
		}

		@Override
		protected FXConnection createVisual() {
			return new FXConnection();
		}

		@Override
		protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
			if (role.equals(START_ROLE)) {
				getVisual().setStartPoint(getVisual().getStartPoint());
			} else if (role.equals(END_ROLE)) {
				getVisual().setEndPoint(getVisual().getEndPoint());
			} else {
				throw new IllegalStateException("Cannot detach from anchor with role <" + role + ">.");
			}
		}

		@Override
		protected void doRefreshVisual(FXConnection visual) {
			if (visual.getWayPoints().size() == 0) {
				visual.addWayPoint(0, ((ConnectionContent) getContent()).getWayPoint());
			}
		}

		@Override
		public SetMultimap<? extends Object, String> getContentAnchorages() {
			SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
			ConnectionContent content = (ConnectionContent) getContent();
			contentAnchorages.put(content.anchorageStart, START_ROLE);
			contentAnchorages.put(content.anchorageEnd, END_ROLE);
			return contentAnchorages;
		}
	}

	private static class TestContentPartFactory implements IContentPartFactory<Node> {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
			if (content instanceof org.eclipse.gef4.geometry.planar.Rectangle) {
				return injector.getInstance(AnchoragePart.class);
			} else if (content instanceof ConnectionContent) {
				return injector.getInstance(ConnectionPart.class);
			} else {
				throw new IllegalArgumentException(content.getClass().toString());
			}
		}
	}

	private static class TestModels {
		public static List<Object> getABC_AB_BC() {
			List<Object> contents = new ArrayList<Object>();
			org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 50, 50);
			org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(100, 0, 50,
					50);
			org.eclipse.gef4.geometry.planar.Rectangle C = new org.eclipse.gef4.geometry.planar.Rectangle(200, 0, 50,
					50);
			contents.add(A);
			contents.add(B);
			contents.add(C);
			contents.add(new ConnectionContent(A, B));
			contents.add(new ConnectionContent(B, C));
			return contents;
		}
	}

	private static class TestModule extends MvcFxModule {
		@Override
		protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			super.bindAbstractContentPartAdapters(adapterMapBinder);
			// focus and select on click
			adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
					.to(FXFocusAndSelectOnClickPolicy.class);
		}

		@SuppressWarnings("serial")
		protected void bindAnchorageAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			// transform policy
			adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(FXTransformPolicy.class);
			// relocate on drag
			adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "translateSelected"))
					.to(FXTranslateSelectedOnDragPolicy.class);
			// bind chopbox anchor provider
			adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IFXAnchor>>() {
			})).to(ChopBoxAnchorProvider.class);
		}

		protected void bindConnectionAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			// transform policy
			adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(FXTransformConnectionPolicy.class);
			// relocate on drag
			adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "translateSelected"))
					.to(FXTranslateSelectedOnDragPolicy.class);
		}

		protected void bindIContentPartFactory() {
			binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
			}).toInstance(new TestContentPartFactory());
		}

		@Override
		protected void configure() {
			super.configure();

			bindIContentPartFactory();

			// contents
			bindAnchorageAdapters(AdapterMaps.getAdapterMapBinder(binder(), AnchoragePart.class));
			bindConnectionAdapters(AdapterMaps.getAdapterMapBinder(binder(), ConnectionPart.class));
		}
	}

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	@Rule
	public FxNonApplicationThreadRule ctx = new FxNonApplicationThreadRule();

	@Inject
	private FXDomain domain;

	@Test
	public void test_relocateAnchor() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.<FXViewer> getAdapter(IViewer.class);
		final Scene scene = ctx.createScene(viewer.getScrollPane(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getABC_AB_BC();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).setContents(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// save initial start point of second connection
		ConnectionPart secondConnectionPart = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		Point initialP1 = secondConnectionPart.getVisual().getCurveNode().getGeometry().toBezier()[0].getP1();

		// move mouse to first connection
		ConnectionPart firstConnectionPart = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 2));
		Robot robot = new Robot();
		Point firstConnectionStart = firstConnectionPart.getVisual().getCurveNode().getGeometry().toBezier()[0]
				.get(0.5);
		ctx.moveTo(robot, firstConnectionPart.getVisual(), firstConnectionStart.x, firstConnectionStart.y);

		// drag connection down by 10px
		ctx.mousePress(robot, InputEvent.BUTTON1_MASK);
		Point pointerLocation = FXUtils.getPointerLocation();
		ctx.mouseDrag(robot, (int) pointerLocation.x, (int) pointerLocation.y + 10);
		ctx.mouseRelease(robot, InputEvent.BUTTON1_MASK);
		robot.delay(1000);

		// check the connection is selected
		assertTrue(viewer.getAdapter(SelectionModel.class).getSelection().contains(firstConnectionPart));

		// move mouse to second anchorage
		AnchoragePart secondPart = (AnchoragePart) viewer.getContentPartMap().get(contents.get(1));
		Point center = ((org.eclipse.gef4.geometry.planar.Rectangle) secondPart.getContent()).getCenter();
		ctx.moveTo(robot, secondPart.getVisual(), center.x, center.y);

		// drag anchorage down by 10px
		ctx.mousePress(robot, InputEvent.BUTTON1_MASK);
		pointerLocation = FXUtils.getPointerLocation();
		ctx.mouseDrag(robot, (int) pointerLocation.x, (int) pointerLocation.y + 10);
		ctx.mouseRelease(robot, InputEvent.BUTTON1_MASK);
		robot.delay(1000);

		// check the anchorage is selected
		assertTrue(viewer.getAdapter(SelectionModel.class).getSelection().contains(secondPart));

		// check the second connection was moved too
		assertNotEquals(initialP1, secondConnectionPart.getVisual().getCurveNode().getGeometry().toBezier()[0].getP1());
	}

}
