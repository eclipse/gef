/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #531049)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.findreferences;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.gef.dot.internal.ui.DotUiMessages;
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor;

public class DotFindReferencesQueryExecutor extends ReferenceQueryExecutor {

	@Override
	protected String getElementName(EObject primaryTarget) {
		String elementName = super.getElementName(primaryTarget);

		if (primaryTarget instanceof NodeId) {
			return " node '" + elementName + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return elementName;
	}

	@Override
	protected String getLabelPrefix() {
		return DotUiMessages.DotReferenceFinder;
	}
}
