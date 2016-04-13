/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.zest.fx.ZestFxModule;

import com.google.inject.TypeLiteral;

import javafx.scene.Node;

/**
 * The {@link DotGraphViewModule} extends the {@link ZestFxModule} and removes
 * the layout bindings.
 * 
 * @author mwienand
 *
 */
public class DotGraphViewModule extends ZestFxModule {

	@Override
	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(DotContentPartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}
}
