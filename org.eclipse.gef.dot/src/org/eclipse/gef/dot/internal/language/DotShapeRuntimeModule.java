/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.formatting.DotShapeFormatter;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotShapeRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotShapeRuntimeModule {

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotShapeFormatter.class;
	}
}
