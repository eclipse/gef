/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)  - initial API and implementation
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.generator;

import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.generator.Naming;

/**
 * This class enables the customization of the ui package name within the Xtext
 * code generation workflow for the dot grammar (host grammar and sub-grammars).
 */
public class DotNaming extends Naming {

	@Override
	public String basePackageUi(Grammar g) {
		if (g.getName().startsWith("org.eclipse.gef.dot.internal.language")) {
			return "org.eclipse.gef.dot.internal.ui.language";
		} else {
			return super.basePackageUi(g);
		}
	}
}
