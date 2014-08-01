/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.models;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.layout.interfaces.LayoutContext;

public interface ILayoutModel extends IPropertyChangeNotifier {

	public static final String LAYOUT_CONTEXT_PROPERTY = "layoutContext";

	public LayoutContext getLayoutContext();

	public void setLayoutContext(LayoutContext context);

}
