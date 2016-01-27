/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

public class FXLogoHoverHandlePartFactory
		extends FXDefaultHoverHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createHoverHandleParts(
			IVisualPart<Node, ? extends Node> target,
			HoverBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node, ? extends Node>> handles = new ArrayList<>();
		if (target instanceof FXGeometricShapePart) {
			// create root handle part
			FXHoverHandleRootPart parentHp = new FXHoverHandleRootPart();
			injector.injectMembers(parentHp);
			handles.add(parentHp);

			// FIXME: addChild() should be called automatically?
			FXDeleteHoverHandlePart deleteHp = new FXDeleteHoverHandlePart();
			injector.injectMembers(deleteHp);
			parentHp.addChild(deleteHp);

			FXCreateCurveHoverHandlePart createCurveHp = new FXCreateCurveHoverHandlePart();
			injector.injectMembers(createCurveHp);
			parentHp.addChild(createCurveHp);

			return handles;
		}
		return super.createHoverHandleParts(target, contextBehavior,
				contextMap);
	}

}
