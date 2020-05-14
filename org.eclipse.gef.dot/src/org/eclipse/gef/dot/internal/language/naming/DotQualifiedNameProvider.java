/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #545441)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.naming.SimpleNameProvider;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.inject.Inject;

/**
 * The implementation of this class is mainly taken from the
 * {@link org.eclipse.xtext.naming.SimpleNameProvider} java class.
 */
public class DotQualifiedNameProvider extends SimpleNameProvider {

	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	public QualifiedName getFullyQualifiedName(EObject obj) {
		// customize the name calculation for NodeId
		String name = null;
		if (obj instanceof NodeId) {
			NodeId node = (NodeId) obj;
			name = node.getName().toValue();
		} else {
			name = SimpleAttributeResolver.NAME_RESOLVER.apply(obj);
		}

		if (name == null || name.length() == 0)
			return null;
		return qualifiedNameConverter.toQualifiedName(name);
	}

}
