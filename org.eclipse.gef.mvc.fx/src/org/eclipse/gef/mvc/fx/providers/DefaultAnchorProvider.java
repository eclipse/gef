/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.fx.anchors.ChopBoxStrategy;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.IComputationStrategy;
import org.eclipse.gef.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link DefaultAnchorProvider} can be used to provide
 * {@link DynamicAnchor}s for anchored {@link IVisualPart}s depending on their
 * visual. For {@link Connection} visuals with an {@link OrthogonalRouter}, a
 * {@link DynamicAnchor} with an {@link OrthogonalProjectionStrategy} is used.
 * Otherwise, a {@link DynamicAnchor} with a {@link ChopBoxStrategy} is used.
 */
public class DefaultAnchorProvider
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
		implements IAnchorProvider {

	private DynamicAnchor defaultAnchor;
	private DynamicAnchor orthoAnchor;

	/**
	 * Constructs a new instance of {@link DefaultAnchorProvider}.
	 */
	public DefaultAnchorProvider() {
	}

	/**
	 * Returns the {@link AnchorageReferenceGeometry} that is to be used for the
	 * given {@link DynamicAnchor}.
	 *
	 * @param anchor
	 *            The {@link DynamicAnchor} for which to compute the
	 *            {@link AnchorageReferenceGeometry}.
	 * @return The {@link AnchorageReferenceGeometry} that is to be used for the
	 *         given {@link DynamicAnchor}.
	 */
	protected IGeometry computeAnchorageReferenceGeometry(
			DynamicAnchor anchor) {
		return NodeUtils.getShapeOutline(getAdaptable().getVisual());
	}

	/**
	 * Creates a new {@link DynamicAnchor} using the visual of the
	 * {@link #getAdaptable()} as its anchorage and passing-in the given
	 * {@link IComputationStrategy}. Also sets up the computation parameters for
	 * the newly constructed anchor using
	 * {@link #initializeComputationParameters(DynamicAnchor)}.
	 *
	 * @param strategy
	 *            The {@link IComputationStrategy} to use.
	 * @return The newly constructed and set up {@link DynamicAnchor}.
	 */
	protected DynamicAnchor createDynamicAnchor(IComputationStrategy strategy) {
		DynamicAnchor anchor = new DynamicAnchor(getAdaptable().getVisual(),
				strategy);
		initializeComputationParameters(anchor);
		return anchor;
	}

	@Override
	public IAnchor get(IVisualPart<? extends Node> anchoredPart, String role) {
		// TODO: role is ignored by default
		Node anchoredVisual = anchoredPart.getVisual();
		// check if orthogonal anchor should be used
		if (anchoredVisual instanceof Connection) {
			Connection connection = (Connection) anchoredVisual;
			if (connection.getRouter() instanceof OrthogonalRouter) {
				return getOrthogonalAnchor();
			}
		}
		// fallback to default anchor
		return getDefaultAnchor();
	}

	/**
	 * Returns the {@link IAnchor} that is to be used when no other, more
	 * specific anchor is used.
	 *
	 * @return The {@link IAnchor} that is to be used when no other, more
	 *         specific anchor is used.
	 */
	protected IAnchor getDefaultAnchor() {
		if (defaultAnchor == null) {
			defaultAnchor = createDynamicAnchor(new ChopBoxStrategy());
		}
		return defaultAnchor;
	}

	/**
	 * Returns the {@link IAnchor} that is to be used for orthogonal
	 * {@link Connection}s.
	 *
	 * @return The {@link IAnchor} that is to be used for orthogonal
	 *         {@link Connection}s
	 */
	protected IAnchor getOrthogonalAnchor() {
		if (orthoAnchor == null) {
			orthoAnchor = createDynamicAnchor(
					new OrthogonalProjectionStrategy());
		}
		return orthoAnchor;
	}

	/**
	 * Initializes the computation parameters for the given
	 * {@link DynamicAnchor}.
	 *
	 * @param anchor
	 *            The {@link DynamicAnchor} for which to initialize computation
	 *            parameters.
	 */
	protected void initializeComputationParameters(final DynamicAnchor anchor) {
		anchor.getComputationParameter(AnchorageReferenceGeometry.class)
				.bind(new ObjectBinding<IGeometry>() {
					{
						// XXX: Binding value needs to be recomputed when the
						// anchorage changes or when the layout bounds of the
						// respective anchorage changes.
						anchor.anchorageProperty()
								.addListener(new ChangeListener<Node>() {
									@Override
									public void changed(
											ObservableValue<? extends Node> observable,
											Node oldValue, Node newValue) {
										if (oldValue != null) {
											unbind(oldValue
													.boundsInLocalProperty());
										}
										if (newValue != null) {
											bind(newValue
													.boundsInLocalProperty());
										}
										invalidate();
									}
								});
						bind(anchor.getAnchorage().boundsInLocalProperty());
					}

					@Override
					protected IGeometry computeValue() {
						return computeAnchorageReferenceGeometry(anchor);
					}
				});
	}
}
