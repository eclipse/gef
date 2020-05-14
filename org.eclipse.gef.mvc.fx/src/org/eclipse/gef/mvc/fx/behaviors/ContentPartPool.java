/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.behaviors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import javafx.scene.Node;

/**
 * A temporary store for {@link IContentPart}s that is used by
 * {@link ContentBehavior}s. They will add {@link IContentPart}s, which are
 * removed from the viewer during content synchronization (e.g. because the
 * related content element was deleted), to be re-used (i.e. removed again and
 * restored within the viewer) when the content element re-appears during
 * synchronization, e.g. because of an undo of a delete operation. The
 * motivation behind recycling {@link IContentPart}s is that after an undo the
 * viewer is in the exact same state as before the execution of an operation
 * (which may be important for feedback or handles).
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class ContentPartPool implements IDisposable {

	private Map<Object, IContentPart<? extends Node>> pool = new HashMap<>();

	/**
	 * Adds an {@link IContentPart} to this pool. The {@link IContentPart} will
	 * be stored under its content element ({@link IContentPart#getContent()})
	 * and may later be retrieved back via this content element (see
	 * {@link #remove(Object)}.
	 *
	 * @param part
	 *            The {@link IContentPart} to add to the pool.
	 */
	public void add(IContentPart<? extends Node> part) {
		// TODO: We need to handle the case that a content part was already
		// registered for the same content element in case we will enable this
		// in the viewer (e.g. by adding context information to the content part
		// map).
		pool.put(part.getContent(), part);
	}

	/**
	 * Clears the pool, that is removes all {@link IContentPart}s.
	 */
	public void clear() {
		pool.clear();
	}

	@Override
	public void dispose() {
		for (IContentPart<? extends Node> cp : getPooled()) {
			cp.dispose();
		}
		clear();
	}

	/**
	 * Returns the {@link IContentPart}'s that are contained in this pool.
	 *
	 * @return The {@link IContentPart}s that are currently contained in this
	 *         pool.
	 */
	public Collection<IContentPart<? extends Node>> getPooled() {
		return Collections.unmodifiableCollection(pool.values());
	}

	/**
	 * Retrieves an {@link IContentPart} for the given content element and
	 * removes it from the pool.
	 *
	 * @param content
	 *            The {@link IContentPart} that was registered for the content
	 *            element, or <code>null</code> if no {@link IContentPart} could
	 *            be retrieved for the content element.
	 * @return The part that was retrieved for the given content element, or
	 *         <code>null</code> if none could be found.
	 */
	public IContentPart<? extends Node> remove(Object content) {
		return pool.remove(content);
	}

}