/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractHandlePart<VR> extends AbstractVisualPart<VR>
		implements IHandlePart<VR> {

	@Override
	protected void addChildVisual(IVisualPart<VR> child, int index) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support children");
	}

	@Override
	protected void removeChildVisual(IVisualPart<VR> child) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

}
