/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.fx.listeners.VisualChangeListener;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef.mvc.fx.parts.IFXTransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.IFXTransformableVisualPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.util.Pair;

/**
 * Abstract base class for external labels, i.e. labels that are not part of the
 * visualization of another controller. The standard node label is part of the
 * node part's visualization. However, edge labels are implemented as external
 * labels, for example.
 *
 * @author anyssen
 *
 */
public abstract class AbstractLabelPart extends AbstractFXContentPart<Group>
		implements IFXTransformableContentPart<Group> {

	/**
	 * The CSS class that is assigned to the visualization of the
	 * {@link EdgeLabelPart} of this {@link EdgePart}.
	 */
	public static final String CSS_CLASS_LABEL = "label";

	private VisualChangeListener vcl = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed, Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};

	private MapChangeListener<String, Object> elementAttributesObserver = new MapChangeListener<String, Object>() {
		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			refreshVisual();
		}
	};

	private Text text;

	@Override
	protected void doAttachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	/**
	 * Computes a position for this label.
	 *
	 * @return The computed position for this label in the coordinate system of
	 *         the {@link GraphPart} that contains this label.
	 */
	public abstract Point computeLabelPosition();

	/**
	 * Creates the text visual.
	 *
	 * @return The created {@link Text}.
	 */
	protected Text createText() {
		text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);
		// add css class
		text.getStyleClass().add(CSS_CLASS_LABEL);
		return text;
	}

	@Override
	protected void doDetachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.unregister();
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().getKey().attributesProperty().addListener(elementAttributesObserver);
	}

	@Override
	protected void doDeactivate() {
		getContent().getKey().attributesProperty().removeListener(elementAttributesObserver);
		super.doDeactivate();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<? extends IAttributeStore, String> getContent() {
		return (Pair<? extends IAttributeStore, String>) super.getContent();
	}

	/**
	 * Retrieves the position attribute key for the given label role.
	 *
	 * @return The key via which to retrieve the position attribute for the
	 *         label.
	 */
	public String getLabelPositionAttributeKey() {
		String labelRole = getContent().getValue();
		String attributeKey = null;
		if (ZestProperties.EXTERNAL_LABEL__NE.equals(labelRole)) {
			attributeKey = ZestProperties.EXTERNAL_LABEL_POSITION__NE;
		} else if (ZestProperties.LABEL__NE.equals(labelRole)) {
			// node do not have 'internal' labels
			attributeKey = ZestProperties.LABEL_POSITION__E;
		} else if (ZestProperties.SOURCE_LABEL__E.equals(labelRole)) {
			attributeKey = ZestProperties.SOURCE_LABEL_POSITION__E;
		} else if (ZestProperties.TARGET_LABEL__E.equals(labelRole)) {
			attributeKey = ZestProperties.TARGET_LABEL_POSITION__E;
		} else {
			throw new IllegalArgumentException("Unsupported content element.");
		}
		return attributeKey;
	}

	/**
	 * Retrieves the stored position for the label.
	 *
	 * @return The label position stored in the attributes.
	 */
	public Point getStoredLabelPosition() {
		String key = getLabelPositionAttributeKey();
		ObservableMap<String, Object> attributes = getContent().getKey().getAttributes();
		if (!attributes.containsKey(key)) {
			return null;
		}
		return (Point) attributes.get(key);
	}

	/**
	 * Returns the text visual.
	 *
	 * @return The {@link Text} used as visual.
	 */
	protected Text getText() {
		return text;
	}

	/**
	 * Adjusts the label's position to fit the given {@link Point}.
	 *
	 * @param visual
	 *            This node's visual.
	 * @param position
	 *            This node's position.
	 */
	protected void refreshPosition(Node visual, Point position) {
		if (position != null) {
			// translate using a transform operation
			FXTransformOperation refreshPositionOp = new FXTransformOperation(
					getAdapter(IFXTransformableVisualPart.TRANSFORM_PROVIDER_KEY).get(),
					Geometry2FX.toFXAffine(new AffineTransform(1, 0, 0, 1, position.x, position.y)));
			try {
				refreshPositionOp.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the stored label position to the given value.
	 *
	 * @param computedPosition
	 *            The new label position.
	 */
	public void setStoredLabelPosition(Point computedPosition) {
		String key = getLabelPositionAttributeKey();
		ObservableMap<String, Object> attributes = getContent().getKey().getAttributes();
		attributes.put(key, computedPosition);
	}

	@Override
	public void transformContent(AffineTransform transform) {
		Point storedLabelPosition = getStoredLabelPosition();
		if (storedLabelPosition == null) {
			storedLabelPosition = new Point();
		}
		setStoredLabelPosition(transform.getTransformed(storedLabelPosition));
	}

}