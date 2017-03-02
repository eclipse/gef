/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.RangeBasedDiagnostic;

/**
 * Provides range based information (offset and length) of validation
 * diagnostics.
 * 
 * @author miklossy
 * 
 */
public class DotRangeBasedDiagnostic extends RangeBasedDiagnostic {

	public DotRangeBasedDiagnostic(int severity, String message, EObject source,
			int offset, int length, CheckType checkType, String issueCode,
			String[] issueData) {
		super(severity, message, source, offset, length, checkType, issueCode,
				issueData);
	}
}
