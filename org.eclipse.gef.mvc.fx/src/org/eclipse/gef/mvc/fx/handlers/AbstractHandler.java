/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.gestures.AbstractGesture;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;

/**
 * Abstract base implementation of {@link IHandler} that handles an interaction.
 *
 * @author anyssen
 *
 */
public abstract class AbstractHandler extends
		IAdaptable.Bound.Impl<IVisualPart<? extends Node>> implements IHandler {

	private static Map<IPolicy, StackTraceElement[]> started = new HashMap<>();

	// SNIP debug utilities

	private static Map<IPolicy, StackTraceElement[]> finished = new HashMap<>();
	private static boolean isDebug = false; // true;

	private static boolean canFinish(IPolicy policy) {
		if (!started.containsKey(policy)) {
			System.out.println(
					"[ERROR] Trying to finish not-yet-started transaction policy "
							+ policy + " from:");
			printStackTrace(getRelevantStackTrace());
			if (finished.containsKey(policy)) {
				System.out.println(
						"[INFO] The policy was previously finished at:");
				printStackTrace(finished.get(policy));
			}
			return false;
		} else {
			started.remove(policy);
			finished.put(policy, getRelevantStackTrace());
			return true;
		}
	}

	private static boolean canStart(IPolicy policy) {
		if (started.containsKey(policy)) {
			System.out.println(
					"[ERROR] Trying to start already-started transaction policy "
							+ policy + " from:");
			printStackTrace(getRelevantStackTrace());
			System.out.println("[INFO] The policy was previously started at:");
			printStackTrace(started.get(policy));
			return false;
		} else {
			started.put(policy, getRelevantStackTrace());
			return true;
		}
	}

	private static StackTraceElement[] getRelevantStackTrace() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		// keep method calls until we find a Tool
		int i = 2; // start at 2 to dismiss local methods
		for (; i < trace.length; i++) {
			if (AbstractGesture.class.isAssignableFrom(trace[i].getClass())) {
				break;
			}
		}
		return Arrays.copyOfRange(trace, 2, i);
	}

	private static void printStackTrace(StackTraceElement[] trace) {
		for (int i = 0; i < trace.length; i++) {
			System.out.println("*) " + trace[i]);
		}
	}

	private final Map<IVisualPart<? extends Node>, Boolean> initialRefreshVisual = new HashMap<>();

	// SNAP debug utilities

	// If using e.g. a hover handle, it may be that this policy looses its
	// viewer link between init() and commit(). In order to being able to safely
	// commit(), we need to keep track of the domains.
	private Map<IPolicy, IDomain> domains = new HashMap<>();

	// TODO: add lifecycle of start, end, and abort interaction -> disable
	// visuals, etc.

	/**
	 * If the given {@link IPolicy} is not <code>null</code>, executes its
	 * commit operation the {@link IDomain}.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void commit(IPolicy policy) {
		if (policy != null) {
			IDomain domain = domains.remove(policy);

			if (isDebug) {
				if (!canFinish(policy)) {
					return;
				}
			}

			ITransactionalOperation o = policy.commit();
			if (o != null && !o.isNoOp()) {
				try {
					domain.execute(o, new NullProgressMonitor());
				} catch (ExecutionException e) {
					throw new RuntimeException(
							"An exception occured when committing policy "
									+ policy + ".",
							e);
				}
			}
		}
	}

	/**
	 * If the given {@link ITransactionalOperation} is not <code>null</code>,
	 * executes it on the {@link IDomain} of the {@link #getHost() host's}
	 * {@link IViewer}.
	 *
	 * @param operation
	 *            The {@link ITransactionalOperation} to execute on the domain.
	 */
	protected void execute(ITransactionalOperation operation) {
		if (operation != null && !operation.isNoOp()) {
			try {
				getHost().getViewer().getDomain().execute(operation,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(
						"An exception occured when executing operation "
								+ operation + ".",
						e);
			}
		}
	}

	/**
	 * Returns the {@link #getAdaptable() adaptable} of this
	 * {@link AbstractHandler}.
	 *
	 * @return The {@link #getAdaptable() adaptable} of this
	 *         {@link AbstractHandler}.
	 */
	@Override
	public IVisualPart<? extends Node> getHost() {
		return getAdaptable();
	}

	/**
	 * If the given {@link IPolicy} is not <code>null</code>, initializes it.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void init(IPolicy policy) {
		if (policy != null) {
			if (isDebug) {
				if (!canStart(policy)) {
					return;
				}
			}

			domains.put(policy, getHost().getRoot().getViewer().getDomain());
			policy.init();
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map, otherwise <code>false</code>.
	 */
	protected boolean isRegistered(EventTarget eventTarget) {
		IVisualPart<? extends Node> host = getHost();
		if (host.getRoot() == null || host.getRoot().getViewer() == null) {
			// host is not in visual-part-hierarchy or not in viewer
			return false;
		}
		IViewer viewer = host.getRoot().getViewer();
		if (eventTarget instanceof Node) {
			return PartUtils.retrieveVisualPart(viewer,
					(Node) eventTarget) != viewer.getRootPart();
		}
		// eventTarget is a Scene
		return false;
	}

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map for the {@link #getHost() host} of this
	 * {@link AbstractHandler}. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map for the host of this policy, otherwise
	 *         <code>false</code>.
	 */
	protected boolean isRegisteredForHost(EventTarget eventTarget) {
		IVisualPart<? extends Node> host = getHost();
		if (host.getRoot() == null || host.getRoot().getViewer() == null) {
			// host is not in visual-part-hierarchy or not in viewer
			return false;
		}
		IViewer viewer = host.getRoot().getViewer();
		if (eventTarget instanceof Node) {
			return PartUtils.retrieveVisualPart(viewer,
					(Node) eventTarget) == host;
		}
		// eventTarget is a Scene
		return false;
	}

	/**
	 * Restores that the given {@link IVisualPart} refreshes its visual if this
	 * was the case prior to disabling the refresh of visuals.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which refreshing of visuals is
	 *            restored.
	 */
	protected void restoreRefreshVisuals(IVisualPart<? extends Node> part) {
		part.setRefreshVisual(initialRefreshVisual.remove(part));
	}

	/**
	 * If the given {@link IPolicy} is not <code>null</code>, rolls it back.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void rollback(IPolicy policy) {
		if (policy != null) {
			domains.remove(policy);

			if (isDebug) {
				if (!canFinish(policy)) {
					return;
				}
			}

			policy.rollback();
		}
	}

	/**
	 * Disable that the given {@link IVisualPart} refreshes its visual, if this
	 * was not already the case (see
	 * {@link IVisualPart#setRefreshVisual(boolean)}). Stores the state (whether
	 * the part was still refreshing its visual or not) so it can be restored
	 * later (see {@link #restoreRefreshVisuals(IVisualPart)}).
	 *
	 * @param part
	 *            The {@link IVisualPart} whose visual refresh is to be
	 *            disabled.
	 */
	protected void storeAndDisableRefreshVisuals(
			IVisualPart<? extends Node> part) {
		initialRefreshVisual.put(part, part.isRefreshVisual());
		part.setRefreshVisual(false);
	}
}
