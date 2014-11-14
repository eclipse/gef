/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.util.HashMap;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class CreationPolicy<VR> extends AbstractPolicy<VR> implements
		ITransactional {

	private HashMap<Object, IContentPart<VR>> contentToCreate;

	@Override
	public IUndoableOperation commit() {
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"Create Content");
		for (Object content : contentToCreate.keySet()) {
			IContentPart<VR> parent = contentToCreate.get(content);
			ContentPolicy<VR> contentPolicy = parent
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (contentPolicy == null) {
				throw new IllegalStateException(
						"No ContentPolicy registered for <" + parent + ">.");
			}
			contentPolicy.init();
			contentPolicy.addContentChild(content);
			fwd.add(contentPolicy.commit());
		}
		return fwd.unwrap();
	}

	/**
	 * Adds the given <i>content</i> to the collection of to-be-created contents
	 * in the specified <i>parent</i>.
	 *
	 * @param content
	 *            The {@link Object} to be created as a content-child of the
	 *            given <i>parent</i>.
	 * @param parent
	 *            The {@link IContentPart} where the <i>content</i> is inserted.
	 */
	public void create(Object content, IContentPart<VR> parent) {
		if (content == null) {
			throw new IllegalArgumentException(
					"The given content may not be null.");
		} else if (parent == null) {
			throw new IllegalArgumentException(
					"The given parent may not be null.");
		}
		contentToCreate.put(content, parent);
	}

	@Override
	public void init() {
		contentToCreate = new HashMap<Object, IContentPart<VR>>();
	}

}
