/*******************************************************************************
 * Copyright (c) 2009, 2015 Mateusz Matela and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.listeners;

import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.LayoutProperties;

/**
 * An {@link IContextListener} is notified about changes to context attributes
 * such as the {@link LayoutProperties#BOUNDS_PROPERTY} which limits the area
 * where nodes can be placed by layout algorithms, and the
 * {@link LayoutProperties#DYNAMIC_LAYOUT_ENABLED_PROPERTY} which defines
 * whether layout algorithms are allowed to perform a layout pass in reaction to
 * layout context events.
 */
public interface IContextListener {

	/**
	 * A stub implementation of the {@link IContextListener} which contains
	 * empty implementations of the specified methods.
	 */
	public class Stub implements IContextListener {

		public boolean boundsChanged(ILayoutContext context) {
			return false;
		}

		public void backgroundEnableChanged(ILayoutContext context) {
			// do nothing
		}

	}

	/**
	 * This method is called whenever the bounds available in a layout context
	 * change. If <code>true</code> is returned, no dynamic layout will be
	 * applied after notifying all listeners, i.e. a dynamic layout pass will
	 * only be applied when all registered {@link IContextListener}s return
	 * <code>false</code>.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @return <code>true</code> if no dynamic layout should be applied
	 *         afterwards.
	 */
	public boolean boundsChanged(ILayoutContext context);

	/**
	 * This method is called whenever background layout is enabled or disabled
	 * in a layout context. If the receiving listener is related to a layout
	 * algorithm that performs layout in reaction to events, it should turn
	 * automatic flush of changes on or off. Also, eventual additional threads
	 * responsible for layout should be stopped or started accordingly.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 */
	public void backgroundEnableChanged(ILayoutContext context);
}
