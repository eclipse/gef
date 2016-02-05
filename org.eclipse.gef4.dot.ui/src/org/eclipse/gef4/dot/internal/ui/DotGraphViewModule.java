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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.zest.fx.ZestFxModule;

import com.google.inject.multibindings.MapBinder;

/**
 * The {@link DotGraphViewModule} extends the {@link ZestFxModule} and removes
 * the layout bindings.
 * 
 * @author mwienand
 *
 */
public class DotGraphViewModule extends ZestFxModule {

	// // TODO: Guard calls to LayoutContextBehaivor and trigger layout
	// // computations from within LayoutContextBehavior so that a replacement
	// is
	// // not needed, but the binding can just be removed.
	// public static final class NullLayoutContextBehavior
	// extends LayoutContextBehavior {
	// @Override
	// protected void doActivate() {
	// // super.doActivate();
	// }
	//
	// @Override
	// protected void doDeactivate() {
	// // super.doDeactivate();
	// }
	//
	// @Override
	// public void applyLayout(boolean clean) {
	// // super.applyLayout(clean);
	// }
	// }

	@Override
	protected void bindNodeLayoutBehaviorAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindNodeLayoutBehaviorAsNodeContentPartAdapter(adapterMapBinder);
	}

	@Override
	protected void bindLayoutContextBehaviorAsGraphContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindLayoutContextBehaviorAsGraphContentPartAdapter(
				adapterMapBinder);
		// adapterMapBinder.addBinding(AdapterKey.defaultRole())
		// .to(NullLayoutContextBehavior.class);
	}

	@Override
	protected void bindEdgeLayoutBehaviorAsEdgeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindEdgeLayoutBehaviorAsEdgeContentPartAdapter(adapterMapBinder);
	}

}
