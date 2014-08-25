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

import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * A hover behavior that in addition to the {@link HoverBehavior} adds
 * lightweight hover feedback to handles.
 *
 * @author anyssen
 *
 */
public class FXHoverBehavior extends HoverBehavior<Node> {

	private final Map<IVisualPart<Node>, Effect> effects = new HashMap<IVisualPart<Node>, Effect>();

	@Override
	protected void addFeedback(List<? extends IVisualPart<Node>> targets,
			Map<Object, Object> contextMap) {
		if (getHost() instanceof IHandlePart) {
			Node visual = getHost().getVisual();
			effects.put(getHost(), visual.getEffect());
			visual.setEffect(getHandleHoverFeedbackEffect(contextMap));
		} else {
			super.addFeedback(targets, contextMap);
		}
	}

	public Effect getHandleHoverFeedbackEffect(Map<Object, Object> contextMap) {
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
