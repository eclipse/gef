/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.google.inject.Inject;

public class DotEObjectHover extends DispatchingEObjectTextHover {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	private ILocationInFileProvider locationInFileProvider;

	/**
	 * Out of the box, Xtext supports hovers only for identifying features of
	 * model artifacts, i.e. the name of an object or crosslinks to other
	 * objects (see locationInFileProvider.getSignificantTextRegion). That's why
	 * this customization is needed to be able to also hover on the current dot
	 * attribute values.
	 */
	@Override
	protected Pair<EObject, IRegion> getXtextElementAt(XtextResource resource,
			int offset) {

		Pair<EObject, IRegion> result = super.getXtextElementAt(resource,
				offset);

		if (result == null) {
			EObject o = eObjectAtOffsetHelper.resolveElementAt(resource,
					offset);
			if (o != null) {
				// use fullTextRegion instead of the significantTtextRegion
				ITextRegion region = locationInFileProvider
						.getFullTextRegion(o);
				final IRegion region2 = new Region(region.getOffset(),
						region.getLength());
				if (TextUtilities.overlaps(region2, new Region(offset, 0)))
					return Tuples.create(o, region2);
			}
		}
		return result;
	}

}
