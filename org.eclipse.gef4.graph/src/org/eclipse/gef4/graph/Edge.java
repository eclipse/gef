/*******************************************************************************
 * Copyright (c) 2013 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.HashMap;
import java.util.Map;

public class Edge {

	private Node source;
	private Node target;
	private Map<String, Object> attr = new HashMap<String, Object>();

	public Edge(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	public Object getAttribute(String key) {
		return attr.get(key.toString());
	}

	public Edge withAttribute(String key, Object value) {
		attr.put(key.toString(), value);
		return this;
	}
}
