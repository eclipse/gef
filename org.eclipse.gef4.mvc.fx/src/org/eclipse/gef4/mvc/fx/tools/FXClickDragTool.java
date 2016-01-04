/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.mvc.fx.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.gef4.fx.gestures.AbstractMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An {@link ITool} to handle click/drag interaction gestures.
 * <p>
 * As click and drag are 'overlapping' gestures (a click is part of each drag,
 * which is composed out of click, drag, and release), these are handled
 * together here, even while distinct interaction policies will be queried to
 * handle the respective gesture parts.
 * <p>
 * During each click/drag interaction, the tool identifies respective
 * {@link IVisualPart}s that serve as interaction targets for click and drag
 * respectively. They are identified via hit-testing on the visuals and the
 * availability of a corresponding {@link AbstractFXOnClickPolicy} or
 * {@link AbstractFXOnDragPolicy}.
 * <p>
 * The {@link FXClickDragTool} handles the opening and closing of an transaction
 * operation via the {@link FXDomain}, to which it is adapted. It controls that
 * a single transaction operation is used for the complete interaction
 * (including the click and potential drag part), so all interaction results can
 * be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXClickDragTool extends AbstractFXTool {

	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * click part of the click/drag interaction gesture.
	 */
	// TODO: Rename to ON_CLICK_POLICY_KEY
	public static final Class<AbstractFXOnClickPolicy> CLICK_TOOL_POLICY_KEY = AbstractFXOnClickPolicy.class;

	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * drag part of the click/drag interaction gesture.
	 */
	// TODO: Rename to ON_DRAG_POLICY_KEY
	public static final Class<AbstractFXOnDragPolicy> DRAG_TOOL_POLICY_KEY = AbstractFXOnDragPolicy.class;

	private final Map<IViewer<Node>, AbstractMouseDragGesture> gestures = new HashMap<>();

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			// register mouse move filter for forwarding events to drag policies
			// that can show a mouse cursor to indicate their action
			viewer.getRootPart().getVisual().getScene().addEventFilter(
					MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							EventTarget eventTarget = event.getTarget();
							if (eventTarget instanceof Node) {
								// determine all drag policies that can be
								// notified about events
								Node target = (Node) eventTarget;
								List<? extends AbstractFXOnDragPolicy> dragPolicies = getTargetPolicies(
										viewer, target, DRAG_TOOL_POLICY_KEY);

								// search drag policies in reverse order first,
								// so that the policy closest to the target part
								// is the first policy to provide an indication
								// cursor
								boolean changedCursor = false;
								ListIterator<? extends AbstractFXOnDragPolicy> dragIterator = dragPolicies
										.listIterator(dragPolicies.size());
								while (!changedCursor
										&& dragIterator.hasPrevious()) {
									changedCursor = dragIterator.previous()
											.showIndicationCursor(event);
								}
							}
						}
					});

			AbstractMouseDragGesture gesture = new AbstractMouseDragGesture() {
				private Collection<? extends AbstractFXOnDragPolicy> policies;

				@Override
				protected void drag(Node target, MouseEvent e, double dx,
						double dy) {
					// abort processing of this gesture if no policies could be
					// found that can process it
					if (policies == null) {
						return;
					}

					for (AbstractFXOnDragPolicy policy : policies) {
						policy.drag(e, new Dimension(dx, dy));
					}
				}

				@Override
				protected void press(Node target, MouseEvent e) {
					// process click first
					boolean opened = false;
					List<? extends AbstractFXOnClickPolicy> clickPolicies = getTargetPolicies(
							viewer, target, CLICK_TOOL_POLICY_KEY);
					if (clickPolicies != null && !clickPolicies.isEmpty()) {
						opened = true;
						getDomain()
								.openExecutionTransaction(FXClickDragTool.this);
						for (AbstractFXOnClickPolicy clickPolicy : clickPolicies) {
							clickPolicy.click(e);
						}
					}

					// determine drag target part
					policies = getTargetPolicies(viewer, target,
							DRAG_TOOL_POLICY_KEY);

					// abort processing of this gesture if no policies could be
					// found
					if (policies.isEmpty()) {
						// remove this tool from the domain's execution
						// transaction
						getDomain().closeExecutionTransaction(
								FXClickDragTool.this);
						policies = null;
						return;
					}

					// add this tool to the execution transaction of the domain
					if (!opened) {
						getDomain()
								.openExecutionTransaction(FXClickDragTool.this);
					}

					// send press() to all drag policies
					for (AbstractFXOnDragPolicy policy : policies) {
						policy.press(e);
					}
				}

				@Override
				protected void release(Node target, MouseEvent e, double dx,
						double dy) {
					// abort processing of this gesture if no policies could be
					// found that can process it
					if (policies == null) {
						return;
					}

					// send release() to all drag policies
					for (AbstractFXOnDragPolicy policy : policies) {
						policy.release(e, new Dimension(dx, dy));
					}

					// remove this tool from the domain's execution transaction
					getDomain().closeExecutionTransaction(FXClickDragTool.this);

					// reset drag policies
					policies = null;
				}
			};

			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (AbstractMouseDragGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
