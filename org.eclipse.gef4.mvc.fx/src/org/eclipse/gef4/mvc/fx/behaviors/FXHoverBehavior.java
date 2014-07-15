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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 */
public class FXHoverBehavior extends AbstractHoverBehavior<Node> {

	private final Map<IVisualPart<Node>, Effect> effects = new HashMap<IVisualPart<Node>, Effect>();

	@Override
	protected void addFeedback(List<? extends IVisualPart<Node>> targets,
			Map<Object, Object> contextMap) {
		if (getHost() instanceof IHandlePart) {
			Node visual = getHost().getVisual();
			effects.put(getHost(), visual.getEffect());
			visual.setEffect(getHoverFeedbackEffect(contextMap));
		} else {
			super.addFeedback(targets, contextMap);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IGeometry getFeedbackGeometry(Map<Object, Object> contextMap) {
		Node visual = getHost().getVisual();

		// in case a FXGeometryNode is used, we can return its IGeometry
		if (visual instanceof IFXConnection) {
			Node curveNode = ((IFXConnection) visual).getCurveNode();
			if (curveNode instanceof FXGeometryNode) {
				return ((FXGeometryNode) curveNode).getGeometry();
			}
		} else if (visual instanceof FXGeometryNode) {
			return ((FXGeometryNode) visual).getGeometry();
		}

		return JavaFX2Geometry.toRectangle(visual.getLayoutBounds());
	}

	public Effect getHoverFeedbackEffect(Map<Object, Object> contextMap) {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

	@Override
	protected void removeFeedback(List<? extends IVisualPart<Node>> targets) {
		if (getHost() instanceof IHandlePart) {
			for (IVisualPart<Node> part : targets) {
				Node visual = part.getVisual();
				visual.setEffect(effects.remove(part));
			}
		} else {
			super.removeFeedback(targets);
		}
	}

}
