/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnDragHandler;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An {@link IGesture} to handle click/drag interaction gestures.
 * <p>
 * As click and drag are 'overlapping' gestures (a click is part of each drag,
 * which is composed out of click, drag, and release), these are handled
 * together here, even while distinct interaction policies will be queried to
 * handle the respective gesture parts.
 * <p>
 * During each click/drag interaction, the tool identifies respective
 * {@link IVisualPart}s that serve as interaction targets for click and drag
 * respectively. They are identified via hit-testing on the visuals and the
 * availability of a corresponding {@link IOnClickHandler} or
 * {@link IOnDragHandler}.
 * <p>
 * The {@link ClickDragGesture} handles the opening and closing of an
 * transaction operation via the {@link IDomain}, to which it is adapted. It
 * controls that a single transaction operation is used for the complete
 * interaction (including the click and potential drag part), so all interaction
 * results can be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class ClickDragGesture extends AbstractGesture {

	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * click part of the click/drag interaction gesture.
	 */
	public static final Class<IOnClickHandler> ON_CLICK_POLICY_KEY = IOnClickHandler.class;

	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * drag part of the click/drag interaction gesture.
	 */
	public static final Class<IOnDragHandler> ON_DRAG_POLICY_KEY = IOnDragHandler.class;

	// TODO: Provide activeViewer in AbstractTool.
	private IViewer activeViewer;
	private Node pressed;
	private Point2D startMousePosition;

	/**
	 * This {@link EventHandler} is registered as an event filter on the
	 * {@link Scene} to handle drag and release events.
	 */
	private EventHandler<? super MouseEvent> mouseFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// determine pressed/dragged/released state
			EventType<? extends Event> type = event.getEventType();
			if (pressed == null && type.equals(MouseEvent.MOUSE_PRESSED)) {
				EventTarget target = event.getTarget();
				if (target instanceof Node) {
					// initialize the gesture
					pressed = (Node) target;
					startMousePosition = new Point2D(event.getSceneX(),
							event.getSceneY());
					press(pressed, event);
				}
				return;
			} else if (pressed == null) {
				// not initialized yet
				return;
			}
			if (type.equals(MouseEvent.MOUSE_EXITED_TARGET)
					|| type.equals(MouseEvent.MOUSE_ENTERED_TARGET)) {
				// ignore mouse exited target events here (they may result from
				// visual changes that are caused by a preceding press)
				return;
			}
			boolean dragged = type.equals(MouseEvent.MOUSE_DRAGGED);
			boolean released = false;
			if (!dragged) {
				released = type.equals(MouseEvent.MOUSE_RELEASED);
				if (!released) {
					// account for missing RELEASE events
					if (!event.isPrimaryButtonDown()
							&& !event.isSecondaryButtonDown()
							&& !event.isMiddleButtonDown()) {
						// no button down
						released = true;
					}
				}
			}
			if (dragged || released) {
				double x = event.getSceneX();
				double dx = x - startMousePosition.getX();
				double y = event.getSceneY();
				double dy = y - startMousePosition.getY();
				if (dragged) {
					drag(pressed, event, dx, dy);
				} else {
					release(pressed, event, dx, dy);
					pressed = null;
				}
			}
		}
	};

	private final IOnDragHandler indicationCursorPolicy[] = new IOnDragHandler[] {
			null };

	@SuppressWarnings("unchecked")
	private final List<IOnDragHandler> possibleDragPolicies[] = new ArrayList[] {
			null };
	private EventHandler<MouseEvent> indicationCursorMouseMoveFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (indicationCursorPolicy[0] != null) {
				indicationCursorPolicy[0].hideIndicationCursor();
				indicationCursorPolicy[0] = null;
			}

			EventTarget eventTarget = event.getTarget();
			if (eventTarget instanceof Node) {
				// determine all drag policies that can be
				// notified about events
				Node target = (Node) eventTarget;
				IViewer viewer = PartUtils.retrieveViewer(getDomain(), target);
				if (viewer != null) {
					possibleDragPolicies[0] = new ArrayList<>(
							getHandlerResolver().resolve(ClickDragGesture.this,
									target, viewer, ON_DRAG_POLICY_KEY));
				} else {
					possibleDragPolicies[0] = new ArrayList<>();
				}

				// search drag policies in reverse order first,
				// so that the policy closest to the target part
				// is the first policy to provide an indication
				// cursor
				ListIterator<? extends IOnDragHandler> dragIterator = possibleDragPolicies[0]
						.listIterator(possibleDragPolicies[0].size());
				while (dragIterator.hasPrevious()) {
					IOnDragHandler policy = dragIterator.previous();
					if (policy.showIndicationCursor(event)) {
						indicationCursorPolicy[0] = policy;
						break;
					}
				}
			}
		}
	};

	private EventHandler<KeyEvent> indicationCursorKeyFilter = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			if (indicationCursorPolicy[0] != null) {
				indicationCursorPolicy[0].hideIndicationCursor();
				indicationCursorPolicy[0] = null;
			}

			if (possibleDragPolicies[0] == null
					|| possibleDragPolicies[0].isEmpty()) {
				return;
			}

			// search drag policies in reverse order first,
			// so that the policy closest to the target part
			// is the first policy to provide an indication
			// cursor
			ListIterator<? extends IOnDragHandler> dragIterator = possibleDragPolicies[0]
					.listIterator(possibleDragPolicies[0].size());
			while (dragIterator.hasPrevious()) {
				IOnDragHandler policy = dragIterator.previous();
				if (policy.showIndicationCursor(event)) {
					indicationCursorPolicy[0] = policy;
					break;
				}
			}
		}
	};

	@Override
	protected void abortPolicies(IViewer viewer) {
		// cannot abort if no activeViewer
		if (activeViewer == null) {
			return;
		}
		// check if any viewer is focused
		for (IViewer v : getDomain().getViewers().values()) {
			if (v.isViewerFocused()) {
				return;
			}
		}
		super.abortPolicies(viewer);
		activeViewer = null;
	}

	@Override
	protected void doAbortPolicies(IViewer viewer) {
		for (IHandler handler : getActiveHandlers(activeViewer)) {
			if (handler instanceof IOnDragHandler) {
				((IOnDragHandler) handler).abortDrag();
			}
		}
	}

	@Override
	protected void doHookScene(Scene scene) {
		// register mouse move filter for forwarding events to drag policies
		// that can show a mouse cursor to indicate their action
		scene.addEventFilter(MouseEvent.MOUSE_MOVED,
				indicationCursorMouseMoveFilter);
		// register key event filter for forwarding events to drag policies
		// that can show a mouse cursor to indicate their action
		scene.addEventFilter(KeyEvent.ANY, indicationCursorKeyFilter);
		// register mouse filter for forwarding press, drag, and release
		// events
		scene.addEventFilter(MouseEvent.ANY, mouseFilter);
	}

	@Override
	protected void doUnhookScene(Scene scene) {
		scene.removeEventFilter(MouseEvent.ANY, mouseFilter);
		scene.removeEventFilter(MouseEvent.MOUSE_MOVED,
				indicationCursorMouseMoveFilter);
		scene.removeEventFilter(KeyEvent.ANY, indicationCursorKeyFilter);
	}

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_DRAGGED} events.
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 * @param dx
	 *            The horizontal displacement from the mouse press location.
	 * @param dy
	 *            The vertical displacement from the mouse press location.
	 */
	protected void drag(Node target, MouseEvent event, double dx, double dy) {
		// abort processing of this gesture if no policies could be
		// found that can process it
		if (activeViewer == null || getActiveHandlers(activeViewer).isEmpty()) {
			return;
		}

		for (IOnDragHandler policy : getActiveHandlers(activeViewer)) {
			policy.drag(event, new Dimension(dx, dy));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IOnDragHandler> getActiveHandlers(IViewer viewer) {
		return (List<IOnDragHandler>) super.getActiveHandlers(viewer);
	}

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_PRESSED} events.
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 */
	protected void press(Node target, MouseEvent event) {
		IViewer viewer = PartUtils.retrieveViewer(getDomain(), target);
		if (viewer == null) {
			return;
		}
		if (viewer instanceof InfiniteCanvasViewer) {
			InfiniteCanvas canvas = ((InfiniteCanvasViewer) viewer).getCanvas();
			// if any node in the target hierarchy is a scrollbar,
			// do not process the event
			if (event.getTarget() instanceof Node) {
				Node targetNode = (Node) event.getTarget();
				while (targetNode != null) {
					if (targetNode == canvas.getHorizontalScrollBar()
							|| targetNode == canvas.getVerticalScrollBar()) {
						return;
					}
					targetNode = targetNode.getParent();
				}
			}
		}

		// show indication cursor on press so that the indication
		// cursor is shown even when no mouse move event was
		// previously fired
		indicationCursorMouseMoveFilter.handle(event);

		// disable indication cursor event filters within
		// press-drag-release gesture
		Scene scene = target.getScene();
		if (scene == null) {
			// FIXME: Should not happen.
			System.err.println("Target part is not in Scene.");
			return;
		}
		scene.removeEventFilter(MouseEvent.MOUSE_MOVED,
				indicationCursorMouseMoveFilter);
		scene.removeEventFilter(KeyEvent.ANY, indicationCursorKeyFilter);

		// determine viewer that contains the given target part
		viewer = PartUtils.retrieveViewer(getDomain(), target);
		// determine click policies
		boolean opened = false;
		List<? extends IOnClickHandler> clickPolicies = getHandlerResolver()
				.resolve(ClickDragGesture.this, target, viewer,
						ON_CLICK_POLICY_KEY);
		// process click first
		if (clickPolicies != null && !clickPolicies.isEmpty()) {
			opened = true;
			getDomain().openExecutionTransaction(ClickDragGesture.this);
			for (IOnClickHandler clickPolicy : clickPolicies) {
				clickPolicy.click(event);
			}
		}

		// determine viewer that contains the given target part
		// again, now that the click policies have been executed
		activeViewer = PartUtils.retrieveViewer(getDomain(), target);

		// determine drag policies
		List<? extends IOnDragHandler> policies = null;
		if (activeViewer != null) {
			// XXX: A click policy could have changed the visual
			// hierarchy so that the viewer cannot be determined for
			// the target node anymore. If that is the case, no drag
			// policies should be notified about the event.
			policies = getHandlerResolver().resolve(ClickDragGesture.this,
					target, activeViewer, ON_DRAG_POLICY_KEY);
		}

		// abort processing of this gesture if no drag policies
		// could be found
		if (policies == null || policies.isEmpty()) {
			// remove this tool from the domain's execution
			// transaction if previously opened
			if (opened) {
				getDomain().closeExecutionTransaction(ClickDragGesture.this);
			}
			policies = null;
			return;
		}

		// add this tool to the execution transaction of the domain
		// if not yet opened
		if (!opened) {
			getDomain().openExecutionTransaction(ClickDragGesture.this);
		}

		// mark the drag policies as active
		setActiveHandlers(activeViewer, policies);

		// send press() to all drag policies
		for (IOnDragHandler policy : policies) {
			policy.startDrag(event);
		}
	}

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_RELEASED} events. This
	 * method is also called for other mouse events, when a mouse release event
	 * was not fired, but was detected otherwise (probably only possible when
	 * using the JavaFX/SWT integration).
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 * @param dx
	 *            The horizontal displacement from the mouse press location.
	 * @param dy
	 *            The vertical displacement from the mouse press location.
	 */
	protected void release(Node target, MouseEvent event, double dx,
			double dy) {
		if (activeViewer == null) {
			return;
		}

		// enable indication cursor event filters outside of
		// press-drag-release gesture
		Scene scene = activeViewer.getRootPart().getVisual().getScene();
		if (scene == null) {
			throw new IllegalStateException(
					"Active viewer's root part visual is not in Scene.");
		}
		scene.addEventFilter(MouseEvent.MOUSE_MOVED,
				indicationCursorMouseMoveFilter);
		scene.addEventFilter(KeyEvent.ANY, indicationCursorKeyFilter);

		// abort processing of this gesture if no policies could be
		// found that can process it
		if (getActiveHandlers(activeViewer).isEmpty()) {
			activeViewer = null;
			return;
		}

		// send release() to all drag policies
		for (IOnDragHandler policy : getActiveHandlers(activeViewer)) {
			policy.endDrag(event, new Dimension(dx, dy));
		}

		// clear active policies before processing release
		clearActiveHandlers(activeViewer);
		activeViewer = null;

		// remove this tool from the domain's execution transaction
		getDomain().closeExecutionTransaction(ClickDragGesture.this);

		// hide indication cursor
		if (indicationCursorPolicy[0] != null) {
			indicationCursorPolicy[0].hideIndicationCursor();
			indicationCursorPolicy[0] = null;
		}
	}
}
