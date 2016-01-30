/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * Abstract base implementation of {@link IPolicy} that handles an interaction.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractInteractionPolicy<VR> extends AbstractPolicy<VR> {

	private final Map<IVisualPart<VR, ? extends VR>, Boolean> initialRefreshVisual = new HashMap<>();

	// If using e.g. a hover handle, it may be that this policy looses its
	// viewer link between init() and commit(). In order to being able to safely
	// commit(), we need to keep track of the domains.
	private Map<IPolicy<VR>, IDomain<VR>> domains = new HashMap<>();

	// TODO: add lifecycle of start, end, and abort interaction -> disable
	// visuals, etc.

	/**
	 * If the given {@link IPolicy} is not <code>null</code>, executes its
	 * commit operation the {@link IDomain}.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void commit(AbstractTransactionPolicy<VR> policy) {
		if (policy != null) {
			ITransactionalOperation o = policy.commit();
			if (o != null && !o.isNoOp()) {
				try {
					domains.remove(policy).execute(o);
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
	protected void init(AbstractTransactionPolicy<VR> policy) {
		if (policy != null) {
			domains.put(policy, getHost().getRoot().getViewer().getDomain());
			policy.init();
		}
	}

	/**
	 * Restores that the given {@link IVisualPart} refreshes its visual if this
	 * was the case prior to disabling the refresh of visuals.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which refreshing of visuals is
	 *            restored.
	 */
	protected void restoreRefreshVisuals(IVisualPart<VR, ? extends VR> part) {
		part.setRefreshVisual(initialRefreshVisual.remove(part));
	}

	/**
	 * If the given {@link IPolicy} is not <code>null</code>, rolls it back.
	 *
	 * @param policy
	 *            The {@link IPolicy} to commit.
	 */
	protected void rollback(AbstractTransactionPolicy<VR> policy) {
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
			IVisualPart<VR, ? extends VR> part) {
		initialRefreshVisual.put(part, part.isRefreshVisual());
		part.setRefreshVisual(false);
	}

}
