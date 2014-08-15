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
package org.eclipse.gef4.fx.anchors;

import javafx.scene.Node;

/**
 * AnchorKey represents a set of anchored node and additional key to identify
 * one anchor target. TODO: make unmodifiable?
 *
 * @author mwienand
 *
 */
public class AnchorKey {

	private Node anchored;
	private Object id;

	public AnchorKey(Node anchored, Object id) {
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

	public Node getAnchored() {
		return anchored;
	}

	public Object getId() {
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

	public void setAnchored(Node anchored) {
		if (anchored == null) {
			throw new IllegalArgumentException(
					"The given Anchored may not be <null>.");
		}
		this.anchored = anchored;
	}

	public void setId(Object id) {
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
