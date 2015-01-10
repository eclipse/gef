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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.interfaces.LayoutContext;

public class LayoutModel implements IPropertyChangeNotifier {

	public static final String LAYOUT_CONTEXT_PROPERTY = "layoutContext";

	private LayoutContext layoutContext;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public LayoutContext getLayoutContext() {
		return layoutContext;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setLayoutContext(LayoutContext context) {
		LayoutContext oldContext = layoutContext;
		layoutContext = context;

		// in case new context does not specify an algorithm, transfer old
		// context (or set default, if no context was set before)
		if (context.getStaticLayoutAlgorithm() == null) {
			if (oldContext != null && oldContext.getStaticLayoutAlgorithm() != null) {
				context.setStaticLayoutAlgorithm(oldContext.getStaticLayoutAlgorithm());
			} else {
				context.setStaticLayoutAlgorithm(new SpringLayoutAlgorithm());
			}
		}

		if (context != oldContext) {
			pcs.firePropertyChange(LAYOUT_CONTEXT_PROPERTY, oldContext, layoutContext);
		}
	}
}
