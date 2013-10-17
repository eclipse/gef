/*******************************************************************************
 * Copyright 2013, Zoltan Ujhelyi. All rights reserved. This program and the 
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Zoltan Ujhelyi
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets.decoration;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;

public class DefaultConnectionDecorator implements IConnectionDecorator {

	public void decorateConnection(GraphConnection connection,
			PolylineConnection figure) {
		// No source or target decorations to add
	}

}
