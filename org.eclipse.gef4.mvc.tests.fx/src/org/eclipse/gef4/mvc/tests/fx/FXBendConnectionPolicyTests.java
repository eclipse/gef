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
package org.eclipse.gef4.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.PreferredOrientation;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.CursorUtils;
import org.eclipse.gef4.fx.utils.NodeUtils;
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
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.providers.DefaultAnchorProvider;
import org.eclipse.gef4.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule.RunnableWithResult;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule.RunnableWithResultAndParam;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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
		protected void doRefreshVisual(final Rectangle visual) {
			final org.eclipse.gef4.geometry.planar.Rectangle rect = ((org.eclipse.gef4.geometry.planar.IShape) getContent())
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
		public boolean isSimple;

		public ConnectionContent(final org.eclipse.gef4.geometry.planar.IShape start,
				final org.eclipse.gef4.geometry.planar.IShape end) {
			anchorageStart = start;
			anchorageEnd = end;
		}

		public ConnectionContent(final org.eclipse.gef4.geometry.planar.IShape start,
				final org.eclipse.gef4.geometry.planar.IShape end, final Point startRef, final Point endRef) {
			anchorageStart = start;
			anchorageEnd = end;
		}

		public Point getWayPoint() {
			final Point delta = anchorageEnd.getBounds().getCenter()
					.getTranslated(anchorageStart.getBounds().getCenter().getNegated());
			return anchorageStart.getBounds().getCenter().getTranslated(delta.getScaled(0.5));
		}
	}

	private static class ConnectionPart extends AbstractFXContentPart<Connection> {
		public static final String START_ROLE = "start";
		public static final String END_ROLE = "end";

		@Override
		protected void attachToAnchorageVisual(final IVisualPart<Node, ? extends Node> anchorage, final String role) {
			final IAnchor anchor = anchorage.getAdapter(IAnchorProvider.class).get(this, role);
			if (role.equals(START_ROLE)) {
				getVisual().setStartAnchor(anchor);
			} else if (role.equals(END_ROLE)) {
				getVisual().setEndAnchor(anchor);
			} else {
				throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
			}
		}

		@Override
		protected Connection createVisual() {
			return new Connection();
		}

		@Override
		protected void detachFromAnchorageVisual(final IVisualPart<Node, ? extends Node> anchorage, final String role) {
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
			final SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
			contentAnchorages.put(getContent().anchorageStart, START_ROLE);
			contentAnchorages.put(getContent().anchorageEnd, END_ROLE);
			return contentAnchorages;
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(final Connection visual) {
			if (!getContent().isSimple && visual.getControlPoints().size() == 0) {
				visual.addControlPoint(0, getContent().getWayPoint());
			}
		}

		@Override
		public ConnectionContent getContent() {
			return (ConnectionContent) super.getContent();
		}
	}

	public static class TestAnchorProvider extends DefaultAnchorProvider {
		@Override
		protected void initializeComputationParameters(final DynamicAnchor anchor) {
			final AnchorageReferenceGeometry computationParameter = anchor
					.getComputationParameter(AnchorageReferenceGeometry.class);
			// if there is a default binding, remove it
			if (computationParameter.isBound()) {
				computationParameter.unbind();
			}
			computationParameter.set((IShape) ((IContentPart<?, ?>) getAdaptable()).getContent());
		}
	}

	private static class TestContentPartFactory implements IContentPartFactory<Node> {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Node, ? extends Node> createContentPart(final Object content,
				final IBehavior<Node> contextBehavior, final Map<Object, Object> contextMap) {
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
		public static List<Object> get_regression_makeExplicit() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(310, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(0, 85,
					50, 50);
			contents.add(A);
			contents.add(B);
			final ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getAB_AB() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500, 0,
					50, 50);
			contents.add(A);
			contents.add(B);
			contents.add(new ConnectionContent(A, B));
			return contents;
		}

		public static List<Object> getAB_AB_simple() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500, 0,
					50, 50);
			contents.add(A);
			contents.add(B);
			final ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getAB_offset_simple() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(500,
					500, 50, 50);
			contents.add(A);
			contents.add(B);
			final ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getAB_offset2_simple() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(300,
					500, 50, 50);
			contents.add(A);
			contents.add(B);
			final ConnectionContent connectionContent = new ConnectionContent(A, B);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}

		public static List<Object> getABC_AB_BC() {
			final List<Object> contents = new ArrayList<>();
			final org.eclipse.gef4.geometry.planar.Rectangle A = new org.eclipse.gef4.geometry.planar.Rectangle(0, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle B = new org.eclipse.gef4.geometry.planar.Rectangle(100, 0,
					50, 50);
			final org.eclipse.gef4.geometry.planar.Rectangle C = new org.eclipse.gef4.geometry.planar.Rectangle(200, 0,
					50, 50);
			contents.add(A);
			contents.add(B);
			contents.add(C);
			contents.add(new ConnectionContent(A, B));
			contents.add(new ConnectionContent(B, C));
			return contents;
		}

		public static List<Object> getDA_click_error() {
			final Polygon startAnchorage = new Polygon(349.49988, 56.54434506500246, 359.9554149349976,
					66.99988000000002, 349.49988, 77.45541493499758, 339.04434506500246, 66.99988000000002, 349.49988,
					56.54434506500246);
			final Polygon endAnchorage = new Polygon(338.49988, 237.54434506500243, 348.9554149349976, 247.99988,
					338.49988, 258.4554149349975, 328.04434506500246, 247.99988, 338.49988, 237.54434506500243);
			final Point startReferencePoint = new Point(356.25, -365.87);
			final Point endReferencePoint = new Point(350.01, -190.0);
			final List<Object> contents = new ArrayList<>();
			contents.add(startAnchorage);
			contents.add(endAnchorage);
			final ConnectionContent connectionContent = new ConnectionContent(startAnchorage, endAnchorage,
					startReferencePoint, endReferencePoint);
			connectionContent.isSimple = true;
			contents.add(connectionContent);
			return contents;
		}
	}

	private static class TestModule extends MvcFxModule {

		@Override
		protected void bindAbstractContentPartAdapters(final MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			super.bindAbstractContentPartAdapters(adapterMapBinder);
			// focus and select on click
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
		}

		protected void bindAnchorageAdapters(final MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
			// transform policy
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);
			// relocate on drag
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
			// bind dynamic anchor provider
			adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TestAnchorProvider.class);
		}

		protected void bindConnectionAdapters(final MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
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
	 * Returns the index within the Connection's anchors for the given explicit
	 * anchor index.
	 *
	 * @param connection
	 *            The {@link Connection} for which to determine the anchor index
	 *            matching the given explicit index.
	 * @param explicitAnchorIndex
	 *            The explicit anchor index for which to return the connection
	 *            index.
	 * @return The connection's anchor index for the given explicit anchor
	 *         index.
	 */
	// FIXME: Duplicate code (see
	// FXBendConnectionOperation#getConnectionIndex(int)). Find a better place
	// for this functionality (perhaps within Connection or IConnectionRouter).
	public static int getConnectionIndex(final Connection connection, final int explicitAnchorIndex) {
		int explicitCount = -1;

		for (int i = 0; i < connection.getPointsUnmodifiable().size(); i++) {
			final IAnchor a = connection.getAnchor(i);
			if (!connection.getRouter().wasInserted(a)) {
				explicitCount++;
			}
			if (explicitCount == explicitAnchorIndex) {
				// found all operation indices
				return i;
			}
		}

		throw new IllegalArgumentException(
				"Cannot determine connection index for operation index " + explicitAnchorIndex + ".");
	}

	private static Point getPosition(final FXBendConnectionPolicy bendPolicy, final int explicitIndex) {
		return bendPolicy.getHost().getVisual()
				.getPoint(getConnectionIndex(bendPolicy.getHost().getVisual(), explicitIndex));
	}

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	@Inject
	private FXDomain domain;

	public int countExplicit(final Connection connection) {
		int numExplicit = 0;
		for (final IAnchor anchor : connection.getAnchorsUnmodifiable()) {
			if (!connection.getRouter().wasInserted(anchor)) {
				numExplicit++;
			}
		}
		return numExplicit;
	}

	private FXViewer createViewer(final List<Object> contents) throws Throwable {
		// create injector (adjust module bindings for test)
		final Injector injector = Guice.createInjector(new TestModule());

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

		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				System.err.println("CANVAS: " + viewer.getCanvas().getWidth() + " x " + viewer.getCanvas().getHeight());
			}
		});

		// set contents on JavaFX application thread (visuals are created)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getAdapter(ContentModel.class).getContents().setAll(contents);
			}
		});

		// check that the parts have been created
		for (final Object content : contents) {
			assertTrue(viewer.getContentPartMap().containsKey(content));
		}
		return viewer;
	}

	private void equalsUnprecise(final Point p, final Point q) {
		assertEquals(p + " and " + q + " are not (unprecisely) equal but differ in x: ", p.x, q.x, 0.5);
		assertEquals(p + " and " + q + " are not (unprecisely) equal but differ in y: ", p.y, q.y, 0.5);
	}

	@Test
	public void test_create_orthogonal_segment_from_implicit_connected() throws Throwable {
		final List<Object> contents = TestModels.getAB_offset2_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				// XXX: The strategies are exchanged before setting the router
				// so that a refresh will use these strategies
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy() {
							@Override
							public Point computePositionInScene(final Node anchorage, final Node anchored,
									final Set<Parameter<?>> parameters) {
								// ensure routing starts going to the right
								return new Point(49, 25);
							}
						});
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy() {
							@Override
							public Point computePositionInScene(final Node anchorage, final Node anchored,
									final Set<Parameter<?>> parameters) {
								// ensure routing ends going to the right
								return new Point(301, 525);
							}
						});
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// check if router inserted implicit points
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());

		// create new segment between 2nd implicit and end
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});

		// determine segment indices for neighbor anchors
		final int firstSegmentIndex = 2;
		final int secondSegmentIndex = 3;

		// determine middle of segment
		final Point firstPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getPoint(firstSegmentIndex);
			}
		});
		final Point secondPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getPoint(secondSegmentIndex);
			}
		});

		// check that segment to be selected is horizontal
		assertEquals(firstPoint.y, secondPoint.y, 0.0001);

		final Vector direction = new Vector(firstPoint, secondPoint);
		final Point midPoint = firstPoint.getTranslated(direction.x / 2, direction.y / 2);

		final Point2D midInScene = ctx.runAndWait(new RunnableWithResult<Point2D>() {
			@Override
			public Point2D run() {
				return connection.getVisual().localToScene(midPoint.x, midPoint.y);
			}
		});

		// determine connectedness of first anchor handle
		final Node firstAnchorage = ctx.runAndWait(new RunnableWithResult<Node>() {
			@Override
			public Node run() {
				return connection.getVisual().getAnchor(firstSegmentIndex).getAnchorage();
			}
		});
		final boolean isFirstConnected = firstAnchorage != null && firstAnchorage != connection.getVisual();

		// make the anchor handles explicit
		final List<Integer> explicit = ctx.runAndWait(new RunnableWithResult<List<Integer>>() {
			@Override
			public List<Integer> run() {
				return bendPolicy.makeExplicit(firstSegmentIndex - 1, secondSegmentIndex);
			}
		});
		int firstAnchorIndex = explicit.get(1);
		final Point firstAnchorHandleInitialPosition = ctx.runAndWait(new RunnableWithResultAndParam<Point, Integer>() {
			@Override
			public Point run(final Integer firstAnchorIndex) {
				return getPosition(bendPolicy, firstAnchorIndex);
			}
		}, firstAnchorIndex);
		int secondAnchorIndex = explicit.get(2);
		assertEquals(4, (int) ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return connection.getVisual().getPointsUnmodifiable().size();
			}
		}));

		// copy first point if connected
		if (isFirstConnected) {
			// use the copy as the new first anchor handle
			firstAnchorIndex = ctx.runAndWait(new RunnableWithResultAndParam<Integer, Integer>() {
				@Override
				public Integer run(final Integer firstAnchorIndex) {
					return bendPolicy.createAfter(firstAnchorIndex, FX2Geometry.toPoint(connection.getVisual()
							.localToScene(Geometry2FX.toFXPoint(firstAnchorHandleInitialPosition))));
				}
			}, firstAnchorIndex);
		}

		// create new anchor at the segment's middle
		secondAnchorIndex = ctx.runAndWait(new RunnableWithResultAndParam<Integer, Integer>() {
			@Override
			public Integer run(final Integer firstAnchorIndex) {
				return bendPolicy.createAfter(firstAnchorIndex, FX2Geometry.toPoint(midInScene));
			}
		}, firstAnchorIndex);
		// copy that new anchor
		secondAnchorIndex = ctx.runAndWait(new RunnableWithResultAndParam<Integer, Integer>() {
			@Override
			public Integer run(final Integer firstAnchorIndex) {
				return bendPolicy.createAfter(firstAnchorIndex, FX2Geometry.toPoint(midInScene));
			}
		}, firstAnchorIndex);

		// check to be selected segment is horizontal
		assertEquals(ctx.runAndWait(new RunnableWithResultAndParam<Point, Integer>() {
			@Override
			public Point run(final Integer firstAnchorIndex) {
				return getPosition(bendPolicy, firstAnchorIndex);
			}
		}, firstAnchorIndex).y, ctx.runAndWait(new RunnableWithResultAndParam<Point, Integer>() {
			@Override
			public Point run(final Integer secondAnchorIndex) {
				return getPosition(bendPolicy, secondAnchorIndex);
			}
		}, secondAnchorIndex).y, 0.0001);

		// select the first anchor and the copy of the new mid anchor for
		// movement
		{
			final int fai = firstAnchorIndex;
			final int sai = secondAnchorIndex;
			ctx.runAndWait(new Runnable() {
				@Override
				public void run() {
					bendPolicy.select(fai);
					bendPolicy.select(sai);
				}
			});
		}
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment up
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -50));
			}
		});
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment further up
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -100));
			}
		});
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment further up
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -150));
			}
		});
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment down a bit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -120));
			}
		});
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment down a bit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -60));
			}
		});
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move new segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());

		// commit (i.e. normalize)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());
	}

	@Test
	public void test_end_overlays_way_restore() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// find way point anchor
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int wayPointAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// select end point
		final int endAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(2);
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(endAnchorHandle);
			}
		});

		// move left to overlay the way point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(),
						new Point(-wayPoint.getDistance(getPosition(bendPolicy, endAnchorHandle)), 0));
			}
		});

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// move back to restore the overlain anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});

		// verify point is present again
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

	@Test
	public void test_move_connected_orthogonal_segment_down() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// copy start point and end point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorIndex, startPoint);
			}
		});
		final int secondWayAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorIndex, endPoint);
			}
		});
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment down by 100 to create 2 new segments
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorIndex);
				bendPolicy.select(secondWayAnchorIndex);
				bendPolicy.move(new Point(), new Point(0, 100));
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(endPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
	}

	@Test
	public void test_move_connected_orthogonal_segment_down_translated() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// copy start point and end point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		assertEquals(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startAnchorHandle);
			}
		}));
		final Point firstWayPoint = startPoint.getCopy();
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
			}
		});
		assertEquals(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startAnchorHandle);
			}
		}));
		final Point secondWayPoint = endPoint.getCopy();
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
			}
		});
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));
		// check coordinates
		assertEquals(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startAnchorHandle);
			}
		}));
		assertEquals(firstWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, firstWayAnchorHandle);
			}
		}));
		assertEquals(secondWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, secondWayAnchorHandle);
			}
		}));
		assertEquals(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment down by 100 to create 2 new segments
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorHandle);
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, 100));
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(firstWayPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
	}

	@Test
	public void test_move_connected_orthogonal_segment_restore() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// copy start point and end point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		final int startAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorIndex, startPoint);
			}
		});
		final int secondWayAnchorIndex = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorIndex, endPoint);
			}
		});
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));

		// move segment down by 100 to create 2 new segments
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorIndex);
				bendPolicy.select(secondWayAnchorIndex);
				bendPolicy.move(new Point(), new Point(0, 100));
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(endPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(startPoint.getTranslated(0, 25), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint.getTranslated(0, 25), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment back
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});
		// check number of points
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// check number of points after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());
	}

	@Test
	public void test_move_connected_orthogonal_segment_up() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// copy start point and end point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, startPoint);
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, endPoint);
			}
		});
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment down by 100 to create 2 new segments
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorHandle);
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, -100));
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint.getTranslated(0, -100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(endPoint.getTranslated(0, -100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
	}

	@Test
	public void test_move_explicit_orthogonal_segment_overlay() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y);
		final Point secondWayPoint = new Point(startPoint.x + 100, startPoint.y + 100);
		final Point thirdWayPoint = new Point(startPoint.x + 200, startPoint.y + 100);
		final Point fourthWayPoint = new Point(startPoint.x + 200, startPoint.y);
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
			}
		});
		final int thirdWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(secondWayAnchorHandle, thirdWayPoint);
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(thirdWayAnchorHandle, fourthWayPoint);
			}
		});
		// check number of points
		assertEquals(6, countExplicit(connection.getVisual()));

		// move segment up to create an overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.select(thirdWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, -100));
			}
		});
		// check number of points and their positions
		assertEquals(2, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment further up to restore the removed points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -200));
			}
		});
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint.getTranslated(0, -200), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(thirdWayPoint.getTranslated(0, -200), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(thirdWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
	}

	@Test
	public void test_move_explicit_orthogonal_segment_overlay_side() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y);
		final Point secondWayPoint = new Point(startPoint.x + 100, startPoint.y + 200);
		final Point thirdWayPoint = new Point(startPoint.x + 200, startPoint.y + 200);
		final Point fourthWayPoint = new Point(startPoint.x + 200, startPoint.y + 100);
		final Point fifthWayPoint = new Point(startPoint.x + 300, startPoint.y + 100);
		final Point sixthWayPoint = new Point(startPoint.x + 300, startPoint.y);
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
			}
		});
		final int thirdWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(secondWayAnchorHandle, thirdWayPoint);
			}
		});
		final int fourthWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(thirdWayAnchorHandle, fourthWayPoint);
			}
		});
		final int fifthWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(fourthWayAnchorHandle, fifthWayPoint);
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(fifthWayAnchorHandle, sixthWayPoint);
			}
		});
		// check number of points
		assertEquals(8, countExplicit(connection.getVisual()));

		// move segment to the right to create a 3 segment overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorHandle);
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(100, 0));
			}
		});
		// check number of points and their positions
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(fifthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(sixthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment back to the left to restore the original positions
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});
		assertEquals(8, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(thirdWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(fifthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(4);
			}
		}));
		equalsUnprecise(sixthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(5);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment to the right to create an unprecise 3 segment overlay
		// (default threshold of 10px)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(95, 0));
			}
		});
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(fifthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(sixthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// check if the overlay is still removed after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(6, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint.getTranslated(100, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(fourthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(fifthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(2);
			}
		}));
		equalsUnprecise(sixthWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(3);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
	}

	@Test
	public void test_move_explicit_orthogonal_segment_simple() throws Throwable {
		final List<Object> contents = TestModels.getAB_offset_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// query start point and end point so that we can construct orthogonal
		// control points
		Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final Point firstWayPoint = new Point(startPoint.x + 100, startPoint.y);
		final Point secondWayPoint = new Point(startPoint.x + 100, endPoint.y);
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, firstWayPoint);
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, secondWayPoint);
			}
		});
		// start point and end point changed due to the new control points
		startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});
		// check number of points
		assertEquals(4, countExplicit(connection.getVisual()));

		// move segment to the left (no overlay)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayAnchorHandle);
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(-10, 0));
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint.getTranslated(-10, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint.getTranslated(-10, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment to the right (no overlay)
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(10, 0));
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint.getTranslated(10, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint.getTranslated(10, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(4, countExplicit(connection.getVisual()));
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(firstWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(0);
			}
		}));
		equalsUnprecise(secondWayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getControlPoint(1);
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
	}

	@Test
	public void test_move_segment_connected_overlay() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB_simple();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});

		// find start and end handles
		final int startHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int endHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(1);
			}
		});

		assertEquals(ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startHandle);
			}
		}));
		assertEquals(ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, endHandle);
			}
		}));

		// copy both connected end points
		final int leftCopy = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startHandle, getPosition(bendPolicy, startHandle));
			}
		});
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startHandle);
			}
		}), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, leftCopy);
			}
		}));

		final int rightCopy = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createBefore(endHandle, getPosition(bendPolicy, endHandle));
			}
		});
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals(getPosition(bendPolicy, endHandle), getPosition(bendPolicy, rightCopy));
			}
		});

		// select the copies for movement
		// and move down by 100
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(leftCopy);
				bendPolicy.select(rightCopy);
				bendPolicy.move(new Point(), new Point(0, 100));
			}
		});

		// check if points are correct
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());

		// move back to get a double overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});

		// check if points have been removed
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());
	}

	@Test
	public void test_move_single_explicit_anchor() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		// find way point anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int anchorBackwards = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});
		final int anchorForwards = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(1);
			}
		});
		// assertEquals(1, anchorBackwards.getExplicitAnchorIndex());
		assertEquals(anchorBackwards, anchorForwards);

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, anchorBackwards);
			}
		}));
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// select anchor
		// and move down by 100
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(anchorBackwards);
				bendPolicy.move(new Point(0, 0), new Point(0, 100));
			}
		});

		assertEquals(wayPoint.getTranslated(0, 100), connection.getVisual().getPoint(1));
		assertEquals(wayPoint.getTranslated(0, 100), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, anchorBackwards);
			}
		}));

		// verify position after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(wayPoint.getTranslated(0, 100), connection.getVisual().getPoint(1));
	}

	@Test
	public void test_overlay_segment_left_first() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 200));
			}
		});
		final int thirdWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 210));
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
			}
		});
		// check if points are correct
		assertEquals(7, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 210), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that only the second way anchor overlays the first
		// way anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.select(thirdWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, -95));
			}
		});
		// check if the overlaying was removed
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move segment so that also the third way anchor overlays the fourth
		// way anchor, i.e. it is a double overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -105));
			}
		});
		// check if the overlaying anchors were removed
		assertEquals(5, connection.getVisual().getPointsUnmodifiable().size());
		// check that the overlain anchors have the same position as before
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(2));

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});
		// check that all anchors have been restored
		assertEquals(7, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 210), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));
	}

	@Test
	public void test_overlay_segment_right_first() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 210));
			}
		});
		final int thirdWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 200));
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
			}
		});
		// check if points are correct
		assertEquals(7, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 210), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that only the second way anchor overlays the first
		// way anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.select(thirdWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, -95));
			}
		});
		// check if the overlaying was removed
		assertEquals(6, connection.getVisual().getPointsUnmodifiable().size());

		// move segment so that also the third way anchor overlays the fourth
		// way anchor, i.e. it is a double overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, -105));
			}
		});
		// check if the overlaying anchors were removed
		assertEquals(5, connection.getVisual().getPointsUnmodifiable().size());
		// check that the overlain anchors have the same position as before
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(2));

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});
		// check that all anchors have been restored
		assertEquals(7, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 210), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));
	}

	@Test
	public void test_overlay_segment_simple() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// create control points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(0);
			}
		});
		final int firstWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(startAnchorHandle, new Point(100, 100));
			}
		});
		final int secondWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(firstWayAnchorHandle, new Point(100, 200));
			}
		});
		final int thirdWayAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.createAfter(secondWayAnchorHandle, new Point(200, 200));
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(thirdWayAnchorHandle, new Point(200, 100));
			}
		});
		// check if points are correct
		assertEquals(7, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(new Point(100, 100), connection.getVisual().getPoint(1));
		assertEquals(new Point(100, 200), connection.getVisual().getPoint(2));
		assertEquals(new Point(200, 200), connection.getVisual().getPoint(3));
		assertEquals(new Point(200, 100), connection.getVisual().getPoint(4));

		// move segment so that we get a double overlay
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(secondWayAnchorHandle);
				bendPolicy.select(thirdWayAnchorHandle);
				bendPolicy.move(new Point(), new Point(0, -100));
				bendPolicy.commit();
			}
		});
		// check if points have been removed
		assertEquals(5, connection.getVisual().getPointsUnmodifiable().size());
	}

	@Test
	public void test_overlay_single() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		// find way point anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int anchorBackwards = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});
		final int anchorForwards = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(1);
			}
		});
		// assertEquals(1, anchorBackwards.getExplicitAnchorIndex());
		assertEquals(anchorBackwards, anchorForwards);
		final int firstWayPointAnchorHandle = anchorBackwards;

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, anchorBackwards);
			}
		}));
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// create way point 20 px to the right of the existing one
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.createAfter(firstWayPointAnchorHandle, wayPoint.getTranslated(20, 0));
			}
		});
		// verify that the point is inserted
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());

		// select first way point
		// and move right by 20
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(firstWayPointAnchorHandle);
				bendPolicy.move(new Point(), new Point(20, 0));
			}
		});

		// verify that the point is removed
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// verify point is removed after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
	}

	@Test
	public void test_regression_makeExplicit() throws Throwable {
		final List<Object> contents = TestModels.get_regression_makeExplicit();
		final FXViewer viewer = createViewer(contents);

		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);

		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter() {
					@Override
					protected void updateComputationParameters(final Connection connection, final int index) {
						if (index == 0) {
							getComputationParameter(connection, index, PreferredOrientation.class)
									.set(Orientation.HORIZONTAL);
							getComputationParameter(connection, index, AnchoredReferencePoint.class)
									.set(NodeUtils.parentToLocal(connection.getCurve(), new Point(310, 40)));
						} else if (index == connection.getPointsUnmodifiable().size() - 1) {
							getComputationParameter(connection, index, PreferredOrientation.class)
									.set(Orientation.HORIZONTAL);
							getComputationParameter(connection, index, AnchoredReferencePoint.class)
									.set(NodeUtils.parentToLocal(connection.getCurve(), new Point(50, 95)));
						} else {
							super.updateComputationParameters(connection, index);
						}
					}
				});
			}
		});

		// verify router inserted two control points
		assertEquals(2, countExplicit(connection.getVisual()));
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());

		// verify control points share X coordinate
		assertEquals(connection.getVisual().getPoint(1).x, connection.getVisual().getPoint(2).x, 0.5);

		// query start point and end point so that we can construct orthogonal
		// control points
		final Point startPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		});
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		});

		// select first segment for manipulation
		// and move down to endPoint height
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
				bendPolicy.selectSegment(0);
				bendPolicy.move(new Point(), new Point(0, endPoint.y - startPoint.y));
			}
		});

		assertEquals(3, countExplicit(connection.getVisual()));
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
		equalsUnprecise(new Point(startPoint.x, endPoint.y), connection.getVisual().getPoint(1));

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});
		// check number of points and their positions
		assertEquals(3, countExplicit(connection.getVisual()));
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));

		// move down to endPoint height - 5 so that it will snap with the two
		// implicit points
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, endPoint.y - startPoint.y - 5));
			}
		});
		assertEquals(3, countExplicit(connection.getVisual()));
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
		equalsUnprecise(new Point(startPoint.x, endPoint.y), connection.getVisual().getPoint(1));

		// move further down so that the segment is restored
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(0, endPoint.y - startPoint.y + 15));
			}
		});
		assertEquals(4, countExplicit(connection.getVisual()));
		assertEquals(5, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));

		// check that segment is "unsnapped", i.e. end point is still on initial
		// y coordinate
		final Point endPositionHint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPointHint();
			}
		});
		assertEquals(connection.getVisual().getEndPoint().y, endPositionHint.y, 0.5);
		// TODO: Ensure position hints are correctly restored.

		// move segment back to its original position
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});
		// check number of points and their positions
		assertEquals(3, countExplicit(connection.getVisual()));
		assertEquals(4, connection.getVisual().getPointsUnmodifiable().size());
		equalsUnprecise(startPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getStartPoint();
			}
		}));
		equalsUnprecise(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return connection.getVisual().getEndPoint();
			}
		}));
	}

	@Test
	public void test_relocateAnchor() throws Throwable {
		final List<Object> contents = TestModels.getABC_AB_BC();
		final FXViewer viewer = createViewer(contents);

		// save initial start point of second connection
		final ConnectionPart secondConnectionPart = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		final Point secondConnectionStart = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				((GeometryNode<?>) secondConnectionPart.getVisual().getCurve()).setStrokeWidth(5);
				return secondConnectionPart.getVisual().getStartPoint();
			}
		});

		// move mouse to first connection
		final ConnectionPart firstConnectionPart = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 2));

		final Point firstConnectionMid = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				((GeometryNode<?>) firstConnectionPart.getVisual().getCurve()).setStrokeWidth(5);
				return firstConnectionPart.getVisual().getStartPoint().getTranslated(firstConnectionPart.getVisual()
						.getStartPoint().getDifference(firstConnectionPart.getVisual().getPoint(1)).getScaled(0.5));
			}
		});
		final EventHandler<MouseEvent> mouseEventFilter = new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				System.err.println("FILTER: " + event);
			}
		};
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getCanvas().getScene().addEventFilter(MouseEvent.ANY, mouseEventFilter);
			}
		});
		ctx.moveTo(firstConnectionPart.getVisual(), firstConnectionMid.x, firstConnectionMid.y);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				viewer.getCanvas().getScene().removeEventFilter(MouseEvent.ANY, mouseEventFilter);
			}
		});

		// drag connection down by 10px
		ctx.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);

		Point cursorLocation = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return CursorUtils.getPointerLocation();
			}
		});
		ctx.mouseDrag((int) cursorLocation.x, (int) cursorLocation.y + 10);
		ctx.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);

		// check the connection is selected
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertTrue(viewer.getAdapter(SelectionModel.class).getSelectionUnmodifiable()
						.contains(firstConnectionPart));
			}
		});

		// move mouse to second anchorage
		final AnchoragePart secondPart = (AnchoragePart) viewer.getContentPartMap().get(contents.get(1));
		final AtomicReference<Point> centerRef = new AtomicReference<>(null);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				centerRef.set(((org.eclipse.gef4.geometry.planar.Rectangle) secondPart.getContent()).getCenter());
			}
		});
		final Point center = centerRef.get();
		ctx.moveTo(secondPart.getVisual(), center.x, center.y);

		// drag anchorage down by 10px
		ctx.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
		cursorLocation = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return CursorUtils.getPointerLocation();
			}
		});
		ctx.mouseDrag((int) cursorLocation.x, (int) cursorLocation.y + 10);
		ctx.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);

		// check the anchorage is selected
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertTrue(viewer.getAdapter(SelectionModel.class).getSelectionUnmodifiable().contains(secondPart));
			}
		});

		// check the second connection was moved too
		assertNotEquals(secondConnectionStart, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return secondConnectionPart.getVisual().getStartPoint();
			}
		}));
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
	 * 56.54434506500246) -&gt; (359.9554149349976, 66.99988000000002) -&gt;
	 * (349.49988, 77.45541493499758) -&gt; (339.04434506500246,
	 * 66.99988000000002) -&gt; (349.49988, 56.54434506500246)
	 * <li>DA anchor key = AnchorKey &lt;start&gt;
	 * &lt;GeometryNode@333e01c6[styleClass=curve]&gt;
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
	 * 237.54434506500243) -&gt; (348.9554149349976, 247.99988) -&gt;
	 * (338.49988, 258.4554149349975) -&gt; (328.04434506500246, 247.99988)
	 * -&gt; (338.49988, 237.54434506500243)
	 * <li>DA anchor key = AnchorKey &lt;end&gt;
	 * &lt;GeometryNode@333e01c6[styleClass=curve]&gt;
	 * <li>DA anchored reference point = Point(350.01, -190.0)
	 * </ul>
	 * </ol>
	 *
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws AWTException
	 */
	@Test
	public void test_segment_select_error_split_segment() throws Throwable {
		final List<Object> contents = TestModels.getDA_click_error();
		final FXViewer viewer = createViewer(contents);
		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		final Point controlPoint = new Point(356.25, 237.54434204101562);

		// setup connection to be orthogonal, i.e. use orthogonal router and
		// use orthogonal projection strategy at the anchorages
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				connection.getVisual().addControlPoint(0, controlPoint);
				((DynamicAnchor) connection.getVisual().getStartAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				((DynamicAnchor) connection.getVisual().getEndAnchor())
						.setComputationStrategy(new OrthogonalProjectionStrategy());
				connection.getVisual().setRouter(new OrthogonalRouter());
			}
		});

		// ensure router inserted point
		assertEquals(3, countExplicit(connection.getVisual()));

		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
				bendPolicy.selectSegment(0);
				bendPolicy.move(new Point(), new Point());
				bendPolicy.commit();
			}
		});

		assertEquals(3, countExplicit(connection.getVisual()));
	}

	@Test
	public void test_start_overlays_way_restore() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);
		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// find way point anchor
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int wayPointAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// select end point
		final int startAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(0);
			}
		});
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(startAnchorHandle);
			}
		});

		// move to the right to overlay the way point
		final double distance = wayPoint.getDistance(ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, startAnchorHandle);
			}
		}));
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(distance, 0));
			}
		});

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// move back to restore the overlain anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});

		// verify point is present again
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

	@Test
	public void test_way_overlays_end_remove() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);
		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// find way point anchor
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int wayPointAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// select way point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(wayPointAnchorHandle);
			}
		});

		// find end point handle
		final int endPointHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(2);
			}
		});

		// move to the right to overlay the end point, but not exactly onto it
		final Point endPoint = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, endPointHandle);
			}
		});
		final double distance = wayPoint.getDistance(endPoint) - 5;
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(distance, 0));
			}
		});
		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// verify that the end point is still at the same location
		assertEquals(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, endPointHandle - 1);
			}
		}));

		// verify point is removed after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(endPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, endPointHandle - 1);
			}
		}));
	}

	@Test
	public void test_way_overlays_end_restore() throws Throwable {
		final List<Object> contents = TestModels.getAB_AB();
		final FXViewer viewer = createViewer(contents);
		// query bend policy for first connection
		final ConnectionPart connection = (ConnectionPart) viewer.getContentPartMap()
				.get(contents.get(contents.size() - 1));

		// verify that way point is present
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());

		// find way point anchor
		final FXBendConnectionPolicy bendPolicy = connection.getAdapter(FXBendConnectionPolicy.class);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.init();
			}
		});
		final Point wayPoint = connection.getContent().getWayPoint();
		final int wayPointAnchorHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrBefore(1);
			}
		});

		// check anchor position
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// select way point
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.select(wayPointAnchorHandle);
			}
		});

		// find end point handle
		final int endPointHandle = ctx.runAndWait(new RunnableWithResult<Integer>() {
			@Override
			public Integer run() {
				return bendPolicy.getExplicitIndexAtOrAfter(2);
			}
		});

		// move to the right to overlay the end point
		final double distance = wayPoint.getDistance(ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, endPointHandle);
			}
		}));
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point(distance, 0));
			}
		});
		assertEquals(wayPoint.getTranslated(distance, 0), ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// verify that the point is removed
		assertEquals(2, connection.getVisual().getPointsUnmodifiable().size());

		// move back to the left to restore the overlain anchor
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.move(new Point(), new Point());
			}
		});
		assertEquals(wayPoint, ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				return getPosition(bendPolicy, wayPointAnchorHandle);
			}
		}));

		// verify point is present again
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));

		// verify point is present after commit
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				bendPolicy.commit();
			}
		});
		assertEquals(3, connection.getVisual().getPointsUnmodifiable().size());
		assertEquals(wayPoint, connection.getVisual().getPoint(1));
	}

}
