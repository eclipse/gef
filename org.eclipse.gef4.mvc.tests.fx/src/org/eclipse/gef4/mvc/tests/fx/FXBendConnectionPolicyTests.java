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
package org.eclipse.gef4.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.AbstractComputationStrategy.AnchoredReferencePoint;
import org.eclipse.gef4.fx.anchors.AbstractComputationStrategy.PreferredOrientation;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.IComputationStrategy;
import org.eclipse.gef4.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.CursorUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy.AnchorHandle;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.providers.DynamicAnchorProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule;
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

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class FXBendConnectionPolicyTests {

	private static class AnchoragePart extends AbstractFXContentPart<Rectangle> {
		@Override
		protected Rectangle createVisual() {
			return new Rectangle(100, 100);
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Rectangle visual) {
			org.eclipse.gef4.geometry.planar.Rectangle rect = ((org.eclipse.gef4.geometry.planar.IShape) getContent())
					.getBounds();
			visual.setX(rect.getX());
			visual.setY(rect.getY());
			visual.setWidth(rect.getWidth());
			visual.setHeight(rect.getHeight());
		}
	}

	private static class ConnectionContent {
		public org.eclipse.gef4.geometry.planar.IShape anchorageStart;
		public org.eclipse.gef4.geometry.planar.IShape anchorageEnd;
		public Point startReferencePoint;
		public Point endReferencePoint;
		public boolean isSimple;

		public ConnectionContent(org.eclipse.gef4.geometry.planar.IShape start,
				org.eclipse.gef4.geometry.planar.IShape end) {
			anchorageStart = start;
			anchorageEnd = end;
		}

		public ConnectionContent(org.eclipse.gef4.geometry.planar.IShape start,
				org.eclipse.gef4.geometry.planar.IShape end, Point startRef, Point endRef) {
			anchorageStart = start;
			anchorageEnd = end;
			startReferencePoint = startRef;
			endReferencePoint = endRef;
		}

		public Point getWayPoint() {
			Point delta = anchorageEnd.getBounds().getCenter()
					.getTranslated(anchorageStart.getBounds().getCenter().getNegated());
			return anchorageStart.getBounds().getCenter().getTranslated(delta.getScaled(0.5));
		}
	}

	private static class ConnectionPart extends AbstractFXContentPart<Connection> {
		public static final String START_ROLE = "start";
		public static final String END_ROLE = "end";

		@SuppressWarnings("serial")
		@Override
		protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
			IAnchor anchor = anchorage.getAdapter(new TypeToken<Provider<? extends IAnchor>>() {
			}).get();
			if (role.equals(START_ROLE)) {
				// TODO: we could provide a visitor that initializes the
				// required keys for the supported strategies
				// provide parameters for projection computation strategies
				getVisual().setStartAnchor(anchor);
				if (getContent().startReferencePoint != null) {
					((DynamicAnchor) anchor).getDynamicComputationParameter(getVisual().getStartAnchorKey(),
							AnchoredReferencePoint.class).set(getContent().startReferencePoint);
				}
				((DynamicAnchor) anchor)
						.getDynamicComputationParameter(getVisual().getStartAnchorKey(), PreferredOrientation.class)
						.set(Orientation.VERTICAL);
			} else if (role.equals(END_ROLE)) {
				getVisual().setEndAnchor(anchor);
				if (getContent().endReferencePoint != null) {
					((DynamicAnchor) anchor)
							.getDynamicComputationParameter(getVisual().getEndAnchorKey(), AnchoredReferencePoint.class)
							.set(getContent().endReferencePoint);
				}
				((DynamicAnchor) anchor)
						.getDynamicComputationParameter(getVisual().getEndAnchorKey(), PreferredOrientation.class)
						.set(Orientation.VERTICAL);
			} else {
				throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
			}
		}

		@Override
		protected Connection createVisual() {
			return new Connection();
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
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
			contentAnchorages.put(getContent().anchorageStart, START_ROLE);
			contentAnchorages.put(getContent().anchorageEnd, END_ROLE);
			return contentAnchorages;
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Connection visual) {
			if (!getContent().isSimple && visual.getControlPoints().size() == 0) {
				visual.addControlPoint(0, getContent().getWayPoint());
			}
		}

		@Override
		public ConnectionContent getContent() {
			return (ConnectionContent) super.getContent();
		}
	}

	public static class TestAnchorProvider extends DynamicAnchorProvider {
		@Override
		protected DynamicAnchor createAnchor(IComputationStrategy computationStrategy) {
			DynamicAnchor anchor = computationStrategy == null ? new DynamicAnchor(getAdaptable().getVisual())
					: new DynamicAnchor(getAdaptable().getVisual(), computationStrategy);
			anchor.setReferenceGeometry((IShape) ((IContentPart<?, ?>) getAdaptable()).getContent());
			return anchor;
		}
	}

	private static class TestContentPartFactory implements IContentPartFactory<Node> {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
			if (content instanceof org.eclipse.gef4.geometry.planar.IShape) {
				return injector.getInstance(AnchoragePart.class);
			} else if (content instanceof ConnectionContent) {
				return injector.getInstance(ConnectionPart.class);
			} else {
				throw new IllegalArgumentException(content.getClass().toString());
			}
		}
	}

	private static class TestModels {
		public static List<Object> getAB_AB() {
			List<Object> contents = new ArrayList<>();
			org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 50, 50);
			org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500, 0, 50,
					50);
			contents.add(A);
			contents.add(B);
			contents.add(new ConnectionContent(A, B));
			return contents;
		}

		public static List<Object> getAB_AB_simple() {
			List<Object> contents = new ArrayList<>();
			org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 50, 50);
			org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500, 0, 50,
					50);
			contents.add(A);
			contents.add(B);
			ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getAB_offset_simple() {
			List<Object> contents = new ArrayList<>();
			org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 50, 50);
			org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500, 500, 50,
					50);
			contents.add(A);
			contents.add(B);
			ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getAB_offset2_simple() {
			List<Object> contents = new ArrayList<>();
			org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 50, 50);
			org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(300, 500, 50,
					50);
			contents.add(A);
			contents.add(B);
			ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getABC_AB_BC() {
			List<Object> contents = new ArrayList<>();
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

		public static List<Object> getDA_click_error() {
			Polygon startAnchorage = new Polygon(349.49988, 56.54434506500246, 359.9554149349976, 66.99988000000002,
					349.49988, 77.45541493499758, 339.04434506500246, 66.99988000000002, 349.49988, 56.54434506500246);
			Polygon endAnchorage = new Polygon(338.49988, 237.54434506500243, 348.9554149349976, 247.99988, 338.49988,
					258.4554149349975, 328.04434506500246, 247.99988, 338.49988, 237.54434506500243);
			Point startReferencePoint = new Point(356.25, -365.87);
			Point endReferencePoint = new Point(350.01, -190.0);
			List<Object> contents = new ArrayList<>();
			contents.add(startAnchorage);
			contents.add(endAnchorage);
			ConnectionContent connectionContent = new ConnectionContent(startAnchorage, endAnchorage,
					startReferencePoint, endReferencePoint);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}
	}

	private static class TestModule extends MvcFxModule {
		@Override
		protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			super.bindAbstractContentPartAdapters(adapterMapBinder);
			// focus and select on click
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
		}

		protected void bindAnchorageAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			// transform policy
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);
			// relocate on drag
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
			// bind dynamic anchor provider
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TestAnchorProvider.class);
		}

		protected void bindConnectionAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			// transform policy
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformConnectionPolicy.class);
			// relocate on drag
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
			// bend
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendConnectionPolicy.class);
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
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	@Inject
	private FXDomain domain;

	public int countExplicit(Connection connection) {
		int numExplicit = 0;
		for (IAnchor anchor : connection.getAnchors()) {
			if (!connection.getRouter().isImplicitAnchor(anchor)) {
				numExplicit++;
			}
		}
		return numExplicit;
	}

	private void equalsUnprecise(Point p, Point q) {
		assertEquals(p + " and " + q + " are not (unprecisely) equal but differ in x: ", p.x, q.x, 0.5);
		assertEquals(p + " and " + q + " are not (unprecisely) equal but differ in y: ", p.y, q.y, 0.5);
	}

	@Test
	public void test_create_orthogonal_segment_from_implicit_connected()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_offset2_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages

		// XXX: The strategies are exchanged before setting the router so that a
		// refresh will use these strategies
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy() {
					@Override
					public Point computePositionInScene(Node anchorage, Node anchored, Set<Parameter<?>> parameters) {
						// ensure routing starts going to the right
						return new Point(49, 25);
					}
				});
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy() {
					@Override
					public Point computePositionInScene(Node anchorage, Node anchored, Set<Parameter<?>> parameters) {
						// ensure routing ends going to the right
						return new Point(301, 525);
					}
				});

		// XXX: Set router on application thread as the position change listener
		// is executed within the application thread, too, and we need to wait
		// for a recent connection refresh that was caused by an anchor position
		// change
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// check if router inserted implicit points
		assertEquals(4, connection.getVisual().getPoints().size());

		// create new segment between 2nd implicit and end
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();

		// determine segment indices for neighbor anchors
		int firstSegmentIndex = 2;
		int secondSegmentIndex = 3;

		// determine middle of segment
		Point firstPoint = connection.getVisual().getPoint(firstSegmentIndex);
		Point secondPoint = connection.getVisual().getPoint(secondSegmentIndex);

		// check that segment to be selected is horizontal
		assertEquals(firstPoint.y, secondPoint.y, 0.0001);

		Vector direction = new Vector(firstPoint, secondPoint);
		Point midPoint = firstPoint.getTranslated(direction.x / 2, direction.y / 2);
		Point2D midInScene = connection.getVisual().localToScene(midPoint.x, midPoint.y);

		// determine connectedness of first anchor handle
		Node firstAnchorage = connection.getVisual().getAnchor(firstSegmentIndex).getAnchorage();
		boolean isFirstConnected = firstAnchorage != null && firstAnchorage != connection.getVisual();

		// make the anchor handles explicit
		List<AnchorHandle> explicit = bendPolicy.makeExplicit(firstSegmentIndex - 1, secondSegmentIndex);
		AnchorHandle firstAnchorHandle = explicit.get(1);
		AnchorHandle secondAnchorHandle = explicit.get(2);
		assertEquals(4, connection.getVisual().getPoints().size());

		// copy first point if connected
		if (isFirstConnected) {
			// use the copy as the new first anchor handle
			firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle, FX2Geometry.toPoint(connection.getVisual()
					.localToScene(Geometry2FX.toFXPoint(firstAnchorHandle.getInitialPosition()))));
		}

		// create new anchor at the segment's middle
		secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle, FX2Geometry.toPoint(midInScene));
		// copy that new anchor
		secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle, FX2Geometry.toPoint(midInScene));

		// check to be selected segment is horizontal
		assertEquals(firstAnchorHandle.getPosition().y, secondAnchorHandle.getPosition().y, 0.0001);

		// select the first anchor and the copy of the new mid anchor for
		// movement
		bendPolicy.select(firstAnchorHandle);
		bendPolicy.select(secondAnchorHandle);
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment up
		bendPolicy.move(new Point(), new Point(0, -50));
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment further up
		bendPolicy.move(new Point(), new Point(0, -100));
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment further up
		bendPolicy.move(new Point(), new Point(0, -150));
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment down a bit
		bendPolicy.move(new Point(), new Point(0, -120));
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment down a bit
		bendPolicy.move(new Point(), new Point(0, -60));
		assertEquals(6, connection.getVisual().getPoints().size());

		// move new segment back to its original position
		bendPolicy.move(new Point(), new Point());
		assertEquals(4, connection.getVisual().getPoints().size());

		// commit (i.e. normalize)
		bendPolicy.commit();
		assertEquals(4, connection.getVisual().getPoints().size());
	}

	@Test
	public void test_end_overlays_way_restore() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPoints().size());

		// find way point anchor
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle wayPointAnchorHandle = bendPolicy.findExplicitAnchorBackward(1);

		// check anchor position
		assertEquals(wayPoint, wayPointAnchorHandle.getPosition());

		// select end point
		AnchorHandle endAnchorHandle = bendPolicy.findExplicitAnchorForward(2);
		bendPolicy.select(endAnchorHandle);

		// move left to overlay the way point
		bendPolicy.move(new Point(), new Point(-wayPoint.getDistance(endAnchorHandle.getPosition()), 0));

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPoints().size());

		// move back to restore the overlain anchor
		bendPolicy.move(new Point(), new Point());

		// verify point is present again
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		bendPolicy.commit();
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

	@Test
	public void test_move_connected_orthogonal_segment_down()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// copy start point and end point
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, startPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, endPoint);
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment down by 100 to create 2 new segments
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, 100));
		bendPolicy.commit();
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(1));
	}

	@Test
	public void test_move_connected_orthogonal_segment_down_translated()
			throws InterruptedException, InvocationTargetException, AWTException {
		// FIXME: This test case is unstable. Sometimes, the segment is
		// properly moved, sometimes it is not.

		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// add translation to connection
		connection.getVisual().setTranslateX(100);
		connection.getVisual().setTranslateY(100);

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// XXX: Set router on application thread as the position change listener
		// is executed within the application thread, too, and we need to wait
		// for a recent connection refresh that was caused by an anchor position
		// change
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// copy start point and end point
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		assertEquals(startPoint, startAnchorHandle.getPosition());
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle,
				startPoint.getTranslated(100, 100));
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle,
				endPoint.getTranslated(100, 100));
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));
		// check coordinates
		assertEquals(startPoint, startAnchorHandle.getPosition());
		assertEquals(startPoint, firstWayAnchorHandle.getPosition());
		assertEquals(endPoint, secondWayAnchorHandle.getPosition());

		// move segment down by 100 to create 2 new segments
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, 100));
		bendPolicy.commit();
		// check number of points and their positions
		// FIXME: Unstable, varies between 2, 3, and 4 explicit anchors.
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(1));
	}

	@Test
	public void test_move_connected_orthogonal_segment_restore()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// copy start point and end point
		bendPolicy.init();
		bendPolicy.move(new Point(), new Point());
		assertEquals(2, connection.getVisual().getPoints().size());
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, startPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, endPoint);
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());
		equalsUnprecise(startPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint, connection.getVisual().getControlPoint(1));

		// move segment down by 100 to create 2 new segments
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, 100));
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint.getTranslated(0, 100), connection.getVisual().getControlPoint(1));
		equalsUnprecise(startPoint.getTranslated(0, 25), connection.getVisual().getStartPoint());
		equalsUnprecise(endPoint.getTranslated(0, 25), connection.getVisual().getEndPoint());

		// move segment back
		bendPolicy.move(new Point(), new Point());
		// check number of points
		assertEquals(4, connection.getVisual().getPoints().size());
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());
		equalsUnprecise(startPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint, connection.getVisual().getControlPoint(1));

		// check number of points after commit
		bendPolicy.commit();
		assertEquals(2, connection.getVisual().getPoints().size());
	}

	@Test
	public void test_move_connected_orthogonal_segment_up()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// copy start point and end point
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, startPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, endPoint);
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment down by 100 to create 2 new segments
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, -100));
		bendPolicy.commit();
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, -100), connection.getVisual().getControlPoint(0));
		equalsUnprecise(endPoint.getTranslated(0, -100), connection.getVisual().getControlPoint(1));
	}

	@Test
	public void test_move_explicit_orthogonal_segment_overlay()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y);
		Point secondWayPoint = new Point(startPoint.x + 100, startPoint.y + 100);
		Point thirdWayPoint = new Point(startPoint.x + 200, startPoint.y + 100);
		Point fourthWayPoint = new Point(startPoint.x + 200, startPoint.y);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
		AnchorHandle thirdWayAnchorHandle = bendPolicy.createAfter(secondWayAnchorHandle, thirdWayPoint);
		bendPolicy.createAfter(thirdWayAnchorHandle, fourthWayPoint);
		// check number of points
		assertEquals(6, countExplicit(connection.getVisual()));

		// move segment up to create an overlay
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.select(thirdWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, -100));
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		// double point at fourth way point due to selection constraint
		// (snapping)
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment further up to restore the removed points
		bendPolicy.move(new Point(), new Point(0, -200));
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint.getTranslated(0, -200), connection.getVisual().getControlPoint(1));
		equalsUnprecise(thirdWayPoint.getTranslated(0, -200), connection.getVisual().getControlPoint(2));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment back to its original position
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(thirdWayPoint, connection.getVisual().getControlPoint(2));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());
	}

	@Test
	public void test_move_explicit_orthogonal_segment_overlay_side()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y);
		Point secondWayPoint = new Point(startPoint.x + 100, startPoint.y + 200);
		Point thirdWayPoint = new Point(startPoint.x + 200, startPoint.y + 200);
		Point fourthWayPoint = new Point(startPoint.x + 200, startPoint.y + 100);
		Point fifthWayPoint = new Point(startPoint.x + 300, startPoint.y + 100);
		Point sixthWayPoint = new Point(startPoint.x + 300, startPoint.y);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
		AnchorHandle thirdWayAnchorHandle = bendPolicy.createAfter(secondWayAnchorHandle, thirdWayPoint);
		AnchorHandle fourthWayAnchorHandle = bendPolicy.createAfter(thirdWayAnchorHandle, fourthWayPoint);
		AnchorHandle fifthWayAnchorHandle = bendPolicy.createAfter(fourthWayAnchorHandle, fifthWayPoint);
		bendPolicy.createAfter(fifthWayAnchorHandle, sixthWayPoint);
		// check number of points
		assertEquals(8, countExplicit(connection.getVisual()));

		// move segment to the right to create a 3 segment overlay
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(100, 0));
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), connection.getVisual().getControlPoint(0));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(fifthWayPoint, connection.getVisual().getControlPoint(2));
		equalsUnprecise(sixthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment back to the left to restore the original positions
		bendPolicy.move(new Point(), new Point());
		assertEquals(8, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(thirdWayPoint, connection.getVisual().getControlPoint(2));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(fifthWayPoint, connection.getVisual().getControlPoint(4));
		equalsUnprecise(sixthWayPoint, connection.getVisual().getControlPoint(5));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment to the right to create an unprecise 3 segment overlay
		// (default threshold of 10px)
		bendPolicy.move(new Point(), new Point(95, 0));
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), connection.getVisual().getControlPoint(0));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(fifthWayPoint, connection.getVisual().getControlPoint(2));
		equalsUnprecise(sixthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// check if the overlay is still removed after commit
		bendPolicy.commit();
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), connection.getVisual().getControlPoint(0));
		equalsUnprecise(fourthWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(fifthWayPoint, connection.getVisual().getControlPoint(2));
		equalsUnprecise(sixthWayPoint, connection.getVisual().getControlPoint(3));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());
	}

	@Test
	public void test_move_explicit_orthogonal_segment_simple()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_offset_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPoints().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		connection.getVisual().setRouter(new OrthogonalRouter());
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy());

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = connection.getVisual().getStartPoint();
		Point endPoint = connection.getVisual().getEndPoint();

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y - 25);
		Point secondWayPoint = new Point(startPoint.x + 100, endPoint.y + 25);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
		// start point and end point changed due to the new control points
		startPoint = connection.getVisual().getStartPoint();
		endPoint = connection.getVisual().getEndPoint();
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment to the left (no overlay)
		bendPolicy.select(firstWayAnchorHandle);
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(-10, 0));
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint.getTranslated(-10, 0), connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint.getTranslated(-10, 0), connection.getVisual().getControlPoint(1));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment to the right (no overlay)
		bendPolicy.move(new Point(), new Point(10, 0));
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint.getTranslated(10, 0), connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint.getTranslated(10, 0), connection.getVisual().getControlPoint(1));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());

		// move segment back to its original position
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, connection.getVisual().getStartPoint());
		equalsUnprecise(firstWayPoint, connection.getVisual().getControlPoint(0));
		equalsUnprecise(secondWayPoint, connection.getVisual().getControlPoint(1));
		equalsUnprecise(endPoint, connection.getVisual().getEndPoint());
	}

	@Test
	public void test_move_segment_connected_overlay()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB_simple();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		assertEquals(2, connection.getVisual().getPoints().size());

		bendPolicy.init();

		// find start and end handles
		AnchorHandle startHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle endHandle = bendPolicy.findExplicitAnchorForward(1);

		assertEquals(connection.getVisual().getStartPoint(), startHandle.getPosition());
		assertEquals(connection.getVisual().getEndPoint(), endHandle.getPosition());

		// copy both connected end points
		AnchorHandle leftCopy = bendPolicy.createAfter(startHandle, startHandle.getPosition());
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(startHandle.getPosition(), leftCopy.getPosition());

		AnchorHandle rightCopy = bendPolicy.createBefore(endHandle, endHandle.getPosition());
		assertEquals(4, connection.getVisual().getPoints().size());
		assertEquals(endHandle.getPosition(), rightCopy.getPosition());

		// select the copies for movement
		bendPolicy.select(leftCopy);
		bendPolicy.select(rightCopy);

		// move down by 100
		bendPolicy.move(new Point(), new Point(0, 100));

		// check if points are correct
		assertEquals(4, connection.getVisual().getPoints().size());

		// move back to get a double overlay
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();

		// check if points have been removed
		assertEquals(2, connection.getVisual().getPoints().size());
	}

	@Test
	public void test_move_single_explicit_anchor()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		// find way point anchor
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle anchorBackwards = bendPolicy.findExplicitAnchorBackward(1);
		AnchorHandle anchorForwards = bendPolicy.findExplicitAnchorForward(1);
		// assertEquals(1, anchorBackwards.getExplicitAnchorIndex());
		assertEquals(anchorBackwards, anchorForwards);

		// check anchor position
		assertEquals(wayPoint, anchorBackwards.getPosition());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// select anchor
		bendPolicy.select(anchorBackwards);

		// move down by 100
		bendPolicy.move(new Point(0, 0), new Point(0, 100));
		assertEquals(wayPoint.getTranslated(0, 100), connection.getVisual().getPoint(1));
		assertEquals(wayPoint.getTranslated(0, 100), anchorBackwards.getPosition());

		// verify position after commit
		bendPolicy.commit();
		assertEquals(wayPoint.getTranslated(0, 100), connection.getVisual().getPoint(1));
	}

	@Test
	public void test_overlay_segment_left_first() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPoints().size());

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 200));
		AnchorHandle thirdWayAnchorHandle = bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 210));
		bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
		// check if points are correct
		assertEquals(7, connection.getVisual().getPoints().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 210), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that only the second way anchor overlays the first
		// way anchor
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.select(thirdWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, -95));
		// check if the overlaying was removed
		assertEquals(6, connection.getVisual().getPoints().size());

		// move segment so that also the third way anchor overlays the fourth
		// way anchor, i.e. it is a double overlay
		bendPolicy.move(new Point(), new Point(0, -105));
		// check if the overlaying anchors were removed
		assertEquals(5, connection.getVisual().getPoints().size());
		// check that the overlain anchors have the same position as before
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(2));

		// move segment back to its original position
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();
		// check that all anchors have been restored
		assertEquals(7, connection.getVisual().getPoints().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 210), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));
	}

	@Test
	public void test_overlay_segment_right_first()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPoints().size());

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 210));
		AnchorHandle thirdWayAnchorHandle = bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 200));
		bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
		// check if points are correct
		assertEquals(7, connection.getVisual().getPoints().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 210), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that only the second way anchor overlays the first
		// way anchor
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.select(thirdWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, -95));
		// check if the overlaying was removed
		assertEquals(6, connection.getVisual().getPoints().size());

		// move segment so that also the third way anchor overlays the fourth
		// way anchor, i.e. it is a double overlay
		bendPolicy.move(new Point(), new Point(0, -105));
		// check if the overlaying anchors were removed
		assertEquals(5, connection.getVisual().getPoints().size());
		// check that the overlain anchors have the same position as before
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(2));

		// move segment back to its original position
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();
		// check that all anchors have been restored
		assertEquals(7, connection.getVisual().getPoints().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 210), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));
	}

	@Test
	public void test_overlay_segment_simple() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPoints().size());

		// create control points
		bendPolicy.init();
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorBackward(0);
		AnchorHandle firstWayAnchorHandle = bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
		AnchorHandle secondWayAnchorHandle = bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 200));
		AnchorHandle thirdWayAnchorHandle = bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 200));
		bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
		// check if points are correct
		assertEquals(7, connection.getVisual().getPoints().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that we get a double overlay
		bendPolicy.select(secondWayAnchorHandle);
		bendPolicy.select(thirdWayAnchorHandle);
		bendPolicy.move(new Point(), new Point(0, -100));
		bendPolicy.commit();
		// check if points have been removed
		assertEquals(5, connection.getVisual().getPoints().size());
	}

	@Test
	public void test_overlay_single() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		// find way point anchor
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle anchorBackwards = bendPolicy.findExplicitAnchorBackward(1);
		AnchorHandle anchorForwards = bendPolicy.findExplicitAnchorForward(1);
		// assertEquals(1, anchorBackwards.getExplicitAnchorIndex());
		assertEquals(anchorBackwards, anchorForwards);
		AnchorHandle firstWayPointAnchorHandle = anchorBackwards;

		// check anchor position
		assertEquals(wayPoint, anchorBackwards.getPosition());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// create way point 20 px to the right of the existing one
		bendPolicy.createAfter(firstWayPointAnchorHandle, wayPoint.getTranslated(20, 0));
		// verify that the point is inserted
		assertEquals(4, connection.getVisual().getPoints().size());

		// select first way point
		bendPolicy.select(firstWayPointAnchorHandle);

		// move right by 20
		bendPolicy.move(new Point(), new Point(20, 0));

		// verify that the point is removed
		assertEquals(3, connection.getVisual().getPoints().size());

		// verify point is removed after commit
		bendPolicy.commit();
		assertEquals(3, connection.getVisual().getPoints().size());
	}

	@Test
	public void test_relocateAnchor() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

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
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
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
		secondConnectionPart.getVisual().getCurveNode().setStrokeWidth(5);

		// move mouse to first connection
		ConnectionPart firstConnectionPart = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 2));
		firstConnectionPart.getVisual().getCurveNode().setStrokeWidth(5);
		Robot robot = new Robot();
		Point firstConnectionStart = firstConnectionPart.getVisual().getCurveNode().getGeometry().toBezier()[0]
				.get(0.5);

		ctx.moveTo(robot, firstConnectionPart.getVisual(), firstConnectionStart.x, firstConnectionStart.y);

		// drag connection down by 10px
		ctx.mousePress(robot, java.awt.event.InputEvent.BUTTON1_MASK);
		Point pointerLocation = CursorUtils.getPointerLocation();
		ctx.mouseDrag(robot, (int) pointerLocation.x, (int) pointerLocation.y + 10);
		ctx.mouseRelease(robot, java.awt.event.InputEvent.BUTTON1_MASK);
		robot.delay(1000);

		// check the connection is selected
		assertTrue(viewer.getAdapter(SelectionModel.class).getSelectionUnmodifiable().contains(firstConnectionPart));

		// move mouse to second anchorage
		AnchoragePart secondPart = (AnchoragePart) viewer.getContentPartMap().get(contents.get(1));
		Point center = ((org.eclipse.gef4.geometry.planar.Rectangle) secondPart.getContent()).getCenter();
		ctx.moveTo(robot, secondPart.getVisual(), center.x, center.y);

		// drag anchorage down by 10px
		ctx.mousePress(robot, java.awt.event.InputEvent.BUTTON1_MASK);
		pointerLocation = CursorUtils.getPointerLocation();
		ctx.mouseDrag(robot, (int) pointerLocation.x, (int) pointerLocation.y + 10);
		ctx.mouseRelease(robot, java.awt.event.InputEvent.BUTTON1_MASK);
		robot.delay(1000);

		// check the anchorage is selected
		assertTrue(viewer.getAdapter(SelectionModel.class).getSelectionUnmodifiable().contains(secondPart));

		// check the second connection was moved too
		assertNotEquals(initialP1, secondConnectionPart.getVisual().getCurveNode().getGeometry().toBezier()[0].getP1());
	}

	/**
	 * <ol>
	 * <li>class com.thyssenkrupp.tkse.promise.mdse.process.ui.view.provider.
	 * ProcessDynamicAnchorProvider$1[Point(352.8252868652344,
	 * -365.8699951171875)]
	 * (com.thyssenkrupp.tkse.promise.mdse.process.ui.view.provider.
	 * ProcessDynamicAnchorProvider$1@665574fb)
	 * {org.eclipse.gef4.mvc.fx.policies.
	 * FXBendConnectionPolicy$AnchorHandle@3eb7ff78},
	 * <ul>
	 * <li>DA anchorage geometry in scene = Polygon: (349.49988,
	 * 56.54434506500246) -> (359.9554149349976, 66.99988000000002) ->
	 * (349.49988, 77.45541493499758) -> (339.04434506500246, 66.99988000000002)
	 * -> (349.49988, 56.54434506500246)
	 * <li>DA anchor key = AnchorKey
	 * <start> <GeometryNode@333e01c6[styleClass=curve]>
	 * <li>DA anchored reference point = Point(356.25, -365.87)
	 * </ul>
	 * <li>class
	 * org.eclipse.gef4.fx.nodes.OrthogonalRouter$OrthogonalPolylineRouterAnchor
	 * [Point(352.8252868652344, -190.0)], class
	 * org.eclipse.gef4.fx.anchors.StaticAnchor[Point(356.25, -190.0)]
	 * (StaticAnchor[referencePosition = Point(356.25, -190.0)])
	 * {org.eclipse.gef4.mvc.fx.policies.
	 * FXBendConnectionPolicy$AnchorHandle@50ad5625},
	 * <li>class com.thyssenkrupp.tkse.promise.mdse.process.ui.view.provider.
	 * ProcessDynamicAnchorProvider$1[Point(346.9552917480469, -190.0)]
	 * (com.thyssenkrupp.tkse.promise.mdse.process.ui.view.provider.
	 * ProcessDynamicAnchorProvider$1@1d921267)
	 * {org.eclipse.gef4.mvc.fx.policies.
	 * FXBendConnectionPolicy$AnchorHandle@1c5f6292}
	 * <ul>
	 * <li>DA anchorage geometry in scene = Polygon: (338.49988,
	 * 237.54434506500243) -> (348.9554149349976, 247.99988) -> (338.49988,
	 * 258.4554149349975) -> (328.04434506500246, 247.99988) -> (338.49988,
	 * 237.54434506500243)
	 * <li>DA anchor key = AnchorKey
	 * <end> <GeometryNode@333e01c6[styleClass=curve]>
	 * <li>DA anchored reference point = Point(350.01, -190.0)
	 * </ul>
	 * </ol>
	 *
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws AWTException
	 */
	@Test
	public void test_segment_select_error_split_segment()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getDA_click_error();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		Point controlPoint = new Point(356.25, 237.54434204101562);
		connection.getVisual().addControlPoint(0, controlPoint);

		// XXX: The strategies are exchanged before setting the router so that a
		// refresh will use these strategies
		((DynamicAnchor) connection.getVisual().getStartAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy() {
				});
		((DynamicAnchor) connection.getVisual().getEndAnchor())
				.setComputationStrategy(new OrthogonalProjectionStrategy() {
				});

		// XXX: Set router on application thread as the position change listener
		// is executed within the application thread, too, and we need to wait
		// for a recent connection refresh that was caused by an anchor position
		// change
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// ensure router inserted point
		assertEquals(3, countExplicit(connection.getVisual()));

		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();
		bendPolicy.selectSegment(0);
		bendPolicy.move(new Point(), new Point());
		bendPolicy.commit();

		assertEquals(3, countExplicit(connection.getVisual()));
	}

	@Test
	public void test_start_overlays_way_restore() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPoints().size());

		// find way point anchor
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle wayPointAnchorHandle = bendPolicy.findExplicitAnchorBackward(1);

		// check anchor position
		assertEquals(wayPoint, wayPointAnchorHandle.getPosition());

		// select end point
		AnchorHandle startAnchorHandle = bendPolicy.findExplicitAnchorForward(0);
		bendPolicy.select(startAnchorHandle);

		// move to the right to overlay the way point
		double distance = wayPoint.getDistance(startAnchorHandle.getPosition());
		bendPolicy.move(new Point(), new Point(distance, 0));

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPoints().size());

		// move back to restore the overlain anchor
		bendPolicy.move(new Point(), new Point());

		// verify point is present again
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		bendPolicy.commit();
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

	@Test
	public void test_way_overlays_end_remove() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPoints().size());

		// find way point anchor
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle wayPointAnchorHandle = bendPolicy.findExplicitAnchorBackward(1);

		// check anchor position
		assertEquals(wayPoint, wayPointAnchorHandle.getPosition());

		// select way point
		bendPolicy.select(wayPointAnchorHandle);

		// find end point handle
		AnchorHandle endPointHandle = bendPolicy.findExplicitAnchorForward(2);

		// move to the right to overlay the end point, but not exactly onto it
		Point endPoint = endPointHandle.getPosition();
		double distance = wayPoint.getDistance(endPoint) - 5;
		bendPolicy.move(new Point(), new Point(distance, 0));
		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPoints().size());

		// verify that the end point is still at the same location
		assertEquals(endPoint, endPointHandle.getPosition());

		// verify point is removed after commit
		bendPolicy.commit();
		assertEquals(2, connection.getVisual().getPoints().size());
		assertEquals(endPoint, endPointHandle.getPosition());
	}

	@Test
	public void test_way_overlays_end_restore() throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new TestModule());

		// inject domain
		injector.injectMembers(this);

		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		ctx.createScene(viewer.getCanvas(), 400, 200);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		final List<Object> contents = TestModels.getAB_AB();
		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}

		// query bend policy for first connection
		ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap().get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPoints().size());

		// find way point anchor
		FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		bendPolicy.init();
		Point wayPoint = connection.getContent().getWayPoint();
		AnchorHandle wayPointAnchorHandle = bendPolicy.findExplicitAnchorBackward(1);

		// check anchor position
		assertEquals(wayPoint, wayPointAnchorHandle.getPosition());

		// select way point
		bendPolicy.select(wayPointAnchorHandle);

		// find end point handle
		AnchorHandle endPointHandle = bendPolicy.findExplicitAnchorForward(2);

		// move to the right to overlay the end point
		double distance = wayPoint.getDistance(endPointHandle.getPosition());
		bendPolicy.move(new Point(), new Point(distance, 0));
		assertEquals(wayPoint.getTranslated(distance, 0), wayPointAnchorHandle.getPosition());

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPoints().size());

		// move back to the left to restore the overlain anchor
		bendPolicy.move(new Point(), new Point());
		assertEquals(wayPoint, wayPointAnchorHandle.getPosition());

		// verify point is present again
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		bendPolicy.commit();
		assertEquals(3, connection.getVisual().getPoints().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

}
