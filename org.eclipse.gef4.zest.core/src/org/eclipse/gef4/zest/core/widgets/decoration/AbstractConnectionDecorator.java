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
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;

public abstract class AbstractConnectionDecorator implements
		IConnectionDecorator {

	/**
	 * Creates a source decoration for the connection
	 * 
	 * @return the provided source decoration, or null if not applicable
	 */
	public abstract RotatableDecoration createSourceDecoration(
			GraphConnection connection);

	/**
	 * Creates a target decoration for the connection
	 * 
	 * @return the provided target decoration, or null if not applicable
	 */
	public abstract RotatableDecoration createTargetDecoration(
			GraphConnection connection);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef4.zest.core.widgets.decoration.IConnectionDecorator#
	 * decorateConnection(org.eclipse.draw2d.PolylineConnection)
	 */
	public void decorateConnection(GraphConnection connection,
			PolylineConnection figure) {
		if (figure instanceof PolylineConnection) {
			RotatableDecoration sourceDecoration = createSourceDecoration(connection);
			figure.setSourceDecoration(sourceDecoration);
			RotatableDecoration targetDecoration = createTargetDecoration(connection);
			figure.setTargetDecoration(targetDecoration);
		}
	}
}
