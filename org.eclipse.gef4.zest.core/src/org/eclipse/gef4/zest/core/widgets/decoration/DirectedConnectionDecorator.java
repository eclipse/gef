/*******************************************************************************
 * Copyright 2013, Zoltan Ujhelyi. All rights reserved. This program and the 
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Zoltan Ujhelyi
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets.decoration;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;

public class DirectedConnectionDecorator extends AbstractConnectionDecorator {

	public RotatableDecoration createSourceDecoration(GraphConnection connection) {
		return null;
	}

	public RotatableDecoration createTargetDecoration(GraphConnection connection) {
		PolygonDecoration decoration = new PolygonDecoration();
		if (connection.getLineWidth() < 3) {
			decoration.setScale(9, 3);
		} else {
			double logLineWith = connection.getLineWidth() / 2.0;
			decoration.setScale(7 * logLineWith, 3 * logLineWith);
		}
		return decoration;
	}

}
