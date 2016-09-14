/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - Fixes related to bug #437076
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.mvc.models.HoverModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractTransformPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXBendFirstAnchorageOnSegmentHandleDragPolicy} is an
 * {@link IFXOnDragPolicy} that can be installed on the handle parts of an
 * {@link Connection}, so that the user is able to manipulate that connection by
 * dragging its handles. This policy expects that a handle is created for each
 * anchor point of the connection (start, way, end), as well as for each middle
 * point of a segment. Moreover, this policy expects that the respective handles
 * are of type {@link FXCircleSegmentHandlePart}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: this is only applicable to FXSegmentHandlePart hosts
public class FXBendFirstAnchorageOnSegmentHandleDragPolicy
		extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);

	private IVisualPart<Node, ? extends Connection> targetPart;

	private boolean isSegmentDragged;

	private Point initialMouseInScene;
	private Point handlePositionInScene;

	private int initialSegmentIndex;
	private double initialSegmentParameter;

	private boolean isInvalid = false;

	private Comparator<IHandlePart<Node, ? extends Node>> handleDistanceComparator = new Comparator<IHandlePart<Node, ? extends Node>>() {
		@Override
		public int compare(IHandlePart<Node, ? extends Node> interactedWith,
				IHandlePart<Node, ? extends Node> other) {
			Bounds otherBounds = other.getVisual().getLayoutBounds();
			Point2D otherPosition = other.getVisual().localToScene(
					otherBounds.getMinX() + otherBounds.getWidth() / 2,
					otherBounds.getMinY() + otherBounds.getHeight() / 2);
			// only useful to find the most similar part
			return (int) (handlePositionInScene
					.getDistance(FX2Geometry.toPoint(otherPosition)) * 10);
		}
	};

	private boolean isPrepared;

	@Override
	public void abortDrag() {
		if (isInvalid) {
			return;
		}
		restoreRefreshVisuals(targetPart);
		rollback(getBendPolicy(targetPart));
		updateHandles();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		// prepare upon first drag
		if (!isPrepared) {
			isPrepared = true;
			prepareBend(e.isShiftDown(), getBendPolicy(targetPart));
		}

		Connection connection = targetPart.getVisual();

		boolean isOrthogonal = isSegmentDragged
				&& connection.getRouter() instanceof OrthogonalRouter;
		boolean isHorizontal = isOrthogonal
				&& getBendPolicy(targetPart).isSelectionHorizontal();

		IViewer<Node> viewer = getHost().getRoot().getViewer();
		Node gridLocalVisual = viewer instanceof FXViewer
				? ((FXViewer) viewer).getCanvas().getContentGroup()
				: targetPart.getVisual().getParent();
		Point newEndPointInScene = AbstractTransformPolicy.snapToGrid(
				targetPart.getVisual(), e.getSceneX(), e.getSceneY(),
				viewer.<GridModel> getAdapter(GridModel.class),
				getSnapToGridGranularityX(), getSnapToGridGranularityY(),
				gridLocalVisual);
		getBendPolicy(targetPart).move(initialMouseInScene, newEndPointInScene);

		if (isOrthogonal) {
			if (isHorizontal) {
				// can only move vertically
				handlePositionInScene.setY(newEndPointInScene.y);
			} else {
				// can only move horizontally
				handlePositionInScene.setX(newEndPointInScene.x);
			}
		} else {
			handlePositionInScene.setX(newEndPointInScene.x);
			handlePositionInScene.setY(newEndPointInScene.y);
		}

		updateHandles();
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}
		commit(getBendPolicy(targetPart));
		restoreRefreshVisuals(targetPart);

		updateHandles();
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The {@link IVisualPart} of which the installed
	 *            {@link FXBendConnectionPolicy} is returned.
	 * @return The {@link FXBendConnectionPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
	protected FXBendConnectionPolicy getBendPolicy(
			IVisualPart<Node, ? extends Node> targetPart) {
		// retrieve the default bend policy
		return targetPart.getAdapter(FXBendConnectionPolicy.class);
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@Override
	public AbstractFXSegmentHandlePart<? extends Node> getHost() {
		return (AbstractFXSegmentHandlePart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy. Per default the
	 * first anchorage is returned, which has to be an {@link IVisualPart} with
	 * an {@link Connection} visual.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	@SuppressWarnings("unchecked")
	protected IVisualPart<Node, ? extends Connection> getTargetPart() {
		return (IVisualPart<Node, ? extends Connection>) getHost()
				.getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * bend, <code>false</code> otherwise. Otherwise returns <code>false</code>
	 * . By default will always return <code>true</code>.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         bend, otherwise <code>false</code>.
	 */
	protected boolean isBend(MouseEvent event) {
		return true;
	}

	/**
	 * Prepares the given {@link FXBendConnectionPolicy} for the manipulation of
	 * its host part.
	 *
	 * @param isShiftDown
	 *            <code>true</code> if shift is pressed, otherwise
	 *            <code>false</code>.
	 * @param bendPolicy
	 *            {@link FXBendConnectionPolicy} of the target part.
	 */
	private void prepareBend(boolean isShiftDown,
			FXBendConnectionPolicy bendPolicy) {
		AbstractFXSegmentHandlePart<? extends Node> host = getHost();
		if (host.getSegmentParameter() == 0.5) {
			if (isShiftDown || targetPart.getVisual()
					.getRouter() instanceof OrthogonalRouter) {
				// move segment, copy ends when connected
				bendPolicy.selectSegment(host.getSegmentIndex());
				isSegmentDragged = true;
			} else {
				// create new way point in middle and move it (disabled for
				// orthogonal connections)

				Integer previousAnchorHandle = bendPolicy
						.getExplicitIndexAtOrBefore(host.getSegmentIndex());
				Integer newAnchorHandle = bendPolicy
						.createAfter(previousAnchorHandle, initialMouseInScene);

				// select for manipulation
				bendPolicy.select(newAnchorHandle);
			}
		} else if (host.getSegmentParameter() == 0.25
				|| host.getSegmentParameter() == 0.75) {
			// split segment
			isSegmentDragged = true;
			boolean selectFirstHalve = host.getSegmentParameter() == 0.25;

			// determine segment indices for neighbor anchors
			int firstSegmentIndex = host.getSegmentIndex();
			int secondSegmentIndex = host.getSegmentIndex() + 1;

			// determine middle of segment
			Point firstPoint = targetPart.getVisual()
					.getPoint(firstSegmentIndex);
			Point secondPoint = targetPart.getVisual()
					.getPoint(secondSegmentIndex);
			Vector direction = new Vector(firstPoint, secondPoint);
			Point midPoint = firstPoint.getTranslated(direction.x / 2,
					direction.y / 2);
			Point2D midInScene = targetPart.getVisual().localToScene(midPoint.x,
					midPoint.y);

			// determine connected status of start or end point (depending on
			// which side of the segment is moved after splitting)
			Node connectedAnchorage = targetPart.getVisual().getAnchor(
					selectFirstHalve ? firstSegmentIndex : secondSegmentIndex)
					.getAnchorage();
			boolean isConnected = connectedAnchorage != null
					&& connectedAnchorage != targetPart.getVisual();

			// make the anchors at the segment indices explicit
			List<Integer> explicit = bendPolicy.makeExplicit(firstSegmentIndex,
					secondSegmentIndex);
			Integer firstAnchorHandle = explicit.get(0);
			Integer secondAnchorHandle = explicit.get(1);

			// copy start/end if it is connected so that the copy can be
			// selected for movement
			if (isConnected) {
				// compute connection index for point to copy
				int connectionIndex = bendPolicy.getBendOperation()
						.getConnectionIndex(selectFirstHalve ? firstAnchorHandle
								: secondAnchorHandle);
				// determine position in scene for point to copy
				Point positionInScene = FX2Geometry
						.toPoint(targetPart.getVisual().localToScene(
								Geometry2FX.toFXPoint(bendPolicy.getConnection()
										.getPoint(connectionIndex))));
				// copy the anchor
				if (selectFirstHalve) {
					firstAnchorHandle = bendPolicy
							.createAfter(firstAnchorHandle, positionInScene);
				} else {
					secondAnchorHandle = bendPolicy
							.createBefore(secondAnchorHandle, positionInScene);
				}
			}

			// create new anchor at segment's middle and copy that new anchor so
			// that the copy can be selected for movement
			if (selectFirstHalve) {
				secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
			} else {
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				// increment second anchor handle because we added 2 points
				// before that
				secondAnchorHandle += 2;
			}

			// select the anchors for movement
			bendPolicy.select(firstAnchorHandle);
			bendPolicy.select(secondAnchorHandle);
		} else {
			// compute connection index from handle part data
			int connectionIndex = host.getSegmentIndex()
					+ (host.getSegmentParameter() == 1 ? 1 : 0);

			// make anchor explicit if it is implicit
			bendPolicy.select(bendPolicy
					.makeExplicit(connectionIndex, connectionIndex).get(0));
		}
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	@Override
	public void startDrag(MouseEvent e) {
		isInvalid = !isBend(e);
		if (isInvalid) {
			return;
		}

		isPrepared = false;
		isSegmentDragged = false;
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());
		handlePositionInScene = initialMouseInScene.getCopy();
		AbstractFXSegmentHandlePart<? extends Node> hostPart = getHost();
		initialSegmentIndex = hostPart.getSegmentIndex();
		initialSegmentParameter = hostPart.getSegmentParameter();
		targetPart = getTargetPart();

		storeAndDisableRefreshVisuals(targetPart);
		FXBendConnectionPolicy bendPolicy = getBendPolicy(targetPart);
		init(bendPolicy);

		updateHandles();
	}

	/**
	 * Re-computes the handle parts. Adjusts the host to reflect its new
	 * position.
	 */
	@SuppressWarnings({ "serial", "unchecked" })
	protected void updateHandles() {
		if (!(targetPart instanceof IContentPart)) {
			return;
		}
		IContentPart<Node, ? extends Node> targetContentPart = (IContentPart<Node, ? extends Node>) targetPart;
		IHandlePart<Node, ? extends Node> replacementHandle = targetPart
				.getRoot().getAdapter(new TypeToken<SelectionBehavior<Node>>() {
				}).updateHandles(targetContentPart, handleDistanceComparator,
						getHost());
		if (replacementHandle instanceof AbstractFXSegmentHandlePart) {
			AbstractFXSegmentHandlePart<Node> segmentData = (AbstractFXSegmentHandlePart<Node>) replacementHandle;
			getHost().setSegmentIndex(segmentData.getSegmentIndex());
			getHost().setSegmentParameter(segmentData.getSegmentParameter());
			if (segmentData.getSegmentParameter() == initialSegmentParameter) {
				// Restore hover if the replacement handle fulfills the same
				// role as the host (same parameter == same role).
				getHost().getRoot().getViewer()
						.getAdapter(new TypeToken<HoverModel<Node>>() {
						}).setHover(getHost());
			} else if (!((initialSegmentParameter == 0.25
					|| initialSegmentParameter == 0.75)
					&& segmentData.getSegmentParameter() == 0.5
					&& Math.abs(segmentData.getSegmentIndex()
							- initialSegmentIndex) < 2)) {
				// XXX: If a quarter handle was dragged and replaced by a mid
				// handle, we do not clear hover.
				getHost().getRoot().getViewer()
						.getAdapter(new TypeToken<HoverModel<Node>>() {
						}).clearHover();
			}
		}
	}

}