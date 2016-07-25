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
package org.eclipse.gef.mvc.tests.stubs.cell;

import org.eclipse.gef.mvc.parts.AbstractRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

public class CellRootPart<VR, V extends VR> extends AbstractRootPart<VR, V> {

	@Override
	protected void addChildVisual(IVisualPart<VR, ? extends VR> child, int index) {
	}

	@Override
	protected V createVisual() {
		return null;
	}

	@Override
	protected void doRefreshVisual(Object visual) {
	}

	@Override
	protected void removeChildVisual(IVisualPart<VR, ? extends VR> child, int index) {
	}
}