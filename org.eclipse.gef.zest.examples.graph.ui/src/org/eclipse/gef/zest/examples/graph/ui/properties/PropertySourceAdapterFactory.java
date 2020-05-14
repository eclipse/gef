/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples.graph.ui.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.graph.Node;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 *
 * @author mwienand
 *
 */
public class PropertySourceAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IPropertySource.class.equals(adapterType)) {
			if (adaptableObject instanceof Node) {
				return new NodePropertySource((Node) adaptableObject);
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
