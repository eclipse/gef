/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.fontname.impl;

import java.util.List;

import org.eclipse.gef.dot.internal.language.fontname.Gravity;
import org.eclipse.gef.dot.internal.language.fontname.Stretch;
import org.eclipse.gef.dot.internal.language.fontname.Style;
import org.eclipse.gef.dot.internal.language.fontname.Variant;
import org.eclipse.gef.dot.internal.language.fontname.Weight;

public class PostScriptFontNameImplCustom extends PostScriptFontNameImpl {
	public Weight getWeight() {
		return this.getAlias().getWeight();
	}

	public Style getStyle() {
		return this.getAlias().getStyle();
	}

	public Variant getVariant() {
		return null;
	}

	public Stretch getStretch() {
		return this.getAlias().getStretch();
	}

	public Gravity getGravity() {
		return null;
	}

	public List<String> getFontFamilies() {
		return this.getAlias().getFamily().getFamilies();
	}
}
