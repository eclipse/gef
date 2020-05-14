/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.operations.TransformVisualOperation;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
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
public abstract class AbstractLabelPart extends AbstractContentPart<Group> implements ITransformableContentPart<Group> {

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
	protected void doActivate() {
		super.doActivate();
		getContent().getKey().attributesProperty().addListener(elementAttributesObserver);
	}

	@Override
	protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	@Override
	protected void doDeactivate() {
		getContent().getKey().attributesProperty().removeListener(elementAttributesObserver);
		super.doDeactivate();
	}

	@Override
	protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		vcl.unregister();
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

	@Override
	public Affine getContentTransform() {
		Point p = getLabelPosition();
		if (p == null) {
			p = new Point();
		}
		return new Affine(new Translate(p.x, p.y));
	}

	/**
	 * Retrieves the stored position for the label.
	 *
	 * @return The label position stored in the attributes.
	 */
	public Point getLabelPosition() {
		String key = getLabelPositionAttributeKey();
		ObservableMap<String, Object> attributes = getContent().getKey().getAttributes();
		if (!attributes.containsKey(key)) {
			return null;
		}
		return (Point) attributes.get(key);
	}

	/**
	 * Retrieves the position attribute key for the given label role.
	 *
	 * @return The key via which to retrieve the position attribute for the
	 *         label.
	 */
	protected String getLabelPositionAttributeKey() {
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
	 * Returns the text visual.
	 *
	 * @return The {@link Text} used as visual.
	 */
	protected Text getText() {
		return text;
	}

	/**
	 * Recomputes the label position.
	 */
	public void recomputeLabelPosition() {
		setLabelPosition(computeLabelPosition());
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
			TransformVisualOperation refreshPositionOp = new TransformVisualOperation(this,
					new Affine(new Translate(position.x, position.y)));
			try {
				refreshPositionOp.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setContentTransform(Affine transform) {
		setLabelPosition(new Point(transform.getTx(), transform.getTy()));
	}

	/**
	 * Sets the stored label position to the given value.
	 *
	 * @param computedPosition
	 *            The new label position.
	 */
	public void setLabelPosition(Point computedPosition) {
		getContent().getKey().getAttributes().put(getLabelPositionAttributeKey(), computedPosition);
	}

}