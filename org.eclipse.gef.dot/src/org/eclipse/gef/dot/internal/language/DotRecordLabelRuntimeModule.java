/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge - initial API and implementation (bug #454629)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.formatting.DotRecordLabelFormatter;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotRecordLabelRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotRecordLabelRuntimeModule {

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotRecordLabelFormatter.class;
	}
}
