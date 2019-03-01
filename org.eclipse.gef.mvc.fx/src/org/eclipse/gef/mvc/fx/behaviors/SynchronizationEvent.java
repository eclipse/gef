/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Rudi (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * The {@link Event} that is fired when synchronizing {@link IContentPart}s
 * w.r.t. content children and content anchorages.
 *
 * Such events are also fired when synchronizing parts w.r.t.
 * {@link IViewer#contentsProperty()}. In this case, the
 * {@link #getSynchronizedPart()} is the {@link IRootPart} of the corresponding
 * {@link IViewer}.
 */
public class SynchronizationEvent extends Event {

	/**
	 * Base {@link EventType} for all {@link SynchronizationEvent}s.
	 */
	public static final EventType<SynchronizationEvent> ANY = new EventType<>(
			Event.ANY);
	/**
	 * The {@link EventType} of the {@link Event} fired when synchronization of
	 * children is started.
	 */
	public static final EventType<SynchronizationEvent> SYNC_CHILDREN_STARTED = new EventType<>(
			ANY, "SYNC_CHILDREN_STARTED");

	/**
	 * The {@link EventType} of the {@link Event} fired when synchronization of
	 * children is finished.
	 */
	public static final EventType<SynchronizationEvent> SYNC_CHILDREN_FINISHED = new EventType<>(
			ANY, "SYNC_CHILDREN_FINISHED");

	/**
	 * The {@link EventType} of the {@link Event} fired when synchronization of
	 * anchorages is started.
	 */
	public static final EventType<SynchronizationEvent> SYNC_ANCHORAGES_STARTED = new EventType<>(
			ANY, "SYNC_ANCHORAGES_STARTED");

	/**
	 * The {@link EventType} of the {@link Event} fired when synchronization of
	 * anchorages is finished.
	 */
	public static final EventType<SynchronizationEvent> SYNC_ANCHORAGES_FINISHED = new EventType<>(
			ANY, "SYNC_ANCHORAGES_FINISHED");

	private static final long serialVersionUID = 1L;

	/**
	 * @param anchored
	 *            The anchored {@link IVisualPart} to be synchronized
	 * @return the {@link SynchronizationEvent} to be fired on synchronization
	 *         finish
	 */
	public static SynchronizationEvent finishSyncAnchorages(
			IVisualPart<? extends Node> anchored) {
		return new SynchronizationEvent(SYNC_ANCHORAGES_FINISHED, anchored);
	}

	/**
	 * @param parent
	 *            The parent {@link IVisualPart} to be synchronized
	 * @return the {@link SynchronizationEvent} to be fired on synchronization
	 *         finish
	 */
	public static SynchronizationEvent finishSyncChildren(
			IVisualPart<? extends Node> parent) {
		return new SynchronizationEvent(SYNC_CHILDREN_FINISHED, parent);
	}

	/**
	 * @param anchored
	 *            The anchored {@link IVisualPart} to be synchronized
	 * @return the {@link SynchronizationEvent} to be fired on synchronization
	 *         start
	 */
	public static SynchronizationEvent startSyncAnchorages(
			IVisualPart<? extends Node> anchored) {
		return new SynchronizationEvent(SYNC_ANCHORAGES_STARTED, anchored);
	}

	/**
	 * @param parent
	 *            The parent {@link IVisualPart} to be synchronized
	 * @return the {@link SynchronizationEvent} to be fired on synchronization
	 *         start
	 */
	public static SynchronizationEvent startSyncChildren(
			IVisualPart<? extends Node> parent) {
		return new SynchronizationEvent(SYNC_CHILDREN_STARTED, parent);
	}

	private IVisualPart<? extends Node> syncedPart;

	/**
	 * @param eventType
	 *            the type of the event
	 * @param syncedPart
	 *            the {@link IVisualPart} that is synchronized.
	 *
	 */
	public SynchronizationEvent(
			EventType<? extends SynchronizationEvent> eventType,
			IVisualPart<? extends Node> syncedPart) {
		super(eventType);
		this.syncedPart = syncedPart;
	}

	/**
	 * Construct a new {@code SynchronizationEvent} with the specified event
	 * source and target. If the source or target is set to {@code null}, it is
	 * replaced by the {@code NULL_SOURCE_TARGET} value. All
	 * SynchronizationEvents have their type set to the given event type.
	 *
	 * @param source
	 *            the event source which sent the event
	 * @param target
	 *            the event target to associate with the event
	 * @param eventType
	 *            the type of the event to be used
	 */
	public SynchronizationEvent(Object source, EventTarget target,
			EventType<? extends SynchronizationEvent> eventType) {
		super(source, target, eventType);
	}

	@Override
	public SynchronizationEvent copyFor(Object newSource,
			EventTarget newTarget) {
		return (SynchronizationEvent) super.copyFor(newSource, newTarget);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventType<? extends SynchronizationEvent> getEventType() {
		return (EventType<? extends SynchronizationEvent>) super.getEventType();
	}

	/**
	 * @return The part to be synchronized
	 */
	public IVisualPart<? extends Node> getSynchronizedPart() {
		return syncedPart;
	}
}