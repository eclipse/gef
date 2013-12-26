/*******************************************************************************
 * Copyright (c) 2013 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	private Map<String, Object> attr = new HashMap<String, Object>();

	public List<Edge> getSourceConnections(Graph graph) {
		List<Edge> result = new ArrayList<Edge>();
		List<Edge> edges = graph.getEdges();
		for (Edge edge : edges)
			if (edge.getTarget().equals(this))
				result.add(edge);
		return result;
	}

	public Object getAttribute(String key) {
		return attr.get(key);
	}

	public Node withAttribute(String key, Object value) {
		attr.put(key, value);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && ((Node) obj).attr.equals(this.attr);
	}

	@Override
	public int hashCode() {
		return attr.hashCode();
	}
}
