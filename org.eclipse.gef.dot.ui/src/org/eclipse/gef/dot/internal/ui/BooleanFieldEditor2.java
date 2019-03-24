/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #521329)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A boolean field editor that provides access to this editors boolean button.
 * 
 * The implementation of this class is mainly taken from the
 * {@link org.eclipse.debug.internal.ui.preferences.BooleanFieldEditor2} java
 * class.
 */
public class BooleanFieldEditor2 extends BooleanFieldEditor {

	private Button fChangeControl;

	/**
	 * @see BooleanFieldEditor#BooleanFieldEditor(java.lang.String,
	 *      java.lang.String, int, org.eclipse.swt.widgets.Composite)
	 */
	public BooleanFieldEditor2(String name, String labelText, int style,
			Composite parent) {
		super(name, labelText, style, parent);
	}

	public BooleanFieldEditor2(String name, String label, Composite parent) {
		this(name, label, DEFAULT, parent);
	}

	/**
	 * @see org.eclipse.jface.preference.BooleanFieldEditor#getChangeControl(Composite)
	 */
	@Override
	public Button getChangeControl(Composite parent) {
		if (fChangeControl == null) {
			fChangeControl = super.getChangeControl(parent);
		}
		return fChangeControl;
	}
}