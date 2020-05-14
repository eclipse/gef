/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.anchors;

import javafx.scene.Node;

/**
 * AnchorKey combines an anchored {@link Node} with a {@link String} qualifier
 * to identify an anchor target.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class AnchorKey {

	private Node anchored;
	private String id;

	/**
	 * Creates a new {@link AnchorKey} for the given <i>anchored</i>
	 * {@link Node} with the given <i>id</i>.
	 *
	 * @param anchored
	 *            The anchored {@link Node}.
	 * @param id
	 *            The identifier for this {@link AnchorKey}, used to
	 *            differentiate multiple keys with the same anchored
	 *            {@link Node}.
	 */
	public AnchorKey(Node anchored, String id) {
		setAnchored(anchored);
		setId(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AnchorKey) {
			AnchorKey other = (AnchorKey) obj;
			return anchored.equals(other.getAnchored())
					&& id.equals(other.getId());
		}
		return false;
	}

	/**
	 * Returns the anchored {@link Node} of this {@link AnchorKey}.
	 *
	 * @return The anchored {@link Node} of this {@link AnchorKey}.
	 */
	public Node getAnchored() {
		return anchored;
	}

	/**
	 * The {@link String} identifier of this {@link AnchorKey}.
	 *
	 * @return The {@link String} identifier of this {@link AnchorKey}.
	 */
	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anchored.hashCode();
		result = prime * result + id.hashCode();
		return result;
	}

	/**
	 * Sets the anchored {@link Node} of this {@link AnchorKey} to the given
	 * value.
	 *
	 * @param anchored
	 *            The new anchored {@link Node} for this {@link AnchorKey}.
	 */
	protected void setAnchored(Node anchored) {
		if (anchored == null) {
			throw new IllegalArgumentException(
					"The given Anchored may not be <null>.");
		}
		this.anchored = anchored;
	}

	/**
	 * Sets the {@link String} identifier of this {@link AnchorKey} to the given
	 * value.
	 *
	 * @param id
	 *            The new {@link String} identifier for this {@link AnchorKey}.
	 */
	protected void setId(String id) {
		if (id == null) {
			throw new IllegalArgumentException(
					"The given Id may not be <null>.");
		}
		this.id = id;
	}

	@Override
	public String toString() {
		return "AnchorKey <" + id.toString() + "> <" + anchored.toString()
				+ ">";
	}

}
