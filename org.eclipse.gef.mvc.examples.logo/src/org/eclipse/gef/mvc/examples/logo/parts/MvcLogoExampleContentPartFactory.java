/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.Map;

import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

public class MvcLogoExampleContentPartFactory implements IContentPartFactory {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
		if (content instanceof GeometricShape) {
			return injector.getInstance(GeometricShapePart.class);
		} else if (content instanceof GeometricCurve) {
			return injector.getInstance(GeometricCurvePart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	};

}
