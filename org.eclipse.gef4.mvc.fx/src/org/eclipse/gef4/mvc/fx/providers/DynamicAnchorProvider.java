/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.common.adapt.AbstractBoundProvider;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.IComputationStrategy;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link DynamicAnchorProvider} is a <code>Provider&lt;IAnchor&gt;</code>
 * implementation that provides an {@link DynamicAnchor} for the host visual.
 *
 * @author anyssen
 *
 */
public class DynamicAnchorProvider extends
		AbstractBoundProvider<IAnchor, IVisualPart<Node, ? extends Node>> {

	private IAnchor anchor;
	private IComputationStrategy computationStrategy = null;

	/**
	 * Creates a new {@link DynamicAnchorProvider} that provides a (cached)
	 * dynamic anchor with the default computation strategy.
	 */
	public DynamicAnchorProvider() {
		this(null);
	}

	/**
	 * Creates a new {@link DynamicAnchorProvider} that provides a (cached)
	 * dynamic anchor with the provided {@link IComputationStrategy}.
	 *
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} to use for the
	 *            {@link DynamicAnchor} that is to be provided.
	 */
	public DynamicAnchorProvider(IComputationStrategy computationStrategy) {
		this.computationStrategy = computationStrategy;
	}

	/**
	 * Creates a new dynamic anchor to be provided.
	 *
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} to use. May be
	 *            <code>null</code>, in which case the default strategy of the
	 *            {@link DynamicAnchor} is to be used.
	 *
	 * @return A new {@link DynamicAnchor}.
	 */
	protected DynamicAnchor createAnchor(
			IComputationStrategy computationStrategy) {
		final Node visual = getAdaptable().getVisual();
		final DynamicAnchor anchor = computationStrategy == null
				? new DynamicAnchor(visual)
				: new DynamicAnchor(visual, computationStrategy);
		anchor.referenceGeometryProperty().bind(new ObjectBinding<IGeometry>() {
			{
				// XXX: Binding value needs to be recomputed when the anchorage
				// changes or when the layout bounds of the respective anchorage
				// changes.
				anchor.anchorageProperty()
						.addListener(new ChangeListener<Node>() {
					@Override
					public void changed(
							ObservableValue<? extends Node> observable,
							Node oldValue, Node newValue) {
						if (oldValue != null) {
							unbind(oldValue.layoutBoundsProperty());
						}
						if (newValue != null) {
							bind(newValue.layoutBoundsProperty());
						}
						invalidate();
					}
				});
				bind(anchor.getAnchorage().layoutBoundsProperty());
			}

			@Override
			protected IGeometry computeValue() {
				return NodeUtils.getShapeOutline(getAdaptable().getVisual());
			}
		});
		return anchor;
	}

	@Override
	public IAnchor get() {
		if (anchor == null) {
			anchor = createAnchor(computationStrategy);
		}
		return anchor;
	}

}