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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link CreationPolicy} is an {@link ITransactional}
 * {@link AbstractPolicy} that handles the creation of new content objects via
 * the {@link ContentPolicy}.
 *
 * @author wienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class CreationPolicy<VR> extends AbstractPolicy<VR> implements
		ITransactional {

	private List<Entry<IContentPart<VR>, Object>> contentToCreate;

	@Override
	public IUndoableOperation commit() {
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"Create Content");
		for (Entry<IContentPart<VR>, Object> entry : contentToCreate) {
			IContentPart<VR> parent = entry.getKey();
			Object content = entry.getValue();

			// retrieve content policy for the parent
			ContentPolicy<VR> contentPolicy = parent
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (contentPolicy == null) {
				throw new IllegalStateException(
						"No ContentPolicy registered for <" + parent + ">.");
			}

			// determine index (create inserts at the end)
			int index = parent.getContentChildren().size();

			// insert content
			contentPolicy.init();
			contentPolicy.addContentChild(content, index);
			fwd.add(contentPolicy.commit());
		}
		return fwd.unwrap();
	}

	/**
	 * Adds the given <i>content</i> to the collection of to-be-created contents
	 * in the specified <i>parent</i>.
	 *
	 * @param parent
	 *            The {@link IContentPart} where the <i>content</i> is inserted.
	 * @param content
	 *            The {@link Object} to be created as a content-child of the
	 *            given <i>parent</i>.
	 */
	public void create(IContentPart<VR> parent, Object content) {
		if (content == null) {
			throw new IllegalArgumentException(
					"The given content may not be null.");
		} else if (parent == null) {
			throw new IllegalArgumentException(
					"The given parent may not be null.");
		}
		contentToCreate.add(new SimpleEntry<IContentPart<VR>, Object>(parent,
				content));
	}

	@Override
	public void init() {
		contentToCreate = new ArrayList<Map.Entry<IContentPart<VR>, Object>>();
	}

}
