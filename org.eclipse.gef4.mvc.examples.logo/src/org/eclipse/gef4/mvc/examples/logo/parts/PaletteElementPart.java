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

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;

import com.google.common.collect.SetMultimap;

public class PaletteElementPart extends AbstractFXContentPart<GeometryNode<IGeometry>> {

	@Override
	protected GeometryNode<IGeometry> createVisual() {
		return new GeometryNode<>();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return CollectionUtils.emptySetMultimap();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(GeometryNode<IGeometry> visual) {
		visual.setGeometry(getContent().getGeometry());
	}

	@Override
	public AbstractFXGeometricElement<?> getContent() {
		return (AbstractFXGeometricElement<?>) super.getContent();
	}

}
