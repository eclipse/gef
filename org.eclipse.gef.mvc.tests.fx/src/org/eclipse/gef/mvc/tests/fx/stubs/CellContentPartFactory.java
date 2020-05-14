/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.tests.fx.stubs;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

public class CellContentPartFactory implements IContentPartFactory {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node> createContentPart(Object content, Map<Object, Object> contextMap) {
		if (content instanceof Cell) {
			return injector.getInstance(CellContentPart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	}
}