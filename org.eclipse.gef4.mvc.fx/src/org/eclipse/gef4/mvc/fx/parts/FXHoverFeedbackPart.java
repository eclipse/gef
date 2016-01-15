/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FXHoverFeedbackPart} is an {@link AbstractFXFeedbackPart} that is
 * parameterized by <code>GeometryNode&lt;IGeometry&gt;</code>.
 *
 * @author mwienand
 *
 */
public class FXHoverFeedbackPart
		extends AbstractFXFeedbackPart<GeometryNode<IGeometry>> {

	/**
	 * The default stroke color for this part's visualization.
	 */
	public static final Color DEFAULT_STROKE = Color.web("#5a61af");

	/**
	 * The role name for the <code>Provider&lt;Effect&gt;</code> that will be
	 * used to query the {@link Effect} for this part's visualization.
	 */
	public static final String EFFECT_PROVIDER = "HoverFeedbackEffectProvider";

	private Provider<? extends IGeometry> feedbackGeometryProvider;

	/**
	 * Default constructor.
	 */
	public FXHoverFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> createVisual() {
		GeometryNode<IGeometry> visual = new GeometryNode<>();
		visual.setFill(Color.TRANSPARENT);
		visual.setMouseTransparent(true);
		visual.setManaged(false);
		visual.setStrokeType(StrokeType.OUTSIDE);
		visual.setStrokeWidth(1);

		// hover specific
		visual.setEffect(getHoverFeedbackEffect());
		visual.setStroke(DEFAULT_STROKE);
		return visual;
	}

	@Override
	public void doRefreshVisual(GeometryNode<IGeometry> visual) {
		if (getAnchoragesUnmodifiable().size() != 1) {
			return;
		}

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		visual.setGeometry(feedbackGeometry);

		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
		}
	}

	/**
	 * Returns the {@link IGeometry} that is provided by this part's
	 * {@link #setGeometryProvider(Provider) geometry provider}.
	 *
	 * @return The {@link IGeometry} that is provided by this part's geometry
	 *         provider.
	 */
	protected IGeometry getFeedbackGeometry() {
		return NodeUtils.sceneToLocal(getVisual(),
				feedbackGeometryProvider.get());
	}

	/**
	 * Returns the {@link Effect} that is provided by the
	 * <code>Provider&lt;Effect&gt;</code> of this part's first anchorage.
	 *
	 * @return The {@link Effect} that is provided by the
	 *         <code>Provider&lt;Effect&gt;</code> of this part's first
	 *         anchorage.
	 */
	@SuppressWarnings("serial")
	public Effect getHoverFeedbackEffect() {
		Provider<? extends Effect> effectProvider = null;
		if (!getAnchoragesUnmodifiable().isEmpty()) {
			IVisualPart<Node, ? extends Node> host = getAnchoragesUnmodifiable().keys()
					.iterator().next();
			effectProvider = host.getAdapter(
					AdapterKey.get(new TypeToken<Provider<? extends Effect>>() {
					}, EFFECT_PROVIDER));
		}
		if (effectProvider == null) {
			DropShadow effect = new DropShadow();
			effect.setRadius(3);
			return effect;
		}
		return effectProvider.get();
	}

	/**
	 * Sets the <code>Provider&lt;IGeometry&gt;</code> of this part to the given
	 * value.
	 *
	 * @param geometryProvider
	 *            The new <code>Provider&lt;IGeometry&gt;</code> for this part.
	 */
	public void setGeometryProvider(
			Provider<? extends IGeometry> geometryProvider) {
		feedbackGeometryProvider = geometryProvider;
	}

}
