/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.dot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.dot.Graph.Attr;

public final class Node {

	public static class Builder {

		private Map<String, Object> attrs = new HashMap<String, Object>();

		public Node.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Builder attr(Attr attr, Object value) {
			return attr(attr.toString(), value);
		}

		public Node build() {
			return new Node(attrs);
		}

	}

	private final Map<String, Object> attrs;

	public Node(Map<String, Object> attrs) {
		this.attrs = attrs;
	}

	public Map<String, Object> getAttrs() {
		return Collections.unmodifiableMap(attrs);
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Node))
			return false;
		boolean attrsEqual = this.getAttrs().equals(((Node) that).getAttrs());
		return attrsEqual;
	}

	@Override
	public int hashCode() {
		return getAttrs().hashCode();
	}

	@Override
	public String toString() {
		return String.format("Node {%s attrs}", attrs.size()); //$NON-NLS-1$
	}
}
