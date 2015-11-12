/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FXSelectionFeedbackPart} is an {@link AbstractFXFeedbackPart} that
 * is parameterized by <code>GeometryNode&lt;IGeometry&gt;</code>.
 *
 * @author mwienand
 *
 */
public class FXSelectionFeedbackPart
		extends AbstractFXFeedbackPart<GeometryNode<IGeometry>> {

	private static final Color FOCUS_COLOR = Color.rgb(125, 173, 217);

	/**
	 * The role name for the <code>Provider&lt;Effect&gt;</code> that will be
	 * used to decorate a primary, focused selection.
	 */
	public static final String PRIMARY_FOCUSED_EFFECT_PROVIDER = "PrimaryFocusedSelectionFeedbackEffectProvider";

	/**
	 * The role name for the <code>Provider&lt;Effect&gt;</code> that will be
	 * used to decorate a primary, unfocused selection.
	 */
	public static final String PRIMARY_UNFOCUSED_EFFECT_PROVIDER = "PrimaryUnfocusedSelectionFeedbackEffectProvider";

	/**
	 * The role name for the <code>Provider&lt;Effect&gt;</code> that will be
	 * used to decorate a secondary, focused selection.
	 */
	public static final String SECONDARY_FOCUSED_EFFECT_PROVIDER = "SecondaryFocusedSelectionFeedbackEffectProvider";

	/**
	 * The role name for the <code>Provider&lt;Effect&gt;</code> that will be
	 * used to decorate a secondary, unfocused selection.
	 */
	public static final String SECONDARY_UNFOCUSED_EFFECT_PROVIDER = "SecondaryUnfocusedSelectionFeedbackEffectProvider";

	private final PropertyChangeListener focusModelListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (FocusModel.VIEWER_FOCUS_PROPERTY
					.equals(evt.getPropertyName())) {
				refreshVisual();
			} else
				if (FocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			}
		}
	};
	private Provider<? extends IGeometry> feedbackGeometryProvider;

	/**
	 * Default constructor.
	 */
	public FXSelectionFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> createVisual() {
		GeometryNode<IGeometry> feedbackVisual = new GeometryNode<IGeometry>();
		feedbackVisual.setFill(Color.TRANSPARENT);
		feedbackVisual.setMouseTransparent(true);
		feedbackVisual.setManaged(false);
		feedbackVisual.setStrokeWidth(1);
		return feedbackVisual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getRoot().getViewer().getAdapter(FocusModel.class)
				.addPropertyChangeListener(focusModelListener);
	}

	@Override
	protected void doDeactivate() {
		getRoot().getViewer().getAdapter(FocusModel.class)
				.removePropertyChangeListener(focusModelListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(GeometryNode<IGeometry> visual) {
		Set<IVisualPart<Node, ? extends Node>> anchorages = getAnchorages()
				.keySet();
		if (anchorages.isEmpty()) {
			return;
		}

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		visual.setGeometry(feedbackGeometry);

		IVisualPart<Node, ? extends Node> anchorage = anchorages.iterator()
				.next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();

		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
		}

		// update color according to focused and selected state
		boolean focused = viewer.getAdapter(FocusModel.class).isViewerFocused()
				&& viewer.getAdapter(FocusModel.class)
						.getFocused() == anchorage;
		List<IContentPart<Node, ? extends Node>> selected = viewer
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelection();
		boolean primary = selected.get(0) == anchorage;
		if (primary) {
			visual.setEffect(getPrimarySelectionFeedbackEffect(focused));
			visual.setStroke(Color.BLACK);
		} else {
			visual.setEffect(getSecondarySelectionFeedbackEffect(focused));
			visual.setStroke(Color.GREY);
		}
	}

	/**
	 * Returns the {@link IGeometry} that is provided by this part's
	 * {@link #setGeometryProvider(Provider) feedback geometry provider}.
	 *
	 * @return The {@link IGeometry} that is provided by this part's
	 *         {@link #setGeometryProvider(Provider) feedback geometry provider}
	 *         .
	 */
	protected IGeometry getFeedbackGeometry() {
		return NodeUtils.sceneToLocal(getVisual(),
				feedbackGeometryProvider.get());
	}

	/**
	 * Returns the {@link Effect} that is applied to a primary selection. When
	 * an effect provider (either {@link #PRIMARY_FOCUSED_EFFECT_PROVIDER} or
	 * {@link #PRIMARY_UNFOCUSED_EFFECT_PROVIDER}) is registered on this part's
	 * anchorage, the provided {@link Effect} is returned. Otherwise, a
	 * {@link DropShadow} is used.
	 *
	 * @param focused
	 *            <code>true</code> if the selection is focused, otherwise
	 *            <code>false</code>.
	 * @return The {@link Effect} that is applied to a primary selection.
	 */
	@SuppressWarnings("serial")
	protected Effect getPrimarySelectionFeedbackEffect(boolean focused) {
		Provider<? extends Effect> effectProvider = null;
		if (!getAnchorages().isEmpty()) {
			IVisualPart<Node, ? extends Node> host = getAnchorages().keys()
					.iterator().next();
			String providerRole = focused ? PRIMARY_FOCUSED_EFFECT_PROVIDER
					: PRIMARY_UNFOCUSED_EFFECT_PROVIDER;
			effectProvider = host.getAdapter(
					AdapterKey.get(new TypeToken<Provider<? extends Effect>>() {
					}, providerRole));
		}
		if (effectProvider == null) {
			DropShadow effect = new DropShadow();
			effect.setColor(focused ? FOCUS_COLOR : Color.GREY);
			effect.setRadius(5);
			effect.setSpread(0.6);
			return effect;
		}
		return effectProvider.get();
	}

	/**
	 * Returns the {@link Effect} that is applied to a secondary selection. When
	 * an effect provider (either {@link #SECONDARY_FOCUSED_EFFECT_PROVIDER} or
	 * {@link #SECONDARY_UNFOCUSED_EFFECT_PROVIDER}) is registered on this
	 * part's anchorage, the provided {@link Effect} is returned. Otherwise, a
	 * {@link DropShadow} is used.
	 *
	 * @param focused
	 *            <code>true</code> if the selection is focused, otherwise
	 *            <code>false</code>.
	 * @return The {@link Effect} that is applied to a primary selection.
	 */
	@SuppressWarnings("serial")
	protected Effect getSecondarySelectionFeedbackEffect(boolean focused) {
		Provider<? extends Effect> effectProvider = null;
		if (!getAnchorages().isEmpty()) {
			IVisualPart<Node, ? extends Node> host = getAnchorages().keys()
					.iterator().next();
			String providerRole = focused ? SECONDARY_FOCUSED_EFFECT_PROVIDER
					: SECONDARY_UNFOCUSED_EFFECT_PROVIDER;
			effectProvider = host.getAdapter(
					AdapterKey.get(new TypeToken<Provider<? extends Effect>>() {
					}, providerRole));
		}
		if (effectProvider == null) {
			DropShadow effect = new DropShadow();
			effect.setColor(focused ? FOCUS_COLOR : Color.GREY);
			effect.setRadius(5);
			effect.setSpread(0.6);
			return effect;
		}
		return effectProvider.get();
	}

	/**
	 * Sets the feedback geometry provider (
	 * <code>Provider&lt;IGeometry&gt;</code>) of this part to the given value.
	 *
	 * @param geometryProvider
	 *            The new feedback geometry provider for this part.
	 */
	public void setGeometryProvider(
			Provider<? extends IGeometry> geometryProvider) {
		feedbackGeometryProvider = geometryProvider;
	}

}
