/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;

/**
 * Abstract base implementation of {@link IPolicy} that handles an interaction.
 *
 * @author anyssen
 *
 */
public abstract class AbstractInteractionPolicy extends AbstractPolicy {

	private final Map<IVisualPart<? extends Node>, Boolean> initialRefreshVisual = new HashMap<>();

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
	protected void commit(AbstractTransactionPolicy policy) {
		if (policy != null) {
			ITransactionalOperation o = policy.commit();
			if (o != null && !o.isNoOp()) {
				try {
					domains.remove(policy).execute(o,
							new NullProgressMonitor());
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
	 * If the given {@link IPolicy} is not <code>null</code>, initializes it.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void init(AbstractTransactionPolicy policy) {
		if (policy != null) {
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
	 * {@link AbstractInteractionPolicy}. Otherwise returns <code>false</code>.
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
	protected void rollback(AbstractTransactionPolicy policy) {
		if (policy != null) {
			domains.remove(policy);
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
